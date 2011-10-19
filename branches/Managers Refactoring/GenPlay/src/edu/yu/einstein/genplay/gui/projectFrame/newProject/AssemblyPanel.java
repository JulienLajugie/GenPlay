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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
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
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.genome.Assembly;
import edu.yu.einstein.genplay.core.genome.Clade;
import edu.yu.einstein.genplay.core.genome.Genome;
import edu.yu.einstein.genplay.core.genome.RetrieveAssemblies;
import edu.yu.einstein.genplay.gui.dialog.chromosomeChooser.ChromosomeChooserDialog;
import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;
import edu.yu.einstein.genplay.util.Utils;


/**
 * This class provides a panel including combo boxes to choose an assembly.
 * @author Nicolas Fourel
 * @version 0.1
 */
class AssemblyPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -5768796908632202321L; //generated ID

	private final static 	String 		ICON_PATH = "edu/yu/einstein/genplay/resource/tools.png"; 	// path of the tools icon
	private 	 			ImageIcon	icon; 

	private static final int COMBO_WIDTH = 200;	// Combo box width value
	private static final int COMBO_HEIGTH = 20;	// Combo box height value

	private static final String CLADE_DEFAULT_VALUE = "mammal";	// Default clade value
	private static final String GENOME_DEFAULT_VALUE = "human";	// Default genome value

	private JLabel 				jlClade;			// Clade label
	private JLabel 				jlGenome;			// Genome label
	private JLabel 				jlAssembly;			// Assembly label
	private JComboBox 			jcClade;			// Clade combo box
	private JComboBox 			jcGenome;			// Genome combo box
	private JComboBox 			jcAssembly;			// Assembly combo box
	private JButton 			jbChromosome;		// Button to create a chromosome chooser object

	private Map<String, Clade> 	cladeList;			// list of all the assembly available for GenPlay retrived from an XML file
	private Clade 				selectedClade;		// Selected Clade
	private Genome 				selectedGenome;		// Selected Genome
	private Assembly 			selectedAssembly;	// Selected Assembly

	private List<Chromosome> 	fullChromosomeList;	// List of chromosome to display
	private List<Chromosome> 	selectedChromosomes;// List of chromosome after selection


	/**
	 * Constructor of {@link AssemblyPanel}
	 */
	protected AssemblyPanel () {
		//Size Panel
		setSize(ProjectFrame.ASSEMBLY_DIM);
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setMaximumSize(getSize());

		//Labels
		jlClade = new JLabel("Clade:");
		jlGenome = new JLabel("Genome:");
		jlAssembly = new JLabel("Assembly:");

		//Combo boxes
		jcClade = new JComboBox();
		jcGenome = new JComboBox();
		jcAssembly = new JComboBox();

		//Size Combo boxes
		Dimension comboDim = new Dimension(COMBO_WIDTH, COMBO_HEIGTH);
		jcClade.setPreferredSize(comboDim);
		jcGenome.setPreferredSize(comboDim);
		jcAssembly.setPreferredSize(comboDim);

		//Boxes color
		jcClade.setBackground(ProjectFrame.ASSEMBLY_COLOR);
		jcGenome.setBackground(ProjectFrame.ASSEMBLY_COLOR);
		jcAssembly.setBackground(ProjectFrame.ASSEMBLY_COLOR);

		//Chromosome selection button
		icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource(ICON_PATH)));
		jbChromosome = new JButton(icon);
		jbChromosome.setPreferredSize(new Dimension(20, 20));
		jbChromosome.setMargin(new Insets(0, 0, 0, 0));

		//Layout
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		//Insets
		Insets labelInsets = new Insets (10, 28, 10, 30);
		Insets boxInsets = new Insets (0, 0, 0, 0);
		Insets buttonInsets = new Insets (0, 5, 0, 0);

		//jlClade
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.weighty = 0;
		add(jlClade, gbc);

		//jcClade
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = boxInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jcClade, gbc);

		//jlGenome
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jlGenome, gbc);

		//jcGenome
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = boxInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jcGenome, gbc);

		//jlAssembly
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = labelInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jlAssembly, gbc);

		//jcAssembly
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = boxInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jcAssembly, gbc);

		//jbChromosome
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = buttonInsets;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jbChromosome, gbc);

		//Background
		setBackground(ProjectFrame.ASSEMBLY_COLOR);

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
	 * Initialization of the clade combo box.
	 */
	private void initClade () {
		//Get assemblies from xml files
		cladeList = new HashMap<String, Clade>();
		try {
			RetrieveAssemblies genomeHandler = new RetrieveAssemblies();
			cladeList = genomeHandler.getCladeList();
		} catch (Exception e) {
			e.printStackTrace();
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
		jcAssembly.setSelectedIndex(0);
		selectedAssembly = (Assembly) jcAssembly.getSelectedItem();
		//fullChromosomeList = new ArrayList<Chromosome>(selectedAssembly.getChromosomeList().values());
		fullChromosomeList = Utils.getSortedChromosomeList(selectedAssembly.getChromosomeList());
		selectedChromosomes = fullChromosomeList;
	}


	/**
	 * This listener updates combo boxes when an action is performed.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == jcClade) {
			if (selectedClade == null || !selectedClade.equals(((JComboBox)arg0.getSource()).getSelectedItem())) {
				selectedClade = (Clade) ((JComboBox) arg0.getSource()).getSelectedItem();
				initGenome();
			}
		} else if (arg0.getSource() == jcGenome) {
			if (jcGenome.getSelectedItem() != null) {
				if (selectedGenome == null || !selectedGenome.equals(((JComboBox)arg0.getSource()).getSelectedItem())) {
					selectedGenome = (Genome) ((JComboBox)arg0.getSource()).getSelectedItem();
					initAssembly();
				}
			}
		} else if (arg0.getSource() == jcAssembly) {
			if (jcAssembly.getSelectedItem() != null) {
				if (selectedAssembly == null || !selectedAssembly.equals(((JComboBox)arg0.getSource()).getSelectedItem())) {
					selectedAssembly = (Assembly) ((JComboBox)arg0.getSource()).getSelectedItem();
					fullChromosomeList = Utils.getSortedChromosomeList(selectedAssembly.getChromosomeList());
					selectedChromosomes = fullChromosomeList;
				}
			}
		} else if (arg0.getSource() == jbChromosome) {
			String title = "Chromosome chooser - " + selectedGenome + " - " + jcAssembly.getSelectedItem().toString();

			ChromosomeChooserDialog chromosomeChooser = new ChromosomeChooserDialog();
			chromosomeChooser.setTitle(title);
			chromosomeChooser.setFullChromosomeList(fullChromosomeList);
			chromosomeChooser.setListOfSelectedChromosome(selectedChromosomes);
			chromosomeChooser.setOrdering(false);
			if (chromosomeChooser.showDialog(getRootPane()) == ChromosomeChooserDialog.APPROVE_OPTION) {
				fullChromosomeList = chromosomeChooser.getFullChromosomeList();
				selectedChromosomes = chromosomeChooser.getListOfSelectedChromosome();
			}
		}
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
}
