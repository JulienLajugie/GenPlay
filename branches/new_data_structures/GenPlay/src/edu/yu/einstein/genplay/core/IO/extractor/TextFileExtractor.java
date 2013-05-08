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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;

import edu.yu.einstein.genplay.core.IO.dataReader.DataReader;
import edu.yu.einstein.genplay.core.IO.utils.Extractors;
import edu.yu.einstein.genplay.core.IO.utils.TrackLineHeader;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;


/**
 * This class must be extended by the {@link Extractor} for text files
 * @author Julien Lajugie
 */
public abstract class TextFileExtractor extends Extractor implements Stoppable, DataReader {

	/** Size of the buffer of the reader */
	private final static int BUFFER_LENGTH = 8192;

	/** Return code when the extraction of a file is done */
	protected final static int EXTRACTION_DONE = 0;

	/** Return code when a line was skipped */
	protected final static int LINE_SKIPPED = 1;

	/** Return code when a line was extracted but not an item */
	protected final static int LINE_EXTRACTED = 2;

	/** Return code when an item was extracted */
	protected final static int ITEM_EXTRACTED = 3;

	private final BufferedReader 			reader;						// buffered reader to read the data
	private boolean							isInitialized = false;		// true when the file has been initialized and is ready to be extracted
	private int 							lineExtracted;				// number of line extracted
	private int								lineSkipped;				// number of line skipped
	private int 							currentLineNumber;			// current line number
	private Integer							randomLineCount = null;		// number of random lines to extract in the text file. Extract the entire file if null
	private TreeSet<Integer> 				randomLineNumbers;			// TreeSet containing the numbers of the lines to extract (the line numbers are randomly generated)
	private TrackLineHeader 				trackLineHeader;			// header of the track extracted from the track line


	/**
	 * 
	 * @param dataFile
	 * @throws FileNotFoundException
	 */
	public TextFileExtractor(File dataFile) throws FileNotFoundException {
		super(dataFile);
		reader = new BufferedReader(new FileReader(dataFile), BUFFER_LENGTH);
		lineExtracted = 0;
		lineSkipped = 0;
	}


	/**
	 * Method defining how to extract the data
	 * @param currentLine a data line
	 * @return
	 * <ul>
	 * <li> {@link #EXTRACTION_DONE} if the extraction is finished (the line was not extracted)
	 * <li> {@link #LINE_SKIPPED} if the line was skipped but the extraction is not done
	 * <li> {@link #LINE_EXTRACTED} if the line was extracted but no items were extracted (multi-line items)
	 * <li> {@link #ITEM_EXTRACTED} if the line was extracted and an item was extracted
	 * </ul>
	 */
	protected abstract int extractDataLine(String currentLine) throws DataLineException;


	/**
	 * Method defining how to extract the header
	 * @param currentLine a header line
	 */
	protected void extractHeaderLine(String currentLine) {
		trackLineHeader = new TrackLineHeader();
		trackLineHeader.parseTrackLine(currentLine);
	}

	/**
	 * Finalized the extraction:
	 * <ul>
	 * <li> Closes the reader
	 * <li> Retrieves the duration of the extraction
	 * </ul>
	 */
	@Override
	protected final void finalizeExtraction() {
		super.finalizeExtraction();
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				ExceptionManager.getInstance().caughtException(e);
			}
		}
	}


	/**
	 * @return the number of the current line being extracted
	 */
	public int getCurrentLineNumber() {
		return currentLineNumber;
	}


	/**
	 * @return the number of line extracted
	 */
	public int getLineExtracted() {
		return lineExtracted;
	}


	/**
	 * @return the number of line skipped
	 */
	public int getLineSkipped() {
		return lineSkipped;
	}


	/**
	 * 
	 * @return the number of random lines to extract in the text file. The entire file will be extracted if null
	 */
	public Integer getRandomLineCount() {
		return randomLineCount;
	}


	/**
	 * @return a {@link TrackLineHeader} object containing the parameters extracted from the "Track" line
	 */
	public TrackLineHeader getTrackLineHeader() {
		return trackLineHeader;
	}


	/**
	 * Extract the header of the a data text file
	 * @throws IOException
	 */
	@Override
	protected final void initializeExtraction() throws IOException {
		super.initializeExtraction();
		isInitialized = true;
		readHeader();
	}


	/**
	 * Reads the header and call the {@link #extractHeader(String)} method for each header line
	 * @throws IOException
	 */
	private void readHeader() throws IOException {
		String currentLine = null;
		reader.mark(BUFFER_LENGTH);
		// loop for the header
		while (!isStopped()
				&& ((currentLine = reader.readLine()) != null)
				&& Extractors.isHeaderLine(currentLine)) {
			currentLineNumber++;
			reader.mark(BUFFER_LENGTH);
			extractHeaderLine(currentLine);
		}
		// roll back to the begining of the read if
		if (((currentLine = reader.readLine()) != null)
				&& !Extractors.isHeaderLine(currentLine)) {
			reader.reset();
		}
	}


	@Override
	public boolean readItem() throws IOException {
		// case where the extraction was stopped
		if (isStopped()) {
			finalizeExtraction();
			return false;
		}

		// case where we need to initialize the extractor
		if (!isInitialized) {
			initializeExtraction();
		}

		String currentLine = null;
		int extractionStatus = LINE_SKIPPED;
		while (((currentLine = reader.readLine()) != null) && (extractionStatus != EXTRACTION_DONE)) {
			currentLineNumber++;
			currentLine = currentLine.trim();
			if (!currentLine.isEmpty()) {
				// we extract a line if either way:
				// 1. the whole file needs to be extracted (ie: the randomLineNumbers variable is not set)
				// 2. we extract a random part of the file and the current line was selected as one of the random line to extract
				// (ie the current line number is present in the randomLineNumbers set)
				if ((randomLineNumbers == null) || (randomLineNumbers.contains(currentLineNumber))) {
					try {
						extractionStatus = extractDataLine(currentLine);
					} catch (DataLineException e) {
						notifyDataEventListeners(e, currentLineNumber, currentLine);
						lineSkipped++;
					}
					switch (extractionStatus) {
					case LINE_EXTRACTED:
						lineExtracted++;
						break;
					case LINE_SKIPPED:
						lineSkipped++;
						break;
					case ITEM_EXTRACTED:
						lineExtracted++;
						itemExtractedCount++;
						return true;
					}
				}
			}
		}
		// case where the extraction is done
		finalizeExtraction();
		return false;
	}


	/**
	 * @return the name of the data.  The name of the data is
	 */
	@Override
	protected String retrieveDataName(File dataFile) {
		String dataName = Extractors.retrieveDataName(dataFile);
		if (dataName == null) {
			dataName = dataFile.getName();
		}
		return dataName;
	}


	/**
	 * Set the number of random lines to extract in the text file
	 * @param randomLineCount number of random lines to extract in the text file. Extract the entire file if null
	 * @throws UnsupportedOperationException if the extractor doesn't support this operation (eg: Wiggle Extractors)
	 * @throws IOException
	 */
	public void setRandomLineCount(Integer randomLineCount) throws UnsupportedOperationException, IOException {
		this.randomLineCount = randomLineCount;
		// if the randomLineCount variable is not null we generate a tree set of random line numbers to extract
		randomLineNumbers = null;
		if (randomLineCount != null) {
			randomLineNumbers = Extractors.generateRandomLineNumbers(randomLineCount, getDataFile());
		}
	}
}
