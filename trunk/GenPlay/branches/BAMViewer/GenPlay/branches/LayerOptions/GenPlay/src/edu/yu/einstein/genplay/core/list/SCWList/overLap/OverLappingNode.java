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
package edu.yu.einstein.genplay.core.list.SCWList.overLap;

import java.io.Serializable;

import edu.yu.einstein.genplay.core.chromosomeWindow.ScoredChromosomeWindow;


/**
 * An overlap node is node on an overlapping region
 * A node is composed of a scored chromosome window and a boolean to know if it refers to a start or stop position.
 * 
 * @author Nicolas
 * @version 0.1
 */
final class OverLappingNode implements Comparable<OverLappingNode>, Serializable {

	private static final long serialVersionUID = 2744418236142472607L;
	private final boolean 				start;	//the node refer to a start position
	private final ScoredChromosomeWindow 	scw;	//scored chromosome window

	/**
	 * OverLapNode constructor
	 * 
	 * @param start	true if the node refers to a start position
	 * @param scw	scored chromosome window associated
	 */
	protected OverLappingNode(boolean start, ScoredChromosomeWindow scw) {
		this.start = start;
		this.scw = scw;
	}

	/**
	 * isStart method
	 * This method allows to know if the node refers to a start or a stop position.
	 * 
	 * @return	start boolean value
	 */
	protected boolean isStart() {
		return this.start;
	}

	/**
	 * getScw method
	 * 
	 * @return the scored chromosome window of the node
	 */
	protected ScoredChromosomeWindow getScw() {
		return scw;
	}

	/**
	 * getValue method
	 * This method return the position of the node.
	 * If the node refers to a start, the start position of the scored chromosome window is returned.
	 * If it refers to a stop, the stop position of the scored chromosome window is returned.
	 * 
	 * @return position associated
	 */
	protected int getValue () {
		if (this.start) {
			return this.scw.getStart();
		} else {
			return this.scw.getStop();
		}
	}

	@Override
	public int compareTo(OverLappingNode arg) {
		if (this.getValue() > arg.getValue()) {
			return 1;
		} else if (this.getValue() == arg.getValue()) {
			return 0;
		} else {
			return -1;
		}
	}

}
