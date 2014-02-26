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
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.enums.LogBase;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.gui.clipboard.LocalClipboard;
import edu.yu.einstein.genplay.gui.dialog.chromosomeChooser.ChromosomeChooserDialog;
import edu.yu.einstein.genplay.gui.fileFilter.AllTrackFileFilter;
import edu.yu.einstein.genplay.gui.fileFilter.BAMFilter;
import edu.yu.einstein.genplay.gui.fileFilter.BedFilter;
import edu.yu.einstein.genplay.gui.fileFilter.BedGraphFilter;
import edu.yu.einstein.genplay.gui.fileFilter.BedGraphWith0Filter;
import edu.yu.einstein.genplay.gui.fileFilter.ElandExtendedFilter;
import edu.yu.einstein.genplay.gui.fileFilter.ExtendedFileFilter;
import edu.yu.einstein.genplay.gui.fileFilter.GFFFilter;
import edu.yu.einstein.genplay.gui.fileFilter.GTFFilter;
import edu.yu.einstein.genplay.gui.fileFilter.GenPlayTrackFilter;
import edu.yu.einstein.genplay.gui.fileFilter.PSLFilter;
import edu.yu.einstein.genplay.gui.fileFilter.PairFilter;
import edu.yu.einstein.genplay.gui.fileFilter.SAMFilter;
import edu.yu.einstein.genplay.gui.fileFilter.TwoBitFilter;
import edu.yu.einstein.genplay.gui.fileFilter.WiggleFilter;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;


