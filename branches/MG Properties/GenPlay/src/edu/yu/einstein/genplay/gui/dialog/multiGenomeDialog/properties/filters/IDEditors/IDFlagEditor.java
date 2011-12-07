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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filters.IDEditors;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.FlagIDFilter;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.IDFilter;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class IDFlagEditor implements IDEditor {

	private VCFHeaderType 	id;			// Header ID
	private String			category;	// category of the filter
	private JRadioButton present;
	private JRadioButton absent;

	@Override
	public void updatePanel(JPanel panel) {
		// Back up the size of the panel
		Dimension previousDimension = panel.getPreferredSize();

		// Remove everything from the panel
		panel.removeAll();

		// Creates the label
		JLabel label = new JLabel("Must be:");

		// Creates the "present" radio box
		present = new JRadioButton("present");

		// Creates the "absent" radio box
		absent = new JRadioButton("absent");

		// Creates the group
		ButtonGroup group = new ButtonGroup();
		group.add(present);
		group.add(absent);

		// Default setting
		present.setSelected(true);

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 0;
		gbc.weighty = 0;

		// Label
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(3, 0, 0, 0);
		panel.add(label, gbc);

		// "Present" button
		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.insets = new Insets(0, 5, 0, 0);
		panel.add(present, gbc);

		// "Absent" button
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weighty = 1;
		panel.add(absent, gbc);

		// Create a new dimension based on the previous one
		Dimension dimension = new Dimension((int)previousDimension.getWidth(), 50);

		// Restore size to former value
		panel.setPreferredSize(dimension);
		panel.setMinimumSize(dimension);
	}

	@Override
	public IDFilter getFilter() {
		FlagIDFilter filter = new FlagIDFilter();
		filter.setID(id);
		filter.setCategory(category);
		if (present.isSelected()) {
			filter.setRequired(true);
		} else {
			filter.setRequired(false);
		}
		return filter;
	}

	@Override
	public void setID(VCFHeaderType id) {
		this.id = id;
	}

	@Override
	public void initializesPanel(IDFilter filter) {
		FlagIDFilter flagFilter = (FlagIDFilter) filter;
		if (flagFilter.isRequired()) {
			present.setSelected(true);
		} else {
			absent.setSelected(true);
		}
	}

	
	@Override
	public void setCategory(String category) {
		this.category = category;
	}

}
