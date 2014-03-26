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
package edu.yu.einstein.genplay.gui.dialog.invalidFileDialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.gui.customComponent.customComboBox.CustomComboBox;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.CustomFileComboBox;
import edu.yu.einstein.genplay.gui.fileFilter.VCFGZFilter;
import edu.yu.einstein.genplay.util.Images;


/**
 * This class allows the user to select new paths in order to replace incorrect paths.
 * Paths to modify are given in an array, null values can be present and will not be taken into account.
 * However, the array containing the corrected paths will be returned according to the index of the given paths.
 * 
 * eg: an array of 5 Strings but only the value on index 2 is not null.
 * After correction, an array of 5 Strings will be returned. This array will contain the new String at the index 2 but null values at the other indexes.
 * 
 * That way, the developer never has to change the length of his array which makes it easier to match former and new paths.
 * 
 * @author Nicolas Fourel
 */
public class InvalidFileDialog extends JDialog {

	/** Generated serial version ID */
	private static final long serialVersionUID = -6703399045694111551L;

	/** Return value when OK has been clicked. */

	public static final 	int 			APPROVE_OPTION 			= 0;

	/** Return value when Cancel has been clicked. */
	public static final 	int 			CANCEL_OPTION 			= 1;


	private 		int						approved 		= CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not
	private 		JPanel 					filePanel;							// the file selection panel
	private 		JPanel 					validationPanel;					// the validation panel
	private 		CustomFileComboBox[] 	correctedFiles;						// the array of combo box containing the corrected files
	private final 	String[] 				files;								// the input files
	private final 	int						inset			= 10;				// unique inset used


	/**
	 * Constructor of {@link InvalidFileDialog}
	 * @param files the array of files
	 */
	public InvalidFileDialog (String[] files) {
		this.files = files;
		// Dialog
		setTitle("Invalid File(s) Correction");
		setIconImages(Images.getApplicationImages());
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setLayout(new BorderLayout());
		add(getTitlePanel(), BorderLayout.NORTH);
		add(getFilesPanel(), BorderLayout.CENTER);
		add(getValidationPanel(), BorderLayout.SOUTH);
		pack();
	}


	/**
	 * @return the array of corrected paths
	 */
	public String[] getCorrectedPaths () {
		String[] correctedPaths = new String[files.length];
		if (correctedFiles != null) {
			for (int i = 0; i < correctedFiles.length; i++) {
				if ((correctedFiles[i] != null) && !correctedFiles[i].getSelectedItem().equals(CustomComboBox.ADD_TEXT)) {
					correctedPaths[i] = ((File) correctedFiles[i].getSelectedItem()).getPath();
				} else {
					correctedPaths[i] = null;
				}
			}
		}
		return correctedPaths;
	}


	/**
	 * Information panel contains label about how to use the table
	 * @return the information panel
	 */
	private JPanel getFilesPanel () {
		filePanel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		filePanel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;

		correctedFiles = new CustomFileComboBox[files.length];

		VCFGZFilter[] filter = {new VCFGZFilter()};

		for (int i = 0; i < files.length; i++) {
			String path = files[i];
			if (path != null) {
				JLabel label = new JLabel(path);
				gbc.insets = new Insets(inset, inset, 0, inset);
				filePanel.add(label, gbc);
				gbc.gridy++;

				correctedFiles[i] = new CustomFileComboBox();
				correctedFiles[i].setFilters(filter);
				gbc.insets = new Insets(0, inset, inset, inset);
				filePanel.add(correctedFiles[i], gbc);
				gbc.gridy++;
			} else {
				correctedFiles[i] = null;
			}
		}

		return filePanel;
	}


	/**
	 * @return the upper panel with the description of the dialog
	 */
	private JPanel getTitlePanel () {
		JPanel panel = new JPanel();
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, inset, 0);
		panel.setLayout(layout);
		JLabel titleLabel = new JLabel("<html>Some files have been moved,<br>please select the new paths.</html>");
		panel.add(titleLabel);
		return panel;
	}


	/**
	 * The validation panel contains ok and cancel buttons
	 * @param dimension dimension of the panel
	 * @return			the panel
	 */
	private JPanel getValidationPanel () {
		Insets inset = new Insets(0, 0, 0, 0);

		JButton confirm = new JButton("Ok");
		confirm.setToolTipText("Ok");
		confirm.setMargin(inset);
		getRootPane().setDefaultButton(confirm);
		confirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				approved = APPROVE_OPTION;
				setVisible(false);
			}
		});

		JButton cancel = new JButton("Cancel");
		cancel.setToolTipText("Cancel");
		cancel.setMargin(inset);
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int n = JOptionPane.showConfirmDialog(null, "If you cancel, the project will not be load. Do you really want to cancel?", "Invalid File(s) Correction", JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION) {
					approved = CANCEL_OPTION;
					setVisible(false);
				}
			}
		});
		validationPanel = new JPanel();
		validationPanel.add(confirm);
		validationPanel.add(cancel);
		return validationPanel;
	}


	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}
}
