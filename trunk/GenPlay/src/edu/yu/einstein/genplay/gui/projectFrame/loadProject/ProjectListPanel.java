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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.yu.einstein.genplay.core.manager.recording.ProjectInformation;
import edu.yu.einstein.genplay.core.manager.recording.RecordingManager;
import edu.yu.einstein.genplay.exception.ExceptionManager;

/**
 * This class displays the 5 last projects recorded.
 * @author Nicolas Fourel
 * @author Julien Lajugie
 */
class ProjectListPanel extends JPanel {

	private static final long serialVersionUID = 2899780798513668868L; //generated ID

	private static final 	String 					OTHER_NAME = "Select a File";	// Name for the field uses to load an existing projects
	private 				GridBagConstraints 		gbc;							// Constraints for GridBagLayout
	private 				Map<JRadioButton, File> projectList;					// The list of existing project
	private					ProjectInformation[] 	projects;						// The list of project information
	private 				ButtonGroup 			group;							// The button group
	private 				JRadioButton 			radioOther;						// The radio button for the field uses to load an existing projects
	private 				ProjectChooserPanel 	projectChooserPanel;			// The chooser file to choose an other project
	private final			LoadProjectPanel		loadProjectPanel;				// The load project object


	/**
	 * Constructor of {@link ProjectListPanel}
	 * @param loadProjectPanel the load project object
	 */
	ProjectListPanel (LoadProjectPanel loadProjectPanel) {
		super();
		this.loadProjectPanel = loadProjectPanel;
	}


	/**
	 * This method adds radio button to the panel.
	 * Radio button is added below the previous one.
	 * @param radio	the radio button to add
	 */
	private void addRadioToPanel (JRadioButton radio) {
		gbc.gridy++;
		add(radio, gbc);
	}


	/**
	 * This method creates the "other" radio button.
	 * It must be treated separately from others radio button.
	 * Listeners and label are different.
	 */
	private void buildButtonOther () {
		//Button other
		radioOther = new JRadioButton();
		radioOther.setText(OTHER_NAME);
		radioOther.setOpaque(false);

		radioOther.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				String selectedPath = null;
				if (projectChooserPanel != null) {
					selectedPath = projectChooserPanel.getSelectedPath();
					projectChooserPanel.setEnabled(radioOther.isSelected());
				}
				loadProjectPanel.showProjectInformation(getProjectInformationOf(selectedPath));
			}
		});

		//Group
		group.add(radioOther);
		if (group.getSelection() == null) {
			group.setSelected(radioOther.getModel(), true);
		}

		addRadioToPanel(radioOther);

		//List
		projectList.put(radioOther, null);
	}


	/**
	 * This method generate automatically every radio button in order to choose one of the last 5 projects.
	 * @param projectPath	list of the 5 last project paths
	 */
	private void buildProjectList () {
		RecordingManager.getInstance().getRecentProjectRecording().refresh();
		projects = RecordingManager.getInstance().getRecentProjectRecording().getProjects();

		projectList = new HashMap<JRadioButton, File>();
		group = new ButtonGroup();
		for (int i = 0; i < 5; i++) {
			boolean isLatestProject = true;
			final JRadioButton radio = new JRadioButton();

			//File
			if (projects[i] != null) {
				File file = projects[i].getFile();
				radio.setOpaque(false);
				radio.setText(file.getName());
				radio.setName(file.getPath());
				radio.setToolTipText(file.getPath());

				radio.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						loadProjectPanel.showProjectInformation(getProjectInformationOf(radio.getName()));
					}
				});

				if (isLatestProject) {
					radio.setSelected(true);
					loadProjectPanel.showProjectInformation(getProjectInformationOf(radio.getName()));
					isLatestProject = false;
				}

				//Group
				group.add(radio);

				addRadioToPanel(radio);

				//List
				projectList.put(radio, projects[i].getFile());
			}
		}
	}


	/**
	 * This method check wich radio button has been selected.
	 * If the user choose "other" without choose a folder, an error box dialog appears.
	 * @return the File object to load
	 */
	File getFileProjectToLoad () {
		for (JRadioButton radio: projectList.keySet()) {
			if (radio.isSelected()) {
				if (projectList.get(radio) != null){
					return projectList.get(radio);
				} else {
					JOptionPane.showMessageDialog(getRootPane(), "Please select a project", "Invalid project", JOptionPane.WARNING_MESSAGE);
				}
			}
		}
		return null;
	}


	/**
	 * Retrieves the project information using the path of a file.
	 * @param filePath path of the project
	 * @return the project information or null if it does not exist (eg: invalid path)
	 */
	private ProjectInformation getProjectInformationOf (String filePath) {
		ProjectInformation projectInformation = null;
		if (filePath != null) {
			File file = new File(filePath);
			if (file.exists()) {
				for (ProjectInformation currentProjectInformation: projects) {
					if ((currentProjectInformation != null) && currentProjectInformation.getFile().getPath().equals(filePath)) {
						projectInformation = currentProjectInformation;
					}
				}
				try {
					projectInformation = RecordingManager.getInstance().getRecentProjectRecording().getProjectInformation(file);
				} catch (Exception e) {
					ExceptionManager.getInstance().caughtException(e);
				}
			}
		}
		return projectInformation;
	}


	/**
	 * Initializes the components of the panel with the list of all the project to load.
	 */
	void initComponents() {
		//Layout
		setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = -1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(0, 0, 15, 0);

		//Radio buttons
		buildProjectList();
		gbc.weighty = 0.01;
		gbc.insets = new Insets(0, 0, 0, 0);
		buildButtonOther();

		//Project chooser
		projectChooserPanel = new ProjectChooserPanel(this);
		projectChooserPanel.setEnabled(radioOther.isSelected());
		gbc.gridy++;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(projectChooserPanel, gbc);

		setOpaque(false);
	}


	/**
	 * This method associates a File object to the radio button named "other"
	 * @param file	file choosen by the user
	 */
	void setButtonOther (File file) {
		projectList.put(radioOther, file);
		loadProjectPanel.showProjectInformation(getProjectInformationOf(file.getPath()));
	}
}
