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

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.geneList.distanceCalculator.DistanceCalculator;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;



/**
 * Computes the distance between closest genes from two {@link GeneList}
 * The position to be used as a reference (ie: start / middle / stop of a gene)
 * must be specified by the user.
 * @author Chirag Gorasia
 * @version 0.1
 */
public class GLODistanceCalculator implements Operation<long[][]>{

	private final GeneList 	geneList1;			// input GeneList
	private final GeneList	geneList2;			// input GeneList
	private final int 		selectionCase;		// selection type
	private boolean			stopped = false;	// true if the operation must be stopped

	private static final int POSITIVE_START_START = 1;
	private static final int POSITIVE_START_MIDDLE = 2;
	private static final int POSITIVE_START_STOP = 3;

	private static final int POSITIVE_MIDDLE_START = 4;
	private static final int POSITIVE_MIDDLE_MIDDLE = 5;
	private static final int POSITIVE_MIDDLE_STOP = 6;

	private static final int POSITIVE_STOP_START = 7;
	private static final int POSITIVE_STOP_MIDDLE = 8;
	private static final int POSITIVE_STOP_STOP = 9;

	private static final int NEGATIVE_START_START = 10;
	private static final int NEGATIVE_START_MIDDLE = 11;
	private static final int NEGATIVE_START_STOP = 12;

	private static final int NEGATIVE_MIDDLE_START = 13;
	private static final int NEGATIVE_MIDDLE_MIDDLE = 14;
	private static final int NEGATIVE_MIDDLE_STOP = 15;

	private static final int NEGATIVE_STOP_START = 16;
	private static final int NEGATIVE_STOP_MIDDLE = 17;
	private static final int NEGATIVE_STOP_STOP = 18;

	private static final int BOTH_RELATIVE_START_START = 19;
	private static final int BOTH_RELATIVE_START_MIDDLE = 20;
	private static final int BOTH_RELATIVE_START_STOP = 21;

	private static final int BOTH_RELATIVE_MIDDLE_START = 22;
	private static final int BOTH_RELATIVE_MIDDLE_MIDDLE = 23;
	private static final int BOTH_RELATIVE_MIDDLE_STOP = 24;

	private static final int BOTH_RELATIVE_STOP_START = 25;
	private static final int BOTH_RELATIVE_STOP_MIDDLE = 26;
	private static final int BOTH_RELATIVE_STOP_STOP = 27;

	private static final int BOTH_ABSOLUTE_START_START = 28;
	private static final int BOTH_ABSOLUTE_START_MIDDLE = 29;
	private static final int BOTH_ABSOLUTE_START_STOP = 30;

	private static final int BOTH_ABSOLUTE_MIDDLE_START = 31;
	private static final int BOTH_ABSOLUTE_MIDDLE_MIDDLE = 32;
	private static final int BOTH_ABSOLUTE_MIDDLE_STOP = 33;

	private static final int BOTH_ABSOLUTE_STOP_START = 34;
	private static final int BOTH_ABSOLUTE_STOP_MIDDLE = 35;
	private static final int BOTH_ABSOLUTE_STOP_STOP = 36;


	/**
	 * Creates an instance of {@link GLODistanceCalculator}
	 * @param geneList1 a {@link GeneList}
	 * @param geneList2 a second {@link GeneList}
	 * @param selectionCase selection type
	 */
	public GLODistanceCalculator(GeneList geneList1, GeneList geneList2, int selectionCase) {
		this.geneList1 = geneList1;
		this.geneList2 = geneList2;
		this.selectionCase = selectionCase;
	}


