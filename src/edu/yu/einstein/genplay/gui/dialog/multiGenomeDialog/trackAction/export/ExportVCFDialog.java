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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.export;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.ExportSettings;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.ExportUtils;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.mainDialog.MultiGenomeTrackActionDialog;
import edu.yu.einstein.genplay.gui.fileFilter.VCFFilter;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ExportVCFDialog extends MultiGenomeTrackActionDialog {

	/** Generated serial version ID */
	private static final long serialVersionUID = -1321930230220361216L;

	private final static String DIALOG_TITLE = "Export as VCF file";

	private JTextField 	jtfFile;		// Text field for the path of the new VCF file
	private JCheckBox 	jcbCompress;	// Check box to compress with BGZIP
	private JCheckBox 	jcbIndex;		// Check box to index with Tabix

	/**
	 * Constructor of {@link ExportVCFDialog}
	 * @param settings the export settings
	 */
	public ExportVCFDialog(ExportSettings settings) {
		super(settings, DIALOG_TITLE);
	}


	@Override
	protected void initializeContentPanel() {
		// Initialize the content panel
		contentPanel = new JPanel();

		// Create the field set effect
		TitledBorder titledBorder = BorderFactory.createTitledBorder("Export settings");
		contentPanel.setBorder(titledBorder);

		// Create the layout
		BorderLayout layout = new BorderLayout();
		contentPanel.setLayout(layout);

		// Add panels
		contentPanel.add(getVCFPanel(), BorderLayout.CENTER);
		contentPanel.add(getOptionPanel(), BorderLayout.SOUTH);
	}

	/**
	 * @return the panel to select a path to export the track
	 */
	private JPanel getVCFPanel () {
		// Create the panel
		JPanel panel = new JPanel();

		// Create the layout
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;

		// Create the title label
		JLabel label = new JLabel("Please select a destination file:");

		// Create the text field
		jtfFile = new JTextField();
		jtfFile.setEditable(false);
		Dimension jtfDim = new Dimension(MIN_DIALOG_WIDTH - 25, 21);
		ExportUtils.setComponentSize(jtfFile, jtfDim);

		// Create the button
		JButton button = new JButton();
		Dimension bDim = new Dimension(20, 20);
		ExportUtils.setComponentSize(button, bDim);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileFilter[] filters = {new VCFFilter()};
				File file = ExportUtils.getFile(filters, false);
				if (file != null) {
					jtfFile.setText(file.getPath());
					jcbCompress.setEnabled(true);
				} else {
					jcbCompress.setEnabled(false);
					jcbIndex.setEnabled(false);
				}
			}
		});

		// Add components
		panel.add(label, gbc);

		gbc.gridy++;
		panel.add(jtfFile, gbc);

		gbc.gridx++;
		panel.add(button, gbc);

		return panel;
	}


	/**
	 * @return the panel to select the additional export options
	 */
	private JPanel getOptionPanel () {
		// Create the panel
		JPanel panel = new JPanel();

		// Create the layout
		GridLayout layout = new GridLayout(2, 1);
		panel.setLayout(layout);

		// Create the check box for the compression
		jcbCompress = new JCheckBox("Compress with BGZIP");
		jcbCompress.setEnabled(false);
		jcbCompress.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (jcbCompress.isSelected()) {
					jcbIndex.setEnabled(true);
				} else {
					jcbIndex.setEnabled(false);
				}
			}
		});

		// Create the check box for the indexing
		jcbIndex = new JCheckBox("Index with Tabix");
		jcbIndex.setEnabled(false);

		// Add components
		panel.add(jcbCompress);
		panel.add(jcbIndex);

		return panel;
	}


	/**
	 * @return the path of the selected VCF
	 */
	public String getVCFPath () {
		return jtfFile.getText();
	}


	/**
	 * @return true if the file has to be compressed
	 */
	public boolean compressVCF () {
		if (jcbCompress.isEnabled() && jcbCompress.isSelected()) {
			return true;
		}
		return false;
	}


	/**
	 * @return true if the file has to be indexed
	 */
	public boolean indexVCF () {
		if (jcbIndex.isEnabled() && jcbIndex.isSelected()) {
			return true;
		}
		return false;
	}


	@Override
	protected String getErrors() {
		String error = "";

		String filePath = jtfFile.getText();
		if (filePath == null) {
			error += "The path of the file has not been found.";
		} else if (filePath.isEmpty()){
			error += "The path of the file has not been found.";
		}

		return error;
	}
}
