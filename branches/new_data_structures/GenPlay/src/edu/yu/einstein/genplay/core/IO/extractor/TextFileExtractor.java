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
package edu.yu.einstein.genplay.core.IO.extractor;

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

import edu.yu.einstein.genplay.core.IO.utils.TrackLineHeader;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.gui.event.invalidDataEvent.InvalidDataListener;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;


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
				} else if (TrackLineHeader.isTrackLine(line)) {

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
	 * Extracts the data from a line.
	 * @param line a line from the data file that is not a header line.
	 * (ie: a line that doesn't start with "#", "browser" or "track")
	 * @return true when the last selected chromosome has been totally extracted (ie returns true when the extraction is done)
	 * @throws DataLineException
	 */
	abstract protected boolean extractLine(String line) throws DataLineException;


	/**
	 * This method doesn't do anything but can be extended by the different text extractor.
	 * This method is called when the current extracted line is a "track" header.
	 * @param line
	 */
	public void extractTrackLineHeader(String line) {}


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
	 * 
	 * @return the number of random lines to extract in the text file. The entire file will be extracted if null
	 */
	public Integer getRandomLineCount() {
		return randomLineCount;
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
				ExceptionManager.getInstance().caughtException(e);
			}
		}
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
	 * Stops the extraction
	 */
	@Override
	public void stop() {
		needToBeStopped = true;
	}



}
