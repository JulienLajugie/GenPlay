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
import java.util.Map;

import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.stripeManagement.MultiGenomeStripe;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeStripe.MultiGenomePanel;


/**
 * 
 * @author Nicolas Fourel
 */
public class GenomeSelectionPanel extends JPanel{

	private static final long serialVersionUID = 8487761103039040232L;

	
	private HeaderPanel header;
	private ContentPanel content;
	
	
	/**
	 * Constructor of {@link GenomeSelectionPanel}
	 */
	public GenomeSelectionPanel () {
		
		header = new HeaderPanel();
		content = new ContentPanel();
		
		//Dimension
		int height = header.getSize().height + content.getSize().height;
		int width = content.getSize().width;
		Dimension panelDim = new Dimension(width, height);
		setSize(panelDim);
		setPreferredSize(panelDim);
		setMinimumSize(panelDim);
		setMaximumSize(panelDim);
		
		
		//Layout
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		
		
		//nameLabel
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, MultiGenomePanel.getHorizontalInset(), 0, MultiGenomePanel.getHorizontalInset());
		add(header, gbc);
		
		//nameValue
		gbc.gridx = 0;
		gbc.gridy = 1;
		add(content, gbc);
		
	}
	
	/**
	 * Creates a track stripe information object:
	 * - genome name, stripe type and color association
	 * @return a new track stripe information object
	 */
	public MultiGenomeStripe getMultiGenomeStripe () {
		return content.getMultiGenomeStripe();
	}
	
	
	/**
	 * Sets colors for every genomes.
	 * @param colorAssociation the genome names and colors association
	 */
	public void initColors (Map<String, Map<VariantType, Color>> colorAssociation) {
		content.initColors(colorAssociation);
	}
	
	
	/**
	 * @return the rows
	 */
	public RowPanel[] getRows () {
		return content.getRows();
	}
}
