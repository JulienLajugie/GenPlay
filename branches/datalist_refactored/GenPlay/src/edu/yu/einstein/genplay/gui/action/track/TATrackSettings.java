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
package edu.yu.einstein.genplay.gui.action.track;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.dialog.trackSettings.TrackSettingsDialog;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.TrackScore;
import edu.yu.einstein.genplay.gui.track.layer.backgroundLayer.BackgroundData;
import edu.yu.einstein.genplay.gui.track.layer.foregroundLayer.ForegroundData;


/**
 * Shows a dialog to manage the selected track
 * @author Julien Lajugie
 */
public class TATrackSettings extends TrackListAction {

	private static final long serialVersionUID = 775293461948991915L;		// generated ID
	private static final String ACTION_NAME = "Track Settings";				// action name
	private static final String DESCRIPTION = "Manage Tracks";				// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_T; 					// mnemonic key


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = TATrackSettings.class.getName();


	/**
	 * Creates an instance of {@link TATrackSettings}
	 */
	public TATrackSettings() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	/**
	 * Shows a dialog to manage the layer settings
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Track selectedTrack = getTrackListPanel().getSelectedTrack();
		if (selectedTrack != null) {
			TrackSettingsDialog trackSettingsDialog = new TrackSettingsDialog();
			int option = trackSettingsDialog.showDialog(getRootPane(), selectedTrack);
			if (option == TrackSettingsDialog.APPROVE_OPTION) {
				TrackScore trackScore = selectedTrack.getScore();
				ForegroundData foregroundData = selectedTrack.getForegroundLayer().getData();
				BackgroundData backgroundData = selectedTrack.getBackgroundLayer().getData();
				// track basic settings
				selectedTrack.setName(trackSettingsDialog.getTrackName());
				selectedTrack.setPreferredHeight(trackSettingsDialog.getTrackHeight());
				// track background settings
				backgroundData.setHorizontalGridVisible(trackSettingsDialog.areHorizontalLinesVisibe());
				backgroundData.setHorizontalLineCount(trackSettingsDialog.getHorizontalLineCout());
				backgroundData.setVerticalGridVisible(trackSettingsDialog.areVerticalLinesVisibe());
				backgroundData.setVerticalLineCount(trackSettingsDialog.getVerticalLineCout());
				// track score settings
				trackScore.setMinimumScore(trackSettingsDialog.getScoreMinimum());
				trackScore.setMaximumScore(trackSettingsDialog.getScoreMaximum());
				trackScore.setScoreAxisAutorescaled(trackSettingsDialog.isScoreAutoRescaled());
				// track foreground settings
				foregroundData.setScorePosition(trackSettingsDialog.getScorePosition());
				foregroundData.setScoreColor(trackSettingsDialog.getScoreColor());

				selectedTrack.repaint();
			}
		}
	}
}
