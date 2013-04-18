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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.IO.dataReader.GeneReader;
import edu.yu.einstein.genplay.core.IO.dataReader.RepeatReader;
import edu.yu.einstein.genplay.core.IO.dataReader.SCWReader;
import edu.yu.einstein.genplay.core.IO.utils.DataLineValidator;
import edu.yu.einstein.genplay.core.IO.utils.Extractors;
import edu.yu.einstein.genplay.core.IO.utils.StrandedExtractorOptions;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.gene.SimpleGene;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.SimpleGeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.Utils;


/**
 * A GTF file extractor
 * @author Julien Lajugie
 */
public class GTFExtractor extends TextFileExtractor implements SCWReader, GeneReader, RepeatReader, StrandedExtractor {

	private StrandedExtractorOptions		strandOptions;		// options on the strand and read length / shift
	private Chromosome 						chromosome;		 	// chromosome of the last item read
	private Integer 						start;				// start position of the last item read
	private Integer 						stop;				// stop position of the last item read
	private String 							name;				// name of the last item read
	private Float 							score;				// score of the last item read
	private Strand 							strand;				// strand of the last item read
	private ListView<ScoredChromosomeWindow>exons;				// exons of the last item read


	/**
	 * Creates an instance of {@link GTFExtractor}
	 * @param dataFile file containing the data
	 * @throws FileNotFoundException if the specified file is not found
	 */
	public GTFExtractor(File dataFile) throws FileNotFoundException {
		super(dataFile);
	}


	/**
	 * Creates a {@link Gene}
	 * @param name name of the gene
	 * @param chromo chromosome
	 * @param strand strand
	 * @param exons list of {@link SimpleScoredChromosomeWindow} representing the exons of the gene
	 * @return a new Gene
	 */
	private Gene createGene(String name, Chromosome chromo, Strand strand, List<SimpleScoredChromosomeWindow> exons){
		// sort the exon by start position
		Collections.sort(exons);
		// creates an array of starts, an array of stops and an array of scores
		int[] exonStartsArray = new int[exons.size()];
		int[] exonStopsArray = new int[exons.size()];
		double[] exonScoresArray = new double[exons.size()];
		boolean areExonsScored = false;
		for (int k = 0; k < exons.size(); k++) {
			exonStartsArray[k] = exons.get(k).getStart();
			exonStopsArray[k] = exons.get(k).getStop();
			exonScoresArray[k] = exons.get(k).getScore();
			// check if all the exons are null
			if (exonScoresArray[k] != 0) {
				areExonsScored = true;
			}
		}
		// the start position is the start of the first exon
		int start = exonStartsArray[0];
		// the stop position is the stop of the last exon
		int stop = exonStopsArray[exonStopsArray.length - 1];
		Gene gene = new SimpleGene(name, chromo, strand, start, stop, 0, exonStartsArray, exonStopsArray, exonScoresArray);
		// if there is no score we set the gene exon score field to null
		if (!areExonsScored) {
			gene.setExonScores(null);
		}
		return gene;
	}


	@Override
	protected int extractDataLine(String line) throws DataLineException {
		chromosome = null;
		start = null;
		stop = null;
		name = null;
		score = null;
		strand = null;
		exons = null;

		String[] splitedLine = Extractors.parseLineTabOnly(line);
		if (splitedLine.length < 8) {
			throw new DataLineException(DataLineException.INVALID_PARAMETER_NUMBER);
		}
		// we're just interested in exon lines
		if (splitedLine[2].trim().equalsIgnoreCase("exon")) {
			// retrieve the chromosome
			try {
				int chromosomeStatus;
				Chromosome chromo = null;
				try {
					chromo = getProjectChromosome().get(splitedLine[0]) ;
					chromosomeStatus = checkChromosomeStatus(chromo);
				} catch (InvalidChromosomeException e) {
					chromosomeStatus = NEED_TO_BE_SKIPPED;
				}

				// check if we extracted all the selected chromosomes
				if (chromosomeStatus == AFTER_LAST_SELECTED) {
					// case where we extracted all the selected chromosomes
					return true;
				} else if (chromosomeStatus == NEED_TO_BE_SKIPPED) {
					// case where the current chromosome is not selected
					return false;
				} else {
					// case where we need to extract the current chromosome
					// retrieve the strand
					Strand strand = null;
					String strandStr = splitedLine[6].trim();
					if (!strandStr.equals(".")) {
						strand = Strand.get(strandStr.charAt(0));
					}
					if ((strand != null) && (isStrandSelected(strand))) {
						// retrieve the start position
						int start = getInt(splitedLine[3].trim());
						// retrieve the stop position
						int stop = getInt(splitedLine[4].trim());
						// compute the read position with specified strand shift and read length
						if (readHandler != null) {
							SimpleChromosomeWindow resultStartStop = readHandler.computeStartStop(chromo, start, stop, strand);
							start = resultStartStop.getStart();
							stop = resultStartStop.getStop();
						}

						// retrieve the score
						Double score = getDouble(splitedLine[5].trim(), null);
						// if there is some attribute informations
						String name = null;
						if (splitedLine.length >= 9) {
							Map<String, String> attributes = parseAttributes(splitedLine[8]);
							// try to retrieve the gene name
							if (attributes.containsKey("gene_id")) {
								name = attributes.get("gene_id");
							} else {
								// this is a mandatory attribute for genplay
								//throw new InvalidDataLineException(line);
								throw new DataLineException("The attribute 'gene_id' is missing.");
							}
							// if there is a FPKM attribute we replace the score by the FPKM
							if (attributes.containsKey("FPKM")) {
								score = getDouble(attributes.get("FPKM"));
							} else if (attributes.containsKey("RPKM")) {
								// if there is no FPKM but there is a RPKM we replace the score by the RPKM
								score = getDouble(attributes.get("RPKM"));
							}
						}

						// Checks errors
						String errors = DataLineValidator.getErrors(chromo, start, stop, score, name, strand);
						if (errors.length() == 0) {

							// Stop position checking, must not overpass the chromosome length
							DataLineException stopEndException = null;
							String stopEndErrorMessage = DataLineValidator.getErrors(chromo, stop);
							if (!stopEndErrorMessage.isEmpty()) {
								stopEndException = new DataLineException(stopEndErrorMessage, DataLineException.SHRINK_STOP_PROCESS);
								stop = chromo.getLength();
							}

							// if we are in a multi-genome project, we compute the position on the meta genome
							start = getMultiGenomePosition(chromo, start);
							stop = getMultiGenomePosition(chromo, stop);
							startList.add(chromo, start);
							stopList.add(chromo, stop);
							scoreList.add(chromo, score);
							strandList.add(chromo, strand);
							nameList.add(chromo, name);

							if (stopEndException != null) {
								throw stopEndException;
							}
						} else {
							throw new DataLineException(errors);
						}
					}
				}
			} catch (InvalidChromosomeException e) {
				//throw new InvalidDataLineException(line);
				throw new DataLineException(DataLineException.INVALID_FORMAT_NUMBER);
			}
		}
		return false;
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
		return stop;
	}


