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

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.fileFilter.ExtendedFileFilter;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.util.Utils;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ExportUtils {


	/**
	 * @param filters list of filters
	 * @param open true if the dialog has to select/open a file, wrong if the dialog has to save a file
	 * @return a file to export the VCF
	 */
	public static File getFile (FileFilter[] filters, boolean open) {
		String defaultDirectory = ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory();
		JFileChooser jfc = new JFileChooser(defaultDirectory);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setDialogTitle("Select an output file");
		for (FileFilter currentFilter: filters) {
			jfc.addChoosableFileFilter(currentFilter);
		}
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setFileFilter(jfc.getChoosableFileFilters()[0]);
		int returnVal = -1;
		boolean checkIfExist = true;
		if (open) {
			returnVal = jfc.showOpenDialog(MainFrame.getInstance().getRootPane());
			checkIfExist = false;
		} else {
			returnVal = jfc.showSaveDialog(MainFrame.getInstance().getRootPane());
		}
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			ExtendedFileFilter selectedFilter = (ExtendedFileFilter)jfc.getFileFilter();
			File selectedFile = Utils.addExtension(jfc.getSelectedFile(), selectedFilter.getExtensions()[0]);
			if (checkIfExist) {
				if (!Utils.cancelBecauseFileExist(MainFrame.getInstance().getRootPane(), selectedFile)) {
					return selectedFile;
				}
			} else {
				return selectedFile;
			}
		}
		return null;
	}


	/**
	 * Set the size of a component
	 * @param c		the component
	 * @param dim	the dimensions
	 */
	public static void setComponentSize (Component c, Dimension dim) {
		c.setSize(dim);
		c.setMinimumSize(dim);
		c.setMaximumSize(dim);
		c.setPreferredSize(dim);
	}
}
