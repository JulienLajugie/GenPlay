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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.ChromosomeWindow;
import edu.yu.einstein.genplay.core.Gene;
import edu.yu.einstein.genplay.core.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.DataPrecision;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.enums.Strand;
import edu.yu.einstein.genplay.core.extractor.utils.DataLineValidator;
import edu.yu.einstein.genplay.core.generator.BinListGenerator;
import edu.yu.einstein.genplay.core.generator.ChromosomeWindowListGenerator;
import edu.yu.einstein.genplay.core.generator.GeneListGenerator;
import edu.yu.einstein.genplay.core.generator.RepeatFamilyListGenerator;
import edu.yu.einstein.genplay.core.generator.ScoredChromosomeWindowListGenerator;
import edu.yu.einstein.genplay.core.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.arrayList.DoubleArrayAsDoubleList;
import edu.yu.einstein.genplay.core.list.arrayList.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.chromosomeWindowList.ChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.list.repeatFamilyList.RepeatFamilyList;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;
import edu.yu.einstein.genplay.exception.InvalidDataLineException;
import edu.yu.einstein.genplay.util.Utils;



/**
 * A GTF file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public class GTFExtractor extends TextFileExtractor implements Serializable, StrandedExtractor, RepeatFamilyListGenerator, ChromosomeWindowListGenerator, 
ScoredChromosomeWindowListGenerator, BinListGenerator, GeneListGenerator {

	private static final long serialVersionUID = 6374158568964537008L; // generated ID
	private final ChromosomeListOfLists<Integer>	startList;		// list of position start
	private final ChromosomeListOfLists<Integer>	stopList;		// list of position stop
	private final ChromosomeListOfLists<String> 	nameList;		// list of name
	private final ChromosomeListOfLists<Strand> 	strandList;		// list of strand
	private final ChromosomeArrayListOfLists<Double>scoreList;		// list of scores
	private Strand 									selectedStrand;	// strand to extract, null for both
	private ReadLengthAndShiftHandler				readHandler;	// handler that computes the position of read by applying the shift


	/**
	 * Creates an instance of {@link GTFExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file containing the extraction logs
	 */
	public GTFExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		// initialize the lists
		startList = new ChromosomeArrayListOfLists<Integer>();
		stopList = new ChromosomeArrayListOfLists<Integer>();
		nameList = new ChromosomeArrayListOfLists<String>();
		strandList = new ChromosomeArrayListOfLists<Strand>();
		scoreList = new ChromosomeArrayListOfLists<Double>();
		// initialize the sublists
		for (int i = 0; i < projectChromosome.size(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			nameList.add(new ArrayList<String>());
			strandList.add(new ArrayList<Strand>());
			scoreList.add(new DoubleArrayAsDoubleList());
		}
	}


	@Override
	protected boolean extractLine(String line) throws InvalidDataLineException {
		String[] splitedLine = Utils.parseLineTabOnly(line);
		if (splitedLine.length < 8) {
			//throw new InvalidDataLineException(line);
			throw new InvalidDataLineException(InvalidDataLineException.INVALID_PARAMETER_NUMBER);
		}
		// we're just interested in exon lines
		if (splitedLine[2].trim().equalsIgnoreCase("exon")) {
			// retrieve the chromosome
			try {
				int chromosomeStatus;
				Chromosome chromo = null;
				try {
					chromo = projectChromosome.get(splitedLine[0]) ;
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
						int start = Integer.parseInt(splitedLine[3].trim());
						// retrieve the stop position
						int stop = Integer.parseInt(splitedLine[4].trim());
						// compute the read position with specified strand shift and read length
						if (readHandler != null) {
							ChromosomeWindow resultStartStop = readHandler.computeStartStop(chromo, start, stop, strand);
							start = resultStartStop.getStart();
							stop = resultStartStop.getStop();							
						}

						// retrieve the score
						Double score = null;
						String scoreStr = splitedLine[5].trim();
						if (!scoreStr.equals("-") && !scoreStr.equals(".")) {
							score = Double.parseDouble(scoreStr);
						}
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
								throw new InvalidDataLineException("The attribute 'gene_id' is missing.");
							}
							// if there is a FPKM attribute we replace the score by the FPKM 
							if (attributes.containsKey("FPKM")) {
								score = Double.parseDouble(attributes.get("FPKM"));
							} else if (attributes.containsKey("RPKM")) {
								// if there is no FPKM but there is a RPKM we replace the score by the RPKM
								score = Double.parseDouble(attributes.get("RPKM"));
							}
						}

						// Checks errors
						String errors = DataLineValidator.getErrors(chromo, start, stop, score, name, strand);
						if (errors.length() == 0) {
							// if we are in a multi-genome project, we compute the position on the meta genome
							start = getMultiGenomePosition(chromo, start);
							stop = getMultiGenomePosition(chromo, stop);
							startList.add(chromo, start);
							stopList.add(chromo, stop);
							scoreList.add(chromo, score);
							strandList.add(chromo, strand);
							nameList.add(chromo, name);
						} else {
							throw new InvalidDataLineException(errors);
						}
					}
				}
			} catch (InvalidChromosomeException e) {
				//throw new InvalidDataLineException(line);
				throw new InvalidDataLineException(InvalidDataLineException.INVALID_FORMAT_NUMBER);
			}
		}
		return false;
	}


	/**
	 * Parses the attribute field of the GTF file
	 * @param attributeString attribute field
	 * @return a Map with the attribute names as keys and the attribute values as fields
	 */
	private Map<String, String> parseAttributes(String attributeString) {
		Map<String, String> attributeMap = new HashMap<String, String>();
		String[] attributes = attributeString.split(";");
		for (String currentAttribute: attributes) {
			int indexFirstQuote = currentAttribute.indexOf('"');
			int indexLastQuote = currentAttribute.lastIndexOf('"');
			String attributeName = currentAttribute.substring(0, indexFirstQuote).trim();
			String attributeValue = currentAttribute.substring(indexFirstQuote + 1, indexLastQuote).trim();
			attributeMap.put(attributeName, attributeValue);			
		}		
		return attributeMap;
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
	public RepeatFamilyList toRepeatFamilyList()
	throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return new RepeatFamilyList(startList, stopList, nameList);
	}


	@Override
	public ChromosomeWindowList toChromosomeWindowList()
	throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return new ChromosomeWindowList(startList, stopList);
	}


	@Override
	public boolean overlapped() {
		return ScoredChromosomeWindowList.overLappingExist(startList, stopList);
	}


	@Override
	public ScoredChromosomeWindowList toScoredChromosomeWindowList(ScoreCalculationMethod scm) 
	throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return new ScoredChromosomeWindowList(startList, stopList, scoreList, scm);
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
	public BinList toBinList(int binSize, DataPrecision precision, ScoreCalculationMethod method) 
	throws IllegalArgumentException, InterruptedException, ExecutionException {
		return new BinList(binSize, precision, method, startList, stopList, scoreList);
	}


	@Override
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
			List<ScoredChromosomeWindow> exons = new ArrayList<ScoredChromosomeWindow>();			
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
						chromo = projectChromosome.get(i);
						strand = strandList.get(i, j);
						// we reset the exon list 
						exons.clear();
						// if there is no score we add an exon with 0 as a score
						if (scoreList.get(i).isEmpty()) {
							exons.add(new ScoredChromosomeWindow(startList.get(i, j), stopList.get(i, j), 0));
						} else {
							exons.add(new ScoredChromosomeWindow(startList.get(i, j), stopList.get(i, j), scoreList.get(i, j)));
						}
					} else {
						// if there is no score we add an exon with 0 as a score
						if (scoreList.get(i).isEmpty()) {
							exons.add(new ScoredChromosomeWindow(startList.get(i, j), stopList.get(i, j), 0));
						} else {
							exons.add(new ScoredChromosomeWindow(startList.get(i, j), stopList.get(i, j), scoreList.get(i, j)));
						}						
					}				
				}
				// we add the last gene of the current chromosome
				if (name != null) {
					geneList.get(i).add(createGene(name, chromo, strand, exons));
				}	
			}
		}		
		return new GeneList(geneList);
	}


	/**
	 * Creates a {@link Gene}
	 * @param name name of the gene
	 * @param chromo chromosome
	 * @param strand strand
	 * @param exons list of {@link ScoredChromosomeWindow} representing the exons of the gene
	 * @return a new Gene
	 */
	private Gene createGene(String name, Chromosome chromo, Strand strand, List<ScoredChromosomeWindow> exons){
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
		Gene gene = new Gene(name, chromo, strand, start, stop, exonStartsArray, exonStopsArray, exonScoresArray);
		// if there is no score we set the gene exon score field to null
		if (!areExonsScored) {
			gene.setExonScores(null);
		}
		return gene;
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
