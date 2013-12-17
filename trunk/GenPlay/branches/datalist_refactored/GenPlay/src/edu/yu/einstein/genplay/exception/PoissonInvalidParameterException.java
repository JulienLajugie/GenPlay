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
 * Exception thrown when a Poisson distribution is asked to be made from 
 * an invalid parameter. 
 * @author Alexander Golec
 * @version 0.1
 */
public final class PoissonInvalidParameterException extends Exception {
	
	private static final long serialVersionUID = -6091702295893962445L;	// generated ID

	
	/**
	 * Creates an instance of {@link PoissonInvalidParameterException}
	 */
	public PoissonInvalidParameterException() {
		super("Invalid parameter passed to Poisson distribution. ");
	}

	
	/**
	 * Creates an instance of {@link PoissonInvalidParameterException}
	 * @param message message of the exception
	 */
	public PoissonInvalidParameterException(String message) {
		super(message);
	}
}
