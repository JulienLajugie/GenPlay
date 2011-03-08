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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.action.project;

import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ActionMap;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.core.manager.ConfigurationManager;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.fileFilter.GenPlayProjectFilter;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.trackList.TrackList;
import edu.yu.einstein.genplay.util.Utils;



/**
 * Loads a project from a file
 * @author Julien Lajugie
 * @version 0.1
 */
public class PALoadProject extends TrackListActionWorker<Boolean> {

	private static final long serialVersionUID = 6498078428524511709L;	// generated ID
	private static final String 	DESCRIPTION = 
		"Load a project from a file"; 								// tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_L; 		// mnemonic key
	private static final String 	ACTION_NAME = "Load Project";	// action name
	private final 		TrackList	trackList;						// track list where to load the project
	private File 					selectedFile;					// selected file


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PALoadProject";


	/**
	 * Creates an instance of {@link PALoadProject}
	 */
	public PALoadProject(TrackList trackList) {
		super();
		this.trackList = trackList;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@Override
	protected Boolean processAction() throws Exception {
		String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		FileFilter[] fileFilters = {new GenPlayProjectFilter()};
		selectedFile = Utils.chooseFileToLoad(trackList.getRootPane(), "Load Project", defaultDirectory, fileFilters);
		if (selectedFile != null) {
			notifyActionStart("Loading Project", 1, false);
			trackList.loadProject(selectedFile);
			return true;
		}
		return false;
	}


	@Override
	protected void doAtTheEnd(Boolean actionResult) {
		if (actionResult) {
			JFrame mainFrame = (JFrame)trackList.getTopLevelAncestor();
			String projectName = Utils.getFileNameWithoutExtension(selectedFile);
			mainFrame.setTitle(projectName + MainFrame.APPLICATION_TITLE);
		}
	}
}
