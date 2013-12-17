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
 * This object is the first one to be written in a project saved file.
 * Therefore, some information can be verified before the loading of the project.
 * 
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public class ProjectInformation implements Serializable {

	private static final long serialVersionUID = 5252641489962010266L; // generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 1;			// saved format version
	private File 		file;						// file of the project
	private String 		projectName;				// project name
	private String		projectPrecision;			// project precision
	private String 		projectGenome;				// project genome
	private String 		projectType;				// project type
	private String 		projectDate;				// project date
	private String 		projectTrackNumber;			// number of track in the project
	private String[] 	projectFiles = null;		// files of the project


	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}


	/**
	 * @return the projectDate
	 */
	public String getProjectDate() {
		return projectDate;
	}


	/**
	 * @return the projectFiles
	 */
	public String[] getProjectFiles() {
		return projectFiles;
	}


	/**
	 * @return the projectGenome
	 */
	public String getProjectGenome() {
		return projectGenome;
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
	public String getProjectPrecision() {
		return projectPrecision;
	}


	/**
	 * @return the projectTrackNumber
	 */
	public String getProjectTrackNumber() {
		return projectTrackNumber;
	}


	/**
	 * @return the projectType
	 */
	public String getProjectType() {
		return projectType;
	}


	/**
	 * @return true if it is a single project, false otherwise
	 */
	public boolean isSingleProject () {
		if (projectType.equals("Single Genome Project")) {
			return true;
		}
		return false;
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		int version = in.readInt();
		file = (File) in.readObject();
		projectName = (String) in.readObject();
		if (version > 0) {
			projectPrecision = (String) in.readObject();
		} else {
			projectPrecision = "Unknown";
		}
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
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}


	/**
	 * @param projectDate the projectDate to set
	 */
	public void setProjectDate(String projectDate) {
		this.projectDate = projectDate;
	}


	/**
	 * @param projectFiles the projectFiles to set
	 */
	public void setProjectFiles(String[] projectFiles) {
		this.projectFiles = projectFiles;
	}


	/**
	 * @param projectGenome the projectGenome to set
	 */
	public void setProjectGenome(String projectGenome) {
		this.projectGenome = projectGenome;
	}


	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	/**
	 * Sets the precision of the scores of the project
	 * @param projectPrecision
	 */
	public void setProjectPrecision(String projectPrecision) {
		this.projectPrecision = projectPrecision;
	}


	/**
	 * @param projectTrackNumber the projectTrackNumber to set
	 */
	public void setProjectTrackNumber(String projectTrackNumber) {
		this.projectTrackNumber = projectTrackNumber;
	}


	/**
	 * @param projectType the projectType to set
	 */
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}


	/**
	 * Shows the project information
	 */
	public void show () {
		String info = "";
		info += "---------- Project Information\n";
		info += "Name: " + projectName + "\n";
		info += "Precision: " + projectPrecision + "\n";
		info += "Genome: " + projectGenome + "\n";
		info += "Date: " + projectDate.toString() + "\n";
		info += "File: " + file.getPath() + "\n";
		info += "Track #: " + projectTrackNumber + "\n";
		info += "Type: " + projectType + "\n";
		if (projectFiles != null) {
			info += "Is file dependant:\n";
			for (int i = 0; i < projectFiles.length; i++) {
				info += (i + 1) + ": " + projectFiles[i] + "\n";
			}
		} else {
			info += "Is not file dependant\n";
		}
		info += "----------";
		System.out.println(info);
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(file);
		out.writeObject(projectName);
		out.writeObject(projectPrecision);
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
}
