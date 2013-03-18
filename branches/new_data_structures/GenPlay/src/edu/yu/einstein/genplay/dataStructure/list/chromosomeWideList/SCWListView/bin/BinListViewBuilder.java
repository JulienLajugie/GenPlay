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

import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.ListOfFloatArraysAsFloatList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.ListOfHalfArraysAsFloatList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;

/**
 * Implementation of the {@link ListViewBuilder} interface vending
 * {@link BinListView} objects.
 * @author Julien Lajugie
 */
public final class BinListViewBuilder implements ListViewBuilder<ScoredChromosomeWindow> {

	/** Size of the bins of the list */
	private final int binSize;

	/** List of the score values of the SCWs */
	private List<Float> windowScores;


	/**
	 * Creates an instance of {@link BinListViewBuilder}
	 * @param scorePrecision precision of the scores of the {@link ListView} to build
	 * @param binSize size of the bins of the {@link ListView}
	 */
	public BinListViewBuilder(ScorePrecision scorePrecision, int binSize) {
		this.binSize = binSize;
		switch (scorePrecision) {
		case PRECISION_16BIT:
			windowScores = new ListOfHalfArraysAsFloatList();
			break;
		case PRECISION_32BIT:
			windowScores = new ListOfFloatArraysAsFloatList();
			break;
		}
	}


	@Override
	public void addElementToBuild(ScoredChromosomeWindow element) throws ObjectAlreadyBuiltException {
		if (windowScores != null) {
			windowScores.add(element.getScore());
		} else {
			throw new ObjectAlreadyBuiltException();
		}
	}


	@Override
	public ListView<ScoredChromosomeWindow> getListView() {
		ListView<ScoredChromosomeWindow> listView = new BinListView(binSize, windowScores);
		windowScores = null;
		return listView;
	}
}
