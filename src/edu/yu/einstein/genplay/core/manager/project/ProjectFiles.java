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


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ProjectFiles {

	private String[] oldFiles;
	private String[] newFiles;


	/**
	 * Constructor of {@link ProjectFiles}
	 */
	protected ProjectFiles () {
		oldFiles = null;
		newFiles = null;
	}


	public boolean isFileDependant () {
		if (oldFiles == null) {
			return false;
		} else {
			return true;
		}
	}


	public String getNewPathOf (String oldPath) {
		int oldPathIndex = -1;
		for (int i = 0; i < oldFiles.length; i++) {
			if (oldFiles[i].equals(oldPath)) {
				oldPathIndex = i;
				break;
			}
		}
		if (oldPathIndex >= 0) {
			return newFiles[oldPathIndex];
		}
		return null;
	}


	/**
	 * @return the oldFiles
	 */
	public String[] getOldFiles() {
		return oldFiles;
	}


	/**
	 * @param oldFiles the oldFiles to set
	 */
	public void setOldFiles(String[] oldFiles) {
		this.oldFiles = oldFiles;
	}


	/**
	 * @return the newFiles
	 */
	public String[] getNewFiles() {
		return newFiles;
	}


	/**
	 * @param newFiles the newFiles to set
	 */
	public void setNewFiles(String[] newFiles) {
		this.newFiles = newFiles;
	}


}
