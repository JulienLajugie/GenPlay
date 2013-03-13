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
package edu.yu.einstein.genplay.dataStructure.list.arrayList;

import java.util.List;

import edu.yu.einstein.genplay.dataStructure.enums.DataPrecision;


/**
 * Factory creating a subtype of {@link List} of double
 * @author Julien Lajugie
 * @version 0.1
 */
public class ListFactory {

	/**
	 * @param precision precision of the data
	 * @param size size of the list
	 * @return a subtype of List<Double>
	 * @throws IllegalArgumentException thrown if the precision is not valid
	 */
	public static List<Double> createList(DataPrecision precision, int size) throws IllegalArgumentException {
		switch (precision) {
		case PRECISION_1BIT:
			return new BooleanArrayAsDoubleList(size);
		case PRECISION_8BIT:
			return new ByteArrayAsDoubleList(size);
		case PRECISION_16BIT:
			return new ShortArrayAsDoubleList(size);
		case PRECISION_32BIT:
			return new FloatArrayAsDoubleList(size);
		case PRECISION_64BIT:
			return new DoubleArrayAsDoubleList(size);
		default: 
			throw new IllegalArgumentException("invalid precision");
		}				
	}
}
