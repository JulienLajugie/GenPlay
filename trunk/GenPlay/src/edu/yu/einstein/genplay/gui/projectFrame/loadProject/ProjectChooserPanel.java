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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.gui.fileFilter.GenPlayProjectFilter;
import edu.yu.einstein.genplay.util.Utils;

/**
 * This class displays a field to write a project path.
 * A file dialog box is also available.
 * @author Nicolas Fourel
 * @author Julien Lajugie
 */
class ProjectChooserPanel extends JPanel {

	private static final long serialVersionUID = 8028393644918726073L; //generated ID

	private final ProjectListPanel 	projectListPanel;	// Panel containing the list of projects
	private final JTextField 		path;				// The path of the selected project


	/**
	 * Constructor of {@link ProjectChooserPanel}
	 * @param projectListPanel	Panel containing the list of projects
	 */
	protected ProjectChooserPanel (final ProjectListPanel projectListPanel) {
		//Misc
		this.projectListPanel = projectListPanel;

		//path
		path = new JTextField();

		//Choose button
		JButton chooseProject = new JButton("Open");
		chooseProject.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				FileFilter[] fileFilters = {new GenPlayProjectFilter()};
				File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Project", fileFilters, true);
				if (selectedFile != null) {
					path.setText(selectedFile.getPath());
					getProjectListPanel().setButtonOther(selectedFile);
				}
			}
		});

		setOpaque(false);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		add(path, gbc);

		gbc.weightx = 0;
		gbc.gridx = 1;
		add(chooseProject, gbc);
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


	@Override
	public void setEnabled(boolean enabled) {
		for (Component c: getComponents()) {
			c.setEnabled(enabled);
		}
		super.setEnabled(enabled);
	}
}
