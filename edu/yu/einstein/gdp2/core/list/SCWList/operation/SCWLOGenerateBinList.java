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
package yu.einstein.gdp2.core.list.SCWList.operation;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;


/**
 * Creates a BinList from the data of the input {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLOGenerateBinList implements Operation<BinList> {

	private final ScoredChromosomeWindowList 	scwList; 	// input list
	private final int 							binSize;	// size of the bin of the result binlist
	private final DataPrecision 				precision;	// precision of the result binlist
	private final ScoreCalculationMethod 		method; 	// method for the calculation of the scores of the result binlist

	
	/**
	 * Creates a BinList from the data of the input {@link ScoredChromosomeWindowList}
	 * @param binSize size of the bins
	 * @param precision precision of the data (eg: 1/8/16/32/64-BIT)
	 * @param method method to generate the BinList (eg: AVERAGE, SUM or MAXIMUM)
	 */
	public SCWLOGenerateBinList(ScoredChromosomeWindowList scwList, int binSize, DataPrecision precision, ScoreCalculationMethod method) {
		this.scwList = scwList;
		this.binSize = binSize;
		this.precision = precision;
		this.method = method;
	}

	
	@Override
	public BinList compute() throws Exception {
		return new BinList(binSize, precision, method, scwList);
	}


	@Override
	public String getDescription() {
		return "Operation: Generate Fixed Window Track";
	}


	@Override
	public String getProcessingDescription() {
		return "Generating Fixed Window Track";
	}


	@Override
	public int getStepCount() {
		return 1 + BinList.getCreationStepCount(binSize);
	}

	
	/**
	 * Does nothing
	 */
	@Override
	public void stop() {}
}
