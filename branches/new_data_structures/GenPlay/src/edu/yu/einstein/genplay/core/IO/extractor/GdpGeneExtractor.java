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

import edu.yu.einstein.genplay.core.IO.dataReader.GeneReader;
import edu.yu.einstein.genplay.core.IO.dataReader.RepeatReader;
import edu.yu.einstein.genplay.core.IO.dataReader.SCWReader;
import edu.yu.einstein.genplay.core.IO.utils.DataLineValidator;
import edu.yu.einstein.genplay.core.IO.utils.StrandedExtractorOptions;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.Utils;


/**
 * A GdpGene file extractor
 * @author Julien Lajugie
 */
public final class GdpGeneExtractor extends TextFileExtractor implements SCWReader, GeneReader, RepeatReader, StrandedExtractor {

	private StrandedExtractorOptions		strandOptions;		// options on the strand and read length / shift
	private Chromosome 						chromosome;		 	// chromosome of the last item read
	private Integer 						start;				// start position of the last item read
	private Integer 						stop;				// stop position of the last item read
	private String 							name;				// name of the last item read
	private Float 							score;				// score of the last item read
	private Strand 							strand;				// strand of the last item read
	private Integer 						UTR5Bound;			// UTR5' stop position of the last item read
	private Integer 						UTR3Bound;			// UTR3' start position of the last item read
	private ListView<ScoredChromosomeWindow>exons;				// exons of the last item read


	/**
	 * Creates an instance of {@link GdpGeneExtractor}
	 * @param dataFile file containing the data
	 * @throws FileNotFoundException if the specified file is not found
	 */
	public GdpGeneExtractor(File dataFile) throws FileNotFoundException {
		super(dataFile);
	}


	@Override
	protected int extractDataLine(String line) throws DataLineException {
		chromosome = null;
		start = null;
		stop = null;
		name = null;
		score = null;
		strand = null;
		UTR5Bound = null;
		UTR3Bound = null;
		exons = null;

		String[] splitedLine = Utils.parseLineTabOnly(extractedLine);
		if (splitedLine.length < 3) {
			//throw new InvalidDataLineException(extractedLine);
			throw new DataLineException(DataLineException.INVALID_PARAMETER_NUMBER);
		}
		try {
			int chromosomeStatus;
			Chromosome chromosome = null;
			try {
				chromosome = getProjectChromosome().get(splitedLine[1]) ;
				chromosomeStatus = checkChromosomeStatus(chromosome);
			} catch (InvalidChromosomeException e) {
				chromosomeStatus = NEED_TO_BE_SKIPPED;
			}

			if (chromosomeStatus == AFTER_LAST_SELECTED) {
				return true;
			} else if (chromosomeStatus == NEED_TO_BE_SKIPPED) {
				return false;
			} else {
				// Gets the values
				String name = splitedLine[0].trim();
				Strand strand = Strand.get(splitedLine[2].trim().charAt(0));
				int start = getInt(splitedLine[3].trim());
				start = getMultiGenomePosition(chromosome, start);
				int stop = getInt(splitedLine[4].trim());
				stop = getMultiGenomePosition(chromosome, stop);
				//String[] exonStartsStr = splitedLine[5].split(",");
				//String[] exonStopsStr = splitedLine[6].split(",");
				String[] exonStartsStr = Utils.split(splitedLine[5], ',');
				String[] exonStopsStr = Utils.split(splitedLine[6], ',');
				int[] exonStarts = new int[exonStartsStr.length];
				int[] exonStops = new int[exonStartsStr.length];
				for (int i = 0; i < exonStartsStr.length; i++) {
					exonStarts[i] = getInt(exonStartsStr[i].trim());
					exonStarts[i] = getMultiGenomePosition(chromosome, exonStarts[i]);
					exonStops[i] = getInt(exonStopsStr[i].trim());
					exonStops[i] = getMultiGenomePosition(chromosome, exonStops[i]);
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

					// Adds the values
					nameList.add(chromosome, name);
					strandList.add(chromosome, strand);
					startList.add(chromosome, start);
					stopList.add(chromosome, stop);
					exonStartsList.add(chromosome, exonStarts);
					exonStopsList.add(chromosome, exonStops);

					// Gets the scores
					if (splitedLine.length > 7) {
						//String[] exonScoresStr = splitedLine[7].split(",");
						String[] exonScoresStr = Utils.split(splitedLine[7], ',');
						double[] exonScores = new double[exonScoresStr.length];
						for (int i = 0; i < exonScoresStr.length; i++) {
							exonScores[i] = getDouble(exonScoresStr[i]);
						}
						exonScoresList.add(chromosome, exonScores);
					}
					lineCount++;

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
	public Chromosome getChromosome() {
		return chromosome;
	}


	@Override
	public ListView<ScoredChromosomeWindow> getExons() {
		return exons;
	}


	@Override
	public String getGeneDBURL() {
		return getTrackLineHeader().getGeneDBURL();
	}


	@Override
	public GeneScoreType getGeneScoreType() {
		return getTrackLineHeader().getGeneScoreType();
	}


	@Override
	public String getName() {
		return name;
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
	public Strand getStrand() {
		return strand;
	}


	@Override
	public StrandedExtractorOptions getStrandedExtractorOptions() {
		return strandOptions;
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
	public void setStrandedExtractorOptions(StrandedExtractorOptions options) {
		strandOptions = options;
	}
}
