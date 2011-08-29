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

import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.enums.FilterType;



/**
 * Filter panel for the band-stop filters
 * @author Julien Lajugie
 * @version 0.1
 */
final class BandStopPanel extends FilterPanel {

	private static final long serialVersionUID = -8470118628769444167L;	// generated ID
	private final static String NAME = FilterType.BANDSTOP.toString();	// name of the filter
	private final static String FILTER_DESCRIPTION = 
		"Remove values between two specified threshold values\n";		// description of the filter
	private final static String TEXT_MIN = "Remove values between";		// text of the min label
	private final static String TEXT_MAX = "And";						// text of the max label
	private final static DecimalFormat DF = new DecimalFormat("0.0");	// decimal format for the input numbers
	private static Number 	defaultMin = 0;								// default/last min value
	private static Number 	defaultMax = 100;							// default/last max value
	
	
	/**
	 * Creates an instance of {@link BandStopPanel}
	 */
	BandStopPanel() {
		super(NAME, FILTER_DESCRIPTION, TEXT_MIN, TEXT_MAX, DF, defaultMin, defaultMax, false);
	}
	
	
	@Override
	boolean isInputValid() {
		double thresholdLow = getMinInput().doubleValue();
		double thresholdHigh = getMaxInput().doubleValue();
		if (thresholdHigh <= thresholdLow) {
			JOptionPane.showMessageDialog(getRootPane(), "The high threshold must be greater than the low one", "Error", JOptionPane.ERROR_MESSAGE, null);
			return false;
		} else {
			return true;
		}
	}


	@Override
	boolean isSaturable() {
		return false;
	}


	@Override
	void saveIsSaturation() {}


	@Override
	void saveMax() {
		defaultMax = getMaxInput();		
	}


	@Override
	void saveMin() {
		defaultMin = getMinInput();		
	}
}
