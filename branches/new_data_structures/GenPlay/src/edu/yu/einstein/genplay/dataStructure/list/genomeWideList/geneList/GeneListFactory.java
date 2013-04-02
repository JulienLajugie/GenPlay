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

import edu.yu.einstein.genplay.core.IO.reader.GeneReader;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.gene.SimpleGene;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicDataArrayList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicDataList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;

/**
 * Factory class for vending standard {@link GeneList} objects
 * @author Julien Lajugie
 */
public class GeneListFactory {


	/**
	 * Creates a {@link GeneList} using {@link ArrayList} structures from the data retrieved by the specified {@link GeneReader}.
	 * @param geneReader a {@link GeneReader}
	 * @return a {@link GeneList}
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public static GeneList createGeneList(GeneReader geneReader) throws InterruptedException, ExecutionException {
		GenomicDataList<Gene> geneList = new GenomicDataArrayList<Gene>();
		Gene currentGene = null;
		while ((currentGene = geneReader.readGene()) != null) {
			geneList.add(geneReader.getCurrentChromosome(), currentGene);
		}
		String geneDBURL = geneReader.getGeneDBURL();
		GeneScoreType geneScoreType = geneReader.getGeneScoreType();
		return new SimpleGeneList(geneList, geneScoreType, geneDBURL);
	}


	/**
	 * Creates a {@link GeneList} from a {@link ScoredChromosomeWindowList}. It creates one gene per window with a single exon having the same score as the window.
	 * @param scoredChromosomeWindowList
	 * @return a {@link GeneList}
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static GeneList createGeneList(ScoredChromosomeWindowList scoredChromosomeWindowList) throws InterruptedException, ExecutionException {
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Gene>>> threadList = new ArrayList<Callable<List<Gene>>>();
		for (int i = 0; i < scoredChromosomeWindowList.size(); i++) {
			final ListView<ScoredChromosomeWindow> currentList = scoredChromosomeWindowList.getView(i);
			final Chromosome chromosome = projectChromosome.get(i);
			final String prefixName = chromosome.getName() + ".";
			Callable<List<Gene>> currentThread = new Callable<List<Gene>>() {
				@Override
				public List<Gene> call() throws Exception {
					List<Gene> currentGeneList = new ArrayList<Gene>();
					for (int j = 0; j < currentList.size(); j++) {
						ScoredChromosomeWindow currentWindow = currentList.get(j);
						String name = prefixName + (j + 1);
						int start = currentWindow.getStart();
						int stop = currentWindow.getStop();
						double score = currentWindow.getScore();
						Strand strand = Strand.FIVE;
						int[] exonStarts = new int[1];
						int[] exonStops = new int[1];
						double[] exonScores = new double[1];
						exonStarts[0] = currentWindow.getStart();
						exonStops[0] = currentWindow.getStop();
						exonScores[0] = currentWindow.getScore();
						Gene geneToAdd = new SimpleGene(name, chromosome, strand, start, stop, score, exonStarts, exonStops, exonScores);
						currentGeneList.add(geneToAdd);
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return null;
				}
			};
			threadList.add(currentThread);
		}
		List<List<Gene>> result = op.startPool(threadList);
		return new SimpleGeneList(result, null, null);
	}
}
