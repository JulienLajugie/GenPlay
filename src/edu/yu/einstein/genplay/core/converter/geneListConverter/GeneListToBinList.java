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
package edu.yu.einstein.genplay.core.converter.geneListConverter;

import edu.yu.einstein.genplay.core.converter.Converter;
import edu.yu.einstein.genplay.dataStructure.enums.DataPrecision;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.dataStructure.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.dataStructure.list.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.geneList.GeneList;


/**
 * Creates a {@link BinList} from the data of the input {@link GeneList}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GeneListToBinList implements Converter {

	private final GeneList 						list; 			// The input list.
	private final int 							binSize;		// size of the bin of the result binlist
	private final DataPrecision 				precision;		// precision of the result binlist
	private final ScoreCalculationMethod 		method; 		// method for the calculation of the scores of the result binlist
	private ChromosomeListOfLists<?> 			result;			// The output list.


	/**
	 * Creates a {@link BinList} from the data of the input {@link GeneList}
	 * @param geneList the BinList
	 * @param binSize size of the bins
	 * @param precision precision of the data (eg: 1/8/16/32/64-BIT)
	 * @param method method to generate the BinList (eg: AVERAGE, SUM or MAXIMUM)
	 */
	public GeneListToBinList(GeneList geneList, int binSize, DataPrecision precision, ScoreCalculationMethod method) {
		list = geneList;
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
	public ChromosomeListOfLists<?> getList() {
		return result;
	}
}
