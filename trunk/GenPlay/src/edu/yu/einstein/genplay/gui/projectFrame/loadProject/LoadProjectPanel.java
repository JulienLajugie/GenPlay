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
package edu.yu.einstein.genplay.gui.projectFrame.loadProject;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.manager.recording.ProjectInformation;
import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;

/**
 * This class displays radio buttons to load the 5 last projects,
 * it also lets users choosing an older project.
 * @author Nicolas Fourel
 */
public class LoadProjectPanel extends JPanel {

	private static final long serialVersionUID = 661493677940668400L; //generated ID

	private final ProjectListPanel 			projectListPanel;		// Panel containing the list of projects
	private final ProjectInformationPanel 	projectInformationPanel;	// Panel about project informatiom


	/**
	 * Constructor of {@link LoadProjectPanel}
	 */
	public LoadProjectPanel () {
		super();
		projectInformationPanel = new ProjectInformationPanel();
		projectListPanel = new ProjectListPanel(this);
		init();
	}


	/**
	 * @return the chosen file project to load
	 */
	public File getFileProjectToLoad() {
		return projectListPanel.getFileProjectToLoad();
	}


	/**
	 * Main method of the class.
	 * It initializes the {@link LoadProjectPanel} panel.
	 */
	private void init() {
		//Size
		setSize(ProjectFrame.LOAD_DIM);
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
		add(projectInformationPanel, gbc);

		//projectList
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.insets = new Insets(0, 70, 0, 0);
		add(projectListPanel, gbc);

		//Misc
		setVisible(false);
		setBackground(ProjectFrame.LOAD_COLOR);
	}


	/**
	 * Reinitializes the panel with the list of all the projects to load
	 */
	public void reinitProjectFileList() {
		projectListPanel.removeAll();
		projectListPanel.initComponents();
	}


	/**
	 * Give the order to the project information panel to display
	 * project information from the given path.
	 * @param path	path of the project
	 */
	protected void showProjectInformation (ProjectInformation projectInformation) {
		projectInformationPanel.showProjectInformation(projectInformation);
	}
}
