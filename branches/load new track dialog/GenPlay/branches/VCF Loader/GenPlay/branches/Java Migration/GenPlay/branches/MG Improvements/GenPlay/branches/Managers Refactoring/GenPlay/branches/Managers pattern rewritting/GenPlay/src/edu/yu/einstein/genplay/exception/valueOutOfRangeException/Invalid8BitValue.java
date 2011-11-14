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
package edu.yu.einstein.genplay.exception.valueOutOfRangeException;

import java.text.DecimalFormat;

import edu.yu.einstein.genplay.core.list.arrayList.ByteArrayAsDoubleList;



/**
 * {@link RuntimeException} thrown when a value is out of the range of a 8Bit data type
 * @author Julien Lajugie
 * @version 0.1
 */
public class Invalid8BitValue extends ValueOutOfRangeException {

	private static final long serialVersionUID = -7111260909692592549L; // generated ID

	
	/**
	 * Creates an instance of {@link Invalid8BitValue}
	 * @param data the data that is out of range
	 */
	public Invalid8BitValue(Double data) {
		super("Invalid Data (score = " + new DecimalFormat("#.#").format(data) + "). A 8Bit value must be between " + ByteArrayAsDoubleList.MIN_VALUE + " and " + ByteArrayAsDoubleList.MAX_VALUE);
	}
}
