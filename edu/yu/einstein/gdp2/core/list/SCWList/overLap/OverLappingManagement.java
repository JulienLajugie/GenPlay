/**
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList.overLap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.ScoredChromosomeWindow;
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
public class OverLappingManagement implements Serializable {
	
	private static final long serialVersionUID = 419831643761204027L;
	protected final ChromosomeManager 			chromosomeManager;		// ChromosomeManager
	private 		SCWLOptions 				sortSCW;				//use the sort option for chromosome list
	private 		List<OverLappingEngine> 	overLappingEngineList;	//overlapping engine for chromosome list
	
	/**
	 * OverLapManagement constructor
	 * 
	 * @param startList		list of start position
	 * @param stopList		list of stop position
	 * @param scoreList		list of score
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public OverLappingManagement (	ChromosomeListOfLists<Integer> startList,
								ChromosomeListOfLists<Integer> stopList,
								ChromosomeListOfLists<Double> scoreList) throws InterruptedException, ExecutionException {
		this.chromosomeManager = ChromosomeManager.getInstance();
		this.sortSCW = new SCWLOptions(startList, stopList, scoreList);
		this.sortSCW.sortAll();
	}
	
	
	////////////////////////////////////////////////	OverLapping running methods
	
	/**
	 * run method
	 * This method allow to run the overlapping engine for a specific chromosome
	 * @param chromosome	Chromosome
	 */
	public void run (Chromosome chromosome) throws InterruptedException, ExecutionException {
		this.overLappingEngineList.get(chromosomeManager.getIndex(chromosome)).init(this.sortSCW.getList().get(chromosome));	//the overlapengine is ran for the chromosome list
		this.sortSCW.setNewList(chromosome, getNewStartList(chromosome), getNewStopList(chromosome), getNewScoreList(chromosome));	//the old chromosome list is replaced by the new one
	}
	
	
	////////////////////////////////////////////////	GETTERS & SETTERS

	public List<ScoredChromosomeWindow> getList(Chromosome chromosome) {
		return this.sortSCW.getList(chromosome);
	}
	
	private IntArrayAsIntegerList getNewStartList(Chromosome chromosome) {
		return this.overLappingEngineList.get(chromosomeManager.getIndex(chromosome)).getNewStartList();
	}
	
	private IntArrayAsIntegerList getNewStopList(Chromosome chromosome) {
		return this.overLappingEngineList.get(chromosomeManager.getIndex(chromosome)).getNewStopList();
	}
	
	private List<Double> getNewScoreList(Chromosome chromosome) {
		return this.overLappingEngineList.get(chromosomeManager.getIndex(chromosome)).getNewScoreList();
	}
	
	public void setScoreCalculationMethod (ScoreCalculationMethod scm) {
		this.overLappingEngineList = new ArrayList<OverLappingEngine>();
		for (int i = 0; i < chromosomeManager.size(); i++) {
			this.overLappingEngineList.add(new OverLappingEngine(scm));
		}
	}
}