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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.ProjectScreen;


/**
 * This class shows information about the loaded VCF:
 * 	- group number
 *  - genome number
 *  - VCF file number 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MultiGenomeInformationPanel extends JPanel {

	private static final long serialVersionUID = 6394382682521718513L;
	
	private static final Dimension LABEL_DIM = new Dimension(100, 25);	// Label name dimension
	private static final Dimension VALUE_DIM = new Dimension(20, 20);	// label value dimension

	private static 	JLabel groupValue;	// group value label
	private static 	JLabel genomeValue;	// genome value label
	private static	JLabel vcfValue;	// VCF file value label
	private 		JLabel groupLabel;	// group name label
	private 		JLabel genomeLabel;	// genome name label
	private 		JLabel vcfLabel;	// VCF name label
	
	
	
	/**
	 * Constructor of {@link MultiGenomeInformationPanel}
	 */
	protected  MultiGenomeInformationPanel () {
		
		Dimension paneDim = new Dimension(ProjectScreen.getVCFDim().width, 70);
		setSize(paneDim);
		setMinimumSize(paneDim);
		setMaximumSize(paneDim);
		setPreferredSize(paneDim);
		
		setBackground(ProjectScreen.getVCFColor());
		
		// Label
		groupLabel = new JLabel("Group :");
		genomeLabel = new JLabel("Genome :");
		vcfLabel = new JLabel("VCF :");
		setLabelSize(groupLabel, LABEL_DIM);
		setLabelSize(genomeLabel, LABEL_DIM);
		setLabelSize(vcfLabel, LABEL_DIM);
		
		// Value
		groupValue = new JLabel("0");
		genomeValue = new JLabel("0");
		vcfValue = new JLabel("0");
		setLabelSize(groupValue, VALUE_DIM);
		setLabelSize(genomeValue, VALUE_DIM);
		setLabelSize(vcfValue, VALUE_DIM);
		
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		Insets labelInsets = new Insets(0, 0, 3, 0);
		Insets valueInsets = new Insets(0, 0, 3, 0);
		setLayout(gbl);
		
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = labelInsets;
		add(groupLabel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = valueInsets;
		add(groupValue, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = labelInsets;
		add(genomeLabel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = valueInsets;
		add(genomeValue, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = labelInsets;
		add(vcfLabel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.insets = valueInsets;
		add(vcfValue, gbc);
		
	}
	
	
	/**
	 * Sets the size of a label
	 * @param label	the label
	 * @param dim	the dimension object
	 */
	private void setLabelSize (JLabel label, Dimension dim) {
		label.setSize(dim);
		label.setMinimumSize(dim);
		label.setMaximumSize(dim);
		label.setPreferredSize(dim);
	}
	

	/**
	 * @param group  	group number
	 * @param genome	genome number
	 * @param vcf		VCF number
	 */
	public static void setInformation (int group, int genome, int vcf) {
		groupValue.setText("" + group);
		genomeValue.setText("" + genome);
		vcfValue.setText("" + vcf);
	}
	
}