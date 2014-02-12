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
package edu.yu.einstein.genplay.gui.projectFrame.newProject;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * This class shows information about the loaded VCF:
 * 	- group number
 *  - genome number
 *  - VCF file number
 * @author Nicolas Fourel
 * @author Julien Lajugie
 */
public class MultiGenomeInformationPanel extends JPanel {

	private static final long serialVersionUID = 6394382682521718513L;

	protected static int GROUP_NUMBER = 0;
	protected static int GENOME_NUMBER = 0;
	protected static int FILE_NUMBER = 0;

	private static 	JLabel groupValue;	// group value label
	private static 	JLabel genomeValue;	// genome value label
	private static	JLabel vcfValue;	// VCF file value label

	/**
	 * Updates the information.
	 */
	public static void refreshInformation () {
		groupValue.setText("" + GROUP_NUMBER);
		genomeValue.setText("" + GENOME_NUMBER);
		vcfValue.setText("" + FILE_NUMBER);
	}

	private final 		JLabel groupLabel;	// group name label
	private final 		JLabel genomeLabel;	// genome name label
	private final 		JLabel vcfLabel;	// VCF name label


	/**
	 * Constructor of {@link MultiGenomeInformationPanel}
	 */
	protected  MultiGenomeInformationPanel () {
		// Label
		groupLabel = new JLabel("Group:");
		genomeLabel = new JLabel("Genome:");
		vcfLabel = new JLabel("VCF:");

		// Value
		groupValue = new JLabel("0");
		genomeValue = new JLabel("0");
		vcfValue = new JLabel("0");

		GridBagConstraints gbc = new GridBagConstraints();
		Insets labelInsets = new Insets(0, 10, 3, 0);
		setLayout(new GridBagLayout());

		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = labelInsets;
		add(groupLabel, gbc);

		gbc.gridx = 1;
		add(groupValue, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		add(genomeLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		add(genomeValue, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		add(vcfLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		add(vcfValue, gbc);

		setOpaque(false);
	}
}
