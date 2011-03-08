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
package yu.einstein.gdp2.core.DAS;

import yu.einstein.gdp2.core.enums.Strand;


/**
 * An Entry Point as described in the DAS 1.53 specifications:
 * <br/><a href="http://www.biodas.org/documents/spec.html">http://www.biodas.org/documents/spec.html</a>
 * @author Julien Lajugie
 * @version 0.1
 */
public class EntryPoint {
	private String 	ID;			// ID of the entry point
	private int 	start;		// start position of the entry point 
	private int 	stop;		// stop position of the entry point
	private Strand 	orientation;// orientation of the entry point
	
	
	/**
	 * @return the iD
	 */
	public final String getID() {
		return ID;
	}
	
	
	/**
	 * @param iD the iD to set
	 */
	public final void setID(String iD) {
		ID = iD;
	}
	
	
	/**
	 * @return the start
	 */
	public final int getStart() {
		return start;
	}
	
	
	/**
	 * @param start the start to set
	 */
	public final void setStart(int start) {
		this.start = start;
	}
	
	
	/**
	 * @return the stop
	 */
	public final int getStop() {
		return stop;
	}
	
	
	/**
	 * @param stop the stop to set
	 */
	public final void setStop(int stop) {
		this.stop = stop;
	}
	
	
	/**
	 * @return the orientation
	 */
	public final Strand getOrientation() {
		return orientation;
	}
	
	
	/**
	 * @param orientation the orientation to set
	 */
	public final void setOrientation(Strand orientation) {
		this.orientation = orientation;
	}
}
