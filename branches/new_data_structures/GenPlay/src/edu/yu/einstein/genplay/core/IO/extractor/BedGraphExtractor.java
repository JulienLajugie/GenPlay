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
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.IO.utils.DataLineValidator;
import edu.yu.einstein.genplay.core.generator.BinListGenerator;
import edu.yu.einstein.genplay.core.generator.ScoredChromosomeWindowListGenerator;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.DataPrecision;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.dataStructure.genomeList.GenomicDataArrayList;
import edu.yu.einstein.genplay.dataStructure.genomeList.GenomicDataList;
import edu.yu.einstein.genplay.dataStructure.genomeList.SCWList.MaskSCWListFactory;
import edu.yu.einstein.genplay.dataStructure.genomeList.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.genomeList.SCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.genomeList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.DoubleArrayAsDoubleList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.Utils;



/**
 * A bedGraph file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BedGraphExtractor extends TextFileExtractor
implements Serializable, ScoredChromosomeWindowListGenerator, BinListGenerator {

	private static final long serialVersionUID = 7106474719716124894L; // generated ID
	private final GenomicDataList<Integer>	startList;		// list of position start
	private final GenomicDataList<Integer>	stopList;		// list of position stop
	private final GenomicDataList<Double>		scoreList;		// list of scores


	/**
	 * Creates an instance of a {@link BedGraphExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public BedGraphExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		// initialize the lists
		startList = new GenomicDataArrayList<Integer>();
		stopList = new GenomicDataArrayList<Integer>();
		scoreList = new GenomicDataArrayList<Double>();
		// initialize the sublists
		for (int i = 0; i < projectChromosome.size(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			scoreList.add(new DoubleArrayAsDoubleList());
		}
	}


	/**
	 * Receives one line from the input file and extracts and adds
	 * a chromosome, a position start, a position stop and a score to the lists.
	 * @param extractedLine line read from the data file
	 * @return true when the extraction is done
	 * @throws DataLineException
	 * @throws FileErrorException
	 */
	@Override
	protected boolean extractLine(String extractedLine) throws DataLineException {
		String[] splitedLine = Utils.parseLineTabOnly(extractedLine);
		if (splitedLine.length < 4) {
			//throw new InvalidDataLineException(extractedLine);
			throw new DataLineException(DataLineException.INVALID_PARAMETER_NUMBER);
		}
		try {
			int chromosomeStatus;
			Chromosome chromosome = null;
			try {
				chromosome = projectChromosome.get(splitedLine[0]) ;
				chromosomeStatus = checkChromosomeStatus(chromosome);
			} catch (InvalidChromosomeException e) {
				chromosomeStatus = NEED_TO_BE_SKIPPED;
			}

			if (chromosomeStatus == AFTER_LAST_SELECTED) {
				return true;
			} else if (chromosomeStatus == NEED_TO_BE_SKIPPED) {
				return false;
			} else {
				int start = getInt(splitedLine[1].trim());
				start = getMultiGenomePosition(chromosome, start);
				int stop = getInt(splitedLine[2].trim());
				stop = getMultiGenomePosition(chromosome, stop);
				double score = getDouble(splitedLine[3].trim());

				String errors = DataLineValidator.getErrors(chromosome, start, stop, score);
				if (errors.length() == 0) {

					// Stop position checking, must not overpass the chromosome length
					DataLineException stopEndException = null;
					String stopEndErrorMessage = DataLineValidator.getErrors(chromosome, stop);
					if (!stopEndErrorMessage.isEmpty()) {
						stopEndException = new DataLineException(stopEndErrorMessage, DataLineException.SHRINK_STOP_PROCESS);
						stop = chromosome.getLength();
					}
					if (score != 0) {
						startList.add(chromosome, start);
						stopList.add(chromosome, stop);
						scoreList.add(chromosome, score);
						lineCount++;
					}
					if (stopEndException != null) {
						throw stopEndException;
					}
				} else {
					throw new DataLineException(errors);
				}
				return false;
			}
		} catch (InvalidChromosomeException e) {
			//throw new InvalidDataLineException(extractedLine);
			throw new DataLineException(DataLineException.INVALID_FORMAT_NUMBER);
		}
	}


	@Override
	public BinList toBinList(int binSize, DataPrecision precision, ScoreCalculationMethod method) throws IllegalArgumentException, InterruptedException, ExecutionException {
		return new BinList(binSize, precision, method, startList, stopList, scoreList);
	}


	@Override
	public ScoredChromosomeWindowList toScoredChromosomeWindowList(ScoreCalculationMethod scm) throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return new SimpleSCWList(startList, stopList, scoreList, scm);
	}

	@Override
	public ScoredChromosomeWindowList toMaskChromosomeWindowList() throws InvalidChromosomeException, InterruptedException,	ExecutionException {
		return MaskSCWListFactory.createMaskSCWArrayList(startList, stopList);
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
	public boolean overlapped() {
		return SimpleSCWList.overLappingExist(startList, stopList);
	}

}
