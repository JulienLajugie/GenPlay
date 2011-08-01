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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.enums.VCFType;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.ProjectScreenManager;


/**
 * This class manages all information regarding new project information.
 * It displays and organize the communication of every panels. 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class NewProject extends JPanel {
	
	private static final long serialVersionUID = 2223959265643927573L;
	
	private GridBagConstraints 			gbc;				// Grid bag constraints
	private NamePanel 					namePanel;			// Name panel
	private AssemblyPanel 				assemblyPanel;		// Assembly panel
	private GenomeProjectTypePanel 		genomePanel;		// Genome panel
	private static MultiGenomePanel		multiGenomePanel;	// Multi genome panel
	private static JPanel 				fakePanel;			// Fake panel 
	
	
	/**
	 * Constructor of {@link NewProject}
	 */
	public NewProject () {
		super();
		init();
	}
	
	
	/**
	 * Main method of the class.
	 * It initializes the {@link NewProject} panel.
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
		fakePanel = new JPanel();
		fakePanel.setSize(ProjectScreenManager.getVCFDim());
		fakePanel.setPreferredSize(fakePanel.getSize());
		fakePanel.setMinimumSize(fakePanel.getSize());
		fakePanel.setMaximumSize(fakePanel.getSize());
		fakePanel.setBackground(ProjectScreenManager.getVCFColor());
		
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
		add(fakePanel, gbc);
		
		//Size
		setSize(ProjectScreenManager.getNewDim());
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setMaximumSize(getSize());
		assemblyPanel.setPreferredSize(ProjectScreenManager.getAssemblyDim());
		genomePanel.setPreferredSize(ProjectScreenManager.getGenomeDim());
		
		//Background
		setBackground(ProjectScreenManager.getNewColor());
	}
	
	
	/**
	 * This method displays the var panel
	 */
	public static void showVarTable () {
		fakePanel.setVisible(false);
		multiGenomePanel.setVisible(true);
	}
	
	
	/**
	 * This method hides the var panel
	 */
	public static void hideVarTable () {
		multiGenomePanel.setVisible(false);
		fakePanel.setVisible(true);
	}
	
	
	/**
	 * @return the selected chromosome list
	 */
	public Map<String, Chromosome> getNewChromosomeList() {
		return assemblyPanel.getNewChromosomeList();
	}
	
	
	/**
	 * @return the project name or null if it is not valid
	 */
	public String getProjectName () {
		if (namePanel.getProjectName().equals("")) {
			JOptionPane.showMessageDialog(getRootPane(), "Please fill the project name field", "Invalid project name", JOptionPane.WARNING_MESSAGE);
			return null;
		} else {
			return namePanel.getProjectName();
		}
	}
	
	
	/**
	 * @return the name of the selected clade
	 */
	public String getCladeName () {
		return assemblyPanel.getSelectedClade();
	}
	
	
	/**
	 * @return the name of the selected genome
	 */
	public String getGenomeName () {
		return assemblyPanel.getSelectedGenome();
	}
	
	
	/**
	 * @return the name of the selected assembly
	 */
	public String getAssemblyName () {
		return assemblyPanel.findAssembly();
	}
	
	
	/**
	 * This method determines if user chose a simple or a multi genome project. 
	 * @return true if user chose a simple genome project.
	 */
	public boolean isSimpleProject () {
		return genomePanel.isSimpleProject();
	}
	
	
	/**
	 * @return the genome/group association
	 */
	public Map<String, List<String>> getGenomeGroupAssociation () {
		return multiGenomePanel.getGenomeGroupAssociation();
	}
	
	
	/**
	 * @return the genome/VCF association
	 */
	public Map<String, List<File>> getGenomeFilesAssociation () {
		return multiGenomePanel.getGenomeFilesAssociation();
	}
	
	
	/**
	 * @return the raw/usual genome names association
	 */
	public Map<String, String> getGenomeNamesAssociation () {
		return multiGenomePanel.getGenomeNamesAssociation();
	}
	
	
	/**
	 * @return the VCF type/files association
	 */
	public Map<VCFType, List<File>> getFilesTypeAssociation () {
		return multiGenomePanel.getFilesTypeAssociation();
	}
	
	
	/**
	 * @return true if the multi genome project is valid
	 */
	public boolean isValidMultigenomeProject () {
		return multiGenomePanel.isValidMultigenomeProject();
	}
	
}