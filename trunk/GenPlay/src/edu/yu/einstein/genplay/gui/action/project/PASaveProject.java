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
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.recording.ProjectRecording;
import edu.yu.einstein.genplay.core.manager.recording.RecordingManager;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.fileFilter.ExtendedFileFilter;
import edu.yu.einstein.genplay.gui.fileFilter.GenPlayProjectFilter;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.trackList.TrackListPanel;
import edu.yu.einstein.genplay.util.Utils;

/**
 * Saves the project into a file
 * 
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public class PASaveProject extends TrackListActionWorker<Boolean> {

	private static final long serialVersionUID = -8503082838697971220L; 			// generated ID
	private static final String 	DESCRIPTION = "Save the project into a file"; 	// tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_S; 						// mnemonic key
	private static final String 	ACTION_NAME = "Save Project"; 					// action name
	private final TrackListPanel 	trackListPanel; 		// track list containing the project to save
	private File 					selectedFile; 									// selected file

	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = PASaveProject.class.getName();


	/**
	 * Creates an instance of {@link PASaveProject}
	 * 
	 * @param trackListPanel singleton TrackList of the project
	 */
	public PASaveProject(TrackListPanel trackListPanel) {
		super();
		this.trackListPanel = trackListPanel;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	@Override
	protected void doAtTheEnd(Boolean actionResult) {
		if ((actionResult != null) && (actionResult)) {
			RecordingManager.getInstance().getRecentProjectRecording().writeProjects();
			MainFrame.getInstance().setTitle();
		}
	}


	@Override
	protected Boolean processAction() throws Exception {
		final JFileChooser jfc = new JFileChooser(ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory());
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setDialogTitle("Save Project");
		FileFilter[] filters = { new GenPlayProjectFilter() };
		jfc.addChoosableFileFilter(filters[0]);
		jfc.setFileFilter(filters[0]);
		jfc.setAcceptAllFileFilterUsed(false);
		File f = new File(ProjectManager.getInstance().getProjectName().concat(".gen"));
		jfc.setSelectedFile(f);
		int returnVal = jfc.showSaveDialog(trackListPanel.getRootPane());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			ExtendedFileFilter selectedFilter = (ExtendedFileFilter) jfc.getFileFilter();
			if (selectedFilter != null) {
				selectedFile = Utils.addExtension(jfc.getSelectedFile(), selectedFilter.getExtensions()[0]);
			} else {
				selectedFile = Utils.addExtension(jfc.getSelectedFile(), "gen");
			}
			if (!Utils.cancelBecauseFileExist(trackListPanel.getRootPane(), selectedFile)) {
				notifyActionStart("Saving Project", 1, false);
				String projectName = Utils.getFileNameWithoutExtension(selectedFile);
				ProjectManager.getInstance().setProjectName(projectName);
				ProjectRecording projectRecording = RecordingManager.getInstance().getProjectRecording();
				projectRecording.setCurrentProjectPath(selectedFile.getPath());
				return projectRecording.saveProject(selectedFile);
			}
		}
		return false;
	}
}