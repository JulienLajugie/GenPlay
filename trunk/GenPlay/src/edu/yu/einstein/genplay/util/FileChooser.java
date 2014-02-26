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
package edu.yu.einstein.genplay.util;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.core.manager.application.ConfigurationManager;
import edu.yu.einstein.genplay.gui.fileFilter.ExtendedFileFilter;


/**
 * Class providing file choosers to save or load files or directories
 * @author Julien Lajugie
 */
public class FileChooser {

	/** Show a file chooser dialog to open a single file */
	public static final int OPEN_FILE_MODE = 0;

	/** Show a file chooser dialog to open a single directory */
	public static final int OPEN_DIRECTORY_MODE = 1;

	/** Show a file chooser dialog to save a file */
	public static final int SAVE_FILE_MODE = 2;

	/** True to use a AWT file chooser component, false to use a swing one*/
	private static final boolean USE_AWT_CHOOSER = true;


	/**
	 * Opens a dialog box asking the user to choose a file or a directory to load or to save
	 * @param parentComponent determines the Frame in which the dialog is displayed; if null, or if the parentComponent has no Frame, a default Frame is used
	 * @param mode one of {@link #OPEN_FILE_MODE}, {@link #OPEN_DIRECTORY_MODE} or {@link #SAVE_FILE_MODE}
	 * @param title title of the open dialog
	 * @param choosableFileFilters {@link FileFilter} available
	 * @param allFiles allow the selection of every kind of file if true, disable the all file selection if false
	 * @return a file or a directory
	 */
	public final static File chooseFile(Component parentComponent, int mode, String title, FileFilter[] choosableFileFilters, boolean allFiles) {
		return chooseFile(parentComponent, mode, title, choosableFileFilters, allFiles, null);
	}


	/**
	 * Opens a dialog box asking the user to choose a file or a directory to load or to save
	 * @param parentComponent determines the Frame in which the dialog is displayed; if null, or if the parentComponent has no Frame, a default Frame is used
	 * @param mode one of {@link #OPEN_FILE_MODE}, {@link #OPEN_DIRECTORY_MODE} or {@link #SAVE_FILE_MODE}
	 * @param title title of the open dialog
	 * @param choosableFileFilters {@link FileFilter} available
	 * @param allFiles allow the selection of every kind of file if true, disable the all file selection if false
	 * @param selectedFile file preselected when the chooser pops up
	 * @return a file or a directory
	 */
	public final static File chooseFile(Component parentComponent, int mode, String title, FileFilter[] choosableFileFilters, boolean allFiles, File selectedFile) {
		if (USE_AWT_CHOOSER) {
			selectedFile = chooseFileUsingAWT(parentComponent, mode, choosableFileFilters, selectedFile);
		} else {
			selectedFile = chooseFileUsingSwing(parentComponent, mode, title, choosableFileFilters, allFiles, selectedFile);
		}
		if (selectedFile == null) {
			return null;
		} else if (((mode == OPEN_FILE_MODE) || (mode == OPEN_DIRECTORY_MODE)) && !selectedFile.canRead()) {
			JOptionPane.showMessageDialog(parentComponent,
					"You don't have the permission to read the selected file.", "Incorrect File", JOptionPane.ERROR_MESSAGE, null);
			return null;
		} else if (mode == SAVE_FILE_MODE) {
			if (!(USE_AWT_CHOOSER && Utils.isMacOS()) && Utils.cancelBecauseFileExist(parentComponent, selectedFile)) {
				return null;
			}
		}
		return selectedFile;
	}


