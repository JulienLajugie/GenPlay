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
package edu.yu.einstein.genplay.gui.dialog.projectScreen.loadProject;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.gui.dialog.projectScreen.ProjectScreen;

/**
 * This class displays radio buttons to load the 5 last projects,
 * it also lets users choosing an older project. 
 * @author Nicolas Fourel
 */
public class LoadProject extends JPanel {
	
	private static final long serialVersionUID = 661493677940668400L;
	
	private final ProjectList 			projectList;		// Panel containing the list of projects
	private final ProjectInformation 	projectInformation;	// Panel about project informatiom
	
	
	/**
	 * Constructor of {@link LoadProject}
	 * @param projectPath project paths array
	 */
	public LoadProject (String[] projectPath) {
		super();
		projectInformation = new ProjectInformation();
		projectList = new ProjectList(this, projectPath);
		
		init();
	}
	
	
	/**
	 * Main method of the class.
	 * It initializes the {@link LoadProject} panel.
	 */
	private void init() {
		//Size
		setSize(ProjectScreen.getLoadDim());
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setMaximumSize(getSize());
		
		//Layout manager
		GridBagLayout grid = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(grid);
		
		//projectInformation
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.weightx = 1;
		gbc.insets = new Insets(0, 0, 0, 0);
		add(projectInformation, gbc);
		
		//projectList
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.insets = new Insets(0, 70, 0, 0);
		add(projectList, gbc);
		
		//Misc
		setVisible(false);
		setBackground(ProjectScreen.getLoadColor());
	}
	
	
	/**
	 * @return the chosen project
	 */
	public File getProject () {
		return projectList.getProject();
	}
	
	
	/**
	 * Give the order to the project information panel to display
	 * project information from the given path.
	 * @param path	path of the project
	 */
	protected void showProjectInformation (String path) {
		projectInformation.showProjectInformation(path);
	}
	
}