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

import edu.yu.einstein.genplay.dataStructure.list.arrayList.ListOfIntArraysAsIntegerList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;

/**
 * Implementation of the {@link ListViewBuilder} interface vending
 * {@link MaskListView} objects.
 * @author Julien Lajugie
 */
public final class MaskListViewBuilder implements ListViewBuilder<ScoredChromosomeWindow> {

	/** List of the start positions of the masks */
	private List<Integer> maskStarts;

	/** List of the stop positions of the masks */
	private List<Integer> maskStops;


	/**
	 * Creates an instance of {@link MaskListViewBuilder}
	 */
	public MaskListViewBuilder() {
		maskStarts = new ListOfIntArraysAsIntegerList();
		maskStops = new ListOfIntArraysAsIntegerList();
	}


	/**
	 * Adds an element to the ListView that will be built.
	 * To assure that ListView objects are immutable, this method
	 * will throw an exception if called after the getListView() has been called.
	 * @param start start position of the mask to add
	 * @param stop stop position of the mask to add
	 * @throws ObjectAlreadyBuiltException
	 */
	public void addElementToBuild(int start, int stop) throws ObjectAlreadyBuiltException {
		if (maskStarts != null) {
			int lastElementIndex = maskStops.size() -1;
			int lastStop = maskStops.get(lastElementIndex);
			// if the current start is smaller than the previous stop we merge the masks
			if (start < lastStop) {
				maskStops.set(lastElementIndex, stop);
			} else {
				maskStarts.add(start);
				maskStops.add(stop);
			}
		} else {
			throw new ObjectAlreadyBuiltException();
		}
	}


	@Override
	public void addElementToBuild(ScoredChromosomeWindow element) throws ObjectAlreadyBuiltException {
		addElementToBuild(element.getStart(), element.getStop());
	}


	/**
	 * Creates a clone of this {@link MaskListViewBuilder} prototype
	 * containing no elements.
	 */
	@Override
	public MaskListViewBuilder clone() throws CloneNotSupportedException {
		MaskListViewBuilder clone = new MaskListViewBuilder();
		return clone;
	}


	@Override
	public ListView<ScoredChromosomeWindow> getListView() {
		ListView<ScoredChromosomeWindow> listView = new MaskListView(maskStarts, maskStops);
		maskStarts = null;
		maskStops = null;
		return listView;
	}
}