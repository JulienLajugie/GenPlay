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
package edu.yu.einstein.genplay.util;

import java.awt.Component;
import java.awt.FontMetrics;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.core.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.core.chromosomeWindow.SimpleScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.comparator.ChromosomeComparator;
import edu.yu.einstein.genplay.core.enums.DataPrecision;
import edu.yu.einstein.genplay.core.enums.FilterType;
import edu.yu.einstein.genplay.core.enums.IslandResultType;
import edu.yu.einstein.genplay.core.enums.LogBase;
import edu.yu.einstein.genplay.core.enums.SaturationType;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationTwoLayersMethod;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;
import edu.yu.einstein.genplay.gui.dialog.chromosomeChooser.ChromosomeChooserDialog;
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
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;



/**
 * Collection of static methods used in this project
 * @author Julien Lajugie
 * @version 0.1
 */
public final class Utils {


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
	 * @param chromoList array of boolean.
	 * @return true if all the booleans are set to true or if the array is null. False otherwise
	 */
	public final static boolean allChromosomeSelected(boolean[] chromoList) {
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
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		List<Chromosome> chromosomeList = Utils.getSortedChromosomeList(projectChromosome.getChromosomeList());
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
	 * Opens a dialog box asking the user to choose a file to load
	 * @param parentComponent determines the Frame in which the dialog is displayed; if null, or if the parentComponent has no Frame, a default Frame is used
	 * @param title title of the open dialog
	 * @param defaultDirectory default directory
	 * @param choosableFileFilters {@link FileFilter} available
	 * @param allFiles allow the selection of every kind of file if true, disable the all file selection if false
	 * @return a file to load
	 */
	public final static File chooseFileToLoad(Component parentComponent, String title, String defaultDirectory, FileFilter[] choosableFileFilters, boolean allFiles) {
		JFileChooser jfc = new JFileChooser(defaultDirectory);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setDialogTitle(title);
		for (FileFilter currentFilter: choosableFileFilters) {
			jfc.addChoosableFileFilter(currentFilter);
		}
		if (allFiles) {
			jfc.setFileFilter(jfc.getAcceptAllFileFilter());
		}
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
	public final static FilterType chooseFilterType(Component parentComponent) {
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
	public final static IslandResultType chooseIslandResultType(Component parentComponent) {
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
	 * A dialog box used to choose a {@link DataPrecision}
	 * @param parentComponent the parent Component for the dialog
	 * @return a {@link DataPrecision}
	 */
	public final static DataPrecision choosePrecision(Component parentComponent) {
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
	public final static DataPrecision choosePrecision(Component parentComponent, DataPrecision defaultValue) {
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
	public final static SaturationType chooseSaturationType(Component parentComponent) {
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
	public final static ScoreCalculationMethod chooseScoreCalculation(Component parentComponent) {
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
	public final static ScoreCalculationTwoLayersMethod chooseScoreCalculationTwoLayersMethod(Component parentComponent) {
		return (ScoreCalculationTwoLayersMethod)JOptionPane.showInputDialog(
				parentComponent,
				"Choose a method for the calculation of the score",
				"Score Calculation",
				JOptionPane.QUESTION_MESSAGE,
				null,
				ScoreCalculationTwoLayersMethod.values(),
				ScoreCalculationTwoLayersMethod.ADDITION);
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
	 * @return the {@link ExtendedFileFilter} associated to the files that can be loaded as BinList
	 */
	public final static ExtendedFileFilter[] getReadableBinListFileFilters() {
		ExtendedFileFilter[] filters = {new BedGraphFilter(), new BedFilter(), new GFFFilter(), new GTFFilter(), new WiggleFilter(), new PairFilter(), new ElandExtendedFilter(), new PSLFilter(), new SAMFilter(), new SerializedBinListFilter()};
		return filters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be loaded as GeneList
	 */
	public final static ExtendedFileFilter[] getReadableGeneFileFilters() {
		ExtendedFileFilter[] filters = {new BedFilter(), new GdpGeneFilter(), new GTFFilter(), new PSLFilter()};
		return filters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be loaded as Repeats
	 */
	public final static ExtendedFileFilter[] getReadableRepeatFileFilters() {
		ExtendedFileFilter[] filters = {new BedFilter(), new GFFFilter(), new GTFFilter(), new PSLFilter()};
		return filters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be loaded as SCWList
	 */
	public final static ExtendedFileFilter[] getReadableSCWFileFilters() {
		ExtendedFileFilter[] filters = {new BedGraphFilter(), new BedFilter(), new GFFFilter(), new GTFFilter(), new WiggleFilter(), new PSLFilter()};
		return filters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be loaded as sequence track (aka nucleotide list)
	 */
	public final static FileFilter[] getReadableSequenceFileFilters() {
		ExtendedFileFilter[] filters = {new TwoBitFilter()};
		return filters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be loaded as SNPList
	 */
	public final static FileFilter[] getReadableSNPFileFilters() {
		ExtendedFileFilter[] filters = {new SOAPsnpFilter()};
		return filters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be loaded as stripes
	 */
	public final static ExtendedFileFilter[] getReadableStripeFileFilters() {
		ExtendedFileFilter[] filters = {new BedGraphFilter(), new BedFilter(), new GFFFilter(), new GTFFilter(), new WiggleFilter(), new PSLFilter()};
		return filters;
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
	 * @return the {@link ExtendedFileFilter} associated to the files that can be saved as BinList
	 */
	public final static ExtendedFileFilter[] getWritableBinListFileFilters() {
		ExtendedFileFilter[] filters = {new BedGraphFilter(), new BedGraphWith0Filter(), new BedFilter(), new GFFFilter(), new WiggleFilter(), new SerializedBinListFilter()};
		return filters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be saved as GeneList
	 */
	public final static ExtendedFileFilter[] getWritableGeneFileFilters() {
		ExtendedFileFilter[] filters = {new BedFilter(), new GdpGeneFilter()};
		return filters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the files that can be saved as SCWList
	 */
	public final static ExtendedFileFilter[] getWritableSCWFileFilter() {
		ExtendedFileFilter[] filters = {new BedGraphFilter(), new BedFilter(), new GFFFilter()};
		return filters;
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
	 * This methods parse a line and returns an array of strings containing
	 * all the fields from the input line that are separated either by one or many
	 * continuous spaces or tabs except if this tabs or spaces are from inside double quotes.
	 * @param line input line to parse
	 * @return an array of strings containing the fields of the input line
	 */
	public final static String[] parseLineTabAndSpace(String line) {
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
	public final static String[] parseLineTabOnly(String line) {
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


	/**
	 * Returns a sublist of the input list. The first variant contains or
	 * starts after the specified start position.
	 * The last variant contains or stops before the specified stop position.
	 * @param list input list
	 * @param positionStart
	 * @param positionStop
	 * @return a sublist of the input list
	 */
	public final static ArrayList<Variant> searchVariantInterval(List<Variant> list, int positionStart, int positionStop) {
		if ((list == null) || (list.size() == 0)) {
			return null;
		}

		ArrayList<Variant> resultList = new ArrayList<Variant>();

		// Gets the start and stop indexes of the list
		int indexStart = findVariantPosition(list, positionStart, 0, list.size() - 1);
		int indexStop = findVariantPosition(list, positionStop, 0, list.size() - 1);

		// Extract the windows from the start (included) to the stop (excluded)
		for (int i = indexStart; i < indexStop; i++) {
			resultList.add(list.get(i));
		}

		// The stop position may have been returned even if the window is not included between the start and stop position
		// It is necessary to test it before adding it
		Variant element = list.get(indexStop);
		if (isInVariant(element, positionStart, positionStop)) {
			resultList.add(element);
		}

		return resultList;
	}


	/**
	 * Returns the index of the variant where the value is found OR the index right after if not found.
	 * The scan is based on the start and stop position of the variant.
	 * Recursive function.
	 * 
	 * @param list			list to scan
	 * @param value			value to find
	 * @param indexStart	index of the list to start the scan
	 * @param indexStop		index of the list to stop the scan
	 * @return				the index where the value of the variant is found or the index right after if the exact value is not found
	 */
	private static int findVariantPosition(List<Variant> list, int value, int indexStart, int indexStop) {
		if (indexStart == indexStop) {
			return indexStart;
		} else {
			int middle = (indexStop - indexStart) / 2;
			int compare = containsVariantPosition(list.get(indexStart + middle), value);

			if (compare == 0) {
				return indexStart + middle;
			} else if (compare < 0) {
				return findVariantPosition(list, value, indexStart, indexStart + middle);
			} else {
				return findVariantPosition(list, value, indexStart + middle + 1, indexStop);
			}
		}
	}


	/**
	 * Checks if the variant contains the given position.
	 * If the position is located before the window, -1 is returned.
	 * If the position is located after the window, 1 is returned.
	 * if the position is included in the window, 0 is returned.
	 * @param variant the variant
	 * @param position the position to check
	 * @return 0 is the position is in the variant, -1 if lower, 1 if higher.
	 */
	public final static int containsVariantPosition (Variant variant, int position) {
		if (position < variant.getStart()) {
			return -1;
		} else if (position > variant.getStop()) {
			return 1;
		}
		return 0;
	}


	/**
	 * @param element		the element to test
	 * @param positionStart	the start position on the main frame
	 * @param positionStop	the stop position on the main frame
	 * @return true if the element is in the main frame, false otherwise
	 */
	public final static boolean isInVariant (Variant element, int positionStart, int positionStop) {
		if (element.getStop() < positionStart) {
			return false;
		}

		if (element.getStart() > positionStop) {
			return false;
		}

		return true;
	}


	/**
	 * Returns a sublist of the input list. The first window contains or
	 * starts after the specified start position.
	 * The last window contains or stops before the specified stop position.
	 * @param <T> type of the window list (ie: {@link SimpleScoredChromosomeWindow},
	 * {@link SimpleChromosomeWindow} ...) must be or extends {@link SimpleChromosomeWindow}
	 * @param list input list
	 * @param positionStart
	 * @param positionStop
	 * @return a sublist of the input list
	 */
	public final static <T extends ChromosomeWindow> List<T> searchChromosomeWindowInterval(List<T> list, int positionStart, int positionStop) {
		if ((list == null) || (list.size() == 0)) {
			return null;
		}

		ArrayList<T> resultList = new ArrayList<T>();

		// Gets the start and stop indexes of the list
		int indexStart = findChromosomePosition(list, positionStart, 0, list.size() - 1);
		int indexStop = findChromosomePosition(list, positionStop, 0, list.size() - 1);

		// Extract the windows from the start (included) to the stop (excluded)
		for (int i = indexStart; i < indexStop; i++) {
			resultList.add(list.get(i));
		}

		// The stop position may have been returned even if the window is not included between the start and stop position
		// It is necessary to test it before adding it
		T element = list.get(indexStop);
		if (isInWindow(element, positionStart, positionStop)) {
			resultList.add(element);
		}

		return resultList;
	}



	/**
	 * 
	 * @param <T> type of the window list (ie: {@link SimpleScoredChromosomeWindow},
	 * {@link SimpleChromosomeWindow} ...) must be or extends {@link SimpleChromosomeWindow}
	 * @param element		the element to test
	 * @param windowStart	the start position on the main frame
	 * @param windowStop	the stop position on the main frame
	 * @return true if the element is in the main frame, false otherwise
	 */
	public final static <T extends ChromosomeWindow> boolean isInWindow (T element, int windowStart, int windowStop) {
		if (element.getStop() < windowStart) {
			return false;
		}

		if (element.getStart() > windowStop) {
			return false;
		}

		return true;
	}


	/**
	 * Returns the index of the window where the value is found OR the index right after if not found.
	 * The scan is based on the start and stop position of the windows (containsPosition method of {@link SimpleChromosomeWindow} is used).
	 * Recursive function.
	 * 
	 * @param <T> 			type of the element of the input list
	 * @param list			list to scan
	 * @param value			value to find
	 * @param indexStart	index of the list to start the scan
	 * @param indexStop		index of the list to stop the scan
	 * @return				the index where the value of the window is found or the index right after if the exact value is not found
	 */
	private static <T extends ChromosomeWindow> int findChromosomePosition(List<T> list, int value, int indexStart, int indexStop) {
		if (indexStart == indexStop) {
			return indexStart;
		} else {
			int middle = (indexStop - indexStart) / 2;
			int compare = list.get(indexStart + middle).containsPosition(value);

			if (compare == 0) {
				return indexStart + middle;
			} else if (compare < 0) {
				return findChromosomePosition(list, value, indexStart, indexStart + middle);
			} else {
				return findChromosomePosition(list, value, indexStart + middle + 1, indexStop);
			}
		}
	}


	/**
	 * Returns the index of the window where the value is found OR the index right after if not found.
	 * The scan is based on the start position of the windows.
	 * Recursive function.
	 * 
	 * @param <T> 			type of the element of the input list
	 * @param list			list to scan
	 * @param value			value to find
	 * @param indexStart	index of the list to start the scan
	 * @param indexStop		index of the list to stop the scan
	 * @return 				the index where the start value of the window is found or the index right after if the exact value is not found
	 */
	public final static <T extends ChromosomeWindow> int findStart(List<T> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getStart()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getStart()) {
			return findStart(list, value, indexStart + middle + 1, indexStop);
		} else {
			return findStart(list, value, indexStart, indexStart + middle);
		}
	}


	/**
	 * Returns the index of the window where the value is found OR the index right after if not found.
	 * The scan is based on the stop position of the windows.
	 * Recursive function.
	 * 
	 * @param <T> 			type of the element of the input list
	 * @param list			list to scan
	 * @param value			value to find
	 * @param indexStart	index of the list to start the scan
	 * @param indexStop		index of the list to stop the scan
	 * @return 				the index where the stop value of the window is found or the index right before if the exact value is not found
	 */
	public final static <T extends ChromosomeWindow> int findStop(List<T> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getStop()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getStop()) {
			return findStop(list, value, indexStart + middle + 1, indexStop);
		} else {
			return findStop(list, value, indexStart, indexStart + middle);
		}
	}


	/**
	 * Sorts a list of chromosome and returned it
	 * @param list	a list of chromosome indexed by their name
	 * @return		a list of chromosome sorted according to the names
	 */
	public final static List<Chromosome> getSortedChromosomeList(List<Chromosome> list) {

		Map<String, Chromosome> chromosomeMap = new HashMap<String, Chromosome>();
		List<Chromosome> chromosomeList = new ArrayList<Chromosome>();
		List<String> chromosomeNames = new ArrayList<String>();

		for (Chromosome chromosome: list) {
			chromosomeNames.add(chromosome.getName().toLowerCase());
			chromosomeMap.put(chromosome.getName().toLowerCase(), chromosome);
		}

		Collections.sort(chromosomeNames, new ChromosomeComparator());

		for (String chromosomeName: chromosomeNames) {
			chromosomeList.add(chromosomeMap.get(chromosomeName));
		}

		return chromosomeList;
	}


	/**
	 * Shows the chromosome details of a list of chromosomes
	 * @param chromosomeList the list of chromosomes
	 */
	public final static void showChromosomeList (List<Chromosome> chromosomeList) {
		String output = "---------- showChromosomeList\n";
		for (Chromosome chromosome: chromosomeList) {
			output += chromosome.getName() + " : " + chromosome.getLength() + "\n";
		}
		output += "----------";
		System.out.println(output);
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
	 * Tries to force the garbage collector to run
	 */
	public final static void garbageCollect() {
		System.gc();/*System.gc();System.gc();System.gc();
		System.gc();System.gc();System.gc();System.gc();
		System.gc();System.gc();System.gc();System.gc();
		System.gc();System.gc();System.gc();System.gc();
		System.gc();System.gc();System.gc();System.gc();*/
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
	 * Split a string using the tabulation character.
	 * @param s	the string to split
	 * @return	an array containing the split string
	 */
	public final static String[] splitWithTab (String s) {
		return split(s, '	');
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
	 * @param objects	the array of objects
	 * @param metrics	the metrics
	 * @return the length of the longest object (as a string), 0 otherwise
	 */
	public final static int getMaximumLength (Object[] objects, FontMetrics metrics) {
		int result = 0;

		if ((objects != null) && (objects.length > 0)) {
			for (Object object: objects) {
				result = Math.max(result, metrics.stringWidth(object.toString()));
			}
		}

		return result;
	}
}
