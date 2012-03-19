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
package edu.yu.einstein.genplay.gui.dialog.invalidFileDialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.gui.customComponent.customComboBox.CustomFileComboBox;
import edu.yu.einstein.genplay.util.Images;


/**
 * This class is the VCF loader dialog.
 * It displays a table for editing multi-genome VCF settings.
 * Developer can:
 * - show it
 * - close it
 * - set the data
 * - get the data
 * - check settings validity
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class InvalidFileDialog extends JDialog {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = -6703399045694111551L;

	/** Return value when OK has been clicked. */
	public static final 	int 			APPROVE_OPTION 		= 0;
	/** Return value when Cancel has been clicked. */
	public static final 	int 			CANCEL_OPTION 		= 1;

	private static final 	int 			DIALOG_WIDTH 		= 700;											// width of the dialog

	private 				int				approved 			= CANCEL_OPTION;								// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not

	private String[] invalidFiles;
	private CustomFileComboBox[] correctedFiles;
	
	
	/**
	 * Constructor of {@link InvalidFileDialog}
	 */
	public InvalidFileDialog (String[] invalidFiles) {
		this.invalidFiles = invalidFiles;
		
		// Dimensions
		//Dimension dialogDim = new Dimension(DIALOG_WIDTH, 400);
		//Dimension validationDim = new Dimension(DIALOG_WIDTH, VALIDATION_HEIGHT);

		
		
		


		// Dialog
		setTitle("Invalid File(s) Correction");
		//setSize(dialogDim);
		//setMinimumSize(new Dimension(getMinimumWidth(), getMinimumHeight()));
		setLayout(new BorderLayout());
		add(getTitlePanel(), BorderLayout.NORTH);
		add(getFilesPanel(), BorderLayout.CENTER);
		add(getValidationPanel(), BorderLayout.SOUTH);
		setIconImage(Images.getApplicationImage());
		pack();
	}
	

	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details 
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		setModal(true);
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}


	/**
	 * Closes the VCF loader dialog
	 */
	public void closeDialog () {
		setVisible(false);
	}

	
	/**
	 * @return the upper panel with the description of the dialog
	 */
	private JPanel getTitlePanel () {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Please correct the files");
		panel.add(label);
		return panel;
	}
	

	/**
	 * Information panel contains label about how to use the table
	 * @return the information panel
	 */
	private JPanel getFilesPanel () {
		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		
		correctedFiles = new CustomFileComboBox[invalidFiles.length];
		
		for (int i = 0; i < invalidFiles.length; i++) {
			String path = invalidFiles[i];
			if (path != null) {
				JLabel label = new JLabel(path);
				panel.add(label);
				gbc.gridy++;
				
				correctedFiles[i] = new CustomFileComboBox();
				panel.add(correctedFiles[i]);
				gbc.gridy++;
			} else {
				correctedFiles[i] = null;
			}
		}

		return panel;
	}


	/**
	 * The validation panel contains ok and cancel buttons
	 * @param dimension dimension of the panel
	 * @return			the panel
	 */
	//private JPanel getValidationPanel (Dimension dimension) {
	private JPanel getValidationPanel () {
		Dimension buttonDim = new Dimension(60, 30);
		Insets inset = new Insets(0, 0, 0, 0);

		JButton confirm = new JButton("Ok");
		confirm.setPreferredSize(buttonDim);
		confirm.setToolTipText("Ok");
		confirm.setMargin(inset);
		confirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				approved = APPROVE_OPTION;
				closeDialog();
			}
		});

		JButton cancel = new JButton("Cancel");
		cancel.setPreferredSize(buttonDim);
		cancel.setToolTipText("Cancel");
		cancel.setMargin(inset);
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				approved = CANCEL_OPTION;
				closeDialog();
			}
		});

		JPanel validationPanel = new JPanel();
		validationPanel.add(confirm);
		validationPanel.add(cancel);
		//validationPanel.setSize(dimension);

		return validationPanel;
	}


	/**
	 * @return the array of corrected paths
	 */
	public String[] getCorrectedPaths () {
		String[] correctedPaths = new String[invalidFiles.length];
		
		for (int i = 0; i < correctedFiles.length; i++) {
			if (correctedFiles != null) {
				correctedPaths[i] = ((File) correctedFiles[i].getSelectedItem()).getPath();
			} else {
				correctedPaths[i] = null;
			}
		}
		
		return correctedPaths;
	}
}