	/**
	 * Opens a dialog box asking the user to choose a file or a directory to load or to save using a AWT component file chooser
	 * @param parentComponent determines the Frame in which the dialog is displayed; if null, or if the parentComponent has no Frame, a default Frame is used
	 * @param mode one of {@link #OPEN_FILE_MODE}, {@link #OPEN_DIRECTORY_MODE} or {@link #SAVE_FILE_MODE}
	 * @param choosableFileFilters {@link FileFilter} available
	 * @param selectedFile file preselected when the chooser pops up
	 * @return a file or a directory
	 */
	private static final File chooseFileUsingAWT(Component parentComponent, int mode, FileFilter[] choosableFileFilters, File selectedFile) {
		if (mode == OPEN_DIRECTORY_MODE) {
			System.setProperty("apple.awt.fileDialogForDirectories", "true");
		}
		Frame parentFrame = null;
		if (parentComponent instanceof JComponent) {
			JComponent jComponent =	(JComponent) parentComponent;
			if ((jComponent.getTopLevelAncestor() != null) && (jComponent.getTopLevelAncestor() instanceof Frame)) {
				parentFrame = (Frame) jComponent.getTopLevelAncestor();
			}
		}
		FileDialog fd = new FileDialog(parentFrame);
		String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		fd.setDirectory(defaultDirectory);
		if (selectedFile != null) {
			fd.setFile(selectedFile.getName());
		}
		if (mode == SAVE_FILE_MODE) {
			fd.setMode(FileDialog.SAVE);
		} else {
			fd.setMode(FileDialog.LOAD);
		}
		if (mode == OPEN_DIRECTORY_MODE) {
			// set file filter that only accepts directories
			fd.setFilenameFilter(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return new File(dir,name).isDirectory();
				}
			});
		} else if (choosableFileFilters != null) {
			fd.setFilenameFilter(unionFilter(choosableFileFilters));
		}
		fd.setVisible(true);
		if (mode == OPEN_DIRECTORY_MODE) {
			System.setProperty("apple.awt.fileDialogForDirectories", "false");
		}
		if (fd.getFile() != null) {
			return new File(fd.getDirectory(), fd.getFile());
		} else {
			return null;
		}
	}


	/**
	 * Opens a dialog box asking the user to choose a file or a directory to load or to save using a swing component file chooser
	 * @param parentComponent determines the Frame in which the dialog is displayed; if null, or if the parentComponent has no Frame, a default Frame is used
	 * @param mode one of {@link #OPEN_FILE_MODE}, {@link #OPEN_DIRECTORY_MODE} or {@link #SAVE_FILE_MODE}
	 * @param title title of the open dialog
	 * @param choosableFileFilters {@link FileFilter} available
	 * @param allFiles allow the selection of every kind of file if true, disable the all file selection if false
	 * @param selectedFile file preselected when the chooser pops up
	 * @return a file or a directory
	 */
	private final static File chooseFileUsingSwing(Component parentComponent, int mode, String title, FileFilter[] choosableFileFilters, boolean allFiles, File selectedFile) {
		JFileChooser jfc = new JFileChooser();
		setFileChooserSelectedDirectory(jfc);
		if (mode == OPEN_DIRECTORY_MODE) {
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		} else {
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (choosableFileFilters != null) {
				for (FileFilter currentFilter: choosableFileFilters) {
					jfc.addChoosableFileFilter(currentFilter);
				}
				jfc.setFileFilter(choosableFileFilters[0]);
			}
		}
		jfc.setDialogTitle(title);
		if (allFiles) {
			jfc.addChoosableFileFilter(jfc.getAcceptAllFileFilter());
		}
		int returnVal;
		if (mode == SAVE_FILE_MODE) {
			returnVal = jfc.showSaveDialog(parentComponent);
		} else {
			returnVal = jfc.showOpenDialog(parentComponent);
		}
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if ((mode == SAVE_FILE_MODE) && (choosableFileFilters != null) && (jfc.getFileFilter() instanceof ExtendedFileFilter)) {
				// add the extension of the file if needed
				return Utils.addExtension(selectedFile, ((ExtendedFileFilter) jfc.getFileFilter()).getExtensions());
			} else {
				return jfc.getSelectedFile();
			}
		} else {
			return null;
		}
	}


	/**
	 * Sets the specified {@link JFileChooser} to the default directory
	 * @param jfc
	 */
	private static void setFileChooserSelectedDirectory(JFileChooser jfc) {
		String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		if (Utils.isMacOS()) {
			jfc.setSelectedFile(new File(defaultDirectory));
		} else {
			jfc.setCurrentDirectory(new File(defaultDirectory));
		}
	}


	/**
	 * Creates a {@link FilenameFilter} that accept all the files of the specified file filters
	 * @param filters variable number of {@link FileFilter} objects
	 * @return a new {@link FilenameFilter}
	 */
	private static final FilenameFilter unionFilter(final FileFilter... filters) {
		FilenameFilter nameFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				File file = new File(dir,name);
				if (file.isDirectory()) {
					return false;
				}
				for (FileFilter ff: filters) {
					if (ff.accept(file)) {
						return true;
					}
				}
				return false;
			}
		};
		return nameFilter;
	}
}
