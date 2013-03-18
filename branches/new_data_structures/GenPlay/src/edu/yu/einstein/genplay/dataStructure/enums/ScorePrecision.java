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
 * Enumeration of the different precisions for the scores of data in GenPlay
 * @author Julien Lajugie
 * @version 0.1
 */
public enum ScorePrecision {


	/**
	 * 16-bit floating point format
	 */
	PRECISION_16BIT ("16-Bit", "16-bit floating point format"),
	/**
	 * 32-bit floating point format
	 */
	PRECISION_32BIT ("32-Bit", "32-bit floating point format");


	private final String name; 			// name of the precision
	private final String description;	// description of the precision


	/**
	 * Private constructor. Creates an instance of {@link ScorePrecision}
	 * @param name name of the precision
	 * @param description
	 */
	private ScorePrecision(String name, String description) {
		this.name = name;
		this.description = description;
	}


	/**
	 * @return the description of the score precision
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @return the name of the score precision
	 */
	public String getName() {
		return name;
	}


	@Override
	public String toString() {
		return name;
	}
}
