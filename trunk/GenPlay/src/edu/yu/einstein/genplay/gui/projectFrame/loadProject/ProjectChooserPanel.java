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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.core.manager.application.ConfigurationManager;
import edu.yu.einstein.genplay.gui.fileFilter.GenPlayProjectFilter;
import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;
import edu.yu.einstein.genplay.util.Utils;

/**
 * This class displays a field to write a project path.
 * A file dialog box is also available.
 * @author Nicolas Fourel
 */
class ProjectChooserPanel extends JPanel {

	private static final long serialVersionUID = 8028393644918726073L; //generated ID

	private final ProjectListPanel 	projectListPanel;	// Panel containing the list of projects
	private final JTextField 			path;				// The path of the selected project


	/**
	 * Constructor of {@link ProjectChooserPanel}
	 * @param projectListPanel	Panel containing the list of projects
	 */
	protected ProjectChooserPanel (final ProjectListPanel projectListPanel) {
		//Misc
		this.projectListPanel = projectListPanel;
		setVisible(false);
		setBackground(ProjectFrame.LOAD_COLOR);

		//Size
		setPreferredSize(ProjectFrame.PROJECT_CHOOSER_DIM);
		setSize(ProjectFrame.PROJECT_CHOOSER_DIM);
		setMinimumSize(ProjectFrame.PROJECT_CHOOSER_DIM);
		setMaximumSize(ProjectFrame.PROJECT_CHOOSER_DIM);

		//layout
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(10);
		borderLayout.setVgap(0);
		setLayout(borderLayout);

		//path
		path = new JTextField();
		Dimension pathDim = new Dimension (100, 10);
		path.setSize(pathDim);
		path.setPreferredSize(pathDim);
		path.setMaximumSize(pathDim);

		//Choose button
		JButton chooseProject = new JButton("...");
		Dimension addDim = new Dimension(getHeight(), getHeight());
		chooseProject.setSize(addDim);
		chooseProject.setPreferredSize(addDim);
		chooseProject.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
				FileFilter[] fileFilters = {new GenPlayProjectFilter()};
				File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Project", defaultDirectory, fileFilters, true);
				if (selectedFile != null) {
					path.setText(selectedFile.getPath());
					getProjectListPanel().setButtonOther(selectedFile);
				}
			}
		});

		//Add
		add(path, BorderLayout.CENTER);
		add(chooseProject, BorderLayout.EAST);
	}


	/**
	 * This method is used in the chooseProject button listener.
	 * @return the project list panel
	 */
	private ProjectListPanel getProjectListPanel() {
		return projectListPanel;
	}


	public String getSelectedPath() {
		return path.getText();
	}

}
