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
package edu.yu.einstein.genplay.core.list.binList.operation;

import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.operation.Operation;


/**
 * Calculates the minimum score to display on a BinList track
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOMinScoreToDisplay implements Operation<Double> {

	private final BinList 	binList;		// input BinList
	private boolean			stopped = false;// true if the operation must be stopped
	
	
	/**
	 * Calculates the minimum score to display on a BinList track
	 * @param binList input {@link BinList}
	 */
	public BLOMinScoreToDisplay(BinList binList) {
		this.binList = binList;
	}
	
	
	@Override
	public Double compute() {
		// if the min is positive we return 0
		final double realMin = binList.getMin();
		if (realMin >= 0) {
			return 0d;
		}
		// if the min of the BinList can be written as -10^x we return this value as a minimum
		double minScoreDisplayed = -1;
		while (realMin / minScoreDisplayed > 1 && !stopped) {
			minScoreDisplayed *= 10;
		}
		if (realMin / minScoreDisplayed == 1) {
			return realMin;
		}
		// otherwise we try to find the closest 10^x value under (average - stdev) 
		final double proposedMin = binList.getAverage() - binList.getStDev(); 
		if (proposedMin >= 0) {
			return 0d;
		}
		minScoreDisplayed = -1;
		while (proposedMin / minScoreDisplayed > 1 && !stopped) {
			minScoreDisplayed *= 10;
		}
		return minScoreDisplayed;
	}

	
	@Override
	public String getDescription() {
		return "Operation: Minimum Score to Display";
	}
	
	
	@Override
	public int getStepCount() {
		return 1;
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Searching Minimum";
	}

	
	@Override
	public void stop() {
		this.stopped = true;
	}
}
