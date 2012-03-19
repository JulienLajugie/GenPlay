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
package edu.yu.einstein.genplay.gui.action.project;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.manager.recording.ProjectInformation;
import edu.yu.einstein.genplay.core.manager.recording.ProjectRecording;
import edu.yu.einstein.genplay.core.manager.recording.RecordingManager;
import edu.yu.einstein.genplay.gui.dialog.invalidFileDialog.InvalidFileDialog;



/**
 * Shows the about dialog window
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PAInitManagers extends AbstractAction {

	private static final long serialVersionUID = 2102571378866219218L; // generated ID
	private static final String 	DESCRIPTION = "Show About GenPlay"; // tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_A; 			// mnemonic key
	private static final String 	ACTION_NAME = "Initializes Managers";		// action name
	private File file;
	private InputStream inputStream;
	private String[] formerPaths;
	private String[] invalidPaths;
	private String[] newPaths;


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PAInitManagers";


	/**
	 * Creates an instance of {@link PAInitManagers}
	 */
	public PAInitManagers() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		file = null;
		inputStream = null;
	}


	/**
	 * Shows the about dialog window
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		if (file != null || inputStream != null) {
			try {
				ProjectRecording projectRecording = RecordingManager.getInstance().getProjectRecording();
				
				// Initializes the object input stream
				if (file != null) {
					projectRecording.initObjectInputStream(file);			// according to the given file
				} else if (inputStream != null) {							// or
					projectRecording.initObjectInputStream(inputStream);	// according to the given input stream
				}

				// Reads the project information object
				projectRecording.initProjectInformation();
				
				// Gets the project information object
				ProjectInformation projectInformation = projectRecording.getProjectInformation();
				projectInformation.show();
				// Gets the files dependant to the project
				formerPaths = projectInformation.getProjectFiles();
				if (formerPaths != null) {									// if the project is file dependant
					invalidPaths = getInvalidPath(formerPaths);				// we get the invalid files
					if (getNumberOfInvalidFiles(invalidPaths) > 0) {		// if some invalid files exist,
						InvalidFileDialog invalidFileDialog = new InvalidFileDialog(invalidPaths);
						if (invalidFileDialog.showDialog(null) == InvalidFileDialog.APPROVE_OPTION) {
							newPaths = invalidFileDialog.getCorrectedPaths();
							
						} else {
							throw new Exception();
						}
					}
				}

				// Reads the project manager
				projectRecording.initProjectManager();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("PAInitManagers.actionPerformed()");
			System.out.println("ERROR!!!!!!!!!!!!");
		}
	}


	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}


	/**
	 * @param inputStream the inputStream to set
	 */
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}


	private String[] getInvalidPath (String[] paths) {
		String[] invalidPaths = new String[paths.length];
		
		for (int i = 0; i < invalidPaths.length; i++) {
			if (!isValidFile(paths[i])) {
				invalidPaths[i] = paths[i];
			} else {
				invalidPaths[i] = null;
			}
		}

		return invalidPaths;
	}
	
	
	private int getNumberOfInvalidFiles (String[] paths) {
		int cpt = 0;
		for (int i = 0; i < paths.length; i++) {
			if (paths[i] != null) {
				cpt++;
			}
		}
		return cpt;
	}


	private boolean isValidFile (String path) {
		File file = new File(path);
		return file.exists();
	}

}
