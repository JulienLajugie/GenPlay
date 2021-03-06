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
package edu.yu.einstein.genplay.core.manager.project;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.genome.Assembly;
import edu.yu.einstein.genplay.dataStructure.gwBookmark.GWBookmark;
import edu.yu.einstein.genplay.dataStructure.list.primitiveList.PrimitiveList;


/**
 * This class manages information about the project.
 * @author Nicolas Fourel
 */
public class ProjectManager implements Serializable {

	private static final long serialVersionUID = -8900126340763056646L; // generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 2;			// saved format version
	private static	ProjectManager	instance = null;					// unique instance of the singleton

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


	private	String						projectName;				// project name
	private transient File				projectDirectory = null;	// directory where the project is saved
	private	String						cladeName;					// clade name
	private	String						genomeName;					// genome name
	private Assembly 					assembly;					// assembly name
	private	boolean						isMultiGenome;				// True if it is a multi genome project, false if it is a simple genome project
	private ScorePrecision				projectScorePrecision;		// precision of the scores of the project (16 / 32 BIT)
	private final ProjectWindow			projectWindow;				// Instance of the Genome Window Manager
	private final ProjectZoom 			projectZoom;				// Instance of the Zoom Manager
	private final ProjectChromosomes	projectChromosomes;			// Instance of the Chromosome Manager
	private MultiGenomeProject			multiGenomeProject;			// Instance of the Multi Genome Project
	private List<GWBookmark> 			projectBookmarks;			// Genome windows bookmarked for the project


	/**
	 * Private constructor of the singleton. Creates an instance of a {@link ProjectManager}.
	 */
	private ProjectManager() {
		isMultiGenome = false;
		projectZoom = new ProjectZoom();
		projectChromosomes = new ProjectChromosomes();
		projectWindow = new ProjectWindow();
		projectBookmarks = new ArrayList<GWBookmark>();
	}


	/**
	 * @return the assembly
	 */
	public Assembly getAssembly() {
		return assembly;
	}


	/**
	 * @return the cladeName
	 */
	public String getCladeName() {
		return cladeName;
	}


	/**
	 * @return the genomeName
	 */
	public String getGenomeName() {
		return genomeName;
	}


	/**
	 * @return the genomeSynchroniser
	 */
	public MultiGenomeProject getMultiGenomeProject() {
		if (isMultiGenome && (multiGenomeProject == null)) {
			multiGenomeProject = new MultiGenomeProject();
			FormattedMultiGenomeName.REFERENCE_GENOME_NAME = assembly.getDisplayName();
		}
		return multiGenomeProject;
	}


	/**
	 * @return the list of the genome windows bookmarked
	 */
	public List<GWBookmark> getProjectBookmarks() {
		return projectBookmarks;
	}


	/**
	 * @return the chromosomeManager
	 */
	public ProjectChromosomes getProjectChromosomes() {
		return projectChromosomes;
	}


	/**
	 * @return the directory where the project is saved
	 */
	public File getProjectDirectory() {
		return projectDirectory;
	}


	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}


	/**
	 * @return the precision of the scores of the project
	 */
	public ScorePrecision getProjectScorePrecision() {
		return projectScorePrecision;
	}


	/**
	 * @return the Genome Window Manager
	 */
	public ProjectWindow getProjectWindow() {
		return projectWindow;
	}


	/**
	 * @return the Zoom Manager
	 */
	public ProjectZoom getProjectZoom() {
		return projectZoom;
	}


	/**
	 * @return the multiGenomeProject
	 */
	public boolean isMultiGenomeProject() {
		return isMultiGenome;
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
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		getInstance();
		int savedVersion = in.readInt();
		instance.setProjectName((String) in.readObject());
		instance.setCladeName((String) in.readObject());
		instance.setGenomeName((String) in.readObject());
		instance.setAssembly((Assembly) in.readObject());
		instance.setMultiGenomeProject(in.readBoolean());
		if (savedVersion >= 1) {
			instance.setProjectScorePrecision((ScorePrecision) in.readObject());
		}
		instance.getProjectWindow().setProjectWindow((ProjectWindow) in.readObject());
		instance.getProjectChromosomes().setProjectChromosomes((ProjectChromosomes) in.readObject());
		if (savedVersion >= 2) {
			instance.projectBookmarks = (List<GWBookmark>) in.readObject();
		} else {
			instance.projectBookmarks = new ArrayList<GWBookmark>();
		}
	}


	/**
	 * @param assembly the assembly to set
	 */
	public void setAssembly(Assembly assembly) {
		this.assembly = assembly;
	}


	/**
	 * @param cladeName the cladeName to set
	 */
	public void setCladeName(String cladeName) {
		this.cladeName = cladeName;
	}


	/**
	 * @param genomeName the genomeName to set
	 */
	public void setGenomeName(String genomeName) {
		this.genomeName = genomeName;
	}


	/**
	 * @param multiGenome the multiGenomeProject to set
	 */
	public void setMultiGenomeProject(boolean multiGenome) {
		isMultiGenome = multiGenome;
		if (!multiGenome) {
			multiGenomeProject = null;
		}
	}


	/**
	 * Sets the directory where the project is saved
	 * @param projectDirectory
	 */
	public void setProjectDirectory(File projectDirectory) {
		this.projectDirectory = projectDirectory;
	}


	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	/**
	 * @param projectScorePrecision the ScorePrecision of the scores of the project
	 */
	public void setProjectScorePrecision(ScorePrecision projectScorePrecision) {
		PrimitiveList.setScorePrecision(projectScorePrecision);
		this.projectScorePrecision = projectScorePrecision;
	}


	/**
	 * Updates the chromosome list
	 */
	public void updateChromosomeList () {
		List<Chromosome> chromosomeList;
		chromosomeList = getAssembly().getChromosomeList();
		projectChromosomes.setChromosomeList(chromosomeList);
	}


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
		out.writeBoolean(isMultiGenome);
		out.writeObject(projectScorePrecision);
		out.writeObject(projectWindow);
		out.writeObject(projectChromosomes);
		out.writeObject(projectBookmarks);
	}
}
