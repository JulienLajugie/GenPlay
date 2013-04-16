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

import edu.yu.einstein.genplay.util.HashCodeUtil;
import edu.yu.einstein.genplay.util.Utils;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SAMRead {

	private int hashcode;
	private int start;
	private int stop;
	private SAMRead pair;


	/**
	 * Constructor of {@link SAMRead}
	 * @param start the start position
	 * @param stop the stop position
	 */
	public SAMRead (int start, int stop) {
		hashcode = 0;
		initialize(start, stop, null);
	}


	/**
	 * Constructor of {@link SAMRead}
	 * @param start the start position
	 * @param stop the stop position
	 * @param pair the paired {@link SAMRead}
	 */
	public SAMRead (int start, int stop, SAMRead pair) {
		initialize(start, stop, pair);
	}


	/**
	 * Initialize the {@link SAMRead}
	 * @param start the start position
	 * @param stop the stop position
	 * @param pair the paired {@link SAMRead}
	 */
	private void initialize (int start, int stop, SAMRead pair) {
		this.start = start;
		this.stop = stop;
		this.pair = pair;
	}


	/**
	 * @return true if the {@link SAMRead} is paired, false otherwise
	 */
	public boolean isPaired () {
		return pair != null;
	}


	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}


	/**
	 * @return the stop
	 */
	public int getStop() {
		return stop;
	}


	/**
	 * @return the highest stop (also looking at the paired read if exists)
	 */
	public int getFullAlignementStop() {
		if (isPaired()) {
			return Math.max(stop, pair.getStop());
		}
		return stop;
	}


	/**
	 * @return the length
	 */
	public int getLength () {
		return stop - start;
	}


	/**
	 * @return the pair
	 */
	public SAMRead getPair() {
		return pair;
	}


	/**
	 * @param start a start position
	 * @param stop a stop position
	 * @return true if the read is included in the given positions, false otherwise
	 */
	public boolean isContained (int start, int stop) {
		if ((this.start <= stop) && (getFullAlignementStop() >= start)) {
			return true;
		}
		return false;
	}


	/**
	 * @param position a position
	 * @return true if the read includes the given position, false otherwise
	 */
	public boolean contains (int position) {
		if ((position >= start) && (position <= getFullAlignementStop())) {
			return true;
		}
		return false;
	}


	/**
	 * A {@link SAMRead} is valid if all its position make sense.
	 * @return true if the read is valid, false otherwise
	 */
	public boolean isValid () {
		boolean valid = true;
		if (start > stop) {
			valid = false;
		}
		if (valid && isPaired()) {
			if (getPair().getStart() > getPair().getStop()) {
				valid = false;
			}
		}
		return valid;
	}


	/**
	 * A read can overlap with its paired read
	 * @return true if paired reads overlap, false otherwise
	 */
	public boolean overlap () {
		boolean overlap = false;
		if (isPaired()) {
			if (stop >= getPair().getStart()) {
				overlap = true;
			}
		}
		return overlap;
	}


	@Override
	public String toString () {
		String s = "";
		s += "Read 1: " + start + " - " + stop;
		if (isPaired()) {
			s += "; Read 2: " + pair.getStart() + " - " + pair.getStop();
		}
		return s;
	}


	@Override
	public boolean equals(Object obj) {
		if(this == obj){
			return true;
		}
		if((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		// object must be Test at this point
		SAMRead test = (SAMRead)obj;
		boolean valid = (start == test.getStart()) && (stop == test.getStop()) && (isPaired() == test.isPaired());
		if (valid && isPaired()) {
			valid = pair.equals(test.getPair());
		}

		return valid;
	}


	@Override
	public int hashCode() {
		if (hashcode == 0) {
			int result = HashCodeUtil.SEED;
			result = HashCodeUtil.hash(result, start);
			result = HashCodeUtil.hash(result, stop);
			result = HashCodeUtil.hash(result, pair);
			result = HashCodeUtil.hash(result, Utils.getRandomValue(1, 1000000));
			hashcode = result;
		}
		return hashcode;
	}

}
