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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.genotype;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.ExportSettings;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.ExportUtils;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.mainDialog.MultiGenomeTrackActionDialog;
import edu.yu.einstein.genplay.gui.fileFilter.VCFFilter;
import edu.yu.einstein.genplay.gui.fileFilter.VCFGZFilter;
import edu.yu.einstein.genplay.gui.track.layer.Layer;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GenotypeVCFDialog extends MultiGenomeTrackActionDialog {

	/** Generated serial version ID */
	private static final long serialVersionUID = -1321930230220361216L;

	private final static String DIALOG_TITLE = "Correct genotype of a file using current VCF stripes";

	private JTextField 			jtfInputFile;		// Text field for the path of the VCF file to correct
	private JTextField 			jtfOutputFile;		// Text field for the path of the new VCF file
	private GenomeMappingPanel 	genomePanel;
	private JCheckBox 			jcbCompress;		// Check box to compress with BGZIP
	private JCheckBox 			jcbIndex;			// Check box to index with Tabix
	private VCFFile 			vcfToGenotype;


	/**
	 * Constructor of {@link GenotypeVCFDialog}
	 * @param settings the export settings
	 * @param layer the selected {@link Layer}
	 */
	public GenotypeVCFDialog(ExportSettings settings, Layer<?> layer) {
		super(settings, DIALOG_TITLE,  layer);
		vcfToGenotype = null;
	}


	@Override
	protected void initializeContentPanel() {
		// Initialize the content panel
		contentPanel = new JPanel();

		// Create the field set effect
		TitledBorder titledBorder = BorderFactory.createTitledBorder("Update settings");
		contentPanel.setBorder(titledBorder);

		genomePanel = new GenomeMappingPanel();

		// Panel layout
		GridBagLayout layout = new GridBagLayout();
		contentPanel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridy = 0;
		gbc.gridx = 0;

		// Add the input file panel
		contentPanel.add(getInputVCFPanel(), gbc);

		// Add the output file panel
		gbc.gridy++;
		gbc.insets = new Insets(10, 0, 0, 0);
		contentPanel.add(getOutputVCFPanel(), gbc);

		// Add the genome mapping panel
		gbc.gridy++;
		gbc.weighty = 1;
		//gbc.insets = new Insets(10, 0, 0, 0);
		contentPanel.add(genomePanel, gbc);

		// Add the option panel
		/*gbc.gridy++;
		gbc.weighty = 1;
		gbc.insets = new Insets(10, 0, 0, 0);
		contentPanel.add(getCompressionOptionPanel(), gbc);*/
	}


	/**
	 * @return the panel to select the file to correct
	 */
	private JPanel getInputVCFPanel () {
		// Create the panel
		JPanel panel = new JPanel();

		// Create the layout
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(0, 0, 0, 0);
		//gbc.weightx = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;

		// Create the title label
		JLabel label = new JLabel("Please select the file to correct:");

		// Create the text field
		jtfInputFile = new JTextField();
		jtfInputFile.setEditable(false);
		Dimension jtfDim = new Dimension(MIN_DIALOG_WIDTH - 25, 21);
		ExportUtils.setComponentSize(jtfInputFile, jtfDim);

		// Create the button
		JButton button = new JButton();
		Dimension bDim = new Dimension(20, 20);
		ExportUtils.setComponentSize(button, bDim);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileFilter[] filters = {new VCFGZFilter()};
				File file = ExportUtils.getFile(filters, true);
				if (file != null) {
					if ((vcfToGenotype == null) || !file.equals(vcfToGenotype.getFile())) {
						jtfInputFile.setText(file.getPath());
						try {
							vcfToGenotype = new VCFFile(file);
							List<String> names = vcfToGenotype.getHeader().getGenomeRawNames();
							genomePanel.initialize(settings.getGenomeNames(), names);
						} catch (Exception e1) {
							ExceptionManager.getInstance().caughtException(e1);
						}
					}
				}
				pack();
			}
		});

		// Add components
		panel.add(label, gbc);

		gbc.gridy++;
		panel.add(jtfInputFile, gbc);

		gbc.gridx++;
		panel.add(button, gbc);

		return panel;
	}


	/**
	 * @return the panel to select a path to export the track
	 */
	private JPanel getOutputVCFPanel () {
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
		jtfOutputFile = new JTextField();
		jtfOutputFile.setEditable(false);
		Dimension jtfDim = new Dimension(MIN_DIALOG_WIDTH - 25, 21);
		ExportUtils.setComponentSize(jtfOutputFile, jtfDim);

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
					jtfOutputFile.setText(file.getPath());
				}
			}
		});

		// Add components
		panel.add(label, gbc);

		gbc.gridy++;
		panel.add(jtfOutputFile, gbc);

		gbc.gridx++;
		panel.add(button, gbc);

		return panel;
	}


	/**
	 * @return the panel to select the additional export options
	 */
	@SuppressWarnings("unused") // Will be managed in the action very soon!
	private JPanel getCompressionOptionPanel () {
		// Create the panel
		JPanel panel = new JPanel();

		// Create the layout
		GridLayout layout = new GridLayout(3, 1);
		panel.setLayout(layout);

		// Create the check box for the compression
		jcbCompress = new JCheckBox("Compress with BGZIP");
		//jcbCompress.setEnabled(false);
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
		panel.add(new JLabel("Additional options:"));
		panel.add(jcbCompress);
		panel.add(jcbIndex);

		return panel;
	}


	/**
	 * @return the VCF to genotype
	 */
	public VCFFile getVCFToGenotype () {
		return vcfToGenotype;
	}


	/**
	 * @return the path of the output file
	 */
	public String getOutputFile () {
		return jtfOutputFile.getText();
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


	/**
	 * @return the genome names mapping
	 */
	public Map<String, String> getGenomeMap () {
		return genomePanel.getGenomeMap();
	}


	@Override
	protected String getErrors() {
		String error = "";

		if (vcfToGenotype == null) {
			error += "The VCF file to apply the genotype has not been found\n.";
		}


		String filePath = jtfOutputFile.getText();
		if (filePath == null) {
			error += "The path of the file has not been found.";
		} else if (filePath.isEmpty()){
			error += "The path of the file has not been found.";
		}

		return error;
	}
}