	@Override
	public long[][] compute() throws Exception {
		long[][] result = new long[geneList1.size()][];
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<long[]>> threadList = new ArrayList<Callable<long[]>>();
		for (int i = 0; i < geneList1.size(); i++) {
			final int chromoindex = i;
			Callable<long[]> currentThread = new Callable<long[]>() {
				@Override
				public long[] call() throws Exception {
					long[] chromoresult = new long[geneList1.get(chromoindex).size()];
					if ((geneList1 != null) && (geneList2 != null)) {
						chromoresult = handleCases(geneList1.get(chromoindex), geneList2, chromoindex);
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return chromoresult;
				}
			};
			threadList.add(currentThread);
		}
		if (op.startPool(threadList) == null) {
			return null;
		}

		int i = 0;
		for (long[] currentResult: op.startPool(threadList)) {
			if (currentResult != null) {
				result[i++] = currentResult;
			}
		}
		return result;
	}


	@Override
	public String getDescription() {
		return "Operation: Distance Calculation";
	}


	@Override
	public String getProcessingDescription() {
		return "Calculating Distance";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	/**
	 * Method to handle all of the 20 cases to compute the distance
	 * @param firstList
	 * @param secondList
	 * @return a double 2-D array containing distances
	 */
	private long[] handleCases(ListView<Gene> firstList, GeneList secondList, int chrindex) {
		DistanceCalculator dc;
		int k = 0;
		long[] distanceByChromosomes = new long[firstList.size()];
		long[] distanceArray = new long[firstList.size()];
		switch(selectionCase) {
		case POSITIVE_START_START:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 0, 0, 0, firstList.get(j).getStart());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[k++] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case POSITIVE_START_MIDDLE:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 0, 0, 1, firstList.get(j).getStart());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[k++] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case POSITIVE_START_STOP:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 0, 0, 2, firstList.get(j).getStart());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[k++] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case POSITIVE_MIDDLE_START:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 0, 1, 0, (int)firstList.get(j).getMiddlePosition());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[k++] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case POSITIVE_MIDDLE_MIDDLE:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 0, 1, 1, (int)firstList.get(j).getMiddlePosition());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[k++] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case POSITIVE_MIDDLE_STOP:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 0, 1, 2, (int)firstList.get(j).getMiddlePosition());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[k++] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case POSITIVE_STOP_START:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 0, 2, 0, firstList.get(j).getStop());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[k++] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case POSITIVE_STOP_MIDDLE:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 0, 2, 1, firstList.get(j).getStop());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[k++] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case POSITIVE_STOP_STOP:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 0, 2, 2, firstList.get(j).getStop());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[k++] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case NEGATIVE_START_START:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 0, 0, firstList.get(j).getStart());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[k++] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case NEGATIVE_START_MIDDLE:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 0, 1, firstList.get(j).getStart());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[k++] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case NEGATIVE_START_STOP:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 0, 2, firstList.get(j).getStart());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[k++] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case NEGATIVE_MIDDLE_START:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 1, 0, firstList.get(j).getStart());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[k++] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case NEGATIVE_MIDDLE_MIDDLE:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 1, 1, (int)firstList.get(j).getMiddlePosition());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[k++] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case NEGATIVE_MIDDLE_STOP:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 1, 2, (int)firstList.get(j).getMiddlePosition());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[k++] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case NEGATIVE_STOP_START:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 0, firstList.get(j).getStop());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[k++] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case NEGATIVE_STOP_MIDDLE:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 1, firstList.get(j).getStop());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[k++] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case NEGATIVE_STOP_STOP:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[k++] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case BOTH_RELATIVE_START_START:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				distanceArray[j] = dc.getClosestDistance();
			}
			distanceByChromosomes = distanceArray;
			break;

		case BOTH_RELATIVE_START_MIDDLE:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				if (dc.getClosestDistance() >= 0) {
					distanceArray[j] = dc.getClosestDistance();
				}
			}
			distanceByChromosomes = distanceArray;
			break;

		case BOTH_RELATIVE_START_STOP:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				distanceArray[j] = dc.getClosestDistance();
			}
			distanceByChromosomes = distanceArray;
			break;

		case BOTH_RELATIVE_MIDDLE_START:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				distanceArray[j] = dc.getClosestDistance();
			}
			distanceByChromosomes = distanceArray;
			break;

		case BOTH_RELATIVE_MIDDLE_MIDDLE:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				distanceArray[j] = dc.getClosestDistance();
			}
			distanceByChromosomes = distanceArray;
			break;

		case BOTH_RELATIVE_MIDDLE_STOP:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				distanceArray[j] = dc.getClosestDistance();
			}
			distanceByChromosomes = distanceArray;
			break;

		case BOTH_RELATIVE_STOP_START:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				distanceArray[j] = dc.getClosestDistance();
			}
			distanceByChromosomes = distanceArray;
			break;

		case BOTH_RELATIVE_STOP_MIDDLE:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				distanceArray[j] = dc.getClosestDistance();
			}
			distanceByChromosomes = distanceArray;
			break;

		case BOTH_RELATIVE_STOP_STOP:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				distanceArray[j] = dc.getClosestDistance();
			}
			distanceByChromosomes = distanceArray;
			break;

		case BOTH_ABSOLUTE_START_START:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				distanceArray[j] = dc.getClosestDistance();
			}
			distanceByChromosomes = distanceArray;
			break;

		case BOTH_ABSOLUTE_START_MIDDLE:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				distanceArray[j] = dc.getClosestDistance();
			}
			distanceByChromosomes = distanceArray;
			break;

		case BOTH_ABSOLUTE_START_STOP:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				distanceArray[j] = dc.getClosestDistance();
			}
			distanceByChromosomes = distanceArray;
			break;

		case BOTH_ABSOLUTE_MIDDLE_START:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				distanceArray[j] = dc.getClosestDistance();
			}
			distanceByChromosomes = distanceArray;
			break;

		case BOTH_ABSOLUTE_MIDDLE_MIDDLE:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				distanceArray[j] = dc.getClosestDistance();
			}
			distanceByChromosomes = distanceArray;
			break;

		case BOTH_ABSOLUTE_MIDDLE_STOP:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				distanceArray[j] = dc.getClosestDistance();
			}
			distanceByChromosomes = distanceArray;
			break;

		case BOTH_ABSOLUTE_STOP_START:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				distanceArray[j] = dc.getClosestDistance();
			}
			distanceByChromosomes = distanceArray;
			break;

		case BOTH_ABSOLUTE_STOP_MIDDLE:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				distanceArray[j] = dc.getClosestDistance();
			}
			distanceByChromosomes = distanceArray;
			break;

		case BOTH_ABSOLUTE_STOP_STOP:
			for (int j = 0; (j < firstList.size()) && !stopped; j++) {
				dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getStop());
				distanceArray[j] = dc.getClosestDistance();
			}
			distanceByChromosomes = distanceArray;
			break;
		}
		return distanceByChromosomes;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
