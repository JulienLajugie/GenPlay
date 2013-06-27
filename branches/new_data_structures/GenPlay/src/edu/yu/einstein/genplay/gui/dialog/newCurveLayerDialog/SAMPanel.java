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
package edu.yu.einstein.genplay.gui.dialog.newCurveLayerDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

import net.sf.samtools.SAMReadGroupRecord;
import edu.yu.einstein.genplay.core.IO.extractor.SAMExtractor;
import edu.yu.einstein.genplay.gui.dialog.TextDialog;
import edu.yu.einstein.genplay.util.Images;


/**
 * Panel for the option for SAM / BAM files.
 * @author Julien Lajugie
 */
class SAMPanel extends JPanel {

	/** Generated serial ID */
	private static final long serialVersionUID = 3121403025053985776L;

	private final SAMExtractor 			samExtractor;			// sam extractor with infos about read groups, programs used etc..

	private final JLabel				jlReadGroups;			// label read groups
	private final JComboBox 			jcbReadGroups;			// combo box to choose read groups

	private final JCheckBox				jcbRemoveDuplicates;	// check box remove duplicates
	private final JLabel 				jlRemoveDuplicatesHelp;	// label help remove duplicates

	private final JRadioButton 			jrbSingleEnd;			// radio button single end
	private final JLabel 				jlSingleEndHelp;		// label help single end
	private final JRadioButton 			jrbPairedEnd;			// radio button paired end
	private final JLabel 				jlPairedEndHelp;		// label help paired ends
	private final ButtonGroup 			readParityRadioGroup;	// group with the single end / pair end radio buttons

	private final JFormattedTextField 	jftfMappingQuality;		// input text field for the mapping quality
	private final JLabel 				jlMappingQuality;		// label for the mapping quality

	private final JLabel				jlAlignmentsToExtract;		// label reads to extract
	private final JRadioButton 			jrbUniqueAlignments;				// radio button unique matches
	private final JRadioButton 			jrbPrimaryAlignments;	// radio button primary alignment
	private final JRadioButton 			jrbAllAlignments;			// radio all reads
	private final ButtonGroup 			readUniquenessGroup;	// group with the reads to extract radio buttons

	private final JButton				jbHeader;				// button to show the header of the SAM/BAM file

	private static int 		defaultMapQual = 0;					// default mapping quality
	private static boolean 	isRemoveDuplicatesSelected = true;	// remove duplicates check-box default value
	private static boolean 	isSingleEndSelected = true;			// single-end radio button default state
	private static boolean 	isPairedEndSelected = false;		// paired-end radio button default state
	private static boolean 	isUniqueAlignmentsSelected = false;			// unique radio button default state
	private static boolean 	isPrimaryAligmentSelected = false;	// primary alignment radio button default state
	private static boolean 	isAllAlignmentsSelected = true;			// all reads radio button default state


