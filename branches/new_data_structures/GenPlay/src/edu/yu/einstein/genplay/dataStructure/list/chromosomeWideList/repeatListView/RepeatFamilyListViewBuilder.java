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
package edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.repeatListView;

import java.util.List;

import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.primitiveList.ListOfIntArraysAsIntegerList;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;

/**
 * Implementation of the {@link ListViewBuilder} interface vending
 * {@link RepeatFamilyListView} objects.
 * @author Julien Lajugie
 */
public class RepeatFamilyListViewBuilder implements ListViewBuilder<ChromosomeWindow> {

	/** List of the start positions of the repeats */
	private List<Integer> repeatStarts;

	/** List of the stop positions of the repeats */
	private List<Integer> repeatStops;

	/** Name of the family of repeat */
	private String name;


	/**
	 * Creates an instance of {@link RepeatFamilyListViewBuilder}
	 * @param repeatFamilyName name of the family of repeat to build
	 */
	public RepeatFamilyListViewBuilder(String repeatFamilyName) {
		name = repeatFamilyName;
		repeatStarts = new ListOfIntArraysAsIntegerList();
		repeatStops = new ListOfIntArraysAsIntegerList();
	}


	@Override
	public void addElementToBuild(ChromosomeWindow element) throws ObjectAlreadyBuiltException {
		if (repeatStarts != null) {
			repeatStarts.add(element.getStart());
			repeatStops.add(element.getStop());
		} else {
			throw new ObjectAlreadyBuiltException();
		}
	}


	@Override
	public RepeatFamilyListViewBuilder clone() throws CloneNotSupportedException {
		RepeatFamilyListViewBuilder clone = new RepeatFamilyListViewBuilder(name);
		return clone;
	}


	@Override
	public ListView<ChromosomeWindow> getListView() {
		ListView<ChromosomeWindow> listView = new RepeatFamilyListView(name, repeatStarts, repeatStops);
		repeatStarts = null;
		repeatStops = null;
		name = null;
		return listView;
	}
}
