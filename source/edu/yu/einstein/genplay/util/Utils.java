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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.util;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.core.enums.DataPrecision;
import edu.yu.einstein.genplay.core.enums.FilterType;
import edu.yu.einstein.genplay.core.enums.IslandResultType;
import edu.yu.einstein.genplay.core.enums.LogBase;
import edu.yu.einstein.genplay.core.enums.SaturationType;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationTwoTrackMethod;
import edu.yu.einstein.genplay.gui.fileFilter.BedFilter;
import edu.yu.einstein.genplay.gui.fileFilter.BedGraphFilter;
import edu.yu.einstein.genplay.gui.fileFilter.BedGraphWith0Filter;
import edu.yu.einstein.genplay.gui.fileFilter.ElandExtendedFilter;
import edu.yu.einstein.genplay.gui.fileFilter.ExtendedFileFilter;
import edu.yu.einstein.genplay.gui.fileFilter.GFFFilter;
import edu.yu.einstein.genplay.gui.fileFilter.GTFFilter;
import edu.yu.einstein.genplay.gui.fileFilter.GdpGeneFilter;
import edu.yu.einstein.genplay.gui.fileFilter.PSLFilter;
import edu.yu.einstein.genplay.gui.fileFilter.PairFilter;
import edu.yu.einstein.genplay.gui.fileFilter.SAMFilter;
import edu.yu.einstein.genplay.gui.fileFilter.SOAPsnpFilter;
import edu.yu.einstein.genplay.gui.fileFilter.SerializedBinListFilter;
import edu.yu.einstein.genplay.gui.fileFilter.TwoBitFilter;
import edu.yu.einstein.genplay.gui.fileFilter.WiggleFilter;



/**
 * Collection of static methods used in this project
 * @author Julien Lajugie
 * @version 0.1
 */
public class Utils {


	/**
	 * Checks if the specified {@link File} name ends with the specified extension.
	 * If not adds the extension to the file name. 
	 * @param file a file 
	 * @param extension a file extension
	 * @return a File with the specified extension
	 */
	public static File addExtension(File file, String extension) {
		String currentExtension = getExtension(file);
		if ((currentExtension == null) || (!currentExtension.equalsIgnoreCase(extension))) {
			String newFile = file.getPath() + "." + extension;
			return new File(newFile);
		} else {
			return file;
		}
	}


