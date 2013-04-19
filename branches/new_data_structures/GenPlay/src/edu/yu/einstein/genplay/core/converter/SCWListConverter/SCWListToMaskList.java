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
package edu.yu.einstein.genplay.core.converter.SCWListConverter;

import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.converter.Converter;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.mask.MaskListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Creates a {@link SCWList} of masks from the data of the input {@link SCWList}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public class SCWListToMaskList implements Converter {

	private final SCWList 		list; 		// input list
	private GenomicListView<?> 	result;		// The output list.


	/**
	 * Creates a {@link SCWList} of masks from the data of the input {@link SCWList}
	 * @param scwList input list
	 */
	public SCWListToMaskList(SCWList scwList) {
		list = scwList;
	}


	@Override
	public void convert() throws Exception {
		List<ListView<ScoredChromosomeWindow>> resultList = new ArrayList<ListView<ScoredChromosomeWindow>>();
		for (ListView<ScoredChromosomeWindow> currentLV: list) {
			ListViewBuilder<ScoredChromosomeWindow> lvBuilder = new MaskListViewBuilder();
			for (ScoredChromosomeWindow scw: currentLV) {
				if ((scw.getScore() != Float.NaN) && (scw.getScore() != 0)) {
					lvBuilder.addElementToBuild(scw);
				}
			}
			resultList.add(lvBuilder.getListView());
		}
		result = new SimpleSCWList(resultList, SCWListType.MASK, null);
	}


	@Override
	public String getDescription() {
		return "Operation: Generate a Mask";
	}


	@Override
	public GenomicListView<?> getList() {
		return result;
	}


	@Override
	public String getProcessingDescription() {
		return "Generating a Mask";
	}
}
