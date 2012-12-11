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
package edu.yu.einstein.genplay.gui.old.action.allTrack;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.gui.dialog.specialPaste.SpecialPasteDialog;
import edu.yu.einstein.genplay.gui.old.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.old.track.Track;
import edu.yu.einstein.genplay.gui.old.track.pasteSettings.PasteSettings;


/**
 * Pastes the copied/cut track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ATAPasteSpecial extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = -8396298208791605371L;				// generated ID
	private static final String ACTION_NAME = "Paste Special"; 						// action name
	private static final String DESCRIPTION = "Paste the last copied/cut track with defined options";// tooltip


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ATAPasteSpecial";


	/**
	 * Creates an instance of {@link ATAPasteSpecial}
	 */
	public ATAPasteSpecial() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	@Override
	protected Void processAction() throws Exception {
		Track<?> selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			String name = getTrackList().getCopiedTrack().getName();

			if (SpecialPasteDialog.getInstance().showSpecialPasteDialog(getRootPane(), name) == SpecialPasteDialog.APPROVE_OPTION) {

				if (SpecialPasteDialog.getInstance().getNewName() == null) {
					PasteSettings.REDEFINE_NAME = PasteSettings.NO_OPTION;
					if (SpecialPasteDialog.getInstance().hasToPasteName()) {
						PasteSettings.PASTE_NAME = PasteSettings.YES_OPTION;
					} else {
						PasteSettings.PASTE_NAME = PasteSettings.NO_OPTION;
					}
				} else {
					PasteSettings.REDEFINE_NAME = PasteSettings.YES_OPTION;
					PasteSettings.PASTE_NAME = PasteSettings.NO_OPTION;
					name = SpecialPasteDialog.getInstance().getNewName();
				}

				if (SpecialPasteDialog.getInstance().hasToPasteData()) {
					PasteSettings.PASTE_DATA = PasteSettings.YES_OPTION;
				} else {
					PasteSettings.PASTE_DATA = PasteSettings.NO_OPTION;
				}

				if (SpecialPasteDialog.getInstance().hasToPasteMask()) {
					PasteSettings.PASTE_MASK = PasteSettings.YES_OPTION;
				} else {
					PasteSettings.PASTE_MASK = PasteSettings.NO_OPTION;
				}

				if (SpecialPasteDialog.getInstance().hasToPasteMultiGenome()) {
					PasteSettings.PASTE_MG = PasteSettings.YES_OPTION;
				} else {
					PasteSettings.PASTE_MG = PasteSettings.NO_OPTION;
				}

				notifyActionStart("Special Pasting Clipboard on Track #" + selectedTrack.getTrackNumber(), 1, false);
				getTrackList().pasteSpecialCopiedTrack(name);

			}
		}
		return null;
	}
}
