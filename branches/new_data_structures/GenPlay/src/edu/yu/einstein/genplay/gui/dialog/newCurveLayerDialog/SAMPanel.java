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
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
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
import edu.yu.einstein.genplay.util.Images;


/**
 * Panel for the option for SAM / BAM files.
 * @author Julien Lajugie
 */
class SAMPanel extends JPanel {

	/** Generated serial ID */
	private static final long serialVersionUID = 3121403025053985776L;

	private final JRadioButton 			jrbSingleEnd;			//
	private final JLabel 				jlSingleEndHelp;		// label help single end
	private final JRadioButton 			jrbPairedEnd;			//
	private final JLabel 				jlPairedEndHelp;		// label help paired ends
	private final ButtonGroup 			readParityRadioGroup;	//

	private final JFormattedTextField 	jftfMappingQuality;		//
	private final JLabel 				jlMappingQuality;

	private final JLabel				jlReadsToExtract;
	private final JRadioButton 			jrbUnique;				//
	private final JRadioButton 			jrbPrimaryAlignment;	//
	private final JRadioButton 			jrbAllReads;			//
	private final ButtonGroup 			readUniquenessGroup;	//

	private final JLabel				jlReadGroups;
	private final JComboBox 			jcbReadGroups;			//

	private static int defaultMapQual = 0;
	private static boolean isSingleEndSelected = true;
	private static boolean isPairedEndSelected = false;
	private static boolean isUniqueSelected = true;
	private static boolean isPrimaryAligmentSelected = false;
	private static boolean isAllReadsSelected = false;

	/**
	 * Creates an instance of {@link SAMPanel}
	 */
	SAMPanel(SAMExtractor samExtractor) {
		// mapping quality field
		jlMappingQuality = new JLabel("Mapping Quality ≥");
		jftfMappingQuality = new JFormattedTextField(NumberFormat.getIntegerInstance());
		jftfMappingQuality.setColumns(5);
		((NumberFormatter) jftfMappingQuality.getFormatter()).setMinimum(0);
		((NumberFormatter) jftfMappingQuality.getFormatter()).setMaximum(255);
		jftfMappingQuality.setValue(defaultMapQual);

		jlReadsToExtract = new JLabel("Choose reads to extract (BWA only):");
		jrbUnique = new JRadioButton("Unique Reads Only");
		jrbUnique.setSelected(isUniqueSelected);
		jrbPrimaryAlignment = new JRadioButton("Unique Reads + Primary Alignment");
		jrbPrimaryAlignment.setSelected(isPrimaryAligmentSelected);
		jrbAllReads = new JRadioButton("All Reads");
		jrbAllReads.setSelected(isAllReadsSelected);
		readUniquenessGroup = new ButtonGroup();
		readUniquenessGroup.add(jrbAllReads);
		readUniquenessGroup.add(jrbPrimaryAlignment);
		readUniquenessGroup.add(jrbUnique);

		jrbSingleEnd = new JRadioButton("Single-End Reads");
		jrbSingleEnd.setSelected(isSingleEndSelected);
		// add lister that activate / deactivate the different single end options when
		// the single end mode is enabled / disabled
		jrbSingleEnd.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				jlReadsToExtract.setEnabled(jrbSingleEnd.isSelected());
				jrbUnique.setEnabled(jrbSingleEnd.isSelected());
				jlMappingQuality.setEnabled(jrbSingleEnd.isSelected());
				jrbPrimaryAlignment.setEnabled(jrbSingleEnd.isSelected());
				jrbAllReads.setEnabled(jrbSingleEnd.isSelected());
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
		jlPairedEndHelp.setToolTipText("<html>Choose this option to extract fragments with both ends mapped and properly paired.</html>");
		readParityRadioGroup = new ButtonGroup();
		readParityRadioGroup.add(jrbSingleEnd);
		readParityRadioGroup.add(jrbPairedEnd);
		readParityRadioGroup.setSelected(jrbSingleEnd.getModel(), true);

		if (samExtractor.getReadGroups() == null) {
			jcbReadGroups = null;
			jlReadGroups = null;
		} else {
			jcbReadGroups = new JComboBox();
			for (SAMReadGroupRecord currentReadGroupe: samExtractor.getReadGroups()) {
				jcbReadGroups.addItem(currentReadGroupe.getId());
			}
			jlReadGroups = new JLabel("Read-Group to Extract:");
		}

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
		jpSingleEndOptions.add(jlReadsToExtract, gbc);

		gbc.gridy = 2;
		gbc.insets = new Insets(0, 0, 0, 0);
		jpSingleEndOptions.add(jrbUnique, gbc);

		gbc.gridy = 3;
		jpSingleEndOptions.add(jrbPrimaryAlignment, gbc);

		gbc.gridy = 4;
		jpSingleEndOptions.add(jrbAllReads, gbc);

		setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = -1;

		if (jcbReadGroups != null) {
			gbc.insets = new Insets(0, 0, 10, 0);
			gbc.gridy++;
			gbc.gridwidth = 1;
			add(jlReadGroups, gbc);
			gbc.gridx = 1;
			add(jcbReadGroups, gbc);
			gbc.insets = new Insets(0, 0, 0, 0);
		}

		gbc.gridx = 0;
		gbc.gridy++;
		add(jrbSingleEnd, gbc);

		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.LINE_START;
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

		setBorder(BorderFactory.createTitledBorder("SAM/BAM Options"));
	}
}
