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
package edu.yu.einstein.genplay.gui.projectFrame.newProject;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import edu.yu.einstein.genplay.core.IO.genomeListLoader.AssemblyListLoader;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.genome.Assembly;
import edu.yu.einstein.genplay.dataStructure.genome.Clade;
import edu.yu.einstein.genplay.dataStructure.genome.Genome;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.dialog.chromosomeChooser.ChromosomeChooserDialog;
import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;
import edu.yu.einstein.genplay.util.Images;


/**
 * This class provides a panel including combo boxes to choose an assembly.
 * @author Nicolas Fourel
 */
class AssemblyComponents implements ActionListener {

	private static final String CLADE_DEFAULT_VALUE = "mammal";	// Default clade value
	private static final String GENOME_DEFAULT_VALUE = "human";	// Default genome value
	private static final String ASSEMBLY_DEFAULT_VALUE = new Assembly("hg19", "02 2009").getIndexName(); // Default assembly

	private final JLabel 		jlClade;			// Clade label
	private final JLabel 		jlGenome;			// Genome label
	private final JLabel 		jlAssembly;			// Assembly label
	private final JComboBox 	jcClade;			// Clade combo box
	private final JComboBox 	jcGenome;			// Genome combo box
	private final JComboBox 	jcAssembly;			// Assembly combo box
	private final JButton 		jbChromosome;		// Button to create a chromosome chooser object

	private Map<String, Clade> 	cladeList;			// list of all the assembly available for GenPlay retrived from an XML file
	private Clade 				selectedClade;		// Selected Clade
	private Genome 				selectedGenome;		// Selected Genome
	private Assembly 			selectedAssembly;	// Selected Assembly
	private List<Chromosome> 	fullChromosomeList;	// List of chromosome to display
	private List<Chromosome> 	selectedChromosomes;// List of chromosome after selection


	/**
	 * Constructor of {@link AssemblyComponents}
	 */
	AssemblyComponents () {

		//Labels
		jlClade = new JLabel("Clade:");
		jlGenome = new JLabel("Genome:");
		jlAssembly = new JLabel("Assembly:");

		//Combo boxes
		jcClade = new JComboBox();
		jcGenome = new JComboBox();
		jcAssembly = new JComboBox();

		//Chromosome selection button
		jbChromosome = new JButton();
		jbChromosome.setMargin(new Insets(0, 0, 0, 0));
		jbChromosome.setIcon(new ImageIcon(Images.getToolsImage()));
		jbChromosome.setFocusPainted(false);
		jbChromosome.setBorderPainted(false);
		jbChromosome.setContentAreaFilled(false);
		jbChromosome.setOpaque(false);

		//init boxes and data
		initClade();
		initGenome();
		initAssembly();
		selectedChromosomes = new ArrayList<Chromosome>();

		// default: we select all the chromosomes
		for (Chromosome currentChromo: fullChromosomeList) {
			selectedChromosomes.add(currentChromo);
		}

		//Listeners
		jcClade.addActionListener(this);
		jcGenome.addActionListener(this);
		jcAssembly.addActionListener(this);
		jbChromosome.addActionListener(this);
	}


	/**
	 * This listener updates combo boxes when an action is performed.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == jcClade) {
			if ((selectedClade == null) || !selectedClade.equals(((JComboBox)arg0.getSource()).getSelectedItem())) {
				selectedClade = (Clade) ((JComboBox) arg0.getSource()).getSelectedItem();
				initGenome();
			}
		} else if (arg0.getSource() == jcGenome) {
			if (jcGenome.getSelectedItem() != null) {
				if ((selectedGenome == null) || !selectedGenome.equals(((JComboBox)arg0.getSource()).getSelectedItem())) {
					selectedGenome = (Genome) ((JComboBox)arg0.getSource()).getSelectedItem();
					initAssembly();
				}
			}
		} else if (arg0.getSource() == jcAssembly) {
			if (jcAssembly.getSelectedItem() != null) {
				if ((selectedAssembly == null) || !selectedAssembly.equals(((JComboBox)arg0.getSource()).getSelectedItem())) {
					selectedAssembly = (Assembly) ((JComboBox)arg0.getSource()).getSelectedItem();
					fullChromosomeList = selectedAssembly.getChromosomeList();
					Collections.sort(fullChromosomeList);
					selectedChromosomes = fullChromosomeList;
				}
			}
		} else if (arg0.getSource() == jbChromosome) {
			String title = "Chromosome chooser - " + selectedGenome + " - " + jcAssembly.getSelectedItem().toString();

			ChromosomeChooserDialog chromosomeChooser = new ChromosomeChooserDialog();
			chromosomeChooser.setTitle(title);
			chromosomeChooser.setFullChromosomeList(fullChromosomeList);
			chromosomeChooser.setSelectedChromosomeList(selectedChromosomes);
			chromosomeChooser.setOrdering(false);
			if (chromosomeChooser.showDialog(ProjectFrame.getInstance().getRootPane()) == ChromosomeChooserDialog.APPROVE_OPTION) {
				fullChromosomeList = chromosomeChooser.getFullChromosomeList();
				selectedChromosomes = chromosomeChooser.getSelectedChromosomeList();
			}
		}
	}


	/**
	 * @return the jbChromosome
	 */
	JButton getJbChromosome() {
		return jbChromosome;
	}


