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
import java.util.List;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.MaskSCWListFactory;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.MaskChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;



/**
 * Converts any type {@link ScoredChromosomeWindowList} into {@link ScoredChromosomeWindowList} of {@link MaskChromosomeWindow}
 * @author Nicolas Fourel
 * @version 0.1
 */
public class BLOConvertToMask implements Operation<ScoredChromosomeWindowList> {

	private final BinList 		cwList;	// input list
	private boolean				stopped = false;// true if the operation must be stopped


	/**
	 * Constructor of {@link BLOConvertToMask}
	 * @param scwList input mask list
	 */
	public BLOConvertToMask(BinList scwList) {
		cwList = scwList;
	}


	@Override
	public ScoredChromosomeWindowList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();
		final int windowData = cwList.getBinSize();

		for (short i = 0; i < cwList.size(); i++) {
			final List<Double> currentList = cwList.get(i);

			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = new ArrayList<ScoredChromosomeWindow>();

					if ((currentList != null) && (currentList.size() > 0)) {
						ScoredChromosomeWindow lastInsertedWindow = null;
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							double currentScore = currentList.get(j);
							if (currentScore > 0) {
								boolean hasBeenProcessed = false;
								int start = j * windowData;
								int stop = start + windowData;

								if (resultList.size() > 0) {
									lastInsertedWindow = resultList.get(resultList.size() - 1);
									if (start <= lastInsertedWindow.getStop()) {
										lastInsertedWindow.setStop(stop);
										hasBeenProcessed = true;
									}
								}

								if (!hasBeenProcessed) {
									lastInsertedWindow = new MaskChromosomeWindow(start, stop);
									resultList.add(lastInsertedWindow);
								}

								ScoredChromosomeWindow resultWindow = new MaskChromosomeWindow(start, stop);
								resultList.add(resultWindow);
							}
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};

			threadList.add(currentThread);
		}
		List<List<ScoredChromosomeWindow>> result = op.startPool(threadList);
		if (result != null) {
			ScoredChromosomeWindowList resultList = MaskSCWListFactory.createMaskSCWArrayList(result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Convert to Mask";
	}


	@Override
	public String getProcessingDescription() {
		return "Converting track to Mask";
	}


	@Override
	public int getStepCount() {
		return 1 + SimpleSCWList.getCreationStepCount();
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
