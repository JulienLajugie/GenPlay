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
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.IO.utils.DataLineValidator;
import edu.yu.einstein.genplay.core.IO.utils.TrackLineHeader;
import edu.yu.einstein.genplay.core.generator.BinListGenerator;
import edu.yu.einstein.genplay.core.generator.GeneListGenerator;
import edu.yu.einstein.genplay.core.generator.RepeatFamilyListGenerator;
import edu.yu.einstein.genplay.core.generator.ScoredChromosomeWindowListGenerator;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.DataPrecision;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.list.SCWList.MaskSCWListFactory;
import edu.yu.einstein.genplay.dataStructure.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.list.SCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.DoubleArrayAsDoubleList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.dataStructure.list.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.geneList.GeneListFactory;
import edu.yu.einstein.genplay.dataStructure.list.genomicDataList.GenomicDataArrayList;
import edu.yu.einstein.genplay.dataStructure.list.genomicDataList.GenomicDataList;
import edu.yu.einstein.genplay.dataStructure.list.repeatFamilyList.RepeatFamilyList;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.Utils;



/**
 * A BED file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public class BedExtractor extends TextFileExtractor
implements Serializable, StrandedExtractor, RepeatFamilyListGenerator,
ScoredChromosomeWindowListGenerator, GeneListGenerator, BinListGenerator {

	private static final long serialVersionUID = 7967902877674655813L; // generated ID
	private final GenomicDataList<Integer>	startList;		// list of position start
	private final GenomicDataList<Integer>	stopList;		// list of position stop
	private final GenomicDataList<String> 	nameList;		// list of name
	private final GenomicDataList<Double>	scoreList;		// list of scores
	private final GenomicDataList<Strand> 	strandList;		// list of strand
	private final GenomicDataList<Integer>	UTR5BoundList;	// list of translation 5' bounds
	private final GenomicDataList<Integer>	UTR3BoundList;	// list of translation 3' bounds
	private final GenomicDataList<int[]> 	exonStartsList;	// list of list of exon starts
	private final GenomicDataList<int[]> 	exonStopsList;	// list of list of exon stops
	private final GenomicDataList<double[]>	exonScoresList;	// list of list of exon scores
	private String							geneDBURL;		// url of the gene database
	private Strand 							selectedStrand;	// strand to extract, null for both
	private GeneScoreType					geneScoreType;	// type of gene and exon score (RPKM, max, sum)
	private ReadLengthAndShiftHandler		readHandler;	// handler that computes the position of read by applying the shift


	/**
	 * Creates an instance of {@link BedExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public BedExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		// initialize the lists
		startList = new GenomicDataArrayList<Integer>();
		stopList = new GenomicDataArrayList<Integer>();
		nameList = new GenomicDataArrayList<String>();
		scoreList = new GenomicDataArrayList<Double>();
		strandList = new GenomicDataArrayList<Strand>();
		UTR5BoundList = new GenomicDataArrayList<Integer>();
		UTR3BoundList = new GenomicDataArrayList<Integer>();
		exonStartsList = new GenomicDataArrayList<int[]>();
		exonStopsList = new GenomicDataArrayList<int[]>();
		exonScoresList = new GenomicDataArrayList<double[]>();
		// initialize the sublists
		for (int i = 0; i < projectChromosome.size(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			nameList.add(new ArrayList<String>());
			scoreList.add(new DoubleArrayAsDoubleList());
			strandList.add(new ArrayList<Strand>());
			UTR5BoundList.add(new IntArrayAsIntegerList());
			UTR3BoundList.add(new IntArrayAsIntegerList());
			exonStartsList.add(new ArrayList<int[]>());
			exonStopsList.add(new ArrayList<int[]>());
			exonScoresList.add(new ArrayList<double[]>());
		}
	}


	@Override
	public void extractTrackLineHeader(String line) {
		TrackLineHeader trackLineHeader = new TrackLineHeader();
		trackLineHeader.parseTrackLine(line);
		geneDBURL = trackLineHeader.getGeneDBURL();
		geneScoreType = trackLineHeader.getGeneScoreType();
	}


	@Override
	protected boolean extractLine(String extractedLine) throws DataLineException {
		if ((extractedLine.trim().length() >= 10) && (extractedLine.trim().substring(0, 10).equalsIgnoreCase("searchURL="))) {
			//searchURL = extractedLine.split("\"")[1].trim();
			geneDBURL = Utils.split(extractedLine, '"')[1].trim();
			return false;
		} else {
			String[] splitedLine = Utils.parseLineTabOnly(extractedLine);
			if (splitedLine.length < 3) {
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
					Strand strand = null;
					if (splitedLine.length > 5) {
						strand = Strand.get(splitedLine[5].trim().charAt(0));
					}
					if ((strand == null) || (isStrandSelected(strand))) {
						strandList.add(chromosome, strand);
						int start = getInt(splitedLine[1].trim());
						int stop = getInt(splitedLine[2].trim());

						String errors = DataLineValidator.getErrors(chromosome, start, stop);
						if (errors.length() == 0) {

							// Stop position checking, must not overpass the chromosome length
							DataLineException stopEndException = null;
							String stopEndErrorMessage = DataLineValidator.getErrors(chromosome, stop);
							if (!stopEndErrorMessage.isEmpty()) {
								stopEndException = new DataLineException(stopEndErrorMessage, DataLineException.SHRINK_STOP_PROCESS);
								stop = chromosome.getLength();
							}
							// compute the read position with specified strand shift and read length
							if (readHandler != null) {
								SimpleChromosomeWindow resultStartStop = readHandler.computeStartStop(chromosome, start, stop, strand);
								start = resultStartStop.getStart();
								stop = resultStartStop.getStop();
							}
							// if we are in a multi-genome project, we compute the position on the meta genome
							start = getMultiGenomePosition(chromosome, start);
							stop = getMultiGenomePosition(chromosome, stop);
							startList.add(chromosome, start);
							stopList.add(chromosome, stop);
							if (splitedLine.length > 3) {
								String name = splitedLine[3].trim();
								if (!name.trim().equals("-")) {
									nameList.add(chromosome, name);
								}
								if (splitedLine.length > 4) {
									if (!splitedLine[4].trim().equals("-")) {
										double score = getDouble(splitedLine[4].trim());
										scoreList.add(chromosome, score);
									}
									if (splitedLine.length > 7) {
										// UTR bounds are for genes only so we don't need to
										// worry about the strand shift and the read length
										// since these operations are not available for genes
										int UTR5Bound = getInt(splitedLine[6].trim(), start);
										int UTR3Bound = getInt(splitedLine[7].trim(), stop);

										// but we need to compute the position on the meta-genome
										UTR5Bound = getMultiGenomePosition(chromosome, UTR5Bound);
										UTR3Bound = getMultiGenomePosition(chromosome, UTR3Bound);

										UTR5BoundList.add(chromosome, UTR5Bound);
										UTR3BoundList.add(chromosome, UTR3Bound);
										if (splitedLine.length > 11) {
											if ((!splitedLine[10].trim().equals("-")) && (!splitedLine[11].trim().equals("-"))) {
												//String[] exonStartsStr = splitedLine[11].split(",");
												//String[] exonLengthsStr = splitedLine[10].split(",");
												String[] exonStartsStr = Utils.split(splitedLine[11], ',');
												String[] exonLengthsStr = Utils.split(splitedLine[10], ',');
												int[] exonStarts = new int[exonLengthsStr.length];
												int[] exonStops = new int[exonLengthsStr.length];
												for (int i = 0; i < exonLengthsStr.length; i++) {
													exonStarts[i] = getInt(exonStartsStr[i]) + start;
													exonStops[i] = exonStarts[i] + getInt(exonLengthsStr[i]);
												}
												exonStartsList.add(chromosome, exonStarts);
												exonStopsList.add(chromosome, exonStops);
												if (splitedLine.length > 12) {
													//String[] exonScoresStr = splitedLine[12].split(",");
													String[] exonScoresStr = Utils.split(splitedLine[12], ',');
													double[] exonScores = new double[exonScoresStr.length];
													for (int i = 0; i < exonScoresStr.length; i++) {
														exonScores[i] = getDouble(exonScoresStr[i]);
													}
													exonScoresList.add(chromosome, exonScores);
												}
											}
										}
									} else {
										// if the file contains no infomation about the UTR sites
										UTR5BoundList.add(chromosome, start);
										UTR3BoundList.add(chromosome, stop);
									}
								}
							}
							if (stopEndException != null) {
								throw stopEndException;
							}
						} else {
							throw new DataLineException(errors);
						}
					}
					lineCount++;
					return false;
				}
			} catch (InvalidChromosomeException e) {
				//throw new InvalidDataLineException(extractedLine);
				ExceptionManager.getInstance().caughtException(e);
				throw new DataLineException(DataLineException.INVALID_FORMAT_NUMBER);
			}
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
	public RepeatFamilyList toRepeatFamilyList() throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return new RepeatFamilyList(startList, stopList, nameList);
	}


	@Override
	public GeneList toGeneList() throws InvalidChromosomeException, InterruptedException, ExecutionException {
		boolean areExonsScored = false;
		// check if there is values in the exon scores list
		for (List<double[]> currentExonScores: exonScoresList) {
			if ((currentExonScores != null) && (!currentExonScores.isEmpty())) {
				areExonsScored = true;
			}
		}
		// if there is no exon score value we check if there is a general gene score value
		if (!areExonsScored) {
			boolean areGenesScored = false;
			for (int i = 0; (i < scoreList.size()) && !areGenesScored; i++) {
				for (int j = 0; (j < scoreList.size(i)) && !areGenesScored; j++) {
					if ((scoreList.get(i, j) != 0) && (scoreList.get(i, j) != 1)) {
						areGenesScored = true;
					}
				}
			}
			// in this case (where there is no exon specific score values but there is
			// genes score values) we attribute the gene score values as exon scores
			if (areGenesScored) {
				for (int i = 0; i < scoreList.size(); i++) {
					for (int j = 0; j < scoreList.size(i); j++) {
						double[] scoreToAdd = {scoreList.get(i, j)};
						exonScoresList.get(i).add(scoreToAdd);
					}
				}
			}
		}
		return GeneListFactory.createGeneList(nameList, strandList, startList, stopList, scoreList, UTR5BoundList, UTR3BoundList, exonStartsList, exonStopsList, exonScoresList, geneDBURL, geneScoreType);
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
