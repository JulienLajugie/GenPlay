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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.Strand;
import edu.yu.einstein.genplay.core.extractor.utils.DataLineValidator;
import edu.yu.einstein.genplay.core.generator.GeneListGenerator;
import edu.yu.einstein.genplay.core.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.arrayList.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;
import edu.yu.einstein.genplay.exception.DataLineException;
import edu.yu.einstein.genplay.util.Utils;



/**
 * A GdpGene file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpGeneExtractor extends TextFileExtractor implements Serializable, GeneListGenerator {

	private static final long serialVersionUID = 7967902877674655813L; // generated ID

	private ChromosomeListOfLists<Integer>	startList;		// list of position start
	private ChromosomeListOfLists<Integer>	stopList;		// list of position stop
	private ChromosomeListOfLists<String> 	nameList;		// list of name
	private ChromosomeListOfLists<Strand> 	strandList;		// list of strand
	private ChromosomeListOfLists<int[]> 	exonStartsList;	// list of list of exon starts
	private ChromosomeListOfLists<int[]> 	exonStopsList;	// list of list of exon stops
	private ChromosomeListOfLists<double[]>	exonScoresList;	// list of list of exon scores
	private String							searchURL;		// url of the gene database for the search


	/**
	 * Creates an instance of a {@link GdpGeneExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public GdpGeneExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		// initialize the lists
		startList = new ChromosomeArrayListOfLists<Integer>();
		stopList = new ChromosomeArrayListOfLists<Integer>();
		nameList = new ChromosomeArrayListOfLists<String>();
		strandList = new ChromosomeArrayListOfLists<Strand>();
		exonStartsList = new ChromosomeArrayListOfLists<int[]>();
		exonStopsList = new ChromosomeArrayListOfLists<int[]>();
		exonScoresList = new ChromosomeArrayListOfLists<double[]>();
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


	/**
	 * Receives one line from the input file and extracts and adds the data in the lists
	 * @param extractedLine line read from the data file  
	 * @return true when the extraction is done
	 * @throws DataLineException 
	 */
	@Override
	protected boolean extractLine(String extractedLine) throws DataLineException {
		if (extractedLine.trim().substring(0, 10).equalsIgnoreCase("searchURL=")) {
			//searchURL = extractedLine.split("\"")[1].trim();
			searchURL = Utils.split(extractedLine, '"')[1].trim();
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
					// Gets the values
					String name = splitedLine[0].trim();
					Strand strand = Strand.get(splitedLine[2].trim().charAt(0));
					int start = Integer.parseInt(splitedLine[3].trim());
					start = getMultiGenomePosition(chromosome, start);
					int stop = Integer.parseInt(splitedLine[4].trim());
					stop = getMultiGenomePosition(chromosome, stop);
					//String[] exonStartsStr = splitedLine[5].split(",");
					//String[] exonStopsStr = splitedLine[6].split(",");
					String[] exonStartsStr = Utils.split(splitedLine[5], ',');
					String[] exonStopsStr = Utils.split(splitedLine[6], ',');
					int[] exonStarts = new int[exonStartsStr.length];
					int[] exonStops = new int[exonStartsStr.length];
					for (int i = 0; i < exonStartsStr.length; i++) {
						exonStarts[i] = Integer.parseInt(exonStartsStr[i].trim());
						exonStarts[i] = getMultiGenomePosition(chromosome, exonStarts[i]);
						exonStops[i] = Integer.parseInt(exonStopsStr[i].trim());
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
								exonScores[i] = Double.parseDouble(exonScoresStr[i]);
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
	}


	@Override
	public GeneList toGeneList() throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return new GeneList(nameList, strandList, startList, stopList, exonStartsList, exonStopsList, exonScoresList, searchURL);
	}

}
