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

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
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
 * Applies a Loess regression on the BinList and returns the result in a new BinList.
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOLoessRegression implements Operation<BinList> {

	private final BinList 	binList;			// input list
	private final int 		movingWindowWidth;				// size of the moving window in bp
	private final boolean	fillNullValues; 	// true to fill the null values
	private boolean			stopped = false;	// true if the operation must be stopped


	/**
	 * Creates an instance of {@link BLOLoessRegression}
	 * Applies a Loess regression on the BinList and returns the result in a new BinList.
	 * @param binList input {@link BinList}
	 * @param movingWindowWidth size of the moving window in bp
	 * @param fillNullValues set to true to fill the null values
	 */
	public BLOLoessRegression(BinList binList, int movingWindowWidth, boolean fillNullValues) {
		this.binList = binList;
		this.movingWindowWidth = movingWindowWidth;
		this.fillNullValues = fillNullValues;
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		final int binSize =  binList.getBinSize();
		final int halfWidth = movingWindowWidth / 2 / binSize;
		// we create an array of coefficients. The index correspond to a distance and for each distance we calculate a coefficient
		final double[] weights = new double[halfWidth + 1];
		for(int i = 0; i <= halfWidth; i++) {
			weights[i] = Math.pow(1d - Math.pow(i / (double) halfWidth,  3d), 3d);
		}

		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		final SCWListBuilder resultListBuilder = new SCWListBuilder(binList);

		for (final Chromosome chromosome: projectChromosome) {
			final ListView<ScoredChromosomeWindow> currentList = binList.get(chromosome);
			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					if (currentList != null) {
						for(int j = 0; (j < currentList.size()) && !stopped; j++) {
							float score = 0f;
							if ((currentList.get(j).getScore() != 0) || (fillNullValues)) {
								// apply the array of coefficients centered on the current value to gauss
								double sumWts = 0;
								double sumWtX = 0;
								double sumWtX2 = 0;
								double sumWtY = 0;
								double sumWtXY = 0;
								for (int k = -halfWidth; (k <= halfWidth) && !stopped; k++) {
									int movingX = j + k; // x coordinate of the current point in the moving window
									if((movingX >= 0) && (movingX < currentList.size()))  {
										int distance = Math.abs(k);
										if(currentList.get(j + k).getScore() != 0)  {
											sumWts += weights[distance];
											sumWtX += movingX * weights[distance];
											sumWtX2 += (movingX ^ 2) * weights[distance];
											sumWtY += currentList.get(movingX).getScore() * weights[distance];
											sumWtXY += movingX * currentList.get(movingX).getScore() * weights[distance];
										}
									}
								}
								double denom = (sumWts * sumWtX2) - Math.pow(sumWtX, 2);
								if(denom != 0) {
									double WLRSlope = ((sumWts * sumWtXY) - (sumWtX * sumWtY)) / denom;
									double WLRIntercept = ((sumWtX2 * sumWtY) - (sumWtX * sumWtXY)) / denom;
									double yLoess = (WLRSlope * j) + WLRIntercept;
									score = (float) yLoess;
								}
								ScoredChromosomeWindow windowToAdd = new SimpleScoredChromosomeWindow(currentList.get(j).getStart(), currentList.get(j).getStop(), score);
								resultListBuilder.addElementToBuild(chromosome, windowToAdd);
							}
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
		return "Operation: Loess Regression, half moving window size = " + movingWindowWidth + "bp";
	}


	@Override
	public String getProcessingDescription() {
		return "Computing Loess Regression";
	}


	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
