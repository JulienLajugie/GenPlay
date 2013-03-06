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
package edu.yu.einstein.genplay.core.manager.project;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.genome.Assembly;


/**
 * This class manages information about the project.
 * @author Nicolas Fourel
 */
public class ProjectManager implements Serializable {

	private static final long serialVersionUID = -8900126340763056646L; // generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private static	ProjectManager	instance = null;		// unique instance of the singleton
	private			String			projectName;			// project name
	private			String			cladeName;				// clade name
	private			String			genomeName;				// genome name
	private  		Assembly 		assembly;				// assembly name
	private			boolean			multiGenome;			// True if it is a multi genome project, false if it is a simple genome project


	private final ProjectConfiguration 	projectConfiguration;		// Instance of the Configuration Manager
	private final ProjectWindow			projectWindow;				// Instance of the Genome Window Manager
	private final ProjectZoom 			projectZoom;				// Instance of the Zoom Manager
	private final ProjectChromosome		projectChromosome;			// Instance of the Chromosome Manager
	private MultiGenomeProject			multiGenomeProject;			// Instance of the Multi Genome Project


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(projectName);
		out.writeObject(cladeName);
		out.writeObject(genomeName);
		out.writeObject(assembly);
		out.writeBoolean(multiGenome);

		out.writeObject(projectWindow);
		out.writeObject(projectChromosome);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		getInstance();
		in.readInt();
		instance.setProjectName((String) in.readObject());
		instance.setCladeName((String) in.readObject());
		instance.setGenomeName((String) in.readObject());
		instance.setAssembly((Assembly) in.readObject());
		instance.setMultiGenomeProject(in.readBoolean());

		instance.getProjectWindow().setProjectWindow((ProjectWindow) in.readObject());
		instance.getProjectChromosome().setProjectChromosome((ProjectChromosome) in.readObject());
	}


	/**
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void readMultiGenomeObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		if (instance.isMultiGenomeProject()) {
			instance.getMultiGenomeProject().setMultiGenomeProject((MultiGenomeProject) in.readObject());
		}
	}


	/**
	 * @return an instance of a {@link ProjectManager}.
	 * Makes sure that there is only one unique instance as specified in the singleton pattern
	 */
	public static ProjectManager getInstance() {
		if (instance == null) {
			synchronized(ProjectManager.class) {
				if (instance == null) {
					instance = new ProjectManager();
				}
			}
		}
		return instance;
	}


	/**
	 * Private constructor of the singleton. Creates an instance of a {@link ProjectManager}.
	 */
	private ProjectManager() {
		multiGenome = false;
		projectConfiguration = new ProjectConfiguration();
		projectZoom = new ProjectZoom();
		projectChromosome = new ProjectChromosome();
		projectWindow = new ProjectWindow();
	}


	/**
	 * Updates the chromosome list
	 */
	public void updateChromosomeList () {
		List<Chromosome> chromosomeList;
		chromosomeList = getAssembly().getChromosomeList();
		projectChromosome.setChromosomeList(chromosomeList);
	}


	/**
	 * @return the multiGenomeProject
	 */
	public boolean isMultiGenomeProject() {
		return multiGenome;
	}


	/**
	 * @param multiGenome the multiGenomeProject to set
	 */
	public void setMultiGenomeProject(boolean multiGenome) {
		this.multiGenome = multiGenome;
		if (!multiGenome) {
			multiGenomeProject = null;
		}
	}


	/**
	 * @return the Genome Window Manager
	 */
	public ProjectWindow getProjectWindow() {
		return projectWindow;
	}


	/**
	 * @return the Configuration Manager
	 */
	public ProjectConfiguration getProjectConfiguration () {
		return projectConfiguration;
	}


	/**
	 * @return the Zoom Manager
	 */
	public ProjectZoom getProjectZoom() {
		return projectZoom;
	}


	/**
	 * @return the chromosomeManager
	 */
	public ProjectChromosome getProjectChromosome() {
		return projectChromosome;
	}


	/**
	 * @return the genomeSynchroniser
	 */
	public MultiGenomeProject getMultiGenomeProject() {
		if (multiGenome && (multiGenomeProject == null)) {
			multiGenomeProject = new MultiGenomeProject();
			FormattedMultiGenomeName.REFERENCE_GENOME_NAME = assembly.getDisplayName();
		}
		return multiGenomeProject;
	}


	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}


	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	/**
	 * @return the cladeName
	 */
	public String getCladeName() {
		return cladeName;
	}


	/**
	 * @param cladeName the cladeName to set
	 */
	public void setCladeName(String cladeName) {
		this.cladeName = cladeName;
	}


	/**
	 * @return the genomeName
	 */
	public String getGenomeName() {
		return genomeName;
	}


	/**
	 * @param genomeName the genomeName to set
	 */
	public void setGenomeName(String genomeName) {
		this.genomeName = genomeName;
	}


	/**
	 * @return the assembly
	 */
	public Assembly getAssembly() {
		return assembly;
	}


	/**
	 * @param assembly the assembly to set
	 */
	public void setAssembly(Assembly assembly) {
		this.assembly = assembly;
	}

}
