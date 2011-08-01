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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import edu.yu.einstein.genplay.core.manager.ConfigurationManager;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.ProjectScreen;
import edu.yu.einstein.genplay.gui.fileFilter.GenPlayProjectFilter;

/**
 * This class displays a field to write a project path.
 * A file dialog box is also available.
 * @author Nicolas Fourel
 */
class ProjectChooser extends JPanel {
	
	private static final long serialVersionUID = 8028393644918726073L;
	
	private ProjectList 	projectList;	// Panel containing the list of projects
	private JFileChooser 	chooser;		// The file chooser panel, to select a project 
	private JTextField 		path;			// The path of the selected project
	
	
	/**
	 * Constructor of {@link ProjectChooser}
	 * @param projectList	Panel containing the list of projects
	 */
	protected ProjectChooser (final ProjectList projectList) {
		//Misc
		this.projectList = projectList;
		setVisible(false);
		setBackground(ProjectScreen.getLoadColor());
		
		//Size
		setPreferredSize(ProjectScreen.getProjectChooserDim());
		setSize(ProjectScreen.getProjectChooserDim());
		setMinimumSize(ProjectScreen.getProjectChooserDim());
		setMaximumSize(ProjectScreen.getProjectChooserDim());
		
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
		
		//ProjectChooser
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(ConfigurationManager.getInstance().getDefaultDirectory()));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new GenPlayProjectFilter());
		
		//Choose button
		JButton chooseProject = new JButton("...");
		Dimension addDim = new Dimension(getHeight(), getHeight());
		chooseProject.setSize(addDim);
		chooseProject.setPreferredSize(addDim);
		chooseProject.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = chooser.showOpenDialog(ProjectScreen.getInstance());
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					path.setText(chooser.getSelectedFile().getPath());
					getProjectList().setButtonOther(chooser.getSelectedFile());
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
	private ProjectList getProjectList() {
		return projectList;
	}
	
}