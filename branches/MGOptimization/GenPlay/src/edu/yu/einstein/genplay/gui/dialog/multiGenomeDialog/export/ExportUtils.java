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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.export;

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
	 * @return a file to export the VCF
	 */
	protected static File getFile (FileFilter[] filters) {
		String defaultDirectory = ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory();
		JFileChooser jfc = new JFileChooser(defaultDirectory);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setDialogTitle("Select an output file");
		for (FileFilter currentFilter: filters) {
			jfc.addChoosableFileFilter(currentFilter);
		}
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setFileFilter(jfc.getChoosableFileFilters()[0]);
		int returnVal = jfc.showSaveDialog(MainFrame.getInstance().getRootPane());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			ExtendedFileFilter selectedFilter = (ExtendedFileFilter)jfc.getFileFilter();
			File selectedFile = Utils.addExtension(jfc.getSelectedFile(), selectedFilter.getExtensions()[0]);
			if (!Utils.cancelBecauseFileExist(MainFrame.getInstance().getRootPane(), selectedFile)) {
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
	protected static void setComponentSize (Component c, Dimension dim) {
		c.setSize(dim);
		c.setMinimumSize(dim);
		c.setMaximumSize(dim);
		c.setPreferredSize(dim);
	}
}
