/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList.overLap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.list.ChromosomeArrayListOfLists;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.operationPool.OperationPool;

/**
 * This class provides some method to sort and generate list of scored chromosome windows indexed by chromosome.
 * 
 * @author Nicolas
 * @version 0.1
 */
final class SCWOption {

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
	protected SCWOption (	final ChromosomeListOfLists<Integer> startList, 
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
	
	
	///////////////////////////	Sort list methods
	
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
	private void generateList (final Chromosome chromosome) throws InterruptedException, ExecutionException {
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
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void sortList (Chromosome chromosome) throws InterruptedException, ExecutionException {
		if (list.get(chromosome) != null) {
			Collections.sort(list.get(chromosome));
		}
	}
	
	
	///////////////////////////	GETTERS
	protected ChromosomeArrayListOfLists<ScoredChromosomeWindow> getList() {
		return list;
	}
	
	protected List<ScoredChromosomeWindow> getList(Chromosome chromosome) {
		return list.get(chromosome);
	}
	
	
	///////////////////////////	Unused methods
	//They are interesting to manage all list at the same time
	//They use the multi-threading
	
	protected void sortAll () throws InterruptedException, ExecutionException {
		generateAllList();
		sortAllList();
	}
	
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
	
}