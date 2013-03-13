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

import java.util.Arrays;
import java.util.List;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.dataStructure.genomeList.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.genomeList.SCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;



/**
 * Removes a specified number of low and high values
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLOFilterCount implements Operation<ScoredChromosomeWindowList> {

	private final ScoredChromosomeWindowList 		inputList;				// list to filter
	private final int 								lowValuesCount;			// number of low values to filter
	private final int 								highValuesCount;		// number of high values to filter
	private final boolean							isSaturation;			// true if we saturate, false if we remove the filtered values
	private boolean									stopped = false;		// true if the operation must be stopped
	private Operation<ScoredChromosomeWindowList> 	scwloFilterThreshold;	// threshold filter that does the real fitering operation


	/**
	 * Creates an instance of {@link SCWLOFilterCount}
	 * @param inputList {@link ScoredChromosomeWindowList} to filter
	 * @param lowValuesCount number of low values to filter
	 * @param highValuesCount number of high values to filter
	 * @param isSaturation true to saturate, false to remove the filtered values
	 */
	public SCWLOFilterCount(ScoredChromosomeWindowList inputList, int lowValuesCount, int highValuesCount, boolean isSaturation) {
		this.inputList = inputList;
		this.lowValuesCount = lowValuesCount;
		this.highValuesCount = highValuesCount;
		this.isSaturation = isSaturation;
	}


	@Override
	public ScoredChromosomeWindowList compute() throws Exception {
		if ((lowValuesCount < 0) || (highValuesCount < 0)) {
			throw new IllegalArgumentException("The number of values to filter must be positive");
		}
		// compute the total number of windows
		int totalLenght = 0;
		for (List<ScoredChromosomeWindow> currentList: inputList) {
			if (currentList != null) {
				totalLenght += currentList.size();
			}
		}
		// create an array containing all the scores of the input list
		double[] allScores = new double[totalLenght];
		int i = 0;
		for (List<ScoredChromosomeWindow> currentList: inputList) {
			if (currentList != null) {
				for (int j = 0; (j < currentList.size()) && !stopped; j++) {
					Double currentScore = currentList.get(j).getScore();
					allScores[i] = currentScore;
					i++;
				}
			}
		}
		// sort the array and search the value of the min and of the max corresponding to the thresholds
		Arrays.sort(allScores);
		double minValue = lowValuesCount == 0 ? Double.NEGATIVE_INFINITY : allScores[lowValuesCount - 1];
		double maxValue = highValuesCount == 0 ? Double.POSITIVE_INFINITY : allScores[allScores.length - highValuesCount];
		// start a SCWLOFilterThreshold with the min and max value that we just found
		scwloFilterThreshold = new SCWLOFilterThreshold(inputList, minValue, maxValue, isSaturation);
		return scwloFilterThreshold.compute();
	}


	@Override
	public String getDescription() {
		String optionStr;
		if (isSaturation) {
			optionStr = ", option = saturation";
		} else {
			optionStr = ", option = remove";
		}
		return "Operation: Filter, " + lowValuesCount + " smallest values, " + highValuesCount + " greatest values" + optionStr;
	}


	@Override
	public String getProcessingDescription() {
		return "Filtering";
	}


	@Override
	public int getStepCount() {
		return 1 + SimpleSCWList.getCreationStepCount();
	}


	@Override
	public void stop() {
		this.stopped = true;
		if (scwloFilterThreshold != null) {
			scwloFilterThreshold.stop();
		}
	}
}
