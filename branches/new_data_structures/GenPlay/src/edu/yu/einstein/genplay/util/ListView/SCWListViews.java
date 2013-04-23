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
package edu.yu.einstein.genplay.util.ListView;

import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListView;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Provides utilities for {@link ListView} objects of items implementing {@link ScoredChromosomeWindow}.
 * @author Julien Lajugie
 */
public class SCWListViews {


	/**
	 * Factory method that creates a {@link GenericSCWListView} with only one element
	 * having the specified start, stop and score values
	 * @param start start position of the only element of the list
	 * @param stop stop position of the only element of the list
	 * @param score score of the only element of the list
	 * @return a {@link GenericSCWListView} with only one element
	 */
	public static final ListView<ScoredChromosomeWindow> createGenericSCWListView(int start, int stop, float score) {
		GenericSCWListViewBuilder builder = new GenericSCWListViewBuilder();
		builder.addElementToBuild(start, stop, score);
		return builder.getListView();
	}
}
