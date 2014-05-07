/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.core.IO.extractor;

import java.io.File;
import java.io.FileNotFoundException;

import edu.yu.einstein.genplay.core.IO.dataReader.GeneReader;
import edu.yu.einstein.genplay.core.IO.dataReader.RepeatReader;
import edu.yu.einstein.genplay.core.IO.dataReader.SCWReader;
import edu.yu.einstein.genplay.core.IO.dataReader.StrandReader;
import edu.yu.einstein.genplay.core.IO.utils.DataLineValidator;
import edu.yu.einstein.genplay.core.IO.utils.Extractors;
import edu.yu.einstein.genplay.core.IO.utils.StrandedExtractorOptions;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.Utils;


/**
 * A PSL file extractor
 * @author Julien Lajugie
 */
public final class PSLExtractor extends TextFileExtractor implements SCWReader, GeneReader, RepeatReader, StrandReader, StrandedExtractor {

	/** Default first base position of bed files. PSL files are 0-based */
	public static final int DEFAULT_FIRST_BASE_POSITION = 0;

	private int	firstBasePosition = DEFAULT_FIRST_BASE_POSITION;// position of the first base
	private StrandedExtractorOptions		strandOptions;		// options on the strand and read length / shift
	private Chromosome 						chromosome;		 	// chromosome of the last item read
	private Integer 						start;				// start position of the last item read
	private Integer 						stop;				// stop position of the last item read
	private String 							name;				// name of the last item read
	private Float 							score;				// score of the last item read
	private Strand 							strand;				// strand of the last item read
	private ListView<ScoredChromosomeWindow>exons;				// exons of the last item read


	/**
	 * Creates an instance of {@link PSLExtractor}
	 * @param dataFile file containing the data
	 * @throws FileNotFoundException if the specified file is not found
	 */
	public PSLExtractor(File dataFile) throws FileNotFoundException {
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
		exons = null;

		String[] splitedLine = Extractors.parseLineTabOnly(line);
		if (splitedLine.length < 21) {
			throw new DataLineException(DataLineException.INVALID_PARAMETER_NUMBER);
		}

		// chromosome
		String chromosomeName = splitedLine[13];
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
			chromosome = getProjectChromosome().get(chromosomeName) ;
		} catch (InvalidChromosomeException e) {
			// unknown chromosome
			return LINE_SKIPPED;
		}

		// strand
		strand = Strand.get(splitedLine[8].charAt(0));
		if ((strand != null) && (strandOptions != null) && (!strandOptions.isSelected(strand))) {
			chromosome = null;
			return LINE_SKIPPED;
		}

		// start and stop
		start = Extractors.getInt(splitedLine[15]);
		stop = Extractors.getInt(splitedLine[16]);

		String errors = DataLineValidator.getErrors(chromosome, start, stop);
		if (!errors.isEmpty()) {
			throw new DataLineException(errors);
		}

		// Stop position checking, must not be greater than the chromosome length
		String stopEndErrorMessage = DataLineValidator.getErrors(chromosome, stop);
		if (!stopEndErrorMessage.isEmpty()) {
			DataLineException stopEndException = new DataLineException(stopEndErrorMessage, DataLineException.SHRINK_STOP_PROCESS);
			// notify the listeners that the stop position needed to be shrunk
			notifyDataEventListeners(stopEndException, getCurrentLineNumber(), line);
			stop = chromosome.getLength();
		}

		// compute the read position with specified strand shift and read length
		if (strandOptions != null) {
			SimpleChromosomeWindow resultStartStop = strandOptions.computeStartStop(chromosome, start, stop, strand);
			start = resultStartStop.getStart();
			stop = resultStartStop.getStop();
		}

		// if we are in a multi-genome project, we compute the position on the meta genome
		int refGenomeStart = start;
		start = getRealGenomePosition(chromosome, start);
		stop = getRealGenomePosition(chromosome, stop);

		// exons
		String[] exonStartsStr = Utils.split(splitedLine[20], '"');
		String[] exonLengthsStr = Utils.split(splitedLine[18], '"');

		GenericSCWListViewBuilder exonListBuilder = new GenericSCWListViewBuilder();
		for (int i = 0; i < exonLengthsStr.length; i++) {
			// exons are for genes only so we don't need to
			// worry about the strand shift and the read length
			// since these operations are not available for genes
			int exonStart = Extractors.getInt(exonStartsStr[i]) + refGenomeStart;
			int exonStop = exonStart + Extractors.getInt(exonLengthsStr[i]);
			// compute the position on the meta-genome
			exonStart = getRealGenomePosition(chromosome, exonStart);
			exonStop = getRealGenomePosition(chromosome, exonStop);
			exonListBuilder.addElementToBuild(exonStart, exonStop, 0f);
		}
		exons = exonListBuilder.getListView();

		return ITEM_EXTRACTED;
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
	public int getFirstBasePosition() {
		return firstBasePosition;
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


	@Override
	public void setFirstBasePosition(int firstBasePosition) {
		this.firstBasePosition = firstBasePosition;
	}


	@Override
	public void setStrandedExtractorOptions(StrandedExtractorOptions options) {
		strandOptions = options;
	}
}
