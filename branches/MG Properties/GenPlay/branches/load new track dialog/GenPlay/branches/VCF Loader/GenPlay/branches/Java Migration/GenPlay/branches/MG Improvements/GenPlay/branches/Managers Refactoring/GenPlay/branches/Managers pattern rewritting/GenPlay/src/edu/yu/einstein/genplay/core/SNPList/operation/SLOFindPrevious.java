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
package edu.yu.einstein.genplay.core.SNPList.operation;

import java.util.List;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.SNP;
import edu.yu.einstein.genplay.core.SNPList.SNPList;
import edu.yu.einstein.genplay.core.operation.Operation;



/**
 * Finds the previous SNP from a specified position 
 * @author Julien Lajugie
 * @version 0.1
 */
public class SLOFindPrevious implements Operation<SNP> {

	private final SNPList 		inputList;	// input list containing the SNPs
	private final Chromosome 	chromosome;	// chromosome of the starting loci
	private final int 			position;	// position of the starting loci
	
	
	/**
	 * Creates an instance of {@link SLOFindPrevious}
	 * @param inputList input list containing the SNPs
	 * @param chromosome {@link Chromosome} of the starting loci
	 * @param position position of the starting loci
	 */
	public SLOFindPrevious(SNPList inputList, Chromosome chromosome, int position) {
		this.inputList = inputList;
		this.chromosome = chromosome;
		this.position = position;
	}
	
	
	@Override
	public SNP compute() throws Exception {
		List<SNP> list = inputList.get(chromosome);
		int previousSNPIndex = SNPList.findSNP(list, position, 0, list.size() - 1) - 1;
		// the previous SNP of the first SNP is the first element itself
		if (previousSNPIndex < 0) {
			return null;
		} else {
			return list.get(previousSNPIndex);
		}
	}

	@Override
	public String getDescription() {
		return "Search Previous SNP";
	}

	@Override
	public String getProcessingDescription() {
		return "Searching previous SNP";
	}

	@Override
	public int getStepCount() {
		return 1;
	}

	
	/**
	 * Does nothing
	 */
	@Override
	public void stop() {}
}
