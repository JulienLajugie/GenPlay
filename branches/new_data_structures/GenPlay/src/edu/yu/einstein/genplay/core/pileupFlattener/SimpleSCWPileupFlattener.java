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

import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.SCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.dense.DenseSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.mask.MaskListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ElementAddedNotSortedException;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;
import edu.yu.einstein.genplay.util.FloatLists;


/**
 * {@link PileupFlattener} for {@link SimpleSCWList}
 * @author Julien Lajugie
 */
public class SimpleSCWPileupFlattener implements PileupFlattener {

	/** Queue containing the windows of the pileup */
	private final SCWQueue windowQueue;

	/** Builder to create the result {@link ListView} */
	private final SCWListViewBuilder resultLVBuilder;

	/** Operation to compute the score of the result value of the flattening */
	private final ScoreOperation scoreOperation;


	/**
	 * Creates an instance of {@link SimpleSCWPileupFlattener}
	 * @param scoreOperation {@link ScoreOperation} to compute the score of the result value of the flattening
	 * @param scwListType type of the list that this flattener will create
	 */
	public SimpleSCWPileupFlattener(ScoreOperation scoreOperation, SCWListType scwListType) {
		windowQueue = new SCWQueue();
		this.scoreOperation = scoreOperation;
		switch (scwListType) {
		case DENSE:
			resultLVBuilder = new DenseSCWListViewBuilder();
			break;
		case GENERIC:
			resultLVBuilder = new GenericSCWListViewBuilder();
			break;
		case MASK:
			resultLVBuilder = new MaskListViewBuilder();
			break;
		default:
			// case where the input list is a binlist
			// the simple SCW flattener will create a generic result list
			resultLVBuilder = new GenericSCWListViewBuilder();
		}
	}


	/**
	 * Creates an instance of {@link SimpleSCWPileupFlattener}
	 * @param scoreOperation scoreOperation {@link ScoreOperation} to compute the score of the result value of the flattening
	 * @param resultLVBuilder {@link ListViewBuilder} to create the result {@link ListView}
	 */
	public SimpleSCWPileupFlattener(ScoreOperation scoreOperation, SCWListViewBuilder resultLVBuilder) {
		windowQueue = new SCWQueue();
		this.scoreOperation = scoreOperation;
		this.resultLVBuilder = resultLVBuilder;
	}


	@Override
	public void addWindow(int windowStart, int windowStop, float windowScore) throws ElementAddedNotSortedException {
		if (windowQueue.isEmpty()) {
			windowQueue.add(windowStart, windowStop, windowScore);
		} else {
			int newWindowStart = windowStart;
			int lastStart = windowQueue.get(windowQueue.size() - 1).getStart();
			if (newWindowStart < lastStart) {
				throw new ElementAddedNotSortedException();
			}
			// add the new window at the end of the queue
			windowQueue.add(windowStart, windowStop, windowScore);
			// retrieve the result of the pileup flattening
			flattenPileup(lastStart, newWindowStart);
			// remove the element that are not needed anymore
			removeProcessedElements(newWindowStart);
		}
	}


	@Override
	public void addWindow(ScoredChromosomeWindow windowToAdd) throws ElementAddedNotSortedException, ObjectAlreadyBuiltException {
		addWindow(windowToAdd.getStart(), windowToAdd.getStop(), windowToAdd.getScore());
	}


	/**
	 * A new instance of {@link SimpleSCWPileupFlattener} containing no element.
	 */
	@Override
	public SimpleSCWPileupFlattener clone() {
		return new SimpleSCWPileupFlattener(scoreOperation, resultLVBuilder.clone());
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


	/**
	 * Flattens the overlapping windows of the queue between the specified start and stop positions.
	 * The score of the windows are computed accordingly to the {@link ScoreOperation} value specified
	 * during construction of this {@link SimpleSCWPileupFlattener} object.
	 * @param startPosition
	 * @param stopPosition
	 */
	private void flattenPileup(int startPosition, int stopPosition) {
		ScoredChromosomeWindow currentWindow;
		// nodes are start and stop positions of the windows resulting from the flattening process
		List<Integer> nodes = new ArrayList<Integer>();
		Iterator<ScoredChromosomeWindow> iterator = windowQueue.iterator();
		nodes.add(startPosition);
		while (iterator.hasNext() && ((currentWindow = iterator.next()).getStart() < stopPosition)) {
			if (currentWindow.getStart() > startPosition) {
				nodes.add(currentWindow.getStart());
			}
			if ((currentWindow.getStop() > startPosition) && (currentWindow.getStop() < stopPosition)) {
				nodes.add(currentWindow.getStop());
			}
		}
		nodes.add(stopPosition);
		// sort the nodes
		Collections.sort(nodes);
		// remove duplicate nodes
		removeDuplicateNodes(nodes);
		// compute the score values for each windows from the flattening
		List<Float> scores = computeScores(nodes);
		// generate the list of windows from the flattening process
		for (int i = 0; i < scores.size(); i++) {
			if (scores.get(i) != 0) {
				resultLVBuilder.addElementToBuild(nodes.get(i), nodes.get(i + 1), scores.get(i));
			}
		}
	}


	/**
	 * Flattens the remaining elements of the queue
	 */
	private void flush() {
		if (!windowQueue.isEmpty()) {
			int lastStart = 0;
			lastStart = windowQueue.get(windowQueue.size() - 1).getStart();
			flattenPileup(lastStart, Integer.MAX_VALUE);
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
