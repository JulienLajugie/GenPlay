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
package edu.yu.einstein.genplay.core.multiGenome.operation;

import java.io.File;
import java.io.IOException;

/**
 * The export engine gives basic attributes and control to export a track as a file.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public abstract class ExportEngine extends BasicEngine {

	protected String 							path;			// Path of the new VCF file.
	protected boolean							isConversion = false;



	/**
	 * Checks every parameter and create an full error message if any of them is not valid.
	 * @return the error message, null if no error.
	 */
	@Override
	protected String getParameterErrors () {
		String errors = super.getParameterErrors();

		if (!isConversion) {
			if (path == null) {
				errors = addErrorMessage(errors, "The path of the new VCF file has not been declared.");
			} else {
				File file = new File(path);
				try {
					file.createNewFile();
				} catch (IOException e) {
					errors = addErrorMessage(errors, "The file could not created, the path may not be valid: " + path + ".");
					e.printStackTrace();
				}
				if (!file.isFile()) {
					errors = addErrorMessage(errors, "The path of the new VCF file is not a valid file: " + path + ".");
				}
				file.delete();
			}
		}

		return errors;
	}


	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}


	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
