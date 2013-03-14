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
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.ImmutableGenomicDataList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.MaskSCWListFactory;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.MaskChromosomeWindow;


/**
 * Creates a {@link ScoredChromosomeWindowList} of {@link MaskChromosomeWindow} from the data of the input {@link GeneList}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GeneListToMaskList implements Converter {

	private final GeneList 						list; 			// The input list.
	private ImmutableGenomicDataList<?>			result;			// The output list.


	/**
	 * Creates a {@link ScoredChromosomeWindowList} of {@link MaskChromosomeWindow} from the data of the input {@link GeneList}
	 * @param geneList the BinList
	 */
	public GeneListToMaskList(GeneList geneList) {
		list = geneList;
	}


	@Override
	public String getDescription() {
		return "Operation: Generate a Mask";
	}


	@Override
	public String getProcessingDescription() {
		return "Generating a Mask";
	}


	@Override
	public void convert() throws Exception {
		result = MaskSCWListFactory.createMaskSCWArrayList(list);
	}


	@Override
	public ImmutableGenomicDataList<?> getList() {
		return result;
	}
}
