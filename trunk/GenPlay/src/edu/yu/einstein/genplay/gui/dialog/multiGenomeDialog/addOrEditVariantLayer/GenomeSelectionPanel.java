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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.addOrEditVariantLayer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filterDialog.variants.VariantData;

/**
 * Panel to select a genome
 * @author Julien Lajugie
 */
class GenomeSelectionPanel extends JScrollPane {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4060807866730514644L;

	/** Property name of the selected genome */
	static final String SELECTED_GENOME_PROPERTY_NAME = "selectedGenome";

	/** Dimension of the panel inside this scroll pane */
	private static final Dimension PANEL_SIZE = new Dimension(200, 100);

	/** selected genome */
	private VariantData selectedGenome;


	/**
	 * Constructor of {@link GenomeSelectionPanel}
	 * @param list of the available genomes
	 */
	GenomeSelectionPanel(List<VariantData> genomes) {
		createPanel(genomes);
		setBorder(BorderFactory.createTitledBorder("Genome(s)"));
	}


	/**
	 * Creates the radios panel
	 */
	private void createPanel (final List<VariantData> genomes) {
		ButtonGroup group = new ButtonGroup();
		JPanel content = new JPanel(new GridBagLayout());
		content.setPreferredSize(PANEL_SIZE);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;

		for (int i = 0; i < genomes.size(); i++) {
			final int radioIndex = i;
			JRadioButton radioBox = new JRadioButton(genomes.get(i).getGenome());
			if (radioIndex == 0) {
				radioBox.setSelected(true);
			}
			radioBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selectedGenome = genomes.get(radioIndex);
					firePropertyChange(SELECTED_GENOME_PROPERTY_NAME, null, selectedGenome);
				}
			});
			group.add(radioBox);
			if (i == (genomes.size() - 1)) {
				gbc.weighty = 1;
			}
			content.add(radioBox, gbc);
			gbc.gridy++;
		}

		add(content);
		setViewportView(content);
	}


	/**
	 * @return the selected genome
	 */
	VariantData getSelectedGenomes () {
		return selectedGenome;
	}


	@Override
	public void setEnabled(boolean enabled) {
		for (Component c: getComponents()) {
			c.setEnabled(enabled);
		}
		super.setEnabled(enabled);
	}
}
