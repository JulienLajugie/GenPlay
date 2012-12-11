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
package edu.yu.einstein.genplay.gui.old.action.allTrack;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.gui.dialog.NumberOptionPane;
import edu.yu.einstein.genplay.gui.old.action.TrackListAction;
import edu.yu.einstein.genplay.gui.old.track.Track;



/**
 * Sets the height of the the selected track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ATASetHeight extends TrackListAction {

	private static final long serialVersionUID = 9169503160914933578L; 					// generated ID
	private static final String ACTION_NAME = "Set Height"; 							// action name
	private static final String DESCRIPTION = "Set the height of the selected track"; 	// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_H; 								// mnemonic key
	

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ATASetHeight";


	/**
	 * Creates an instance of {@link ATASetHeight}
	 */
	public ATASetHeight() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	/**
	 * Sets the height of the the selected track
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Track<?> selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			int minimumHeight = selectedTrack.getMinimumSize().height;
			int currentHeight = selectedTrack.getSize().height;
			Number newPreferred = NumberOptionPane.getValue(getRootPane(), "Default Height", "Enter a new default size for the selected tracks:", new DecimalFormat("#"), minimumHeight, 500d, currentHeight);
			if (newPreferred != null) {
				selectedTrack.setPreferredHeight(newPreferred.intValue());
			}	
		}
	}
}
