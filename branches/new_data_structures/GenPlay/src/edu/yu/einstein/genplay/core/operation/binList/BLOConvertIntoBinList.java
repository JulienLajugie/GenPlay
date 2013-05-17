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
import edu.yu.einstein.genplay.core.pileupFlattener.BinListPileupFlattener;
import edu.yu.einstein.genplay.core.pileupFlattener.GenomeWideFlattener;
import edu.yu.einstein.genplay.core.pileupFlattener.PileupFlattener;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Creates a new BinList with a specified bin size from a {@link SCWList}
 * @author Julien Lajugie
 */
public class BLOConvertIntoBinList implements Operation<BinList> {

	private final SCWList 					inputList;		// input BinList
	private final int 						binSize;		// new bin size
	private final ScoreOperation 			method;			// method for the calculation of the new binlist


	/**
	 * Creates a new BinList with a new bin size
	 * @param inputList input {@link SCWList}
	 * @param binSize new bin size
	 * @param method {@link ScoreOperation} for the calculation of the new BinList
	 */
	public BLOConvertIntoBinList(SCWList inputList, int binSize, ScoreOperation method) {
		this.inputList = inputList;
		this.binSize = binSize;
		this.method = method;
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException, InvalidParameterException, CloneNotSupportedException {
		if ((inputList instanceof BinList) && (binSize == ((BinList) inputList).getBinSize())) {
			return (BinList) inputList;
		}
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		PileupFlattener flattenerPrototype = new BinListPileupFlattener(binSize, method);
		final GenomeWideFlattener gwflFlattener = new GenomeWideFlattener(flattenerPrototype);

		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<ScoredChromosomeWindow> currentList = inputList.get(chromosome);
			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					if (currentList != null) {
						for (ScoredChromosomeWindow currentWindow: currentList) {
							// we add the current window to the flattener and retrieve the list of
							// flattened windows
							gwflFlattener.addWindow(chromosome, currentWindow);
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
		return new BinList(gwflFlattener.getGenomicList(), binSize);
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
