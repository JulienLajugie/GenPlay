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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Map;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.stripeManagement.MultiGenomeStripes;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeStripe.MultiGenomeStripeSelectionDialog;


/**
 * 
 * @author Nicolas Fourel
 */
class ContentPanel extends JPanel {

	private static final long serialVersionUID = -4116989246209927348L;

	
	private RowPanel rows[];


	protected ContentPanel () {

		GridLayout layout = new GridLayout(MultiGenomeStripeSelectionDialog.getGenomeNumber(), 1, 0, 0);
		setLayout(layout);

		rows = new RowPanel[MultiGenomeStripeSelectionDialog.getGenomeNumber()];
		for (int i = 0; i < MultiGenomeStripeSelectionDialog.getGenomeNumber(); i++) {
			rows[i] = new RowPanel(MultiGenomeStripeSelectionDialog.getGenomeNames(i));
			add(rows[i]);
		}

		//Dimension
		Dimension panelDim = new Dimension(MultiGenomeStripeSelectionDialog.getDialogWidth(), MultiGenomeStripeSelectionDialog.getSelectionPanelHeight());
		setSize(panelDim);
		setPreferredSize(panelDim);
		setMinimumSize(panelDim);
		setMaximumSize(panelDim);
	}


	/**
	 * Sets colors for every genomes.
	 * @param colorAssociation the genome names and colors association
	 */
	protected void initColors (Map<String, Map<VariantType, Color>> colorAssociation) {
		for (int i = 0; i < MultiGenomeStripeSelectionDialog.getGenomeNumber(); i++) {
			if (colorAssociation.get(MultiGenomeStripeSelectionDialog.getGenomeNames(i)) != null) {
				rows[i].initColors(colorAssociation.get(MultiGenomeStripeSelectionDialog.getGenomeNames(i)));
			}
		}
	}


	/**
	 * Creates a track stripe information object:
	 * - genome name, stripe type and color association
	 * @return a new track stripe information object
	 */
	protected MultiGenomeStripes getMultiGenomeStripe () {
		MultiGenomeStripes stripeInformation = new MultiGenomeStripes();
		for (RowPanel row: rows) {
			stripeInformation.addColorInformation(row.getGenomeName(), row.getStripeColor());
		}
		return stripeInformation;
	}


	/**
	 * @return the rows
	 */
	protected RowPanel[] getRows() {
		return rows;
	}
	

}
