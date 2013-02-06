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
package edu.yu.einstein.genplay.core.extractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Random;
import java.util.TreeSet;

import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.gui.event.invalidDataEvent.InvalidDataListener;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;
import edu.yu.einstein.genplay.util.Utils;


/**
 * This class must be extended by the {@link Extractor} for text files
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class TextFileExtractor extends Extractor implements Stoppable {

	private static final long serialVersionUID = 1224425396819320502L;	//generated ID
	private boolean	needToBeStopped = false;			// set to true if the execution of the extractor needs to be stopped
	private Integer	randomLineCount = null;				// number of random lines to extract in the text file. Extract the entire file if null
	protected int 	totalCount = 0;						// total number of line in the file minus the header
	protected int 	lineCount = 0;						// number of line extracted


	/**
	 * Creates an instance of {@link TextFileExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public TextFileExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		String retrievedName = retrieveName();
		if (retrievedName != null) {
			name = retrievedName;
		}
	}


	/**
	 * @return
	 */
	private String retrieveName() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(dataFile));
			boolean isHeader = true;
			boolean isTrackInfo = false;
			String line = null;

			while (((line = reader.readLine()) != null) && isHeader) {
				isHeader = !isDataLine(line);
				isTrackInfo = isTrackInfoLine(line);
				if (isHeader && isTrackInfo) {
					String lineTmp = line.toLowerCase();
					if (lineTmp.contains("name")) {
						int indexStart = lineTmp.indexOf("name") + 4;
						line = line.substring(indexStart);
						line = line.trim();
						if (line.charAt(0) != '=') {
							reader.close();
							return null;
						}
						// remove the '=' from the line
						line = line.substring(1);
						line = line.trim();
						if (line.charAt(0) == '\"') {
							reader.close();
							// remove the first "
							line = line.substring(1);
							return Utils.split(line, '"')[0];
						} else {
							reader.close();
							line = line.trim();
							return Utils.split(line, ' ')[0].trim();
						}
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			try {
				reader.close();
			} catch (IOException e1) {
				ExceptionManager.getInstance().handleException(e);
			}
			return null;
		}
		return null;
	}


	/**
	 * @param line line from the data file
	 * @return true if the line contains actual data. False otherwise
	 */
	private boolean isDataLine(String line) {
		// This first block is the latest isDataLine method implementation (version > 450)
		// That version does not work with SAM files from Eric
		// In order to make it working and in a temporary way, this method return true by default.


		// the following line is an optimization:
		// if the line starts with chr it's a data line so we skip the other tests
		if ((line.length() >= 3) && (line.substring(0, 3).equalsIgnoreCase("chr"))) {
			return true;
		}
		// empty line
		if (line.length() == 0) {
			return false;
		}
		// comment line
		if (line.charAt(0) == '#') {
			return false;
		}
		// track line
		if ((line.length() >= 5) && (line.substring(0, 5).equalsIgnoreCase("track"))) {
			return false;
		}
		// browser line
		if ((line.length() >= 7) && (line.substring(0, 7).equalsIgnoreCase("browser"))) {
			return false;
		}

		return true; // This is a modification compare to the original version
	}


	/**
	 * @param line line from the data file
	 * @return true if the line is a track info line (line starting with 'track'). False otherwise
	 */
	private boolean isTrackInfoLine(String line) {
		if ((line.length() > 5) && (line.substring(0, 5).equalsIgnoreCase("track"))) {
			return true;
		}
		return false;
	}


	/**
	 * Extracts the data from a line.
	 * @param line a line from the data file that is not a header line.
	 * (ie: a line that doesn't start with "#", "browser" or "track")
	 * @return true when the last selected chromosome has been totally extracted (ie returns true when the extraction is done)
	 * @throws DataLineException
	 */
	abstract protected boolean extractLine(String line) throws DataLineException;


	@Override
	public void extract() throws FileNotFoundException, IOException, InterruptedException, DataLineException {
		BufferedReader reader = null;
		try {
			// if the randomLineCount variable is not null we generate a tree set of random line numbers to extract
			TreeSet<Integer> randomLineNumbers = null;
			if (randomLineCount != null) {
				randomLineNumbers = generateRandomLineNumbers(randomLineCount);
			}
			// try to open the input file
			reader = new BufferedReader(new FileReader(dataFile));
			// log the basic information
			logBasicInfo();
			// time when extraction starts
			startTime = System.currentTimeMillis();
			// extract data
			String line = null;
			int currentLineNumber = 1; 		// current line number
			int currentValidLineNumber = 1; // current valid line number
			boolean isExtractionDone = false; // true when the last selected chromosome has been extracted
			// we stop at the end of the file or when the last selected chromosome has been extracted
			while(((line = reader.readLine()) != null) && (!isExtractionDone)){
				// if the extractor needs to be stopped we throw an InterruptedException
				// that stops the execution
				if (needToBeStopped) {
					throw new InterruptedException();
				}
				boolean isDataLine = isDataLine(line);
				// data line
				if (isDataLine) {
					try {
						totalCount++;
						// we extract a line if either way:
						// 1. the whole file needs to be extracted (ie: the randomLineNumbers variable is not set)
						// 2. we extract a random part of the file and the current line was selected as one of the random line to extract
						// (ie the current line number is present in the randomLineNumbers set)
						if ((randomLineNumbers == null) || (randomLineNumbers.contains(currentValidLineNumber))) {
							isExtractionDone = extractLine(line);
						}
						currentValidLineNumber++;
					} catch (DataLineException e) {
						//logMessage("The following line can't be extracted: \"" + line + "\"");
						//e.printStackTrace();
						e.setFile(dataFile);
						e.setLineNumber(currentLineNumber);
						e.setLine(line);
						for (InvalidDataListener listeners: invalidDataListenersList) {
							listeners.handleDataError(e);
						}
					}
				}
				currentLineNumber++;
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
	 * Stops the extraction
	 */
	@Override
	public void stop() {
		needToBeStopped = true;
	}


	@Override
	protected void logExecutionInfo() {
		super.logExecutionInfo();
		if(logFile != null) {
			try {
				NumberFormat nf = NumberFormat.getInstance();
				BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
				writer.write("Number lines in the file: " + totalCount);
				writer.newLine();
				writer.write("Number of lines extracted: " + lineCount);
				writer.newLine();
				writer.write("Percentage of lines extracted: " + nf.format(((double)lineCount / totalCount) * 100) + "%");
				writer.newLine();
				writer.close();
			} catch (IOException e) {
				ExceptionManager.getInstance().handleException(e);
			}
		}
	}


	/**
	 * Counts the number of line in a file
	 * @param filename input file
	 * @return the number of line in the specified file
	 * @throws IOException
	 */
	private int countLines(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		try {
			String line = null;
			int count = 0;
			while ((line = reader.readLine()) != null) {
				if (isDataLine(line)) {
					count++;
				}
			}
			return count;
		} finally {
			reader.close();
		}
	}


	/**
	 * Creates a list of random integers that represents the numbers of the line to extract.
	 * @param randomCount count of lines to extract
	 * @throws IOException
	 */
	private TreeSet<Integer> generateRandomLineNumbers(int randomCount) throws IOException {
		TreeSet<Integer> randomLineNumbers = new TreeSet<Integer>();
		// we compute how many lines there is in the file
		int lineCount = countLines(dataFile);
		// if there is less line in the file than the specified number of line to extract
		// we extract the entire file
		if (lineCount > randomCount) {
			randomLineNumbers = new TreeSet<Integer>();
			Random randomGenerator = new Random();
			while (randomLineNumbers.size() < randomCount) {
				// the add function in a set works only if the element to add is not already present
				randomLineNumbers.add(randomGenerator.nextInt(lineCount) + 1);
			}
		}
		return randomLineNumbers;
	}


	/**
	 * Set the number of random lines to extract in the text file
	 * @param randomLineCount number of random lines to extract in the text file. Extract the entire file if null
	 * @throws UnsupportedOperationException if the extractor doesn't support this operation (eg: Wiggle Extractors)
	 */
	public void setRandomLineCount(Integer randomLineCount) throws UnsupportedOperationException {
		this.randomLineCount = randomLineCount;
	}


	/**
	 * 
	 * @return the number of random lines to extract in the text file. The entire file will be extracted if null
	 */
	public Integer getRandomLineCount() {
		return randomLineCount;
	}


	/**
	 * Convert a string to an integer
	 * @param s	the string
	 * @return	the integer if the string is valid
	 * @throws DataLineException
	 */
	protected Integer getInt (String s) throws DataLineException {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			throw new DataLineException("The information '" + s + "' does not seem to be a valid number.", DataLineException.SKIP_PROCESS);
		}
	}


	/**
	 * Convert a string to an integer
	 * @param s	the string
	 * @param alternative the value to return if the string could not be converted (can be null)
	 * @return	the integer if the string is valid, the alternative otherwise
	 */
	protected Integer getInt (String s, Integer alternative) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			return alternative;
		}
	}


	/**
	 * Convert a string to a double
	 * @param s	the string
	 * @return	the double if the string is valid
	 * @throws DataLineException
	 */
	protected Double getDouble (String s) throws DataLineException {
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			throw new DataLineException("The information '" + s + "' does not seem to be a valid number.", DataLineException.SKIP_PROCESS);
		}
	}


	/**
	 * Convert a string to a double
	 * @param s	the string
	 * @param alternative the value to return if the string could not be converted  (can be null)
	 * @return	the double if the string is valid, the alternative otherwise
	 */
	protected Double getDouble (String s, Double alternative) {
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			return alternative;
		}
	}
}
