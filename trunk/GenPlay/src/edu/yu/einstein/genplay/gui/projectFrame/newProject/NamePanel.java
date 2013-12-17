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
package edu.yu.einstein.genplay.gui.projectFrame.newProject;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;

/**
 * This class manages the name project information.
 * @author Nicolas Fourel
 * @version 0.1
 */
class NamePanel extends JPanel {

	private static final long serialVersionUID = 3176088024132214727L;

	private static final Dimension 	JTF_DIM = new Dimension(250, 24);	// Name text field size
	private final JLabel 			jlName;								// Name label
	private final JTextField 		jtName;								// Name text field


	/**
	 * Constructor of {@link NamePanel}
	 */
	protected NamePanel () {
		//Size
		setSize(ProjectFrame.NAME_DIM);
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setMaximumSize(getSize());

		//Background
		setBackground(ProjectFrame.NAME_COLOR);

		//Name label
		jlName = new JLabel("Name: ");

		//Name text field
		jtName = new JTextField(NewProjectPanel.DEFAULT_PROJECT_NAME);
		jtName.setPreferredSize(JTF_DIM);
		jtName.setMinimumSize(JTF_DIM);

		//Layout
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		//Insets
		Insets labelInsets = new Insets (10, 15, 10, 0);
		Insets jtfInsets = new Insets (0, 15, 0, 0);

		//jlClade
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.weighty = 0;
		add(jlName, gbc);

		//jcClade
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = jtfInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jtName, gbc);
	}


	/**
	 * @return the name of the project
	 */
	protected String getProjectName() {
		return jtName.getText();
	}

}