	/**
	 * Creates an instance of {@link SAMPanel}
	 */
	SAMPanel(SAMExtractor samExtractor) {
		this.samExtractor = samExtractor;

		// mapping quality field
		jlMappingQuality = new JLabel("Mapping Quality (0 - 255) ≥");
		jftfMappingQuality = new JFormattedTextField(NumberFormat.getIntegerInstance());
		jftfMappingQuality.setColumns(5);
		((NumberFormatter) jftfMappingQuality.getFormatter()).setMinimum(0);
		((NumberFormatter) jftfMappingQuality.getFormatter()).setMaximum(255);
		jftfMappingQuality.setValue(defaultMapQual);

		jlAlignmentsToExtract = new JLabel("Choose Alignments to Extract (BWA only):");
		jrbAllAlignments = new JRadioButton("All Alignments");
		jrbAllAlignments.setSelected(isAllAlignmentsSelected);
		jrbPrimaryAlignments = new JRadioButton("Unique + Primary Alignments");
		jrbPrimaryAlignments.setSelected(isPrimaryAligmentSelected);
		jrbUniqueAlignments = new JRadioButton("Unique Alignments Only");
		jrbUniqueAlignments.setSelected(isUniqueAlignmentsSelected);
		readUniquenessGroup = new ButtonGroup();
		readUniquenessGroup.add(jrbUniqueAlignments);
		readUniquenessGroup.add(jrbPrimaryAlignments);
		readUniquenessGroup.add(jrbAllAlignments);

		jrbSingleEnd = new JRadioButton("Single-End Reads");
		jrbSingleEnd.setSelected(isSingleEndSelected);
		// add lister that activate / deactivate the different single end options when
		// the single end mode is enabled / disabled
		jrbSingleEnd.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				jlAlignmentsToExtract.setEnabled(jrbSingleEnd.isSelected());
				jrbUniqueAlignments.setEnabled(jrbSingleEnd.isSelected());
				jlMappingQuality.setEnabled(jrbSingleEnd.isSelected());
				jrbPrimaryAlignments.setEnabled(jrbSingleEnd.isSelected());
				jrbAllAlignments.setEnabled(jrbSingleEnd.isSelected());
				jftfMappingQuality.setEnabled(jrbSingleEnd.isSelected());
			}
		});
		// tooltip
		jlSingleEndHelp = new JLabel(new ImageIcon(Images.getHelpImage()));
		jlSingleEndHelp.setToolTipText("<html>Choose this option to extract each mapped read separatly.</html>");
		jrbPairedEnd = new JRadioButton("Paired-End Reads");
		jrbPairedEnd.setSelected(isPairedEndSelected);
		// tooltip
		jlPairedEndHelp = new JLabel(new ImageIcon(Images.getHelpImage()));
		jlPairedEndHelp.setToolTipText("<html>Choose this option to only extract fragments with both ends mapped and properly paired.</html>");

		readParityRadioGroup = new ButtonGroup();
		readParityRadioGroup.add(jrbSingleEnd);
		readParityRadioGroup.add(jrbPairedEnd);
		readParityRadioGroup.setSelected(jrbSingleEnd.getModel(), true);

		if (samExtractor.getReadGroups() == null) {
			jcbReadGroups = null;
			jlReadGroups = null;
		} else {
			jcbReadGroups = new JComboBox();
			jcbReadGroups.addItem("All Read-Groups");
			for (SAMReadGroupRecord currentReadGroupe: samExtractor.getReadGroups()) {
				jcbReadGroups.addItem(currentReadGroupe.getId());
			}
			jlReadGroups = new JLabel("Read-Group to Extract:");
		}

		jcbRemoveDuplicates = new JCheckBox("Remove Duplicates");
		jcbRemoveDuplicates.setSelected(isRemoveDuplicatesSelected);
		// tooltip
		jlRemoveDuplicatesHelp = new JLabel(new ImageIcon(Images.getHelpImage()));
		jlRemoveDuplicatesHelp.setToolTipText("<html>Choose this option to exclude marked PCR and optical duplicates.</html>");

		jbHeader = new JButton("Show Header");
		jbHeader.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String header = SAMPanel.this.samExtractor.getHeaderString();
				TextDialog.showDialog(SAMPanel.this, "SAM/BAM Header", header);
			}
		});

		JPanel jpSingleEndOptions = new JPanel();
		jpSingleEndOptions.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(10, 0, 10, 0);
		jpSingleEndOptions.add(jlMappingQuality, gbc);

		gbc.gridx = 1;
		jpSingleEndOptions.add(jftfMappingQuality, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(10, 0, 0, 0);
		jpSingleEndOptions.add(jlAlignmentsToExtract, gbc);

		gbc.gridy = 2;
		gbc.insets = new Insets(0, 0, 0, 0);
		jpSingleEndOptions.add(jrbAllAlignments, gbc);

		gbc.gridy = 3;
		jpSingleEndOptions.add(jrbPrimaryAlignments, gbc);

		gbc.gridy = 4;
		jpSingleEndOptions.add(jrbUniqueAlignments, gbc);

		setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridy = -1;

		if (jcbReadGroups != null) {
			gbc.gridy++;
			gbc.gridwidth = 1;
			add(jlReadGroups, gbc);
			gbc.gridx = 1;
			add(jcbReadGroups, gbc);
		}

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = new Insets(10, 0, 0, 0);
		add(jcbRemoveDuplicates, gbc);

		gbc.gridx = 1;
		add(jlRemoveDuplicatesHelp, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		add(jrbSingleEnd, gbc);

		gbc.gridx = 1;
		add(jlSingleEndHelp, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(0, 25, 0, 0);
		add(jpSingleEndOptions, gbc);

		gbc.gridy++;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(10, 0, 0, 0);
		add(jrbPairedEnd, gbc);

		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jlPairedEndHelp, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		add(jbHeader, gbc);

		setBorder(BorderFactory.createTitledBorder("SAM/BAM Options"));
	}


	/**
	 * @return the minimum mapping quality to extract
	 */
	int getMappingQuality() {
		return ((Number) jftfMappingQuality.getValue()).intValue();
	}


	/**
	 * @return the selected read group, null if all read-groups are selected
	 */
	SAMReadGroupRecord getSelectedReadGroup() {
		int selectedIndex = jcbReadGroups.getSelectedIndex();
		if (selectedIndex == 0) {
			return null;
		} else {
			return samExtractor.getReadGroups()[selectedIndex - 1];
		}
	}


	/**
	 * @return true if all the reads should be extracted
	 */
	boolean isAllReadsSelected() {
		return jrbAllAlignments.isSelected();
	}


	/**
	 * @return true if the pair end mode is selected
	 */
	boolean isPairedEndSelected() {
		return jrbPairedEnd.isSelected();
	}


	/**
	 * @return true if the unique reads and the primary alignments should be extracted
	 */
	boolean isPrimaryAligmentSelected() {
		return jrbPrimaryAlignments.isSelected();
	}


	/**
	 * @return true if the remove duplicates options is selected
	 */
	public boolean isRemoveDuplicatesSelected() {
		return jcbRemoveDuplicates.isSelected();
	}


	/**
	 * @return true if the extraction should be done in single end mode
	 */
	boolean isSingleEndSelected() {
		return jrbSingleEnd.isSelected();
	}


	/**
	 * @return true if only the unique reads should be extracted
	 */
	boolean isUniqueSelected() {
		return jrbUniqueAlignments.isSelected();
	}


	/**
	 * Saves the default value for next time the window will become visible
	 */
	void saveDefault() {
		defaultMapQual = getMappingQuality();
		isRemoveDuplicatesSelected = isRemoveDuplicatesSelected();
		isAllAlignmentsSelected = isAllReadsSelected();
		isPairedEndSelected = isPairedEndSelected();
		isPrimaryAligmentSelected = isPrimaryAligmentSelected();
		isSingleEndSelected = isSingleEndSelected();
		isUniqueAlignmentsSelected = isUniqueSelected();
	}
}
