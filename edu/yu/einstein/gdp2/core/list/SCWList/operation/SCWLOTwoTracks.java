/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.enums.ScoreCalculationTwoTrackMethod;
import yu.einstein.gdp2.core.list.DisplayableListOfLists;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.overLap.SCWLTwoTracksManagement;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;


/**
 * Realizes operation on two tracks
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SCWLOTwoTracks implements Operation<DisplayableListOfLists<?, ?>> {

	private final DisplayableListOfLists<?, ?> 		list1;	// input list
	private final DisplayableListOfLists<?, ?> 		list2;	// input list
	private final ScoreCalculationTwoTrackMethod 	scm;
	
	
	/**
	 * Adds a specified constant to the scores of each window of a {@link ScoredChromosomeWindow}
	 * @param scwList input list
	 * @param constant constant to add
	 */
	public SCWLOTwoTracks(	DisplayableListOfLists<?, ?> list1,
							DisplayableListOfLists<?, ?> list2,
							ScoreCalculationTwoTrackMethod scm) {
		this.list1 = list1;
		this.list2 = list2;
		this.scm = scm;
	}
	
	
	@Override
	public ScoredChromosomeWindowList compute() throws Exception {
		/*if (this.scwList1 == null & this.scwList2 != null) {
			return this.scwList2;
		} else if (this.scwList1 != null & this.scwList2 == null) {
			return this.scwList1;
		} else if (this.scwList1 == null & this.scwList2 == null) {
			return null;
		}*/
		
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();
		
		ChromosomeManager chromosomeManager = ChromosomeManager.getInstance();
		final SCWLTwoTracksManagement twoTracks = new SCWLTwoTracksManagement(list1, list2, scm);
		
		for(final Chromosome currentChromosome : chromosomeManager) {
			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {	
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					twoTracks.run(currentChromosome);
					//System.out.println("Chromosome : " + currentChromosome.getName() + "\nList:\n" + twoTracks.showList());
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return twoTracks.getList(currentChromosome);
				}
			};
			threadList.add(currentThread);
		}
		List<List<ScoredChromosomeWindow>> result = op.startPool(threadList);
		if (result != null) {
			ScoredChromosomeWindowList resultList = new ScoredChromosomeWindowList(result);
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
		return 1 + ScoredChromosomeWindowList.getCreationStepCount();
	}
}
