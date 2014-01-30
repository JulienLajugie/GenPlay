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

import javax.swing.ActionMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.exception.exceptions.IncompatibleAssembliesException;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.TransferableTrack;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Pastes the copied/cut track
 * @author Julien Lajugie
 */
public final class TAPasteOrDrop extends TrackListActionWorker<Void> {

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
	public static final String ACTION_KEY = TAPasteOrDrop.class.getName();


	private Transferable transferable = null;	// transferable to load
	private boolean isDrop = false;				// true if it's a drop action, false if it's a paste action

	/**
	 * Creates an instance of {@link TAPasteOrDrop}
	 * Constructor used for paste actions
	 */
	public TAPasteOrDrop() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	/**
	 * Creates an instance of {@link TAPasteOrDrop}
	 * Constructor used for drop actions
	 */
	public TAPasteOrDrop(Transferable transferable) {
		super();
		this.transferable = transferable;
		isDrop = true;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@Override
	public Void processAction() throws Exception {
		try {
			Track selectedTrack = getTrackListPanel().getSelectedTrack();
			if (selectedTrack != null) {
				String actionStr = isDrop ? "Dropping" : "Pasting";
				notifyActionStart(actionStr + " Data on Track #" + selectedTrack.getNumber(), 1, false);
				if (selectedTrack != null) {
					if (transferable == null) {
						Clipboard clipboard = Utils.getClipboard();
						// try to retrieve TRACK_FLAVOR data
						transferable = clipboard.getContents(null);
					}
					if (transferable.isDataFlavorSupported(TransferableTrack.TRACK_FLAVOR)) {
						Track copiedTrack = TransferableTrack.getTrackFromTransferable(transferable);
						if (copiedTrack != null) {
							if (selectedTrack.getLayers().isEmpty()) {
								selectedTrack.setContentFrom(copiedTrack);
							} else {
								Layer<?>[] layers = copiedTrack.getLayers().getLayers();
								for (Layer<?> currentLayer: layers) {
									currentLayer.setTrack(selectedTrack);
									selectedTrack.getLayers().add(currentLayer);
									selectedTrack.setActiveLayer(currentLayer);
								}
							}
						}
					} else {
						// try to retrieve a file from the clipboard
						File fileFromClipboard = TransferableTrack.getFileFromTransferable(transferable);
						if (fileFromClipboard != null) {
							notifyActionStop();
							new TAAddLayer(fileFromClipboard).actionPerformed(null);
						}
					}
				}
			}
		} catch (IllegalStateException e) {
			JOptionPane.showMessageDialog(getRootPane(), "The clipboard is unavailable. It might be accessed by another application.", "Clipboard Unavailable", JOptionPane.WARNING_MESSAGE, null);
		}catch (IncompatibleAssembliesException e) {
			JOptionPane.showMessageDialog(getRootPane(), "The selected file in cannot be loaded. "
					+ "\nIt contains a track from a project with different assembly or multigenome parameters.", "Invalid File", JOptionPane.WARNING_MESSAGE, null);
		} catch (Exception e) {
			String title = isDrop ? "Cannot Drop Data" : "Invalid Clipboard Content";
			String message = isDrop ? "the selected file cannot be Dropped." : "The content of the clipboard cannot be loaded.";
			JOptionPane.showMessageDialog(getRootPane(), message, title, JOptionPane.WARNING_MESSAGE, null);
		}
		return null;
	}
}
