/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.pileupFlattener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.bin.BinListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ElementAddedNotSortedException;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;
import edu.yu.einstein.genplay.util.FloatLists;

/**
 * {@link PileupFlattener} for {@link BinList}
 * @author Julien Lajugie
 */
public class BinListPileupFlattener implements PileupFlattener {

	/** Size of the bins of the flattened list*/
	private final int binSize;

	/** Queue containing the windows of the pileup */
	private final SCWQueue windowQueue;

	/** Builder to create the result {@link ListView} */
	private final BinListViewBuilder resultLVBuilder;

	/** Operation to compute the score of the result value of the flattening */
	private final ScoreOperation scoreOperation;


	/**
	 * Creates a new instance of {@link BinListPileupFlattener}
	 * @param binSize size of the bins of the binlist
	 * @param scoreOperation {@link ScoreOperation} method to compute a score from overlapping windows
	 */
	public BinListPileupFlattener(int binSize, ScoreOperation scoreOperation) {
		this.binSize = binSize;
		windowQueue = new SCWQueue();
		resultLVBuilder = new BinListViewBuilder(binSize);
		this.scoreOperation = scoreOperation;
	}


	@Override
	public void addWindow(int windowStart, int windowStop, float windowScore) throws ElementAddedNotSortedException {
		if (windowQueue.isEmpty()) {
			windowQueue.add(windowStart, windowStop, windowScore);
			int firstBinStart = windowStart / binSize;
			for (int i = 0; i < firstBinStart; i++) {
				resultLVBuilder.addElementToBuild(0);
			}
		} else {
			int lastWindowStart = windowQueue.get(windowQueue.size() - 1).getStart();
			if (windowStart < lastWindowStart) {
				throw new ElementAddedNotSortedException();
			}
			int firstBinStart = (((lastWindowStart - 1) / binSize) * binSize) + 1;
			int lastBinStop = (((windowStart - 1) / binSize) * binSize) + 1;

			// add the new window at the end of the queue
			windowQueue.add(windowStart, windowStop, windowScore);
			// retrieve the result of the pileup flattening
			flattenPileup(firstBinStart, lastBinStop);
			// remove the element that are already processed
			removeProcessedElements(lastBinStop);
		}
	}


	@Override
	public void addWindow(ScoredChromosomeWindow windowToAdd) throws ElementAddedNotSortedException, ObjectAlreadyBuiltException {
		addWindow(windowToAdd.getStart(), windowToAdd.getStop(), windowToAdd.getScore());
	}


	/**
	 * A new instance of {@link BinListPileupFlattener} containing no element.
	 */
	@Override
	public BinListPileupFlattener clone() {
		return new BinListPileupFlattener(binSize, scoreOperation);
	}


	/**
	 * Computes the normalized score of the specified {@link ScoredChromosomeWindow}.
	 * A normalized score is the score
	 * @param binStart
	 * @param binStop
	 * @param currentRead
	 * @return A normalized score
	 */
	private Float computeNormalizedScore(int binStart, int binStop, ScoredChromosomeWindow currentRead) {
		// case where the entire read fall in the bin
		if ((currentRead.getStart() >= binStart)
				&& (currentRead.getStop() <= binStop)) {
			return currentRead.getScore();
		}
		int readLengthBeforeBinStart = 0;
		if (currentRead.getStart() < binStart) {
			readLengthBeforeBinStart = binStart - currentRead.getStart();
		}
		int readLengthAfterBinStop = 0;
		if (currentRead.getStop() > binStop) {
			readLengthAfterBinStop = currentRead.getStop() - binStop;
		}
		float partOfReadInsideBin = (currentRead.getSize() - (readLengthBeforeBinStart + readLengthAfterBinStop)) / (float) currentRead.getSize();
		float score = currentRead.getScore() * partOfReadInsideBin;
		return score;
	}


