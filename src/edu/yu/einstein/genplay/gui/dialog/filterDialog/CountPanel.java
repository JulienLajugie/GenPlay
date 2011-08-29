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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.filterDialog;

import java.text.DecimalFormat;

import edu.yu.einstein.genplay.core.enums.FilterType;



/**
 * Filter panel for the count filters
 * @author Julien Lajugie
 * @version 0.1
 */
final class CountPanel extends FilterPanel {

	private static final long serialVersionUID = -6666939773683020846L;				// generated ID
	private final static String NAME = FilterType.COUNT.toString();					// name of the filter
	private final static String FILTER_DESCRIPTION = "Filter the X lowest values and the Y greatest values,\n" +
			"where X and Y are two specified integers";								// description of the filter
	private final static String TEXT_MIN = "Number of lowest values to filter";		// text of the min label
	private final static String TEXT_MAX = "Number of greatest values to filter";	// text of the max label
	private final static DecimalFormat 	DF = new DecimalFormat("0");				// decimal format for the input numbers
	private static Number 	defaultMin = 0;											// default/last min value
	private static Number 	defaultMax = 0;											// default/last max value
	private static boolean 	defaultIsSaturation = false;							// default/last saturation state
	
	
	/**
	 * Creates an instance of {@link CountPanel}
	 */
	CountPanel() {
		super(NAME, FILTER_DESCRIPTION, TEXT_MIN, TEXT_MAX, DF, defaultMin, defaultMax, defaultIsSaturation);
	}

	
	@Override
	boolean isInputValid() {
		return true;
	}


	@Override
	boolean isSaturable() {
		return true;
	}


	@Override
	void saveIsSaturation() {
		defaultIsSaturation = isSaturation();
	}


	@Override
	void saveMax() {
		defaultMax = getMaxInput();		
	}


	@Override
	void saveMin() {
		defaultMin = getMinInput();		
	}
}
