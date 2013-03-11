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
package edu.yu.einstein.genplay.core.operation.SCWList.overlap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.dataStructure.list.GenomicDataList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Manage the overlapping engine
 * Provides news lists for chromosome:
 * 	- start positions
 * 	- stop positions
 * 	- scores
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class OverlapManagement implements Serializable {

	private static final long serialVersionUID = 419831643761204027L;

	protected final ProjectChromosome 			projectChromosome;		// TChromosomeManager
	private final 	SCWLOptions 				sortSCW;				// use the sort option for chromosome list
	private 		List<OverlapEngine> 	overLappingEngineList;	// overlapping engine for chromosome list

	/**
	 * OverLapManagement constructor
	 * 
	 * @param startList		list of start position
	 * @param stopList		list of stop position
	 * @param scoreList		list of score
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public OverlapManagement (	GenomicDataList<Integer> startList,
			GenomicDataList<Integer> stopList,
			GenomicDataList<Double> scoreList) throws InterruptedException, ExecutionException {
		this.projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		this.sortSCW = new SCWLOptions(startList, stopList, scoreList);
		this.sortSCW.sortAll();
	}


	////////////////////////////////////////////////	OverLapping running methods

	/**
	 * run method
	 * This method allow to run the overlapping engine for a specific chromosome
	 * @param chromosome	Chromosome
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void run (Chromosome chromosome) throws InterruptedException, ExecutionException {
		this.overLappingEngineList.get(projectChromosome.getIndex(chromosome)).init(this.sortSCW.getList().get(chromosome));	//the overlap engine is ran for the chromosome list
		this.sortSCW.setNewList(chromosome, getNewStartList(chromosome), getNewStopList(chromosome), getNewScoreList(chromosome));	//the old chromosome list is replaced by the new one
	}


	////////////////////////////////////////////////	GETTERS & SETTERS

	/**
	 * @param chromosome the chromosome
	 * @return	the sorted score chromosome window list associated to the chromosome
	 */
	public List<ScoredChromosomeWindow> getList(Chromosome chromosome) {
		return this.sortSCW.getList(chromosome);
	}

	private IntArrayAsIntegerList getNewStartList(Chromosome chromosome) {
		return this.overLappingEngineList.get(projectChromosome.getIndex(chromosome)).getNewStartList();
	}

	private IntArrayAsIntegerList getNewStopList(Chromosome chromosome) {
		return this.overLappingEngineList.get(projectChromosome.getIndex(chromosome)).getNewStopList();
	}

	private List<Double> getNewScoreList(Chromosome chromosome) {
		return this.overLappingEngineList.get(projectChromosome.getIndex(chromosome)).getNewScoreList();
	}

	/**
	 * Sets the score calculation method
	 * @param scm the score calculation method
	 */
	public void setScoreCalculationMethod (ScoreCalculationMethod scm) {
		this.overLappingEngineList = new ArrayList<OverlapEngine>();
		for (int i = 0; i < projectChromosome.size(); i++) {
			this.overLappingEngineList.add(new OverlapEngine(scm));
		}
	}
}
