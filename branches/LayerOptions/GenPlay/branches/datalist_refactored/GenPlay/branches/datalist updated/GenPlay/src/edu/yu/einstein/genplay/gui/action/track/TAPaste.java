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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.layerChooser.LayerChooserDialog;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.Layer;



/**
 * Pastes the copied/cut track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class TAPaste extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 1603320424932972117L; 			// generated ID
	private static final String ACTION_NAME = "Paste"; 							// action name
	private static final String DESCRIPTION = "Paste the last copied/cut track";// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_P; 						// mnemonic key


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = TAPaste.class.getName();


	/**
	 * Creates an instance of {@link TAPaste}
	 */
	public TAPaste() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@Override
	protected Void processAction() throws Exception {
		Track selectedTrack = getTrackListPanel().getSelectedTrack();
		Track copiedTrack = getTrackListPanel().getCopiedTrack();
		if ((selectedTrack != null) && (copiedTrack != null)) {
			// we ask the user to choose the layers to paste
			Layer<?>[] layers = copiedTrack.getLayers().getLayers();
			if (layers.length > 1) {
				LayerChooserDialog layerChooserDialog = new LayerChooserDialog();
				layerChooserDialog.setLayers(Arrays.asList(layers));
				// since Arrays.asList create a list that doesn't support the remove method we convert it as an ArrayList
				layerChooserDialog.setSelectedLayers(new ArrayList<Layer<?>>(Arrays.asList(layers)));
				layerChooserDialog.setMultiselectable(true);
				if (layerChooserDialog.showDialog(getRootPane(), "Select Layers to Paste") == LayerChooserDialog.APPROVE_OPTION) {
					List<Layer<?>> selectedLayerList = layerChooserDialog.getSelectedLayers();
					Collections.reverse(selectedLayerList);
					Layer<?>[] selectedLayers = selectedLayerList.toArray(new Layer<?>[0]);
					if ((selectedLayers != null) && (selectedLayers.length > 0)) {
						notifyActionStart("Pasting Clipboard on Track #" + selectedTrack.getNumber(), 1, false);
						for (Layer<?> currentLayer: selectedLayers) {
							Layer<?> layerToAdd = currentLayer.deepCopy();
							layerToAdd.setTrack(selectedTrack);
							selectedTrack.getLayers().add(layerToAdd);
							selectedTrack.setActiveLayer(layerToAdd);
						}
					}
				}
			} else if (layers.length == 1) {
				notifyActionStart("Pasting Clipboard on Track #" + selectedTrack.getNumber(), 1, false);
				Layer<?> layerToAdd = layers[0].deepCopy();
				layerToAdd.setTrack(selectedTrack);
				selectedTrack.getLayers().add(layerToAdd);
				selectedTrack.setActiveLayer(layerToAdd);
			}
		}
		return null;
	}
}
