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
import javax.swing.filechooser.FileFilter;
import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.manager.ChromosomeManager;
import edu.yu.einstein.genplay.core.manager.ConfigurationManager;
import edu.yu.einstein.genplay.core.manager.ProjectRecordingManager;
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
	private File 					selectedFile = null;			// selected file


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
	}

	
	/**
	 * @param selectedFile the file to load
	 */
	public void setSelectedFile(File selectedFile) {
		this.selectedFile = selectedFile;
	}
	

	@Override
	protected Track<?>[] processAction() throws Exception {
		if (selectedFile == null) {
			String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
			FileFilter[] fileFilters = {new GenPlayProjectFilter()};		
			selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Project", defaultDirectory, fileFilters);
			if (selectedFile != null) {
				ProjectRecordingManager.getInstance().initManagers(selectedFile);
			}
		}
		if (selectedFile != null) {
			notifyActionStart("Loading Project", 1, false);
			return ProjectRecordingManager.getInstance().getTrackList();
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(Track<?>[] actionResult) {
		if (actionResult != null) {
			selectedFile = null;
			Chromosome chromosome = ChromosomeManager.getInstance().get(0);
			GenomeWindow genomeWindow = new GenomeWindow(chromosome, 0, chromosome.getLength());
			MainFrame.getInstance().setTitle();
			MainFrame.getInstance().getControlPanel().updateChromosomePanel(genomeWindow);
			MainFrame.getInstance().getTrackList().setTrackList(actionResult);
		}
	}	
}
