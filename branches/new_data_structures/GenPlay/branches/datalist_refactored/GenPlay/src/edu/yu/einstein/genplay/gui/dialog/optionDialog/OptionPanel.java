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
package edu.yu.einstein.genplay.gui.dialog.optionDialog;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.yu.einstein.genplay.core.manager.project.ProjectConfiguration;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;


/**
 * Right panel of an {@link OptionDialog} Defines the common attributes of the
 * different panels of the configuration frame.
 * @author Julien Lajugie
 * @version 0.1
 */
abstract class OptionPanel extends JPanel {

	private static final long serialVersionUID = 4821469631755757767L; 	// Generated ID
	final ProjectConfiguration projectConfiguration; 			// ConfigurationManager

	
	/**
	 * Constructor. Creates an instance of {@link OptionPanel}
	 * @param name name of the category of configuration
	 */
	OptionPanel(String name) {
		super();
		setName(name);
		this.projectConfiguration = ProjectManager.getInstance().getProjectConfiguration();
	}

	
	/**
	 * Override of toString use for the JTree in order to set the name of a
	 * category.
	 */
	@Override
	public String toString() {
		return getName();
	}

	
	/**
	 * Open a file choose and set the text field with the chosen value
	 * @param title title of the open dialog
	 * @param currentFile Name of the current log file
	 * @param textField a {@link JTextField}
	 * @param chooseFile true to choose a file, false to choose a directory
	 */
	protected void browse(String title, File currentFile, JTextField textField,	boolean chooseFile) {
		JFileChooser jfc = new JFileChooser();
		if (chooseFile) {
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		} else {
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		if ((currentFile != null) && (currentFile.getName() != null) && (!currentFile.getName().equals(""))) {
			// if the current file exist we select this file
			jfc.setSelectedFile(currentFile.getAbsoluteFile());
		} else {
			if ((projectConfiguration.getDefaultDirectory() != null) && (!projectConfiguration.getDefaultDirectory().equals(""))) {
				// if the current file doesn't exist but if the default
				// directory is set we select this directory
				jfc.setCurrentDirectory(new File(projectConfiguration.getDefaultDirectory()));
			}
		}
		jfc.setDialogTitle(title);
		int returnVal = jfc.showSaveDialog(getRootPane());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			textField.setText(jfc.getSelectedFile().toString());
		}
	}
}
