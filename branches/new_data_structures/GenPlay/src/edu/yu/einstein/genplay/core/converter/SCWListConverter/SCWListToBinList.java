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
package edu.yu.einstein.genplay.core.converter.SCWListConverter;

import edu.yu.einstein.genplay.core.converter.Converter;
import edu.yu.einstein.genplay.dataStructure.enums.DataPrecision;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.dataStructure.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.list.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.genomicDataList.GenomicDataList;


/**
 * Creates a {@link BinList} from the data of the input {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SCWListToBinList implements Converter {

	private final ScoredChromosomeWindowList 	list; 		// input list
	private final int 							binSize;		// size of the bin of the result binlist
	private final DataPrecision 				precision;		// precision of the result binlist
	private final ScoreCalculationMethod 		method; 		// method for the calculation of the scores of the result binlist
	private GenomicDataList<?> 			result;			// The output list.


	/**
	 * Creates a {@link BinList} from the data of the input {@link ScoredChromosomeWindowList}
	 * @param scwList input list
	 * @param binSize size of the bins
	 * @param precision precision of the data (eg: 1/8/16/32/64-BIT)
	 * @param method method to generate the BinList (eg: AVERAGE, SUM or MAXIMUM)
	 */
	public SCWListToBinList(ScoredChromosomeWindowList scwList, int binSize, DataPrecision precision, ScoreCalculationMethod method) {
		list = scwList;
		this.binSize = binSize;
		this.precision = precision;
		this.method = method;
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
	public void convert() throws Exception {
		result = new BinList(binSize, precision, method, list);
	}


	@Override
	public GenomicDataList<?> getList() {
		return result;
	}
}
