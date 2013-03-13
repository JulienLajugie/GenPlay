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

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.dataStructure.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.list.SCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.binList.BinList;


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
		return new SimpleSCWList(this.binList);
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
		return 1 + SimpleSCWList.getCreationStepCount();
	}


	/**
	 * Does nothing
	 */
	@Override
	public void stop() {}
}
