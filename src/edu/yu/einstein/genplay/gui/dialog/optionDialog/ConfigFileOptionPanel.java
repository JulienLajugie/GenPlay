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
package edu.yu.einstein.genplay.gui.dialog.optionDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;


/**
 * @author Julien Lajugie
 * @version 0.1
 */
final class ConfigFileOptionPanel extends OptionPanel {

	private static final long serialVersionUID = 4936841930455874582L; // generated ID
	private final JLabel 		jlZoomFile; 	// Label zoom file
	private final JTextField 	jtfZoomFile; 	// TextField zoom file
	private final JButton 		jbZoomBrowse; 	// Button browse zoom file
	private final JLabel 		jlRestart;		// label telling the user to restart the application

	
	/**
	 * Creates an instance of {@link ConfigFileOptionPanel}
	 */
	ConfigFileOptionPanel() {
		super("Configuration Files");
		jlZoomFile = new JLabel("Zoom configuration file: ");
		if ((configurationManager.getZoomFile() == null) || (configurationManager.getZoomFile().equals(""))) {
			jtfZoomFile = new JTextField();
		} else {
			jtfZoomFile = new JTextField(new File(configurationManager.getZoomFile()).getAbsolutePath());
		}
		jtfZoomFile.setColumns(30);
		jtfZoomFile.setEditable(false);

		jbZoomBrowse = new JButton("Browse");
		jbZoomBrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				browse("Zoom File:", new File(configurationManager.getZoomFile()), jtfZoomFile, true);
				configurationManager.setZoomFile(jtfZoomFile.getText());
			}
		});

		jlRestart = new JLabel("Restart the application to take these modifications into account");

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 0, 0);
		add(jlZoomFile, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(0, 0, 0, 0);
		add(jtfZoomFile, c);

		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		add(jbZoomBrowse, c);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.insets = new Insets(50, 0, 0, 0);
		c.anchor = GridBagConstraints.CENTER;
		add(jlRestart, c);
	}
}
