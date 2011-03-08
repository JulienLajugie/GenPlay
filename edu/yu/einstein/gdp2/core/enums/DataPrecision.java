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
package yu.einstein.gdp2.core.enums;

import yu.einstein.gdp2.core.list.binList.BinList;


/**
 * Enumeration representing a precision of data for a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public enum DataPrecision {
	
	PRECISION_1BIT ("1-Bit"),
	PRECISION_8BIT ("8-Bit"),
	PRECISION_16BIT ("16-Bit"),
	PRECISION_32BIT ("32-Bit"),
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
