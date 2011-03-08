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
package yu.einstein.gdp2.gui.statusBar;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JProgressBar;

/**
 * Progress bar of the {@link StatusBar}
 * @author Julien Lajugie
 * @version 0.1
 */
final class ProgressBar extends JProgressBar {

	private static final long serialVersionUID = -3669001086333207235L; // generated ID
	private static final Color 	BACKGROUND_COLOR = Color.white; // color of the background of the progressbar
	private static final int 	HEIGHT = 15; 					// height of the progress bar
	
	
	/**
	 * Creates an instance of {@link ProgressBar}
	 */
	ProgressBar() {
		super();
		// the progression a percentage between 0 and 100
		setMinimum(0);
		setMaximum(100);
		setMinimumSize(new Dimension(getPreferredSize().width, HEIGHT));
		setBackground(BACKGROUND_COLOR);
		setStringPainted(true);
	}


	/**
	 * Sets the level of completion showed on the {@link ProgressBar}
	 * @param progress
	 */
	synchronized void setProgress(int progress) {
		setIndeterminate(false);
		// set the progression position
		setValue(progress);
		// set the text on the progress bar
		setString(progress + "%");
	}

	
	/**
	 * Sets the progress bar indeterminate and don't print the string 
	 * on the progress bar if the parameter is true
	 */
	@Override
	public void setIndeterminate(boolean b) {
		firePropertyChange("indeterminate", isIndeterminate(), b);
		super.setIndeterminate(b);
		setStringPainted(!b);
	}
}
