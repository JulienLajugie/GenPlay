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
		int newWindowStart = window.getStart();
		int lastStart = windowQueue.get(windowQueue.size() - 1).getStart();
		if (newWindowStart < lastStart) {
			throw new ElementAddedNotSortedException();
		}
		// add the new window at the end of the queue
		windowQueue.add(window);
		// retrieve the result of the pileup flattening
		List<ScoredChromosomeWindow> flattenPileup = getFlattenedPileup(newWindowStart);
		// remove the ele
		removeProcessedElements(newWindowStart);
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
		float partOfReadInsideBin = (currentRead.getSize() - (readLengthBeforeBinStart + readLengthAfterBinStop)) / currentRead.getSize();
		float score = currentRead.getScore() * partOfReadInsideBin;
		return score;
	}


	@Override
	public List<ScoredChromosomeWindow> flush() {
		List<ScoredChromosomeWindow> flattenedWindows = getFlattenedPileup(Integer.MAX_VALUE);
		windowQueue.clear();
		return flattenedWindows;
	}


	/**
	 * Flattens the overlapping windows of the queue up to specified positions.
	 * @param position a position
	 * @return a list of {@link ScoredChromosomeWindow} resulting from the flattening process.
	 * The score of the windows are computing accordingly to a {@link ScoreOperation} during
	 * construction of this {@link SimpleSCWPileupFlattener} object.
	 */
	private List<ScoredChromosomeWindow> getFlattenedPileup(int position) {
		List<ScoredChromosomeWindow> flattenedPileup = new ArrayList<ScoredChromosomeWindow>();
		int firstBinCompletedStart = (windowQueue.get(0).getStart() / binSize) * binSize;
		int lastBinCompletedStop = (position / binSize) * binSize;
		// nothing to do this time
		if (firstBinCompletedStart == lastBinCompletedStop) {
			return flattenedPileup;
		}
		int currentBinCompletedStart = firstBinCompletedStart;
		// loop for each bin before the bin of the item to add
		while (currentBinCompletedStart < lastBinCompletedStop) {
			int currentBinCompletedStop = currentBinCompletedStart + binSize;
			int currentIndex = 0;
			ScoredChromosomeWindow currentRead = windowQueue.get(currentIndex);
			// list of the scores of the read falling in the bin
			List<Float> currentScoreList = new ArrayList<Float>();
			// loop for each read falling in the bin
			while (currentRead.getStart() < currentBinCompletedStop) {
				if (currentRead.getStop() > currentBinCompletedStart) {
					currentScoreList.add(computeNormalizedScore(currentBinCompletedStart, currentBinCompletedStop, currentRead));
				}
				currentIndex++;
				currentRead = windowQueue.get(currentIndex);
			}
			float score = processScoreList(currentScoreList);
			// add the completed bin to the flatten pileup
			ScoredChromosomeWindow windowToAdd = new SimpleScoredChromosomeWindow(currentBinCompletedStart, currentBinCompletedStop, score);
			flattenedPileup.add(windowToAdd);
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
			if (windowQueue.get(i).getStop() < position) {
				windowQueue.remove(i);
			} else {
				i++;
			}
		}
	}
}
