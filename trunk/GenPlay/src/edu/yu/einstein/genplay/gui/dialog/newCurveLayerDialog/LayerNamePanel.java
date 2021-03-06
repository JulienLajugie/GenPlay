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
package edu.yu.einstein.genplay.gui.dialog.newCurveLayerDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * Panel for the layer name input of a {@link NewCurveLayerDialog}
 * @author Julien Lajugie
 */
class LayerNamePanel extends JPanel {

	private static final long serialVersionUID = -5969101278574088008L;	// generated ID
	private final JTextField jtfLayerName;	// text field for the layer name


	/**
	 * Creates an instance of a {@link LayerNamePanel}
	 * @param layerName default name of a layer
	 */
	LayerNamePanel(String layerName) {
		super();
		jtfLayerName = new JTextField(layerName);
		jtfLayerName.setColumns(20);
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 10, 10, 10);
		add(jtfLayerName, gbc);
		setBorder(BorderFactory.createTitledBorder("Layer Name"));
	}


	/**
	 * @return the name inside the input box
	 */
	String getLayerName() {
		return jtfLayerName.getText();
	}
}