	/**
	 * Flattens the overlapping windows of the queue between the specified start and stop positions.
	 * @param firstBinStart
	 * @param lastBinStop
	 */
	private void flattenPileup(int firstBinStart, int lastBinStop) {

		// nothing to do if the start and the stop are equal
		if (firstBinStart != lastBinStop) {
			int binCount = (lastBinStop - firstBinStart) / binSize;

			List<List<Float>> scores = new ArrayList<List<Float>>(binCount);
			for (int i = 0; i < binCount; i++) {
				scores.add(new ArrayList<Float>());
			}

			Iterator<ScoredChromosomeWindow> windowQueueIterator = windowQueue.listIterator();
			ScoredChromosomeWindow currentRead;

			while (windowQueueIterator.hasNext() &&
					((currentRead = windowQueueIterator.next()).getStart() < lastBinStop)) {
				int readFirstBinStart = (((currentRead.getStart() - 1)/ binSize) * binSize) + 1;
				readFirstBinStart = Math.max(readFirstBinStart, firstBinStart);
				// we subtract one the stop because this position is excluded
				int readLastBinStart = (((currentRead.getStop() - 1) / binSize) * binSize) + 1;
				readLastBinStart = Math.min(readLastBinStart, lastBinStop - binSize);
				int i = 0;
				for (int binStart = readFirstBinStart; binStart <= readLastBinStart; binStart += binSize) {
					float normalizedScore = computeNormalizedScore(binStart, binStart + binSize, currentRead);
					scores.get(i++).add(normalizedScore);
				}
			}

			int i = 0;
			for (int binStart = firstBinStart; binStart < lastBinStop; binStart += binSize) {
				float score = processScoreList(scores.get(i++));
				// add the completed bin to the flatten pileup
				resultLVBuilder.addElementToBuild(score);
			}
		}
	}


	/**
	 * Flattens the remaining elements of the queue
	 */
	private void flush() {
		if (!windowQueue.isEmpty()) {
			int lastWindowStart = windowQueue.get(windowQueue.size() - 1).getStart();
			int lastWindowStop = windowQueue.get(windowQueue.size() - 1).getStop();
			int firstBinStart = (((lastWindowStart - 1) / binSize) * binSize) + 1;
			int lastBinStop = (((lastWindowStop - 1) / binSize) * binSize) + 1;
			flattenPileup(firstBinStart, lastBinStop);
		}
	}


	@Override
	public ListView<ScoredChromosomeWindow> getListView() {
		flush();
		return resultLVBuilder.getListView();
	}


	/**
	 * Computes a score for a window based on the scores of the different windows overlapping the resulting
	 * flattened window.  The score of the window is computed using the {@link ScoreOperation} set during the
	 * construction of this {@link PileupFlattener} object.
	 * @param currentScoreList scores of the windows overlapping the "flattened window"
	 * @return the score computed
	 */
	private Float processScoreList(List<Float> currentScoreList) {
		switch (scoreOperation) {
		case ADDITION:
			return FloatLists.sum(currentScoreList);
		case AVERAGE:
			return FloatLists.average(currentScoreList);
		case DIVISION:
			throw new UnsupportedOperationException("Operation not supported: Division. Noncommutative operations are not supported.");
		case MAXIMUM:
			return FloatLists.maxNoZero(currentScoreList);
		case MINIMUM:
			return FloatLists.minNoZero(currentScoreList);
		case MULTIPLICATION:
			return FloatLists.multiply(currentScoreList);
		case SUBTRACTION:
			throw new UnsupportedOperationException("Operation not supported: Subtraction. Noncommutative operations are not supported.");
		default:
			throw new UnsupportedOperationException("Operation not supported: " + scoreOperation.name());
		}
	}


	/**
	 * Removes all the windows with a stop position smaller than the specified position.
	 * Because windows are added in start position order, theses windows won't be involved
	 * in future pileups.
	 * @param position a position
	 */
	private void removeProcessedElements(int position) {
		int i = 0;
		while ((i < windowQueue.size()) && (windowQueue.get(i).getStart() <= position)) {
			if (windowQueue.get(i).getStop() <= position) {
				windowQueue.remove(i);
			} else {
				i++;
			}
		}
	}
}
