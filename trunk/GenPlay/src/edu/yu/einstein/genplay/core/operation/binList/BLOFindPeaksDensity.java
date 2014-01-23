/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.core.operation.binList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Removes the values above and under specified thresholds
 * @author Julien Lajugie
 */
public class BLOFindPeaksDensity implements Operation<BinList[]> {
	private final BinList 	binList;				// input binlist to filter
	private float 			lowThreshold;			// saturates the values under this threshold
	private float 			highThreshold;			// saturates the values above this threshold
	private double 			density;				// minimum density of windows above and under the thresholds for a region to be selected (percentage btw 0 and 1)
	private int 			halfWidth;				// half size of the region (in number of bins)
	private boolean			stopped = false;		// true if the operation must be stopped


	/**
	 * Creates an instance of {@link BLOFindPeaksDensity}
	 * @param binList {@link BinList} to filter
	 */
	public BLOFindPeaksDensity(BinList binList) {
		this.binList = binList;
	}


	@Override
	public BinList[] compute() throws Exception {
		if (lowThreshold >= highThreshold) {
			throw new IllegalArgumentException("The high threshold must be greater than the low one");
		}
		// if percentage is zero, everything is selected
		if (density == 0) {
			BinList[] returnValue = {binList};
			return returnValue;
		}
		// we calculate the min number of bins above the threshold needed to select a region
		final int minBinCount = (int)Math.ceil(halfWidth * 2 * density);

		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		final BinListBuilder resultListBuilder = new BinListBuilder(binList.getBinSize());

		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<ScoredChromosomeWindow> currentList = binList.get(chromosome);
			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					if (currentList != null) {
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
								// we accept a window if there is nbConsecutiveValues above or under the filter
								int binSelectedCount = 0;
								int k = indexStart;
								while ((binSelectedCount < minBinCount) && (k <= indexStop) && !stopped) {
									// depending on the filter type we accept values above or under the threshold
									if ((currentList.get(k).getScore() > lowThreshold) && (currentList.get(k).getScore() < highThreshold)) {
										binSelectedCount++;
									}
									k++;
								}
								if (binSelectedCount >= minBinCount) {
									score = currentList.get(j).getScore();
								}
							}
							resultListBuilder.addElementToBuild(chromosome, score);
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
		return "Operation: Density Filter, minimum = " + lowThreshold + ", maximum = " + highThreshold
				+ ", density = " + density + ", region size = " + (halfWidth * 2 * binList.getBinSize()) + "bp";
	}


	@Override
	public String getProcessingDescription() {
		return "Filtering";
	}


	@Override
	public int getStepCount() {
		return 1 + binList.getCreationStepCount();
	}


	/**
	 * @param density minimum density of windows above and under the thresholds for a region to be selected (percentage btw 0 and 1)
	 */
	public final void setDensity(double density) {
		this.density = density;
	}


	/**
	 * @param halfWidth half size of the region (in number of bins)
	 */
	public final void setHalfWidth(int halfWidth) {
		this.halfWidth = halfWidth;
	}


	/**
	 * @param highThreshold filters the values above this threshold
	 */
	public final void setHighThreshold(float highThreshold) {
		this.highThreshold = highThreshold;
	}


	/**
	 * @param lowThreshold filters the values under this threshold
	 */
	public final void setLowThreshold(float lowThreshold) {
		this.lowThreshold = lowThreshold;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
