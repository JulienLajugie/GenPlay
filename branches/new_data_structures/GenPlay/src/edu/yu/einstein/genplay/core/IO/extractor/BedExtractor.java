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

import edu.yu.einstein.genplay.core.IO.dataReader.GeneReader;
import edu.yu.einstein.genplay.core.IO.dataReader.RepeatReader;
import edu.yu.einstein.genplay.core.IO.dataReader.SCWReader;
import edu.yu.einstein.genplay.core.IO.utils.DataLineValidator;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.Utils;


/**
 * A BED file extractor
 * @author Julien Lajugie
 */
public class BedExtractor implements StrandedExtractor, SCWReader, GeneReader, RepeatReader {

	private Chromosome 						chromosome = null; 	// chromosome of the last item read
	private Integer 						start = null;		// start position of the last item read
	private Integer 						stop = null;		// stop position of the last item read
	private String 							name = null;		// name of the last item read
	private Float 							score = null;		// score of the last item read
	private Strand 							strand = null;		// strand of the last item read
	private Integer 						UTR5Bound = null;	// UTR5' stop position of the last item read
	private Integer 						UTR3Bound = null;	// UTR3' start position of the last item read
	private ListView<ScoredChromosomeWindow>exons = null;		// exons of the last item read
	
	
	@Override
	public boolean readItem() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	private boolean extractData(String line) {
			String[] splitedLine = Utils.parseLineTabOnly(extractedLine);
			if (splitedLine.length < 3) {
				//throw new InvalidDataLineException(extractedLine);
				throw new DataLineException(DataLineException.INVALID_PARAMETER_NUMBER);
			}
			try {
				int chromosomeStatus;
				try {
					chromosome = projectChromosome.get(splitedLine[0]) ;
				} catch (InvalidChromosomeException e) {}
					if (splitedLine.length > 5) {
						strand = Strand.get(splitedLine[5].trim().charAt(0));
					}
					if ((strand == null) || (isStrandSelected(strand))) {
						start = getInt(splitedLine[1].trim());
						stop = getInt(splitedLine[2].trim());

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
	public ListView<ScoredChromosomeWindow> getExons() {
		return exons;
	}

	@Override
	public String getGeneDBURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeneScoreType getGeneScoreType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Strand getStrand() {
		return strand;
	}

	@Override
	public Integer getUTR3Bound() {
		return UTR3Bound;
	}

	@Override
	public Integer getUTR5Bound() {
		return UTR5Bound;
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
