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
package edu.yu.einstein.genplay.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;

import edu.yu.einstein.genplay.exception.ChromosomeWindowException;


/**
 * The ChromosomeWindow class represents a window on a chromosome. 
 * @author Julien Lajugie
 * @version 0.1
 */
public class ChromosomeWindow implements Serializable, Cloneable, Comparable<ChromosomeWindow> {

	private static final long serialVersionUID = -6548181911063983578L; // generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private static final DecimalFormat POSITION_FORMAT = 
		new DecimalFormat("###,###,###"); // Format used for the toString() method
	private int  	start;		// Position start of the window
	private int 	stop;		// Position stop of the window

	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeInt(start);
		out.writeInt(stop);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		start = in.readInt();
		stop = in.readInt();
	}
	
	
	/**
	 * Default constructor. 
	 */
	public ChromosomeWindow() {
		super();
	}
	
	
	/**
	 * Creates an instance of {@link ChromosomeWindow}.
	 * @param start a window start
	 * @param stop a window stop
	 */
	public ChromosomeWindow(int start, int stop) {
		this.start = start;
		this.stop = stop;
	}
	
	
	/**
	 * Creates an instance of {@link ChromosomeWindow} having the same value than the specified {@link ChromosomeWindow}
	 * @param chromosomeWindow
	 */
	public ChromosomeWindow(ChromosomeWindow chromosomeWindow) {
		this.start = chromosomeWindow.start;
		this.stop = chromosomeWindow.stop;
	}
	
	
	/**
	 * Creates an instance of {@link ChromosomeWindow} from a String.
	 * @param windowStr String following the format "startpos-stoppos" (example: "100-500")
	 * @throws ChromosomeWindowException
	 */
	public ChromosomeWindow(String windowStr) throws ChromosomeWindowException {
		String startStr = windowStr.substring(0, windowStr.lastIndexOf("-"));
		String stopStr = windowStr.substring(windowStr.lastIndexOf("-") + 1);
		try {
			start = (POSITION_FORMAT.parse(startStr.trim())).intValue();
		} catch (ParseException e) {
			throw new ChromosomeWindowException("Invalid start position.");
		}
		try {
			stop = (POSITION_FORMAT.parse(stopStr.trim())).intValue();
		} catch (ParseException e) {
			throw new ChromosomeWindowException("Invalid stop position.");
		}
		if (start > stop) {
			throw new ChromosomeWindowException("Invalid window, the start position must be greatter than the stop position");
		}
	}
	
	
	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}
	
	
	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}
	
	
	/**
	 * @param stop the stop to set
	 */
	public void setStop(int stop) {
		this.stop = stop;
	}
	
	
	/**
	 * @return the stop
	 */
	public int getStop() {
		return stop;
	}
	

	@Override
	public String toString() {
		String startStr = POSITION_FORMAT.format(start);
		String stopStr = POSITION_FORMAT.format(stop);
		return startStr + "-" + stopStr;
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
		if (start != other.start) {
			return false;
		}
		if (stop != other.stop) {
			return false;
		}
		return true;
	}


	/**
	 * @return the size of the window in base pair (ie: stop - start)
	 */
	public int getSize() {
		return stop - start;
	}
	
	
	
	/**
	 * @return the position of the middle of the window
	 */
	public double getMiddlePosition() {
		return (start + stop) / (double)2;
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
}
