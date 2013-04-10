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
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.gene.SimpleGene;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.geneListView.GeneListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.SimpleGeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Removes the genes with an overall RPKM above and under specified thresholds
 * @author Julien Lajugie
 * @version 0.1
 */
public class GLOFilterThreshold implements Operation<GeneList> {
	private final GeneList 			geneList;			// input list
	private final float 			lowThreshold;		// filters the genes with an overall RPKM under this threshold
	private final float 			highThreshold;		// filters the genes with an overall RPKM above this threshold
	private final boolean			isSaturation;		// true if we saturate, false if we remove the filtered values
	private final ScorePrecision 	scorePrecision;		// precision of the scores of the result list
	private boolean					stopped = false;	// true if the operation must be stopped


	/**
	 * Creates an instance of {@link GLOFilterThreshold}
	 * @param geneList input list
	 * @param lowThreshold filters the genes with an overall RPKM under this threshold
	 * @param highThreshold filters the genes with an overall RPKM above this threshold
	 * @param isSaturation true to saturate, false to remove the filtered values
	 * @param scorePrecision precision of the scores of the genes of the result list
	 */
	public GLOFilterThreshold(GeneList geneList, float	lowThreshold, float highThreshold, boolean isSaturation, ScorePrecision scorePrecision) {
		this.geneList = geneList;
		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
		this.isSaturation = isSaturation;
		this.scorePrecision = scorePrecision;
	}


	// FIXME finish to update this class

	@Override
	public GeneList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<ListView<Gene>>> threadList = new ArrayList<Callable<ListView<Gene>>>();

		for(short i = 0; i < geneList.size(); i++) {
			final ListView<Gene> currentList = geneList.get(i);
			Callable<ListView<Gene>> currentThread = new Callable<ListView<Gene>>() {
				@Override
				public ListView<Gene> call() throws Exception {
					if (currentList == null) {
						return null;
					}
					ListViewBuilder<Gene> resultLVBuilder = new GeneListViewBuilder(scorePrecision);
					for (int j = 0; (j < currentList.size()) && !stopped; j++) {
						Gene currentGene = currentList.get(j);


						if ((currentGene.getScore() != Float.NaN)) {
							Gene geneToAdd = null;
							if (currentGene.getScore() > highThreshold) {
								// if the score is greater than the high threshold
								if (isSaturation) {
									// set the value to high threshold (saturation)
									ListViewBuilder<ScoredChromosomeWindow> exonsLVBuilder = new GenericSCWListViewBuilder(scorePrecision);
									for (ScoredChromosomeWindow currentExon: currentGene.getExons()) {
										exonsLVBuilder.addElementToBuild(currentExon.getStart(), currentExon.getStop(), highThreshold);
									}
								}
							} else if (currentGene.getScore() < lowThreshold) {
								// if the score is smaller than the low threshold
								if (isSaturation) {
									// set the value to low threshold (saturation)
									for (int k = 0; k < currentGene.getExons().size(); k++) {
										geneToAdd.getExonScores()[k] = lowThreshold;
									}
								}
							} else {
								// if the score is between the two threshold
								geneToAdd = new SimpleGene(currentGene);
							}
							if (geneToAdd != null) {
								resultLVBuilder.addElementToBuild(geneToAdd);
							}
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultLVBuilder.getListView();
				}
			};
			threadList.add(currentThread);
		}
		List<ListView<Gene>> result = op.startPool(threadList);
		if (result == null) {
			return null;
		} else {
			return new SimpleGeneList(result, geneList.getGeneScoreType(), geneList.getGeneDBURL());
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
