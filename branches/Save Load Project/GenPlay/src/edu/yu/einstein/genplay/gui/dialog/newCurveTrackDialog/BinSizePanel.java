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
package edu.yu.einstein.genplay.gui.dialog.newCurveTrackDialog;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;


/**
 * Panel of a {@link NewCurveTrackDialog} with an input box for the bin size
 * @author Julien Lajugie
 * @version 0.1
 */
class BinSizePanel extends JPanel {

	private static final long serialVersionUID = -7359118518250220846L;	// generated ID
	private static final int 	MAX_BINSIZE = Integer.MAX_VALUE;		// maximum bin size
	private static final int 	SPINNER_STEP = 100; 					// step of the spinner
	private final JSpinner 		jsBinSize; 								// spinner for the binsize input
	private static int 			defaultBinSize = 1000; 					// default binsize
	
	
	/**
	 * Creates an instance of {@link BinSizePanel}
	 */
	BinSizePanel() {
		super();
		SpinnerNumberModel snm = new SpinnerNumberModel(defaultBinSize, 1, MAX_BINSIZE, SPINNER_STEP);
		jsBinSize = new JSpinner(snm);
		add(jsBinSize);
		setBorder(BorderFactory.createTitledBorder("Window Size"));
	}

	
	/**
	 * @return the selected binsize
	 */
	int getBinSize() {
		return (Integer) jsBinSize.getValue();
	}
	
	
	/**
	 * Saves the selected bin size as default
	 */
	void saveDefault() {
		defaultBinSize = getBinSize();
	}
}