/**
 * Collection of static methods used in this project
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public final class Utils {

	/**
	 * The increment unit for all {@link JScrollPane}
	 */
	public final static int SCROLL_INCREMENT_UNIT = 20;


	/**
	 * Checks if the specified {@link File} name ends with one of the specified extensions.
	 * If not adds the first specified extension to the file name.
	 * @param file a file
	 * @param extensions file extensions
	 * @return a File with the specified extension
	 */
	public final static File addExtension(File file, String... extensions) {
		String currentExtension = getExtension(file);
		boolean specifedExtensionsFound = false;
		// if there is no extension specified we return the input file
		if (extensions == null) {
			return file;
		}
		// if the current file has an extension we check if it's one of the specified extension
		if (currentExtension != null) {
			int i = 0;
			while ((i < extensions.length) && !specifedExtensionsFound) {
				if (currentExtension.equalsIgnoreCase(extensions[i])) {
					specifedExtensionsFound = true;
				}
				i++;
			}
		}
		// if we didn't find one of the specified extensions we return
		// a new file having the name of the input file concatenated
		// with the first specified extension
		if (!specifedExtensionsFound) {
			String newFile = file.getPath() + "." + extensions[0];
			return new File(newFile);
		} else {
			return file;
		}
	}


	/**
	 * Asks if the user wants to replace a file if this file already exists.
	 * @param parentComponent determines the Frame in which the dialog is displayed; if null, or if the parentComponent has no Frame, a default Frame is used
	 * @param f A file.
	 * @return True if the user wants to cancel. False otherwise.
	 */
	public final static boolean cancelBecauseFileExist(Component parentComponent, File f) {
		if (f.exists()) {
			int res = JOptionPane.showConfirmDialog(parentComponent, "The file " + f.getName() + " already exists. Do you want to replace the existing file?", "File already exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
			if (res == JOptionPane.NO_OPTION) {
				return true;
			}
		}
		f.delete();
		return false;
	}


	/**
	 * Opens a dialog box asking the user to choose some chromosomes among all the chromosomes available for the project
	 * @param parentComponent determines the Frame in which the dialog is displayed; if null, or if the parentComponent has no Frame, a default Frame is used
	 * @return an array of boolean with a length equal to the number of chromosome in the project.
	 * The elements of the array are set to true if selected or false otherwise.
	 */
	public final static boolean[] chooseChromosomes(Component parentComponent) {
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		List<Chromosome> chromosomeList = projectChromosomes.getChromosomeList();
		Collections.sort(chromosomeList);
		ChromosomeChooserDialog chromoChooser = new ChromosomeChooserDialog();
		chromoChooser.setFullChromosomeList(chromosomeList);
		chromoChooser.setSelectedChromosomeList(chromosomeList);
		chromoChooser.setOrdering(false);
		if (chromoChooser.showDialog(parentComponent) == ChromosomeChooserDialog.APPROVE_OPTION) {
			if (chromoChooser.getSelectedChromosomeList() != null) {
				boolean[] selectedChromo = new boolean[chromosomeList.size()];
				for (int i = 0; i < chromosomeList.size(); i++) {
					selectedChromo[i] = chromoChooser.getSelectedChromosomeList().contains(chromosomeList.get(i));
				}
				return selectedChromo;
			}
		}
		return null;
	}


	/**
	 * A dialog box used to choose a {@link GeneScoreType}
	 * @param parentComponent the parent Component for the dialog
	 * @return a {@link GeneScoreType}
	 */
	public final static GeneScoreType chooseGeneScoreCalculation(Component parentComponent) {
		return (GeneScoreType) JOptionPane.showInputDialog(
				parentComponent,
				"Choose a method for the calculation of the score of the genes",
				"Score Calculation",
				JOptionPane.QUESTION_MESSAGE,
				null,
				GeneScoreType.values(),
				GeneScoreType.RPKM);
	}


	/**
	 * A dialog box used to choose a {@link LogBase}
	 * @param parentComponent the parent Component for the dialog
	 * @return a {@link LogBase} value
	 */
	public final static LogBase chooseLogBase(Component parentComponent) {
		return (LogBase) JOptionPane.showInputDialog(
				parentComponent,
				"Choose a base for the logarithm",
				"Logarithm Base",
				JOptionPane.QUESTION_MESSAGE,
				null,
				LogBase.values(),
				LogBase.BASE_2);
	}


	/**
	 * A dialog box used to choose a {@link ScoreOperation}
	 * @param parentComponent the parent Component for the dialog
	 * @return a {@link ScoreOperation}
	 */
	public final static ScoreOperation chooseScoreCalculation(Component parentComponent) {
		return (ScoreOperation)JOptionPane.showInputDialog(
				parentComponent,
				"Choose a method for the calculation of the score",
				"Score Calculation",
				JOptionPane.QUESTION_MESSAGE,
				null,
				ScoreOperation.values(),
				ScoreOperation.AVERAGE);
	}


	/**
	 * Tries to force the garbage collector to run
	 */
	public final static void garbageCollect() {
		System.gc();
	}


	/**
	 * @return the system clipboard if the application has the permission to access it.
	 * A local clipboard otherwise.
	 */
	public static Clipboard getClipboard() {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			try {
				sm.checkSystemClipboardAccess();
			} catch(SecurityException e) {
				return LocalClipboard.getInstance();
			}
		}
		return Toolkit.getDefaultToolkit().getSystemClipboard();
	}


	/**
	 * If the user is using Windows, the configuration directory is the temporary one (eg: C:\Users\USER\AppData\Local\Temp)
	 * If the user is using POSIX like platform, the configuration directory is $HOME/.genplay
	 * @return the configuration directory
	 */
	public static String getConfigurationDirectoryPath() {
		if (isWindowsOS()) {
			// windows system
			return System.getenv("APPDATA") + "\\GenPlay\\";
		} else if (isMacOS()){
			// mac
			return System.getProperty("user.home") + "/Library/Application Support/GenPlay/";
		} else {
			// linux / unix
			return System.getProperty("user.home") + "/.config/GenPlay/";
		}
	}


	/**
	 * @param file a {@link File}
	 * @return the extension of the file (without the dot), null if none.
	 */
	public final static String getExtension(File file) {
		String fileName = file.getName();
		if (fileName == null) {
			return null;
		}
		int dotIndex =  fileName.lastIndexOf('.');
		if ((dotIndex > 0) && (dotIndex < (fileName.length() - 1))) {
			return fileName.substring(dotIndex + 1).toLowerCase().trim();
		} else {
			return null;
		}
	}


	/**
	 * @param path
	 * @return the name of file without its path
	 */
	public static String getFileName(String path) {
		int lastBackSlashIndex = path.lastIndexOf('\\');
		int latSlashIndex = path.lastIndexOf('/');
		String[] splitPath;
		if (lastBackSlashIndex > latSlashIndex) {
			splitPath = path.split("\\\\");
		} else {
			splitPath = path.split("/");
		}
		return splitPath[splitPath.length - 1];
	}


	/**
	 * @param file a {@link File}
	 * @return the name of a file without its extension
	 */
	public final static String getFileNameWithoutExtension(File file) {
		String fileName = file.getName();
		int index = fileName.lastIndexOf('.');
		if ((index > 0) && (index <= (file.getName().length() - 2))) {
			return fileName.substring(0, index);
		} else {
			return fileName;
		}
	}


	/**
	 * This method return the index of the first int found in a string, starting from the specified index position
	 * @param s			the string
	 * @param index		the index to start
	 * @return			the index of the first int found in the string after the specified start, -1 if not found
	 */
	public final static int getFirstIntegerOffset (String s, int index) {
		for (int i = 0; i < s.length(); i++) {
			int c = s.charAt(i);
			if ((c >= 48) && (c <= 57)) {
				return i;
			}
		}
		return -1;
	}


	/**
	 * This method looks for the full integer part in a string from a start index.
	 * @param s		the string
	 * @param index	index of the first integer
	 * @return		the full integer starting at the index
	 */
	public final static Integer getFullIntegerPart (String s, int index) {
		Integer result = null;									// Initialize the result to null
		int nextIndex = index + 1;								// Next index is initialized with index + 1
		while (nextIndex <= s.length()) {						// while the next index is shorter or equal to the string length
			String text = s.substring(index, nextIndex);		// gets the sub string from the string (index to next index)
			try {
				result = Integer.parseInt(text);				// tries to get the integer part
			} catch (Exception e) {								// if there is no integer part
				return result;									// we return result (that contains the previous integer part or null)
			}
			nextIndex++;										// if it worked, we keep looking in the string increasing the next index
		}
		return result;											// return the result of the scan
	}


	/**
	 * @return the java version number, -1 if not found
	 */
	public static int getJavaVersion () {
		String version = System.getProperty("java.specification.version", "");
		int versionNumber = -1;
		if (version.length() > 2) {
			versionNumber = Integer.parseInt("" + version.charAt(2));
		}
		return versionNumber;
	}


	/**
	 * @param layers a list of {@link Layer}
	 * @param filter an array of {@link LayerType} as filters
	 * @return a list of {@link Layer} containing only the {@link LayerType} defined in the filters.
	 */
	public static List<Layer<?>> getLayers(List<Layer<?>> layers, LayerType[] filter) {
		List<Layer<?>> result = new ArrayList<Layer<?>>();
		for (Layer<?> layer: layers) {
			boolean found = false;
			int index = 0;
			while (!found & (index < filter.length)) {
				if (layer.getType() == filter[index]) {
					found = true;
				}
				index++;
			}
			if (found) {
				result.add(layer);
			}
		}
		return result;
	}


	/**
	 * @param tracks a list of {@link Track}
	 * @param layerTypes a list of {@link LayerType}
	 * @return all the layer from the list of tracks that are in the specified list of the {@link LayerType}. All layers if the layer type list is null
	 */
	public final static Layer<?>[] getLayers(Track[] tracks, LayerType[] layerTypes) {
		List<Layer<?>> layerList = new ArrayList<Layer<?>>();
		for (Track currentTrack: tracks) {
			for (Layer<?> currentLayer: currentTrack.getLayers()) {
				if ((layerTypes == null) || currentLayer.getType().isContainedIn(layerTypes)) {
					layerList.add(currentLayer);
				}
			}
		}
		Layer<?>[] returnLayers = new Layer<?>[layerList.size()];
		return layerList.toArray(returnLayers);
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be loaded as GeneList
	 */
	public final static FileFilter[] getReadableLayerFileFilters() {
		ExtendedFileFilter[] filters = {
				new AllTrackFileFilter(),
				new GenPlayTrackFilter(),
				new BAMFilter(),
				new SAMFilter(),
				new BedFilter(),
				new BedGraphFilter(),
				new WiggleFilter(),
				new GTFFilter(),
				new GFFFilter(),
				new PSLFilter(),
				new TwoBitFilter(),
				new ElandExtendedFilter()
		};
		return filters;
	}


	/**
	 * @return the file filters for the sequence files
	 */
	public static FileFilter[] getReadableSequenceFileFilters() {
		FileFilter[] filefilters = {new TwoBitFilter()};
		return filefilters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be sorted by the application
	 */
	public final static FileFilter[] getSortableFileFilters() {
		ExtendedFileFilter[] filters = {new BedGraphFilter(), new BedFilter(), new SAMFilter(), new GFFFilter(), new PairFilter(), new PSLFilter()};
		return filters;
	}


	/**
	 * @return the location of the temporary directory
	 */
	public static String getTmpDirectoryPath() {
		return System.getProperty("java.io.tmpdir");
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be saved as BinList
	 */
	public final static FileFilter[] getWritableBinListFileFilters() {
		ExtendedFileFilter[] filters = {new BedGraphFilter(), new BedFilter(), new GFFFilter(), new WiggleFilter()};
		return filters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be saved as GeneList
	 */
	public final static FileFilter[] getWritableGeneFileFilters() {
		ExtendedFileFilter[] filters = {new BedFilter()};
		return filters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be saved as SCWList
	 */
	public final static FileFilter[] getWritableSCWFileFilter() {
		ExtendedFileFilter[] filters = {new BedGraphFilter(), new BedGraphWith0Filter(), new BedFilter(), new GFFFilter()};
		return filters;
	}


	/**
	 * @return true if the software is a mac installation (ie: the plateform is mac and there is an content directory).
	 */
	public static boolean isMacInstall() {
		File genPlayApp = new File("GenPlay.app");
		return isMacOS() && genPlayApp.exists();
	}


	/**
	 * @return true if the computer running GenPlay is a mac, false otherwise
	 */
	public static boolean isMacOS() {
		return System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0;
	}


	/**
	 * @return true if the software is a windows installation (ie: the plateform is windows and there is an exe file).
	 */
	public static boolean isWindowsInstall() {
		File genPlayExe = new File("GenPlay.exe");
		return isWindowsOS() && genPlayExe.exists();
	}


	/**
	 * @return true if the OS running the program is Windows, false otherwise
	 */
	public static boolean isWindowsOS() {
		return System.getProperty("os.name").toLowerCase().indexOf("windows") != -1;
	}


	/**
	 * Returns the logarithm of a double value. The logarithm is computed in the specified base
	 * @param logBase
	 * @param value value to l
	 * @return a logarithm value
	 */
	public final static double log(LogBase logBase, double value) {
		if (logBase == LogBase.BASE_E) {
			// the Math.log function return the natural log (no needs to change the base)
			return Math.log(value);
		} else {
			// change of base: logb(x) = logk(x) / logk(b)
			return Math.log(value) / Math.log(logBase.getValue());
		}
	}


	/**
	 * This methods reverse an array of int
	 * @param b the int array
	 * @return	the reversed array
	 */
	public final static int[] reverse(int[] b) {
		int left  = 0;          // index of leftmost element
		int right = b.length-1; // index of rightmost element

		while (left < right) {
			// exchange the left and right elements
			int temp = b[left];
			b[left]  = b[right];
			b[right] = temp;
			// move the bounds toward the center
			left++;
			right--;
		}
		return b;
	}


	/**
	 * Split a string using the char code of a character.
	 * @param s	the string to split
	 * @param c	the integer code of the character
	 * @return	an array containing the split string (or empty if the string is null)
	 */
	public final static String[] split (String s, int c) {
		List<String> list = new ArrayList<String>();
		if (s != null) {
			int pos = 0, end;
			while ((end = s.indexOf(c, pos)) >= 0) {
				String sub = s.substring(pos, end);
				list.add(sub);
				pos = end + 1;
			}
			list.add(s.substring(pos));
		}
		if (list.isEmpty()) {
			return null;
		}
		int size = list.size();
		if (list.get(size - 1).isEmpty()) {
			size--;
		}

		String[] result = new String[size];
		for (int i = 0; i < size; i++) {
			result[i] = list.get(i);
		}

		return result;
	}


	/**
	 * This methods will split a String in different lines according to a maximum length.
	 * It does split in middle of a word but at a whitespace if necessary
	 * @param s			the string
	 * @param length	the length
	 * @param fm		the font metrics
	 * @return			the formatted line
	 */
	public final static String[] splitStringWithLength (String s, int length, FontMetrics fm) {
		List<String> result = new ArrayList<String>();
		String[] array = split(s, ' ');
		String current = array[0];
		int currentLength = fm.stringWidth(current);

		for (int i = 1; i < array.length; i++) {
			String tmp = array[i];
			currentLength += fm.stringWidth(" " + tmp);
			if (currentLength < length) {
				current += " " + tmp;
			} else {
				result.add(current);
				current = tmp;
				currentLength = fm.stringWidth(tmp);
			}
		}
		result.add(current);


		String[] arrayResult = new String[result.size()];
		for (int i = 0; i < arrayResult.length; i++) {
			arrayResult[i] = result.get(i);
		}

		return arrayResult;
	}


	/**
	 * Split a string using the tabulation character.
	 * @param s	the string to split
	 * @return	an array containing the split string
	 */
	public final static String[] splitWithTab (String s) {
		return split(s, '	');
	}
}
