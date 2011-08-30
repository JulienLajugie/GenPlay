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
 * The InvalidChromosomeException class is thrown when a Chromosome is not valid. 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class InvalidChromosomeException extends RuntimeException {

	private static final long serialVersionUID = -2244843030262784715L;	// Generated ID
	
	
	/**
	 * Creates an instance of {@link InvalidChromosomeException}
	 */
	public InvalidChromosomeException() {
		super("Invalid chromosome");
	}
}
