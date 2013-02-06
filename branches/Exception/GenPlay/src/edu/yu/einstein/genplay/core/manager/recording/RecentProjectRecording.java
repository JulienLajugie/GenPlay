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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidFileTypeException;

/**
 * This class manages the basic information about the last saved projects.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class RecentProjectRecording {

	private static final int PROJECT_NUMBER = 5;						// number of recent project path to save
	private ProjectInformation[] 	projects;
	private String[] 				projectPaths;


	/**
	 * This method writes the projects list content in the configuration file.
	 * It updates the list before writing information.
	 */
	public void writeProjects () {
		updateProjectPaths();
		String path = ProjectManager.getInstance().getProjectConfiguration().getRecentProjectsAbsolutePath();
		File file = new File(path);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			GZIPOutputStream gz = new GZIPOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(gz);
			for (int i = 0; i < PROJECT_NUMBER; i++) {
				oos.writeObject(projectPaths[i]);
			}
			oos.flush();
			oos.close();
			gz.flush();
			gz.close();
			fos.flush();
			fos.close();
		} catch (IOException e) {
			ExceptionManager.getInstance().handleException(e);
		}
	}


	/**
	 * Updates the paths array
	 */
	private void updateProjectPaths () {
		refresh();			// we first refresh the list of existing path (in order to be sure to have the last modification)
		ProjectInformation currentProjectInformation = RecordingManager.getInstance().getProjectRecording().getProjectInformation();
		String currentProjectPath = currentProjectInformation.getFile().getPath();	// gets the path of the current project

		// we look if the current path already exists in the current list
		int currentExistingProjectPath = -1;												// set by default at -1
		for (int i = 0; i < PROJECT_NUMBER; i++) {											// scan all path to test them
			if ((projectPaths[i] != null) && projectPaths[i].equals(currentProjectPath)) {	// if the path has been found
				currentExistingProjectPath = i;												// we store its index
			}
		}

		String[] newProjectPaths = new String[PROJECT_NUMBER];		// sets a new array of paths
		newProjectPaths[0] = currentProjectPath;					// the first path is obviously the one of the current project
		if (currentExistingProjectPath == -1) {						// if the current path does not exist among the current ones
			for (int i = 1; i < PROJECT_NUMBER; i++) {				// we go through them
				newProjectPaths[i] = projectPaths[i - 1];			// in order to add them (i - 1 : all old paths go one step down)
			}
		} else {													// if the current path exists among the current ones
			for (int i = 1; i < PROJECT_NUMBER; i++) {				// we scan them
				if (i <= currentExistingProjectPath) {				// if the index is still inferior or equal to the old index of the current path,
					newProjectPaths[i] = projectPaths[i - 1];		// we add the previous ones
				} else {											// if we are one step after the index of the current path
					newProjectPaths[i] = projectPaths[i];			// we add the actual path, we jump over the index of the current path
				}
			}
		}
		projectPaths = newProjectPaths;								// the old array is replaced with the new one
	}


	/**
	 * This methods refreshes the list of recent saved projects.
	 * It first gets the path of the recent project and then, retrieves the project information for all of them.
	 */
	public void refresh () {
		try {
			retrieveProjectsPath();
			retrieveProjectsInformation();
		} catch (Exception e) {
			ExceptionManager.getInstance().handleException(e);
		}
	}


	/**
	 * Retrieves basic information about all recent projects
	 */
	public void retrieveProjectsPath () {
		String path = ProjectManager.getInstance().getProjectConfiguration().getRecentProjectsAbsolutePath();
		File file = new File(path);
		projectPaths = new String[PROJECT_NUMBER];
		if (file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				GZIPInputStream gz = new GZIPInputStream(fis);
				ObjectInputStream ois = new ObjectInputStream(gz);
				for (int i = 0; i < PROJECT_NUMBER; i++) {
					Object retrievedPath = ois.readObject();
					if (retrievedPath != null) {
						projectPaths[i] = (String) retrievedPath;
					} else {
						projectPaths[i] = null;
					}
				}
				ois.close();
				gz.close();
				fis.close();
			} catch (Exception e) {
				ExceptionManager.getInstance().handleException(e);
			}
		} else {
			for (int i = 0; i < PROJECT_NUMBER; i++) {
				projectPaths[i] = null;
			}
		}
	}


	/**
	 * This method retrieves all project information using retrieved project paths.
	 * If a project path is not valid, its information will be lost.
	 * @throws Exception
	 */
	private void retrieveProjectsInformation () throws Exception {
		projects = new ProjectInformation[PROJECT_NUMBER];
		for (int i = 0; i < PROJECT_NUMBER; i++) {
			boolean valid = false;
			String currentPath = projectPaths[i];
			File file = null;
			if (currentPath != null) {
				file = new File(currentPath);
				if (file.exists()) {
					valid = true;
				}
			}
			if (valid) {
				projects[i] = getProjectInformation(file);
			} else {
				projects[i] = null;
			}
		}
	}


	/**
	 * Retrieve the project informations from an input file
	 * @param inputFile
	 * @return the {@link ProjectInformation} of the specified input file
	 * @throws Exception
	 */
	public ProjectInformation getProjectInformation(File inputFile) throws Exception {
		try {
			FileInputStream fis = new FileInputStream(inputFile);
			GZIPInputStream gz = new GZIPInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(gz);
			ProjectInformation projectInformation = (ProjectInformation) ois.readObject();
			ois.close();
			gz.close();
			fis.close();
			return projectInformation;
		} catch (IOException e) {
			// a IOException is likely to be caused by a invalid file type
			throw new InvalidFileTypeException();
		}
	}


	/**
	 * @return the projects
	 */
	public ProjectInformation[] getProjects() {
		return projects;
	}

}
