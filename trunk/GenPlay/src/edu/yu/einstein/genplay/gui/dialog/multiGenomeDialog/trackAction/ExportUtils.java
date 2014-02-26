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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction;

import java.io.File;

import edu.yu.einstein.genplay.gui.fileFilter.ExtendedFileFilter;
import edu.yu.einstein.genplay.util.FileChooser;
import edu.yu.einstein.genplay.util.Utils;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ExportUtils {


	/**
	 * @param filters list of filters
	 * @param open true if the dialog has to select/open a file, false if the dialog has to save a file
	 * @return a file to export the VCF
	 */
	public static File getFile (ExtendedFileFilter[] filters, boolean open) {
		int mode;
		if (open) {
			mode = FileChooser.OPEN_FILE_MODE;
		} else {
			mode = FileChooser.SAVE_FILE_MODE;
		}
		File selectedFile = FileChooser.chooseFile(null, mode, "Select an Output File", filters, false);
		if(selectedFile != null) {
			selectedFile = Utils.addExtension(selectedFile, filters[0].getExtensions()[0]);
			return selectedFile;
		}
		return null;
	}
}
