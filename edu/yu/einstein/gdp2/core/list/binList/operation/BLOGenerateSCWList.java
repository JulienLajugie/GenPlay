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
package yu.einstein.gdp2.core.list.binList.operation;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;


/**
 * Creates a SCWList from the data of the input {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class BLOGenerateSCWList implements Operation<ScoredChromosomeWindowList> {

	private final BinList 	binList; 		// input list
	
	
	/**
	 * Creates a SCWList from the data of the input BinList
	 * @param binList the BinList
	 */
	public BLOGenerateSCWList(BinList binList) {
		this.binList = binList;
	}
	
	
	@Override
	public ScoredChromosomeWindowList compute() throws Exception {
		return new ScoredChromosomeWindowList(this.binList);
	}
	
	
	@Override
	public String getDescription() {
		return "Operation: Generate Variable Window Track";
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Generating Variable Window Track";
	}
	
	
	@Override
	public int getStepCount() {
		return 1 + ScoredChromosomeWindowList.getCreationStepCount();
	}

	
	/**
	 * Does nothing
	 */
	@Override
	public void stop() {}
}
