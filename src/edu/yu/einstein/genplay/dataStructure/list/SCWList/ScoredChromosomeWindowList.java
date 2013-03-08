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
package edu.yu.einstein.genplay.dataStructure.list.SCWList;

import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.list.GenomicDataList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * A {@link GenomicDataList} of {@link ScoredChromosomeWindow}
 * @author Nicolas Fourel
 */
public interface ScoredChromosomeWindowList extends List<List<ScoredChromosomeWindow>>, GenomicDataList<ScoredChromosomeWindow> {

	/**
	 * Computes the statistics for the list
	 * @throws InterruptedException 
	 * @throws ExecutionException 
	 */
	public void computeStatistics() throws InterruptedException, ExecutionException;

	
	/**
	 * Performs a deep clone of the current object
	 * @return a new ScoredChromosomeWindowList
	 */
	public ScoredChromosomeWindowList deepClone();


	/**
	 * @return the average score of the windows of the list
	 */
	public Double getAverage();


	/**
	 * @return the greatest score of the windows of the list list
	 */
	public Double getMaximum();


	/**
	 * @return the smallest score of the windows of the list list
	 */
	public Double getMinimum();


	/**
	 * @return the sum of the lengths (in bp) of none-null windows of the list
	 */
	public Long getNonNullLength();


	/**
	 * @return the sum of the scores of the windows of the list
	 */
	public Double getScoreSum();


	/**
	 * @param chromosome a {@link Chromosome}
	 * @param position a position on the chromosome
	 * @return the score at the specified position
	 */
	public double getScore(Chromosome chromosome, int position);


	/**
	 * @return the standard deviation of the scores of the list
	 */
	public Double getStandardDeviation();


	/**
	 * @return the type of the {@link ScoredChromosomeWindowList}
	 */
	public SCWListType getScoredChromosomeWindowListType();
	
	
	/**
	 * Sort the list for each chromosome ordering items by window start positions
	 */
	public void sort();
}
