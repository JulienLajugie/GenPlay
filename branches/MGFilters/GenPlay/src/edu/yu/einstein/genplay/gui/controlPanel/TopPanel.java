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
package edu.yu.einstein.genplay.gui.controlPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

/**
 * This panel gathers two elements:
 * - the panel that contains the button for the multi genome properties dialog
 * - the scroll bar
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class TopPanel extends JPanel {

	/** Generated default version ID */
	private static final long serialVersionUID = 2637751583693743095L;

	private final MGPanel				multiGenomePanel;		// the multi genome panel
	private final PositionScrollPanel 	positionScrollPanel;	// the scroll bar
	
	
	/**
	 * Constructor of {@link TopPanel}
	 */
	TopPanel () {
		multiGenomePanel = new MGPanel();
		positionScrollPanel = new PositionScrollPanel();
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		// adds the multi genome panel
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(0, 0, 0, 0);
		add(multiGenomePanel, gbc);
		
		// add the scroll bar
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0.0;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(0, 0, 0, 0);
		add(positionScrollPanel, gbc);
	}


	/**
	 * @return the multiGenomePanel
	 */
	public MGPanel getMultiGenomePanel() {
		return multiGenomePanel;
	}


	/**
	 * @return the positionScrollPanel
	 */
	public PositionScrollPanel getPositionScrollPanel() {
		return positionScrollPanel;
	}
	
}
