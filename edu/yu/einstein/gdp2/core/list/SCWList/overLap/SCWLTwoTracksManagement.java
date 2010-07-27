/**
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList.overLap;

import java.util.ArrayList;
import java.util.List;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.enums.ScoreCalculationTwoTrackMethod;
import yu.einstein.gdp2.core.list.DisplayableListOfLists;
import yu.einstein.gdp2.core.manager.ChromosomeManager;

/**
 * This class manages theses operations on two tracks:
 * 	- addition
 * 	- subtraction
 * 	- multiplication
 * 	- division 
 * 
 * @author Nicolas
 * @version 0.1
 */
public class SCWLTwoTracksManagement {

	protected 	final 	ChromosomeManager 					chromosomeManager;	//ChromosomeManager
	private 	final 	List<DisplayableListOfLists<?, ?>> 	scwList;			//list containing originals lists
	private 			List<SCWLTwoTracksEngine>			twoTracksEngineList;
	
	/**
	 * SCWLTwoTracks constructor
	 * 
	 * @param list1	first track
	 * @param list2	second track
	 * @param scm		operation
	 */
	public SCWLTwoTracksManagement (	DisplayableListOfLists<?, ?> list1,
										DisplayableListOfLists<?, ?> list2,
										ScoreCalculationTwoTrackMethod scm) {
		this.chromosomeManager = ChromosomeManager.getInstance();
		this.scwList = new ArrayList<DisplayableListOfLists<?, ?>>();
		this.scwList.add(list1);
		this.scwList.add(list2);
		this.twoTracksEngineList = new ArrayList<SCWLTwoTracksEngine>();
		for (int i = 0; i < chromosomeManager.size(); i++) {
			this.twoTracksEngineList.add(new SCWLTwoTracksEngine(scm));
		}
	}
	
	public void run(Chromosome chromosome) {
		this.twoTracksEngineList.get(chromosomeManager.getIndex(chromosome)).init(scwList.get(0), scwList.get(1), chromosome);
	}

	public List<ScoredChromosomeWindow> getList(Chromosome chromosome) {
		return this.twoTracksEngineList.get(chromosomeManager.getIndex(chromosome)).getList();
	}
	
	/**/
	
	
	
}