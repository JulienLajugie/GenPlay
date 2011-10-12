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
package edu.yu.einstein.genplay.gui.action.allTrack;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.track.Track;



/**
 * Renames the selected track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ATARename extends TrackListAction {

	private static final long serialVersionUID = -6475180772964541278L; 	// generated ID
	private static final String ACTION_NAME = "Rename"; 					// action name
	private static final String DESCRIPTION = "Rename the selected track"; 	// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_R; 					// mnemonic key
	
	
	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ATARename";


	/**
	 * Creates an instance of {@link ATARename}
	 */
	public ATARename() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	/**
	 * Renames the selected track
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Track<?> selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			String message = "Enter a name for the track #" + selectedTrack.getTrackNumber() + ":";
			String currentName = selectedTrack.getName();
			String newName = (String) JOptionPane.showInputDialog(getRootPane(), message, "Track Name", JOptionPane.QUESTION_MESSAGE, null, null, currentName);
			if (newName != null) {
				selectedTrack.setName(newName);
				selectedTrack.repaint();
			}
		}
	}
}
