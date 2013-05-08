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

import java.text.NumberFormat;

import edu.yu.einstein.genplay.core.comparator.ChromosomeWindowComparator;
import edu.yu.einstein.genplay.util.HashCodeUtil;

/**
 * This class provides a skeletal implementation of the {@link ChromosomeWindow} interface
 * to minimize the effort required to implement this interface.
 * @author Julien Lajugie
 */
public abstract class AbstractChromosomeWindow implements ChromosomeWindow{

	/** Generated serial ID */
	private static final long serialVersionUID = -3169498307618918378L;


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
		if (position < getStart()) {
			return -1;
		} else if (position > getStop()) {
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
		ChromosomeWindow other = (ChromosomeWindow) obj;
		if (getStart() != other.getStart()) {
			return false;
		}
		if (getStop() != other.getStop()) {
			return false;
		}
		return true;
	}


	/**
	 * @return the position of the middle of the window
	 */
	@Override
	public double getMiddlePosition() {
		return (getStart() + getStop()) / (double)2;
	}


	/**
	 * @return the size of the window in base pair (ie: stop - start)
	 */
	@Override
	public int getSize() {
		return getStop() - getStart();
	}


	@Override
	public int hashCode() {
		int hashCode = HashCodeUtil.SEED;
		hashCode = HashCodeUtil.hash(hashCode, getStart());
		hashCode = HashCodeUtil.hash(hashCode, getStop());
		return hashCode;
	}


	@Override
	public String toString() {
		String startStr = NumberFormat.getInstance().format(getStart());
		String stopStr = NumberFormat.getInstance().format(getStop());
		return startStr + "-" + stopStr;
	}
}
