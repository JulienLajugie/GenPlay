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
import java.io.IOException;
import java.io.InputStream;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.manager.ProjectFiles;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.recording.ProjectInformation;
import edu.yu.einstein.genplay.core.manager.recording.ProjectRecording;
import edu.yu.einstein.genplay.core.manager.recording.RecordingManager;
import edu.yu.einstein.genplay.gui.dialog.invalidFileDialog.InvalidFileDialog;


/**
 * This action initializes a project just before loading it.
 * The loading action {@link PALoadProject} gets the track list.
 * A project files contains two elements before the track list, they are:
 * - the project information {@link ProjectInformation}
 * - the managers {@link ProjectManager}
 * 
 * This action first reads the project information in order to validate the files related to the project wether the project is file dependant.
 * Then, this action reads the managers.
 * 
 * This action is set either with an input stream or with a file.
 * The method hasBeenInitialized() must be used in order to validate the initialization.
 * If any error happened, the method getErrorMessage() gives the error message.
 * 
 * @author Nicolas Fourel
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PAInitManagers extends AbstractAction {

	private static final long serialVersionUID = 2102571378866219218L; // generated ID
	private static final String 	DESCRIPTION = "Show About GenPlay"; // tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_A; 			// mnemonic key
	private static final String 	ACTION_NAME = "Initializes Managers";		// action name
	private File 		file;			// the file of the project to load
	private InputStream inputStream;	// the input stream of the project to load
	private boolean		loadingFromWelcomeScreen;
	private String[] 	formerPaths;	// array of the former paths
	private String[] 	invalidPaths;	// array of the invalid paths
	private String[] 	newPaths;		// array of the new paths
	private String		error;			// the error message


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = PAInitManagers.class.getName();


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
		loadingFromWelcomeScreen = false;
		formerPaths = null;
		invalidPaths = null;
		newPaths = null;
		error = null;
	}


	/**
	 * Shows the about dialog window
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		if ((file != null) || (inputStream != null)) {
			ProjectRecording projectRecording = RecordingManager.getInstance().getProjectRecording();

			// Initializes the object input stream
			try {
				if (file != null) {
					projectRecording.initObjectInputStream(file);			// according to the given file
				} else if (inputStream != null) {							// or
					projectRecording.initObjectInputStream(inputStream);	// according to the given input stream
				}
			} catch (Exception e) {
				error = "Could not open the project file.";
				e.printStackTrace();
			}


			if (error == null) {

				// Reads the project information object
				try {
					projectRecording.initProjectInformation();
				} catch (Exception e) {
					error = "Could not read the project information.";
					e.printStackTrace();
				}

				if (!isValidProjectType()) {
					String message = "";
					if (loadIncorrectMultiGenomeProject()) {
						message += "You are trying to load a Multi Genome Project from a Single Genome Project.\n";
					} else if (loadIncorrectSingleProject()) {
						message += "You are trying to load a Single Genome Project from a Multi Genome Project.\n";
					}
					message += "GenPlay does not allow this operation yet.\n";
					message += "Please restart GenPlay and load your project from the welcome screen.";
					JOptionPane.showMessageDialog(null, message, "Invalid project type", JOptionPane.INFORMATION_MESSAGE);
					error = message;
				} else {
					if (error == null) {
						// Manages the missing files
						try {

							// Gets the project information object
							ProjectInformation projectInformation = projectRecording.getProjectInformation();

							// Gets the files dependant to the project
							formerPaths = projectInformation.getProjectFiles();

							if (formerPaths != null) {									// if the project is file dependant
								invalidPaths = getInvalidPath(formerPaths);				// we get the invalid files
								if (hasInvalidFiles()) {		// if some invalid files exist,

									// Warn the user about the .gz and .gz.tbi files
									if (!projectInformation.isSingleProject()) {
										JOptionPane.showMessageDialog(null, "You are about to load a Multi Genome Project but some files have been moved.\n" +
												"The next window will allow you to define their new location.\n" +
												"Please remind that .gz and .gz.tbi files must have the same name and location.");
									}

									InvalidFileDialog invalidFileDialog = new InvalidFileDialog(invalidPaths);
									if (invalidFileDialog.showDialog(null) == InvalidFileDialog.APPROVE_OPTION) {
										newPaths = invalidFileDialog.getCorrectedPaths();
										if (getNumberOfInvalidFiles(newPaths) == 0) {
											ProjectFiles.getInstance().setCurrentFiles(formerPaths);
											ProjectFiles.getInstance().setNewFiles(newPaths);
										} else {						// the user can valid the dialog using invalid files
											throw new Exception();
										}
									} else {							// the user canceled the dialog
										throw new Exception();
									}
								}
							}
						} catch (Exception e) {
							error = "Invalid files path not corrected.";
						}

						if (error == null) {
							// Reads the project manager
							try {
								projectRecording.initProjectManager();
							} catch (Exception e) {
								error = "Could not read the managers.";
								e.printStackTrace();
							}
						}
					}
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					inputStream = null;
					e.printStackTrace();
				}
			}
		}
		if (error != null) {
			System.err.println("PAInitManagers.actionPerformed() Errors:\n" + error);
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


	/**
	 * @return the inputStream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}


	private boolean hasInvalidFiles () {
		for (int i = 0; i < invalidPaths.length; i++) {
			if (invalidPaths[i] != null) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Create an array of invalid paths.
	 * @param paths the file paths
	 * @return the array of invalid paths
	 */
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


	/**
	 * @param paths the array of paths
	 * @return the number of valid paths
	 */
	private int getNumberOfInvalidFiles (String[] paths) {
		int cpt = 0;
		for (int i = 0; i < paths.length; i++) {
			if (!isValidFile(paths[i])) {
				cpt++;
			}
		}
		return cpt;
	}


	/**
	 * @param path file path
	 * @return true if the file is valid
	 */
	private boolean isValidFile (String path) {
		if (path != null) {
			File file = new File(path);
			return file.exists();
		}
		return false;
	}


	/**
	 * @return the error message
	 */
	public String getErrorMessage () {
		return error;
	}


	/**
	 * @return true if the managers have been initialized, false otherwise
	 */
	public boolean hasBeenInitialized () {
		if (error == null) {
			return true;
		}
		return false;
	}


	/**
	 * Checks if the current project and the new project are the same type (single/multigenome)
	 * @return
	 */
	private boolean isValidProjectType () {
		if (loadIncorrectMultiGenomeProject() || loadIncorrectSingleProject()) {
			return false;
		}
		return true;
	}


	/**
	 * @return true if the user is trying to load a multi genome project from a single genome project, false otherwise
	 */
	private boolean loadIncorrectMultiGenomeProject () {
		if (!loadingFromWelcomeScreen) {
			boolean newSingleProject = RecordingManager.getInstance().getProjectRecording().getProjectInformation().isSingleProject();
			boolean currentMultiGenomeProject = ProjectManager.getInstance().isMultiGenomeProject();
			if (!currentMultiGenomeProject && !newSingleProject) {
				return true;
			}
		}
		return false;
	}


	/**
	 * @return true if the user is trying to load a single genome project from a multi genome project, false otherwise
	 */
	private boolean loadIncorrectSingleProject () {
		if (!loadingFromWelcomeScreen) {
			boolean newSingleProject = RecordingManager.getInstance().getProjectRecording().getProjectInformation().isSingleProject();
			boolean currentMultiGenomeProject = ProjectManager.getInstance().isMultiGenomeProject();
			if (currentMultiGenomeProject && newSingleProject) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Use this method when a project is loaded from the welcome screen.
	 * Do not need to use when the action is started from the main frame.
	 * False by default.
	 * @param loadingFromWelcomeScreen the loadingFromWelcomeScreen to set
	 */
	public void setLoadingFromWelcomeScreen(boolean loadingFromWelcomeScreen) {
		this.loadingFromWelcomeScreen = loadingFromWelcomeScreen;
	}

}
