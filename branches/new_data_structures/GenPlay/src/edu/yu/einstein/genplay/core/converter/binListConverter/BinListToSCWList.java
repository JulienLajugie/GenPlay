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
package edu.yu.einstein.genplay.core.converter.binListConverter;

import edu.yu.einstein.genplay.core.converter.Converter;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.binList.BinList;


/**
 * Creates a {@link ScoredChromosomeWindowList} from the data of the input {@link BinList}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class BinListToSCWList implements Converter {

	private final BinList 				list; 		// The input list.
	private GenomicListView<?> 	result;			// The output list.


	/**
	 * Creates a {@link ScoredChromosomeWindowList} from the data of the input {@link BinList}
	 * @param binList the BinList
	 */
	public BinListToSCWList(BinList binList) {
		list = binList;
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
	public void convert() throws Exception {
		result = new SimpleSCWList(list);
	}


	@Override
	public GenomicListView<?> getList() {
		return result;
	}
}
