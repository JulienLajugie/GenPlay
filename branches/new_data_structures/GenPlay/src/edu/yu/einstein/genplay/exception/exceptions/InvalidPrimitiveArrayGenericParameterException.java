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
package edu.yu.einstein.genplay.exception.exceptions;

import edu.yu.einstein.genplay.dataStructure.list.primitiveList.PrimitiveList;

/**
 * Invalid generic parameter of a {@link PrimitiveList} object.
 * @author Julien Lajugie
 */
public class InvalidPrimitiveArrayGenericParameterException extends RuntimeException {

	/**
	 * Generated ID
	 */
	private static final long serialVersionUID = -65479207030623021L;

	/**
	 * Constructor.
	 * @param genericParameterType type of the generic parameter
	 * @param <T> type of the generic parameter
	 */
	public <T> InvalidPrimitiveArrayGenericParameterException(Class<T> genericParameterType) {
		super(new String("Invalid generic parameter: " + genericParameterType.getName()));
	}
}
