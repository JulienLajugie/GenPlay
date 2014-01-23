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
package edu.yu.einstein.genplay.gui.projectFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * This class manages the button which validates the project screen.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ConfirmPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 2332665074234979205L;	// generated ID
	private final JButton valid;	// Button to valid the project screen


	/**
	 * Constructor of {@link ConfirmPanel}
	 */
	protected ConfirmPanel () {
		//Size
		setSize(ProjectFrame.CONFIRM_DIM);
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setMaximumSize(getSize());

		//Valid button
		valid = new JButton("Create");
		valid.addActionListener(this);

		//Background color
		setBackground(ProjectFrame.CONFIRM_COLOR);

		//Add valid button
		add(valid);
		// set valid button as the default button of the project screen manager
		ProjectFrame.getInstance().getRootPane().setDefaultButton(valid);
	}


	/**
	 * This method run the right function if the user chooses to create a new project
	 * or to load an existing project.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String name = ((JButton)(arg0.getSource())).getText();
		if (name.equals(ProjectFrame.CREATE_BUTTON)) {
			ProjectFrame.getInstance().confirmCreate();
		} else {
			ProjectFrame.getInstance().confirmLoading();
		}
	}


	/**
	 * Change the text on the valid button
	 * @param s	new text
	 */
	protected void setConfirmButton (String s) {
		valid.setText(s);
	}
}
