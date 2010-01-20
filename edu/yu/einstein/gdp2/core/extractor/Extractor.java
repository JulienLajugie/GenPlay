/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.extractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import yu.einstein.gdp2.exception.InvalidDataLineException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;
import yu.einstein.gdp2.util.ChromosomeManager;

/**
 * This class must be extended by the data file extractors
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class Extractor {

	private File 						dataFile = null;	// file containing the data
	private File 						logFile = null;		// log file
	private long 						startTime = 0;		// time at the beginning of the extraction
	private String						name = null;		// name
	protected final ChromosomeManager 	chromosomeManager;	// ChromosomeManager
	protected int 						lineCount = 0;		// number of line extracted


	/**
	 * Constructor
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 * @param chromosomeManager a {@link ChromosomeManager}
	 */
	public Extractor(File dataFile, File logFile, ChromosomeManager chromosomeManager) {
		this.dataFile = dataFile;
		this.logFile = logFile;
		this.chromosomeManager = chromosomeManager;
	}


	/**
	 * Extracts the data from a line. 
	 * @param line a data line of the data file.
	 * A data line is a line that doesn't start with "#", "browser" or "track"
	 * @throws ManagerDataNotLoadedException
	 * @throws InvalidDataLineException
	 */
	abstract protected void extractLine(String line) throws ManagerDataNotLoadedException, InvalidDataLineException;


	/**
	 * Extracts the data from a file.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ManagerDataNotLoadedException
	 */
	public void extract() throws FileNotFoundException, IOException, ManagerDataNotLoadedException {
		BufferedReader reader = null;
		try {
			// try to open the input file
			reader = new BufferedReader(new FileReader(dataFile));
			// log the basic information
			logBasicInfo();
			// time when extraction starts
			startTime = System.currentTimeMillis();
			// extract data
			String line = null;
			while((line = reader.readLine()) != null) {
				boolean isDataLine = true;
				// case when the line is empty
				if (line.length() == 0) {
					isDataLine = false;
				}
				// comment line
				if (line.charAt(0) == '#') {
					isDataLine = false;
				} 
				// browser line
				if ((line.length() > 7) && (line.substring(0, 7).equalsIgnoreCase("browser"))) {
					isDataLine = false;
				}
				// track line
				if ((line.length() > 5) && (line.substring(0, 5).equalsIgnoreCase("track"))) {
					isDataLine = false;
					String[] splitedLine = line.split(" ");
					for (String currentOption: splitedLine) {					
						if ((currentOption.trim().length() > 4) && (currentOption.trim().substring(0, 4).equalsIgnoreCase("name"))) {
							name = currentOption.substring(5).trim();
						}
					}
				}
				// data line
				if (isDataLine) {
					try {
						extractLine(line);
					} catch (InvalidDataLineException e) {
						//logMessage("The following line can't be extracted: \"" + line + "\"");
						//e.printStackTrace();
					}
				}
			}
			reader.close();
			logExecutionInfo();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}


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
	private void logBasicInfo() {
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
	private void logExecutionInfo() {
		if(logFile != null) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
				long timeEnd = (System.currentTimeMillis() - startTime) / 1000l;
				writer.write("Extraction done. Time elapsed: " + timeEnd + " seconds");
				writer.newLine();
				writer.write("Number of lines extracted: " + lineCount);
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
