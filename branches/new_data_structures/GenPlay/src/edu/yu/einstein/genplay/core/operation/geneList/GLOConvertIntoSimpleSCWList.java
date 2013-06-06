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
package edu.yu.einstein.genplay.core.operation.geneList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.core.pileupFlattener.GenomeWideFlattener;
import edu.yu.einstein.genplay.core.pileupFlattener.SimpleSCWPileupFlattener;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;

/**
 * Converts a gene list into a {@link SimpleSCWList}.
 * @author Julien Lajugie
 */
public class GLOConvertIntoSimpleSCWList implements Operation<SimpleSCWList> {

	private final GeneList			geneList;		// input list
	private final ScoreOperation 	scoreOperation;	// method used to flatten the pileups
	private boolean					stopped = false;// true if the operation must be stopped


	/**
	 * Creates an instance of {@link GLOConvertIntoSimpleSCWList}
	 * @param geneList list to convert
	 * @param scoreOperation score operation to compute the scores of the windows
	 */
	public GLOConvertIntoSimpleSCWList(GeneList geneList, ScoreOperation scoreOperation) {
		this.geneList = geneList;
		this.scoreOperation = scoreOperation;
	}


	@Override
	public SimpleSCWList compute() throws Exception {
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		SimpleSCWPileupFlattener flattener = new SimpleSCWPileupFlattener(scoreOperation, SCWListType.GENERIC);
		final GenomeWideFlattener gwFlattener = new GenomeWideFlattener(flattener);

		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<? extends ScoredChromosomeWindow> currentList = geneList.get(chromosome);
			Callable<Void> currentThread = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					if (currentList != null) {
						for (int i = 0; (i < currentList.size()) && !stopped; i++) {
							ScoredChromosomeWindow scw = currentList.get(i);
							float score = scw.getScore();
							if (score != 0) {
								int start = scw.getStart();
								int stop = scw.getStop();
								gwFlattener.addWindow(chromosome, start, stop, score);
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
		return (SimpleSCWList) gwFlattener.getSCWList();
	}


	@Override
	public String getDescription() {
		return "Operation: Convert Into SCW List";
	}


	@Override
	public String getProcessingDescription() {
		return "Converting Layer";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
