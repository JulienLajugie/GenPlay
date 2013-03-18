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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.IO.utils.DataLineValidator;
import edu.yu.einstein.genplay.core.generator.BinListGenerator;
import edu.yu.einstein.genplay.core.generator.RepeatFamilyListGenerator;
import edu.yu.einstein.genplay.core.generator.ScoredChromosomeWindowListGenerator;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.old.DoubleArrayAsDoubleList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.old.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicDataArrayList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicDataList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.MaskSCWListFactory;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.repeatFamilyList.RepeatFamilyList;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.Utils;



/**
 * A GFF file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GFFExtractor extends TextFileExtractor
implements Serializable, StrandedExtractor, RepeatFamilyListGenerator,
ScoredChromosomeWindowListGenerator, BinListGenerator {

	private static final long serialVersionUID = -2798372250708609794L; // generated ID
	private final GenomicDataList<Integer>	startList;		// list of position start
	private final GenomicDataList<Integer>	stopList;		// list of position stop
	private final GenomicDataList<String> 	nameList;		// list of name
	private final GenomicDataList<Double>	scoreList;		// list of scores
	private final GenomicDataList<Strand> 	strandList;		// list of strand
	private Strand 							selectedStrand;	// strand to extract, null for both
	private ReadLengthAndShiftHandler		readHandler;	// handler that computes the position of read by applying the shift


	/**
	 * Creates an instance of {@link GFFExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public GFFExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		// initialize the lists
		startList = new GenomicDataArrayList<Integer>();
		stopList = new GenomicDataArrayList<Integer>();
		nameList = new GenomicDataArrayList<String>();
		scoreList = new GenomicDataArrayList<Double>();
		strandList = new GenomicDataArrayList<Strand>();
		// initialize the sublists
		for (int i = 0; i < projectChromosome.size(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			nameList.add(new ArrayList<String>());
			scoreList.add(new DoubleArrayAsDoubleList());
			strandList.add(new ArrayList<Strand>());
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
		String[] splitedLine = Utils.parseLineTabOnly(extractedLine);
		if (splitedLine.length < 7) {
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
				Strand strand = Strand.get(splitedLine[6].charAt(0));
				if (isStrandSelected(strand)) {
					String name = splitedLine[2];
					int start = getInt(splitedLine[3]);
					int stop = getInt(splitedLine[4]);
					double score = getDouble(splitedLine[5]);
					// compute the read position with specified strand shift and read length
					if (readHandler != null) {
						SimpleChromosomeWindow resultStartStop = readHandler.computeStartStop(chromosome, start, stop, strand);
						start = resultStartStop.getStart();
						stop = resultStartStop.getStop();
					}

					// Checks errors
					String errors = DataLineValidator.getErrors(chromosome, start, stop, score, name, strand);
					if (errors.length() == 0) {

						// Stop position checking, must not overpass the chromosome length
						DataLineException stopEndException = null;
						String stopEndErrorMessage = DataLineValidator.getErrors(chromosome, stop);
						if (!stopEndErrorMessage.isEmpty()) {
							stopEndException = new DataLineException(stopEndErrorMessage, DataLineException.SHRINK_STOP_PROCESS);
							stop = chromosome.getLength();
						}

						nameList.add(chromosome, name);
						// if we are in a multi-genome project, we compute the position on the meta genome
						start = getMultiGenomePosition(chromosome, start);
						stop = getMultiGenomePosition(chromosome, stop);
						startList.add(chromosome, start);
						stopList.add(chromosome, stop);
						scoreList.add(chromosome, score);
						strandList.add(chromosome, strand);
						lineCount++;

						if (stopEndException != null) {
							throw stopEndException;
						}
					} else {
						throw new DataLineException(errors);
					}
				}
				return false;
			}
		} catch (InvalidChromosomeException e) {
			//throw new InvalidDataLineException(extractedLine);
			throw new DataLineException(DataLineException.INVALID_FORMAT_NUMBER);
		}
	}


	@Override
	public RepeatFamilyList toRepeatFamilyList() throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return new RepeatFamilyList(startList, stopList, nameList);
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
	public BinList toBinList(int binSize, ScorePrecision precision, ScoreCalculationMethod method) throws IllegalArgumentException, InterruptedException, ExecutionException {
		return new BinList(binSize, precision, method, startList, stopList, scoreList);
	}


	@Override
	public boolean overlapped() {
		return SimpleSCWList.overLappingExist(startList, stopList);
	}

	@Override
	public boolean isStrandSelected(Strand aStrand) {
		if (selectedStrand == null) {
			return true;
		} else {
			return selectedStrand.equals(aStrand);
		}
	}


	@Override
	public void selectStrand(Strand strandToSelect) {
		selectedStrand = strandToSelect;
	}


	@Override
	public ReadLengthAndShiftHandler getReadLengthAndShiftHandler() {
		return readHandler;
	}


	@Override
	public void setReadLengthAndShiftHandler(ReadLengthAndShiftHandler handler) {
		readHandler = handler;
	}

}
