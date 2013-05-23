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

import java.io.File;
import java.io.FileNotFoundException;

import edu.yu.einstein.genplay.core.IO.dataReader.SCWReader;
import edu.yu.einstein.genplay.core.IO.utils.DataLineValidator;
import edu.yu.einstein.genplay.core.IO.utils.Extractors;
import edu.yu.einstein.genplay.core.IO.utils.StrandedExtractorOptions;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;


/**
 * A SAM file extractor
 * @author Julien Lajugie
 */
public class SAMExtractor extends TextFileExtractor implements StrandedExtractor, SCWReader {

	/** Default first base position of bed files. Sam files are 1-based */
	public static final int DEFAULT_FIRST_BASE_POSITION = 1;

	private int	firstBasePosition = DEFAULT_FIRST_BASE_POSITION;// position of the first base
	private Chromosome 						chromosome;		 	// chromosome of the last item read
	private StrandedExtractorOptions		strandOptions;		// options on the strand and read length / shift
	private Integer 						start;				// start position of the last item read
	private Integer 						stop;				// stop position of the last item read
	private Float 							score;				// score of the last item read


	/**
	 * Creates an instance of {@link SAMExtractor}
	 * @param dataFile file containing the data
	 * @throws FileNotFoundException if the specified file is not found
	 */
	public SAMExtractor(File dataFile) throws FileNotFoundException {
		super(dataFile);
	}


	@Override
	protected int extractDataLine(String line) throws DataLineException {
		chromosome = null;
		start = null;
		stop = null;
		score = null;

		// if the line starts with @ it's header line so we skip it
		if (line.trim().charAt(0) != '@') {
			String[] splitedLine = Extractors.parseLineTabOnly(line);

			String chromosomeName = splitedLine[2];

			if (getChromosomeSelector() != null) {
				// case where last chromosome already extracted, no more data to extract
				if (getChromosomeSelector().isExtractionDone(chromosomeName)) {
					return EXTRACTION_DONE;
				}
				// chromosome was not selected for extraction
				if (!getChromosomeSelector().isSelected(chromosomeName)) {
					return LINE_SKIPPED;
				}
			}

			Chromosome chromosome = null;
			try {
				chromosome = getProjectChromosome().get(chromosomeName) ;
			} catch (InvalidChromosomeException e) {
				// unknown chromosome
				return LINE_SKIPPED;
			}

			Strand strand = null;
			int flag = Extractors.getInt(splitedLine[1].trim());
			boolean isReversedRead = (flag & 0x10) == 0x10;
			if (isReversedRead) {
				strand = Strand.THREE;
			} else {
				strand = Strand.FIVE;
			}

			// check if the strand of the read is selected for extraction
			if ((strand != null) && (strandOptions != null) && (!strandOptions.isSelected(strand))) {
				chromosome = null;
				return LINE_SKIPPED;
			}

			start = Extractors.getInt(splitedLine[3]);
			// in order to find the stop position we need to
			// add the length of sequence to the start position
			String sequence = splitedLine[9].trim();
			stop = start + sequence.length();
			// compute the read position with specified strand shift and read length
			if (strandOptions != null) {
				SimpleChromosomeWindow resultStartStop = strandOptions.computeStartStop(chromosome, start, stop, strand);
				start = resultStartStop.getStart();
				stop = resultStartStop.getStop();
			}

			// Checks errors
			String errors = DataLineValidator.getErrors(chromosome, start, stop);
			if (!errors.isEmpty()) {
				throw new DataLineException(errors);
			}

			// Stop position checking, must not be greater than the chromosome length
			String stopEndErrorMessage = DataLineValidator.getErrors(chromosome, stop);
			if (!stopEndErrorMessage.isEmpty()) {
				DataLineException stopEndException = new DataLineException(stopEndErrorMessage, DataLineException.SHRINK_STOP_PROCESS);
				// notify the listeners that the stop position needed to be shrunk
				notifyDataEventListeners(stopEndException, getCurrentLineNumber(), line);
				stop = chromosome.getLength();
			}

			// if we are in a multi-genome project, we compute the position on the meta genome
			start = getRealGenomePosition(chromosome, start);
			stop = getRealGenomePosition(chromosome, stop);
			score = 1f;
			return ITEM_EXTRACTED;
		}
		return LINE_SKIPPED;
	}


	@Override
	public Chromosome getChromosome() {
		return chromosome;
	}


	@Override
	public int getFirstBasePosition() {
		return firstBasePosition;
	}


	@Override
	public Float getScore() {
		return score;
	}


	@Override
	public Integer getStart() {
		return start;
	}


	@Override
	public Integer getStop() {
		return stop;
	}


	@Override
	public StrandedExtractorOptions getStrandedExtractorOptions() {
		return strandOptions;
	}


	@Override
	public void setFirstBasePosition(int firstBasePosition) {
		this.firstBasePosition = firstBasePosition;
	}


	@Override
	public void setStrandedExtractorOptions(StrandedExtractorOptions options) {
		strandOptions = options;
	}
}
