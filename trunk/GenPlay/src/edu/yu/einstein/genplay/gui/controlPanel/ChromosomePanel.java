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
package edu.yu.einstein.genplay.gui.controlPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.SimpleGenomeWindow;
import edu.yu.einstein.genplay.gui.action.project.PAMoveFarLeft;
import edu.yu.einstein.genplay.gui.action.project.PAMoveFarRight;
import edu.yu.einstein.genplay.gui.action.project.PAMoveLeft;
import edu.yu.einstein.genplay.gui.action.project.PAMoveRight;
import edu.yu.einstein.genplay.gui.action.project.PAZoomIn;
import edu.yu.einstein.genplay.gui.action.project.PAZoomOut;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEvent;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowListener;



/**
 * The ChromosomePanel part of the {@link ControlPanel}
 * @author Julien Lajugie
 * @version 0.1
 */
final class ChromosomePanel extends JPanel implements MouseWheelListener, ItemListener, GenomeWindowListener {

	private static final long serialVersionUID = -7749788921295566004L;	// generated ID
	private final JLabel 							jlChromosome;		// label chromosome
	private final JComboBox 						jcbChromosome;		// combo box chromosome
	private final ProjectChromosomes 				projectChromosomes; 	// Instance of the Chromosome Manager
	private final ProjectWindow						projectWindow;		// Instance of the Genome Window Manager


	/**
	 * Creates an instance of {@link ChromosomePanel}
	 * @param genomeWindow a {@link SimpleGenomeWindow}
	 */
	ChromosomePanel() {
		projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		projectWindow = ProjectManager.getInstance().getProjectWindow();
		jlChromosome = new JLabel(" Chromosome ");
		// Create ComboBox for the chromosome selection
		jcbChromosome = new JComboBox(projectChromosomes.toArray());
		// select the first item case currentChromosome is not in the list
		jcbChromosome.setSelectedIndex(0);
		jcbChromosome.setSelectedItem(projectWindow.getGenomeWindow().getChromosome());
		jcbChromosome.addItemListener(this);

		// Add the components
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(jlChromosome, gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(jcbChromosome, gbc);

		addMouseWheelListener(this);

		// Deactivate chromosome box key listener (bother the main frame key event management)
		jcbChromosome.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAMoveLeft.ACCELERATOR, "none");
		jcbChromosome.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAMoveFarLeft.ACCELERATOR, "none");
		jcbChromosome.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAMoveRight.ACCELERATOR, "none");
		jcbChromosome.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAMoveFarRight.ACCELERATOR, "none");
		jcbChromosome.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAZoomIn.ACCELERATOR, "none");
		jcbChromosome.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAZoomOut.ACCELERATOR, "none");
	}


	@Override
	public void mouseWheelMoved(MouseWheelEvent mwe) {
		if ((mwe.getWheelRotation() + jcbChromosome.getSelectedIndex()) < 0) {
			jcbChromosome.setSelectedIndex(0);
		} else if ((mwe.getWheelRotation() + jcbChromosome.getSelectedIndex()) > (jcbChromosome.getItemCount() - 1)) {
			jcbChromosome.setSelectedIndex(jcbChromosome.getItemCount() - 1);
		} else {
			jcbChromosome.setSelectedIndex(mwe.getWheelRotation() + jcbChromosome.getSelectedIndex());
		}
	}


	/**
	 * This method updates the chromosome panel when a project is loaded.
	 */
	public void updateChromosomePanel () {
		jcbChromosome.removeAllItems();
		for (Chromosome currentChromosome: projectChromosomes) {
			jcbChromosome.addItem(currentChromosome);
		}
	}


	@Override
	public void itemStateChanged(ItemEvent arg0) {
		Chromosome newChromosome = (Chromosome)jcbChromosome.getSelectedItem();
		if ((newChromosome != null) && !newChromosome.equals(projectWindow.getGenomeWindow().getChromosome())) {
			SimpleGenomeWindow newGenomeWindow = new SimpleGenomeWindow(newChromosome, 0, newChromosome.getLength());
			projectWindow.setGenomeWindow(newGenomeWindow);
		}
	}


	/**
	 * Locks the chromosome panel
	 */
	public void lock() {
		jcbChromosome.setEnabled(false);
	}


	/**
	 * Unlocks the chromosome panel
	 */
	public void unlock() {
		jcbChromosome.setEnabled(true);
	}


	@Override
	public void genomeWindowChanged(GenomeWindowEvent evt) {
		jcbChromosome.setSelectedItem(evt.getNewWindow().getChromosome());
	}

}
