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

import yu.einstein.gdp2.core.manager.ChromosomeManager;

/**
 * This class must be extended by the file extractors
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class Extractor implements Serializable {

	private static final long serialVersionUID = 374481155831573347L;	// generated ID
	protected File 						dataFile = null;	// file containing the data
	protected File 						logFile = null;		// log file
	protected long 						startTime = 0;		// time at the beginning of the extraction
	protected String					name = null;		// name
	protected final ChromosomeManager 	chromosomeManager;	// ChromosomeManager
	

	/**
	 * Constructor
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public Extractor(File dataFile, File logFile) {
		this.dataFile = dataFile;
		this.logFile = logFile;
		this.chromosomeManager = ChromosomeManager.getInstance();
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
}
