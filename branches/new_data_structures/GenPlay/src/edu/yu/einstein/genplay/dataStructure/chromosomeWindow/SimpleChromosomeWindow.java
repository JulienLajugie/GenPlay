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
package edu.yu.einstein.genplay.dataStructure.chromosomeWindow;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;

import edu.yu.einstein.genplay.core.comparator.ChromosomeWindowComparator;
import edu.yu.einstein.genplay.exception.exceptions.ChromosomeWindowException;
import edu.yu.einstein.genplay.util.HashCodeUtil;


/**
 * A simple implementation of the {@link ChromosomeWindow} interface.
 * {@link SimpleChromosomeWindow} objects are immutable.
 * @author Julien Lajugie
 */
public final class SimpleChromosomeWindow implements ChromosomeWindow, Serializable, Comparable<ChromosomeWindow> {

	/**  Generated serial ID */
	private static final long serialVersionUID = -6548181911063983578L;

	/**  Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Start position of the window */
	private final int start;

	/** Stop position of the window */
	private final int stop;


	/**
	 * Creates an instance of {@link SimpleChromosomeWindow} having the same value than the specified {@link SimpleChromosomeWindow}
	 * @param chromosomeWindow
	 */
	public SimpleChromosomeWindow(ChromosomeWindow chromosomeWindow) {
		start = chromosomeWindow.getStart();
		stop = chromosomeWindow.getStop();
	}


	/**
	 * Creates an instance of {@link SimpleChromosomeWindow}.
	 * @param start a window start
	 * @param stop a window stop
	 */
	public SimpleChromosomeWindow(int start, int stop) {
		this.start = start;
		this.stop = stop;
	}


	/**
	 * Creates an instance of {@link SimpleChromosomeWindow} from a String.
	 * @param windowStr String following the format "startpos-stoppos" (example: "100-500")
	 * @throws ChromosomeWindowException
	 */
	public SimpleChromosomeWindow(String windowStr) throws ChromosomeWindowException {
		String startStr = windowStr.substring(0, windowStr.lastIndexOf("-"));
		String stopStr = windowStr.substring(windowStr.lastIndexOf("-") + 1);
		try {
			start = (NumberFormat.getInstance().parse(startStr.trim())).intValue();
		} catch (ParseException e) {
			throw new ChromosomeWindowException("Invalid start position.");
		}
		try {
			stop = (NumberFormat.getInstance().parse(stopStr.trim())).intValue();
		} catch (ParseException e) {
			throw new ChromosomeWindowException("Invalid stop position.");
		}
		if (start > stop) {
			throw new ChromosomeWindowException("Invalid window, the start position must be greatter than the stop position");
		}
	}


	/**
	 * A ChromosomeWindow is greater to another one if its position start is greater
	 * or if its position start is equal but its position stop is greater.
	 * A {@link ChromosomeWindow} is equal to another one if both its start and stop positions are equals.
	 * A ChromosomeWindow is smaller to another one if its position start is smaller
	 * or if its position start is equal but its position stop is smaller.
	 */
	@Override
	public int compareTo(ChromosomeWindow otherChromosomeWindow) {
		return new ChromosomeWindowComparator().compare(this, otherChromosomeWindow);
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
		SimpleChromosomeWindow other = (SimpleChromosomeWindow) obj;
		if (start != other.start) {
			return false;
		}
		if (stop != other.stop) {
			return false;
		}
		return true;
	}


	/**
	 * @return the position of the middle of the window
	 */
	@Override
	public double getMiddlePosition() {
		return (start + stop) / (double)2;
	}


	/**
	 * @return the size of the window in base pair (ie: stop - start)
	 */
	@Override
	public int getSize() {
		return stop - start;
	}


	/**
	 * @return the start
	 */
	@Override
	public int getStart() {
		return start;
	}


	/**
	 * @return the stop
	 */
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
		// read the version number of the object
		in.readInt();
		// read the final fields
		in.defaultReadObject();
	}


	@Override
	public String toString() {
		String startStr = NumberFormat.getInstance().format(start);
		String stopStr = NumberFormat.getInstance().format(stop);
		return startStr + "-" + stopStr;
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		// write the format version number of the object
		out.writeInt(CLASS_VERSION_NUMBER);
		// write the final fields
		out.defaultWriteObject();
	}
}
