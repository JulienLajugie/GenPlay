/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.core.operation.SCWList;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;


/**
 * Calculates the maximum score to display on a ScoredChromosomeWindowList track
 * @author Julien Lajugie
 */
public class SCWLOMaxScoreToDisplay implements Operation<Float> {

	private final SCWList 	scwList;			// input list
	private boolean			stopped = false;	// true if the operation must be stopped


	/**
	 * Calculates the maximum score to display on a ScoredChromosomeWindowList track
	 * @param scwList input {@link SCWList}
	 */
	public SCWLOMaxScoreToDisplay(SCWList scwList) {
		this.scwList = scwList;
	}


	@Override
	public Float compute() {
		final float realMax = scwList.getStatistics().getMaximum();
		// if the max is negative we return 0
		if (realMax <= 0) {
			return 0f;
		}
		// if the max of the BinList can be written as 10^x we return this value as a maximum
		float maxScoreDisplayed = 1;
		while (((realMax / maxScoreDisplayed) > 1) && !stopped) {
			maxScoreDisplayed *= 10;
		}
		if ((realMax / maxScoreDisplayed) == 1) {
			return realMax;
		}
		// otherwise we try to find the closest 10^x value above (average + stdev)
		float proposedMax = (float) (scwList.getStatistics().getAverage() + scwList.getStatistics().getStandardDeviation());
		if (proposedMax <= 0) {
			return 0f;
		}
		maxScoreDisplayed = 1;
		while (((proposedMax / maxScoreDisplayed) > 1) && !stopped) {
			maxScoreDisplayed *= 10;
		}
		return maxScoreDisplayed;
	}


	@Override
	public String getDescription() {
		return "Operation: Maximum Score to Display";
	}


	@Override
	public String getProcessingDescription() {
		return "Searching Maximum";
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
