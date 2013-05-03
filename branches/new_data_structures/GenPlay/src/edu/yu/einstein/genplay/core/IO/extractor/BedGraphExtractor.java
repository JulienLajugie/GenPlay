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
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;


/**
 * A bedGraph file extractor
 * @author Julien Lajugie
 */
public final class BedGraphExtractor extends TextFileExtractor implements SCWReader {

	private Chromosome 						chromosome;		 	// chromosome of the last item read
	private Integer 						start;				// start position of the last item read
	private Integer 						stop;				// stop position of the last item read
	private Float 							score;				// score of the last item read


	/**
	 * Creates an instance of {@link BedGraphExtractor}
	 * @param dataFile file containing the data
	 * @throws FileNotFoundException if the specified file is not found
	 */
	public BedGraphExtractor(File dataFile) throws FileNotFoundException {
		super(dataFile);
	}


	@Override
	protected int extractDataLine(String line) throws DataLineException {
		chromosome = null;
		start = null;
		stop = null;
		score = null;
		String[] splitedLine = Extractors.parseLineTabOnly(line);
		if (splitedLine.length < 4) {
			//throw new InvalidDataLineException(extractedLine);
			throw new DataLineException(DataLineException.INVALID_PARAMETER_NUMBER);
		}
		String chromosomeName = splitedLine[0];

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

		try {
			chromosome = getProjectChromosome().get(chromosomeName) ;
		} catch (InvalidChromosomeException e) {
			// unknown chromosome
			return LINE_SKIPPED;
		}

		start = Extractors.getInt(splitedLine[1].trim());
		start = getMultiGenomePosition(chromosome, start);
		stop = Extractors.getInt(splitedLine[2].trim());
		stop = getMultiGenomePosition(chromosome, stop);
		score = Extractors.getFloat(splitedLine[3].trim());

		if (score == 0) {
			return LINE_SKIPPED;
		}

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
		return ITEM_EXTRACTED;
	}



	@Override
	public Chromosome getChromosome() {
		return chromosome;
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
}
