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
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.IO.dataReader.DataReader;
import edu.yu.einstein.genplay.core.IO.utils.ChromosomesSelector;
import edu.yu.einstein.genplay.core.IO.utils.Extractors;
import edu.yu.einstein.genplay.core.IO.utils.TrackLineHeader;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.gui.event.invalidDataEvent.InvalidDataEventsGenerator;
import edu.yu.einstein.genplay.gui.event.invalidDataEvent.InvalidDataListener;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;


/**
 * This class must be extended by the file extractors
 * @author Julien Lajugie
 */
public abstract class Extractor implements InvalidDataEventsGenerator, DataReader, Stoppable {

	/** Maximum number of warning that will be reported */
	private final static int WARNING_LIMIT = 100;

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

	private final File						dataFile;					// file containing the data
	private final List<InvalidDataListener> invalidDataListenersList;	// List of invalid data listeners
	private final BufferedReader 			reader;						// buffered reader to read the data
	private boolean							isStopped = false;			// set to true if the execution of the extractor needs to be stopped
	private boolean							isInitialized = false;		// true when the file has been initialized and is ready to be extracted
	private ChromosomesSelector				chromosomeSelector = null; 	// object that defines which chromosomes need to be extracted
	private long							startTime;					// time when the extraction started
	private long 							extractionDuration;			// duration of the extraction in seconds
	private	String							genomeName;					// name of the genome used for the mapping of the data
	private AlleleType						alleleType;					// type of allele to load the data (multi genome)
	private int								warningCount;				// number of warning sent to the listeners
	private int								itemExtracted;				// number of item extracted
	private int 							lineExtracted;				// number of line extracted
	private int								lineSkipped;				// number of line skipped
	private int 							currentLineNumber;			// current line number
	private TrackLineHeader 				trackLineHeader;			// header of the track extracted from the track line


	/**
	 * Constructor
	 * @param dataFile file containing the data
	 * @throws FileNotFoundException if the specified file is not found
	 */
	public Extractor(File dataFile) throws FileNotFoundException {
		this.dataFile = dataFile;
		invalidDataListenersList = new ArrayList<InvalidDataListener>();
		reader = new BufferedReader(new FileReader(dataFile), BUFFER_LENGTH);
		warningCount = 0;
		itemExtracted = 0;
		lineExtracted = 0;
		lineSkipped = 0;
	}


	@Override
	public final void addInvalidDataListener(InvalidDataListener invalidDataListener) {
		if (!invalidDataListenersList.contains(invalidDataListener)) {
			invalidDataListenersList.add(invalidDataListener);
		}
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
	protected final void finalizeExtraction() {
		if (reader != null) {
			extractionDuration = (System.currentTimeMillis() - startTime) / 1000l;
			try {
				reader.close();
			} catch (IOException e) {
				ExceptionManager.getInstance().caughtException(e);
			}
		}
	}


	/**
	 * @return the {@link ChromosomesSelector} object that specifies which chromosomes are selected to be extracted
	 */
	public ChromosomesSelector getChromosomeSelector() {
		return chromosomeSelector;
	}


	/**
	 * @return the number of the current line being extracted
	 */
	public int getCurrentLineNumber() {
		return currentLineNumber;
	}


	/**
	 * @return the duration of the extraction in seconds
	 */
	public long getExtractionDuration() {
		return extractionDuration;
	}


	@Override
	public InvalidDataListener[] getInvalidDataListeners() {
		return (InvalidDataListener[]) invalidDataListenersList.toArray();
	}


	/**
	 * @return the number of item extracted
	 */
	public int getItemExtracted() {
		return itemExtracted;
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
	 * @param chromosome	current chromosome
	 * @param position		current position
	 * @return				the associated associated meta genome position
	 */
	protected int getMultiGenomePosition(Chromosome chromosome, int position) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			return ShiftCompute.getPosition(genomeName, alleleType, position, chromosome, FormattedMultiGenomeName.META_GENOME_NAME);
		} else {
			return position;
		}
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
	private final void initializeExtraction() throws IOException {
		startTime = System.currentTimeMillis();
		isInitialized = true;
		readHeader();
	}


	/**
	 * @return true if the extraction was stopped
	 */
	public boolean isStopped() {
		return isStopped;
	}


	/**
	 * Notifies the listener that a data event occurred
	 * @param e exception to send to the listeners
	 * @param lineNumber line number with a problem
	 * @param line line with a problem
	 */
	protected void notifyDataEventListeners(DataLineException e, int lineNumber, String line) {
		if (warningCount < WARNING_LIMIT) {
			warningCount++;
			e.setFile(dataFile);
			e.setLineNumber(lineNumber);
			e.setLine(line);
			for (InvalidDataListener listeners: invalidDataListenersList) {
				listeners.handleDataError(e);
			}
		} else {
			// TODO send message to tell users that there is more warnings but they are not displayed
		}
	}


	/**
	 * Reads the header and call the {@link #extractHeader(String)} method for each header line
	 * @throws IOException
	 */
	private void readHeader() throws IOException {
		String currentLine = null;
		reader.mark(BUFFER_LENGTH);
		// loop for the header
		while (!isStopped
				&& ((currentLine = reader.readLine()) != null)
				&& Extractors.isHeaderLine(currentLine)) {
			currentLineNumber++;
			reader.mark(BUFFER_LENGTH);
			extractHeaderLine(currentLine);
		}
		// roll back to the begining of the read if
		if (((currentLine = reader.readLine()) != null)
				&& Extractors.isHeaderLine(currentLine)) {
			reader.reset();
		}
	}


	@Override
	public boolean readItem() throws IOException, DataLineException {
		// case where the extraction was stopped
		if (isStopped()) {
			finalizeExtraction();
			return false;
		}

		// case where we need to initialize the extractor
		if (!isInitialized) {
			initializeExtraction();
		}

		String currentLine = reader.readLine();
		currentLineNumber++;

		if (currentLine == null) {
			finalizeExtraction();
			return false;
		} else {
			int extractionStatus = ITEM_EXTRACTED;
			try {
				extractionStatus = extractDataLine(currentLine);
			} catch (DataLineException e) {
				notifyDataEventListeners(e, currentLineNumber, currentLine);
				lineSkipped++;
				throw e;
			}
			switch (extractionStatus) {
			case LINE_EXTRACTED:
				lineExtracted++;
				return readItem();
			case LINE_SKIPPED:
				lineSkipped++;
				return true;
			case ITEM_EXTRACTED:
				lineExtracted++;
				itemExtracted++;
				return true;
			}
			// case where the extraction is done
			finalizeExtraction();
			return false;
		}
	}


	@Override
	public void removeInvalidDataListener(InvalidDataListener invalidDataListener) {
		invalidDataListenersList.remove(invalidDataListener);
	}


	/**
	 * @param alleleType the alleleType to set
	 */
	public void setAlleleType(AlleleType alleleType) {
		this.alleleType = alleleType;
	}


	/**
	 * @param chromosomeSelector set the {@link ChromosomesSelector} that specifies which chromosome will be extracted
	 */
	public void setChromosomeSelector(ChromosomesSelector chromosomeSelector) {
		this.chromosomeSelector = chromosomeSelector;
	}


	/**
	 * @param genomeName the genomeName to set
	 */
	public void setGenomeName(String genomeName) {
		this.genomeName = genomeName;
	}


	@Override
	public void stop() {
		isStopped = true;
	}
}
