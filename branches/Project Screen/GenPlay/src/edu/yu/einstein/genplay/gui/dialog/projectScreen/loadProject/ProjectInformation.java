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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import edu.yu.einstein.genplay.core.manager.ProjectRecordingManager;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.ProjectScreenManager;

/**
 * This class displays project information:
 * - name
 * - genome
 * - type (simple or multi genome)
 * - last modified date
 * - track number
 * @author Nicolas Fourel
 */
class ProjectInformation extends JPanel {
	
	private static final long serialVersionUID = 970034695447470123L;
	
	private final static Dimension PANEL_DIM = new Dimension (ProjectScreenManager.getLoadDim().width - 70, 170);
	
	private JLabel jlProjectName;			// Label name for project name
	private JLabel jlProjectGenome;			// Label name for project genome
	private JLabel jlProjectType;			// Label name for project type
	private JLabel jlProjectDate;			// Label name for project date
	private JLabel jlProjectTrackNumber;	// Label name for project track number
	
	private JLabel projectName;				// Label information for project name
	private JLabel projectGenome;			// Label information for project genome
	private JLabel projectType;				// Label information for project type
	private JLabel projectDate;				// Label information for project date
	private JLabel projectTrackNumber;		// Label information for project track number
	
	
	/**
	 * Constructor of {@link ProjectInformation}
	 */
	protected ProjectInformation () {
		//Misc
		setBackground(ProjectScreenManager.getLoadColor());
		setBorder(BorderFactory.createTitledBorder("Information"));
		
		//Size
		setSize(PANEL_DIM);
		setPreferredSize(PANEL_DIM);
		setMinimumSize(PANEL_DIM);
		
		//Fields name
		jlProjectName = new JLabel("Name");
		jlProjectGenome = new JLabel("Genome");
		jlProjectType = new JLabel("Project type");
		jlProjectDate = new JLabel("Last modified");
		jlProjectTrackNumber = new JLabel("Track number");
		
		//Fields label
		projectName = new JLabel("");
		projectGenome = new JLabel("");
		projectType = new JLabel("");
		projectDate = new JLabel("");
		projectTrackNumber = new JLabel("");
		
		//Layout
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		//Insets
		Insets topInsets = new Insets (0, 10, 10, 0);
		Insets labelInsets = new Insets (0, 10, 10, 0);
		Insets infoInsets = new Insets (0, 0, 10, 0);
		Insets bottomInsets = new Insets (0, 10, 10, 0);
		
		//jlProjectName
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = topInsets;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jlProjectName, gbc);
		
		//projectName
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = infoInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(projectName, gbc);
		
		//jlProjectGenome
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jlProjectGenome, gbc);
		
		//projectGenome
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = infoInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(projectGenome, gbc);
		
		//jlProjectType
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jlProjectType, gbc);
		
		//projectType
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.insets = infoInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(projectType, gbc);
		
		//jlProjectDate
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jlProjectDate, gbc);
		
		//projectDate
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.insets = infoInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(projectDate, gbc);
		
		//jlProjectTrackNumber
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.insets = bottomInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jlProjectTrackNumber, gbc);
		
		//projectTrackNumber
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.insets = infoInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(projectTrackNumber, gbc);
	}
	
	
	/**
	 * This method sets labels information
	 * @param path	path of the project
	 */
	protected void showProjectInformation (String path) {
		List<String> info = getInformation(path);
		projectName.setText(info.get(0));
		projectGenome.setText(info.get(1));
		projectType.setText(info.get(2));
		projectDate.setText(info.get(3));
		projectTrackNumber.setText(info.get(4));
	}
	
	
	/**
	 * This method create an information list to fill information labels.
	 * It gets information from the project record if it exists.
	 * @param path	path of the project record
	 * @return	a list of String (information)
	 */
	private List<String> getInformation (String path) {
		List<String> info = new ArrayList<String>();
		if (path != null) {
			File file = new File(path);
			try {
				info = ProjectRecordingManager.getInstance().getProjectHeader(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			info.add("...");
			info.add("...");
			info.add("...");
			info.add("...");
			info.add("...");
		}
		return info;
	}
	
}