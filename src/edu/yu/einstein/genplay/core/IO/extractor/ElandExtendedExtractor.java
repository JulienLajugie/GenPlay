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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.generator.BinListGenerator;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.DataPrecision;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.list.GenomicDataArrayList;
import edu.yu.einstein.genplay.dataStructure.list.GenomicDataList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.DoubleArrayAsDoubleList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.dataStructure.list.binList.BinList;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;



/**
 * A Eland Extended file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ElandExtendedExtractor extends TextFileExtractor implements Serializable, StrandedExtractor, BinListGenerator {

	private static final long serialVersionUID = 8952410963820358882L;	// generated ID

	private final GenomicDataList<Integer>	positionList;		// list of position
	private GenomicDataList<Integer>	stopPositionList;	// list of stop position. Only used when a read length is specified
	private GenomicDataList<Double>	scoreList;			// list of score. Only used when a read length is specified
	private final GenomicDataList<Strand>	strandList;			// list of strand
	private final int[][] 						matchTypeCount; 	// number of lines with 0,1,2 mistakes per chromosome
	private int 							NMCount = 0;		// Non matched line count
	private int 							QCCount = 0;		// quality control line count
	private int 							multiMatchCount = 0;// multi-match line count
	private Strand 							selectedStrand;		// strand to extract, null for both
	private ReadLengthAndShiftHandler		readHandler;		// handler that computes the position of read by applying the shift


	/**
	 * Creates an instance of {@link ElandExtendedExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public ElandExtendedExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		positionList = new GenomicDataArrayList<Integer>();
		strandList = new GenomicDataArrayList<Strand>();
		for (int i = 0; i < projectChromosome.size(); i++) {
			positionList.add(new IntArrayAsIntegerList());
			strandList.add(new ArrayList<Strand>());
		}
		matchTypeCount = new int[projectChromosome.size()][3];
		for(short i = 0; i < projectChromosome.size(); i++) {
			for(short j = 0; j < 3; j++) {
				matchTypeCount[i][j] = 0;
			}
		}
	}


	@Override
	protected void logExecutionInfo() {
		super.logExecutionInfo();
		// display statistics
		if(logFile != null) {
			try {
				// initialize the number of read per chromosome and the data for statistics
				int total0M = 0, total1M = 0, total2M = 0;
				BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
				NumberFormat nf = NumberFormat.getInstance();
				writer.write("NM: " + NMCount);
				writer.newLine();
				writer.write("Percentage of NM: " + nf.format(((double)NMCount / totalCount) * 100) + "%");
				writer.newLine();
				writer.write("QC: " + QCCount);
				writer.newLine();
				writer.write("Percentage of QC: " + nf.format(((double)QCCount / totalCount) * 100) + "%");
				writer.newLine();
				writer.write("Multi match: " + multiMatchCount);
				writer.newLine();
				writer.write("Percentage of multimatch: " + nf.format(((double)multiMatchCount / totalCount) * 100) + "%");
				writer.newLine();
				writer.write("Chromosome\t0MM\t1MM\t2MM\tTotal");
				writer.newLine();
				for(short i = 0; i < projectChromosome.size(); i++) {
					writer.write(projectChromosome.get(i) +
							"\t\t" + nf.format(((double)matchTypeCount[i][0] / lineCount)*100) +
							"%\t" + nf.format(((double)matchTypeCount[i][1] / lineCount)*100) +
							"%\t" + nf.format(((double)matchTypeCount[i][2] / lineCount)*100) +
							"%\t" + nf.format(((double)(matchTypeCount[i][0] + matchTypeCount[i][1] + matchTypeCount[i][2]) / lineCount)*100) + "%");
					writer.newLine();
					total0M+=matchTypeCount[i][0];
					total1M+=matchTypeCount[i][1];
					total2M+=matchTypeCount[i][2];
				}
				writer.write("Total:\t" + nf.format(((double)total0M/lineCount)*100) +
						"%\t" + nf.format(((double)total1M/lineCount)*100) +
						"%\t" + nf.format(((double)total2M/lineCount)*100) +
						"%\t\t100%");
				writer.newLine();
				writer.close();
			} catch (IOException e) {

			}
		}
	}


	@Override
	protected boolean extractLine(String extractedLine) throws DataLineException {
		byte[] line = extractedLine.getBytes();
		byte[] matchChar = new byte[4];
		byte[] chromoChar = new byte[64];
		byte[] positionChar = new byte[10];
		short match0MNumber, match1MNumber, match2MNumber, chromoNumber;
		Chromosome chromo;
		int positionNumber;

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
		try {
			chromo = projectChromosome.get(new String(chromoChar, 0, j).trim());
		} catch (InvalidChromosomeException e) {
			return false;
		}
		// checks if we need to extract the data on the chromosome
		int chromosomeStatus = checkChromosomeStatus(chromo);
		if (chromosomeStatus == AFTER_LAST_SELECTED) {
			return true;
		} else if (chromosomeStatus == NEED_TO_BE_SKIPPED) {
			return false;
		} else {
			chromoNumber = projectChromosome.getIndex(chromo);

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
			Strand strand = Strand.get(strandChar);
			if (isStrandSelected(strand)) {
				positionNumber = getInt(new String(positionChar, 0, j));
				if (positionNumber <= chromo.getLength()) {
					// add data for the statistics
					matchTypeCount[chromoNumber][0] += match0MNumber;
					matchTypeCount[chromoNumber][1] += match1MNumber;
					matchTypeCount[chromoNumber][2] += match2MNumber;
					// add the data
					strandList.add(chromo, strand);
					// compute the read position with specified strand shift and read length
					positionNumber = getMultiGenomePosition(chromo, positionNumber);
					if (readHandler != null) {
						SimpleChromosomeWindow resultStartStop = readHandler.computeStartStop(chromo, positionNumber, positionNumber, strand);
						positionNumber = resultStartStop.getStart();
						// if a read length is specified we need to add a stop position
						if (readHandler.getReadLength() != 0) {
							int stop = resultStartStop.getStop();
							stop = getMultiGenomePosition(chromo, stop);
							stopPositionList.add(chromo, stop);
							// TODO: add a BinList constructor that doesn't need
							// as score list so we don't need the useless next line
							scoreList.add(chromo, 1.0);
						}
					}
				}
				positionList.add(chromo, positionNumber);
				lineCount++;
			}
			return false;
		}
	}


	@Override
	public boolean isBinSizeNeeded() {
		return true;
	}


	@Override
	public boolean isCriterionNeeded() {
		return false;
	}


	@Override
	public boolean isPrecisionNeeded() {
		return true;
	}


	@Override
	public BinList toBinList(int binSize, DataPrecision precision, ScoreCalculationMethod method) throws IllegalArgumentException, InterruptedException, ExecutionException {
		// case where a read length is specified
		if ((readHandler != null) && (readHandler.getReadLength() != 0)) {
			return new BinList(binSize, precision, ScoreCalculationMethod.SUM, positionList, stopPositionList, scoreList);
		} else { // case where there is no specified read length
			return new BinList(binSize, precision, positionList);
		}
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
		if (readHandler.getReadLength() != 0) {
			// if a read length is specified we need to have a stop position list
			stopPositionList = new GenomicDataArrayList<Integer>();
			scoreList = new GenomicDataArrayList<Double>();
			for (int i = 0; i < projectChromosome.size(); i++) {
				stopPositionList.add(new IntArrayAsIntegerList());
				scoreList.add(new DoubleArrayAsDoubleList());
			}
		}
	}

}
