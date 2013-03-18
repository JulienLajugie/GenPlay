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
import edu.yu.einstein.genplay.core.generator.GeneListGenerator;
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
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneListFactory;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.repeatFamilyList.RepeatFamilyList;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.Utils;



/**
 * A PSL file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PSLExtractor extends TextFileExtractor implements Serializable, StrandedExtractor, RepeatFamilyListGenerator,
ScoredChromosomeWindowListGenerator, BinListGenerator, GeneListGenerator {

	private static final long serialVersionUID = -7099425835087057587L;	//generated ID
	private final GenomicDataList<Integer>	startList;		// list of position start
	private final GenomicDataList<Integer>	stopList;		// list of position stop
	private final GenomicDataList<String> 	nameList;		// list of name
	private final GenomicDataList<Double>	scoreList;		// list of scores
	private final GenomicDataList<Strand> 	strandList;		// list of strand
	private final GenomicDataList<int[]> 	exonStartsList;	// list of list of exon starts
	private final GenomicDataList<int[]> 	exonStopsList;	// list of list of exon stops
	private String							searchURL;		// url of the gene database for the search
	private Strand 							selectedStrand;	// strand to extract, null for both
	private ReadLengthAndShiftHandler		readHandler;	// handler that computes the position of read by applying the shift


	/**
	 * Creates an instance of {@link PSLExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public PSLExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		// initialize the lists
		startList = new GenomicDataArrayList<Integer>();
		stopList = new GenomicDataArrayList<Integer>();
		nameList = new GenomicDataArrayList<String>();
		scoreList = new GenomicDataArrayList<Double>();
		strandList = new GenomicDataArrayList<Strand>();
		exonStartsList = new GenomicDataArrayList<int[]>();
		exonStopsList = new GenomicDataArrayList<int[]>();
		// initialize the sublists
		for (int i = 0; i < projectChromosome.size(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			nameList.add(new ArrayList<String>());
			scoreList.add(new DoubleArrayAsDoubleList());
			strandList.add(new ArrayList<Strand>());
			exonStartsList.add(new ArrayList<int[]>());
			exonStopsList.add(new ArrayList<int[]>());
		}
	}


	@Override
	protected boolean extractLine(String extractedLine)	throws DataLineException {
		if (extractedLine.trim().substring(0, 10).equalsIgnoreCase("searchURL=")) {
			//searchURL = extractedLine.split("\"")[1].trim();
			searchURL = Utils.split(extractedLine, '"')[1].trim();
		}
		String[] splitedLine = Utils.parseLineTabOnly(extractedLine);
		if (splitedLine.length < 21) {
			//throw new InvalidDataLineException(extractedLine);
			throw new DataLineException(DataLineException.INVALID_PARAMETER_NUMBER);
		}

		try {
			int chromosomeStatus;
			Chromosome chromosome = null;
			try {
				chromosome = projectChromosome.get(splitedLine[13]) ;
				chromosomeStatus = checkChromosomeStatus(chromosome);
			} catch (InvalidChromosomeException e) {
				chromosomeStatus = NEED_TO_BE_SKIPPED;
			}

			if (chromosomeStatus == AFTER_LAST_SELECTED) {
				return true;
			} else if (chromosomeStatus == NEED_TO_BE_SKIPPED) {
				return false;
			} else {
				Strand strand = Strand.get(splitedLine[8].charAt(0));
				if (isStrandSelected(strand)) {
					int start = getInt(splitedLine[15]);
					int stop = getInt(splitedLine[16]);
					// compute the read position with specified strand shift and read length
					if (readHandler != null) {
						SimpleChromosomeWindow resultStartStop = readHandler.computeStartStop(chromosome, start, stop, strand);
						start = resultStartStop.getStart();
						stop = resultStartStop.getStop();
					}
					// add exons
					//String[] exonStartsStr = splitedLine[20].split(",");
					//String[] exonLengthsStr = splitedLine[18].split(",");
					String[] exonStartsStr = Utils.split(splitedLine[20], '"');
					String[] exonLengthsStr = Utils.split(splitedLine[18], '"');
					int[] exonStarts = new int[exonStartsStr.length];
					int[] exonStops = new int[exonStartsStr.length];
					for (int i = 0; i < exonStartsStr.length; i++) {
						// exons are for genes only so we don't need to
						// worry about the strand shift and the read length
						// since these operations are not available for genes
						int exonStart = getInt(exonStartsStr[i].trim());
						exonStart = getMultiGenomePosition(chromosome, exonStart);
						exonStarts[i] = exonStart;
						int exonLength = getInt(exonLengthsStr[i].trim());
						int exonStop = exonStarts[i] + exonLength;
						exonStop = getMultiGenomePosition(chromosome, exonStop);
						exonStops[i] = exonStop;
					}

					// Checks errors
					String errors = DataLineValidator.getErrors(chromosome, start, stop, exonStarts, exonStops, name, strand);
					if (errors.length() == 0) {

						// Stop position checking, must not overpass the chromosome length
						DataLineException stopEndException = null;
						String stopEndErrorMessage = DataLineValidator.getErrors(chromosome, stop);
						if (!stopEndErrorMessage.isEmpty()) {
							stopEndException = new DataLineException(stopEndErrorMessage, DataLineException.SHRINK_STOP_PROCESS);
							stop = chromosome.getLength();
						}

						// if we are in a multi-genome project, we compute the position on the meta genome
						start = getMultiGenomePosition(chromosome, start);
						stop = getMultiGenomePosition(chromosome, stop);
						nameList.add(chromosome, splitedLine[9]);
						startList.add(chromosome, start);
						stopList.add(chromosome, stop);
						scoreList.add(chromosome, getDouble(splitedLine[0]));
						strandList.add(chromosome, strand);
						exonStartsList.add(chromosome, exonStarts);
						exonStopsList.add(chromosome, exonStops);
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
	public GeneList toGeneList() throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return GeneListFactory.createGeneList(nameList, strandList, startList, stopList, scoreList, null, null, exonStartsList, exonStopsList, null, searchURL, null);
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
