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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.TreeSet;

import edu.yu.einstein.genplay.exception.InvalidDataLineException;
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


	private String retrieveName() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(dataFile));
			boolean isHeader = true;
			boolean isTrackInfo = false;
			String line = null;

			while (((line = reader.readLine()) != null) && isHeader) {
				isHeader = false;
				if (line.length() == 0) {
					isHeader = true;
				}
				// comment line
				if (line.charAt(0) == '#') {
					isHeader = true;
				} 
				// browser line
				if ((line.length() > 7) && (line.substring(0, 7).equalsIgnoreCase("browser"))) {
					isHeader = true;
				}
				// track line
				if ((line.length() > 5) && (line.substring(0, 5).equalsIgnoreCase("track"))) {
					isHeader = true;
					isTrackInfo = true;
				}
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
							return line.split("\"")[0];							
						} else {
							reader.close();
							line = line.trim();
							return line.split(" ")[0].trim();
						}
					}
				}				
			}
			reader.close();
		} catch (Exception e) {
			return null;
		}
		return null;
	}


	/**
	 * Extracts the data from a line. 
	 * @param line a line from the data file that is not a header line. 
	 * (ie: a line that doesn't start with "#", "browser" or "track")
	 * @return true when the last selected chromosome has been totally extracted (ie returns true when the extraction is done)
	 * @throws InvalidDataLineException
	 */
	abstract protected boolean extractLine(String line) throws InvalidDataLineException;


	@Override
	public void extract() throws FileNotFoundException, IOException, InterruptedException {
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
			int currentLineNumber = 1; // current line number 
			boolean isExtractionDone = false; // true when the last selected chromosome has been extracted
			// we stop at the end of the file or when the last selected chromosome has been extracted
			while(((line = reader.readLine()) != null) && (!isExtractionDone)){
				// if the extractor needs to be stopped we throw an InterruptedException
				// that stops the execution
				if (needToBeStopped) {
					throw new InterruptedException();
				}
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
						// we extract a line if either way:
						// 1. the whole file needs to be extracted (ie: the randomLineNumbers variable is not set)
						// 2. we extract a random part of the file and the current line was selected as one of the random line to extract 
						// (ie the current line number is present in the randomLineNumbers set) 
						if ((randomLineNumbers == null) || (randomLineNumbers.contains(currentLineNumber))) {
							isExtractionDone = extractLine(line);
						}
						currentLineNumber++;
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


	/**
	 * Counts the number of line in a file.  About 6 times faster than readLines.
	 * @param filename input file
	 * @return the number of line in the specified file
	 * @throws IOException
	 */
	private int countLines(File file) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(file));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			while ((readChars = is.read(c)) != -1) {
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n')
						++count;
				}
			}
			return count;
		} finally {
			is.close();
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
	 */
	public void setRandomLineCount(Integer randomLineCount) {
		this.randomLineCount = randomLineCount;
	}


	/**
	 * 
	 * @return the number of random lines to extract in the text file. The entire file will be extracted if null
	 */
	public Integer getRandomLineCount() {
		return randomLineCount;
	}




}
