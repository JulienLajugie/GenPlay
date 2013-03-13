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
 * A type for inequalities
 * @author Nicolas Fourel
 * @version 0.1
 */
public enum InequalityOperators {
	

	/**
	 * Symbol for the equality
	 */
	EQUAL ("="),
	
	/**
	 * Symbol for the difference
	 */
	DIFFERENT ("!="),
	
	/**
	 * Symbol for the superior or equal inequality
	 */
	SUPERIOR_OR_EQUAL (">="),
	
	/**
	 * Symbol for the superior inequality
	 */
	SUPERIOR (">"),
	
	/**
	 * Symbol for the inferior or equal inequality
	 */
	INFERIOR_OR_EQUAL ("<="),
	
	/**
	 * Symbol for the inferior inequality
	 */
	INFERIOR ("<");
	
	
	
	private final String name; // String representing the inequality 
	
	
	/**
	 * Private constructor. Creates an instance of {@link InequalityOperators}
	 * @param name
	 */
	private InequalityOperators(String name) {
		this.name = name;
	}
	
	
	@Override
	public String toString() {
		return name;
	}
}
