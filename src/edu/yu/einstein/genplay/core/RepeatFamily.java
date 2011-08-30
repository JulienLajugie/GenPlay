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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The RepeatFamily class provides a representation of a family of repeats.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class RepeatFamily implements Serializable, Comparable<RepeatFamily> {

	private static final long serialVersionUID = -7691967168795920365L; // generated ID
	private String 						name;			// Name of the family of repeat
	private ArrayList<ChromosomeWindow> repeatList;		// 1 list of repeat per chromosome
	
	
	/**
	 * Creates an instance of {@link RepeatFamily}
	 * @param name name of the family
	 */
	public RepeatFamily(String name) {
		this.name = name; 
		repeatList = new ArrayList<ChromosomeWindow>();
	}
	
	
	/**
	 * @return the number of repeats
	 */
	public int repeatCount() {
		return repeatList.size();
	}
	
	
	/**
	 * Adds a repeat to the list
	 * @param repeat a repeat
	 */
	public void addRepeat(ChromosomeWindow repeat) {
		repeatList.add(repeat);
	}
	
	
	/**
	 * Returns the repeat at the specified position in this list. 
	 * @param index index of the repeat to return 
	 * @return the repeat at the specified position in this list 
	 */
	public ChromosomeWindow getRepeat(int index) {
		return repeatList.get(index);
	}
	
	
	/**
	 * @return the list of repeat
	 */
	public ArrayList<ChromosomeWindow> getRepeatList() {
		return repeatList;
	}
	
	
	/**
	 * @return the name of the family
	 */
	public String getName() {
		return name;
	}


	@Override
	public int compareTo(RepeatFamily otherRepeatFamily) {
		return this.getName().compareTo(otherRepeatFamily.getName());
	}
}
