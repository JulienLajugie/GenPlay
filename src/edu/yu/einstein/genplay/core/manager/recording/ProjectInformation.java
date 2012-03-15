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
package edu.yu.einstein.genplay.core.manager.recording;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * This class contains basic information about a project.
 * 
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ProjectInformation implements Serializable {

	private static final long serialVersionUID = 5252641489962010266L; // generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private File 		file;						// file of the project
	private String 		projectName;				// project name
	private String 		projectGenome;				// project genome
	private String 		projectType;				// project type
	private String 		projectDate;				// project date
	private String 		projectTrackNumber;			// number of track in the project
	private String[] 	projectFiles = null;		// files of the project

	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(file);
		out.writeObject(projectName);
		out.writeObject(projectGenome);
		out.writeObject(projectType);
		out.writeObject(projectDate);
		out.writeObject(projectTrackNumber);
		if (projectFiles == null) {
			out.writeInt(0);
		} else {
			out.writeInt(projectFiles.length);
			for (String path: projectFiles) {
				out.writeObject(path);
			}
		}
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		file = (File) in.readObject();
		projectName = (String) in.readObject();
		projectGenome = (String) in.readObject();
		projectType = (String) in.readObject();
		projectDate = (String) in.readObject();
		projectTrackNumber = (String) in.readObject();
		int numberOfFiles = in.readInt();
		if (numberOfFiles > 0) {
			projectFiles = new String[numberOfFiles];
			for (int i = 0; i < numberOfFiles; i++) {
				projectFiles[i] = (String) in.readObject();
			}
		} else {
			projectFiles = null;
		}
	}


	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}


	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}


	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}


	/**
	 * @param projectGenome the projectGenome to set
	 */
	public void setProjectGenome(String projectGenome) {
		this.projectGenome = projectGenome;
	}


	/**
	 * @return the projectGenome
	 */
	public String getProjectGenome() {
		return projectGenome;
	}


	/**
	 * @param projectType the projectType to set
	 */
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}


	/**
	 * @return the projectType
	 */
	public String getProjectType() {
		return projectType;
	}


	/**
	 * @param projectDate the projectDate to set
	 */
	public void setProjectDate(String projectDate) {
		this.projectDate = projectDate;
	}


	/**
	 * @return the projectDate
	 */
	public String getProjectDate() {
		return projectDate;
	}


	/**
	 * @param projectTrackNumber the projectTrackNumber to set
	 */
	public void setProjectTrackNumber(String projectTrackNumber) {
		this.projectTrackNumber = projectTrackNumber;
	}


	/**
	 * @return the projectTrackNumber
	 */
	public String getProjectTrackNumber() {
		return projectTrackNumber;
	}


	/**
	 * @return the projectFiles
	 */
	public String[] getProjectFiles() {
		return projectFiles;
	}


	/**
	 * @param projectFiles the projectFiles to set
	 */
	public void setProjectFiles(String[] projectFiles) {
		this.projectFiles = projectFiles;
	}
	
}
