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
 * Enumeration of the different type of operation with constant
 * @author Julien Lajugie
 */
public enum OperationWithConstant {

	/** Add a constant */
	ADDITION ("Addition", "<html>Apply the following function f for each score x of the selected layer:<br>f(x) = x + constant</html>"),

	/** Subtract a constant */
	SUBTRACTION ("Subtraction", "<html>Apply the following function f for each score x of the selected layer:<br>f(x) = x - constant</html>"),

	/** Multiply by a constant */
	MULTIPLICATION ("Multiplication", "<html>Apply the following function f for each score x of the selected layer:<br>f(x) = x * constant</html>"),

	/** Divide by a constant */
	DIVISION ("Division", "<html>Apply the following function f for each score x of the selected layer:<br>f(x) = x / constant</html>"),

	/** Invert */
	INVERTION ("Invertion", "<html>Apply the following function f for each score x of the selected layer:<br>f(x) = constant / x</html>"),

	/** Set a unique score */
	UNIQUE_SCORE ("Unique Score", "<html>Apply the following function f for each score x of the selected layer:<br>f(x) = constant</html>");

	private final 	String name;			// name of the operation
	private final	String description;		// description of the operation


	/**
	 * Creates an instance of {@link OperationWithConstant}
	 * @param name
	 * @param description
	 */
	private OperationWithConstant(String name, String description) {
		this.name = name;
		this.description = description;
	}


	/**
	 * @return the description of the operation
	 */
	public String getDescription() {
		return description;
	}


	@Override
	public String toString() {
		return name;
	}
}
