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
package edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.mask;

import java.util.List;

import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.SCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.primitiveList.PrimitiveList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ElementAddedNotSortedException;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;

/**
 * Implementation of the {@link ListViewBuilder} interface vending
 * {@link MaskListView} objects.
 * @author Julien Lajugie
 */
public final class MaskListViewBuilder implements ListViewBuilder<ScoredChromosomeWindow>, SCWListViewBuilder {

	/** List of the start positions of the masks */
	private List<Integer> maskStarts;

	/** List of the stop positions of the masks */
	private List<Integer> maskStops;


	/**
	 * Creates an instance of {@link MaskListViewBuilder}
	 */
	public MaskListViewBuilder() {
		maskStarts = new PrimitiveList<Integer>(Integer.class);
		maskStops = new PrimitiveList<Integer>(Integer.class);
	}


	/**
	 * Adds an element to the ListView that will be built.
	 * To assure that ListView objects are immutable, this method
	 * will throw an exception if called after the getListView() has been called.
	 * Checks that the elements are added in start position order.
	 * @param start start position of the mask to add
	 * @param stop stop position of the mask to add
	 * @throws ObjectAlreadyBuiltException
	 * @throws ElementAddedNotSortedException If elements are not added in sorted order
	 */
	public void addElementToBuild(int start, int stop)
			throws ObjectAlreadyBuiltException, ElementAddedNotSortedException {
		if (maskStarts == null) {
			throw new ObjectAlreadyBuiltException();
		}
		if (maskStarts.isEmpty()) {
			maskStarts.add(start);
			maskStops.add(stop);
		} else {
			int lastElementIndex = maskStarts.size() -1;
			int lastStart = maskStarts.get(lastElementIndex);
			int lastStop = maskStops.get(lastElementIndex);
			if (start < lastStart) {
				// case where the element added are not sorted
				throw new ElementAddedNotSortedException();
			} else if (start <= lastStop) {
				// if the current start is smaller than the previous stop we merge the masks
				stop = Math.max(stop, lastStop);
				maskStops.set(lastElementIndex, stop);
			} else {
				maskStarts.add(start);
				maskStops.add(stop);
			}
		}
	}


	/**
	 * {@inheritDoc}
	 * The score parameter is not used.
	 */
	@Override
	public void addElementToBuild(int start, int stop, float score) throws ObjectAlreadyBuiltException {
		addElementToBuild(start, stop);
	}


	@Override
	public void addElementToBuild(ScoredChromosomeWindow element)
			throws ObjectAlreadyBuiltException, ElementAddedNotSortedException {
		addElementToBuild(element.getStart(), element.getStop());
	}


	/**
	 * Creates a clone of this {@link MaskListViewBuilder} prototype
	 * containing no elements.
	 */
	@Override
	public MaskListViewBuilder clone() {
		MaskListViewBuilder clone = new MaskListViewBuilder();
		return clone;
	}


	@Override
	public ListView<ScoredChromosomeWindow> getListView() {
		trimListsToSize();
		ListView<ScoredChromosomeWindow> listView = new MaskListView(maskStarts, maskStops);
		maskStarts = null;
		maskStops = null;
		return listView;
	}


	/**
	 * Trims the lists to their sizes in order to improve the memory usage of the list view
	 */
	private void trimListsToSize() {
		if (maskStarts instanceof PrimitiveList<?>) {
			((PrimitiveList<?>) maskStarts).trimToSize();
		}
		if (maskStops instanceof PrimitiveList<?>) {
			((PrimitiveList<?>) maskStops).trimToSize();
		}
	}
}