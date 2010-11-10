/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.util;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.FilterType;
import yu.einstein.gdp2.core.enums.IslandResultType;
import yu.einstein.gdp2.core.enums.LogBase;
import yu.einstein.gdp2.core.enums.SaturationType;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.enums.ScoreCalculationTwoTrackMethod;
import yu.einstein.gdp2.gui.fileFilter.BedFilter;
import yu.einstein.gdp2.gui.fileFilter.BedGraphFilter;
import yu.einstein.gdp2.gui.fileFilter.BedGraphWith0Filter;
import yu.einstein.gdp2.gui.fileFilter.ElandExtendedFilter;
import yu.einstein.gdp2.gui.fileFilter.ExtendedFileFilter;
import yu.einstein.gdp2.gui.fileFilter.GFFFilter;
import yu.einstein.gdp2.gui.fileFilter.GTFFilter;
import yu.einstein.gdp2.gui.fileFilter.GdpGeneFilter;
import yu.einstein.gdp2.gui.fileFilter.PSLFilter;
import yu.einstein.gdp2.gui.fileFilter.PairFilter;
import yu.einstein.gdp2.gui.fileFilter.SAMFilter;
import yu.einstein.gdp2.gui.fileFilter.SOAPsnpFilter;
import yu.einstein.gdp2.gui.fileFilter.SerializedBinListFilter;
import yu.einstein.gdp2.gui.fileFilter.TwoBitFilter;
import yu.einstein.gdp2.gui.fileFilter.WiggleFilter;


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
	 * Returns a color associated to a score. 
	 * High intensities are red. Medium are green. Low are blue.
	 * @param score A score indexed between min and max.
	 * @param min minimum intensity value
	 * @param max maximum intensity value
	 * @return A color
	 */
	public static Color scoreToColor(double score, double min, double max) {
		// set the score to min if the score is smaller than min
		score = Math.max(min, score);
		// set the score to max if the score is greater than max
		score = Math.min(max, score);
		double distance = max - min;
		double newScore = score - min;
		double distanceQuarter = distance / 4;
		int r = 0;
		int v = 0;
		int b = 0;

		if ((newScore >= 0) && (newScore <= distanceQuarter)) {
			r = 0;
			v = (int)(newScore * 255 / distanceQuarter);
			b = 255;			
		} else if ((newScore > distanceQuarter) && (newScore <= 2 * distanceQuarter)) {
			r = 0;
			v = 255;
			b = (int)(255 - (newScore - distanceQuarter) * 255 / distanceQuarter);			
		} else if ((newScore > 2 * distanceQuarter) && (newScore <= 3 * distanceQuarter)) {
			r = (int)((newScore - 2 * distanceQuarter) * 255 / distanceQuarter);
			v = 255;
			b = 0;			
		} else if ((newScore > 3 * distanceQuarter) && (newScore <= distance)) {
			r = 255;
			v = (int)(255 - (newScore - 3 * distanceQuarter) * 255 / distanceQuarter);
			b = 0;			
		}		
		return new Color(r, v, b);
	}
}