	/**
	 * @param chromoList array of boolean. 
	 * @return true if all the booleans are set to true or if the array is null. False otherwise 
	 */
	public static boolean allChromosomeSelected(boolean[] chromoList) {
		if (chromoList == null) {
			return true;
		} 
		for (boolean isSelected: chromoList) {
			if (!isSelected) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Asks if the user wants to replace a file if this file already exists.
	 * @param parentComponent determines the Frame in which the dialog is displayed; if null, or if the parentComponent has no Frame, a default Frame is used
	 * @param f A file.
	 * @return True if the user wants to cancel. False otherwise.
	 */
	final public static boolean cancelBecauseFileExist(Component parentComponent, File f) {
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
	 * Opens a dialog box asking the user to choose a file to load
	 * @param parentComponent determines the Frame in which the dialog is displayed; if null, or if the parentComponent has no Frame, a default Frame is used
	 * @param title title of the open dialog
	 * @param defaultDirectory default directory
	 * @return a file to load
	 */
	final public static File chooseFileToLoad(Component parentComponent, String title, String defaultDirectory, FileFilter[] choosableFileFilters) {
		JFileChooser jfc = new JFileChooser(defaultDirectory);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setDialogTitle(title);
		for (FileFilter currentFilter: choosableFileFilters) {
			jfc.addChoosableFileFilter(currentFilter);
		}
		jfc.setFileFilter(jfc.getAcceptAllFileFilter());
		int returnVal = jfc.showOpenDialog(parentComponent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			if (!selectedFile.canRead()) {
				JOptionPane.showMessageDialog(parentComponent, 
						"You don't have the permission to read the selected file.", "File Incorrect", JOptionPane.ERROR_MESSAGE, null);
				return null;
			} else {				
				return jfc.getSelectedFile();
			}
		} else {
			return null;
		}
	}


	/**
	 * A dialog box used to choose a {@link FilterType}
	 * @param parentComponent the parent Component for the dialog 
	 * @return a {@link FilterType} value
	 */
	public static FilterType chooseFilterType(Component parentComponent) {
		return (FilterType)JOptionPane.showInputDialog(
				parentComponent,
				"Choose a type of filter",
				"Filter Type",
				JOptionPane.QUESTION_MESSAGE,
				null,
				FilterType.values(),
				FilterType.PERCENTAGE);
	}


	/**
	 * A dialog box used to choose a {@link IslandResultType}
	 * @param parentComponent the parent Component for the dialog 
	 * @return a {@link FilterType} value
	 */
	public static IslandResultType chooseIslandResultType(Component parentComponent) {
		return (IslandResultType)JOptionPane.showInputDialog(
				parentComponent,
				"Choose a type of result",
				"Island result Type",
				JOptionPane.QUESTION_MESSAGE,
				null,
				IslandResultType.values(),
				IslandResultType.FILTERED);
	}


	/**
	 * A dialog box used to choose a {@link LogBase}
	 * @param parentComponent the parent Component for the dialog
	 * @return a {@link LogBase} value
	 */
	public static LogBase chooseLogBase(Component parentComponent) {
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
	 * A dialog box used to choose a {@link DataPrecision}
	 * @param parentComponent the parent Component for the dialog 
	 * @return a {@link DataPrecision}
	 */
	public static DataPrecision choosePrecision(Component parentComponent) {
		return (DataPrecision)JOptionPane.showInputDialog(
				parentComponent,
				"Choose a precision for the data of the fixed window list",
				"Select Data Precision",
				JOptionPane.QUESTION_MESSAGE,
				null,
				DataPrecision.values(),
				DataPrecision.PRECISION_32BIT);
	}	


	/**
	 * A dialog box used to choose a {@link DataPrecision}
	 * @param parentComponent the parent Component for the dialog 
	 * @param defaultValue default value in the input box
	 * @return a {@link DataPrecision}
	 */
	public static DataPrecision choosePrecision(Component parentComponent, DataPrecision defaultValue) {
		return (DataPrecision)JOptionPane.showInputDialog(
				parentComponent,
				"Choose a precision for the data of the fixed window list",
				"Select Data Precision",
				JOptionPane.QUESTION_MESSAGE,
				null,
				DataPrecision.values(),
				defaultValue);
	}


	/**
	 * A dialog box used to choose a {@link SaturationType}
	 * @param parentComponent the parent Component for the dialog 
	 * @return a {@link SaturationType}
	 */
	public static SaturationType chooseSaturationType(Component parentComponent) {
		return (SaturationType)JOptionPane.showInputDialog(
				parentComponent,
				"Choose a type of saturation",
				"Saturation Type",
				JOptionPane.QUESTION_MESSAGE,
				null,
				SaturationType.values(),
				SaturationType.PERCENTAGE);
	}


	/**
	 * A dialog box used to choose a {@link ScoreCalculationMethod}
	 * @param parentComponent the parent Component for the dialog 
	 * @return a {@link ScoreCalculationMethod}
	 */
	public static ScoreCalculationMethod chooseScoreCalculation(Component parentComponent) {
		return (ScoreCalculationMethod)JOptionPane.showInputDialog(
				parentComponent,
				"Choose a method for the calculation of the score",
				"Score Calculation",
				JOptionPane.QUESTION_MESSAGE,
				null,
				ScoreCalculationMethod.values(),
				ScoreCalculationMethod.AVERAGE);
	}


	/**
	 * A dialog box used to choose a {@link ScoreCalculationMethod}
	 * @param parentComponent the parent Component for the dialog 
	 * @return a {@link ScoreCalculationMethod}
	 */
	public static ScoreCalculationTwoTrackMethod chooseScoreCalculationTwoTrackMethod(Component parentComponent) {
		return (ScoreCalculationTwoTrackMethod)JOptionPane.showInputDialog(
				parentComponent,
				"Choose a method for the calculation of the score",
				"Score Calculation",
				JOptionPane.QUESTION_MESSAGE,
				null,
				ScoreCalculationTwoTrackMethod.values(),
				ScoreCalculationTwoTrackMethod.ADDITION);
	}


	/**
	 * @param file a {@link File}
	 * @return the extension of the file. null if none
	 */
	final public static String getExtension(File file) {
		String fileName = file.getName();
		if (fileName == null) {
			return null;
		}
		int dotIndex =  fileName.lastIndexOf('.');
		if ((dotIndex > 0) && (dotIndex < fileName.length() - 1)) {
			return fileName.substring(dotIndex + 1).toLowerCase().trim();
		} else {
			return null;
		}		
	}


	/**
	 * @param file a {@link File}
	 * @return the name of a file without its extension
	 */
	public static String getFileNameWithoutExtension(File file) {
		String fileName = file.getName();
		int index = fileName.lastIndexOf('.');		
		if ((index > 0) && (index <= file.getName().length() - 2)) {
			return fileName.substring(0, index);
		} else {
			return fileName;
		}
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be loaded as BinList
	 */
	public static ExtendedFileFilter[] getReadableBinListFileFilters() {
		ExtendedFileFilter[] filters = {new BedGraphFilter(), new BedFilter(), new GFFFilter(), new GTFFilter(), new WiggleFilter(), new PairFilter(), new ElandExtendedFilter(), new PSLFilter(), new SAMFilter(), new SerializedBinListFilter()};
		return filters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be loaded as GeneList
	 */
	public static ExtendedFileFilter[] getReadableGeneFileFilters() {
		ExtendedFileFilter[] filters = {new BedFilter(), new GdpGeneFilter(), new GTFFilter(), new PSLFilter()};
		return filters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be loaded as Repeats
	 */
	public static ExtendedFileFilter[] getReadableRepeatFileFilters() {
		ExtendedFileFilter[] filters = {new BedFilter(), new GFFFilter(), new GTFFilter(), new PSLFilter()};
		return filters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be loaded as SCWList
	 */
	public static ExtendedFileFilter[] getReadableSCWFileFilters() {
		ExtendedFileFilter[] filters = {new BedGraphFilter(), new BedFilter(), new GFFFilter(), new GTFFilter(), new WiggleFilter(), new PSLFilter()};
		return filters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be loaded as SNPList
	 */
	public static FileFilter[] getReadableSNPFileFilters() {
		ExtendedFileFilter[] filters = {new SOAPsnpFilter()};
		return filters;
	}	


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be loaded as sequence track (aka nucleotide list)
	 */
	public static FileFilter[] getReadableSequenceFileFilters() {
		ExtendedFileFilter[] filters = {new TwoBitFilter()};
		return filters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be loaded as stripes
	 */
	public static ExtendedFileFilter[] getReadableStripeFileFilters() {
		ExtendedFileFilter[] filters = {new BedGraphFilter(), new BedFilter(), new GFFFilter(), new GTFFilter(), new WiggleFilter(), new PSLFilter()};
		return filters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be saved as BinList
	 */
	public static ExtendedFileFilter[] getWritableBinListFileFilters() {
		ExtendedFileFilter[] filters = {new BedGraphFilter(), new BedGraphWith0Filter(), new BedFilter(), new GFFFilter(), new WiggleFilter(), new SerializedBinListFilter()};
		return filters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be saved as GeneList
	 */
	public static ExtendedFileFilter[] getWritableGeneFileFilters() {
		ExtendedFileFilter[] filters = {new BedFilter(), new GdpGeneFilter()};
		return filters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be saved as SCWList
	 */
	public static ExtendedFileFilter[] getWritableSCWFileFilter() {
		ExtendedFileFilter[] filters = {new BedGraphFilter(), new BedFilter(), new GFFFilter()};
		return filters;
	}


	/**
	 * Returns the logarithm of a double value. The logarithm is computed in the specified base 
	 * @param logBase
	 * @param value value to l
	 * @return a logarithm value
	 */
	public static double log(LogBase logBase, double value) {
		if (logBase == LogBase.BASE_E) {
			// the Math.log function return the natural log (no needs to change the base)
			return Math.log(value);
		} else {
			// change of base: logb(x) = logk(x) / logk(b)
			return Math.log(value) / Math.log(logBase.getValue());									
		}
	}


	/**
	 * This methods parse a line and returns an array of strings containing
	 * all the fields from the input line that are separated either by one or many 
	 * continuous spaces or tabs except if this tabs or spaces are from inside double quotes.
	 * @param line input line to parse
	 * @return an array of strings containing the fields of the input line
	 */
	public static String[] parseLineTabAndSpace(String line) {
		List<String> parsedLine = new ArrayList<String>();
		int i = 0;
		while (i < line.length()) {
			// skip all the space and tabs
			while ((i < line.length()) && 
					((line.charAt(i) == ' ') || (line.charAt(i) == '\t'))) {
				i++;
			}
			if (i < line.length()) {
				// if the spaces and tabs weren't at the end of the line
				int indexStart = i; // retrieve the start index
				boolean isInsideQuotes = false; // when we start we're not inside double quotes
				while ((i < line.length()) && 
						(isInsideQuotes || ((line.charAt(i) != ' ') && (line.charAt(i) != '\t')))) {
					// loop until we meet a new space or tab that is not between double quotes
					if (line.charAt(i) == '"') { // check if we enter or leave double quotes
						isInsideQuotes = !isInsideQuotes;
					}
					i++;
				}
				// add the field to the result list
				parsedLine.add(line.substring(indexStart, i));
			}
		}
		
		if (parsedLine.isEmpty()) { // if our list is empty we return null
			return null;
		} else { // if there is element in our list we transform it in an array and return it
			String[] returnArray = new String[parsedLine.size()];
			return parsedLine.toArray(returnArray);
		}
	}
	
	/**
	 * This methods parse a line and returns an array of strings containing
	 * all the fields from the input line that are separated by one or many 
	 * continuous tabs except if this tabs are from inside double quotes.
	 * @param line input line to parse
	 * @return an array of strings containing the fields of the input line
	 */
	public static String[] parseLineTabOnly(String line) {
		List<String> parsedLine = new ArrayList<String>();
		int i = 0;
		while (i < line.length()) {
			// skip all the tabs
			while ((i < line.length()) && 
					(line.charAt(i) == '\t')) {
				i++;
			}
			if (i < line.length()) {
				// if the tabs weren't at the end of the line
				int indexStart = i; // retrieve the start index
				boolean isInsideQuotes = false; // when we start we're not inside double quotes
				while ((i < line.length()) && 
						(isInsideQuotes || (line.charAt(i) != '\t'))) {
					// loop until we meet a new tab that is not between double quotes
					if (line.charAt(i) == '"') { // check if we enter or leave double quotes
						isInsideQuotes = !isInsideQuotes;
					}
					i++;
				}
				// add the field to the result list
				parsedLine.add(line.substring(indexStart, i));
			}
		}
		
		if (parsedLine.isEmpty()) { // if our list is empty we return null
			return null;
		} else { // if there is element in our list we transform it in an array and return it
			String[] returnArray = new String[parsedLine.size()];
			return parsedLine.toArray(returnArray);
		}

	}
}
