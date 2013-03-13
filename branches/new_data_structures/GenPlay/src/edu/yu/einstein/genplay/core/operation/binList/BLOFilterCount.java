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
package edu.yu.einstein.genplay.core.operation.binList;

import java.util.Arrays;
import java.util.List;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.dataStructure.list.binList.BinList;



/**
 * Removes (ie sets to zero) a specified number of low and high values
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOFilterCount implements Operation<BinList> {

	private final BinList 		binList;			// {@link BinList} to filter
	private final int 			lowValuesCount;		// number of low values to filter
	private final int 			highValuesCount;	// number of high values to filter
	private final boolean		isSaturation;		// true if we saturate, false if we remove the filtered values 
	private boolean				stopped = false;	// true if the operation must be stopped
	private Operation<BinList> 	bloFilterThreshold;	// threshold filter that does the real fitering operation
	

	/**
	 * Creates an instance of {@link BLOFilterCount}
	 * @param binList {@link BinList} to filter
	 * @param lowValuesCount number of low values to filter
	 * @param highValuesCount number of high values to filter
	 * @param isSaturation true to saturate, false to remove the filtered values
	 */
	public BLOFilterCount(BinList binList, int lowValuesCount, int highValuesCount, boolean isSaturation) {
		this.binList = binList;
		this.lowValuesCount = lowValuesCount;
		this.highValuesCount = highValuesCount;
		this.isSaturation = isSaturation;
	}


	@Override
	public BinList compute() throws Exception {
		if ((lowValuesCount < 0) || (highValuesCount < 0)) {
			throw new IllegalArgumentException("The number of values to filter must be positive");
		}
		boolean[] selectedChromo = new boolean[binList.size()];
		Arrays.fill(selectedChromo, true);
		int totalLenght = new BLOCountNonNullBins(binList,selectedChromo).compute().intValue();
		double[] allScores = new double[totalLenght];
		int i = 0;
		for (List<Double> currentList: binList) {
			if (currentList != null) {
				for (int j = 0; j < currentList.size() && !stopped; j++) {
					Double currentScore = currentList.get(j);
					if (currentScore != 0) {
						allScores[i] = currentScore;
						i++;
					}
				}
			}
		}
		Arrays.sort(allScores);
		double minValue = lowValuesCount == 0 ? Double.NEGATIVE_INFINITY : allScores[lowValuesCount - 1];
		double maxValue = highValuesCount == 0 ? Double.POSITIVE_INFINITY : allScores[allScores.length - highValuesCount];
		bloFilterThreshold = new BLOFilterThreshold(binList, minValue, maxValue, isSaturation); 
		return bloFilterThreshold.compute(); 
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
		return 1 + BinList.getCreationStepCount(binList.getBinSize());
	}

	
	@Override
	public void stop() {
		this.stopped = true;
		if (bloFilterThreshold != null) {
			bloFilterThreshold.stop();
		}
	}
}
