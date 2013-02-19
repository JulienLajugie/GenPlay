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
package edu.yu.einstein.genplay.core.list.SCWList;

import java.util.List;

import edu.yu.einstein.genplay.core.chromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface ScoredChromosomeWindowList extends List<List<ScoredChromosomeWindow>>, ChromosomeListOfLists<ScoredChromosomeWindow> {

	/**
	 * Performs a deep clone of the current object
	 * @return a new ScoredChromosomeWindowList
	 */
	public ScoredChromosomeWindowList deepClone();


	/**
	 * @return the average of the BinList
	 */
	public Double getAverage();


	/**
	 * @return the greatest value of the BinList
	 */
	public Double getMax();


	/**
	 * @return the smallest value of the BinList
	 */
	public Double getMin();


	/**
	 * @return the count of none-null bins in the BinList
	 */
	public Long getNonNullLength();


	/**
	 * @param position a position on the fitted chromosome
	 * @return the score of the window on the fitted chromosome containing the specified position
	 */
	public double getScore(int position);


	/**
	 * @return the sum of the scores
	 */
	public Double getScoreSum();


	/**
	 * @param scoreSum the scoreSum to set
	 */
	public void setScoreSum(Double scoreSum);


	/**
	 * @return the standard deviation of the BinList
	 */
	public Double getStDev();


	/**
	 * @param genomeWindow {@link GenomeWindow} to display
	 * @param xFactor xRatio on the screen (ie ratio between the number of pixel and the number of base to display)
	 * @return a data list adapted to the screen resolution
	 */
	public List<ScoredChromosomeWindow> getFittedData(GenomeWindow genomeWindow, double xFactor);
}
