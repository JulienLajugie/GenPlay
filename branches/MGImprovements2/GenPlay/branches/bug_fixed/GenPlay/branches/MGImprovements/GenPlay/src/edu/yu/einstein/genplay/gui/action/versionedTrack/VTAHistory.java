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
package edu.yu.einstein.genplay.gui.action.versionedTrack;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.dialog.HistoryDialog;
import edu.yu.einstein.genplay.gui.track.VersionedTrack;
import edu.yu.einstein.genplay.util.History;


/**
 * Shows the history of the selected {@link VersionedTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class VTAHistory extends TrackListAction {

	private static final long serialVersionUID = 6153915221242216274L;  // generated ID
	private static final String 	ACTION_NAME = "Show History";		// action name
	private static final String 	DESCRIPTION =
			"Show the history of the selected track";				 		// tooltip


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "VTAHistory";


	/**
	 * Creates an instance of {@link VTAHistory}
	 */
	public VTAHistory() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (getTrackList().getSelectedTrack() instanceof VersionedTrack) {
			VersionedTrack selectedTrack = (VersionedTrack) getTrackList().getSelectedTrack();
			if (selectedTrack != null) {
				String trackName = getTrackList().getSelectedTrack().getName();
				History history = selectedTrack.getHistory();
				HistoryDialog.showHistoryDialog(getRootPane(), trackName, history);
			}
		}
	}
}
