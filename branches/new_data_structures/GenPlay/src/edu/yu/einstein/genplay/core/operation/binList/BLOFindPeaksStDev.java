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

import java.security.InvalidParameterException;
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
import edu.yu.einstein.genplay.util.ListView.SCWListViews;


/**
 * Searches the peaks of a specified {@link BinList}. A bin is considered as a peak when the
 * local stdev centered on this bin is higher than a certain threshold.
 * The threshold is specified in chromosome wide stdev folds.
 * @author Julien Lajugie
 */
public class BLOFindPeaksStDev implements Operation<BinList[]> {

	private final BinList 	binList;		// input BinList
	private int 			halfWidth;		// half size of the moving stdev (in bins)
	private double 			nbSDAccepted;	/* 	threshold: we accept a bin if the local stdev centered
											 	on this point is at least this parameter time higher than
												the chromosome wide stdev 	*/
	private boolean			stopped = false;// true if the operation must be stopped


	/**
	 * Creates an instance of {@link BLOFindPeaksStDev}
	 * @param binList input {@link BinList}

	 */
	public BLOFindPeaksStDev(BinList binList) {
		this.binList = binList;
	}


	@Override
	public BinList[] compute() throws InterruptedException, ExecutionException, InvalidParameterException, CloneNotSupportedException {
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
						// compute the stdev for the chromosome
						double sd = SCWListViews.standardDeviation(currentList, 0, currentList.size() - 1);
						if (sd != 0) {
							// compute the value the local standard deviation must be for a bin to be accepted
							double minAcceptedSD = nbSDAccepted * sd;
							for (int j = 0; (j < currentList.size()) && !stopped; j++) {
								float score = 0;
								if (currentList.get(j).getScore() != 0) {
									int indexStart = j - halfWidth;
									int indexStop = j + halfWidth;
									// if the start index is negative we set it to 0
									if (indexStart < 0) {
										indexStart = 0;
									}
									// if the stop index is out of range we set it to the size of the list
									if (indexStop > (currentList.size() - 1)) {
										indexStop = currentList.size() - 1;
									}
									// we compute the local stdev centered on the current bin, btw start and stop index
									double localStdev = SCWListViews.standardDeviation(currentList, indexStart, indexStop);
									if ((localStdev != 0) && (localStdev >= minAcceptedSD)) {
										// if the local stdev is higher than the threshold we keep the bin
										score = currentList.get(j).getScore();
									}
								}
								// TODO optimize with a bin list builder that doesn't require to create SCW
								int start = currentList.get(j).getStart();
								int stop = currentList.get(j).getStop();
								ScoredChromosomeWindow windowToAdd = new SimpleScoredChromosomeWindow(start, stop, score);
								resultListBuilder.addElementToBuild(chromosome, windowToAdd);
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
		BinList[] returnValue = {(BinList) resultListBuilder.getSCWList()};
		return returnValue;
	}


	@Override
	public String getDescription() {
		return "Operation: Search Peaks, Local Stdev Width = " +  (halfWidth * 2 * binList.getBinSize()) + " bp, Threshold = " + nbSDAccepted + " Stdev";
	}


	@Override
	public String getProcessingDescription() {
		return "Searching Peaks";
	}


	@Override
	public int getStepCount() {
		return binList.getCreationStepCount() + 1;
	}


	/**
	 * Sets the half width
	 * @param halfWidth half size of the moving stdev (in bins)
	 */
	public void setHalfWidth(int halfWidth) {
		this.halfWidth = halfWidth;
	}


	/**
	 * Sets the thresholds
	 * @param nbSDAccepted threshold: we accept a bin if the local stdev centered on this point is at
	 * least this parameter time higher than the chromosome wide stdev
	 */
	public void setThreshold(double nbSDAccepted) {
		this.nbSDAccepted = nbSDAccepted;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
