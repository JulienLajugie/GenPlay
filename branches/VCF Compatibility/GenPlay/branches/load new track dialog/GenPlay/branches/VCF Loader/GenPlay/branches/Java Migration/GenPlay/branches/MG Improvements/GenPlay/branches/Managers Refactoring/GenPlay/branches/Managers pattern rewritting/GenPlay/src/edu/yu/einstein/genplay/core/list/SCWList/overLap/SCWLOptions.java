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
package edu.yu.einstein.genplay.core.list.SCWList.overLap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.arrayList.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.core.manager.ChromosomeManager;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;


/**
 * This class provides some method to sort and generate list of scored chromosome windows indexed by chromosome.
 * 
 * @author Nicolas
 * @version 0.1
 */
final class SCWLOptions implements Serializable {
	
	private static final long serialVersionUID = -2601316105498708787L;
	protected final ChromosomeManager 									chromosomeManager;	//ChromosomeManager
	private 		ChromosomeArrayListOfLists<ScoredChromosomeWindow> 	list;				//list of scored chromosome windows indexed by chromosome
	private 		ChromosomeListOfLists<Integer> 						startList;			//store the original start list position
	private 		ChromosomeListOfLists<Integer> 						stopList;			//store the original stop list position
	private 		ChromosomeListOfLists<Double> 						scoreList;			//store the original score list
	
	/**
	 * SCWOption constructor
	 * 
	 * @param startList	the original start list position
	 * @param stopList	the original stop list position
	 * @param scoreList	the original score list
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	protected SCWLOptions (	final ChromosomeListOfLists<Integer> startList, 
							final ChromosomeListOfLists<Integer> stopList,
							final ChromosomeListOfLists<Double> scoreList) throws InterruptedException, ExecutionException {
		this.chromosomeManager = ChromosomeManager.getInstance();
		this.list = new ChromosomeArrayListOfLists<ScoredChromosomeWindow>();
		this.startList = startList;
		this.stopList = stopList;
		this.scoreList = scoreList;
		for (int i = 0; i < chromosomeManager.size(); i++) {	//initializes the list
			this.list.add(new ArrayList<ScoredChromosomeWindow>());
		}
	}
	
	
	///////////////////////////	Manage all list
	
	/**
	 * sortAll method
	 * This method manage the generating and sorting operation for all chromosomes
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	protected void sortAll () throws InterruptedException, ExecutionException {
		generateAllList();
		sortAllList();
	}
	
	/**
	 * generateList method
	 * This method generate the right list for all chromosomes
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void generateAllList () throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();	
		for(final Chromosome currentChromosome : this.chromosomeManager) {
			Callable<Void> currentThread = new Callable<Void>() {	
				@Override
				public Void call() throws Exception {
					generateList(currentChromosome);
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return null;
				}
			};
			threadList.add(currentThread);
		}
		// starts the pool
		op.startPool(threadList);
	}
	
	/**
	 * sortAllList method
	 * This method sort the right list for all chromosomes
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void sortAllList () throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();	
		for(final Chromosome currentChromosome : this.chromosomeManager) {
			Callable<Void> currentThread = new Callable<Void>() {	
				@Override
				public Void call() throws Exception {
					sortList(currentChromosome);
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return null;
				}
			};
			threadList.add(currentThread);
		}
		// starts the pool
		op.startPool(threadList);
	}
	
	
	///////////////////////////	Manage one list
	
	/**
	 * sortOne method
	 * This method manage the generating and sorting operation of the right list for a specific chromosome
	 * 
	 * @param	chromosome	the specific chromosome
	 */
	protected void sortOne (Chromosome chromosome) throws InterruptedException, ExecutionException {
		generateList(chromosome);
		sortList(chromosome);
	}
	
	/**
	 * generateList method
	 * This method generate the right list for a specific chromosome
	 * 
	 * @param	chromosome	the specific chromosome
	 */
	private void generateList (final Chromosome chromosome) {
		if (startList.get(chromosome) != null) {
			for (int i=0; i < startList.get(chromosome).size(); i++) {
				this.list.add(chromosome, new ScoredChromosomeWindow(	startList.get(chromosome, i),
																		stopList.get(chromosome, i),
																		scoreList.get(chromosome, i)));
			}
		}
	}
	
	/**
	 * sortList method
	 * This method sort the right list for a specific chromosome
	 * 
	 * @param chromosome	the specific chromosome
	 */
	private void sortList (Chromosome chromosome) {
		if (list.get(chromosome) != null) {
			Collections.sort(list.get(chromosome));
		}
	}
	
	
	///////////////////////////	GETTERS & SETTERS
	
	protected void setNewList (Chromosome chromosome, IntArrayAsIntegerList newStartList, IntArrayAsIntegerList newStopList, List<Double> newScoresList) {
		if (newStartList != null & newStopList != null & newScoresList != null) {
			this.list.get(chromosome).clear();
			for (int i=0; i < newStartList.size(); i++) {
				this.list.add(chromosome, new ScoredChromosomeWindow(	newStartList.get(i),
																		newStopList.get(i),
																		newScoresList.get(i)));
			}
		}
	}
	
	protected ChromosomeArrayListOfLists<ScoredChromosomeWindow> getList() {
		return list;
	}
	
	protected List<ScoredChromosomeWindow> getList(Chromosome chromosome) {
		return list.get(chromosome);
	}
	
}
