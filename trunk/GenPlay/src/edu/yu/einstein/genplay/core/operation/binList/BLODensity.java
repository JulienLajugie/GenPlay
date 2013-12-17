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
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Computes the density of bin with values on a region of halfWidth * 2 + 1 bins
 * @author Julien Lajugie
 */
public class BLODensity implements Operation<BinList> {

	private final BinList 	binList;		// input BinList
	private final int		halfWidth; 		// half size of the region (in number of bin)
	private boolean			stopped = false;// true if the operation must be stopped


	/**
	 * Computes the density of bin with values on a region of halfWidth * 2 + 1 bins
	 * @param binList input {@link BinList}
	 * @param halfWidth half size of the region (in number of bin)
	 */
	public BLODensity(BinList binList, int halfWidth) {
		this.binList = binList;
		this.halfWidth = halfWidth;
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException, InvalidParameterException, CloneNotSupportedException {
		final int binCount = (2 * halfWidth) + 1;

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
						// We compute the density for each bin
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							int noneZeroBinCount = 0;
							for (int k = -halfWidth; (k <= halfWidth) && !stopped; k++) {
								if(((j + k) >= 0) && ((j + k) < currentList.size()))  {
									if (currentList.get(j + k).getScore() != 0) {
										noneZeroBinCount++;
									}
								}
							}
							float score = noneZeroBinCount / (float) binCount;
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
		return (BinList) resultListBuilder.getSCWList();
	}


	@Override
	public String getDescription() {
		return "Operation: Density, Region Size = " + ((halfWidth * 2) + 1) + " Bins";
	}


	@Override
	public String getProcessingDescription() {
		return "Computing Density";
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
