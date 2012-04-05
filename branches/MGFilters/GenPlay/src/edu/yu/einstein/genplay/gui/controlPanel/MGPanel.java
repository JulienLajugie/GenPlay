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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.action.project.multiGenome.PAMultiGenomeProperties;
import edu.yu.einstein.genplay.util.Images;


/**
 * The Multi Genome part of the {@link ControlPanel} 
 * @author Nicolas Fourel
 * @version 0.1
 */
final class MGPanel extends JPanel {

	private static final long 	serialVersionUID = -8481919273684304592L; 	// generated ID
	private static final int HANDLE_WIDTH 			= 50;					// Width of the track handle
	private static final int BUTTON_WIDTH 			= 25;					// Width of the button
	private static final int TRACKS_SCROLL_WIDTH 	= 17;					// Width of the scroll bar
	private JButton jbMultiGenome;											// button for the multi genome properties dialog


	/**
	 * Creates an instance of {@link MGPanel}
	 */
	MGPanel() {

		// Initializes the panel dimension
		Dimension panelDimension = new Dimension(HANDLE_WIDTH, TRACKS_SCROLL_WIDTH);
		setSize(panelDimension);
		setPreferredSize(panelDimension);

		// We add the button only if it is a multi genome project
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			// Initializes the button
			initializesMultiGenomeButton();

			// Add the components
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.gridx = 0;
			gbc.gridy = 0;
			add(jbMultiGenome, gbc);
		}
	}


	/**
	 * Initializes the multi genome button
	 */
	private void initializesMultiGenomeButton () {
		// creates the button
		jbMultiGenome = new JButton(new ImageIcon(Images.getDNAImage()));

		// sets some attributes
		Dimension buttonDimension = new Dimension(BUTTON_WIDTH, TRACKS_SCROLL_WIDTH);
		jbMultiGenome.setSize(buttonDimension);
		jbMultiGenome.setPreferredSize(buttonDimension);
		jbMultiGenome.setMargin(new Insets(0, 0, 0, 0));
		jbMultiGenome.setToolTipText("Show the Multi Genome Properties Dialog");

		// defines the listener
		jbMultiGenome.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PAMultiGenomeProperties action = new PAMultiGenomeProperties();
				action.actionPerformed(null);
			}
		});
	}

}
