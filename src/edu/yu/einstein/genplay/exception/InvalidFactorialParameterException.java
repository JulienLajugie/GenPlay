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
package edu.yu.einstein.genplay.exception;

/**
 * Exception thrown when the factorial parameter is inferior or equal to zero
 * @author Nicolas Fourel
 * @version 0.1
 */

public final class InvalidFactorialParameterException extends Exception {
	
	private static final long serialVersionUID = 6501319100070818381L;	// generated ID

	
	/**
	 * Creates an instance of {@link InvalidFactorialParameterException}
	 */
	public InvalidFactorialParameterException() {
		super("Factorial parameter cannot be negative.");
	}
	
	
	/**
	 * Creates an instance of {@link InvalidFactorialParameterException} 
	 * @param message the message of the exception
	 */
	public InvalidFactorialParameterException(String message) {
		super(message);
	}
}
