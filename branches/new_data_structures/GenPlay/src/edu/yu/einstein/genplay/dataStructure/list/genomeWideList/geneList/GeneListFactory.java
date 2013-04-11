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
package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.IO.dataReader.GeneReader;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.gene.SimpleGene;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.geneListView.GeneListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.ListOfListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;

/**
 * Factory class for vending standard {@link GeneList} objects
 * @author Julien Lajugie
 */
public class GeneListFactory {


	/**
	 * Creates a {@link GeneList} from the data retrieved by the specified {@link GeneReader}.
	 * @param geneReader a {@link GeneReader}
	 * @param scorePrecision precision of the gene and exon scores
	 * @return a {@link GeneList}
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws CloneNotSupportedException
	 */
	public static GeneList createGeneList(GeneReader geneReader, ScorePrecision scorePrecision) throws InterruptedException, ExecutionException, CloneNotSupportedException {
		GeneListViewBuilder lvBuilderPrototype = new GeneListViewBuilder(scorePrecision);
		ListOfListViewBuilder<Gene> builder = new ListOfListViewBuilder<Gene>(lvBuilderPrototype);
		while (geneReader.readItem()) {
			Gene currentGene = new SimpleGene(
					geneReader.getName(),
					geneReader.getStrand(),
					geneReader.getStart(),
					geneReader.getStop(),
					geneReader.getScore(),
					geneReader.getUTR5Bound(),
					geneReader.getUTR3Bound(),
					geneReader.getExons());
			builder.addElementToBuild(geneReader.getChromosome(), currentGene);
		}
		String geneDBURL = geneReader.getGeneDBURL();
		GeneScoreType geneScoreType = geneReader.getGeneScoreType();
		return new SimpleGeneList(builder.getGenomicList(), scorePrecision, geneScoreType, geneDBURL);
	}


	/**
	 * Creates a {@link GeneList} from a {@link SCWList}.
	 * It creates one gene per window with a single exon having the same start, stop and score as the window.
	 * @param scoredChromosomeWindowList
	 * @param scorePrecision precision of the gene and exon scores
	 * @return a {@link GeneList}
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static GeneList createGeneList(SCWList scoredChromosomeWindowList, final ScorePrecision scorePrecision) throws InterruptedException, ExecutionException {
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<ListView<Gene>>> threadList = new ArrayList<Callable<ListView<Gene>>>();
		for (int i = 0; i < scoredChromosomeWindowList.size(); i++) {
			final ListView<ScoredChromosomeWindow> currentList = scoredChromosomeWindowList.get(i);
			final Chromosome chromosome = projectChromosome.get(i);
			final String prefixName = chromosome.getName() + ".";
			Callable<ListView<Gene>> currentThread = new Callable<ListView<Gene>>() {
				@Override
				public ListView<Gene> call() throws Exception {
					ListViewBuilder<Gene> geneListBuilder = new GeneListViewBuilder(scorePrecision);
					for (int j = 0; j < currentList.size(); j++) {
						ScoredChromosomeWindow currentWindow = currentList.get(j);
						String name = prefixName + (j + 1);
						int start = currentWindow.getStart();
						int stop = currentWindow.getStop();
						float score = currentWindow.getScore();
						Strand strand = Strand.FIVE;
						ListViewBuilder<ScoredChromosomeWindow> exonListBuilder = new GenericSCWListViewBuilder(scorePrecision);
						ScoredChromosomeWindow exonToAdd = new SimpleScoredChromosomeWindow(currentWindow.getStart(), currentWindow.getStop(), currentWindow.getScore());
						exonListBuilder.addElementToBuild(exonToAdd);
						Gene geneToAdd = new SimpleGene(name, strand, start, stop, score, exonListBuilder.getListView());
						geneListBuilder.addElementToBuild(geneToAdd);
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return geneListBuilder.getListView();
				}
			};
			threadList.add(currentThread);
		}
		List<ListView<Gene>> result = op.startPool(threadList);
		return new SimpleGeneList(result, scorePrecision, null, null);
	}
}
