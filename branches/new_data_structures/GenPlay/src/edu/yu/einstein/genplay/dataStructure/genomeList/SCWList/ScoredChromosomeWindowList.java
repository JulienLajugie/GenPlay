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
package edu.yu.einstein.genplay.dataStructure.genomeList.SCWList;

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.genomeList.GenomicDataList;
import edu.yu.einstein.genplay.dataStructure.genomeList.ImmutableGenomicDataList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * A {@link GenomicDataList} of {@link ScoredChromosomeWindow}
 * @author Julien Lajugie
 */
public interface ScoredChromosomeWindowList extends ImmutableGenomicDataList<ScoredChromosomeWindow> {


	/**
	 * @return the average score of the windows of the list
	 */
	public double getAverage();


	/**
	 * @return the greatest score of the windows of the list list
	 */
	public double getMaximum();


	/**
	 * @return the smallest score of the windows of the list list
	 */
	public double getMinimum();


	/**
	 * @return the sum of the lengths (in bp) of none-null windows of the list
	 */
	public long getNonNullLength();


	/**
	 * @param chromosome a {@link Chromosome}
	 * @param position a position on the chromosome
	 * @return the score at the specified position
	 */
	public double getScore(Chromosome chromosome, int position);


	/**
	 * @return the sum of the scores of the windows of the list
	 */
	public double getScoreSum();


	/**
	 * @return the type of the {@link ScoredChromosomeWindowList}
	 */
	public SCWListType getSCWListType();


	/**
	 * @return the standard deviation of the scores of the list
	 */
	public double getStandardDeviation();
}
