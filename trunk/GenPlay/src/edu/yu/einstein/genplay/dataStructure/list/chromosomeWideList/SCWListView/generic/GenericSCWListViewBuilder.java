/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic;

import java.util.List;

import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.SCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.primitiveList.PrimitiveList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ElementAddedNotSortedException;
import edu.yu.einstein.genplay.exception.exceptions.ElementAddedOverlapException;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;

/**
 * Implementation of the {@link ListViewBuilder} interface vending
 * {@link GenericSCWListView} objects.
 * @author Julien Lajugie
 */
public final class GenericSCWListViewBuilder implements ListViewBuilder<ScoredChromosomeWindow>, SCWListViewBuilder {

	/** List of the start positions of the SCWs */
	private List<Integer> windowStarts;

	/** List of the stop positions of the SCWs */
	private List<Integer> windowStops;

	/** List of the score values of the SCWs */
	private List<Float> windowScores;


	/**
	 * Creates an instance of {@link GenericSCWListViewBuilder}
	 */
	public GenericSCWListViewBuilder() {
		windowStarts = new PrimitiveList<Integer>(Integer.class);
		windowStops = new PrimitiveList<Integer>(Integer.class);
		windowScores = new PrimitiveList<Float>(Float.class);
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
	 */
	@Override
	public void addElementToBuild(int start, int stop, float score)
			throws ObjectAlreadyBuiltException, ElementAddedNotSortedException, ElementAddedOverlapException {
		if (windowStarts == null) {
			throw new ObjectAlreadyBuiltException();
		}
		if (!windowStarts.isEmpty()) {
			int lastElementIndex = windowStarts.size() -1;
			int lastStart = windowStarts.get(lastElementIndex);;
			int lastStop = windowStops.get(lastElementIndex);
			float lastScore = windowScores.get(lastElementIndex);
			if (start < lastStart) {
				// case where the elements added are not sorted
				throw new ElementAddedNotSortedException();
			} else if (start < lastStop) {
				// case where the elements added overlap
				throw new ElementAddedOverlapException();
			}
			// if the previous window stop is equal to the current window start
			// and the 2 windows have the same same score we merge them
			if ((start == lastStop) && (score == lastScore)) {
				windowStops.set(lastElementIndex, stop);
				return;
			}
		}
		windowStarts.add(start);
		windowStops.add(stop);
		windowScores.add(score);
	}


	@Override
	public void addElementToBuild(ScoredChromosomeWindow element)
			throws ObjectAlreadyBuiltException, ElementAddedNotSortedException, ElementAddedOverlapException {
		addElementToBuild(element.getStart(), element.getStop(), element.getScore());
	}


	/**
	 * Adds a {@link ScoredChromosomeWindow} to the list to be build without checking if the elements are
	 * added in start position order
	 * @param scw window to add
	 * @throws ObjectAlreadyBuiltException
	 */
	public void addUnsortedElementToBuild(ScoredChromosomeWindow scw) throws ObjectAlreadyBuiltException {
		if (windowStops == null) {
			throw new ObjectAlreadyBuiltException();
		}
		windowStarts.add(scw.getStart());
		windowStops.add(scw.getStop());
		windowScores.add(scw.getScore());
	}


	/**
	 * Creates a clone of this {@link GenericSCWListViewBuilder} prototype
	 * containing no elements.
	 */
	@Override
	public GenericSCWListViewBuilder clone() {
		GenericSCWListViewBuilder clone = new GenericSCWListViewBuilder();
		return clone;
	}


	@Override
	public ListView<ScoredChromosomeWindow> getListView() {
		trimListsToSize();
		ListView<ScoredChromosomeWindow> listView = new GenericSCWListView(windowStarts, windowStops, windowScores);
		windowStarts = null;
		windowStops = null;
		windowScores = null;
		return listView;
	}


	/**
	 * Trims the lists to their sizes in order to improve the memory usage of the list view
	 */
	private void trimListsToSize() {
		if (windowStarts instanceof PrimitiveList<?>) {
			((PrimitiveList<?>) windowStarts).trimToSize();
		}
		if (windowStops instanceof PrimitiveList<?>) {
			((PrimitiveList<?>) windowStops).trimToSize();
		}
		if (windowScores instanceof PrimitiveList<?>) {
			((PrimitiveList<?>) windowScores).trimToSize();
		}
	}
}
