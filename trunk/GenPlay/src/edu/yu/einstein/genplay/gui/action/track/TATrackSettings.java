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

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.dialog.trackSettings.TrackSettingsDialog;
import edu.yu.einstein.genplay.gui.dialog.trackSettings.layerPanel.LayerSettingsRow;
import edu.yu.einstein.genplay.gui.dialog.trackSettings.trackPanel.TrackSettingsPanel;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.TrackScore;
import edu.yu.einstein.genplay.gui.track.layer.ColoredLayer;
import edu.yu.einstein.genplay.gui.track.layer.GraphLayer;
import edu.yu.einstein.genplay.gui.track.layer.background.BackgroundData;
import edu.yu.einstein.genplay.gui.track.layer.foreground.ForegroundData;
import edu.yu.einstein.genplay.util.Utils;
import edu.yu.einstein.genplay.util.colors.Colors;
import edu.yu.einstein.genplay.util.colors.LayerColors;


/**
 * Shows a dialog to manage the selected track
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public class TATrackSettings extends TrackListAction {

	private static final long serialVersionUID = 775293461948991915L;		// generated ID
	private static final String ACTION_NAME = "Track Settings";				// action name
	private static final String DESCRIPTION = "Manage Track Options";		// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_T; 					// mnemonic key


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());


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
	 * Set the new layer options
	 * @param selectedTrack the selected track
	 * @param layerSettings the layer options
	 */
	private void setLayerOptions (Track selectedTrack, LayerSettingsRow[] layerSettings) {
		selectedTrack.getLayers().clear();
		selectedTrack.setActiveLayer(null);
		for (LayerSettingsRow currentRow: layerSettings) {
			if (!currentRow.isLayerSetForDeletion()) {
				currentRow.getLayer().setName(currentRow.getLayerName());
				currentRow.getLayer().setVisible(currentRow.isLayerVisible());
				if (currentRow.getLayer() instanceof ColoredLayer) {
					Color selectedColor = currentRow.getLayerColor();
					// will be removed in the java7 version of genplay since user will be able to select the transparency
					if (Utils.getJavaVersion() < 7) {
						selectedColor = Colors.addTransparency(selectedColor, LayerColors.DEFAULT_TRANSPARENCY);
					}
					((ColoredLayer) currentRow.getLayer()).setColor(selectedColor);
				}
				if (currentRow.getLayer() instanceof GraphLayer) {
					((GraphLayer) currentRow.getLayer()).setGraphType(currentRow.getLayerGraphType());
				}
				selectedTrack.getLayers().addLast(currentRow.getLayer());
				if (currentRow.isLayerActive()) {
					selectedTrack.setActiveLayer(currentRow.getLayer());
				}
			}
		}

		// if there is no active layer we set the first layer as active (if there is a layer)
		if ((selectedTrack.getActiveLayer() == null) && (selectedTrack.getLayers().size() > 0)) {
			selectedTrack.setActiveLayer(selectedTrack.getLayers().getLayers()[0]);
		}
	}


	/**
	 * Set the new track options
	 * @param selectedTrack the selected track
	 * @param trackOptions the track options
	 */
	private void setTrackOptions (Track selectedTrack, TrackSettingsPanel trackOptions) {
		TrackScore trackScore = selectedTrack.getScore();
		ForegroundData foregroundData = selectedTrack.getForegroundLayer().getData();
		BackgroundData backgroundData = selectedTrack.getBackgroundLayer().getData();
		// track basic settings
		selectedTrack.setName(trackOptions.getTrackName());
		selectedTrack.setPreferredHeight(trackOptions.getTrackHeight());
		// track background settings
		backgroundData.setHorizontalGridVisible(trackOptions.areHorizontalLinesVisibe());
		backgroundData.setHorizontalLineCount(trackOptions.getHorizontalLineCout());
		backgroundData.setVerticalGridVisible(trackOptions.areVerticalLinesVisibe());
		backgroundData.setVerticalLineCount(trackOptions.getVerticalLineCout());
		// track score settings
		trackScore.setMinimumScore(trackOptions.getScoreMinimum());
		trackScore.setMaximumScore(trackOptions.getScoreMaximum());
		trackScore.setScoreAxisAutorescaled(trackOptions.isScoreAutoRescaled());
		// track foreground settings
		foregroundData.setScorePosition(trackOptions.getScorePosition());
		foregroundData.setScoreColor(trackOptions.getScoreColor());
	}


	/**
	 * Shows a dialog to manage the layer settings
	 */
	@Override
	public void trackListActionPerformed(ActionEvent arg0) {
		if (MainFrame.getInstance().isLocked()) {
			return;
		}

		Track selectedTrack = getTrackListPanel().getSelectedTrack();
		if (selectedTrack != null) {
			TrackSettingsDialog dialog = new TrackSettingsDialog();
			int option = dialog.showDialog(getRootPane(), selectedTrack);
			if (option == TrackSettingsDialog.APPROVE_OPTION) {

				// Set the track options
				setTrackOptions(selectedTrack, dialog.getTrackOptions());

				// Set the layer options
				if (dialog.getLayerOptions() != null) {
					setLayerOptions(selectedTrack, dialog.getLayerOptions());
				}

				// Update the track display
				selectedTrack.repaint();
			}
			dialog.dispose();
		}
	}

}
