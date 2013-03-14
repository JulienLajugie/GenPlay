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
package edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.NumberFormat;

import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.util.HashCodeUtil;


/**
 * Implementation of a {@link ScoredChromosomeWindow} with a score always equal to one.
 * {@link MaskChromosomeWindow} objects are immutable.
 * @author Julien Lajugie
 */
public final class MaskChromosomeWindow implements ScoredChromosomeWindow, ChromosomeWindow, Serializable, Cloneable, Comparable<ChromosomeWindow> {

	/** Generated serial ID */
	private static final long serialVersionUID = 8073707507054963197L;

	/**  Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** start position of the window */
	private final int start;

	/** stop position of the window */
	private final int stop;


	/**
	 * Creates an instance of a {@link MaskChromosomeWindow}
	 * @param start start position of the window
	 * @param stop stop position of the window
	 */
	public MaskChromosomeWindow(int start, int stop) {
		this.start = start;
		this.stop = stop;
	}


	/**
	 * Creates an instance of a {@link MaskChromosomeWindow} having the same
	 * start and stop values as the specified {@link ScoredChromosomeWindow}
	 * @param scw a {@link ScoredChromosomeWindow}
	 */
	public MaskChromosomeWindow(ScoredChromosomeWindow scw) {
		start = scw.getStart();
		stop = scw.getStop();
	}


	/**
	 * A ChromosomeWindow is superior to another one if its position start is greater
	 * or if its position start is equal but its position stop is greater.
	 */
	@Override
	public int compareTo(ChromosomeWindow otherChromosomeWindow) {
		if (start > otherChromosomeWindow.getStart()) {
			return 1;
		} else if (start < otherChromosomeWindow.getStart()) {
			return -1;
		} else {
			if (stop > otherChromosomeWindow.getStop()) {
				return 1;
			} else if (stop < otherChromosomeWindow.getStop()) {
				return -1;
			} else {
				return 0;
			}
		}
	}


	/**
	 * Checks if the window contains the given position.
	 * If the position is located before the window, -1 is returned.
	 * If the position is located after the window, 1 is returned.
	 * if the position is included in the window, 0 is returned.
	 * @param position the position to check
	 * @return 0 is the position is in the window, -1 if lower, 1 if higher.
	 */
	@Override
	public int containsPosition (int position) {
		if (position < start) {
			return -1;
		} else if (position > stop) {
			return 1;
		}
		return 0;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MaskChromosomeWindow other = (MaskChromosomeWindow) obj;
		if (start != other.start) {
			return false;
		}
		if (stop != other.stop) {
			return false;
		}
		return true;
	}


	@Override
	public double getMiddlePosition() {
		return (start + stop) / (double)2;
	}


	@Override
	public double getScore() {
		return 1;
	}


	@Override
	public int getSize() {
		return stop - start;
	}


	@Override
	public int getStart() {
		return start;
	}


	@Override
	public int getStop() {
		return stop;
	}


	@Override
	public int hashCode() {
		int hashCode = HashCodeUtil.SEED;
		hashCode = HashCodeUtil.hash(hashCode, start);
		hashCode = HashCodeUtil.hash(hashCode, stop);
		return hashCode;
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		// read the final fields
		in.defaultReadObject();
		// read the version number of the object
		in.readInt();
	}


	@Override
	public String toString() {
		String startStr = NumberFormat.getInstance().format(start);
		String stopStr = NumberFormat.getInstance().format(stop);
		return startStr + "-" + stopStr + " : 1";
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		// write the final fields
		out.defaultWriteObject();
		// write the format version number of the object
		out.writeInt(CLASS_VERSION_NUMBER);
	}
}
