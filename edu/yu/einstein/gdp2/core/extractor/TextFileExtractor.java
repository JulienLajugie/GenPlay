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
import java.text.DecimalFormat;

import yu.einstein.gdp2.exception.InvalidDataLineException;

/**
 * This class must be extended by the {@link Extractor} for text files
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class TextFileExtractor extends Extractor {

	private static final long serialVersionUID = 1224425396819320502L;	//generated ID
	protected int 						totalCount = 0;		// total number of line in the file minus the header
	protected int 						lineCount = 0;		// number of line extracted

	/**
	 * Creates an instance of {@link TextFileExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public TextFileExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
	}


	/**
	 * Extracts the data from a line. 
	 * @param line a line from the data file that is not a header line. 
	 * (ie: a line that doesn't start with "#", "browser" or "track")
	 * @throws InvalidDataLineException
	 */
	abstract protected void extractLine(String line) throws InvalidDataLineException;


	@Override
	public void extract() throws FileNotFoundException, IOException {
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
				// the following line is an optimization:
				// if the line starts with chr it's a data line so we skip the other tests
				if ((line.length() <= 2) || (!line.substring(0, 3).equalsIgnoreCase("chr"))) {
					// case when the line is empty
					if (line.length() == 0) {
						isDataLine = false;
					} else if (line.charAt(0) == '#') {
						// comment line
						isDataLine = false;
					} else if ((line.length() > 7) && (line.substring(0, 7).equalsIgnoreCase("browser"))) {
						// browser line
						isDataLine = false;
					} else if ((line.length() > 5) && (line.substring(0, 5).equalsIgnoreCase("track"))) {
						// track line
						isDataLine = false;
						String[] splitedLine = line.split(" ");
						for (String currentOption: splitedLine) {					
							if ((currentOption.trim().length() > 4) && (currentOption.trim().substring(0, 4).equalsIgnoreCase("name"))) {
								name = currentOption.substring(5).trim();
							}
						}
					}
				}
				// data line
				if (isDataLine) {
					try {
						totalCount++;
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


	@Override
	protected void logExecutionInfo() {
		super.logExecutionInfo();
		if(logFile != null) {
			try {
				DecimalFormat df = new DecimalFormat("##.#");
				BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
				writer.write("Number lines in the file: " + totalCount);
				writer.newLine();
				writer.write("Number of lines extracted: " + lineCount);
				writer.newLine();
				writer.write("Percentage of lines extracted: " + df.format((double)lineCount / totalCount * 100) + "%");
				writer.newLine();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
