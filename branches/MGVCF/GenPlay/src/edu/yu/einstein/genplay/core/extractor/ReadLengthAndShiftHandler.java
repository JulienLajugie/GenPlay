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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.extractor;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.ChromosomeWindow;
import edu.yu.einstein.genplay.core.enums.Strand;


/**
 * This class provides tools to compute the start and stop positions  
 * of reads depending on the specified strand shift value and read length value 
 * @author Julien Lajugie
 * @version 0.1
 */
public class ReadLengthAndShiftHandler {
	private final int strandShift;	// value of the shift to perform on the selected strand
	private final int readLength;	// length of the reads. 0 to keep the values from the input data file

	
	/**
	 * Creates an instance of {@link ReadLengthAndShiftHandler}
	 * @param strandShift value of the shift to perform on the selected strand
	 * @param readLength length of the reads. 0 to keep the values from the input data file
	 */
	public ReadLengthAndShiftHandler(int strandShift, int readLength) {
		super();
		this.strandShift = strandShift;
		this.readLength = readLength;
	}

	
	/**
	 * @return the strand shift value
	 */
	public int getStrandShift() {
		return strandShift;
	}

	
	/**
	 * @return the read length value
	 */
	public int getReadLength() {
		return readLength;
	}
	
	
	/**
	 * Computes the start and stop position using the shift and the read length information
	 * @param chromo chromosome of the read
	 * @param start start of the read
	 * @param stop stop of the read
	 * @param strand strand of the read
	 * @return a {@link ChromosomeWindow} with the result start and stop positions
	 */
	public ChromosomeWindow computeStartStop(Chromosome chromo, int start, int stop, Strand strand) {
		if ((strand == null) || (chromo == null)) {
			// if the strand or chromosome parameter is null we return the value without modifying them
			return new ChromosomeWindow(start, stop);
		}		
		ChromosomeWindow resultPositions = new ChromosomeWindow();
		if (strand == Strand.FIVE) {
			// case where the read is on the 5' strand
			// we want to make sure that the result positions are not greater than the chromosome length
			start = Math.min(chromo.getLength(), start + strandShift);
			resultPositions.setStart(start);
			if (readLength == 0) {
				// case where we keep the original read length
				stop = Math.min(chromo.getLength(), stop + strandShift);
			} else {
				// case where we use the read length specified by the user
				stop = Math.min(chromo.getLength(), start + readLength);
			}
			resultPositions.setStop(stop);
		} else {
			// case where the read is on the 3' strand
			// we want to make sure that the result positions are not smaller than zero
			stop = Math.max(0, stop - strandShift);
			resultPositions.setStop(stop);	
			if (readLength == 0) {
				// case where we keep the original read length
				start = Math.max(0, start - strandShift);
							
			} else {
				// case where we use the read length specified by the user
				start = Math.max(0, stop - readLength);
			}
			resultPositions.setStart(start);
		}
		return resultPositions;		
	}
}
