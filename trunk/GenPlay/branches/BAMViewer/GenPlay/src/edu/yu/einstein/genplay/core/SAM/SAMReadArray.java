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
package edu.yu.einstein.genplay.core.SAM;

import net.sf.samtools.SAMRecord;
import edu.yu.einstein.genplay.core.multiGenome.data.display.array.MGIntegerArray;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SAMReadArray {

	private final MGIntegerArray 		start;			// The array of start positions.
	private final MGIntegerArray 		stop;			// The array of stop positions.


	/**
	 * Constructor of {@link SAMReadArray}
	 */
	public SAMReadArray () {
		start = new MGIntegerArray();
		stop = new MGIntegerArray();
	}


	/**
	 * Add a {@link SAMRecord} at the specified index
	 * @param index the index where to add the {@link SAMRecord}
	 * @param record the {@link SAMRecord} to add
	 */
	public void add (int index, SAMRecord record) {
		start.set(index, record.getAlignmentStart());
		stop.set(index, record.getAlignmentEnd());
	}


	/**
	 * Resize the lists.
	 * Optional method, use it to save memory.
	 * @param size the new size for the lists.
	 */
	public void resize (int size) {
		start.resize(size);
		stop.resize(size);
	}


	/**
	 * @return the number of elements
	 */
	public int getSize () {
		return start.size();
	}


	/**
	 * @param index index of the start to get
	 * @return the start, -1 if it doesn't exist
	 */
	public int getStart(int index) {
		return getValue(start, index);
	}


	/**
	 * @param index index of the stop to get
	 * @return the stop, -1 if it doesn't exist
	 */
	public int getStop(int index) {
		return getValue(stop, index);
	}


	/**
	 * 
	 * @param list a {@link MGIntegerArray}
	 * @param index index of the value to get
	 * @return the value, -1 if it doesn't exist
	 */
	private int getValue (MGIntegerArray list, int index) {
		int value = list.get(index);
		if (value < 0) {
			value = -1;
		}
		return value;
	}


	/**
	 * @param start
	 * @return the index of the start value or the index right after if the exact value is not found
	 */
	public int getStartIndex (int start) {
		return this.start.getIndex(start);
	}


	/**
	 * @param stop
	 * @return the index of the stop value or the index right after if the exact value is not found
	 */
	public int getStopIndex (int stop) {
		return this.stop.getIndex(stop);
	}

}
