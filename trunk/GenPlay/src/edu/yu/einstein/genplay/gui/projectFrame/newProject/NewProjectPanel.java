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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.genome.Assembly;
import edu.yu.einstein.genplay.dataStructure.genome.Clade;
import edu.yu.einstein.genplay.dataStructure.genome.Genome;


/**
 * This class manages all information regarding new project information.
 * It displays and organizes the communication of every panels.
 * @author Nicolas Fourel
 * @author Julien Lajugie
 */
public class NewProjectPanel extends JPanel {

	private static final long serialVersionUID = 2223959265643927573L;

	protected static final String DEFAULT_PROJECT_NAME = "New Project";

	private ProjectNameComponents 		nameComponents;				// Name components
	private ScorePrecisionComponents	scorePrecisionComponents;	// Score precision components
	private AssemblyComponents 			assemblyComponents;			// Assembly components
	private GenomeProjectTypePanel 		genomePanel;				// Genome panel
	private MultiGenomePanel			multiGenomePanel;			// Multi genome panel


	/**
	 * Constructor of {@link NewProjectPanel}
	 */
	public NewProjectPanel () {
		super();
		init();
	}


	/**
	 * @return the selected assembly
	 */
	public Assembly getAssembly() {
		return assemblyComponents.getSelectedAssembly();
	}


	/**
	 * @return the selected clade
	 */
	public Clade getClade() {
		return assemblyComponents.getSelectedClade();
	}


	/**
	 * @return the selected genome
	 */
	public Genome getGenome() {
		return assemblyComponents.getSelectedGenome();
	}


	/**
	 * @return the mapping between genome full names and their readers.
	 */
	public Map<String, List<VCFFile>> getGenomeFileAssociation ()  {
		return multiGenomePanel.getGenomeFileAssociation();
	}


	/**
	 * @return the project name or null if it is not valid
	 */
	public String getProjectName () {
		return nameComponents.getProjectName();
	}


	/**
	 * @return the selected score precision
	 */
	public ScorePrecision getProjectScorePrecision() {
		return scorePrecisionComponents.getProjectScorePrecision();
	}


	/**
	 * @return a {@link Map} containing the selected chromosomes.  Each chromosome is associated to its name in the map
	 */
	public List<Chromosome> getSelectedChromosomes() {
		return assemblyComponents.getSelectedChromosomes();
	}


	/**
	 * Main method of the class.
	 * It initializes the {@link NewProjectPanel} panel.
	 */
	private void init() {
		//Layout
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		Insets minorInsets = new Insets(0, 5, 5, 5);
		Insets majorInsets = new Insets(5, 5, 25, 5);

		//Panels
		nameComponents = new ProjectNameComponents();
		scorePrecisionComponents = new ScorePrecisionComponents();
		assemblyComponents = new AssemblyComponents();
		genomePanel = new GenomeProjectTypePanel();
		multiGenomePanel = new MultiGenomePanel();

		// panel with the labels name, precision, clade ...
		JPanel jpBasicInfo = new JPanel();
		jpBasicInfo.setOpaque(false);
		jpBasicInfo.setLayout(new GridBagLayout());

		// project name
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 0;
		gbc.insets = majorInsets;
		jpBasicInfo.add(nameComponents.getJlName(), gbc);

		gbc.gridx = 1;
		gbc.gridwidth = 2;
		jpBasicInfo.add(nameComponents.getJtName(), gbc);

		// project precision
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 1;
		jpBasicInfo.add(scorePrecisionComponents.getJlScorePrecision(), gbc);

		gbc.gridx = 1;
		jpBasicInfo.add(scorePrecisionComponents.getJcbScorePrecision(), gbc);

		gbc.gridx = 2;
		jpBasicInfo.add(scorePrecisionComponents.getJlHelp(), gbc);

		// project assembly
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = minorInsets;
		jpBasicInfo.add(assemblyComponents.getJlClade(), gbc);

		gbc.gridx = 1;
		jpBasicInfo.add(assemblyComponents.getJcClade(), gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		jpBasicInfo.add(assemblyComponents.getJlGenome(), gbc);

		gbc.gridx = 1;
		jpBasicInfo.add(assemblyComponents.getJcGenome(), gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		jpBasicInfo.add(assemblyComponents.getJlAssembly(), gbc);

		gbc.gridx = 1;
		jpBasicInfo.add(assemblyComponents.getJcAssembly(), gbc);

		gbc.gridx = 2;
		jpBasicInfo.add(assemblyComponents.getJbChromosome(), gbc);

		gbc = new GridBagConstraints();

		gbc.insets = majorInsets;
		add(jpBasicInfo, gbc);

		// Genome panel
		gbc.gridy = 1;
		add(genomePanel, gbc);

		// multi-genome panel
		gbc.gridy = 2;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		add(multiGenomePanel, gbc);

		setOpaque(false);
	}


	/**
	 * This method determines if user chose a simple or a multi genome project.
	 * @return true if user chose a simple genome project.
	 */
	public boolean isSingleProject () {
		return genomePanel.isSingleProject();
	}


	/**
	 * @return true if the multi genome project is valid
	 */
	public boolean isValidMultigenomeProject () {
		return multiGenomePanel.isValidMultigenomeProject();
	}


	/**
	 * Displays or hides the var panel
	 * @param visible set to true to show the var table
	 */
	public void setVarTableVisible(boolean visible) {
		multiGenomePanel.setVarTableVisible(visible);
	}
}
