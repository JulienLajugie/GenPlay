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
package edu.yu.einstein.genplay.gui.statusBar;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;


/**
 * Stop button of the status bar. The button stops the current operation in the thread pool when clicked
 * @author Julien Lajugie
 * @version 0.1
 */
final class StopButton extends JButton implements ActionListener {
	
	private static final long serialVersionUID = 8260242568878040712L; 		// generated ID	
	private static final Color ENABLED_COLOR = Color.red;					// color of the button when enabled
	private static final Color DISABLED_COLOR = new Color(200, 175, 175);	// color of the button when disabled
	private Stoppable stoppable = null;	// stoppable to stop when the button is clicked
	
	
	/**
	 * Creates an instance of a {@link StopButton}
	 */
	StopButton() {
		setBackground(Color.red);
		setMargin(new Insets(4, 4, 4, 4));
		setFocusPainted(false);
		setEnabled(false);
		addActionListener(this);
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
	
	
	/**
	 * Changes the color of the button when the enabled state changes
	 */
	@Override
	public void setEnabled(boolean b) {
		if (b) {
			setBackground(ENABLED_COLOR);
		} else {
			setBackground(DISABLED_COLOR);
		}
		super.setEnabled(b);		
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
}
