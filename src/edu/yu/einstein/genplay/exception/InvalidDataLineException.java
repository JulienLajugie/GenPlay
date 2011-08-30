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
 * The InvalidDataLineException class is thrown when an extractor can't extract a line. 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class InvalidDataLineException extends Exception {

	private static final long serialVersionUID = 7000180996789501289L;	// generated ID
	
	
	/**
	 * Creates an instance of {@link InvalidDataLineException}
	 * @param line the invalid line
	 */
	public InvalidDataLineException(String line) {
		super("Invalid data line: \"" + line + "\"");
	}
}
