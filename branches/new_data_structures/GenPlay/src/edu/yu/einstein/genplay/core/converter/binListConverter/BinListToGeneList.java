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

import javax.naming.OperationNotSupportedException;

import edu.yu.einstein.genplay.core.converter.Converter;
import edu.yu.einstein.genplay.dataStructure.genomeList.GenomicDataList;
import edu.yu.einstein.genplay.dataStructure.genomeList.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.genomeList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.genomeList.geneList.GeneList;


/**
 * Creates a {@link GeneList} from the data of the input {@link BinList}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class BinListToGeneList implements Converter {

	@SuppressWarnings("unused")
	private final BinList 		list; 		// The input list.
	private GenomicDataList<?> 	result;		// The output list.


	/**
	 * Creates a {@link ScoredChromosomeWindowList} from the data of the input {@link BinList}
	 * @param binList the BinList
	 */
	public BinListToGeneList(BinList binList) {
		list = binList;
	}


	@Override
	public String getDescription() {
		return "Operation: Generate Gene Track";
	}


	@Override
	public String getProcessingDescription() {
		return "Generating Gene Track";
	}


	@Override
	public void convert() throws Exception {
		// TODO creates this method
		throw new OperationNotSupportedException("convert binlist into genelist not implemented yet");
		//result = GeneListFactory.createGeneArrayList(list);
	}


	@Override
	public GenomicDataList<?> getList() {
		return result;
	}
}
