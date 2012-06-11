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
package edu.yu.einstein.genplay.core.enums;

import edu.yu.einstein.genplay.core.list.binList.BinList;


/**
 * Enumeration representing a method for the calculation of the scores of a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public enum ScoreCalculationMethod {

	/**
	 * Compute the average
	 */
	AVERAGE ("Average"),
	/**
	 * Compute the maximum
	 */
	MAXIMUM ("Maximum"),
	/**
	 * Compute the sum
	 */
	SUM ("Sum");
	
	private final String name;	// name of the method of score calculation
	
	/**
	 * Private constructor. Creates an instance of a {@link ScoreCalculationMethod}
	 * @param name name of the method of score calculation
	 */
	private ScoreCalculationMethod(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
