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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.dialog.layerSettings.LayerSettingsDialog;
import edu.yu.einstein.genplay.gui.dialog.layerSettings.LayerSettingsRow;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.ColoredLayer;
import edu.yu.einstein.genplay.gui.track.layer.GraphLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.util.colors.LayerColor;


/**
 * Shows a dialog to manage the layer settings
 * @author Julien Lajugie
 */
public class TALayerSettings extends TrackListAction {

	private static final long serialVersionUID = 775293461948991915L;		// generated ID
	private static final String ACTION_NAME = "Layer Settings";				// action name
	private static final String DESCRIPTION = "Manage layers settings";		// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_L; 					// mnemonic key


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = TALayerSettings.class.getName();


	/**
	 * Creates an instance of {@link TALayerSettings}
	 */
	public TALayerSettings() {
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
			Layer<?>[] trackLayers = selectedTrack.getLayers().getLayers();
			if ((trackLayers != null) && (trackLayers.length > 0)) {
				LayerSettingsRow[] layerSettings = new LayerSettingsRow[trackLayers.length];
				for (int i = 0; i < trackLayers.length; i++) {
					layerSettings[i] = new LayerSettingsRow(trackLayers[i]);
					layerSettings[i].setLayerActive(selectedTrack.getActiveLayer() == trackLayers[i]);
				}
				LayerSettingsDialog layerSettingsDialog = new LayerSettingsDialog(layerSettings);
				if (layerSettingsDialog.showDialog(getRootPane()) == LayerSettingsDialog.APPROVE_OPTION) {
					selectedTrack.getLayers().clear();
					selectedTrack.setActiveLayer(null);
					for (LayerSettingsRow currentRow: layerSettings) {
						if (!currentRow.isLayerSetForDeletion()) {
							currentRow.getLayer().setName(currentRow.getLayerName());
							currentRow.getLayer().setVisible(currentRow.isLayerVisible());
							if (currentRow.getLayer() instanceof ColoredLayer) {
								Color selectedColor = currentRow.getLayerColor();
								// will be removed in the java7 version of genplay since user will be able to select the transparency
								selectedColor = LayerColor.createTransparentColor(selectedColor);
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
					selectedTrack.repaint();
				}
			}
		}
	}
}
