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
import edu.yu.einstein.genplay.core.IO.utils.TrackLineHeader;
import edu.yu.einstein.genplay.core.generator.GeneListGenerator;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.old.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicDataArrayList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicDataList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneListFactory;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.Utils;



/**
 * A GdpGene file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpGeneExtractor extends TextFileExtractor implements Serializable, GeneListGenerator {

	private static final long serialVersionUID = 7967902877674655813L; // generated ID

	private final GenomicDataList<Integer>	startList;		// list of position start
	private final GenomicDataList<Integer>	stopList;		// list of position stop
	private final GenomicDataList<String> 	nameList;		// list of name
	private final GenomicDataList<Strand> 	strandList;		// list of strand
	private final GenomicDataList<int[]> 	exonStartsList;	// list of list of exon starts
	private final GenomicDataList<int[]> 	exonStopsList;	// list of list of exon stops
	private final GenomicDataList<double[]>	exonScoresList;	// list of list of exon scores
	private String							geneDBURL;		// url of the gene database for the search
	private GeneScoreType					geneScoreType;	// type of gene and exon score (RPKM, max, sum)

	/**
	 * Creates an instance of a {@link GdpGeneExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public GdpGeneExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		// initialize the lists
		startList = new GenomicDataArrayList<Integer>();
		stopList = new GenomicDataArrayList<Integer>();
		nameList = new GenomicDataArrayList<String>();
		strandList = new GenomicDataArrayList<Strand>();
		exonStartsList = new GenomicDataArrayList<int[]>();
		exonStopsList = new GenomicDataArrayList<int[]>();
		exonScoresList = new GenomicDataArrayList<double[]>();
		// initialize the sublists
		for (int i = 0; i < projectChromosome.size(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			nameList.add(new ArrayList<String>());
			strandList.add(new ArrayList<Strand>());
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


	/**
	 * Receives one line from the input file and extracts and adds the data in the lists
	 * @param extractedLine line read from the data file
	 * @return true when the extraction is done
	 * @throws DataLineException
	 */
	@Override
	protected boolean extractLine(String extractedLine) throws DataLineException {
		String[] splitedLine = Utils.parseLineTabOnly(extractedLine);
		if (splitedLine.length < 3) {
			//throw new InvalidDataLineException(extractedLine);
			throw new DataLineException(DataLineException.INVALID_PARAMETER_NUMBER);
		}
		try {
			int chromosomeStatus;
			Chromosome chromosome = null;
			try {
				chromosome = projectChromosome.get(splitedLine[1]) ;
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
	public GeneList toGeneList() throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return GeneListFactory.createGeneList(nameList, strandList, startList, stopList, null, null, null, exonStartsList, exonStopsList, exonScoresList, geneDBURL, geneScoreType);
	}

}
