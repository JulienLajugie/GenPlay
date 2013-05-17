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
package edu.yu.einstein.genplay.core.converter.maskListConverter;

import edu.yu.einstein.genplay.core.converter.Converter;
import edu.yu.einstein.genplay.core.operation.binList.BLOConvertIntoBinList;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;


/**
 * Creates a {@link BinList} from the data of the input {@link SCWList} of masks
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public class MaskListToBinList implements Converter {

	private final SCWList 				list; 		// input list
	private final int 					binSize;	// size of the bin of the result binlist
	private final ScoreOperation 		method; 	// method for the calculation of the scores of the result binlist
	private GenomicListView<?> 			result;		// The output list.


	/**
	 * Creates a {@link BinList} from the data of the input {@link SCWList} of masks
	 * @param maskList input list
	 * @param binSize size of the bins
	 * @param method method to generate the BinList
	 */
	public MaskListToBinList(SCWList maskList, int binSize, ScoreOperation method) {
		list = maskList;
		this.binSize = binSize;
		this.method = method;
	}


	@Override
	public void convert() throws Exception {
		result = new BLOConvertIntoBinList(list, binSize, method).compute();
	}


	@Override
	public String getDescription() {
		return "Operation: Generate Fixed Window Track";
	}


	@Override
	public GenomicListView<?> getList() {
		return result;
	}


	@Override
	public String getProcessingDescription() {
		return "Generating Fixed Window Track";
	}
}
