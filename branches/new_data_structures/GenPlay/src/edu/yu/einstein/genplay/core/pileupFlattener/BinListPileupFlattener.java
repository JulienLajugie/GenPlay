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
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ElementAddedNotSortedException;
import edu.yu.einstein.genplay.util.FloatLists;

/**
 * {@link PileupFlattener} for {@link BinList}
 * @author Julien Lajugie
 */
public class BinListPileupFlattener implements PileupFlattener {

	/** Size of the bins of the flattened list*/
	private final int binSize;

	/** Queue containing the windows of the pileup */
	private final List<ScoredChromosomeWindow> windowQueue;

	/** Operation to compute the score of the result value of the flattening */
	private final ScoreOperation scoreOperation;


	/**
	 * 
	 * @param binSize
	 * @param scoreOperation
	 */
	public BinListPileupFlattener(int binSize, ScoreOperation scoreOperation) {
		this.binSize = binSize;
		windowQueue = new ArrayList<ScoredChromosomeWindow>();
		this.scoreOperation = scoreOperation;
	}


	@Override
	public List<ScoredChromosomeWindow> addWindow(ScoredChromosomeWindow window) throws ElementAddedNotSortedException {
		if (windowQueue.isEmpty()) {
			windowQueue.add(window);
			return new ArrayList<ScoredChromosomeWindow>();
		}
		int lastWindowStart = windowQueue.get(windowQueue.size() - 1).getStart();
		int currentWindowStart = window.getStart();
		if (currentWindowStart < lastWindowStart) {
			throw new ElementAddedNotSortedException();
		}
		int firstBinStart = (lastWindowStart / binSize) * binSize;
		int lastBinStop = ((currentWindowStart / binSize) * binSize);

		// add the new window at the end of the queue
		windowQueue.add(window);
		// retrieve the result of the pileup flattening
		List<ScoredChromosomeWindow> flattenPileup = getFlattenedPileup(firstBinStart, lastBinStop);
		// remove the element that are already processed
		removeProcessedElements(lastBinStop);
		return flattenPileup;
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


	@Override
	public List<ScoredChromosomeWindow> flush() {
		if (windowQueue.isEmpty()) {
			return new ArrayList<ScoredChromosomeWindow>();
		}
		int lastWindowStart = windowQueue.get(windowQueue.size() - 1).getStart();
		int lastWindowStop = windowQueue.get(windowQueue.size() - 1).getStop() - 1;
		int firstBinStart = (lastWindowStart / binSize) * binSize;
		int lastBinStop = (((lastWindowStop / binSize) + 1 )* binSize);
		List<ScoredChromosomeWindow> flattenedWindows = getFlattenedPileup(firstBinStart, lastBinStop);
		windowQueue.clear();
		return flattenedWindows;
	}


	/**
	 * Flattens the overlapping windows of the queue up to specified positions start and stop positions.
	 * @param firstBinStart
	 * @param lastBinStop
	 * @return a list of {@link ScoredChromosomeWindow} resulting from the flattening process.
	 * The score of the windows are computing accordingly to a {@link ScoreOperation} during
	 * construction of this {@link SimpleSCWPileupFlattener} object.
	 */
	private List<ScoredChromosomeWindow> getFlattenedPileup(int firstBinStart, int lastBinStop) {
		List<ScoredChromosomeWindow> flattenedPileup = new ArrayList<ScoredChromosomeWindow>();

		// nothing to do this time
		if (firstBinStart == lastBinStop) {
			return flattenedPileup;
		}
		int binCount = (lastBinStop - firstBinStart) / binSize;

		List<List<Float>> scores = new ArrayList<List<Float>>(binCount);
		for (int i = 0; i < binCount; i++) {
			scores.add(new ArrayList<Float>());
		}

		Iterator<ScoredChromosomeWindow> windowQueueIterator = windowQueue.listIterator();
		ScoredChromosomeWindow currentRead;

		while (windowQueueIterator.hasNext() &&
				((currentRead = windowQueueIterator.next()).getStart() < lastBinStop)) {
			int readFirstBinStart = (currentRead.getStart() / binSize) * binSize;
			readFirstBinStart = Math.max(readFirstBinStart, firstBinStart);
			// we subtract one the stop because this position is excluded
			int readLastBinStart = ((currentRead.getStop() - 1) / binSize) * binSize;
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
			if (score != 0) {
				// add the completed bin to the flatten pileup
				ScoredChromosomeWindow windowToAdd = new SimpleScoredChromosomeWindow(binStart, binStart + binSize, score);
				flattenedPileup.add(windowToAdd);
			}
		}
		return flattenedPileup;
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
			if (currentScoreList.size() == 1) {
				return currentScoreList.get(0);
			} else if (currentScoreList.size() == 2) {
				return currentScoreList.get(0) / currentScoreList.get(1);
			} else {
				throw new UnsupportedOperationException("Division with more than two operands not supported");
			}
		case MAXIMUM:
			return FloatLists.maxNoZero(currentScoreList);
		case MINIMUM:
			return FloatLists.minNoZero(currentScoreList);
		case MULTIPLICATION:
			if (currentScoreList.size() == 1) {
				// multiplication with a window with a score of 0
				return 0f;
			} else if (currentScoreList.size() == 2) {
				return currentScoreList.get(0) * currentScoreList.get(1);
			} else {
				throw new UnsupportedOperationException("Multiplication with more than two operands not supported");
			}
		case SUBTRACTION:
			if (currentScoreList.size() == 1) {
				return currentScoreList.get(0);
			} else if (currentScoreList.size() == 2) {
				return currentScoreList.get(0) - currentScoreList.get(1);
			} else {
				throw new UnsupportedOperationException("Subtraction with more than two operands not supported");
			}
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
