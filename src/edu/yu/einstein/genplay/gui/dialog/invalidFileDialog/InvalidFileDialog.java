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
import java.awt.FlowLayout;
import java.awt.FontMetrics;
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
import edu.yu.einstein.genplay.gui.fileFilter.VCFFilter;
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
 * @version 0.1
 */
public class InvalidFileDialog extends JDialog {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = -6703399045694111551L;

	/** Return value when OK has been clicked. */
	public static final 	int 			APPROVE_OPTION 			= 0;
	/** Return value when Cancel has been clicked. */
	public static final 	int 			CANCEL_OPTION 			= 1;

	private 				int				approved 				= CANCEL_OPTION;								// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not
	private 				int				inset					= 10;				// unique inset used
	private					int				dialogWidth;								// width of the dialog
	private					int				dialogHeight;								// height of the dialog
	private					int				titlePanelHeight		= 40;				// height of the title panel
	private					int				filePanelHeight;							// height of the file selection panel
	private					int				validationPanelHeight	= 40;				// height of the validation panel
	private					int				lineHeight				= 20;				// height of a line in the file selection panel

	private JPanel 					filePanel;											// the file selection panel
	private JPanel 					validationPanel;									// the validation panel
	private String[] 				files;												// the input files
	private CustomFileComboBox[] 	correctedFiles;										// the array of combo box containing the corrected files


	/**
	 * Constructor of {@link InvalidFileDialog}
	 * @param files the array of files
	 */
	public InvalidFileDialog (String[] files) {
		this.files = files;

		// Dimensions
		updateDimensions();
		Dimension dialogDimension = new Dimension(dialogWidth, dialogHeight);
		setSize(dialogDimension);
		setMinimumSize(dialogDimension);
		setPreferredSize(dialogDimension);

		// Dialog
		setTitle("Invalid File(s) Correction");
		setIconImage(Images.getApplicationImage());
		setResizable(false);
		setModal(true);
		setLayout(new BorderLayout());
		add(getTitlePanel(), BorderLayout.NORTH);
		add(getFilesPanel(), BorderLayout.CENTER);
		add(getValidationPanel(), BorderLayout.SOUTH);
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
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, inset, 0);
		panel.setLayout(layout);
		JLabel titleLabel = new JLabel("<html>Some files have been moved,<br>please select the new paths.</html>");
		Dimension titleLabelDimension = new Dimension(dialogWidth, titlePanelHeight);
		titleLabel.setPreferredSize(titleLabelDimension);
		titleLabel.setMinimumSize(titleLabelDimension);
		panel.add(titleLabel);
		return panel;
	}


	/**
	 * Information panel contains label about how to use the table
	 * @return the information panel
	 */
	private JPanel getFilesPanel () {
		filePanel = new JPanel();
		Dimension filePanelDimension = new Dimension(dialogWidth, filePanelHeight);
		filePanel.setPreferredSize(filePanelDimension);
		filePanel.setMinimumSize(filePanelDimension);
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
		Dimension comboDimension = new Dimension(dialogWidth - 2 * inset, lineHeight);

		VCFFilter[] filter = {new VCFFilter()};

		for (int i = 0; i < files.length; i++) {
			String path = files[i];
			if (path != null) {
				JLabel label = new JLabel(path);
				gbc.insets = new Insets(inset, inset, 0, inset);
				filePanel.add(label, gbc);
				gbc.gridy++;

				correctedFiles[i] = new CustomFileComboBox();
				correctedFiles[i].setSize(comboDimension);
				correctedFiles[i].setMinimumSize(comboDimension);
				correctedFiles[i].setPreferredSize(comboDimension);
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
	 * The validation panel contains ok and cancel buttons
	 * @param dimension dimension of the panel
	 * @return			the panel
	 */
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
				int n = JOptionPane.showConfirmDialog(null, "If you cancel, the project will not be load. Do you really want to cancel?", "Invalid File(s) Correction", JOptionPane.YES_NO_OPTION); 
				if (n == JOptionPane.YES_OPTION) {
					approved = CANCEL_OPTION;
					closeDialog();
				}
			}
		});

		validationPanel = new JPanel();
		Dimension validationPanelDimension = new Dimension(dialogWidth, validationPanelHeight);
		validationPanel.setPreferredSize(validationPanelDimension);
		validationPanel.add(confirm);
		validationPanel.add(cancel);

		return validationPanel;
	}


	/**
	 * @return the array of corrected paths
	 */
	public String[] getCorrectedPaths () {
		String[] correctedPaths = new String[files.length];

		for (int i = 0; i < correctedFiles.length; i++) {
			if (correctedFiles != null && !correctedFiles[i].getSelectedItem().equals(CustomComboBox.ADD_TEXT)) {
				correctedPaths[i] = ((File) correctedFiles[i].getSelectedItem()).getPath();
			} else {
				correctedPaths[i] = null;
			}
		}

		return correctedPaths;
	}


	/**
	 * Updates the parameters used to calculate the dimensions
	 */
	private void updateDimensions () {
		dialogWidth = (int) (getMaxLength() * 1.3 + inset * 2);
		int numberOfLines = files.length * 2;
		filePanelHeight = (lineHeight + 2 * inset) * numberOfLines;
		dialogHeight = titlePanelHeight + filePanelHeight + validationPanelHeight;
	}


	/**
	 * @return the longest path length
	 */
	private int getMaxLength () {
		int result = 0;
		JPanel p = new JPanel();
		FontMetrics fm = getFontMetrics(p.getFont());
		for (String path: files) {
			int pathLength = fm.stringWidth(path);
			if (pathLength > result) {
				result = pathLength;
			}
		}
		return result;
	}

}
