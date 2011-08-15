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
package edu.yu.einstein.genplay.core.list.binList.operation;

import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.enums.IslandResultType;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.binList.operation.peakFinder.IslandFinder;
import edu.yu.einstein.genplay.core.operation.Operation;



/**
 * Use the Island approach to separate data on islands
 * @author Nicolas Fourel
 * @version 0.1
 */
public class BLOFindIslands implements Operation<BinList[]> {

	private final BinList 		inputBinList;	// input binlist
	private BinList[]			outputBinList;
	private IslandResultType[] 	list;
	private IslandFinder 		island;

	
	/**
	 * @param binList
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public BLOFindIslands (BinList binList) throws InterruptedException, ExecutionException {
		this.inputBinList = binList;
		this.island = new IslandFinder(binList);
	}
	
	
	@Override
	public BinList[] compute () throws InterruptedException, ExecutionException {
		this.outputBinList = new BinList[this.list.length];
		for (int i=0; i < this.list.length; i++) {
			if (this.list[i] != null) {
				this.island.setResultType(this.list[i]);	// at this point, the resultType setting is the last to set
				this.outputBinList[i] = this.island.findIsland();	// we store the calculated bin list on the output binlist array of bloIsland object
			}
		}
		return this.outputBinList;
	}

	
	@Override
	public String getDescription () {
		String description = "Operation: Island Finder";
		description += ", Window value: " + getIsland().getWindowLimitValue();
		description += ", Gap: " + getIsland().getGap();
		description += ", Island score: " + getIsland().getIslandLimitScore();
		description += ", Island length: " + getIsland().getMinIslandLength();
		//description += ", Result type: " + getIsland().getResultType();
		return description;
	}
	
	
	@Override
	public int getStepCount() {
		return (BinList.getCreationStepCount(inputBinList.getBinSize()) + 1) * this.numResult();
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Searching Islands";
	}

	
	/**
	 * Count the number of valid result type.
	 * The array size will be always 2 (filtered & island score) but some fields can be null and do not been counted.
	 * @return	number of valid result type
	 */
	private int numResult() {
		int cpt = 0;
		for (int i=0; i < this.list.length; i++) {
			if (this.list[i] != null) {
				cpt++;
			}
		}
		return cpt;
	}
	
	
	//Getters & Setters
	public IslandFinder getIsland() {
		return island;
	}

	
	public IslandResultType[] getResultTypes() {
		return list;
	}
	
	
	public void setList(IslandResultType[] list) {
		this.list = list;
	}
	

	@Override
	public void stop() {
		if (this.island != null) {
			this.island.stop();
		}
	}
}
