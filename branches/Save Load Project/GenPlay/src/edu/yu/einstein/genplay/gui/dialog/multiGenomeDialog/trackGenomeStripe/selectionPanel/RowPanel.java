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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeStripe.selectionPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.multiGenomeManager.MultiGenomeManager;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeStripe.MultiGenomePanel;


/**
 * 
 * @author Nicolas Fourel
 */
public class RowPanel extends JPanel {

	private static final long serialVersionUID = 7302221734315638809L;
	
	private JLabel genomeGroup;
	private CellSelectionPanel insertionBox;
	private CellSelectionPanel deletionBox;
	private CellSelectionPanel snpsBox;
	private CellSelectionPanel svBox;
	
	
	protected RowPanel (String genomeGroupName) {
		
		//Dimension
		Dimension panelDim = new Dimension(MultiGenomePanel.getDialogWidth(), MultiGenomePanel.getRowHeight());
		setSize(panelDim);
		setPreferredSize(panelDim);
		setMinimumSize(panelDim);
		setMaximumSize(panelDim);
		
		//Components
		genomeGroup = new JLabel(genomeGroupName);
		insertionBox = new CellSelectionPanel(MultiGenomeManager.getInsertionDefaultColor());
		deletionBox = new CellSelectionPanel(MultiGenomeManager.getDeletionDefaultColor());
		snpsBox = new CellSelectionPanel(MultiGenomeManager.getSnpsDefaultColor());
		svBox = new CellSelectionPanel(MultiGenomeManager.getSvDefaultColor());
		
		//snpsBox.betaLimit();
		svBox.betaLimit();
		
		Dimension genomeDim = new Dimension(MultiGenomePanel.getGroupLabelWidth(), MultiGenomePanel.getRowHeight());
		genomeGroup.setSize(genomeDim);
		genomeGroup.setPreferredSize(genomeDim);
		genomeGroup.setMinimumSize(genomeDim);
		genomeGroup.setMaximumSize(genomeDim);
		
		//Layout
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		
		
		//genomeGroup
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 0, 0);
		add(genomeGroup, gbc);
		
		//insertionBox
		gbc.gridx = 1;
		gbc.gridy = 0;
		add(insertionBox, gbc);
		
		//deletionBox
		gbc.gridx = 2;
		gbc.gridy = 0;
		add(deletionBox, gbc);
		
		//snpsBox
		gbc.gridx = 3;
		gbc.gridy = 0;
		add(snpsBox, gbc);
		
		//svBox
		gbc.gridx = 4;
		gbc.gridy = 0;
		add(svBox, gbc);
		
	}
	
	
	/**
	 * @return the formatted name of the genome
	 */
	public String getGenomeName () {
		return genomeGroup.getText();
	}
	
	
	/**
	 * Initializes colors and checkboxes of the row.
	 * @param colorAssociation	the color association map
	 */
	protected void initColors (Map<VariantType, Color> colorAssociation) {
		if (colorAssociation != null) {
			for (VariantType type: colorAssociation.keySet()) {
				if (type.equals(VariantType.INSERTION)) {
					insertionBox.initCell(colorAssociation.get(type));
				} else if (type.equals(VariantType.DELETION)) {
					deletionBox.initCell(colorAssociation.get(type));
				} else if (type.equals(VariantType.SNPS)) {
					snpsBox.initCell(colorAssociation.get(type));
				} else if (type.equals(VariantType.SV)) {
					svBox.initCell(colorAssociation.get(type));
				}
			}
		}
	}
	
	
	/**
	 * @return the color association map
	 */
	public Map<VariantType, Color> getStripeColor () {
		Map<VariantType, Color> stripe = new HashMap<VariantType, Color>();
		if (insertionBox.isSelected()) {
			stripe.put(VariantType.INSERTION, insertionBox.getColor());
		}
		if (deletionBox.isSelected()) {
			stripe.put(VariantType.DELETION, deletionBox.getColor());
		}
		if (snpsBox.isSelected()) {
			stripe.put(VariantType.SNPS, snpsBox.getColor());
		}
		if (svBox.isSelected()) {
			stripe.put(VariantType.SV, svBox.getColor());
		}
		return stripe;
	}
	
}