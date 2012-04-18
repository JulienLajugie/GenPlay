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
package edu.yu.einstein.genplay.core.RNAPosToDNAPos;

import edu.yu.einstein.genplay.core.manager.ChromosomeManager;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;


/**
 * Class to sort the file based on chromosome names and start values
 * @author Chirag Gorasia
 * @version 0.1
 */
public class FileDataLineForSorting implements Comparable<FileDataLineForSorting>{

	private String chromosomeName;														// chromosome name
	private int start;																	// start position
	private int stop;																	// stop position
	private double score;																// score
		
	
	/**
	 * Creates an instance of {@link FileDataLineForSorting}
	 * @param chromosomeName
	 * @param start
	 * @param stop
	 * @param score
	 */
	public FileDataLineForSorting(String chromosomeName, int start, int stop, double score) {
		this.chromosomeName = chromosomeName;
		this.start = start;
		this.stop = stop;
		this.score = score;		
	}
	
	
	/**
	 * Returns the chromosomeNumber 
	 * @return chromosomeNumber
	 */
	public int getChromosomeNumber() {
		try {
			return ChromosomeManager.getInstance().getIndex(getChromosomeName());
		} catch (InvalidChromosomeException e) {
			return 0;
		}
	}

	
	/**
	 * Returns the chromosmeName			
	 * @return chromosomeName
	 */
	public String getChromosomeName() {
		return chromosomeName;
	}
	
	
	/**
	 * Returns the start position
	 * @return start
	 */
	public int getStart() {
		return start;
	}
	
	
	/**
	 * Returns the stop position
	 * @return stop
	 */
	public int getStop() {
		return stop;
	}
	
	
	/**
	 * Returns the score
	 * @return score
	 */
	public double getScore() {
		return score;
	}
	
	
	@Override
	public int compareTo(FileDataLineForSorting o) {
		if (this.getChromosomeNumber() > o.getChromosomeNumber()) {
			return 1;
		} else if (this.getChromosomeNumber() < o.getChromosomeNumber()) {
			return -1;
		} else { 
			if (this.start > o.start) {
				return 1;
			} else if (this.start < o.start) {
				return -1;
			} else {
				if (this.stop > o.stop) {
					return 1;
				} else if (this.stop < o.stop) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	}
}
