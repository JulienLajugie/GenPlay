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


/**
 * Enumeration representing the different base available for the logarithm operations
 * @author Julien Lajugie
 * @version 0.1
 */
public enum LogBase {
	
	/**
	 * Binary logarithm
	 */
	BASE_2 ("Binary Logarithm (base 2)", 2d),
	/**
	 * Exponential logarithm
	 */
	BASE_E ("Natural Logarithm (base e)", Math.E),
	/**
	 * Decimal logarithm 
	 */
	BASE_10 ("Common Logaritm (base 10)", 10d);
	
	
	private final String name;		// name of the base
	private final double baseValue;	// value of the base
	
	
	/**
	 * Privates constructor. Creates an instance of a {@link LogBase}
	 * @param name name of the logarithm
	 * @param baseValue value of the base
	 */
	private LogBase(String name, Double baseValue) {
		this.name = name;
		this.baseValue = baseValue;
	}
	
	
	/**
	 * @return the name of the logarithm
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * @return the value of the base
	 */
	public double getValue() {
		return baseValue;
	}
	
	
	@Override
	public String toString() {
		return name;
	}
}
