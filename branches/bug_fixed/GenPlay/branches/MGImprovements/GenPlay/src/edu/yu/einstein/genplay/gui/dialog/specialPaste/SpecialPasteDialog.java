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
package edu.yu.einstein.genplay.gui.dialog.specialPaste;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.util.Images;


/**
 * Dialog allowing to change the configuration of the program.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SpecialPasteDialog extends JDialog {

	/** Generate default serial version ID */
	private static final long serialVersionUID = 4050757943368845382L; // Generated ID
	/** Return value when OK has been clicked. */
	public static final int APPROVE_OPTION = 0;
	/** Return value when Cancel has been clicked. */
	public static final int CANCEL_OPTION = 1;

	// Option
	private int 			approved = CANCEL_OPTION; 		// Equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not

	private static SpecialPasteDialog 	instance;			// Instance of the class

	// Name items
	private JRadioButton jrNamePaste;
	private JRadioButton jrNameDoNothing;
	private JRadioButton jrNameNew;
	private JTextField jtfNameNew;

	// Data items
	private JRadioButton jrDataPaste;
	private JRadioButton jrDataDoNothing;

	// Mask items
	private JRadioButton jrMaskPaste;
	private JRadioButton jrMaskDoNothing;

	// MG items
	private JRadioButton jrMGPaste;
	private JRadioButton jrMGDoNothing;


	/**
	 * @return an instance of a {@link SpecialPasteDialog}.
	 * Makes sure that there is only one unique instance as specified in the singleton pattern
	 */
	public static SpecialPasteDialog getInstance() {
		if (instance == null) {
			instance = new SpecialPasteDialog();
		}
		return instance;
	}


	/**
	 * Creates an instance of {@link SpecialPasteDialog}
	 */
	private SpecialPasteDialog() {
		super();

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		// Add the "Name" panel
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(7, 10, 0, 0);
		add(getNamePanel(), gbc);

		// Add the "Data" panel
		gbc.gridy++;
		add(getDataPanel(), gbc);

		// Add the "Mask" panel
		gbc.gridy++;
		add(getMaskPanel(), gbc);

		// Add the "MG" panel
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			gbc.gridy++;
			add(getMultiGenomePanel(), gbc);
		}

		// Add the "Validation" panel
		gbc.gridy++;
		gbc.weighty = 1;
		add(getValidationPanel(), gbc);

		// Define dialog settings
		setTitle("Special Paste Settings");
		setIconImage(Images.getApplicationImage());
		setResizable(false);
		setModal(true);
		setLocationRelativeTo(getRootPane());
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		pack();
	}


	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details
	 * @param trackName name of the copied track
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showSpecialPasteDialog(Component parent, String trackName) {
		jtfNameNew.setText("Copy of " + trackName);
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}


	/**
	 * @return the new name of the track, null otherwise
	 */
	public String getNewName () {
		if (jtfNameNew.isEnabled()) {
			return jtfNameNew.getText();
		}
		return null;
	}


	/**
	 * @return true if the name has to be paste, false otherwise
	 */
	public boolean hasToPasteName () {
		return jrNamePaste.isSelected();
	}


	/**
	 * @return true if data has to be paste, false otherwise
	 */
	public boolean hasToPasteData () {
		return jrDataPaste.isSelected();
	}


	/**
	 * @return true if the mask has to be paste, false otherwise
	 */
	public boolean hasToPasteMask () {
		return jrMaskPaste.isSelected();
	}


	/**
	 * @return true if the MG information has to be paste, false otherwise
	 */
	public boolean hasToPasteMultiGenome () {
		if (jrMGPaste != null) {
			return jrMGPaste.isSelected();
		}
		return false;
	}


	/**
	 * @return the panel to define "Name" settings
	 */
	private JPanel getNamePanel () {
		// Create the new panel
		JPanel panel = new JPanel();

		// Set the layout manager
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		// Set items
		JLabel title = new JLabel("Name");
		jrNamePaste = new JRadioButton("Paste");
		jrNameDoNothing = new JRadioButton("Do nothing");
		jrNameNew = new JRadioButton("Define new name:");
		jtfNameNew = new JTextField("");
		ButtonGroup group = new ButtonGroup();
		group.add(jrNamePaste);
		group.add(jrNameDoNothing);
		group.add(jrNameNew);
		jrNameNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {jtfNameNew.setEnabled(true);}
		});
		jrNamePaste.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {jtfNameNew.setEnabled(false);}
		});
		jrNameDoNothing.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {jtfNameNew.setEnabled(false);}
		});
		jrNameNew.setSelected(true);

		//Dimension
		int height = jtfNameNew.getFontMetrics(jtfNameNew.getFont()).getHeight() + 5;
		Dimension dimension = new Dimension(200, height);
		jtfNameNew.setPreferredSize(dimension);
		jtfNameNew.setMinimumSize(dimension);

		// Add the title
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		panel.add(title, gbc);

		// Add the first option
		gbc.gridy++;
		gbc.insets = new Insets(0, 20, 0, 0);
		panel.add(jrNamePaste, gbc);

		// Add the second option
		gbc.gridy++;
		gbc.insets = new Insets(0, 20, 0, 0);
		panel.add(jrNameDoNothing, gbc);

		// Add the second option
		gbc.gridy++;
		panel.add(jrNameNew, gbc);

		// Add the text field
		gbc.gridy++;
		gbc.insets = new Insets(0, 40, 0, 10);
		gbc.weighty = 1;
		panel.add(jtfNameNew, gbc);

		// Return the panel
		return panel;
	}


	/**
	 * @return the panel to define "Data" settings
	 */
	private JPanel getDataPanel () {
		// Create the new panel
		JPanel panel = new JPanel();

		// Set the layout manager
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		// Set items
		JLabel title = new JLabel("Data:");
		jrDataPaste = new JRadioButton("Paste");
		jrDataDoNothing = new JRadioButton("Do nothing");
		ButtonGroup group = new ButtonGroup();
		group.add(jrDataPaste);
		group.add(jrDataDoNothing);
		jrDataPaste.setSelected(true);

		// Add the title
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		panel.add(title, gbc);

		// Add the first option
		gbc.gridy++;
		gbc.insets = new Insets(0, 20, 0, 0);
		panel.add(jrDataPaste, gbc);

		// Add the second option
		gbc.gridy++;
		gbc.weighty = 1;
		panel.add(jrDataDoNothing, gbc);

		// Return the panel
		return panel;
	}


	/**
	 * @return the panel to define "Mask" settings
	 */
	private JPanel getMaskPanel () {
		// Create the new panel
		JPanel panel = new JPanel();

		// Set the layout manager
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		// Set items
		JLabel title = new JLabel("Mask:");
		jrMaskPaste = new JRadioButton("Paste");
		jrMaskDoNothing = new JRadioButton("Do nothing");
		ButtonGroup group = new ButtonGroup();
		group.add(jrMaskPaste);
		group.add(jrMaskDoNothing);
		jrMaskPaste.setSelected(true);

		// Add the title
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		panel.add(title, gbc);

		// Add the first option
		gbc.gridy++;
		gbc.insets = new Insets(0, 20, 0, 0);
		panel.add(jrMaskPaste, gbc);

		// Add the second option
		gbc.gridy++;
		gbc.weighty = 1;
		panel.add(jrMaskDoNothing, gbc);

		// Return the panel
		return panel;
	}


	/**
	 * @return the panel to define the multi genome information (stripes/filters) settings
	 */
	private JPanel getMultiGenomePanel () {
		// Create the new panel
		JPanel panel = new JPanel();

		// Set the layout manager
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		// Set items
		JLabel title = new JLabel("Multi Genome (Stripes & Filters):");
		jrMGPaste = new JRadioButton("Paste");
		jrMGDoNothing = new JRadioButton("Do nothing");
		ButtonGroup group = new ButtonGroup();
		group.add(jrMGPaste);
		group.add(jrMGDoNothing);
		jrMGPaste.setSelected(true);

		// Add the title
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		panel.add(title, gbc);

		// Add the first option
		gbc.gridy++;
		gbc.insets = new Insets(0, 20, 0, 0);
		panel.add(jrMGPaste, gbc);

		// Add the second option
		gbc.gridy++;
		gbc.weighty = 1;
		panel.add(jrMGDoNothing, gbc);

		// Return the panel
		return panel;
	}


	/**
	 * Creates the panel that contains OK and CANCEL buttons
	 * @return the panel
	 */
	private JPanel getValidationPanel () {
		// Creates the ok button
		JButton jbOk = new JButton("Ok");
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				approved = APPROVE_OPTION;
				setVisible(false);
			}
		});

		// Creates the cancel button
		JButton jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				approved = CANCEL_OPTION;
				setVisible(false);
			}
		});

		// Creates the panel
		JPanel panel = new JPanel();
		panel.add(jbOk);
		panel.add(jbCancel);

		getRootPane().setDefaultButton(jbOk);

		// Returns the panel
		return panel;
	}
}
