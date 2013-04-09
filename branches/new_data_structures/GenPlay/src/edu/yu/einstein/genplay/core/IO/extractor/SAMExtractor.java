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
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.old.DoubleArrayAsDoubleList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.old.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicDataArrayList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.binList.BinList;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.Utils;


/**
 * A SAM file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public class SAMExtractor extends TextFileExtractor implements Serializable, StrandedExtractor, BinListGenerator {

	private static final long serialVersionUID = -1917159784796564734L; // generated ID
	private final GenomicListView<Integer>	startList;		// list of position start
	private final GenomicListView<Integer>	stopList;		// list of position stop
	private final GenomicListView<Double>		scoreList;		// list of scores
	private Strand 									selectedStrand;	// strand to extract, null for both
	private ReadLengthAndShiftHandler				readHandler;	// handler that computes the position of read by applying the shift


	/**
	 * Creates an instance of {@link SAMExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public SAMExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		startList = new GenomicDataArrayList<Integer>();
		stopList = new GenomicDataArrayList<Integer>();
		scoreList = new GenomicDataArrayList<Double>();
		for (int i = 0; i < projectChromosome.size(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			scoreList.add(new DoubleArrayAsDoubleList());
		}
	}


	@Override
	protected boolean extractLine(String line) throws DataLineException {
		// if the line starts with @ it's header line so we skip it
		if (line.trim().charAt(0) != '@') {
			String[] splitedLine = Utils.parseLineTabOnly(line);
			try {
				int chromosomeStatus;
				Chromosome chromosome = null;
				try {
					chromosome = projectChromosome.get(splitedLine[2]) ;
					chromosomeStatus = checkChromosomeStatus(chromosome);
				} catch (InvalidChromosomeException e) {
					chromosomeStatus = NEED_TO_BE_SKIPPED;
				}

				if (chromosomeStatus == AFTER_LAST_SELECTED) {
					return true;
				} else if (chromosomeStatus != NEED_TO_BE_SKIPPED) {
					Strand strand = null;
					int flag = getInt(splitedLine[1].trim());
					boolean isReversedRead = (flag & 0x10) == 0x10;
					if (isReversedRead) {
						strand = Strand.THREE;
					} else {
						strand = Strand.FIVE;
					}
					if ((strand == null) || (isStrandSelected(strand))) {
						// we subtract 1 to the position because sam file position
						// are 1 base and genplay is 0 based
						int start = getInt(splitedLine[3]) - 1;
						// in order to find the stop position we need to
						// add the length of sequence to the start position
						String sequence = splitedLine[9].trim();
						int stop = start + sequence.length();
						// compute the read position with specified strand shift and read length
						if (readHandler != null) {
							SimpleChromosomeWindow resultStartStop = readHandler.computeStartStop(chromosome, start, stop, strand);
							start = resultStartStop.getStart();
							stop = resultStartStop.getStop();
						}

						// Checks errors
						String errors = DataLineValidator.getErrors(chromosome, start, stop);
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
							startList.add(chromosome, start);
							stopList.add(chromosome, stop);
							// TODO: add a BinList constructor that doesn't need
							// as score list so we don't need the useless next line
							scoreList.add(chromosome, 1.0);
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
				//return false;
				throw new DataLineException(DataLineException.INVALID_FORMAT_NUMBER);
			}
		}
		return false;
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
	public BinList toBinList(int binSize, ScorePrecision precision, ScoreOperation method)
			throws IllegalArgumentException, InterruptedException, ExecutionException {
		return new BinList(binSize, precision, method, startList, stopList, scoreList);
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
		this.readHandler = handler;
	}

}
