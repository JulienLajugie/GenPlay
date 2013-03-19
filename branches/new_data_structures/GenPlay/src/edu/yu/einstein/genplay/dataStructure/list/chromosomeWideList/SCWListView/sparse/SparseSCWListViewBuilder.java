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
package edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.sparse;

import java.util.List;

import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.ListOfFloatArraysAsFloatList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.ListOfHalfArraysAsFloatList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.ListOfIntArraysAsIntegerList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;

/**
 * Implementation of the {@link ListViewBuilder} interface vending
 * {@link SparseSCWListView} objects.
 * @author Julien Lajugie
 */
public final class SparseSCWListViewBuilder implements ListViewBuilder<ScoredChromosomeWindow> {

	/** List of the stop positions of the SCWs */
	private List<Integer> windowStops;

	/** List of the start positions of the SCWs */
	private List<Integer> windowStarts;

	/** List of the score values of the SCWs */
	private List<Float> windowScores;


	/**
	 * Creates an instance of {@link SparseSCWListViewBuilder}
	 * @param scorePrecision precision of the scores of the {@link ListView} to build
	 */
	public SparseSCWListViewBuilder(ScorePrecision scorePrecision) {
		windowStarts = new ListOfIntArraysAsIntegerList();
		windowStops = new ListOfIntArraysAsIntegerList();
		switch (scorePrecision) {
		case PRECISION_16BIT:
			windowScores = new ListOfHalfArraysAsFloatList();
			break;
		case PRECISION_32BIT:
			windowScores = new ListOfFloatArraysAsFloatList();
			break;
		}
	}


	/**
	 * Adds an element to the ListView that will be built.
	 * To assure that ListView objects are immutable, this method
	 * will throw an exception if called after the getListView() has been called.
	 * @param start start position of the SCW to add
	 * @param stop stop position of the SCW to add
	 * @param score score value of the SCW to add
	 * @throws ObjectAlreadyBuiltException
	 */
	public void addElementToBuild(int start, int stop, float score) throws ObjectAlreadyBuiltException {
		if (windowStops != null) {
			windowStarts.add(start);
			windowStops.add(stop);
			windowScores.add(score);
		} else {
			throw new ObjectAlreadyBuiltException();
		}
	}


	@Override
	public void addElementToBuild(ScoredChromosomeWindow element) throws ObjectAlreadyBuiltException {
		addElementToBuild(element.getStart(), element.getStop(), element.getScore());
	}


	@Override
	public ListView<ScoredChromosomeWindow> getListView() {
		ListView<ScoredChromosomeWindow> listView = new SparseSCWListView(windowStarts, windowStops, windowScores);
		windowStarts = null;
		windowStops = null;
		windowScores = null;
		return listView;
	}
}
