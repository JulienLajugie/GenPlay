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
import java.util.HashMap;
import java.util.Map;

import edu.yu.einstein.genplay.core.IO.dataReader.GeneReader;
import edu.yu.einstein.genplay.core.IO.utils.DataLineValidator;
import edu.yu.einstein.genplay.core.IO.utils.Extractors;
import edu.yu.einstein.genplay.core.IO.utils.StrandedExtractorOptions;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.Utils;


/**
 * A GTF file extractor
 * @author Julien Lajugie
 */
public class GTFExtractor extends TextFileExtractor implements GeneReader, StrandedExtractor {

	private StrandedExtractorOptions				strandOptions;		// options on the strand and read length / shift
	private Chromosome 								currentChromosome;	// chromosome of the current item
	private Chromosome 								previousChromosome;	// chromosome of the last item read
	private String 									currentName;		// name of the current item
	private String 									previousName;		// name of the last item read
	private Strand 									currentStrand;		// strand of the current item
	private Strand									previousStrand;		// strand of the last item read
	private ListViewBuilder<ScoredChromosomeWindow> exonBuilder;		// exons builder of the last item read
	private ListView<ScoredChromosomeWindow> 		previousExons;		// exons of the previous item read


	/**
	 * Creates an instance of {@link GTFExtractor}
	 * @param dataFile file containing the data
	 * @throws FileNotFoundException if the specified file is not found
	 */
	public GTFExtractor(File dataFile) throws FileNotFoundException {
		super(dataFile);
		exonBuilder = new GenericSCWListViewBuilder();
	}


	@Override
	protected int extractDataLine(String line) throws DataLineException {
		previousChromosome = currentChromosome;
		previousName = currentName;
		previousStrand = currentStrand;

		currentChromosome = null;
		currentName = null;
		currentStrand = null;

		String[] splitedLine = Extractors.parseLineTabOnly(line);
		if (splitedLine.length < 8) {
			throw new DataLineException(DataLineException.INVALID_PARAMETER_NUMBER);
		}

		// chromosome
		String chromosomeName = splitedLine[0];
		if (getChromosomeSelector() != null) {
			// case where last chromosome already extracted, no more data to extract
			if (getChromosomeSelector().isExtractionDone(chromosomeName)) {
				return EXTRACTION_DONE;
			}
			// chromosome was not selected for extraction
			if (!getChromosomeSelector().isSelected(chromosomeName)) {
				return LINE_SKIPPED;
			}
		}
		try {
			currentChromosome = getProjectChromosome().get(chromosomeName) ;
		} catch (InvalidChromosomeException e) {
			// unknown chromosome
			return LINE_SKIPPED;
		}

		// case where we need to extract the current chromosome
		// retrieve the strand
		String strandStr = splitedLine[6].trim();
		if (!strandStr.equals(".")) {
			currentStrand = Strand.get(strandStr.charAt(0));
		}

		if ((currentStrand != null) && (strandOptions != null) && (!strandOptions.isSelected(currentStrand))) {
			currentChromosome = null;
			return LINE_SKIPPED;
		}

		// start and stop position
		int start = Extractors.getInt(splitedLine[3].trim());
		int stop = Extractors.getInt(splitedLine[4].trim());

		String errors = DataLineValidator.getErrors(currentChromosome, start, stop);
		if (!errors.isEmpty()) {
			throw new DataLineException(errors);
		}

		// Stop position checking, must not be greater than the chromosome length
		String stopEndErrorMessage = DataLineValidator.getErrors(currentChromosome, stop);
		if (!stopEndErrorMessage.isEmpty()) {
			DataLineException stopEndException = new DataLineException(stopEndErrorMessage, DataLineException.SHRINK_STOP_PROCESS);
			// notify the listeners that the stop position needed to be shrunk
			notifyDataEventListeners(stopEndException, getCurrentLineNumber(), line);
			stop = currentChromosome.getLength();
		}

		// compute the read position with specified strand shift and read length
		if (strandOptions != null) {
			SimpleChromosomeWindow resultStartStop = strandOptions.computeStartStop(currentChromosome, start, stop, currentStrand);
			start = resultStartStop.getStart();
			stop = resultStartStop.getStop();
		}

		// if we are in a multi-genome project, we compute the position on the meta genome
		start = getMultiGenomePosition(currentChromosome, start);
		stop = getMultiGenomePosition(currentChromosome, stop);

		// retrieve the score
		Float score = Extractors.getFloat(splitedLine[5].trim(), null);
		// if there is some attribute informations
		if (splitedLine.length >= 9) {
			Map<String, String> attributes = parseAttributes(splitedLine[8]);
			// try to retrieve the gene name
			if (attributes.containsKey("gene_id")) {
				currentName = attributes.get("gene_id");
			} else {
				// this is a mandatory attribute for genplay
				throw new DataLineException("The attribute 'gene_id' is missing.");
			}
			if (attributes.containsKey("RPKM")) {
				// if there is a RPKM attribute we replace the score by the RPKM
				score = Extractors.getFloat(attributes.get("RPKM"));
			} else if (attributes.containsKey("FPKM")) {
				// if there is no RPKM but there is a FPKM we replace the score by the FPKM
				score = Extractors.getFloat(attributes.get("FPKM"));
			} else {
				score = Float.NaN;
			}
			if (currentName.equals(previousName)) {
				ScoredChromosomeWindow scw = new SimpleScoredChromosomeWindow(start, stop, score);
				exonBuilder.addElementToBuild(scw);
				return LINE_EXTRACTED;
			} else {
				previousExons = exonBuilder.getListView();
				exonBuilder = new GenericSCWListViewBuilder();
				ScoredChromosomeWindow scw = new SimpleScoredChromosomeWindow(start, stop, score);
				exonBuilder.addElementToBuild(scw);
				return ITEM_EXTRACTED;
			}
		}
		// this is a mandatory attribute for genplay
		throw new DataLineException("The attribute 'gene_id' is missing.");
	}


	@Override
	public Chromosome getChromosome() {
		return previousChromosome;
	}


	@Override
	public ListView<ScoredChromosomeWindow> getExons() {
		return previousExons;
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
		return previousName;
	}


	@Override
	public Float getScore() {
		return Float.NaN;
	}


	@Override
	public Integer getStart() {
		if ((previousExons != null) && !previousExons.isEmpty()) {
			return previousExons.get(0).getStart();
		} else {
			return null;
		}
	}


	@Override
	public Integer getStop() {
		if ((previousExons != null) && !previousExons.isEmpty()) {
			return previousExons.get(previousExons.size() - 1).getStart();
		} else {
			return null;
		}
	}


	@Override
	public Strand getStrand() {
		return previousStrand;
	}


	@Override
	public StrandedExtractorOptions getStrandedExtractorOptions() {
		return strandOptions;
	}


	@Override
	public Integer getUTR3Bound() {
		if ((previousExons != null) && !previousExons.isEmpty()) {
			return previousExons.get(previousExons.size() - 1).getStart();
		} else {
			return null;
		}
	}


	@Override
	public Integer getUTR5Bound() {
		if ((previousExons != null) && !previousExons.isEmpty()) {
			return previousExons.get(0).getStart();
		} else {
			return null;
		}
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


	@Override
	public void setStrandedExtractorOptions(StrandedExtractorOptions options) {
		strandOptions = options;
	}
}
