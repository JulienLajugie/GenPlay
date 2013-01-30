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
package edu.yu.einstein.genplay.gui.projectFrame.newProject;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;

/**
 * This class allows users to choose a simple genome project
 * or a multi genome project.
 * @author Nicolas Fourel
 * @version 0.1
 */
class GenomeProjectTypePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -5329054202308798840L;

	private final JRadioButton 	singleRadio;	// Radio button for a simple genomic project
	private final JRadioButton 	multiRadio;		// Radio button for a multi genomic project
	private final ButtonGroup 	genomeRadio;	// Radio group


	/**
	 * Constructor of {@link GenomeProjectTypePanel}
	 */
	protected GenomeProjectTypePanel () {
		//Size
		setSize(ProjectFrame.GENOME_DIM);
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setMaximumSize(getSize());

		//Radio buttons
		singleRadio = new JRadioButton("Single Genome Project");
		singleRadio.setSelected(true);
		multiRadio = new JRadioButton("Multi Genome Project");

		//Color
		setBackground(ProjectFrame.GENOME_COLOR);
		singleRadio.setBackground(ProjectFrame.GENOME_COLOR);
		multiRadio.setBackground(ProjectFrame.GENOME_COLOR);

		//Listener
		singleRadio.addActionListener(this);
		multiRadio.addActionListener(this);

		//Radio group
		genomeRadio = new ButtonGroup();
		genomeRadio.add(singleRadio);
		genomeRadio.add(multiRadio);

		//Layout
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		Insets gbcInsets = new Insets (5, 77, 5, 0);

		//newRadio
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = gbcInsets;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 1;
		add(singleRadio, gbc);

		//loadRadio
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = gbcInsets;
		add(multiRadio, gbc);
	}


	/**
	 * This method displays or hide the loading var files panel.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		ProjectFrame.getInstance().setVarTableVisible(!(arg0.getSource() == singleRadio));
	}


	/**
	 * This method determines if user chose a simple or a multi genome project.
	 * @return true if user chose a simple genome project.
	 */
	protected boolean isSingleProject () {
		if (singleRadio.isSelected()) {
			return true;
		} else {
			return false;
		}
	}

}
