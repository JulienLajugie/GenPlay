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
package edu.yu.einstein.genplay.core.list.geneList.operation;

import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.gene.Gene;
import edu.yu.einstein.genplay.core.list.GenomicDataList;
import edu.yu.einstein.genplay.core.operation.Operation;


/**
 * Computes the average value of the scores of a {@link GenomicDataList} of genes
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GLOAverageScore implements Operation<Double> {

	private final GenomicDataList<Gene> geneList;		// input genomic list of genes
	private final boolean[] 			chromoList;		// 1 boolean / chromosome.
	// each boolean sets to true means that the corresponding chromosome is selected


	/**
	 *  Computes the average value of the scores of a specified {@link GenomicDataList} of genes
	 * @param geneList input 
	 * @param chromoList list of boolean. A boolean set to true means that the
	 * chromosome with the same index is going to be used for the calculation.
	 */
	public GLOAverageScore(GenomicDataList<Gene> geneList, boolean[] chromoList) {
		this.geneList = geneList;
		this.chromoList = chromoList;
	}


	@Override
	public Double compute() throws InterruptedException, ExecutionException {
		long geneNumber = new GLOCountExons(geneList, chromoList).compute();
		double totalScore = new GLOSumScore(geneList, chromoList).compute();
		if (geneNumber == 0) {
			return 0d;
		} else {
			return totalScore / geneNumber;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Average";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public String getProcessingDescription() {
		return "Computing Average";
	}


	@Override
	public void stop() {}
}
