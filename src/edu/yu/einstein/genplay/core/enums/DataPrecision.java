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
 * Enumeration representing a precision of data for a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public enum DataPrecision {
	
	/**
	 * 1-BIT data
	 */
	PRECISION_1BIT ("1-Bit"),
	/**
	 * 8-BIT data
	 */
	PRECISION_8BIT ("8-Bit"),
	/**
	 * 16-BIT data
	 */
	PRECISION_16BIT ("16-Bit"),
	/**
	 * 32-BIT data
	 */
	PRECISION_32BIT ("32-Bit"),
	/**
	 * 64-BIT data
	 */
	PRECISION_64BIT ("64-Bit");
	
	private final String name; // name of the precision
	
	
	/**
	 * Private constructor. Creates an instance of {@link DataPrecision}
	 * @param name name of the precision
	 */
	private DataPrecision(String name) {
		this.name = name;
	}
	
	
	public String toString() {
		return name;
	}
}
