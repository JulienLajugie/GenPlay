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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterDialog.idEditors;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.GenotypeIDFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.IDFilterInterface;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class IDGTEditor implements IDEditor {

	private final static String CONSTRAINT_LABEL_TTT 	= "Select a constraint for the value.";
	private final static String HOMOZYGOTE_TTT 			= "The GT field must show a homozygote genotype in order to pass the filter.";
	private final static String HETEROZYGOTE_TTT 		= "The GT field must show a heterozygote genotype in order to pass the filter.";
	private final static String HOMOZYGOTE 				= "homozygote";
	private final static String HETEROZYGOTE 			= "heterozygote";

	private JPanel			panel;
	private VCFHeaderType 	header;				// Header ID
	private JLabel 			constraintLabel;	// Label for naming the constraint
	private JRadioButton	homozygote;			// Radio box for PRESENT value
	private JRadioButton	heterozygote;		// Radio box for ABSENT value
	private JCheckBox		phased;				// Check box to take into account (or not) phased genotypes
	private JCheckBox		unPhased;			// Check box to take into account (or not) phased genotypes


	@Override
	public JPanel updatePanel() {
		panel = new JPanel();

		// Creates the label
		constraintLabel = new JLabel("Must be:");
		constraintLabel.setToolTipText(CONSTRAINT_LABEL_TTT);

		// Creates the radio boxes
		homozygote = new JRadioButton(HOMOZYGOTE);
		homozygote.setToolTipText(HOMOZYGOTE_TTT);
		heterozygote = new JRadioButton(HETEROZYGOTE);
		heterozygote.setToolTipText(HETEROZYGOTE_TTT);

		// Creates the group
		ButtonGroup group = new ButtonGroup();
		group.add(homozygote);
		group.add(heterozygote);

		// Creates the checkboxes
		phased = new JCheckBox("include phased GT");
		phased.setSelected(true);
		unPhased = new JCheckBox("include unphased GT");
		unPhased.setSelected(true);

		// Default setting
		homozygote.setSelected(true);

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Label
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 10, 10, 0);
		panel.add(constraintLabel, gbc);

		// "homozygote" button
		gbc.gridy++;
		gbc.insets = new Insets(5, 20, 0, 0);
		panel.add(homozygote, gbc);

		// "heterozygote" button
		gbc.gridy++;
		panel.add(heterozygote, gbc);

		// "heterozygote" button
		gbc.gridy++;
		panel.add(phased, gbc);

		// "heterozygote" button
		gbc.gridy++;
		gbc.weighty = 1;
		panel.add(unPhased, gbc);

		return panel;
	}


	@Override
	public IDFilterInterface getFilter() {
		GenotypeIDFilter filter = new GenotypeIDFilter();
		filter.setHeaderType(header);
		if (homozygote.isSelected()) {
			filter.setOption(GenotypeIDFilter.HOMOZYGOTE_OPTION);
		} else {
			filter.setOption(GenotypeIDFilter.HETEROZYGOTE_OPTION);
		}

		filter.setCanBePhased(phased.isSelected());
		filter.setCanBeUnPhased(unPhased.isSelected());

		return filter;
	}


	@Override
	public void setHeaderType(VCFHeaderType id) {
		this.header = id;
	}


	@Override
	public VCFHeaderType getHeaderType () {
		return header;
	}


	@Override
	public void initializesPanel(IDFilterInterface filter) {
		GenotypeIDFilter gtFilter = (GenotypeIDFilter) filter;
		if (gtFilter.getOption() == GenotypeIDFilter.HOMOZYGOTE_OPTION) {
			homozygote.setSelected(true);
		} else {
			heterozygote.setSelected(true);
		}

		if (gtFilter.canBePhased()) {
			phased.setSelected(true);
		} else {
			phased.setSelected(false);
		}

		if (gtFilter.canBeUnPhased()) {
			unPhased.setSelected(true);
		} else {
			unPhased.setSelected(false);
		}
	}


	@Override
	public String getErrors() {
		String errors = "";
		if (header == null) {
			errors += "ID selection\n";
		}
		if (!phased.isSelected() && !unPhased.isSelected()) {
			errors += "At least one phasing constraint must be selected\n";
		}
		return errors;
	}


	@Override
	public void setEnabled(boolean b) {
		if (panel != null) {
			panel.setEnabled(b);
			constraintLabel.setEnabled(b);
			homozygote.setEnabled(b);
			heterozygote.setEnabled(b);
			phased.setEnabled(b);
			unPhased.setEnabled(b);
		}
	}


	@Override
	public boolean isEnabled() {
		return panel.isEnabled();
	}

}
