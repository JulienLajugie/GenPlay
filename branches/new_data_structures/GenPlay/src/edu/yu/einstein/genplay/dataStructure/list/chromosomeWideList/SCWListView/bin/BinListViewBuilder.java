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
package edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.bin;

import java.util.List;

import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.SCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.primitiveList.FloatListFactory;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ElementAddedDontFallInBinException;
import edu.yu.einstein.genplay.exception.exceptions.ElementAddedNotSortedException;
import edu.yu.einstein.genplay.exception.exceptions.ElementAddedOverlapException;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;

/**
 * Implementation of the {@link ListViewBuilder} interface vending
 * {@link BinListView} objects.
 * @author Julien Lajugie
 */
public final class BinListViewBuilder implements ListViewBuilder<ScoredChromosomeWindow>, SCWListViewBuilder {

	/** Size of the bins of the list */
	private final int binSize;

	/** List of the score values of the SCWs */
	private List<Float> windowScores;


	/**
	 * Creates an instance of {@link BinListViewBuilder}
	 * @param binSize size of the bins of the {@link ListView}
	 */
	public BinListViewBuilder(int binSize) {
		this.binSize = binSize;
		windowScores = FloatListFactory.createFloatList();
	}


	/**
	 * Adds a new {@link ScoredChromosomeWindow} to the list to be build with the specified score.
	 * The start value of the window is equal to (bin size) * element added index.
	 * The stop value of the window is equal to (bin size) * (element added index + 1).
	 * @param score score of the window to add
	 */
	public void addElementToBuild(float score) {
		windowScores.add(score);
	}


	/**
	 * Adds an element to the ListView that will be built.
	 * To assure that ListView objects are immutable, this method
	 * will throw an exception if called after the getListView() has been called.
	 * Checks that the elements are added in start position order.
	 * @param start start position of the SCW to add
	 * @param stop stop position of the SCW to add
	 * @param score score value of the SCW to add
	 * @throws ObjectAlreadyBuiltException
	 * @throws ElementAddedNotSortedException If elements are not added in sorted order
	 * @throws ElementAddedOverlapException If elements added overlaps
	 * @throws ElementAddedDontFallInBinException If the window added doesn't correspond to a bin
	 */
	@Override
	public void addElementToBuild(int start, int stop, float score)
			throws ObjectAlreadyBuiltException, ElementAddedNotSortedException, ElementAddedOverlapException, ElementAddedDontFallInBinException {
		if (windowScores == null) {
			throw new ObjectAlreadyBuiltException();
		}
		if (((start % binSize) != 0) || ((stop % binSize) != 0)) {
			throw new ElementAddedDontFallInBinException();
		}
		if (!windowScores.isEmpty()) {
			int lastElementIndex = windowScores.size() -1;
			int lastStart = lastElementIndex * binSize;
			int lastStop = ((lastElementIndex + 1) * binSize);
			if (start < lastStart) {
				// case where the elements added are not sorted
				throw new ElementAddedNotSortedException();
			} else if (start < lastStop) {
				// case where the elements added overlap
				throw new ElementAddedOverlapException();
			}
		}
		int indexWindowToAdd = start / binSize;
		while (windowScores.size() < indexWindowToAdd) {
			windowScores.add(0f);
		}
		windowScores.add(score);
	}


	@Override
	public void addElementToBuild(ScoredChromosomeWindow element)
			throws ObjectAlreadyBuiltException, ElementAddedNotSortedException, ElementAddedOverlapException, ElementAddedDontFallInBinException {
		addElementToBuild(element.getStart(), element.getStop(), element.getScore());
	}


	@Override
	public BinListViewBuilder clone() {
		BinListViewBuilder clone = new BinListViewBuilder(binSize);
		return clone;
	}


	@Override
	public ListView<ScoredChromosomeWindow> getListView() {
		ListView<ScoredChromosomeWindow> listView = new BinListView(binSize, windowScores);
		windowScores = null;
		return listView;
	}
}
