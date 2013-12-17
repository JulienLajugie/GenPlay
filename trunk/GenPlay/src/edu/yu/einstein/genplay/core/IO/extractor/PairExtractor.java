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
import edu.yu.einstein.genplay.util.Utils;


/**
 * An Affymetrix pair file extractor
 * @author Julien Lajugie
 */
public final class PairExtractor extends TextFileExtractor implements SCWReader {

	/** Default first base position of bed files. Affymetrix PAIR files are 1-based (to be verified) */
	public static final int DEFAULT_FIRST_BASE_POSITION = 0;

	private int	firstBasePosition = DEFAULT_FIRST_BASE_POSITION;// position of the first base
	private Chromosome 						chromosome;		 	// chromosome of the last item read
	private Integer 						position;			// position of the last item read
	private Float 							score;				// score of the last item read


	/**
	 * Creates an instance of {@link PairExtractor}
	 * @param dataFile file containing the data
	 * @throws FileNotFoundException if the specified file is not found
	 */
	public PairExtractor(File dataFile) throws FileNotFoundException {
		super(dataFile);
	}


	@Override
	protected int extractDataLine(String line) throws DataLineException {
		chromosome = null;
		position = null;
		score = null;
		if (line.trim().isEmpty()) {
			return LINE_SKIPPED;
		}
		// We don't want to extract the header lines
		// So we extract only if the line starts with a number
		try {
			Extractors.getInt(line.substring(0, 1));
		} catch (Exception e){
			return LINE_SKIPPED;
		}

		String[] splitedLine = Extractors.parseLineTabOnly(line);
		if (splitedLine.length < 10) {
			throw new DataLineException(DataLineException.INVALID_PARAMETER_NUMBER);
		}
		String chromosomeField[] = Utils.split(splitedLine[2], ':');
		if (chromosomeField.length != 2) {
			throw new DataLineException(DataLineException.INVALID_PARAMETER_NUMBER);
		}
		String chromosomeName = chromosomeField[0];

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

		position = Extractors.getInt(splitedLine[4]);
		score = Extractors.getFloat(splitedLine[9]);

		// Checks errors
		String errors = DataLineValidator.getErrors(chromosome, position, position, score);
		if (errors.length() == 0) {
			position = getRealGenomePosition(chromosome, position);
			return ITEM_EXTRACTED;
		} else {
			throw new DataLineException(errors);
		}
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
		return position;
	}


	@Override
	public Integer getStop() {
		return position;
	}


	@Override
	public void setFirstBasePosition(int firstBasePosition) {
		this.firstBasePosition = firstBasePosition;
	}
}

