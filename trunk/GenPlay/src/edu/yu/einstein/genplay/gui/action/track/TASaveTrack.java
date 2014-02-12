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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.core.IO.writer.TransferableTrackWriter;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.fileFilter.ExtendedFileFilter;
import edu.yu.einstein.genplay.gui.fileFilter.GenPlayTrackFilter;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Saves the selected track in GenPlay transferable track format
 * @author Julien Lajugie
 */
public final class TASaveTrack extends TrackListActionWorker<Void> {


	private static final long serialVersionUID = -1893808843074231009L;							// generated ID
	private static final String ACTION_NAME = "Save Track"; 									// action name
	private static final String DESCRIPTION = "Save the selected track with all its layers";	// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_S; 										// mnemonic key
	private File 				selectedFile; 													// selected file


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.ALT_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = TASaveTrack.class.getName();


	/**
	 * Creates an instance of {@link TASaveTrack}
	 */
	public TASaveTrack() {
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
		final JFileChooser jfc = new JFileChooser();
		Utils.setFileChooserSelectedDirectory(jfc);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setDialogTitle("Save Track");
		FileFilter[] filters = {new GenPlayTrackFilter()};
		jfc.addChoosableFileFilter(filters[0]);
		jfc.setFileFilter(filters[0]);
		jfc.setAcceptAllFileFilterUsed(false);
		File f = new File(selectedTrack.getName() + "." + GenPlayTrackFilter.EXTENSIONS[0]);
		jfc.setSelectedFile(f);
		int returnVal = jfc.showSaveDialog(getRootPane());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			ExtendedFileFilter selectedFilter = (ExtendedFileFilter) jfc.getFileFilter();
			if (selectedFilter != null) {
				selectedFile = Utils.addExtension(jfc.getSelectedFile(), selectedFilter.getExtensions()[0]);
			} else {
				selectedFile = Utils.addExtension(jfc.getSelectedFile(), GenPlayTrackFilter.EXTENSIONS[0]);
			}
			if (!Utils.cancelBecauseFileExist(getRootPane(), selectedFile)) {
				notifyActionStart("Saving Track " + selectedTrack.getNumber(), 1, false);
				new TransferableTrackWriter(selectedTrack, selectedFile).write();
			}
		}
		return null;
	}
}
