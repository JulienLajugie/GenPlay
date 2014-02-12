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
package edu.yu.einstein.genplay.core.manager;

import java.io.File;


/**
 * The project file manager must be used when a project is file dependant.
 * Some project as the multi genome projects are file dependants.
 * A project file dependant is a project that requires file(s) at its loading.
 * 
 * The required files are stored as current files.
 * If they are not valid, new files must be defined and store as new files.
 * 
 * A project becomes file dependant using the method setCurrentFiles.
 * It will take effect at the saving.
 * 
 * When a class needs a file, it as to use the method getValidPathOf/getValidFileOf in order to use a valid file.
 * By default, the new file will be returned if it has been defined for the requested file.
 * If there is no new file associated, the requested file is supposed to be valid and then returned.
 * 
 * When the project information is written (save project operation), the process must use the method getValidArrayOfFiles.
 * This method return a mix between current and new files according to the rules above.
 * 
 * This manager is not serialized, it is initialized when at the loading project process.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ProjectFiles {

	/** Separator on windows platforms */
	private final static char WINDOWS_FILE_SEPARATOR = '\\';

	/** Separator on Unix platforms */
	private final static char UNIX_FILE_SEPARATOR = '/';

	private static	ProjectFiles	instance = null;		// unique instance of the singleton


	/**
	 * @return an instance of a {@link ProjectFiles}.
	 * Makes sure that there is only one unique instance as specified in the singleton pattern
	 */
	public static ProjectFiles getInstance() {
		if (instance == null) {
			synchronized(ProjectFiles.class) {
				if (instance == null) {
					instance = new ProjectFiles();
				}
			}
		}
		return instance;
	}

	private String[] currentFiles;	// current or old files, these are the ones supposed to be used if no new file defined
	private String[] newFiles;		// if the current files are not valid, here are the ones selected by the user


	/**
	 * Constructor of {@link ProjectFiles}
	 */
	protected ProjectFiles () {
		currentFiles = null;
		newFiles = null;
	}


	private String createPlatformIndependantFileName(String path) {
		return path.replace(WINDOWS_FILE_SEPARATOR, UNIX_FILE_SEPARATOR);
	}


	/**
	 * @return the oldFiles
	 */
	public String[] getCurrentFiles() {
		return currentFiles;
	}


	/**
	 * @return the newFiles
	 */
	public String[] getNewFiles() {
		return newFiles;
	}


	/**
	 * Creates the array containing valid file paths.
	 * The new paths are used by default, if one is missing the current path is used.
	 * @return the array of the most recent set of path
	 */
	public String[] getValidArrayOfFiles () {
		String[] array = new String[currentFiles.length];

		for (int i = 0; i < array.length; i++) {
			if ((newFiles != null) && (newFiles[i] != null)) {
				array[i] = newFiles[i];
			} else {
				array[i] = currentFiles[i];
			}
		}

		return array;
	}


	/**
	 * Controls the path of the given file in order to see if another path has been defined for it.
	 * This method uses the getValidPathOf method.
	 * If no path has been define for this file, it will return the same file.
	 * @param file 	the file
	 * @return		a valid file (if defined, the same otherwise)
	 */
	public File getValidFileOf (File file) {
		String path = file.getPath();
		String newPath = getValidPathOf(path);
		if (path.equals(newPath)) {
			return file;
		}
		return new File(newPath);
	}


	/**
	 * Get the path of this path, it means that if a new one has been given for this path, it will be used!
	 * If no path has been define, it will return the same path.
	 * @param path 	the path
	 * @return		the valid path (if defined, the same otherwise)
	 */
	public String getValidPathOf (String path) {
		if ((currentFiles == null) || (newFiles == null)) {
			return path;
		}
		int oldPathIndex = -1;
		path = createPlatformIndependantFileName(path);
		for (int i = 0; i < currentFiles.length; i++) {
			String currentFile = createPlatformIndependantFileName(currentFiles[i]);
			if (currentFile.equals(path)) {
				oldPathIndex = i;
				break;
			}
		}

		if (oldPathIndex >= 0) {
			if (newFiles[oldPathIndex] != null) {
				return newFiles[oldPathIndex];
			}
		}
		return path;
	}


	/**
	 * @return true if the project is file dependent, false otherwise
	 */
	public boolean isFileDependant () {
		if (currentFiles == null) {
			return false;
		} else {
			return true;
		}
	}


	/**
	 * @param currentFiles the oldFiles to set
	 */
	public void setCurrentFiles(String[] currentFiles) {
		this.currentFiles = currentFiles;
	}


	/**
	 * @param newFiles the newFiles to set
	 */
	public void setNewFiles(String[] newFiles) {
		this.newFiles = newFiles;
	}
}
