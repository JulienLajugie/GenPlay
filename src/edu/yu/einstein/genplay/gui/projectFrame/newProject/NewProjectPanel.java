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
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.genome.Assembly;
import edu.yu.einstein.genplay.core.genome.Clade;
import edu.yu.einstein.genplay.core.genome.Genome;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;


/**
 * This class manages all information regarding new project information.
 * It displays and organizes the communication of every panels. 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class NewProjectPanel extends JPanel {
	
	private static final long serialVersionUID = 2223959265643927573L;
	
	protected static final String DEFAULT_PROJECT_NAME = "New Project";
	
	private GridBagConstraints 			gbc;				// Grid bag constraints
	private NamePanel 					namePanel;			// Name panel
	private AssemblyPanel 				assemblyPanel;		// Assembly panel
	private GenomeProjectTypePanel 		genomePanel;		// Genome panel
	private MultiGenomePanel			multiGenomePanel;	// Multi genome panel
	private JPanel 						blankPanel;			// blank panel under the  genome panel when a simple genome is selected
	
	
	/**
	 * Constructor of {@link NewProjectPanel}
	 */
	public NewProjectPanel () {
		super();
		init();
	}
	
	
	/**
	 * Main method of the class.
	 * It initializes the {@link NewProjectPanel} panel.
	 */
	private void init() {
		//Layout
		setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		
		//Panels
		namePanel = new NamePanel();
		assemblyPanel = new AssemblyPanel();
		genomePanel = new GenomeProjectTypePanel();
		//VCFPanel_old = new VCFPanel_old();
		multiGenomePanel = new MultiGenomePanel();
		
		//Fake panel
		blankPanel = new JPanel();
		blankPanel.setSize(ProjectFrame.VCF_DIM);
		blankPanel.setPreferredSize(blankPanel.getSize());
		blankPanel.setMinimumSize(blankPanel.getSize());
		blankPanel.setMaximumSize(blankPanel.getSize());
		blankPanel.setBackground(ProjectFrame.VCF_COLOR);
		
		//Name panel
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(namePanel, gbc);
		
		//Assembly panel
		gbc.gridx = 0;
		gbc.gridy = 1;
		add(assemblyPanel, gbc);
		
		//Genome panel
		gbc.gridx = 0;
		gbc.gridy = 2;
		add(genomePanel, gbc);
		
		//Fake panel
		gbc.gridx = 0;
		gbc.gridy = 3;
		add(multiGenomePanel, gbc);
		add(blankPanel, gbc);
		
		//Size
		setSize(ProjectFrame.NEW_DIM);
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setMaximumSize(getSize());
		assemblyPanel.setPreferredSize(ProjectFrame.ASSEMBLY_DIM);
		genomePanel.setPreferredSize(ProjectFrame.GENOME_DIM);
		
		//Background
		setBackground(ProjectFrame.NEW_COLOR);
	}
	
	
	/**
	 * Displays or hides the var panel
	 * @param visible set to true to show the var table
	 */
	public void setVarTableVisible(boolean visible) {
		multiGenomePanel.setVisible(visible);
		blankPanel.setVisible(!visible);
	}
	
	
	/**
	 * @return a {@link Map} containing the selected chromosomes.  Each chromosome is associated to its name in the map
	 */
	public List<Chromosome> getSelectedChromosomes() {
		return assemblyPanel.getSelectedChromosomes();
	}
	
	
	/**
	 * @return the project name or null if it is not valid
	 */
	public String getProjectName () {
		return namePanel.getProjectName();
	}
	
	
	/**
	 * @return the selected clade
	 */
	public Clade getClade() {
		return assemblyPanel.getSelectedClade();
	}
	
	
	/**
	 * @return the selected genome
	 */
	public Genome getGenome() {
		return assemblyPanel.getSelectedGenome();
	}
	
	
	/**
	 * @return the selected assembly
	 */
	public Assembly getAssembly() {
		return assemblyPanel.getSelectedAssembly();
	}
	
	
	/**
	 * This method determines if user chose a simple or a multi genome project. 
	 * @return true if user chose a simple genome project.
	 */
	public boolean isSingleProject () {
		return genomePanel.isSingleProject();
	}
	
	
	/**
	 * @return the mapping between genome full names and their readers.
	 */
	public Map<String, List<VCFReader>> getGenomeFileAssociation ()  {
		return multiGenomePanel.getGenomeFileAssociation();
	}
	
	
	/**
	 * @return true if the multi genome project is valid
	 */
	public boolean isValidMultigenomeProject () {
		return multiGenomePanel.isValidMultigenomeProject();
	}
	
}
