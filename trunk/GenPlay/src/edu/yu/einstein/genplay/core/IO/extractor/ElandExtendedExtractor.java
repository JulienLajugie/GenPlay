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

import edu.yu.einstein.genplay.core.IO.dataReader.SCWReader;
import edu.yu.einstein.genplay.core.IO.dataReader.StrandReader;
import edu.yu.einstein.genplay.core.IO.utils.DataLineValidator;
import edu.yu.einstein.genplay.core.IO.utils.Extractors;
import edu.yu.einstein.genplay.core.IO.utils.StrandedExtractorOptions;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;


/**
 * A Eland Extended file extractor
 * @author Julien Lajugie
 */
public final class ElandExtendedExtractor extends TextFileExtractor implements SCWReader, StrandReader, StrandedExtractor {

	/** Default first base position of bed files. Eland files are 0-based */
	public static final int DEFAULT_FIRST_BASE_POSITION = 1;

	private int	firstBasePosition = DEFAULT_FIRST_BASE_POSITION;// position of the first base
	private StrandedExtractorOptions		strandOptions;		// options on the strand and read length / shift
	private Chromosome 						chromosome;		 	// chromosome of the last item read
	private Integer 						start;				// start position of the last item read
	private Integer 						stop;				// stop position of the last item read
	private Float 							score;				// score of the last item read
	private Strand							strand;				// strand of the last item read
	private final int[][] 					matchTypeCount; 	// number of lines with 0,1,2 mistakes per chromosome
	private int 							NMCount = 0;		// Non matched line count
	private int 							QCCount = 0;		// quality control line count
	private int 							multiMatchCount = 0;// multi-match line count


	/**
	 * Creates an instance of {@link ElandExtendedExtractor}
	 * @param dataFile file containing the data
	 * @throws FileNotFoundException if the specified file is not found
	 */
	public ElandExtendedExtractor(File dataFile) throws FileNotFoundException {
		super(dataFile);
		matchTypeCount = new int[getProjectChromosome().size()][3];
		for(short i = 0; i < getProjectChromosome().size(); i++) {
			for(short j = 0; j < 3; j++) {
				matchTypeCount[i][j] = 0;
			}
		}
	}


	@Override
	protected int extractDataLine(String extractedLine) throws DataLineException {
		chromosome = null;
		start = null;
		stop = null;
		score = null;
		byte[] line = extractedLine.getBytes();
		byte[] matchChar = new byte[4];
		byte[] chromoChar = new byte[64];
		byte[] positionChar = new byte[10];
		short match0MNumber, match1MNumber, match2MNumber, chromoNumber;

		if (line[0] == '\0') {
			throw new DataLineException("Null character found at the beginning of the line.");
		}

		// skip first field
		int i = 0;
		while (line[i] != '\t') {
			i++;
		}
		// skip second field
		i++;
		while (line[i] != '\t') {
			i++;
		}
		// try to extract the number of match 0M
		i++;
		int j = 0;
		while ((line[i] != '\t') && (line[i] != ':')) {
			matchChar[j] = line[i];
			i++;
			j++;
		}
		// case where we don't found a match
		if (line[i] == '\t') {
			if (matchChar[0] == 'N') {
				NMCount++;
			} else if (matchChar[0] == 'Q') {
				QCCount++;
			}
			throw new DataLineException("No match found for: " + matchChar[0]);
		}
		match0MNumber = Short.parseShort(new String(matchChar, 0, j));
		// try to extract the number of match 1M
		i++;
		j = 0;
		while (line[i] != ':') {
			matchChar[j] = line[i];
			i++;
			j++;
		}
		match1MNumber = Short.parseShort(new String(matchChar, 0, j));
		// try to extract the number of match 2M
		i++;
		j = 0;
		while (line[i] != '\t') {
			matchChar[j] = line[i];
			i++;
			j++;
		}
		match2MNumber = Short.parseShort(new String(matchChar, 0, j));
		// we only want lines that correspond to our criteria
		if ((match0MNumber + match1MNumber + match2MNumber) != 1) {
			multiMatchCount++;
			throw new DataLineException("The line does not match the criteria: " + match0MNumber + " + " + match1MNumber + " + " + match2MNumber + " != 1");
		}

		while ((i < line.length) && (line[i] != '.'))  {
			chromoChar[j] = line[i];
			i++;
			j++;
		}

		// if we reach the end of the line now there is no data to extract
		if (i == line.length) {
			throw new DataLineException("End of the line reached, no data to extract.");
		}

		// chromosome
		String chromosomeName = new String(chromoChar, 0, j).trim();
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

		chromoNumber = (short) getProjectChromosome().getIndex(chromosomeName);
		// try to extract the position number
		i+=4;  // we want to get rid of 'fa:'
		j = 0;
		while ((line[i] != 'F') && (line[i] != 'R')) {
			positionChar[j] = line[i];
			i++;
			j++;
		}
		// retrieve the strand
		char strandChar = (char) (line[i] & 0xFF); // because byte goes from -128 to 127 and char from 0 to 255
		strand = Strand.get(strandChar);
		if ((strand != null) && (strandOptions != null) && (!strandOptions.isSelected(strand))) {
			chromosome = null;
			return LINE_SKIPPED;
		}

		start = Extractors.getInt(new String(positionChar, 0, j));
		stop = start;

		String errors = DataLineValidator.getErrors(chromosome, start, stop);
		if (!errors.isEmpty()) {
			throw new DataLineException(errors);
		}

		// Stop position checking, must not be greater than the chromosome length
		String stopEndErrorMessage = DataLineValidator.getErrors(chromosome, stop);
		if (!stopEndErrorMessage.isEmpty()) {
			DataLineException stopEndException = new DataLineException(stopEndErrorMessage, DataLineException.SHRINK_STOP_PROCESS);
			// notify the listeners that the stop position needed to be shrunk
			notifyDataEventListeners(stopEndException, getCurrentLineNumber(), extractedLine);
			stop = chromosome.getLength();
		}

		// compute the read position with specified strand shift and read length
		if (strandOptions != null) {
			SimpleChromosomeWindow resultStartStop = strandOptions.computeStartStop(chromosome, start, stop, strand);
			start = resultStartStop.getStart();
			stop = resultStartStop.getStop();
		}

		// if we are in a multi-genome project, we compute the position on the meta genome
		start = getRealGenomePosition(chromosome, start);
		stop = getRealGenomePosition(chromosome, stop);

		// add data for the statistics
		matchTypeCount[chromoNumber][0] += match0MNumber;
		matchTypeCount[chromoNumber][1] += match1MNumber;
		matchTypeCount[chromoNumber][2] += match2MNumber;

		return ITEM_EXTRACTED;
	}


	@Override
	public Chromosome getChromosome() {
		return chromosome;
	}


	@Override
	public int getFirstBasePosition() {
		return firstBasePosition;
	}


	/**
	 * @return the number of lines with 0,1,2 mistakes per chromosome
	 */
	public int[][] getMatchTypeCount() {
		return matchTypeCount;
	}


	/**
	 * @return the count of multi-match lines
	 */
	public int getMultiMatchCount() {
		return multiMatchCount;
	}


	/**
	 * @return the count of non-matched lines
	 */
	public int getNMCount() {
		return NMCount;
	}


	/**
	 * @return the count of quality control lines
	 */
	public int getQCCount() {
		return QCCount;
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
	public void setFirstBasePosition(int firstBasePosition) {
		this.firstBasePosition = firstBasePosition;
	}


	@Override
	public void setStrandedExtractorOptions(StrandedExtractorOptions options) {
		strandOptions = options;
	}
}
