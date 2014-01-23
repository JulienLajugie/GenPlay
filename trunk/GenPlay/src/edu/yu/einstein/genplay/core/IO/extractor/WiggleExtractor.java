/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
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
 * A Wiggle file extractor
 * @author Julien Lajugie
 */
public final class WiggleExtractor extends TextFileExtractor implements SCWReader {

	/** Default first base position of bed files. Bedgraph files are 0-based */
	public static final int DEFAULT_FIRST_BASE_POSITION = 1;

	private int	firstBasePosition = DEFAULT_FIRST_BASE_POSITION;// position of the first base
	private Chromosome 	chromosome;		 						// chromosome of the last item read
	private Integer 	start;									// start position of the last item read
	private Integer 	stop;									// stop position of the last item read
	private Float 		score;									// score of the last item read
	private int 		currentSpan;							// last span specified
	private int 		currentStep;							// last step specified
	private int 		currentPosition;						// current position
	private boolean 	isFixedStep = false;					// true if we are extrating a fixedStep line


	/**
	 * Creates an instance of {@link WiggleExtractor}
	 * @param dataFile file containing the data
	 * @throws FileNotFoundException if the specified file is not found
	 */
	public WiggleExtractor(File dataFile) throws FileNotFoundException {
		super(dataFile);
	}


	@Override
	protected int extractDataLine(String line) throws DataLineException {
		start = null;
		stop = null;
		score = null;
		String[] splittedLine = Extractors.parseLineTabAndSpace(line);
		int i = 0;
		while (i < splittedLine.length) {
			String currentField = splittedLine[i].trim();
			if (currentField.equalsIgnoreCase("variableStep")) {
				// a variableStep must at least contain 2 elements
				if (splittedLine.length < 2) {
					throw new DataLineException(DataLineException.INVALID_PARAMETER_NUMBER);
				} else {
					isFixedStep = false;
					currentSpan = 1;
				}
			} else if (currentField.equalsIgnoreCase("fixedStep")) {
				// a fixedStep must at least contain 4 elements
				if (splittedLine.length < 4) {
					throw new DataLineException(DataLineException.INVALID_PARAMETER_NUMBER);
				} else {
					isFixedStep = true;
					currentSpan = 1;
				}
			} else if ((currentField.length() > 6) && (currentField.substring(0, 6).equalsIgnoreCase("chrom="))) {
				// retrieve chromosome
				String chromosomeName = splittedLine[i].trim().substring(6).trim();
				if (getChromosomeSelector() != null) {
					// case where last chromosome already extracted, no more data to extract
					if (getChromosomeSelector().isExtractionDone(chromosomeName)) {
						return EXTRACTION_DONE;
					}
					// chromosome was not selected for extraction
					if (!getChromosomeSelector().isSelected(chromosomeName)) {
						chromosome = getProjectChromosome().get(chromosomeName);
						return LINE_SKIPPED;
					}
				}
				try {
					chromosome = getProjectChromosome().get(chromosomeName);
				} catch (InvalidChromosomeException e) {
					// unknown chromosome
					return LINE_SKIPPED;
				}
			} else if ((currentField.length() > 6) && (currentField.substring(0, 6).equalsIgnoreCase("start="))) {
				// retrieve start position
				String posStr = splittedLine[i].trim().substring(6);
				currentPosition = Extractors.getInt(posStr);
			} else if ((currentField.length() > 5) && (currentField.substring(0, 5).equalsIgnoreCase("step="))) {
				// retrieve step position
				String stepStr = splittedLine[i].trim().substring(5);
				currentStep = Extractors.getInt(stepStr);
			} else if ((currentField.length() > 5) && (currentField.substring(0, 5).equalsIgnoreCase("span="))) {
				// retrieve span
				String spanStr = splittedLine[i].trim().substring(5);
				currentSpan = Extractors.getInt(spanStr);
			} else {
				if (chromosome == null) {
					return LINE_SKIPPED;
				}
				if (isFixedStep) {
					score = Extractors.getFloat(splittedLine[i]);
					if ((score == 0) || !getChromosomeSelector().isSelected(chromosome.getName())) {
						return LINE_SKIPPED;
					}

					start = currentPosition;
					stop = currentPosition + currentSpan;
				} else {
					if (splittedLine.length < 2) {
						throw new DataLineException(DataLineException.INVALID_PARAMETER_NUMBER);
					}
					currentPosition = Extractors.getInt(splittedLine[i].trim());
					float score = Extractors.getFloat(splittedLine[i + 1]);
					i++;
					if ((score == 0) || !getChromosomeSelector().isSelected(chromosome.getName())) {
						return LINE_SKIPPED;
					}

					start = currentPosition;
					stop =  currentPosition + currentSpan;
				}
				// check for data line errors
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

				currentPosition += currentStep;
				return ITEM_EXTRACTED;
			}
			i++;
		}
		return LINE_EXTRACTED;
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
	public void setFirstBasePosition(int firstBasePosition) {
		this.firstBasePosition = firstBasePosition;
	}


	/**
	 * We raise a new UnsupportedOperationException because it's not possible to load
	 * a random fraction of a wiggle file
	 */
	@Override
	public void setRandomLineCount(Integer randomLineCount) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Wiggle files need to be entirely extracted");
	}
}