	/**
	 * @return the jcAssembly
	 */
	JComboBox getJcAssembly() {
		return jcAssembly;
	}


	/**
	 * @return the jcClade
	 */
	JComboBox getJcClade() {
		return jcClade;
	}


	/**
	 * @return the jcGenome
	 */
	JComboBox getJcGenome() {
		return jcGenome;
	}


	/**
	 * @return the jlAssembly
	 */
	JLabel getJlAssembly() {
		return jlAssembly;
	}


	/**
	 * @return the jlClade
	 */
	JLabel getJlClade() {
		return jlClade;
	}


	/**
	 * @return the jlGenome
	 */
	JLabel getJlGenome() {
		return jlGenome;
	}


	/**
	 * @return the selectedAssembly
	 */
	protected Assembly getSelectedAssembly() {
		return selectedAssembly;
	}


	/**
	 * @return a {@link Map} containing the selected chromosomes.  Each chromosome is associated to its name in the map
	 */
	protected List<Chromosome> getSelectedChromosomes() {
		List<Chromosome> chromosomeList = new ArrayList<Chromosome>();
		for (Chromosome chromosome: selectedChromosomes) {
			// the chromosomes are index by their names in lower case to avoid the case sensitivity problems
			chromosomeList.add(chromosome);
		}
		return chromosomeList;
	}


	/**
	 * @return the selectedClade
	 */
	protected Clade getSelectedClade() {
		return selectedClade;
	}


	/**
	 * @return the selectedGenome
	 */
	protected Genome getSelectedGenome() {
		return selectedGenome;
	}


	/**
	 * Initialization of the assembly combo box
	 */
	private void initAssembly () {
		jcAssembly.removeAllItems();
		List<String> assemblies = new ArrayList<String>(selectedGenome.getAssemblyList().keySet());
		Collections.sort(assemblies);
		for (int i = (assemblies.size()-1); i >= 0; i--) {
			jcAssembly.addItem(selectedGenome.getAssemblyList().get(assemblies.get(i)));
		}
		selectedAssembly = selectedGenome.getAssemblyList().get(ASSEMBLY_DEFAULT_VALUE);
		if (selectedAssembly == null) {
			selectedAssembly = (Assembly) jcAssembly.getItemAt(0);
		}
		jcAssembly.setSelectedItem(selectedAssembly);
		fullChromosomeList = selectedAssembly.getChromosomeList();
		Collections.sort(fullChromosomeList);
		selectedChromosomes = fullChromosomeList;
	}


	/**
	 * Initialization of the clade combo box.
	 */
	private void initClade () {
		//Get assemblies from xml files
		cladeList = new HashMap<String, Clade>();
		try {
			AssemblyListLoader genomeHandler = new AssemblyListLoader();
			cladeList = genomeHandler.getCladeList();
		} catch (Exception e) {
			ExceptionManager.getInstance().caughtException(e);
		}
		List<String> cladeNames = new ArrayList<String>(cladeList.keySet());
		Collections.sort(cladeNames);
		for (String name: cladeNames){
			jcClade.addItem(cladeList.get(name));
		}
		jcClade.setSelectedItem(cladeList.get(CLADE_DEFAULT_VALUE));
		selectedClade = (Clade) jcClade.getSelectedItem();
	}


	/**
	 * Initialization of the genome combo box.
	 */
	private void initGenome () {
		jcGenome.removeAllItems();
		List<String> genomeNames = new ArrayList<String>(selectedClade.getGenomeList().keySet());
		Collections.sort(genomeNames);
		for (String name: genomeNames){
			jcGenome.addItem(selectedClade.getGenomeList().get(name));
		}
		jcGenome.setSelectedItem(selectedClade.getGenomeList().get(GENOME_DEFAULT_VALUE));
		if (jcGenome.getSelectedItem() == null) {
			jcGenome.setSelectedIndex(0);
		}
		selectedGenome = (Genome) jcGenome.getSelectedItem();
	}
}
