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


import java.io.File;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.DataPrecision;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.extractor.utils.DataLineValidator;
import edu.yu.einstein.genplay.core.generator.BinListGenerator;
import edu.yu.einstein.genplay.core.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.arrayList.DoubleArrayAsDoubleList;
import edu.yu.einstein.genplay.core.list.arrayList.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.exception.DataLineException;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.Utils;



/**
 * A pair file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PairExtractor extends TextFileExtractor
implements Serializable, BinListGenerator {

	private static final long serialVersionUID = -2160273514926102255L; // generated ID
	private final ChromosomeListOfLists<Integer>	positionList;		// list of position start
	private final ChromosomeListOfLists<Double>	scoreList;			// list of scores


	/**
	 * Creates an instance of {@link PairExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public PairExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		positionList = new ChromosomeArrayListOfLists<Integer>();
		scoreList = new ChromosomeArrayListOfLists<Double>();
		// initialize the sublists
		for (int i = 0; i < projectChromosome.size(); i++) {
			positionList.add(new IntArrayAsIntegerList());
			scoreList.add(new DoubleArrayAsDoubleList());
		}
	}


	/**
	 * Receives one line from the input file and extracts and adds the data in the lists
	 * @param extractedLine line read from the data file
	 * @return true when the extraction is done
	 * @throws DataLineException
	 */
	@Override
	protected boolean extractLine(String extractedLine) throws DataLineException {
		if (extractedLine.trim().length() == 0) {
			return false;
		}
		// We don't want to extract the header lines
		// So we extract only if the line starts with a number
		try {
			getInt(extractedLine.substring(0, 1));
		} catch (Exception e){
			return false;
		}

		String[] splitedLine = Utils.parseLineTabOnly(extractedLine);
		if (splitedLine.length < 10) {
			//throw new InvalidDataLineException(extractedLine);
			throw new DataLineException(DataLineException.INVALID_PARAMETER_NUMBER);
		}
		//String chromosomeField[] = splitedLine[2].split(":");
		String chromosomeField[] = Utils.split(splitedLine[2], ':');
		if (chromosomeField.length != 2) {
			//throw new InvalidDataLineException(extractedLine);
			throw new DataLineException(DataLineException.INVALID_PARAMETER_NUMBER);
		}
		try {
			int chromosomeStatus;
			Chromosome chromosome = null;
			try {
				chromosome = projectChromosome.get(chromosomeField[0]) ;
				chromosomeStatus = checkChromosomeStatus(chromosome);
			} catch (InvalidChromosomeException e) {
				chromosomeStatus = NEED_TO_BE_SKIPPED;
			}

			if (chromosomeStatus == AFTER_LAST_SELECTED) {
				return true;
			} else if (chromosomeStatus == NEED_TO_BE_SKIPPED) {
				return false;
			} else {
				int position = getInt(splitedLine[4]);
				Double score = getDouble(splitedLine[9]);

				// Checks errors
				String errors = DataLineValidator.getErrors(chromosome, position, position, score);
				if (errors.length() == 0) {
					position = getMultiGenomePosition(chromosome, position);
					positionList.add(chromosome, position);
					scoreList.add(chromosome, score);
					lineCount++;
					return false;
				} else {
					throw new DataLineException(errors);
				}
			}
		} catch (InvalidChromosomeException e) {
			//throw new InvalidDataLineException(extractedLine);
			throw new DataLineException(DataLineException.INVALID_FORMAT_NUMBER);
		}
	}


	@Override
	public boolean isBinSizeNeeded() {
		return true;
	}


	@Override
	public boolean isCriterionNeeded() {
		return true;
	}


	@Override
	public boolean isPrecisionNeeded() {
		return true;
	}


	@Override
	public BinList toBinList(int binSize, DataPrecision precision, ScoreCalculationMethod method) throws IllegalArgumentException, InterruptedException, ExecutionException {
		return new BinList(binSize, precision, method, positionList, scoreList);
	}

}
