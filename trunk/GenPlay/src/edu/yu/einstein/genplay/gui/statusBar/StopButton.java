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
package edu.yu.einstein.genplay.gui.statusBar;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import edu.yu.einstein.genplay.util.Images;


/**
 * Stop button of the status bar. The button stops the current operation in the thread pool when clicked
 * @author Julien Lajugie
 * @version 0.1
 */
final class StopButton extends JButton implements ActionListener {

	private static final long serialVersionUID = 8260242568878040712L; 		// generated ID
	private Stoppable stoppable = null;	// stoppable to stop when the button is clicked


	/**
	 * Creates an instance of a {@link StopButton}
	 */
	StopButton() {
		super();
		setIcon(new ImageIcon(Images.getStopImage()));
		setRolloverIcon(new ImageIcon(Images.getStopRolledOverImage()));
		setDisabledIcon(new ImageIcon(Images.getStopDisabledImage()));
		setBorderPainted(false);
		setFocusPainted(false);
		setMargin(new Insets(0, 0, 0, 0));
		setContentAreaFilled(false);
		setPreferredSize(new Dimension(16, 16));
		setSize(new Dimension(16, 16));
		setMaximumSize(new Dimension(16, 16));
		setEnabled(false);
		addActionListener(this);
	}


	/**
	 * Stops the stoppable when the button is clicked if the stoppable is not null.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (stoppable != null) {
			stoppable.stop();
		}
	}


	/**
	 * Sets the stoppable to stop when the button is clicked.
	 * Disables the button if the stoppable is null
	 * @param stoppable a {@link Stoppable}
	 */
	void setStoppable(Stoppable stoppable) {
		this.stoppable = stoppable;
		setEnabled(stoppable != null);
	}
}
