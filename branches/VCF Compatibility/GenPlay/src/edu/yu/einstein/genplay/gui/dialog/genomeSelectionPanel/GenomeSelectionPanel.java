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
package edu.yu.einstein.genplay.gui.dialog.genomeSelectionPanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;


/**
 * Panel for the selection of a genome as a reference in a multi-genome project
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GenomeSelectionPanel extends JPanel {

	private static final long 	serialVersionUID = -2863825210102188370L;	// generated ID
	private static final int 	PANEL_WIDTH = 180;	// width of the panel
	private 					JComboBox 	jcbGenome; 									// combo box to choose the genome
	private						JComboBox	jcbAllele;									// combo box to choose the allele type
	private static int 						defaultGenome = 0;	// default method of calculation
	
	
	/**
	 * Creates an instance of a {@link GenomeSelectionPanel}
	 */
	public GenomeSelectionPanel() {
		super();
		
		//Layout
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;
	
		// Insert the genome label
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(7, 10, 0, 10);
		add(new JLabel("Select a genome:"), gbc);
		
		// Insert the genome combo box
		gbc.gridy = 1;
		gbc.insets = new Insets(2, 10, 0, 10);
		add(getGenomeComboBox(), gbc);
		
		// Insert the allele type label
		gbc.gridy = 2;
		gbc.insets = new Insets(7, 10, 0, 10);
		add(new JLabel("Select an allele:"), gbc);
		
		// Insert the allele type combo box
		gbc.gridy = 3;
		gbc.insets = new Insets(2, 10, 0, 10);
		add(getAlleleTypeComboBox(), gbc);
		
	}
	
	
	private JComboBox getGenomeComboBox () {
		// Creates the combo box
		jcbGenome = new JComboBox(ProjectManager.getInstance().getMultiGenome().getFormattedGenomeArray());
		jcbGenome.setSelectedIndex(defaultGenome);
		
		//Dimension
		int height = jcbGenome.getFontMetrics(jcbGenome.getFont()).getHeight() + 5;
		Dimension dimension = new Dimension(PANEL_WIDTH, height);
		jcbGenome.setPreferredSize(dimension);
		jcbGenome.setMinimumSize(dimension);
		
		// Tool tip text
		jcbGenome.setToolTipText("Select a genome");
		
		return jcbGenome;
	}
	
	
	private JComboBox getAlleleTypeComboBox () {
		// Creates the combo box
		Object[] alleles = new Object[]{AlleleType.PATERNAL, AlleleType.MATERNAL};
		jcbAllele = new JComboBox(alleles);
		jcbAllele.setSelectedIndex(defaultGenome);
		
		//Dimension
		int height = jcbAllele.getFontMetrics(jcbAllele.getFont()).getHeight() + 5;
		Dimension dimension = new Dimension(PANEL_WIDTH, height);
		jcbAllele.setPreferredSize(dimension);
		jcbAllele.setMinimumSize(dimension);
		
		// Tool tip text
		jcbAllele.setToolTipText("Select an allele to synchronize with");
		
		return jcbAllele;
	}
	
	
	/**
	 * @return the selected score calculation method
	 */
	public int getGenomeIndex() {
		return jcbGenome.getSelectedIndex();
	}
	

	/**
	 * @return the full name of the selected genome
	 */
	public String getGenomeName () {
		return (String)jcbGenome.getSelectedItem();
	}
	
	
	/**
	 * @return the selected allele type
	 */
	public AlleleType getAlleleType () {
		return (AlleleType)jcbAllele.getSelectedItem();
	}
	
	
	/**
	 * Saves the selected method of calculation as default
	 */
	public void saveDefault() {
		defaultGenome = getGenomeIndex();
	}
}
