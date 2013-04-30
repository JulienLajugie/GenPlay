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

import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.mask.MaskListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ElementAddedNotSortedException;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;

/**
 * Implementation of the {@link ListViewBuilder} interface vending
 * {@link RepeatFamilyListView} objects.
 * @author Julien Lajugie
 */
public class RepeatFamilyListViewBuilder implements ListViewBuilder<ScoredChromosomeWindow> {

	/** Builders to create the list of repeats */
	private final MaskListViewBuilder repeatListBuilder;

	/** Name of the family of repeat */
	private String name;


	/**
	 * Creates an instance of {@link RepeatFamilyListViewBuilder}
	 * @param repeatFamilyName name of the family of repeat to build
	 */
	public RepeatFamilyListViewBuilder(String repeatFamilyName) {
		name = repeatFamilyName;
		repeatListBuilder = new MaskListViewBuilder();
	}


	/**
	 * Adds an element to the {@link ListView} that will be built.
	 * To assure that {@link ListView} objects are immutable, this method will throw an exception
	 * if called after the getListView() has been called.
	 * @param start start position of the repeat to add
	 * @param stop stop position of the repeat to add
	 * @throws ObjectAlreadyBuiltException if this method is called after the build method was called
	 * @throws ElementAddedNotSortedException
	 */
	public void addElementToBuild(int start, int stop) throws ObjectAlreadyBuiltException, ElementAddedNotSortedException {
		repeatListBuilder.addElementToBuild(start, stop);
	}


	@Override
	public void addElementToBuild(ScoredChromosomeWindow element) throws ObjectAlreadyBuiltException, ElementAddedNotSortedException {
		addElementToBuild(element.getStart(), element.getStop());
	}


	@Override
	public RepeatFamilyListViewBuilder clone() throws CloneNotSupportedException {
		RepeatFamilyListViewBuilder clone = new RepeatFamilyListViewBuilder(name);
		return clone;
	}


	@Override
	public ListView<ScoredChromosomeWindow> getListView() {
		ListView<ScoredChromosomeWindow> listView = new RepeatFamilyListView(name, repeatListBuilder.getListView());
		name = null;
		return listView;
	}
}
