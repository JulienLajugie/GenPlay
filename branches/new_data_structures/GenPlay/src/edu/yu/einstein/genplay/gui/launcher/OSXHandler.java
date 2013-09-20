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
package edu.yu.einstein.genplay.gui.launcher;

import java.io.File;

import com.apple.eawt.AppEvent.OpenFilesEvent;
import com.apple.eawt.OpenFilesHandler;

/**
 * Handle the opening of a GenPlay project file in Mac OSX when the user
 * launched GenPlay by double clicking on a project file
 * @author Julien Lajugie
 */
class OSXHandler implements OpenFilesHandler {

	/**
	 * GenPlay project file to open in Mac OSX if the user
	 * launched GenPlay by double clicking on a project file
	 */
	private File fileToOpen = null;

	/**
	 * @return the GenPlay project file to open in Mac OSX if the user
	 * launched GenPlay by double clicking on a project file
	 */
	File getFileToOpen() {
		return fileToOpen;
	}

	@Override
	public void openFiles(OpenFilesEvent openFilesEvent) {
		fileToOpen = openFilesEvent.getFiles().get(0);
	}
}
