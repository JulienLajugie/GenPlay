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
import java.util.HashMap;
import java.util.Map;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.operation.VCF.MGOApplyVCFGenotype;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.exception.ExceptionManager;

/**
 * The update engine is made to create a new VCF file based on a file to update, using data from a current VCF track.
 * The first example in GenPlay is the {@link MGOApplyVCFGenotype}.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public abstract class UpdateEngine extends BasicEngine {

	// Input parameters: file to update
	protected VCFFile							fileToUpdate;	// The file to update.
	protected Map<String, String> 				genomeNameMap;	// The map between names from the track and the file to update.

	// Output file
	protected String 							path;			// Path of the new VCF file.


	/**
	 * Checks every parameter and create an full error message if any of them is not valid.
	 * @return the error message, null if no error.
	 */
	@Override
	protected String getParameterErrors () {
		String errors = super.getParameterErrors();

		if (fileToUpdate == null) {
			errors = addErrorMessage(errors, "The VCF file to update has not been declared.");
		}

		if (path == null) {
			errors = addErrorMessage(errors, "The path of the new VCF file has not been declared.");
		} else {
			File file = new File(path);
			try {
				file.createNewFile();
			} catch (IOException e) {
				errors = addErrorMessage(errors, "The file could not created, the path may not be valid: " + path + ".");
				ExceptionManager.getInstance().caughtException(e);
			}
			if (!file.isFile()) {
				errors = addErrorMessage(errors, "The path of the new VCF file is not a valid file: " + path + ".");
			}
			file.delete();
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


	/**
	 * @return the fileToPhase
	 */
	public VCFFile getFileToPhase() {
		return fileToUpdate;
	}


	/**
	 * @param fileToPhase the fileToPhase to set
	 */
	public void setFileToPhase(VCFFile fileToPhase) {
		this.fileToUpdate = fileToPhase;
	}


	/**
	 * @return the genomeNameMap
	 */
	public Map<String, String> getGenomeNameMap() {
		return genomeNameMap;
	}


	/**
	 * @param genomeNameMap the genomeNameMap to set
	 */
	public void setGenomeNameMap(Map<String, String> genomeNameMap) {
		this.genomeNameMap = new HashMap<String, String>();
		for (String rawDestName: genomeNameMap.keySet()) {
			String valueRawName = FormattedMultiGenomeName.getRawName(genomeNameMap.get(rawDestName));
			this.genomeNameMap.put(rawDestName, valueRawName);
		}
	}

}
