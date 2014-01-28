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
package edu.yu.einstein.genplay.gui.action.project;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.security.InvalidParameterException;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.core.IO.fileSorter.ExternalSortAdapter;
import edu.yu.einstein.genplay.core.manager.application.ConfigurationManager;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.exceptionDialog.WarningReportDialog;
import edu.yu.einstein.genplay.util.Utils;

/**
 * Sorts the specified genomic file by chromosome, start and then stop position
 * @author Julien Lajugie
 */
public class PASortFile extends TrackListActionWorker<Boolean> {

	private static final long serialVersionUID = -1635888549644450204L;	// generated serial ID
	private static final String 	DESCRIPTION = "Sort the specified file by chromosome, start and stop position"; 	// tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_R; 			// mnemonic key
	private static final String 	ACTION_NAME = "Sort File";	 		// action name
	private File 					selectedFile; 						// selected file


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = PASortFile.class.getName();


	/**
	 * Creates an instance of {@link PASortFile}
	 */
	public PASortFile() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	@Override
	protected void doAtTheEnd(Boolean actionResult) {
		if ((actionResult != null) && (actionResult)) {
			JOptionPane.showMessageDialog(getRootPane(), "File Sorted Successfully");
		}
	}


	@Override
	protected Boolean processAction() throws Exception {
		String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		final JFileChooser jfc = new JFileChooser(defaultDirectory);
		// redundant on Windows and Linux but needed for OSX
		jfc.setSelectedFile(new File(defaultDirectory));
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setDialogTitle("Sort File");
		jfc.setAcceptAllFileFilterUsed(true);
		int returnVal = jfc.showOpenDialog(getRootPane());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			selectedFile = jfc.getSelectedFile();
			if (!Utils.cancelBecauseFileExist(getRootPane(), ExternalSortAdapter.generateOutputFile(selectedFile))) {
				notifyActionStart("Sorting File", 1, false);
				try {
					ExternalSortAdapter.externalSortGenomicFile(selectedFile);
					return true;
				} catch (InvalidParameterException e) {
					WarningReportDialog.getInstance().addMessage(e.getMessage());
					WarningReportDialog.getInstance().showDialog(getRootPane());
				}
			}

		}
		return false;
	}
}
