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
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;


/**
 * Computes a moving average on the BinList and returns the result in a new BinList.
 * @author Julien Lajugie
 */
public class BLOMovingAverage implements Operation<BinList> {

	private final BinList 	binList;			// input list
	private final int		movingWindowWidth;	// the size of the average window
	private final boolean	fillNullValues; 	// true to fill the null values
	private boolean			stopped = false;	// true if the operation must be stopped


	/**
	 * Creates an instance of {@link BLOMovingAverage}
	 * Computes a moving average on the BinList and returns the result in a new BinList.
	 * @param binList {@link BinList} input binList
	 * @param movingWindowWidth size in bases
	 * @param fillNullValues set to true to fill the null values
	 */
	public BLOMovingAverage(BinList binList, int movingWindowWidth, boolean fillNullValues) {
		this.binList = binList;
		this.movingWindowWidth = movingWindowWidth;
		this.fillNullValues = fillNullValues;
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException, CloneNotSupportedException {
		final int binSize =  binList.getBinSize();
		final int halfWidthBin = movingWindowWidth / 2 / binSize;

		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		final SCWListBuilder resultListBuilder = new SCWListBuilder(binList);

		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<ScoredChromosomeWindow> currentList = binList.get(chromosome);
			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					if (currentList != null) {
						for(int j = 0; (j < currentList.size()) && !stopped; j++) {
							float score = 0f;
							if ((currentList.get(j).getScore() != 0) || (fillNullValues)) {
								double count = 0;
								double SumNormSignalCoef = 0;
								for (int k = -halfWidthBin; (k <= halfWidthBin) && !stopped; k++) {
									if(((j + k) >= 0) && ((j + k) < currentList.size()))  {
										if(currentList.get(j + k).getScore() != 0)  {
											SumNormSignalCoef += currentList.get(j + k).getScore();
											count++;
										}
									}
								}
								if(count != 0) {
									score = (float) (SumNormSignalCoef / count);
								}
							}
							// TODO optimize with a bin list builder that doesn't require to create SCW
							ScoredChromosomeWindow windowToAdd = new SimpleScoredChromosomeWindow(currentList.get(j).getStart(), currentList.get(j).getStop(), score);
							resultListBuilder.addElementToBuild(chromosome, windowToAdd);
						}
					}
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
		return "Operation: Moving Average, Half Width = " + movingWindowWidth + "bp";
	}


	@Override
	public String getProcessingDescription() {
		return "Computing Moving Average";
	}


	@Override
	public int getStepCount() {
		return binList.getCreationStepCount() + 1;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
