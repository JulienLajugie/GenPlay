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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeStripe.selectionPanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeStripe.MultiGenomeStripeSelectionDialog;

/**
 * 
 * @author Nicolas Fourel
 */
class HeaderPanel extends JPanel {

	private static final long serialVersionUID = 5218670270091294443L;

	
	protected HeaderPanel () {
		//setBackground(MultiGenomePanel.getHeaderColor());
		
		//Dimension
		Dimension panelDim = new Dimension(MultiGenomeStripeSelectionDialog.getDialogWidth(), MultiGenomeStripeSelectionDialog.getHeaderHeight());
		setSize(panelDim);
		setPreferredSize(panelDim);
		setMinimumSize(panelDim);
		setMaximumSize(panelDim);
		
		JLabel group = new JLabel("Groups");
		JLabel insertion = new JLabel("Insertion");
		JLabel deletion = new JLabel("Deletion");
		JLabel snps = new JLabel("SNPs");
		//JLabel sv = new JLabel("SV");
		
		group.setHorizontalAlignment(JLabel.CENTER);
		insertion.setHorizontalAlignment(JLabel.CENTER);
		deletion.setHorizontalAlignment(JLabel.CENTER);
		snps.setHorizontalAlignment(JLabel.CENTER);
		//sv.setHorizontalAlignment(JLabel.CENTER);
		
		
		Dimension groupDim = new Dimension(MultiGenomeStripeSelectionDialog.getGroupLabelWidth(), MultiGenomeStripeSelectionDialog.getHeaderHeight());
		Dimension typeDim = new Dimension(MultiGenomeStripeSelectionDialog.getCellVcfTypeWidth(), MultiGenomeStripeSelectionDialog.getHeaderHeight());
		
		
		//setLabelSize(fake, selectionDim);
		setLabelSize(group, groupDim);
		setLabelSize(insertion, typeDim);
		setLabelSize(deletion, typeDim);
		setLabelSize(snps, typeDim);
		//setLabelSize(sv, typeDim);
		
		
		//Layout
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		

		//group
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(group, gbc);
		
		//insertion
		gbc.gridx = 1;
		gbc.gridy = 0;
		add(insertion, gbc);
		
		//deletion
		gbc.gridx = 2;
		gbc.gridy = 0;
		add(deletion, gbc);
		
		//snps
		gbc.gridx = 3;
		gbc.gridy = 0;
		add(snps, gbc);
		
		//sv
		/*gbc.gridx = 4;
		gbc.gridy = 0;
		add(sv, gbc);*/
		
	}
	
	
	private void setLabelSize (JLabel label, Dimension dim) {
		label.setSize(dim);
		label.setPreferredSize(dim);
		label.setMinimumSize(dim);
		label.setMaximumSize(dim);
	}
}
