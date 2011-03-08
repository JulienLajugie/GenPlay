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
package edu.yu.einstein.genplay.core.list.SCWList.operation;

import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.operation.Operation;


/**
 * Calculates the maximum score to display on a ScoredChromosomeWindowList track
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLOMaxScoreToDisplay implements Operation<Double> {

	private final ScoredChromosomeWindowList 	scwList;		// input list
	private boolean								stopped = false;// true if the operation must be stopped


	/**
	 * Calculates the maximum score to display on a ScoredChromosomeWindowList track
	 * @param scwList input {@link ScoredChromosomeWindowList}
	 */
	public SCWLOMaxScoreToDisplay(ScoredChromosomeWindowList scwList) {
		this.scwList = scwList;
	}


	@Override
	public Double compute() {
		final double realMax = scwList.getMax();
		// if the max is negative we return 0
		if (realMax <= 0) {
			return 0d;
		}
		// if the max of the BinList can be written as 10^x we return this value as a maximum
		double maxScoreDisplayed = 1;
		while (realMax / maxScoreDisplayed > 1 && !stopped) {
			maxScoreDisplayed *= 10;
		}
		if (realMax / maxScoreDisplayed == 1) {
			return realMax;
		}
		// otherwise we try to find the closest 10^x value above (average + stdev) 
		double proposedMax = scwList.getAverage() + scwList.getStDev(); 
		if (proposedMax <= 0) {
			return 0d;
		}
		maxScoreDisplayed = 1;
		while (proposedMax / maxScoreDisplayed > 1 && !stopped) {
			maxScoreDisplayed *= 10;
		}
		return maxScoreDisplayed;
	}


	@Override
	public String getDescription() {
		return "Operation: Maximum Score to Display";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public String getProcessingDescription() {
		return "Searching Maximum";
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
