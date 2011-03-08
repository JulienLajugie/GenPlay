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
package yu.einstein.gdp2.gui.action.project;

import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListActionWorker;
import yu.einstein.gdp2.gui.fileFilter.ExtendedFileFilter;
import yu.einstein.gdp2.gui.fileFilter.GenPlayProjectFilter;
import yu.einstein.gdp2.gui.mainFrame.MainFrame;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.Utils;


/**
 * Saves the project into a file
 * @author Julien Lajugie
 * @version 0.1
 */
public class PASaveProject extends TrackListActionWorker<Boolean> {

	private static final long serialVersionUID = -8503082838697971220L;	// generated ID
	private static final String 	DESCRIPTION = 
		"Save the project into a file"; 							// tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_S; 		// mnemonic key
	private static final String 	ACTION_NAME = "Save Project";	// action name
	private final 		TrackList	trackList;						// track list containing the project to save
	private File 					selectedFile;					// selected file


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PASaveProject";


	/**
	 * Creates an instance of {@link PASaveProject}
	 */
	public PASaveProject(TrackList trackList) {
		super();
		this.trackList = trackList;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@Override
	protected Boolean processAction() throws Exception {
		final JFileChooser jfc = new JFileChooser(ConfigurationManager.getInstance().getDefaultDirectory());
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setDialogTitle("Save Project");
		jfc.addChoosableFileFilter(new GenPlayProjectFilter());
		jfc.setAcceptAllFileFilterUsed(false);
		int returnVal = jfc.showSaveDialog(trackList.getRootPane());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			ExtendedFileFilter selectedFilter = (ExtendedFileFilter)jfc.getFileFilter();
			selectedFile = Utils.addExtension(jfc.getSelectedFile(), selectedFilter.getExtensions()[0]);
			if (!Utils.cancelBecauseFileExist(trackList.getRootPane(), selectedFile)) {
				notifyActionStart("Saving Project", 1, false);
				trackList.saveProject(selectedFile);
				return true;
			}
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
