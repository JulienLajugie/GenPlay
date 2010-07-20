/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList.overLap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.arrayList.IntArrayAsIntegerList;
import yu.einstein.gdp2.core.manager.ChromosomeManager;

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
public class OverLapManagement {
	
	protected final ChromosomeManager 				chromosomeManager;		// ChromosomeManager
	private 		SCWOption 						sortSCW;				//use the sort option for chromosome list
	private 		List<OverLapEngine> 			overLappingEngineList;	//overlapping engine for chromosome list
	private 		ChromosomeListOfLists<Integer> 	originalStartList;		//store the original start list position
	private 		ChromosomeListOfLists<Integer> 	originalStopList;		//store the original stop list position
	private 		ChromosomeListOfLists<Double> 	originalScoreList;		//store the original scores list
	
	/**
	 * OverLapManagement constructor
	 * 
	 * @param startList		list of start position
	 * @param stopList		list of stop position
	 * @param scoreList		list of score
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public OverLapManagement (	ChromosomeListOfLists<Integer> startList,
								ChromosomeListOfLists<Integer> stopList,
								ChromosomeListOfLists<Double> scoreList) throws InterruptedException, ExecutionException {
		this.chromosomeManager = ChromosomeManager.getInstance();
		this.originalStartList = startList;
		this.originalStopList = stopList;
		this.originalScoreList = scoreList;
		this.sortSCW = new SCWOption(this.originalStartList, this.originalStopList, this.originalScoreList);
		/*this.overLappingEngineList = new ArrayList<OverLapEngine>();
		for (int i = 0; i < chromosomeManager.size(); i++) {
			this.overLappingEngineList.add(new OverLapEngine(scm));
		}*/
	}
	

	////////////////////////////////////////////////	OverLapping existing methods
	
	/**
	 * overLappingExist method
	 * Scan the original list to find overlapping region.
	 * 
	 * @return	true is an overlapping region is found
	 */
	public boolean overLappingExist () {
		int index = 0;
		boolean isFound = false;
		for(final Chromosome currentChromosome : chromosomeManager) {
			while (index < this.originalStartList.size(currentChromosome)) {
				isFound = searchOverLappingPositionsForIndex(currentChromosome, index);	//Search for overlapping position on the current index
				if (isFound) {
					return true;
				} else {
					index++;
				}
			}
		}
		return false;
	}
	
	/**
	 * searchOverLappingPositionsForIndex method
	 * This method search if the index is involved on an overlapping region
	 * 
	 * @param currentChromosome	Chromosome
	 * @param index				current index
	 * @return					true if the current index is involved on an overlapping region
	 */
	private boolean searchOverLappingPositionsForIndex (Chromosome currentChromosome, int index) {
		int nextIndex = index + 1;
		boolean valid = true;
		if (nextIndex < this.originalStartList.size(currentChromosome)) {
			while (valid & (this.originalStopList.get(currentChromosome, index) > this.originalStartList.get(currentChromosome, nextIndex))) {
				if (this.originalStopList.get(currentChromosome, index) > this.originalStartList.get(currentChromosome, nextIndex)) {
					return true;
				}
				if ((nextIndex + 1) < this.originalStartList.size(currentChromosome)) {
					nextIndex++;
				} else {
					valid = false;
				}
			}
		}
		return false;
	}
	
	
	////////////////////////////////////////////////	OverLapping existing methods
	
	/**
	 * run method
	 * This method allow to run the overlap engine for a specific chromosome
	 * @param chromosome	Chromosome
	 */
	public void run (Chromosome chromosome) throws InterruptedException, ExecutionException {
		this.sortSCW.sortOne(chromosome);	//the chromosome list is sorter
		this.overLappingEngineList.get(chromosomeManager.getIndex(chromosome)).init(this.sortSCW.getList(chromosome));	//the overlapengine is ran for the chromosome list
	}
	
	
	////////////////////////////////////////////////	GETTERS & SETTERS

	public IntArrayAsIntegerList getNewStartList(Chromosome chromosome) {
		return this.overLappingEngineList.get(chromosomeManager.getIndex(chromosome)).getNewStartList();
	}
	
	public IntArrayAsIntegerList getNewStopList(Chromosome chromosome) {
		return this.overLappingEngineList.get(chromosomeManager.getIndex(chromosome)).getNewStopList();
	}
	
	public List<Double> getNewScoreList(Chromosome chromosome) {
		return this.overLappingEngineList.get(chromosomeManager.getIndex(chromosome)).getNewScoreList();
	}
	
	public void setScoreCalculationMethod (ScoreCalculationMethod scm) {
		this.overLappingEngineList = new ArrayList<OverLapEngine>();
		for (int i = 0; i < chromosomeManager.size(); i++) {
			this.overLappingEngineList.add(new OverLapEngine(scm));
		}
	}
}