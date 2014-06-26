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

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.trackTransfer.TransferableTrack;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Cuts the selected track
 * @author Julien Lajugie
 */
public final class TACut extends TrackListAction implements ClipboardOwner{

	private static final long serialVersionUID = 5387375446702872880L;  // generated ID
	private static final String ACTION_NAME = "Cut"; 					// action name
	private static final String DESCRIPTION = "Cut the selected track"; // tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_U; 				// mnemonic key


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = TACut.class.getName();


	/**
	 * Creates an instance of {@link TACut}
	 */
	public TACut() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@Override
	public void trackListActionPerformed(ActionEvent e) {
		final Track selectedTrack = getTrackListPanel().getSelectedTrack();
		if (selectedTrack != null) {
			// create image needs to be done on the EDT, that's why the cut class is not a swing worker
			final Image trackImage = TASaveAsImage.createImage(selectedTrack);

			new TrackListActionWorker<Void>() {
				private static final long serialVersionUID = 6666475714903356647L;
				@Override
				protected Void processAction() throws Exception {
					notifyActionStart("Cutting Track #" + selectedTrack.getNumber(), 1, false);
					TransferableTrack data = TransferableTrack.getInstance();
					data.setTrackToTransfer(selectedTrack);
					data.setImageToTransfer(trackImage);
					Clipboard clipboard = Utils.getClipboard();
					clipboard.setContents(data, TACut.this);
					getTrackListPanel().cutTrack();
					return null;
				}
			}.actionPerformed(e);

		}
	}


	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {}


}
