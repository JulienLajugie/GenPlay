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
package edu.yu.einstein.genplay.core.SNPList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.Gene;
import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.SNP;
import edu.yu.einstein.genplay.core.SNPList.SNPList;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;



/**
 * Removes the SNPs from a {@link SNPList} that are not in the genes 
 * or in the exons of the genes of a specified {@link GeneList} 
 * @author Julien Lajugie
 * @version 0.1
 */
public class SLORemoveSNPsNotInGenes implements Operation<SNPList> {

	private final SNPList 	snpList;			// input SNP list
	private final GeneList 	geneList;			// input gene list
	private final int 		removalType;		// type of removal (SNPs not in genes or not in exons) 
	private boolean			stopped = false;	// true if the operation must be stopped

	/**
	 * Select this option to remove the SNPs that are not in the genes of the specified {@link GeneList}
	 */
	public static final int REMOVE_SNPs_NOT_IN_GENES = 0;
	/**
	 * Select this option to remove the SNPs that are not in the exons of the genes of the specified {@link GeneList}
	 */
	public static final int REMOVE_SNPs_NOT_IN_EXONS = 1;


	/**
	 * Creates an instance of {@link SLORemoveSNPsNotInGenes}
	 * @param snpList input SNP list
	 * @param geneList input gene list
	 * @param removalType type of removal (SNPs not in genes or not in exons) 
	 */
	public SLORemoveSNPsNotInGenes(SNPList snpList, GeneList geneList, int removalType) {
		this.snpList = snpList;
		this.geneList = geneList;
		this.removalType = removalType;
	}


	@Override
	public SNPList compute() throws Exception {
		//final Map<SNP, Boolean> snpMap = createHashMap();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<SNP>>> threadList = new ArrayList<Callable<List<SNP>>>();
		for (final Chromosome currentChromosome: ProjectManager.getInstance().getProjectChromosome()) {
			Callable<List<SNP>> currentThread = new Callable<List<SNP>>() {			
				@Override
				public List<SNP> call() throws Exception {
					// retrieve the SNPs of the current chromosome
					List<SNP> currentSNPList = snpList.get(currentChromosome);
					// retrieve the genes of the current chromosome 
					List<Gene> currentGeneList = geneList.get(currentChromosome);
					List<SNP> resultList = null;
					// Hashset to store the selected SNP (ie the ones in Genes or Exons)
					Set<SNP> resultSet = new HashSet<SNP>(); 
					if ((currentSNPList != null) && (!currentSNPList.isEmpty()) 
							&& (currentGeneList != null) && (!currentGeneList.isEmpty())) {
						for (int i = 0; (i < currentGeneList.size()) && !stopped; i++) {
							Gene currentGene = currentGeneList.get(i);
							List<SNP> selectedSNPs = null;
							if (removalType == REMOVE_SNPs_NOT_IN_EXONS) {
								// case where we remove the SNPs that are not in the exons of the genes of the genelist
								if (currentGene.getExonStarts() != null) {
									for (int j = 0; (j < currentGene.getExonStarts().length) && !stopped; j++) {
										GenomeWindow genomeWindow = new GenomeWindow(currentChromosome, currentGene.getExonStarts()[j], currentGene.getExonStops()[j]);
										selectedSNPs = snpList.get(genomeWindow);
									}
								}
							} else if (removalType == REMOVE_SNPs_NOT_IN_GENES) {
								// case where we remove the SNPs that are not in the genes of the genelist
								GenomeWindow genomeWindow = new GenomeWindow(currentChromosome, currentGene.getStart(), currentGene.getStop());
								selectedSNPs = snpList.get(genomeWindow);								
							} else {
								throw new IllegalArgumentException("Invalid removal type");
							}
							// add the selected SNPs to the list of accepted SNPs
							if ((selectedSNPs != null) && (!selectedSNPs.isEmpty())) {
								for (SNP currentSNP: selectedSNPs) {
									resultSet.add(currentSNP);
								}
							}
						}
						// add the selected SNPs to the result list
						resultList = new ArrayList<SNP>();
						for (SNP currentSNP: resultSet) {
							resultList.add(currentSNP);
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};

			threadList.add(currentThread);
		}
		List<List<SNP>> result = op.startPool(threadList);
		if (result != null) {
			SNPList resultList = new SNPList(result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		if (removalType == REMOVE_SNPs_NOT_IN_EXONS) {
			return "Operation: Remove SNPs that are not in the exons of the genes of " + geneList;
		} else if (removalType == REMOVE_SNPs_NOT_IN_GENES) {
			return "Operation: Remove SNPs that are not in the genes of " + geneList;
		} else {
			return null;
		}
	}

	
	@Override
	public String getProcessingDescription() { 
		return "Filtering SNPs";
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
