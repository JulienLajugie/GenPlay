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
package edu.yu.einstein.genplay.gui.dialog.newCurveLayerDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.gui.dialog.chromosomeChooser.ChromosomeChooserDialog;
import edu.yu.einstein.genplay.util.Utils;



/**
 * Panel of a {@link NewCurveLayerDialog} for the chromosome selection input
 * @author Julien Lajugie
 * @version 0.1
 */
class ChromoSelectionPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -8940594564630580785L; // generated ID
	private final ProjectChromosome cm; 					// chromosome manager
	private final JList 			jlSelectedChromo;		// list showing the selected chromosomes
	private final JScrollPane 		jcpSelectedChromo;		// scroll pane containing the list
	private final JButton 			jbModifySelection;		// button to modify the selection
	private static boolean[]		defaultSelection = null;// default selected chromosome
	private final List<Chromosome> 	fullChromosomeList;		// List of chromosome to display
	private List<Chromosome> 		selectedChromosomes;	// List of chromosome after selection

	/**
	 * Creates an instance of {@link ChromoSelectionPanel}
	 */
	ChromoSelectionPanel() {
		super();
		jlSelectedChromo = new JList(new DefaultListModel());
		fullChromosomeList = new ArrayList<Chromosome>();
		selectedChromosomes = new ArrayList<Chromosome>();
		cm = ProjectManager.getInstance().getProjectChromosome();
		for (int i = 0; i < cm.size(); i++) {
			fullChromosomeList.add(cm.get(i));
			if ((defaultSelection == null) || (defaultSelection[i])) {
				((DefaultListModel) jlSelectedChromo.getModel()).addElement(cm.get(i));
				selectedChromosomes.add(cm.get(i));
			}
		}
		jcpSelectedChromo = new JScrollPane(jlSelectedChromo);
		jcpSelectedChromo.getVerticalScrollBar().setUnitIncrement(Utils.SCROLL_INCREMENT_UNIT);
		jbModifySelection = new JButton("Modify Selection");
		jbModifySelection.addActionListener(this);
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		add(jcpSelectedChromo, gbc);

		gbc = new GridBagConstraints();
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(jbModifySelection, gbc);
		setBorder(BorderFactory.createTitledBorder("Selected Chromosomes"));
	}


	/**
	 * Shows a windows where the user can select the chromosomes
	 * when the "modify selection" button is clicked
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		ChromosomeChooserDialog chromosomeChooser = new ChromosomeChooserDialog();
		chromosomeChooser.setFullChromosomeList(fullChromosomeList);
		chromosomeChooser.setSelectedChromosomeList(selectedChromosomes);
		chromosomeChooser.setOrdering(false);
		if (chromosomeChooser.showDialog(getRootPane()) == ChromosomeChooserDialog.APPROVE_OPTION) {
			selectedChromosomes = chromosomeChooser.getSelectedChromosomeList();
			if (selectedChromosomes.size() == 0) {
				JOptionPane.showMessageDialog(getRootPane(), "You must select at least one chromosome", "Nothing Selected", JOptionPane.WARNING_MESSAGE);
			} else {
				DefaultListModel lm = (DefaultListModel) jlSelectedChromo.getModel();
				lm.removeAllElements();
				for (int i = 0; i < selectedChromosomes.size(); i++) {
					lm.addElement(selectedChromosomes.get(i));
				}
			}
		}
	}


	/**
	 * @return the chromosomes selected
	 */
	boolean[] getSelectedChromosomes() {
		boolean[] selectedChromo = new boolean[cm.size()];
		for (int i = 0; i < selectedChromo.length; i++) {
			selectedChromo[i] = isSelected(cm.get(i));
		}
		return selectedChromo;
	}


	/**
	 * @param chromo a chromosome
	 * @return true if the specified chromosome had been selected. Otherwise return false
	 */
	private boolean isSelected(Chromosome chromo) {
		if (selectedChromosomes.contains(chromo)) {
			return true;
		}
		return false;
	}


	/**
	 * Saves the selected chromsomes as default
	 */
	void saveDefault() {
		defaultSelection = getSelectedChromosomes();
	}
}
