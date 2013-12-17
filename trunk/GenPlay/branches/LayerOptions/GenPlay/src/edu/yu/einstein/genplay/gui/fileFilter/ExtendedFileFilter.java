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
package edu.yu.einstein.genplay.gui.fileFilter;

import java.io.File;
import java.io.Serializable;

import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.util.Utils;



/**
 * Extension of the {@link FileFilter} class with a new method 
 * that returns the list of the extensions accepted by the filter
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class ExtendedFileFilter extends FileFilter implements Serializable {

	private static final long serialVersionUID = -4860666388921247783L;	// generated ID
	private final String[] 	extensions;		// extensions accepted for this file type
	private final String 	description;	// description of the file type
	
	
	/**
	 * Creates an instance of {@link ExtendedFileFilter} 
	 * @param extensions extensions associated with the file filter
	 * @param description description of the file filter
	 */
	protected ExtendedFileFilter(String[] extensions, String description) {
		this.extensions = extensions;
		this.description = description;
	}
	
	
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		String extension = Utils.getExtension(f);
		if (extension == null) {
			return false;
		} else {
			for (String currentExtension: extensions) {
				if (currentExtension.equalsIgnoreCase(extension)) {
					return true;
				}
			}
			return false;
		}
	}

	
	@Override
	public String getDescription() {
		return description;
	}


	/**
	 * @return the list of the extensions accepted by the filter
	 */
	public String[] getExtensions() {
		return extensions;
	}
}
