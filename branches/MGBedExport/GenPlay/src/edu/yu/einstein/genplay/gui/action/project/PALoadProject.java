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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.recording.RecordingManager;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.fileFilter.GenPlayProjectFilter;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Loads a project from a file
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class PALoadProject extends TrackListActionWorker<Track<?>[]> {

	private static final long serialVersionUID = 6498078428524511709L;	// generated ID
	private static final String 	DESCRIPTION =
			"Load a project from a file"; 								// tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_L; 		// mnemonic key
	private static final String 	ACTION_NAME = "Load Project";	// action name
	private boolean					skipFileSelection = false;		// true if the file selection need to be skipped. Default is false


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke 	ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PALoadProject";


	/**
	 * Creates an instance of {@link PALoadProject}
	 */
	public PALoadProject() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	/**
	 * Sets the user selection of a file to load needs to be skipped
	 * @param skipFileSelection true if no files need to be selected by the user
	 */
	public void setSkipFileSelection(boolean skipFileSelection) {
		this.skipFileSelection = skipFileSelection;
	}


	@Override
	protected Track<?>[] processAction() throws Exception {
		boolean hasBeenInitialized = true;						// If the file has been selected, managers have been successfully initialized.
		if (!skipFileSelection) {
			String defaultDirectory = ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory();
			FileFilter[] fileFilters = {new GenPlayProjectFilter()};
			File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Project", defaultDirectory, fileFilters, true);
			if (selectedFile == null) {
				return null;
			}
			PAInitManagers init = new PAInitManagers();
			init.setFile(selectedFile);
			init.actionPerformed(null);
			hasBeenInitialized = init.hasBeenInitialized();		// We just selected the file, we have to check if managers have been successfully initialized
		}
		if (hasBeenInitialized) {
			MainFrame.getInstance().getTrackList().resetTrackList(); // we remove all the track before the loading (better for memory usage)
			notifyActionStart("Loading Project", 1, false);
			return RecordingManager.getInstance().getProjectRecording().getTrackList();
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(Track<?>[] actionResult) {
		if (actionResult != null) {
			skipFileSelection = false;

			MainFrame.getInstance().setTitle();
			MainFrame.getInstance().getControlPanel().reinitChromosomePanel();

			MainFrame.getInstance().setVisible(true);
			MainFrame.getInstance().getTrackList().setTrackList(actionResult);
			ProjectManager.getInstance().getProjectWindow().removeAllListeners();
			MainFrame.getInstance().registerToGenomeWindow();
		}
	}

}
