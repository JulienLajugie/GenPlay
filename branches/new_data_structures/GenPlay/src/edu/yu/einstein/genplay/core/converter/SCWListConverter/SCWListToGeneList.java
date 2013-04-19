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
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.gene.SimpleGene;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.geneListView.GeneListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.SimpleGeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.util.ListView.SCWListViews;


/**
 * Creates a {@link GeneList} from the data of the input {@link SCWList}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public class SCWListToGeneList implements Converter {

	private final SCWList 			list;		// input list
	private GenomicListView<?> 		result;		// The output list.
	private final ScorePrecision 	precision;	// precision of the scores of the result list


	/**
	 * Creates a {@link GeneList} from the data of the input {@link SCWList}
	 * @param scwList input list
	 * @param precision precision of the scores of the result list
	 */
	public SCWListToGeneList(SCWList scwList, ScorePrecision precision) {
		list = scwList;
		this.precision = precision;
	}


	@Override
	public void convert() throws Exception {
		List<ListView<Gene>> resultList = new ArrayList<ListView<Gene>>();
		int geneNumber = 1;
		for (ListView<ScoredChromosomeWindow> currentLV: list) {
			ListViewBuilder<Gene> lvBuilder = new GeneListViewBuilder(precision);
			for (ScoredChromosomeWindow scw: currentLV) {
				int start = scw.getStart();
				int stop = scw.getStop();
				float score = scw.getScore();
				ListView<ScoredChromosomeWindow> exonLV = SCWListViews.createGenericSCWListView(start, stop, score, precision);
				Gene geneToAdd = new SimpleGene("Gene#" + geneNumber, Strand.FIVE, start, stop, score, exonLV);
				lvBuilder.addElementToBuild(geneToAdd);
				geneNumber++;
			}
			resultList.add(lvBuilder.getListView());
		}
		result = new SimpleGeneList(resultList, precision, null, null);
	}


	@Override
	public String getDescription() {
		return "Operation: Generate Gene Track";
	}


	@Override
	public GenomicListView<?> getList() {
		return result;
	}


	@Override
	public String getProcessingDescription() {
		return "Generating Gene Track";
	}
}
