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

import edu.yu.einstein.genplay.core.SNPList.SNPList;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.Nucleotide;
import edu.yu.einstein.genplay.core.extractor.utils.DataLineValidator;
import edu.yu.einstein.genplay.core.generator.SNPListGenerator;
import edu.yu.einstein.genplay.core.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.arrayList.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.exception.DataLineException;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.Utils;



/**
 * Extracts the SOAP SNP files
 * @author Julien Lajugie
 * @version 0.1
 */
public class SOAPsnpExtractor extends TextFileExtractor implements Serializable, SNPListGenerator {

	private static final long serialVersionUID = -7942385011427936L;	// generated ID
	private final ChromosomeListOfLists<Integer>	positionList;		// list of extracted position
	private final ChromosomeListOfLists<Nucleotide> firstBaseList;		// list of first base
	private final ChromosomeListOfLists<Integer> 	firstBaseCountList;	// list of first base count
	private final ChromosomeListOfLists<Nucleotide> secondBaseList;		// list of second base
	private final ChromosomeListOfLists<Integer> 	secondBaseCountList;// list of second base count
	private final ChromosomeListOfLists<Boolean> 	isSecondBaseSignificantList; // true if the second base is significant


	/**
	 * Creates an instance of {@link SOAPsnpExtractor}
	 * @param dataFile
	 * @param logFile
	 */
	public SOAPsnpExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		// initialize the lists
		positionList = new ChromosomeArrayListOfLists<Integer>();
		firstBaseList = new ChromosomeArrayListOfLists<Nucleotide>();
		firstBaseCountList = new ChromosomeArrayListOfLists<Integer>();
		secondBaseList = new ChromosomeArrayListOfLists<Nucleotide>();
		secondBaseCountList = new ChromosomeArrayListOfLists<Integer>();
		isSecondBaseSignificantList = new ChromosomeArrayListOfLists<Boolean>();
		// initialize the sublists
		for (int i = 0; i < projectChromosome.size(); i++) {
			positionList.add(new IntArrayAsIntegerList());
			firstBaseList.add(new ArrayList<Nucleotide>());
			firstBaseCountList.add(new IntArrayAsIntegerList());
			secondBaseList.add(new ArrayList<Nucleotide>());
			secondBaseCountList.add(new IntArrayAsIntegerList());
			isSecondBaseSignificantList.add(new ArrayList<Boolean>());
		}
	}


	@Override
	protected boolean extractLine(String line) throws DataLineException {
		String[] splitedLine = Utils.parseLineTabOnly(line);
		try {
			int chromosomeStatus;
			Chromosome chromosome = null;
			try {
				chromosome = projectChromosome.get(splitedLine[0].trim()) ;
				chromosomeStatus = checkChromosomeStatus(chromosome);
			} catch (InvalidChromosomeException e) {
				chromosomeStatus = NEED_TO_BE_SKIPPED;
			}

			if (chromosomeStatus == AFTER_LAST_SELECTED) {
				return true;
			} else if (chromosomeStatus == NEED_TO_BE_SKIPPED) {
				return false;
			} else {
				int position = getInt(splitedLine[1].trim()) - 1;

				Nucleotide consensusGenotype = Nucleotide.get(splitedLine[3].trim().charAt(0));
				Nucleotide firstBase = Nucleotide.get(splitedLine[5].trim().charAt(0));
				int firstBaseCount = getInt(splitedLine[7].trim());
				Nucleotide secondBase = Nucleotide.get(splitedLine[9].trim().charAt(0));
				int secondBaseCount = getInt(splitedLine[11].trim());
				boolean isSecondBaseSignificant = !consensusGenotype.equals(firstBase);

				// Checks errors
				String errors = DataLineValidator.getErrors(chromosome, position);
				if (errors.length() == 0) {
					position = getMultiGenomePosition(chromosome, position);
					positionList.add(chromosome, position);
					firstBaseList.add(chromosome, firstBase);
					firstBaseCountList.add(chromosome, firstBaseCount);
					secondBaseList.add(chromosome, secondBase);
					secondBaseCountList.add(chromosome, secondBaseCount);
					isSecondBaseSignificantList.add(chromosome, isSecondBaseSignificant);
					lineCount++;
				} else {
					throw new DataLineException(errors);
				}
				return false;
			}
		} catch (InvalidChromosomeException e) {
			//throw new InvalidDataLineException(line);
			throw new DataLineException(DataLineException.INVALID_FORMAT_NUMBER);
		}
	}


	@Override
	public SNPList toSNPList() throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return new SNPList(null, positionList, firstBaseList, firstBaseCountList, secondBaseList, secondBaseCountList, isSecondBaseSignificantList);
	}

}
