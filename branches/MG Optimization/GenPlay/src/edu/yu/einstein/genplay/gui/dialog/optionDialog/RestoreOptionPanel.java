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
package edu.yu.einstein.genplay.gui.dialog.optionDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * Panel of the {@link OptionDialog} that allows to restore the config file
 * @author Julien Lajugie
 * @version 0.1
 */
final class RestoreOptionPanel extends OptionPanel {

	private static final long serialVersionUID = 32933937591821971L; // generated ID
	private final JLabel jlRestore; 	// Label restore
	private final JButton jbRestore; 	// Button restore

	
	/**
	 * Creates an instance of {@link RestoreOptionPanel}
	 */
	RestoreOptionPanel() {
		super("Restore Default");
		jlRestore = new JLabel("Restore default configuration:");
		jbRestore = new JButton("Restore");
		jbRestore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				configurationManager.restoreDefault();
				firePropertyChange("reset", false, true);
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(10, 0, 0, 20);
		add(jlRestore, c);

		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(10, 20, 0, 0);
		add(jbRestore, c);
	}
}
