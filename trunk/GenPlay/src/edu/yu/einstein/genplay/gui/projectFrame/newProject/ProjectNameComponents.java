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
package edu.yu.einstein.genplay.gui.projectFrame.newProject;

import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * This class manages the name project information.
 * @author Nicolas Fourel
 * @author Julien Lajugie
 */
class ProjectNameComponents {

	private final JLabel 			jlName;								// Name label
	private final JTextField 		jtName;								// Name text field


	/**
	 * Constructor of {@link ProjectNameComponents}
	 */
	ProjectNameComponents () {
		//Name label
		jlName = new JLabel("Name: ");

		//Name text field
		jtName = new JTextField(NewProjectPanel.DEFAULT_PROJECT_NAME);
	}


	/**
	 * @return  jlName
	 */
	JLabel getJlName() {
		return jlName;
	}


	/**
	 * @return  jtName
	 */
	JTextField getJtName() {
		return jtName;
	}


	/**
	 * @return the name of the project
	 */
	protected String getProjectName() {
		return jtName.getText();
	}
}
