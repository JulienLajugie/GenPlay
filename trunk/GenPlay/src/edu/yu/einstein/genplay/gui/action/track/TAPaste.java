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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.core.IO.extractor.ExtractorFactory;
import edu.yu.einstein.genplay.exception.exceptions.InvalidFileTypeException;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.layerChooser.LayerChooserDialog;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.TransferableTrack;
import edu.yu.einstein.genplay.gui.track.layer.Layer;


/**
 * Pastes the copied/cut track
 * @author Julien Lajugie
 */
public final class TAPaste extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 1603320424932972117L; 			// generated ID
	private static final String ACTION_NAME = "Paste"; 							// action name
	private static final String DESCRIPTION = "Paste the last copied/cut track";// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_P; 						// mnemonic key


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = TAPaste.class.getName();


	/**
	 * @param fileListObj
	 * @return retrieve a {@link TransferableTrack} from a file if the file contain a serialized TransferableTrack
	 */
	private static TransferableTrack retrieveTrackFromFile(File file) {
		/*else if (flavors[i].match(DataFlavor.javaFileListFlavor)) {
			@SuppressWarnings("unchecked")
			List<File> fileList = (List<File>) transferable.getTransferData(flavors[i]);
			if (!fileList.isEmpty()) {
				File file = fileList.get(0);
				transTrack = retrieveTrackFromFile(file);
			}
		} else if  (flavors[i].match(TransferableTrack.uriListFlavor)) {
			String fileNameList = (String)  transferable.getTransferData(flavors[i]);
			File file = new File(new URI(fileNameList.split("\r\n")[0]));
			transTrack = retrieveTrackFromFile(file);
		}*/
		ObjectInputStream ois = null;
		try {
			try {
				ExtractorFactory.getExtractor(file); // throws an error if extractor type cannot be determined
				TAAddLayer addLayerAction = new TAAddLayer();
				addLayerAction.actionPerformed(null);
				return null;
			} catch (InvalidFileTypeException e) {
				FileInputStream in = new FileInputStream(file);
				ois = new ObjectInputStream(in);
				TransferableTrack transTrack = (TransferableTrack) (ois.readObject());
				return transTrack;
			}
		} catch (Exception e) { // do nothing
			e.printStackTrace();
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}


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
		notifyActionStart("Pasting Clipboard on Track #" + selectedTrack.getNumber(), 1, false);
		if (selectedTrack != null) {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable transferable = (Transferable) clipboard.getData(TransferableTrack.TRACK_FLAVOR);
			if (transferable != null) {
				Track copiedTrack = TransferableTrack.getTrackFromTransferable(transferable);
				// TODO handle incompatible assemblies exception
				if (copiedTrack != null) {
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
								for (Layer<?> currentLayer: selectedLayers) {
									Layer<?> layerToAdd = currentLayer.clone();
									layerToAdd.setTrack(selectedTrack);
									selectedTrack.getLayers().add(layerToAdd);
									selectedTrack.setActiveLayer(layerToAdd);
								}
							}
						}
					} else if (layers.length == 1) {
						notifyActionStart("Pasting Clipboard on Track #" + selectedTrack.getNumber(), 1, false);
						Layer<?> layerToAdd = layers[0].clone();
						layerToAdd.setTrack(selectedTrack);
						selectedTrack.getLayers().add(layerToAdd);
						selectedTrack.setActiveLayer(layerToAdd);
					}
				}
			}
		}
		return null;
	}
}
