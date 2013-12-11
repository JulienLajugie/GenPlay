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
package edu.yu.einstein.genplay.core.operation.SCWList.simulation;

/**
 * Represents the result of a simulation
 * @author Julien Lajugie
 */
public class SimulationResult {

	private final int 		islandSize;				// size of the islands used in the simulation
	private final double 	percentageReadsAdded;	// number of reads added to the island (eg: 0.1 if there were 10% more reads)
	private final double	falsePositiveRate;		// rate of false positives
	private final double	falseNegativeRate;		// rate of false negatives


	/**
	 * Creates an instance of {@link SimulationResult}
	 * @param islandSize size of the islands used in the simulation
	 * @param percentageReadsAdded number of reads added to the island (eg: 0.1 if there were 10% more reads)
	 * @param islandCount number of island generated for the simulation
	 * @param falsePositiveCount number of islands found that were not generated
	 * @param falseNegativeCount number of islands missed
	 */
	public SimulationResult(int islandSize, double percentageReadsAdded, int islandCount, int falsePositiveCount, int falseNegativeCount) {
		this.islandSize = islandSize;
		this.percentageReadsAdded = percentageReadsAdded;
		falsePositiveRate = falsePositiveCount / islandCount;
		falseNegativeRate = falseNegativeCount / islandCount;
	}


	/**
	 * @return the false negative rate of the simulation
	 */
	public double getFalseNegativeRate() {
		return falseNegativeRate;
	}


	/**
	 * @return the false positive rate of the simulation
	 */
	public double getFalsePositiveRate() {
		return falsePositiveRate;
	}


	/**
	 * @return the size of the island used during the simulation
	 */
	public int getIslandSize() {
		return islandSize;
	}


	/**
	 * @return the percentage of read added to the S phase in the island during the simulation
	 */
	public double getPercentageReadsAdded() {
		return percentageReadsAdded;
	}
}
