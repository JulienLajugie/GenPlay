/**
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.geneList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import yu.einstein.gdp2.core.Gene;
import yu.einstein.gdp2.core.enums.Strand;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;


/**
 * Extracts intervals relative to gene positions
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
public class GLOExtractIntervals implements Operation<GeneList> {

	private final GeneList 	geneList;			// input list
	private final int 		startDistance;		// distance from the start reference
	private final int 		startFrom;			// start reference (see constants below)
	private final int 		stopDistance;		// distant from the stop reference
	private final int 		stopFrom;			// stop reference
	private boolean			stopped = false;	// true if the operation must be stopped


	/**
	 * before the start position (used for interval extraction)
	 */
	public static final int BEFORE_START = 0;


	/**
	 * after the start position (used for interval extraction)
	 */
	public static final int AFTER_START = 1;


	/**
	 * before the middle position (used for interval extraction)
	 */
	public static final int BEFORE_MIDDLE = 2;


	/**
	 * after the middle position (used for interval extraction)
	 */
	public static final int AFTER_MIDDLE = 3;


	/**
	 * before the stop position (used for interval extraction)
	 */
	public static final int BEFORE_STOP = 4;


	/**
	 * after the stop position (used for interval extraction)
	 */
	public static final int AFTER_STOP = 5;


	/**
	 * Extracts intervals relative to gene positions
	 * @param geneList input list
	 * @param startDistance distance from the start reference
	 * @param startFrom start reference (see constants below) 
	 * @param stopDistance distant from the stop reference
	 * @param stopFrom stop reference
	 */
	public GLOExtractIntervals(GeneList geneList, int startDistance, int startFrom, int stopDistance, int stopFrom) {
		this.geneList = geneList;
		this.startDistance = startDistance;
		this.startFrom = startFrom;
		this.stopDistance = stopDistance;
		this.stopFrom = stopFrom;
	}

	
	@Override
	public GeneList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Gene>>> threadList = new ArrayList<Callable<List<Gene>>>();

		for(short i = 0; i < geneList.size(); i++) {
			final List<Gene> currentList = geneList.get(i);
			final int chromoLength = ChromosomeManager.getInstance().get(i).getLength();

			Callable<List<Gene>> currentThread = new Callable<List<Gene>>() {
				@Override
				public List<Gene> call() throws Exception {					
					if (currentList == null) {
						return null;
					}

					List<Gene> resultList = new ArrayList<Gene>();
					for (int j = 0; j < currentList.size() && !stopped; j++) {
						Gene currentGene = currentList.get(j); 
						Gene geneToAdd = new Gene(currentList.get(j));
						// search the new start
						int newStart = 0;
						switch (startFrom) {
						case BEFORE_START:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStart = Math.max(0, currentGene.getTxStart() - startDistance);
							} else {
								newStart = Math.min(chromoLength, currentGene.getTxStop() + startDistance);
							}
							break;
						case AFTER_START:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStart = Math.min(chromoLength, currentGene.getTxStart() + startDistance);
							} else {
								newStart = Math.max(0, currentGene.getTxStop() - startDistance);
							}
							break;
						case BEFORE_MIDDLE:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStart = Math.max(0, (currentGene.getTxStop() + currentGene.getTxStart())/2 - startDistance);
							} else {
								newStart = Math.min(chromoLength, (currentGene.getTxStart() + currentGene.getTxStop())/2 + startDistance);
							}
							break;
						case AFTER_MIDDLE:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStart = Math.min(chromoLength, (currentGene.getTxStop() + currentGene.getTxStart())/2 + startDistance);
							} else {
								newStart = Math.max(0, (currentGene.getTxStart() + currentGene.getTxStop())/2 - startDistance);
							}
							break;
						case BEFORE_STOP:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStart = Math.max(0, currentGene.getTxStop() - startDistance);
							} else {
								newStart = Math.min(chromoLength, currentGene.getTxStart() + startDistance);
							}
							break;
						case AFTER_STOP:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStart = Math.min(chromoLength, currentGene.getTxStop() + startDistance);
							} else {
								newStart = Math.max(0, currentGene.getTxStart() - startDistance);
							}
							break;
						default:
							// invalid argument
							throw new IllegalArgumentException("Invalid Start Reference");
						}
						// search the new stop
						int newStop = 0;
						switch (stopFrom) {
						case BEFORE_START:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStop = Math.max(0, currentGene.getTxStart() - stopDistance);
							} else {
								newStop =  Math.min(chromoLength, currentGene.getTxStop() + stopDistance);
							}
							break;
						case AFTER_START:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStop = Math.min(chromoLength, currentGene.getTxStart() + stopDistance);
							} else {
								newStop = Math.max(0, currentGene.getTxStop() - stopDistance);
							}
							break;
						case BEFORE_MIDDLE:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStop = Math.max(0, (currentGene.getTxStop() + currentGene.getTxStart())/2 - stopDistance);
							} else {
								newStop =  Math.min(chromoLength, (currentGene.getTxStart() + currentGene.getTxStop())/2 + stopDistance);
							}
							break;
						case AFTER_MIDDLE:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStop = Math.min(chromoLength, (currentGene.getTxStop() + currentGene.getTxStart())/2 + stopDistance);
							} else {
								newStop = Math.max(0, (currentGene.getTxStart() + currentGene.getTxStop())/2 - stopDistance);
							}
							break;
						case BEFORE_STOP:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStop = Math.max(0, currentGene.getTxStop() - stopDistance);
							} else {
								newStop =  Math.min(chromoLength, currentGene.getTxStart() + stopDistance);
							}
							break;
						case AFTER_STOP:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStop = Math.min(chromoLength, currentGene.getTxStop() + stopDistance);
							} else {
								newStop = Math.max(0, currentGene.getTxStart() - stopDistance);
							}
							break;
						default:
							// invalid argument
							throw new IllegalArgumentException("Invalid Stop Reference");
						}
						geneToAdd.setExonScores(null);
						// add the new gene
						if ((newStart < newStop) && (currentGene.getStrand() == Strand.FIVE)) {
							int[] exonStart = {newStart};
							int[] exonStop = {newStop};
							geneToAdd.setExonStarts(exonStart);
							geneToAdd.setExonStops(exonStop);							
							geneToAdd.setTxStart(newStart);
							geneToAdd.setTxStop(newStop);
							resultList.add(geneToAdd);
						} else if ((newStart > newStop) && (currentGene.getStrand() == Strand.THREE)) {
							int[] exonStart = {newStop};
							int[] exonStop = {newStart};
							geneToAdd.setExonStarts(exonStart);
							geneToAdd.setExonStops(exonStop);							
							geneToAdd.setTxStart(newStop);
							geneToAdd.setTxStop(newStart);
							resultList.add(geneToAdd);	
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};

			threadList.add(currentThread);
		}
		List<List<Gene>> result = op.startPool(threadList);
		if (result == null) {
			return null;
		} else {
			return new GeneList(result, geneList.getSearchURL());
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Extract Intervals";
	}


	@Override
	public String getProcessingDescription() {
		return "Extracting Intervals";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public void stop() {
		this.stopped = true;
	}
}
