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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.extractor;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.enums.DataPrecision;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.enums.Strand;
import edu.yu.einstein.genplay.core.generator.BinListGenerator;
import edu.yu.einstein.genplay.core.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.arrayList.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;
import edu.yu.einstein.genplay.exception.InvalidDataLineException;
import edu.yu.einstein.genplay.util.Utils;


/**
 * A SAM file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public class SAMExtractor extends TextFileExtractor implements Serializable, StrandedExtractor, BinListGenerator {

	private static final long serialVersionUID = -1917159784796564734L; // generated ID
	private ChromosomeListOfLists<Integer>	positionList;				// list of position
	private Strand 							selectedStrand;	// strand to extract, null for both
	private int 							strandShift;	// value of the shift to perform on the selected strand


	/**
	 * Creates an instance of {@link SAMExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public SAMExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		positionList = new ChromosomeArrayListOfLists<Integer>();
		for (int i = 0; i < chromosomeManager.size(); i++) {
			positionList.add(new IntArrayAsIntegerList());
		}
	}


	@Override
	protected boolean extractLine(String line) throws InvalidDataLineException {
		// if the line starts with @ it's header line so we skip it
		if (line.trim().charAt(0) != '@') {
			String[] splitedLine = Utils.parseLineTabOnly(line);
			try {
				Chromosome chromosome = chromosomeManager.get(splitedLine[2]);
				// checks if we need to extract the data on the chromosome
				int chromosomeStatus = checkChromosomeStatus(chromosome);
				if (chromosomeStatus == AFTER_LAST_SELECTED) {
					return true;
				} else if (chromosomeStatus != NEED_TO_BE_SKIPPED) {
					Strand strand = null;
					int flag = Integer.parseInt(splitedLine[1].trim());
					boolean isReversedRead = (flag & 0x10) == 0x10;
					if (isReversedRead) {
						strand = Strand.THREE;
					} else {
						strand = Strand.FIVE;
					}
					if ((strand == null) || (isStrandSelected(strand))) {
						int position = Integer.parseInt(splitedLine[3]);
						// we subtract 1 to the position because sam file position  
						// are 1 base and genplay is 0 based
						position--;
						positionList.get(chromosome).add(position);		
						lineCount++;
					}
					return false;
				}
			} catch (InvalidChromosomeException e) {
				return false;
			}
		}
		return false;
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
	public BinList toBinList(int binSize, DataPrecision precision, ScoreCalculationMethod method) 
	throws IllegalArgumentException, InterruptedException, ExecutionException {
		return new BinList(binSize, precision, positionList);
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
	public int getShiftedPosition(Strand strand, Chromosome chromosome, int position) {
		if (strand == null) {
			return position;
		} else if (strand == Strand.FIVE) {
			return Math.min(chromosome.getLength(), position + strandShift);
		} else {
			return Math.max(0, position - strandShift);
		}
	}


	@Override
	public void setStrandShift(int shiftValue) {
		strandShift = shiftValue; 
	}
}