	@Override
	public Integer getUTR5Bound() {
		return start;
	}



	/**
	 * Parses the attribute field of the GTF file
	 * @param attributeString attribute field
	 * @return a Map with the attribute names as keys and the attribute values as fields
	 */
	private Map<String, String> parseAttributes(String attributeString) {
		Map<String, String> attributeMap = new HashMap<String, String>();
		//String[] attributes = attributeString.split(";");
		String[] attributes = Utils.split(attributeString, ';');
		for (String currentAttribute: attributes) {
			int indexFirstQuote = currentAttribute.indexOf('"');
			int indexLastQuote = currentAttribute.lastIndexOf('"');
			String attributeName = currentAttribute.substring(0, indexFirstQuote).trim();
			String attributeValue = currentAttribute.substring(indexFirstQuote + 1, indexLastQuote).trim();
			attributeMap.put(attributeName, attributeValue);
		}
		return attributeMap;
	}


	/**
	 * We raise a new UnsupportedOperationException because it's not possible to load
	 * a random fraction of a GTF file
	 */
	@Override
	public void setRandomLineCount(Integer randomLineCount) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Wiggle files need to be entirely extracted");
	}


	public GeneList toGeneList()
			throws InvalidChromosomeException, InterruptedException, ExecutionException {
		// return list
		List<List<Gene>> geneList = new ArrayList<List<Gene>>();
		// creates a list of genes from the list of exons
		for (int i = 0; i < startList.size(); i++) {
			geneList.add(new ArrayList<Gene>());
			String name = null;
			Chromosome chromo = null;
			Strand strand = null;
			List<SimpleScoredChromosomeWindow> exons = new ArrayList<SimpleScoredChromosomeWindow>();
			if (startList.get(i) != null) {
				for (int j = 0; j < startList.size(i); j++) {
					// if we starting a new gene
					if ((name == null) || !(nameList.get(i, j).equalsIgnoreCase(name))) {
						// if it's not the first gene of the chromosome and the last gene is done we add it
						if (name != null) {
							geneList.get(i).add(createGene(name, chromo, strand, exons));
						}
						// now that the last gene has been added we start a new one
						name = nameList.get(i, j);
						chromo = getProjectChromosome().get(i);
						strand = strandList.get(i, j);
						// we reset the exon list
						exons.clear();
						// if there is no score we add an exon with 0 as a score
						if (scoreList.get(i).isEmpty()) {
							exons.add(new SimpleScoredChromosomeWindow(startList.get(i, j), stopList.get(i, j), 0));
						} else {
							exons.add(new SimpleScoredChromosomeWindow(startList.get(i, j), stopList.get(i, j), scoreList.get(i, j)));
						}
					} else {
						// if there is no score we add an exon with 0 as a score
						if (scoreList.get(i).isEmpty()) {
							exons.add(new SimpleScoredChromosomeWindow(startList.get(i, j), stopList.get(i, j), 0));
						} else {
							exons.add(new SimpleScoredChromosomeWindow(startList.get(i, j), stopList.get(i, j), scoreList.get(i, j)));
						}
					}
				}
				// we add the last gene of the current chromosome
				if (name != null) {
					geneList.get(i).add(createGene(name, chromo, strand, exons));
				}
			}
		}
		return new SimpleGeneList(geneList, null, null);
	}
}
