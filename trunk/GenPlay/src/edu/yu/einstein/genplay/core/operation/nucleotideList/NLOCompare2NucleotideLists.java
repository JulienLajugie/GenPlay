/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.core.operation.nucleotideList;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.Nucleotide;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.SCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.dense.DenseSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.nucleotideList.NucleotideList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;


/**
 * Creates a {@link SCWList} showing the differences between to {@link NucleotideList}
 * @author Julien Lajugie
 */
public class NLOCompare2NucleotideLists implements Operation<SCWList> {

	private boolean					stopped = false;	// true if the operation must be stopped
	private final NucleotideList 	list1;				// first nucleotide
	private final NucleotideList 	list2;				// second nucleotide list


	/**
	 * Creates an instance of {@link NLOCompare2NucleotideLists}
	 * @param list1 first nucleotide list
	 * @param list2 second nucleotide list
	 */
	public NLOCompare2NucleotideLists(NucleotideList list1, NucleotideList list2) {
		this.list1 = list1;
		this.list2 = list2;
	}


	@Override
	public SCWList compute() throws Exception {
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		SCWListViewBuilder builderProto = new DenseSCWListViewBuilder();
		SCWListBuilder resultListBuilder = new SCWListBuilder(builderProto);

		for (Chromosome chromosome: projectChromosomes) {
			ListView<Nucleotide> chrList1 = list1.get(chromosome);
			ListView<Nucleotide> chrList2 = list2.get(chromosome);

			if ((chrList1 != null) && (chrList2 != null)) {
				int maxLength = Math.max(chrList1.size(), chrList2.size());
				for (int i = 0; (i < maxLength) && !stopped; i++) {
					if ((i % 1000000) == 0) {
						System.out.println(chromosome.getName() + ": " + i);
					}

					float score = 0f;
					if ((i >= chrList1.size()) || (i >= chrList2.size()) || (chrList1.get(i) != chrList2.get(i) )) {
						score = 1;
					}
					resultListBuilder.addElementToBuild(chromosome, i, i + 1, score);
				}
			}
		}
		return resultListBuilder.getSCWList();
	}


	@Override
	public String getDescription() {
		return "Operation: Compute differences between sequence layers";
	}


	@Override
	public String getProcessingDescription() {
		return "Computing differences between sequence layers";
	}


	@Override
	public int getStepCount() {
		return 1 + SimpleSCWList.getCreationStepCount(SCWListType.DENSE);
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
