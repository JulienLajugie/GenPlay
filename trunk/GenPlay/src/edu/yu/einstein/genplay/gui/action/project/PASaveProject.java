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

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.recording.ProjectRecording;
import edu.yu.einstein.genplay.core.manager.recording.RecordingManager;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.fileFilter.GenPlayProjectFilter;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.util.Utils;

/**
 * Saves the project into a file
 * 
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public class PASaveProject extends TrackListActionWorker<Boolean> {

	private static final long serialVersionUID = -8503082838697971220L; 			// generated ID
	private static final String 	DESCRIPTION = "Save the current project"; 		// tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_S; 						// mnemonic key
	private static final String 	ACTION_NAME = "Save";		 					// action name


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = PASaveProject.class.getName();


	/**
	 * Creates an instance of {@link PASaveProject}
	 */
	public PASaveProject() {
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
			RecordingManager.getInstance().getRecentProjectRecording().writeProjects();
			MainFrame.getInstance().setTitle();
		}
	}


	@Override
	protected Boolean processAction() throws Exception {
		File projectDirectory = ProjectManager.getInstance().getProjectDirectory();
		if (projectDirectory != null) {
			notifyActionStart("Saving Project", 1, false);
			String projectName = ProjectManager.getInstance().getProjectName();
			File projectFile = Utils.addExtension(new File(projectDirectory, projectName), GenPlayProjectFilter.EXTENSIONS[0]);
			ProjectRecording projectRecording = RecordingManager.getInstance().getProjectRecording();
			projectRecording.setCurrentProjectPath(projectFile.getPath());
			return projectRecording.saveProject(projectFile);
		}
		return false;
	}
}
