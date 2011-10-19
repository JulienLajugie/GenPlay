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
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.genome.Assembly;


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
	private			boolean			multiGenomeProject;		// True if it is a multi genome project, false if it is a simple genome project 


	private ProjectConfiguration 		projectConfiguration;		// Instance of the Configuration Manager
	private ProjectZoom 				projectZoom;				// Instance of the Zoom Manager
	private ProjectChromosome			projectChromosome;			// Instance of the Chromosome Manager
	private GenomeSynchronizer			genomeSynchronizer;			// Instance of the Genome Synchroniser


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
		out.writeBoolean(multiGenomeProject);

		out.writeObject(projectConfiguration);
		out.writeObject(projectZoom);
		out.writeObject(projectChromosome);
		out.writeObject(genomeSynchronizer);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		projectName = (String) in.readObject();
		cladeName = (String) in.readObject();
		genomeName = (String) in.readObject();
		assembly = (Assembly) in.readObject();
		multiGenomeProject = in.readBoolean();

		projectConfiguration = (ProjectConfiguration) in.readObject();
		projectZoom = (ProjectZoom) in.readObject();
		projectChromosome = (ProjectChromosome) in.readObject();
		genomeSynchronizer = (GenomeSynchronizer) in.readObject();

		instance = this;
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
		multiGenomeProject = false;
		projectConfiguration = new ProjectConfiguration();
		projectZoom = new ProjectZoom();
		projectChromosome = new ProjectChromosome();
		//updateChromosomeList();
	}


	/**
	 * Updates the chromosome list
	 */
	public void updateChromosomeList () {
		List<Chromosome> chromosomeList;
		if (multiGenomeProject) {
			chromosomeList = genomeSynchronizer.getMetaGenomeSynchroniser().getChromosomeList();
			if (chromosomeList == null) {
				chromosomeList = getAssembly().getChromosomeList();
			} else {
				System.err.println("is multi project but meta genome does not have chromosomes list");
			}
		} else {
			chromosomeList = getAssembly().getChromosomeList();
		}
		projectChromosome.setChromosomeList(chromosomeList);
	}


	/**
	 * In a multi genome project, the list of chromosome can be different as usual.
	 * See the CHROMOSOME_LOADING_OPTION in {@link GenomeSynchronizer}.
	 * @return the chromosomeList for multi genome scan algorithm
	 */
	public List<Chromosome> getCurrentMultiGenomeChromosomeList() {
		if (GenomeSynchronizer.CHROMOSOME_LOADING_OPTION == GenomeSynchronizer.FULL) {
			return projectChromosome.getChromosomeList();
		} else {
			List<Chromosome> newList = new ArrayList<Chromosome>();
			newList.add(projectChromosome.getCurrentChromosome());
			return newList;
		}
	}
	

	/**
	 * @return the multiGenomeProject
	 */
	public boolean isMultiGenomeProject() {
		return multiGenomeProject;
	}


	/**
	 * @param multiGenomeProject the multiGenomeProject to set
	 */
	public void setMultiGenomeProject(boolean multiGenomeProject) {
		this.multiGenomeProject = multiGenomeProject;
		if (!multiGenomeProject) {
			genomeSynchronizer = null;
		}
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
	public GenomeSynchronizer getGenomeSynchronizer() {
		if (multiGenomeProject && genomeSynchronizer == null) {
			genomeSynchronizer = new GenomeSynchronizer(projectChromosome.getChromosomeList());
		}
		return genomeSynchronizer;
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
