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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.core.pileupFlattener.BinListPileupFlattener;
import edu.yu.einstein.genplay.core.pileupFlattener.PileupFlattener;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.bin.BinListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.ListOfListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Creates a new BinList with a new bin size
 * @author Julien Lajugie
 */
public class BLOChangeBinSize implements Operation<BinList> {

	private final BinList 					binList;		// input BinList
	private final int 						binSize;		// new bin size
	private final ScoreOperation 			method;			// method for the calculation of the new binlist


	/**
	 * Creates a new BinList with a new bin size
	 * @param binList input BinList
	 * @param binSize new bin size
	 * @param method {@link ScoreOperation} for the calculation of the new BinList
	 */
	public BLOChangeBinSize(BinList binList, int binSize, ScoreOperation method) {
		this.binList = binList;
		this.binSize = binSize;
		this.method = method;
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException, InvalidParameterException, CloneNotSupportedException {
		if (binSize == binList.getBinSize()) {
			return binList;
		}
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		ListViewBuilder<ScoredChromosomeWindow> lvbPrototype = new BinListViewBuilder(binSize);
		final ListOfListViewBuilder<ScoredChromosomeWindow> resultListBuilder = new ListOfListViewBuilder<ScoredChromosomeWindow>(lvbPrototype);

		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<ScoredChromosomeWindow> currentList = binList.get(chromosome);
			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					if (currentList != null) {
						// TODO optimize with a bin list builder that doesn't require to create SCW
						PileupFlattener flattener = new BinListPileupFlattener(binSize, method);
						for (ScoredChromosomeWindow currentWindow: currentList) {
							// we add the current window to the flattener and retrieve the list of
							// flattened windows
							List<ScoredChromosomeWindow> flattenedWindows = flattener.addWindow(currentWindow);
							for (ScoredChromosomeWindow scw: flattenedWindows) {
								resultListBuilder.addElementToBuild(chromosome, scw);
							}
						}
						// at the end of a chromosome we flush the flattener and
						// retrieve the remaining of flattened windows
						List<ScoredChromosomeWindow> flattenedWindows = flattener.flush();
						for (ScoredChromosomeWindow scw: flattenedWindows) {
							resultListBuilder.addElementToBuild(chromosome, scw);
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
		return new BinList(resultListBuilder.getGenomicList(), binSize);
	}


	@Override
	public String getDescription() {
		return "Bin Size Changes to " + binSize + "bp, Method of Calculation = " + method;
	}


	@Override
	public String getProcessingDescription() {
		return "Changing Window Size";
	}


	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(SCWListType.BIN);
	}


	/**
	 * Does nothing
	 */
	@Override
	public void stop() {}
}
