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

import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.binList.BinList;



/**
 * Creates a new BinList with a new bin size
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOChangeBinSize implements Operation<BinList> {

	private final BinList 					binList;		// input BinList
	private final int 						binSize;		// new bin size 
	private final ScoreOperation 	method;			// method for the calculation of the new binlist
	

	/**
	 * Creates a new BinList with a new bin size
	 * @param binList input BinList
	 * @param binSize new bin size
	 * @param method {@link ScoreOperation} for the calculation of the new BinList
	 */
	public BLOChangeBinSize(BinList binList, int binSize, ScoreOperation method) {
		this.binList = binList;
		this.binSize = binSize;
		this.method = method;
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		if (binSize == binList.getBinSize()) {
			return binList.deepClone();
		}
		BinList resultList = new BinList(binSize, binList.getPrecision(), method, binList, true);
		return resultList;
	}
	

	@Override
	public String getDescription() {
		return "Bin Size Changes to " + binSize + "bp, Method of Calculation = " + method;
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binSize) + 1;
	}


	@Override
	public String getProcessingDescription() {
		return "Changing Window Size";
	}

	
	/**
	 * Does nothing
	 */
	@Override
	public void stop() {}
}
