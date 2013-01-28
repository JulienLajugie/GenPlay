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
package edu.yu.einstein.genplay.gui.dialog.filterDialog;

import java.text.NumberFormat;

import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.enums.FilterType;



/**
 * Filter panel for the threshold filter
 * @author Julien Lajugie
 * @version 0.1
 */
final class ThresholdPanel extends FilterPanel {

	private static final long serialVersionUID = 7419403825735753325L;	// generated ID
	private final static String NAME = FilterType.THRESHOLD.toString();	// name of the filter
	private final static String FILTER_DESCRIPTION = "Filter (discard) the values lower than X OR greater than Y,\n" +
			"where X and Y are two specified threshold values.";			// description of the filter
	private final static String TEXT_MIN = "Filter values lower than:";	// text of the min label
	private final static String TEXT_MAX = "Filter values greater than:";// text of the max label
	private static Number 	defaultMin = Double.NEGATIVE_INFINITY;		// default/last min value
	private static Number 	defaultMax = Double.POSITIVE_INFINITY;		// default/last max value
	private static boolean 	defaultIsSaturation = false;				// default/last saturation state


	/**
	 * Creates an instance of {@link ThresholdPanel}
	 */
	ThresholdPanel() {
		super(NAME, FILTER_DESCRIPTION, TEXT_MIN, TEXT_MAX, NumberFormat.getInstance(), defaultMin, defaultMax, defaultIsSaturation);
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
