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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * Panel of the {@link OptionDialog} that allows to <br/>
 * - choose the default directory <br/>
 * - choose the log file <br/>
 * - choose the look and feel
 * @author Julien Lajugie
 * @version 0.1
 */
final class GeneralOptionPanel extends OptionPanel {

	private static final long serialVersionUID = 3540849857411182507L; // generated ID
	private final JLabel 		jlDefaultDir; 		// label default directory
	private final JTextField 	jtfDefautlDir; 		// textField default directory
	private final JButton 		jbDefaultDirBrowse; // button browse default directory
	private final JLabel 		jlLogFile; 			// label log file
	private final JTextField 	jtfLogFile; 		// textField log file
	private final JButton 		jbLogBrowse; 		// button browse log file
	private final JLabel 		jlLookAndFeel; 		// label look and feel
	private final JComboBox 	jcbLookAndFeel; 	// comboBox look and feel

	
	/**
	 * Creates an instance of {@link GeneralOptionPanel}
	 */
	GeneralOptionPanel() {
		super("General");

		jlDefaultDir = new JLabel("Default directory: ");
		if (projectConfiguration.getDefaultDirectory() != null) {
			jtfDefautlDir = new JTextField(new File(projectConfiguration.getDefaultDirectory()).getPath());
		} else {
			jtfDefautlDir = new JTextField();
		}
		jtfDefautlDir.setColumns(30);
		jtfDefautlDir.setEditable(false);

		jbDefaultDirBrowse = new JButton("Browse");
		jbDefaultDirBrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				browse("Default Directory:", new File(projectConfiguration.getDefaultDirectory()), jtfDefautlDir, false);
				projectConfiguration.setDefaultDirectory(jtfDefautlDir.getText());
			}
		});

		jlLogFile = new JLabel("Log file: ");

		jtfLogFile = new JTextField(new File(projectConfiguration.getLogFile()).getAbsolutePath());
		jtfLogFile.setColumns(30);
		jtfLogFile.setEditable(false);

		jbLogBrowse = new JButton("Browse");
		jbLogBrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				browse("Log File:",	new File(projectConfiguration.getLogFile()), jtfLogFile, true);
				projectConfiguration.setLogFile(jtfLogFile.getText());
			}
		});

		jlLookAndFeel = new JLabel("Look and feel:  ");
		// Retrieve the list of installed look and feel
		LookAndFeelInfo[] lafi = UIManager.getInstalledLookAndFeels();
		String[] installedAndFeelClassNames = new String[lafi.length];
		for (int i = 0; i < lafi.length; i++) {
			installedAndFeelClassNames[i] = lafi[i].getName();
		}

		jcbLookAndFeel = new JComboBox(installedAndFeelClassNames);
		// Select the look and feel of the configuration
		for (int i = 0; i < lafi.length; i++) {
			if (lafi[i].getClassName().equals(projectConfiguration.getLookAndFeel())) {
				jcbLookAndFeel.setSelectedIndex(i);
			}
		}
		jcbLookAndFeel.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				projectConfiguration.setLookAndFeel(UIManager.getInstalledLookAndFeels()[jcbLookAndFeel.getSelectedIndex()].getClassName());
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 0, 0);
		add(jlDefaultDir, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(0, 0, 0, 0);
		add(jtfDefautlDir, c);

		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		add(jbDefaultDirBrowse, c);

		c.gridx = 0;
		c.gridy = 3;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 0, 0);
		add(jlLogFile, c);

		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(0, 0, 0, 0);
		add(jtfLogFile, c);

		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		add(jbLogBrowse, c);

		c.gridx = 0;
		c.gridy = 6;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(30, 0, 0, 0);
		add(jlLookAndFeel, c);

		c.gridx = 1;
		c.gridy = 6;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(30, 0, 0, 0);
		add(jcbLookAndFeel, c);
	}
}