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
package edu.yu.einstein.genplay.core.operation.SCWList;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;


/**
 * Calculates the minimum score to display on a ScoredChromosomeWindowList track
 * @author Julien Lajugie
 */
public class SCWLOMinScoreToDisplay implements Operation<Float> {

	private final SCWList 	scwList;			// input list
	private boolean			stopped = false;	// true if the operation must be stopped


	/**
	 * Calculates the minimum score to display on a ScoredChromosomeWindowList track
	 * @param scwList input {@link SCWList}
	 */
	public SCWLOMinScoreToDisplay(SCWList scwList) {
		this.scwList = scwList;
	}


	@Override
	public Float compute() {
		// if the min is positive we return 0
		float realMin = scwList.getStatistics().getMinimum();
		if (realMin >= 0) {
			return 0f;
		}
		// if the min of the list can be written as -10^x we return this value as a minimum
		float minScoreDisplayed = -1;
		while (((realMin / minScoreDisplayed) > 1) && !stopped) {
			minScoreDisplayed *= 10;
		}
		if ((realMin / minScoreDisplayed) == 1) {
			return realMin;
		}
		// otherwise we try to find the closest 10^x value under (average - stdev)
		float proposedMin = (float) (scwList.getStatistics().getAverage() - scwList.getStatistics().getStandardDeviation());
		if (proposedMin >= 0) {
			return 0f;
		}
		minScoreDisplayed = -1;
		while (((proposedMin / minScoreDisplayed) > 1) && !stopped) {
			minScoreDisplayed *= 10;
		}
		return minScoreDisplayed;
	}


	@Override
	public String getDescription() {
		return "Operation: Minimum Score to Display";
	}


	@Override
	public String getProcessingDescription() {
		return "Searching Minimum";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
