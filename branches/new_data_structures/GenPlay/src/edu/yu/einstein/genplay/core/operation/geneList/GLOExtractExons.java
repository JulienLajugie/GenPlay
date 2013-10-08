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
package edu.yu.einstein.genplay.core.operation.geneList;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.gene.SimpleGene;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.SCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.geneListView.GeneListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.ListOfListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.SimpleGeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Extracts exons of each gene of a {@link GeneList}
 * @author Julien Lajugie
 */
public class GLOExtractExons implements Operation<GeneList> {

	/** Extract the first exon */
	public static final int FIRST_EXON = 0;

	/** Extract the last exon */
	public static final int LAST_EXON = 1;

	/** Extract all the exons */
	public static final int ALL_EXONS = 2;

	private final GeneList 			geneList;			// input list
	private final int 				exonOption;			// exon option: first, last or all
	private boolean					stopped = false;	// true if the operation must be stopped


	/**
	 * Creates an instance of {@link GLOExtractExons}
	 * @param geneList input list
	 * @param exonOption used to specify to extract only the first exon, the last exon or all the exon
	 */
	public GLOExtractExons(GeneList geneList, int exonOption) {
		if ((exonOption != FIRST_EXON) && (exonOption != LAST_EXON) && (exonOption != ALL_EXONS)) {
			throw new InvalidParameterException("The exons to extract option is not valid.");
		}
		this.geneList = geneList;
		this.exonOption = exonOption;
	}


	@Override
	public GeneList compute() throws Exception {
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		ListViewBuilder<Gene> lvbPrototype = new GeneListViewBuilder();
		final ListOfListViewBuilder<Gene> resultListBuilder = new ListOfListViewBuilder<Gene>(lvbPrototype);

		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<Gene> currentList = geneList.get(chromosome);
			Callable<Void> currentThread = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					if (currentList != null) {
						List<Gene> geneListTmp = new ArrayList<Gene>();
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							Gene currentGene = currentList.get(j);
							List<Gene> extractedExons = extractExons(currentGene, exonOption);
							for (Gene extractedExon: extractedExons) {
								geneListTmp.add(extractedExon);
							}
						}
						Collections.sort(geneListTmp);
						for (Gene currentGene: geneListTmp) {
							resultListBuilder.addElementToBuild(chromosome, currentGene);
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return null;
				}
			};
			threadList.add(currentThread);
		}
		op.startPool(threadList);
		List<ListView<Gene>> data = resultListBuilder.getGenomicList();
		return new SimpleGeneList(data, geneList.getGeneScoreType(), geneList.getGeneDBURL());
	}


	/**
	 * Convert the specified exon into a gene
	 * @param gene the gene of the exon to convert
	 * @param exonsToExtract exon that needs to be converted into a gene
	 * @param exonName name of the exon
	 * @return a new {@link Gene}
	 */
	private Gene convertExonIntoGene(Gene gene, ScoredChromosomeWindow exonToConvert, String exonName) {
		int start = exonToConvert.getStart();
		int stop = exonToConvert.getStop();
		float score = exonToConvert.getScore();
		SCWListViewBuilder exonLvBuilder = new GenericSCWListViewBuilder();
		exonLvBuilder.addElementToBuild(start, stop, score);
		Strand strand = gene.getStrand();
		return new SimpleGene(exonName, strand, start, stop, score, exonLvBuilder.getListView());
	}


	/**
	 *
	 * @param gene
	 * @param exonOption
	 * @return
	 */
	private List<Gene> extractExons(Gene gene, int exonOption) {
		List<Gene> extractedExons = new ArrayList<Gene>();
		if (gene.getExons() != null) {
			ScoredChromosomeWindow exonToExtract = null;
			String exonName = gene.getName();
			// find the index of the exon to extract in the list of exons
			// depending on the strand of the gene
			if (((exonOption == FIRST_EXON) && (gene.getStrand() == Strand.FIVE))
					|| ((exonOption == LAST_EXON) && (gene.getStrand() == Strand.THREE))) {
				exonToExtract = gene.getExons().get(0);
			} else if (((exonOption == FIRST_EXON) && (gene.getStrand() == Strand.THREE))
					|| ((exonOption == LAST_EXON) && (gene.getStrand() == Strand.FIVE))) {
				exonToExtract = gene.getExons().get(gene.getExons().size() - 1);
			}
			if (exonToExtract != null) {
				// case where only one exon (first or last one) needs to be extracted
				if (exonOption == FIRST_EXON) {
					exonName += " (1st Exon)";
				} else {
					exonName += " (Last Exon)";
				}
				extractedExons.add(convertExonIntoGene(gene, exonToExtract, exonName));
			} else {
				// case where we need to extract all exon
				for (int i = 0; i < gene.getExons().size(); i++) {

					extractedExons.add(convertExonIntoGene(gene, gene.getExons().get(i), exonName + " (" + (i + 1) + ")"));
				}
			}
		}
		return extractedExons;
	}


	@Override
	public String getDescription() {
		switch (exonOption) {
		case 0:
			return "Operation: Extract First Exons";
		case 1:
			return "Operation: Extract Last Exons";
		case 2:
			return "Operation: Extract All Exons";
		default:
			return "Operation: Extract Exons";
		}
	}


	@Override
	public String getProcessingDescription() {
		return "Extracting Exons";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
