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
package edu.yu.einstein.genplay.core.list.SCWList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.chromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.chromosomeWindow.SimpleScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationTwoLayerMethod;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.SimpleScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.overLap.SCWLTwoTracksManagement;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;



/**
 * Realizes operation on two tracks
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SCWLOTwoTracks implements Operation<ChromosomeListOfLists<?>>, Stoppable {

	private final ScoreCalculationTwoLayerMethod 	scm;
	private final SCWLTwoTracksManagement 		twoTracks;			// manage the operation between two tracks


	/**
	 * Adds a specified constant to the scores of each window of a {@link SimpleScoredChromosomeWindow}
	 * @param list1 1st input list
	 * @param list2 2nd input list
	 * @param scm {@link ScoreCalculationTwoLayerMethod}
	 */
	public SCWLOTwoTracks(	ChromosomeListOfLists<?> list1,
			ChromosomeListOfLists<?> list2,
			ScoreCalculationTwoLayerMethod scm) {
		this.scm = scm;
		twoTracks = new SCWLTwoTracksManagement(list1, list2, scm);
	}


	@Override
	public ScoredChromosomeWindowList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();

		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();

		for(final Chromosome currentChromosome : projectChromosome) {
			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					twoTracks.run(currentChromosome);
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return twoTracks.getList(currentChromosome);
				}
			};
			threadList.add(currentThread);
		}
		List<List<ScoredChromosomeWindow>> result = op.startPool(threadList);
		if (result != null) {
			ScoredChromosomeWindowList resultList = new SimpleScoredChromosomeWindowList(result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation on two tracks: " + scm.toString();
	}


	@Override
	public String getProcessingDescription() {
		return "Two Tracks Operation";
	}


	@Override
	public int getStepCount() {
		return 1 + SimpleScoredChromosomeWindowList.getCreationStepCount();
	}


	@Override
	public void stop() {
		twoTracks.stop();
	}
}
