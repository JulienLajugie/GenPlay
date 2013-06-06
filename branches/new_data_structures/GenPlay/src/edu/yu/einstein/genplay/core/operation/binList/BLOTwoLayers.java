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
package edu.yu.einstein.genplay.core.operation.binList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOTwoLayers;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.BinListDifferentWindowSizeException;


/**
 * Adds the scores of the bins of two specified BinLists
 * @author Julien Lajugie
 */
public class BLOTwoLayers implements Operation<SCWList> {

	private final BinList 					binList1;		// first binlist to add
	private final BinList 					binList2; 		// second binlist to add
	private final ScoreOperation 			scm;			// method of calculation for the score
	private boolean							stopped = false;// true if the operation must be stopped


	/**
	 * Adds the scores of the bins of the two specified BinLists
	 * @param binList1
	 * @param binList2
	 * @param scm {@link ScoreOperation} method used to compute the scores
	 */
	public BLOTwoLayers(BinList binList1, BinList binList2, ScoreOperation scm) {
		this.binList1 = binList1;
		this.binList2 = binList2;
		this.scm = scm;
	}


	private float average(float a, float b) {
		return sum(a, b) / 2;
	}


	@Override
	public SCWList compute() throws BinListDifferentWindowSizeException, CloneNotSupportedException, InterruptedException, ExecutionException  {
		// if the two bin lists have different bin sizes we use the generic algorithm for SCWList
		if (binList1.getBinSize() != binList2.getBinSize()) {
			return new SCWLOTwoLayers(binList1, binList2, scm).compute();
		}

		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		final BinListBuilder resultListBuilder = new BinListBuilder(binList1.getBinSize());

		for(final Chromosome currentChromosome : projectChromosomes) {
			final ListView<ScoredChromosomeWindow> currentList1 = binList1.get(currentChromosome);
			final ListView<ScoredChromosomeWindow> currentList2 = binList2.get(currentChromosome);
			final boolean firstLayerIsEmpty = (currentList1 == null) || currentList1.isEmpty();
			final boolean secondLayerIsEmpty = (currentList2 == null) || currentList2.isEmpty();

			Callable<Void> currentThread = new Callable<Void>() {
				@Override
				public Void call() throws Exception {

					if (!firstLayerIsEmpty && !secondLayerIsEmpty) {
						for (int j = 0; (j < currentList1.size()) && !stopped; j++) {
							float score = 0f;
							if (j < currentList2.size()) {
								// we add the bins of the two binlists
								score =  getScore(currentList1.get(j).getScore(), currentList2.get(j).getScore());
							}
							resultListBuilder.addElementToBuild(currentChromosome, score);
						}
					} else {
						ListView<ScoredChromosomeWindow> currentList = null;
						if (!firstLayerIsEmpty & secondLayerIsEmpty) {
							currentList = currentList1;
						} else if (firstLayerIsEmpty & !secondLayerIsEmpty) {
							currentList = currentList2;
						}

						if (currentList != null) {
							if ((scm == ScoreOperation.ADDITION) || (scm == ScoreOperation.MAXIMUM)) {
								for (int j = 0; (j < currentList.size()) && !stopped; j++) {
									resultListBuilder.addElementToBuild(currentChromosome, currentList.get(j));
								}
							} else if (scm == ScoreOperation.SUBTRACTION) {
								for (int j = 0; (j < currentList.size()) && !stopped; j++) {
									if (firstLayerIsEmpty) {
										ScoredChromosomeWindow currentWindow = currentList.get(j);
										resultListBuilder.addElementToBuild(currentChromosome, -currentWindow.getScore());
									} else {
										resultListBuilder.addElementToBuild(currentChromosome, currentList.get(j));
									}
								}
							} else if ((scm == ScoreOperation.MULTIPLICATION) || (scm == ScoreOperation.DIVISION) || (scm == ScoreOperation.MINIMUM)) {
								for (int j = 0; (j < currentList.size()) && !stopped; j++) {
									resultListBuilder.addElementToBuild(currentChromosome, 0f);
								}
							} else if (scm == ScoreOperation.AVERAGE) {
								for (int j = 0; (j < currentList.size()) && !stopped; j++) {
									ScoredChromosomeWindow currentWindow = currentList.get(j);
									resultListBuilder.addElementToBuild(currentChromosome, currentWindow.getScore() / 2);
								}
							}
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
		return resultListBuilder.getSCWList();
	}


	private float division(float a, float b) {
		if ((a != 0f) && (b != 0f)) {
			return a / b;
		} else {
			return 0f;
		}
	}


	@Override
	public String getDescription() {
		return "Operation on two layers: " + scm.toString();
	}


	@Override
	public String getProcessingDescription() {
		return "Two Layers Operation";
	}


	///////////////////////////	Calculation methods

	/**
	 * getScore method
	 * This method manages the calculation of the score according to the score calculation method.
	 * 
	 * @return	the score
	 */
	private float getScore (float a, float b) {
		switch (scm) {
		case ADDITION:
			return sum(a, b);
		case SUBTRACTION:
			return subtraction(a, b);
		case MULTIPLICATION:
			return multiplication(a, b);
		case DIVISION:
			return division(a, b);
		case AVERAGE:
			return average(a, b);
		case MAXIMUM:
			return maximum(a, b);
		case MINIMUM:
			return minimum(a, b);
		default:
			return Float.NaN;
		}
	}


	@Override
	public int getStepCount() {
		return binList1.getCreationStepCount() + 1;
	}


	private float maximum(float a, float b) {
		return Math.max(a, b);
	}


	private float minimum(float a, float b) {
		return Math.min(a, b);
	}


	private float multiplication(float a, float b) {
		return (a * b);
	}


	@Override
	public void stop() {
		stopped = true;
	}


	private float subtraction(float a, float b) {
		return (a - b);
	}


	private float sum(float a, float b) {
		return (a + b);
	}
}
