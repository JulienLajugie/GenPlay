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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.list.geneList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.Gene;
import edu.yu.einstein.genplay.core.enums.Strand;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.manager.ChromosomeManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;



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
								newStart = Math.max(0, currentGene.getStart() - startDistance);
							} else {
								newStart = Math.min(chromoLength, currentGene.getStop() + startDistance);
							}
							break;
						case AFTER_START:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStart = Math.min(chromoLength, currentGene.getStart() + startDistance);
							} else {
								newStart = Math.max(0, currentGene.getStop() - startDistance);
							}
							break;
						case BEFORE_MIDDLE:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStart = Math.max(0, (currentGene.getStop() + currentGene.getStart())/2 - startDistance);
							} else {
								newStart = Math.min(chromoLength, (currentGene.getStart() + currentGene.getStop())/2 + startDistance);
							}
							break;
						case AFTER_MIDDLE:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStart = Math.min(chromoLength, (currentGene.getStop() + currentGene.getStart())/2 + startDistance);
							} else {
								newStart = Math.max(0, (currentGene.getStart() + currentGene.getStop())/2 - startDistance);
							}
							break;
						case BEFORE_STOP:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStart = Math.max(0, currentGene.getStop() - startDistance);
							} else {
								newStart = Math.min(chromoLength, currentGene.getStart() + startDistance);
							}
							break;
						case AFTER_STOP:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStart = Math.min(chromoLength, currentGene.getStop() + startDistance);
							} else {
								newStart = Math.max(0, currentGene.getStart() - startDistance);
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
								newStop = Math.max(0, currentGene.getStart() - stopDistance);
							} else {
								newStop =  Math.min(chromoLength, currentGene.getStop() + stopDistance);
							}
							break;
						case AFTER_START:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStop = Math.min(chromoLength, currentGene.getStart() + stopDistance);
							} else {
								newStop = Math.max(0, currentGene.getStop() - stopDistance);
							}
							break;
						case BEFORE_MIDDLE:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStop = Math.max(0, (currentGene.getStop() + currentGene.getStart())/2 - stopDistance);
							} else {
								newStop =  Math.min(chromoLength, (currentGene.getStart() + currentGene.getStop())/2 + stopDistance);
							}
							break;
						case AFTER_MIDDLE:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStop = Math.min(chromoLength, (currentGene.getStop() + currentGene.getStart())/2 + stopDistance);
							} else {
								newStop = Math.max(0, (currentGene.getStart() + currentGene.getStop())/2 - stopDistance);
							}
							break;
						case BEFORE_STOP:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStop = Math.max(0, currentGene.getStop() - stopDistance);
							} else {
								newStop =  Math.min(chromoLength, currentGene.getStart() + stopDistance);
							}
							break;
						case AFTER_STOP:
							if (currentGene.getStrand() == Strand.FIVE) {
								newStop = Math.min(chromoLength, currentGene.getStop() + stopDistance);
							} else {
								newStop = Math.max(0, currentGene.getStart() - stopDistance);
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
							geneToAdd.setStart(newStart);
							geneToAdd.setStop(newStop);
							resultList.add(geneToAdd);
						} else if ((newStart > newStop) && (currentGene.getStrand() == Strand.THREE)) {
							int[] exonStart = {newStop};
							int[] exonStop = {newStart};
							geneToAdd.setExonStarts(exonStart);
							geneToAdd.setExonStops(exonStop);							
							geneToAdd.setStart(newStop);
							geneToAdd.setStop(newStart);
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


	/**
	 * @return a string representing the distance from argument.
	 * See constant on top of the class
	 */
	private String distanceFromToString(int distanceFrom) {
		switch (distanceFrom) {
		case AFTER_MIDDLE:
			return "after gene middle positions";
		case AFTER_START:
			return "after gene start positions";
		case AFTER_STOP:
			return "after gene stop positions";
		case BEFORE_MIDDLE:
			return "before gene middle positions";
		case BEFORE_START:
			return "before gene start positions";
		case BEFORE_STOP:
			return "before gene stop positions";
		default:
			return null;
		}

	}


	@Override
	public String getDescription() {
		return "Operation: Extract Intervals starting "
		 + startDistance + " bp " + distanceFromToString(startFrom) 
		 + " and ending " 
		 + stopDistance + " bp " + distanceFromToString(stopFrom) ;
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
