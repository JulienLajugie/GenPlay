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

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.layerChooser.LayerChooserDialog;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.trackTransfer.TransferableTrack;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Copies the selected track
 * @author Julien Lajugie
 */
public final class TACopy extends TrackListAction implements ClipboardOwner {

	private static final long serialVersionUID = -1436541643590614314L; // generated ID
	private static final String ACTION_NAME = "Copy"; 					// action name
	private static final String DESCRIPTION = "Copy the selected track";// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_C; 				// mnemonic key


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = TACopy.class.getName();


	/**
	 * Creates an instance of {@link TACopy}
	 */
	public TACopy() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		final Track selectedTrack = getTrackListPanel().getSelectedTrack();
		if (selectedTrack != null) {
			// create image needs to be done on the EDT, that's why the copy class is not a swing worker
			final Image trackImage = TASaveAsImage.createImage(selectedTrack);

			new TrackListActionWorker<Void>() {
				private static final long serialVersionUID = -484217489657614805L;

				@Override
				protected Void processAction() throws Exception {
					Track trackToCopy = null;
					// if there is more than one layer we ask the user which layer to copy
					Layer<?>[] layers = selectedTrack.getLayers().getLayers();
					if (layers.length > 1) {
						trackToCopy = new Track(1);
						LayerChooserDialog layerChooserDialog = new LayerChooserDialog();
						layerChooserDialog.setLayers(Arrays.asList(layers));
						// since Arrays.asList create a list that doesn't support the remove method we convert it as an ArrayList
						layerChooserDialog.setSelectedLayers(new ArrayList<Layer<?>>(Arrays.asList(layers)));
						layerChooserDialog.setMultiselectable(true);
						if (layerChooserDialog.showDialog(getRootPane(), "Select Layers to Copy") == LayerChooserDialog.APPROVE_OPTION) {
							List<Layer<?>> selectedLayerList = layerChooserDialog.getSelectedLayers();
							Layer<?>[] selectedLayers = selectedLayerList.toArray(new Layer<?>[0]);
							if ((selectedLayers != null) && (selectedLayers.length > 0)) {
								for (Layer<?> currentLayer: selectedLayers) {
									Layer<?> layerToAdd = currentLayer.clone();
									layerToAdd.setTrack(trackToCopy);
									trackToCopy.getLayers().add(layerToAdd);
									trackToCopy.setActiveLayer(layerToAdd);
								}
							}
						}
					} else {
						trackToCopy = selectedTrack;
					}
					if (trackToCopy != null) {
						notifyActionStart("Copying Track #" + selectedTrack.getNumber(), 1, false);
						TransferableTrack data = TransferableTrack.getInstance();
						data.setTrackToTransfer(trackToCopy);
						data.setImageToTransfer(trackImage);
						Clipboard clipboard = Utils.getClipboard();
						clipboard.setContents(data, TACopy.this);
					}
					return null;
				}

			}.actionPerformed(e);
		}
	}


	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {}


}
