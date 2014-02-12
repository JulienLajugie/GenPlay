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
package edu.yu.einstein.genplay.gui.action.track;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;


/**
 * Inserts a blank track
 * @author Julien Lajugie
 * @version 0.1
 */
public class TAInsert extends TrackListAction {

	private static final long serialVersionUID = 775293461948991915L;		// generated ID
	private static final String ACTION_NAME = "Insert"; 					// action name
	private static final String DESCRIPTION = "Insert a blank track " +
			"right above the selected track"; 								// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_I; 					// mnemonic key


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = TAInsert.class.getName();


	/**
	 * Creates an instance of {@link TAInsert}
	 */
	public TAInsert() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	/**
	 * Inserts a blank track
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (MainFrame.getInstance().isLocked()) {
			return;
		}

		Track selectedTrack = getTrackListPanel().getSelectedTrack();
		if (selectedTrack != null) {
			int trackIndex = getTrackListPanel().getModel().indexOf(selectedTrack);
			Track newTrack = new Track(trackIndex);
			getTrackListPanel().getModel().insertTrack(trackIndex, newTrack);
		}
	}
}
