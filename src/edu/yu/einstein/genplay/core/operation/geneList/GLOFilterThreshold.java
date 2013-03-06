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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.gene.SimpleGene;
import edu.yu.einstein.genplay.dataStructure.list.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.geneList.GeneListFactory;


/**
 * Removes the genes with an overall RPKM above and under specified thresholds
 * @author Julien Lajugie
 * @version 0.1
 */
public class GLOFilterThreshold implements Operation<GeneList> {
	private final GeneList 	geneList;			// input list
	private final double 	lowThreshold;		// filters the genes with an overall RPKM under this threshold
	private final double 	highThreshold;		// filters the genes with an overall RPKM above this threshold
	private final boolean	isSaturation;		// true if we saturate, false if we remove the filtered values
	private boolean			stopped = false;	// true if the operation must be stopped


	/**
	 * Creates an instance of {@link GLOFilterThreshold}
	 * @param geneList input list
	 * @param lowThreshold filters the genes with an overall RPKM under this threshold
	 * @param highThreshold filters the genes with an overall RPKM above this threshold
	 * @param isSaturation true to saturate, false to remove the filtered values
	 */
	public GLOFilterThreshold(GeneList geneList, double	lowThreshold, double highThreshold, boolean isSaturation) {
		this.geneList = geneList;
		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
		this.isSaturation = isSaturation;
	}


	@Override
	public GeneList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Gene>>> threadList = new ArrayList<Callable<List<Gene>>>();

		for(short i = 0; i < geneList.size(); i++) {
			final List<Gene> currentList = geneList.get(i);
			Callable<List<Gene>> currentThread = new Callable<List<Gene>>() {
				@Override
				public List<Gene> call() throws Exception {
					if (currentList == null) {
						return null;
					}
					List<Gene> resultList = new ArrayList<Gene>();
					for (int j = 0; (j < currentList.size()) && !stopped; j++) {
						Gene currentGene = currentList.get(j);
						if ((currentGene.getGeneRPKM() != null)) {
							Gene geneToAdd = null;
							if (currentGene.getGeneRPKM() > highThreshold) {
								// if the score is greater than the high threshold
								if (isSaturation) {
									// set the value to high threshold (saturation)
									geneToAdd = new SimpleGene(currentGene);
									for (int k = 0; k < currentGene.getExonScores().length; k++) {
										geneToAdd.getExonScores()[k] = highThreshold;
									}
								}
							} else if (currentGene.getGeneRPKM() < lowThreshold) {
								// if the score is smaller than the low threshold
								if (isSaturation) {
									// set the value to low threshold (saturation)
									geneToAdd = new SimpleGene(currentGene);
									for (int k = 0; k < currentGene.getExonScores().length; k++) {
										geneToAdd.getExonScores()[k] = lowThreshold;
									}
								}
							} else {
								// if the score is between the two threshold
								geneToAdd = new SimpleGene(currentGene);
							}
							if (geneToAdd != null) {
								resultList.add(geneToAdd);
							}
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};
			threadList.add(currentThread);
		}
		List<List<Gene>> result = op.startPool(threadList);
		if (result == null) {
			return null;
		} else {
			return GeneListFactory.createGeneArrayList(result, geneList.getGeneDBURL(), geneList.getGeneScoreType());
		}
	}


	@Override
	public String getDescription() {
		String optionStr;
		if (isSaturation) {
			optionStr = ", option = saturation";
		} else {
			optionStr = ", option = remove";
		}
		return "Operation: Threshold Filter, minimum = " + lowThreshold + ", maximum = " + highThreshold + optionStr;
	}


	@Override
	public String getProcessingDescription() {
		return "Filtering Genes";
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
