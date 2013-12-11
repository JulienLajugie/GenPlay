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
package edu.yu.einstein.genplay.core.operation.SCWList.simulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import cern.jet.random.Binomial;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;

/**
 * Randomly resample the data from the S and G1 phase.
 * A percentage of reads can be added to the S phase.
 * @author Julien Lajugie
 */
public class ResampleLayers implements Operation<SCWList[]>{

	private final SCWList 	sList;				// input list with the S phase data
	private final SCWList 	g1List;				// input list with the G1 phase data
	private final double	percentageToAdd;	// percentage of reads to add in the S phase
	private boolean			stopped = false;	// true if the operation must be stopped


	/**
	 * Creates an instance of {@link ResampleLayers}
	 * @param sList input list with the S phase data
	 * @param g1List input list with the G1 phase data
	 * @param percentageToAdd percentage of reads to add in the S phase
	 */
	public ResampleLayers(SCWList sList, SCWList g1List, double percentageToAdd) {
		this.sList = sList;
		this.g1List = g1List;
		this.percentageToAdd = percentageToAdd;
	}


	/**
	 * Computes the resample lists.
	 * @return an array where the first element is the S result {@link SCWList}
	 * and the second element is the G1 {@link SCWList}
	 */
	@Override
	public SCWList[] compute() throws Exception {
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		final SCWListBuilder sListBuilder = new SCWListBuilder(sList);
		final SCWListBuilder g1ListBuilder = new SCWListBuilder(g1List);
		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<ScoredChromosomeWindow> currentSList = sList.get(chromosome);
			final ListView<ScoredChromosomeWindow> currentG1List = g1List.get(chromosome);
			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					for (int j = 0; (j < currentSList.size()) && !stopped; j++) {
						int oldK = (int) currentSList.get(j).getScore();
						int oldN = (int) (currentSList.get(j).getScore() + currentG1List.get(j).getScore());
						int readToAdd = (int) Math.round(oldK * percentageToAdd);
						int newK = oldK + readToAdd;
						int newN = oldN + readToAdd;
						float newS = Binomial.staticNextInt(newN, newK / newN);
						float newG1 = newN - newS;
						sListBuilder.addElementToBuild(chromosome, currentSList.get(j).getStart(), currentSList.get(j).getStop(), newS);
						g1ListBuilder.addElementToBuild(chromosome, currentG1List.get(j).getStart(), currentG1List.get(j).getStop(), newG1);
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return null;
				}
			};

			threadList.add(currentThread);
		}
		op.startPool(threadList);
		SCWList[] result = {sListBuilder.getSCWList(), g1ListBuilder.getSCWList()};
		return result;
	}


	@Override
	public String getDescription() {
		return "Operation: Resample Layers";
	}


	@Override
	public String getProcessingDescription() {
		return "Resampling Layers";
	}


	@Override
	public int getStepCount() {
		return 1 + (SimpleSCWList.getCreationStepCount(SCWListType.GENERIC) * 2);
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
