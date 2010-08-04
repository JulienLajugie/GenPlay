/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.extractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.util.Utils;

/**
 * This class must be extended by the file extractors
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class Extractor implements Serializable {

	private static final long serialVersionUID = 374481155831573347L;	// generated ID
	protected static final int NEED_TO_BE_EXTRACTED = 0;		// the chromsome needs to be extracted
	protected static final int NEED_TO_BE_SKIPPED = 1;			// the chromosome needs to be skipped
	protected static final int AFTER_LAST_SELECTED = 2;			// the chromosome is after the last selected chromosome 
	protected File 						dataFile = null;		// file containing the data
	protected File 						logFile = null;			// log file
	protected long 						startTime = 0;			// time at the beginning of the extraction
	protected String					name = null;			// name
	protected final ChromosomeManager 	chromosomeManager;		// ChromosomeManager
	private boolean[] 					selectedChromo = null;	// array of booleans. The indexes set to true correspond to the index of the selected chromosomes in the ChromosomeManager
	private int		 					lastSelectedChromoIndex;// index of the last chromosome to extract


	/**
	 * Constructor
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public Extractor(File dataFile, File logFile) {
		this.dataFile = dataFile;
		this.logFile = logFile;
		this.chromosomeManager = ChromosomeManager.getInstance();
		this.name = Utils.getFileNameWithoutExtension(dataFile);
	}


	/**
	 * @param chromosome a chromosome
	 * @return if the specified chromosome needs to be extracted, needs to be skipped or is after the last selected chromosome
	 */
	protected int checkChromosomeStatus(Chromosome chromosome) {
		if (selectedChromo == null) {
			return NEED_TO_BE_EXTRACTED;
		} else {
			int index = chromosomeManager.getIndex(chromosome);
			if (index > lastSelectedChromoIndex) {
				return AFTER_LAST_SELECTED;
			} else {
				if (selectedChromo[index]) {
					return NEED_TO_BE_EXTRACTED;
				} else {
					return NEED_TO_BE_SKIPPED;
				}
			}
		}
	}


	/**
	 * Extracts the data from a file.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ManagerDataNotLoadedException
	 */
	public abstract void extract() throws Exception;


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * Writes basic information about the extraction 
	 * in the log file if the log file is specified
	 */
	protected void logBasicInfo() {
		if(logFile != null) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				writer.write("-------------------------------------------------------------------");
				writer.newLine();
				writer.write("Extraction - " + dateFormat.format(date));
				writer.newLine();
				writer.write("File: " + dataFile.getAbsolutePath());
				writer.newLine();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * Writes information about the execution of the extraction 
	 * in the log file if the log file is specified
	 */
	protected void logExecutionInfo() {
		if(logFile != null) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
				long timeEnd = (System.currentTimeMillis() - startTime) / 1000l;
				writer.write("Extraction done. Time elapsed: " + timeEnd + " seconds");
				writer.newLine();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * Writes the specified message in the log file
	 * @param message message to write
	 */
	protected void logMessage(String message) {
		if(logFile != null) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
				writer.write(message);
				writer.newLine();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * Sets the chromosomes selected for the extraction
	 * @param selectedChromo array of booleans. The indexes set to true correspond to the index of the selected chromosomes in the {@link ChromosomeManager}	
	 */
	public void setSelectedChromosomes(boolean[] selectedChromosomes) {
		this.selectedChromo = selectedChromosomes;
		// look for the index of the last selected chromosome
		if (selectedChromo == null) {
			lastSelectedChromoIndex = chromosomeManager.size() -1;
		} else {
			int lastIndex = 0;
			for (int i = 0; i < chromosomeManager.size(); i++) {
				if (selectedChromo[i]) {
					lastIndex = i;
				}
			}
			lastSelectedChromoIndex = lastIndex;
		}
	}
}
