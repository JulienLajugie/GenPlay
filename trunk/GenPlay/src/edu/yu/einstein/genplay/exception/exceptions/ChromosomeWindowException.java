/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.exception.exceptions;

import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;

/**
 * The ChromosomeWindowException class represents an exception associated to a {@link SimpleChromosomeWindow}.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ChromosomeWindowException extends Exception {

	private static final long serialVersionUID = -4357641195693048950L;	// Generated ID

	/**
	 * Creates an instance of {@link ChromosomeWindowException}.
	 */
	public ChromosomeWindowException() {
		super("Invalid window");
	}


	/**
	 * Creates an instance of {@link ChromosomeWindowException}.
	 * @param msg message of the error
	 */
	public ChromosomeWindowException(String msg) {
		super (msg);
	}
}
