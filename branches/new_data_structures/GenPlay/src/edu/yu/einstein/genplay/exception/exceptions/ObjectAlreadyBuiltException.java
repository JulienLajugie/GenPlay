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

import java.io.Serializable;

import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;

/**
 * Thrown when an application attempts to add an element to a {@link ListView} builder after the {@link ListView} has been built.
 * @author Julien Lajugie
 */
public class ObjectAlreadyBuiltException extends RuntimeException implements Serializable {

	/** Generated serial ID */
	private static final long serialVersionUID = 2210337586653431042L;


	/**
	 * Creates an instance of {@link ObjectAlreadyBuiltException} with no detail message.
	 */
	public ObjectAlreadyBuiltException() {
		super();
	}


	/**
	 * Create an instance of {@link ObjectAlreadyBuiltException} with the specified detail message.
	 * @param message
	 */
	public ObjectAlreadyBuiltException(String message) {
		super(message);
	}
}
