/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.util;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.FilterType;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.gui.fileFilter.BedFilter;
import yu.einstein.gdp2.gui.fileFilter.BedGraphFilter;
import yu.einstein.gdp2.gui.fileFilter.BedGraphWith0Filter;
import yu.einstein.gdp2.gui.fileFilter.ElandExtendedFilter;
import yu.einstein.gdp2.gui.fileFilter.ExtendedFileFilter;
import yu.einstein.gdp2.gui.fileFilter.GFFFilter;
import yu.einstein.gdp2.gui.fileFilter.GdpGeneFilter;
import yu.einstein.gdp2.gui.fileFilter.PairFilter;
import yu.einstein.gdp2.gui.fileFilter.SerializedBinListFilter;
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
				FilterType.HIGH_PASS_FILTER);
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
	 * @return the {@link ExtendedFileFilter} associated to the BinList files
	 */
	public static ExtendedFileFilter[] getBinListFileFilters() {
		ExtendedFileFilter[] stripeFileFilters = {new BedFilter(), new BedGraphFilter(), new BedGraphWith0Filter(), new GFFFilter(), new WiggleFilter(), new PairFilter(), new ElandExtendedFilter(), new SerializedBinListFilter()};
		return stripeFileFilters;
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
	 * @return the {@link ExtendedFileFilter} associated to the gene files
	 */
	public static ExtendedFileFilter[] getGeneFileFilters() {
		ExtendedFileFilter[] stripeFileFilters = {new BedFilter(), new GdpGeneFilter() };
		return stripeFileFilters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the repeats files
	 */
	public static ExtendedFileFilter[] getRepeatFileFilters() {
		ExtendedFileFilter[] stripeFileFilters = {new BedFilter(), new GFFFilter()};
		return stripeFileFilters;
	}


	/**
	 * @return the {@link ExtendedFileFilter} associated to the scored chromosome window files
	 */
	public static ExtendedFileFilter[] getSCWFileFilters() {
		ExtendedFileFilter[] stripeFileFilters = {new BedFilter(), new BedGraphFilter(), new GFFFilter(), new WiggleFilter()};
		return stripeFileFilters;
	}
	
	
	/**
	 * @return the {@link ExtendedFileFilter} associated to the stripe files
	 */
	public static ExtendedFileFilter[] getStripeFileFilters() {
		ExtendedFileFilter[] stripeFileFilters = {new BedFilter(), new BedGraphFilter(), new GFFFilter(), new WiggleFilter()};
		return stripeFileFilters;
	}
}
