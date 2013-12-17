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
package edu.yu.einstein.genplay.dataStructure.enums;


/**
 * A type of island result
 * @author Nicolas Fourel
 * @version 0.1
 */
public enum IslandResultType {
	
	/**
	 * Value will be the original value of a window
	 */	
	FILTERED ("Filtered"),
	/**
	 * Value will be the score of an island
	 */
	IFSCORE ("Island Finder Score"),
	/**
	 * Value will be the maximum of the island
	 */	
	SUMMIT ("Island Summit");	
	
	
	private final String name; // String representing the filter 
	
	
	/**
	 * Private constructor. Creates an instance of {@link IslandResultType}
	 * @param name
	 */
	private IslandResultType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
