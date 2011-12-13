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
package edu.yu.einstein.genplay.gui.dialog.newCurveTrackDialog;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * Panel for the track name input of a {@link NewCurveTrackDialog}
 * @author Julien Lajugie
 * @version 0.1
 */
class TrackNamePanel extends JPanel {	
	
	private static final long serialVersionUID = -5969101278574088008L;	// generated ID
	private final JTextField jtfTrackName;	// text field for the track name
	

	/**
	 * Creates an instance of a {@link TrackNamePanel}
	 * @param trackName default name of a track
	 */
	TrackNamePanel(String trackName) {
		super();
		jtfTrackName = new JTextField(trackName);
		jtfTrackName.setColumns(15);
		add(jtfTrackName);
		setBorder(BorderFactory.createTitledBorder("Track Name"));
	}
	

	/**
	 * @return the name inside the input box
	 */
	String getTrackName() {
		return jtfTrackName.getText();
	}
}
