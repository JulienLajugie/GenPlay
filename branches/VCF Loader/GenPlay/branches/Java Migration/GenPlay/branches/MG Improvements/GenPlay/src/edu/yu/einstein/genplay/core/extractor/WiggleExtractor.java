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
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.DataPrecision;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.generator.BinListGenerator;
import edu.yu.einstein.genplay.core.generator.ChromosomeWindowListGenerator;
import edu.yu.einstein.genplay.core.generator.ScoredChromosomeWindowListGenerator;
import edu.yu.einstein.genplay.core.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.arrayList.DoubleArrayAsDoubleList;
import edu.yu.einstein.genplay.core.list.arrayList.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.chromosomeWindowList.ChromosomeWindowList;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;
import edu.yu.einstein.genplay.exception.InvalidDataLineException;
import edu.yu.einstein.genplay.util.Utils;


/**
 * A Wiggle file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public final class WiggleExtractor extends TextFileExtractor 
implements Serializable, ChromosomeWindowListGenerator, ScoredChromosomeWindowListGenerator, BinListGenerator{

	private static final long serialVersionUID = 3397954112622122744L; // generated ID

	private ChromosomeListOfLists<Integer>	startList;		// list of position start
	private ChromosomeListOfLists<Integer>	stopList;		// list of position stop
	private ChromosomeListOfLists<Double>	scoreList;		// list of scores

	private Chromosome 		currentChromo;					// last chromosome specified
	private int 			currentSpan;					// last span specified
	private int 			currentStep;					// last step specified
	private int 			currentPosition;				// current position
	private boolean 		isFixedStep = false;			// true if we are extrating a fixedStep line
	//	private int 			binSize = -1;					// size of the bin (only used if constant through the entire file)
	//
	//	private Boolean 		isStepUnique = null;			// true if the bin size is constant through the entire file


	/**
	 * Creates an instance of {@link WiggleExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public WiggleExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		startList = new ChromosomeArrayListOfLists<Integer>();
		stopList = new ChromosomeArrayListOfLists<Integer>();
		scoreList = new ChromosomeArrayListOfLists<Double>();
		for (int i = 0; i < projectChromosome.size(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			scoreList.add(new DoubleArrayAsDoubleList());
		}
	}


	@Override
	protected boolean extractLine(String line) throws InvalidDataLineException {
		String[] splittedLine = Utils.parseLineTabAndSpace(line);

		int i = 0;
		while (i < splittedLine.length) {
			String currentField = splittedLine[i].trim();
			if (currentField.equalsIgnoreCase("variableStep")) {
				// a variableStep must at least contain 2 elements
				if (splittedLine.length < 2) {
					throw new InvalidDataLineException(line);
				} else {
					isFixedStep = false;
					currentSpan = 1;
					totalCount--; // not a data line
				}
			} else if (currentField.equalsIgnoreCase("fixedStep")) {
				// a fixedStep must at least contain 4 elements
				if (splittedLine.length < 4) {
					throw new InvalidDataLineException(line);
				} else {
					isFixedStep = true;
					currentSpan = 1;
					totalCount--; // not a data line
				}
			} else if ((currentField.length() > 6) && (currentField.substring(0, 6).equalsIgnoreCase("chrom="))) {
				// retrieve chromosome
				String chromStr = splittedLine[i].trim().substring(6);
				try {
					currentChromo = projectChromosome.get(chromStr.trim());
				} catch (InvalidChromosomeException e) {
					currentChromo = null;
					throw new InvalidDataLineException(line);
				}
				// check if the extraction is done
				if (checkChromosomeStatus(currentChromo) == AFTER_LAST_SELECTED) {
					return true;
				}
			} else if ((currentField.length() > 6) && (currentField.substring(0, 6).equalsIgnoreCase("start="))) {
				// retrieve start position
				String posStr = splittedLine[i].trim().substring(6);
				currentPosition = Integer.parseInt(posStr);
			} else if ((currentField.length() > 5) && (currentField.substring(0, 5).equalsIgnoreCase("step="))) {
				// retrieve step position
				String stepStr = splittedLine[i].trim().substring(5);
				currentStep = Integer.parseInt(stepStr);
			} else if ((currentField.length() > 5) && (currentField.substring(0, 5).equalsIgnoreCase("span="))) {
				// retrieve span
				String spanStr = splittedLine[i].trim().substring(5);
				currentSpan = Integer.parseInt(spanStr);				
			} else {
				if (currentChromo != null) {
					if (isFixedStep) {
						double score = Double.parseDouble(splittedLine[i]); 
						try {
							if ((score != 0) && (checkChromosomeStatus(currentChromo) == NEED_TO_BE_EXTRACTED)) {
								startList.add(currentChromo, getMultiGenomePosition(currentChromo, currentPosition));
								stopList.add(currentChromo, getMultiGenomePosition(currentChromo, currentPosition + currentSpan));
								scoreList.add(currentChromo, score);
							}
							lineCount++;
							currentPosition += currentStep;
						} catch (Exception e) {
							throw new InvalidDataLineException(line);
						}					
					} else {
						if (splittedLine.length < 2) {
							throw new InvalidDataLineException(line);
						} else {
							currentPosition = Integer.parseInt(splittedLine[i].trim());
							double score = Double.parseDouble(splittedLine[i + 1]);
							i++;
							try {
								if ((score != 0) && (checkChromosomeStatus(currentChromo) == NEED_TO_BE_EXTRACTED)) {
									startList.add(currentChromo, getMultiGenomePosition(currentChromo, currentPosition));
									stopList.add(currentChromo, getMultiGenomePosition(currentChromo, currentPosition + currentSpan));
									scoreList.add(currentChromo, score);
								}
								lineCount++;
							} catch (Exception e) {
								throw new InvalidDataLineException(line);
							}		
						}
					}			
				}
			}
			i++;
		}
		return false;
	}


	//	/**
	//	 * @return if the step is constant through the entire file
	//	 */
	//	private boolean checkIfStepIsUnique() {
	//		for (short i = 0; i < startList.size(); i++) {
	//			for (int j = 0; j < startList.size(i); j++) {
	//				if (binSize == -1) {
	//					binSize = stopList.get(i, j) - startList.get(i, j);
	//				} else {
	//					int currentBinsize = stopList.get(i, j) - startList.get(i, j);
	//					// if the size of a window not always the same
	//					// or if the start is not a multiple of the bin size
	//					// the step is not unique
	//					if (currentBinsize != binSize) {
	//						return false;
	//					}
	//					if (startList.get(i, j) % binSize != 0) {
	//						return false;
	//					}
	//				}
	//			}
	//		}
	//		return true;
	//	}


	@Override
	public ChromosomeWindowList toChromosomeWindowList() throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return new ChromosomeWindowList(startList, stopList);
	}


	@Override
	public ScoredChromosomeWindowList toScoredChromosomeWindowList(ScoreCalculationMethod scm) throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return new ScoredChromosomeWindowList(startList, stopList, scoreList, scm);
	}


	@Override
	public boolean isBinSizeNeeded() {
		//		if (isStepUnique == null) {
		//			isStepUnique = checkIfStepIsUnique();
		//		}
		//		return !isStepUnique;
		return true;
	}


	@Override
	public boolean isCriterionNeeded() {
		//		if (isStepUnique == null) {
		//			isStepUnique = checkIfStepIsUnique();
		//		}
		//		return !isStepUnique;
		return true;
	}


	@Override
	public boolean isPrecisionNeeded() {
		return true;
	}


	@Override
	public BinList toBinList(int aBinSize, DataPrecision precision, ScoreCalculationMethod method) throws IllegalArgumentException, InterruptedException, ExecutionException {
		//		if (isStepUnique == null) {
		//			isStepUnique = checkIfStepIsUnique();
		//		}
		//		if (isStepUnique) {
		//			return new BinList(binSize, precision, startList, scoreList);
		//		} else {
		return new BinList(aBinSize, precision, method, startList, stopList, scoreList);
		//		}
	}


	@Override
	public boolean overlapped() {
		return ScoredChromosomeWindowList.overLappingExist(startList, stopList);
	}
	
	
	/**
	 * We raise a new UnsupportedOperationException because it's not possible to load
	 * a random fraction of a wiggle file
	 */
	@Override
	public void setRandomLineCount(Integer randomLineCount) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Wiggle files need to be entirely extracted");		
	}
}
