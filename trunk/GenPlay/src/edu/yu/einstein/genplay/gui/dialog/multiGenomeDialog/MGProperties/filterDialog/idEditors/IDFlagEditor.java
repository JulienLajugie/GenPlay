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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.FlagIDFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.IDFilterInterface;

/**
 * @author Nicolas Fourel
 */
public class IDFlagEditor implements IDEditor {

	private final static String CONSTRAINT_LABEL_TTT 	= "Select a constraint for the value.";
	private final static String PRESENT_TTT 			= "The flag in the associated file data must be PRESENT in order to be accepted.";
	private final static String ABSENT_TTT 				= "The flag in the associated file data must be ABSENT in order to be accepted.";
	private final static String LABEL 					= "The selected flag field";
	private final static String PRESENT 				= "Must be present";
	private final static String ABSENT 					= "Must not be present";
	private JPanel			panel;
	private VCFHeaderType 	header;				// Header ID
	private JLabel 			constraintLabel;	// Label for naming the constraint
	private JRadioButton	present;			// Radio box for PRESENT value
	private JRadioButton	absent;				// Radio box for ABSENT value


	@Override
	public IDFilterInterface getFilter() {
		FlagIDFilter filter = new FlagIDFilter();
		filter.setHeaderType(header);
		if (present.isSelected()) {
			filter.setRequired(true);
		} else {
			filter.setRequired(false);
		}
		return filter;
	}


	@Override
	public VCFHeaderType getHeaderType () {
		return header;
	}


	@Override
	public void initializesPanel(IDFilterInterface filter) {
		FlagIDFilter flagFilter = (FlagIDFilter) filter;
		if (flagFilter.isRequired()) {
			present.setSelected(true);
		} else {
			absent.setSelected(true);
		}
	}


	@Override
	public boolean isSelectionValid() {
		return true;
	}


	@Override
	public boolean isVisible() {
		return panel.isVisible();
	}


	@Override
	public void setHeaderType(VCFHeaderType id) {
		header = id;
	}


	@Override
	public void setVisible(boolean b) {
		if (panel != null) {
			panel.setVisible(b);
		}
	}


	@Override
	public JPanel updatePanel() {
		panel = new JPanel();

		// Creates the label
		constraintLabel = new JLabel(LABEL);
		constraintLabel.setToolTipText(CONSTRAINT_LABEL_TTT);

		// Creates the radio boxes
		present = new JRadioButton(PRESENT);
		present.setToolTipText(PRESENT_TTT);
		absent = new JRadioButton(ABSENT);
		absent.setToolTipText(ABSENT_TTT);

		// Creates the group
		ButtonGroup group = new ButtonGroup();
		group.add(present);
		group.add(absent);

		// Default setting
		present.setSelected(true);

		// Layout settings
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;

		// Label
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 20, 0);
		panel.add(constraintLabel, gbc);

		// "Present" button
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 10, 10, 0);
		panel.add(present, gbc);

		// "Absent" button
		gbc.gridy = 2;
		panel.add(absent, gbc);

		return panel;
	}
}
