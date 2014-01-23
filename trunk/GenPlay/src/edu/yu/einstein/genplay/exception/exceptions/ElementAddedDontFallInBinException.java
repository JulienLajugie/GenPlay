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

import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.bin.BinListView;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.bin.BinListViewBuilder;

/**
 * Exception thrown by {@link BinListViewBuilder} objects when the elements added to
 * the {@link BinListView} to be constructed don't correspond to a bin (start and stop position are not multiple of the bin size).
 * @author Julien Lajugie
 */
public class ElementAddedDontFallInBinException extends RuntimeException {
	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -44848949206424656L;


	/**
	 * Creates an instance of {@link ElementAddedDontFallInBinException}
	 */
	public ElementAddedDontFallInBinException() {
		super("The start and the stop position of the element to add should be a multiple of the bin size.");
	}


	/**
	 * Creates an instance of {@link ElementAddedDontFallInBinException}
	 * @param message message of the exception
	 */
	public ElementAddedDontFallInBinException(String message) {
		super(message);
	}
}
