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
package edu.yu.einstein.genplay.core.manager;

import java.io.File;
import java.util.List;
import edu.yu.einstein.genplay.core.genome.Assembly;


/**
 * This class manages information about the project.
 * @author Nicolas Fourel
 */
public class ProjectManager {

	private static	ProjectManager	instance = null;		// unique instance of the singleton
	private		String		projectName;
	private		String		cladeName;
	private		String		genomeName;
	private  	Assembly 	assembly;
	private 	List<File>	varFiles;
	private		boolean		multiGenomeProject;


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
	}


	/**
	 * @return the multiGenomeProject
	 */
	public boolean isMultiGenomeProject() {
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


	/**
	 * @return the varFiles
	 */
	public List<File> getVarFiles() {
		return varFiles;
	}


	/**
	 * @param varFiles the varFiles to set
	 */
	public void setVarFiles(List<File> varFiles) {
		this.varFiles = varFiles;
	}


	/**
	 * @param multiGenomeProject the multiGenomeProject to set
	 */
	public void setMultiGenomeProject(boolean multiGenomeProject) {
		this.multiGenomeProject = multiGenomeProject;
		ChromosomeManager.getInstance().setChromosomeList();
	}


}
