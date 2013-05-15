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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.BinListDifferentWindowSizeException;
import edu.yu.einstein.genplay.util.FloatLists;


/**
 * Computes the average, the max or the sum of the {@link BinList} on intervals defined by another BinList
 * @author Julien Lajugie
 */
public class BLOIntervalsScoring implements Operation<BinList> {

	private final BinList 					intervalList;				// BinList defining the intervals
	private final BinList 					valueList;					// BinList defining the values for the calculation
	private final int 						percentageAcceptedValues;	// the calculation is calculated only on the x% greatest values of each interval
	private final ScoreOperation 			method;						// method of calculation
	private final ScorePrecision 			precision;					// precision of the result BinList
	private boolean							stopped = false;			// true if the operation must be stopped


	/**
	 * Creates an instance of {@link BLOIntervalsScoring}
	 * Computes the average, the max or the sum of the {@link BinList} on intervals defined by another BinList
	 * @param intervalList BinList defining the intervals
	 * @param valueList BinList defining the values for the calculation
	 * @param percentageAcceptedValues the calculation is calculated only on the x% greatest values of each interval
	 * @param method method of calculation
	 * @param precision precision of the result BinList
	 */
	public BLOIntervalsScoring(BinList intervalList, BinList valueList, int percentageAcceptedValues, ScoreOperation method, ScorePrecision precision) {
		this.intervalList = intervalList;
		this.valueList = valueList;
		this.percentageAcceptedValues = percentageAcceptedValues;
		this.method = method;
		this.precision = precision;
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException, BinListDifferentWindowSizeException, CloneNotSupportedException {
		// check if the binList defining the intervals and the binList with the values have the same bin size
		if (intervalList.getBinSize() != valueList.getBinSize()) {
			throw new BinListDifferentWindowSizeException();
		}

		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		final SCWListBuilder resultListBuilder = new SCWListBuilder(valueList);

		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<ScoredChromosomeWindow> currentIntervals = intervalList.get(chromosome);
			final ListView<ScoredChromosomeWindow> currentValues = valueList.get(chromosome);
			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					if ((currentIntervals != null) && (currentValues != null)) {
						int j = 0;
						while ((j < currentIntervals.size()) && (j < currentValues.size()) && !stopped) {
							while ((j < currentIntervals.size()) && (j < currentValues.size()) && (currentIntervals.get(j).getScore() == 0) && !stopped) {
								// TODO optimize with a bin list builder that doesn't require to create SCW
								resultListBuilder.addElementToBuild(chromosome, currentIntervals.get(j));
								j++;
							}
							int k = j;
							List<Float> values = new ArrayList<Float>();
							while ((j < currentIntervals.size()) && (j < currentValues.size()) && (currentIntervals.get(j).getScore() != 0) && !stopped) {
								if (currentValues.get(j).getScore() != 0) {
									values.add(currentValues.get(j).getScore());
								}
								j++;
							}
							if (values.size() > 0) {
								Collections.sort(values);
								int indexStart = values.size() - (int)((values.size() * (double)percentageAcceptedValues) / 100d);
								float result = 0;
								switch (method) {
								case AVERAGE:
									result = FloatLists.average(values, indexStart, values.size() - 1);
									break;
								case MAXIMUM:
									List<Float> listTmp = values.subList(indexStart, values.size() - 1);
									if ((listTmp != null) && (listTmp.size() > 0)) {
										result = Collections.max(listTmp);
									}
									break;
								case ADDITION:
									result = FloatLists.sum(values, indexStart, values.size() - 1);
									break;
								default:
									throw new IllegalArgumentException("Invalid score calculation method");
								}

								for (; k <= j; k++) {
									if (k < currentIntervals.size()) {
										// TODO optimize with a bin list builder that doesn't require to create SCW
										int start = currentIntervals.get(j).getStart();
										int stop = currentIntervals.get(j).getStop();
										ScoredChromosomeWindow windowToAdd = new SimpleScoredChromosomeWindow(start, stop, result);
										resultListBuilder.addElementToBuild(chromosome, windowToAdd);
									}
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
		return (BinList) resultListBuilder.getSCWList();
	}


	@Override
	public String getDescription() {
		return "Operation: Calculation on Projection, Accepted Values = " + percentageAcceptedValues + "%, Method = " + method + ", precision = " + precision;
	}


	@Override
	public String getProcessingDescription() {
		return "Computing Calculation on Projection";
	}


	@Override
	public int getStepCount() {
		return intervalList.getCreationStepCount() + 1;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
