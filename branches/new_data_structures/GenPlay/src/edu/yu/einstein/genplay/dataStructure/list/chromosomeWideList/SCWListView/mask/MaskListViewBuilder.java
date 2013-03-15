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

import edu.yu.einstein.genplay.dataStructure.list.arrayList.ListofIntArraysAsIntegerList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;

/**
 * Implementation of the {@link ListViewBuilder} interface vending
 * {@link MaskListView} objects.
 * @author Julien Lajugie
 */
public class MaskListViewBuilder implements ListViewBuilder<ScoredChromosomeWindow> {

	/** List of the start positions of the masks */
	private List<Integer> maskStarts;

	/** List of the stop positions of the masks */
	private List<Integer> maskStops;


	/**
	 * Creates an instance of {@link MaskListViewBuilder}
	 */
	public MaskListViewBuilder() {
		maskStarts = new ListofIntArraysAsIntegerList();
		maskStops = new ListofIntArraysAsIntegerList();
	}


	@Override
	public void addElementToBuild(ScoredChromosomeWindow element) throws ObjectAlreadyBuiltException {
		if (maskStarts != null) {
			maskStarts.add(element.getStart());
			maskStarts.add(element.getStop());
		} else {
			throw new ObjectAlreadyBuiltException();
		}
	}


	@Override
	public ListView<ScoredChromosomeWindow> getListView() {
		ListView<ScoredChromosomeWindow> listView = new MaskListView(maskStarts, maskStops);
		maskStarts = null;
		maskStops = null;
		return listView;
	}
}
