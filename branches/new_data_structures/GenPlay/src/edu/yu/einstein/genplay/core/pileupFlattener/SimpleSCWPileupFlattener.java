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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ElementAddedNotSortedException;
import edu.yu.einstein.genplay.util.FloatLists;


/**
 * {@link PileupFlattener} for {@link SimpleSCWList}
 * @author Julien Lajugie
 */
public class SimpleSCWPileupFlattener implements PileupFlattener {

	/** Queue containing the windows of the pileup */
	private final List<ScoredChromosomeWindow> windowQueue;

	/** Operation to compute the score of the result value of the flattening */
	private final ScoreOperation scoreOperation;


	/**
	 * Creates an instance of {@link SimpleSCWPileupFlattener}
	 * @param scoreOperation {@link ScoreOperation} to compute the score of the result value of the flattening
	 */
	public SimpleSCWPileupFlattener(ScoreOperation scoreOperation) {
		windowQueue = new ArrayList<ScoredChromosomeWindow>();
		this.scoreOperation = scoreOperation;
	}


	@Override
	public List<ScoredChromosomeWindow> addWindow(ScoredChromosomeWindow window) throws ElementAddedNotSortedException {
		int newWindowStart = window.getStart();
		if (!windowQueue.isEmpty()) {
			int lastStart = windowQueue.get(windowQueue.size() - 1).getStart();
			if (newWindowStart < lastStart) {
				throw new ElementAddedNotSortedException();
			}
		}
		// add the new window at the end of the queue
		windowQueue.add(window);
		// retrieve the result of the pileup flattening
		List<ScoredChromosomeWindow> flattenPileup = getFlattenedPileup(newWindowStart);
		// remove the element that are not needed anymore
		removeProcessedElements(newWindowStart);
		return flattenPileup;
	}


	/**
	 * Computes a score for each window of the list of flattened windows.
	 * The score is based on the scores of the different windows overlapping the resulting flattened window.
	 * The score of each window is computed using the {@link ScoreOperation} set during the construction
	 * of this {@link SimpleSCWPileupFlattener} object.
	 * @param nodes list of the nodes (start and stop positions) from the flattening of the pileup
	 * @return a list of score containing one score for each flattened window
	 */
	private List<Float> computeScores(List<Integer> nodes) {
		List<Float> scores = new ArrayList<Float>();
		if ((nodes != null) && (nodes.size() > 1)) {
			// creates structure for scores of each flattened window
			List<List<Float>> scoreLists = new ArrayList<List<Float>>();
			for (int i = 0; i < (nodes.size() - 1); i++) {
				scoreLists.add(new ArrayList<Float>());
			}
			// retrieve list of scores for each flattened window
			for (int i = 0; (i + 1) < nodes.size(); i++) {
				int flattenedStart = nodes.get(i);
				int flattenedStop = nodes.get(i + 1);
				int j = 0;
				while ((j < windowQueue.size()) && (windowQueue.get(j).getStart() < flattenedStop)) {
					if (windowQueue.get(j).getStop() > flattenedStart) {
						scoreLists.get(i).add(windowQueue.get(j).getScore());
					}
					j++;
				}
			}
			// compute and return score of each flattened window
			for (List<Float> currentScoreList: scoreLists) {
				scores.add(processScoreList(currentScoreList));
			}

		}
		return scores;
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
		ScoredChromosomeWindow currentWindow;
		// nodes are start and stop positions of the windows resulting from the flattening process
		List<Integer> nodes = new ArrayList<Integer>();
		Iterator<ScoredChromosomeWindow> iterator = windowQueue.iterator();
		while (iterator.hasNext() && ((currentWindow = iterator.next()).getStart() < position)) {
			nodes.add(currentWindow.getStart());
			if (currentWindow.getStop() < position) {
				nodes.add(currentWindow.getStop());
			}
		}
		nodes.add(position);
		// sort the nodes
		Collections.sort(nodes);
		// remove duplicate nodes
		removeDuplicateNodes(nodes);
		// compute the score values for each windows from the flattening
		List<Float> scores = computeScores(nodes);
		// generate the list of windows from the flattening process
		List<ScoredChromosomeWindow> flattenedPileup = new ArrayList<ScoredChromosomeWindow>();
		for (int i = 0; i < scores.size(); i++) {
			ScoredChromosomeWindow windowToAdd = new SimpleScoredChromosomeWindow(nodes.get(i), nodes.get(i + 1), scores.get(i));
			flattenedPileup.add(windowToAdd);
		}
		return flattenedPileup;
	}


	/**
	 * Computes a score for a window based on the scores of the different windows overlapping the resulting
	 * flattened window.  The score of the window is computed using the {@link ScoreOperation} set during the
	 * construction of this {@link SimpleSCWPileupFlattener} object.
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
	 * Removes the duplicate values in the sorted list of nodes
	 * @param nodes sorted list of integers
	 */
	private void removeDuplicateNodes(List<Integer> nodes) {
		// nothing to do in these cases
		if ((nodes != null) && (nodes.size() > 1)) {
			int previousValue = nodes.get(0);
			int i = 1;
			while (i < nodes.size()) {
				int currentValue = nodes.get(i);
				// remove if current value is equal to previous one
				if (currentValue == previousValue) {
					nodes.remove(i);
				} else {
					previousValue = currentValue;
					i++;
				}
			}
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
