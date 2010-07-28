/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.extractor;


import java.io.File;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.generator.BinListGenerator;
import yu.einstein.gdp2.core.generator.ChromosomeWindowListGenerator;
import yu.einstein.gdp2.core.generator.ScoredChromosomeWindowListGenerator;
import yu.einstein.gdp2.core.list.ChromosomeArrayListOfLists;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.arrayList.DoubleArrayAsDoubleList;
import yu.einstein.gdp2.core.list.arrayList.IntArrayAsIntegerList;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.InvalidDataLineException;

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
	private int 			binSize = -1;					// size of the bin (only used if constant through the entire file)

	private Boolean 		isStepUnique = null;			// true if the bin size is constant through the entire file

	
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
		for (int i = 0; i < chromosomeManager.size(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			scoreList.add(new DoubleArrayAsDoubleList());
		}
	}


	@Override
	protected void extractLine(String line) throws InvalidDataLineException {
		String[] splittedLine = line.split(" ");

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
					currentChromo = chromosomeManager.get(chromStr.trim());
				} catch (InvalidChromosomeException e) {
					throw new InvalidDataLineException(line);
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
				if (isFixedStep) {
					double score = Double.parseDouble(splittedLine[i]); 
					try {
						if (score != 0) {
							startList.add(currentChromo, currentPosition);
							stopList.add(currentChromo, currentPosition + currentSpan);
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
							if (score != 0) {
								startList.add(currentChromo, currentPosition);
								stopList.add(currentChromo, currentPosition + currentSpan);
								scoreList.add(currentChromo, score);
							}
							lineCount++;
						} catch (Exception e) {
							throw new InvalidDataLineException(line);
						}		
					}
				}			
			}
			i++;
		}
	}

	
	/**
	 * @return if the step is constant through the entire file
	 */
	private boolean checkIfStepIsUnique() {
		for (short i = 0; i < startList.size(); i++) {
			for (int j = 0; j < startList.size(i); j++) {
				if (binSize == -1) {
					binSize = stopList.get(i, j) - startList.get(i, j); 
				} else {
					int currentBinsize = stopList.get(i, j) - startList.get(i, j);
					// if the size of a window not always the same
					// or if the start is not a multiple of the bin size
					// the step is not unique
					if (currentBinsize != binSize) {
						return false;
					}
					if (startList.get(i, j) % binSize != 0) {
						return false;
					}
				}
			}
		}
		return true;
	}
	

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
		if (isStepUnique == null) {
			isStepUnique = checkIfStepIsUnique();
		}
		return !isStepUnique;
	}


	@Override
	public boolean isCriterionNeeded() {
		if (isStepUnique == null) {
			isStepUnique = checkIfStepIsUnique();
		}
		return !isStepUnique;
	}
	
	
	@Override
	public boolean isPrecisionNeeded() {
		return true;
	}


	@Override
	public BinList toBinList(int aBinSize, DataPrecision precision, ScoreCalculationMethod method) throws IllegalArgumentException, InterruptedException, ExecutionException {
		if (isStepUnique == null) {
			isStepUnique = checkIfStepIsUnique();
		}
		if (isStepUnique) {
			return new BinList(binSize, precision, startList, scoreList);
		} else {
			return new BinList(aBinSize, precision, method, startList, stopList, scoreList);
		}
	}
	
	
	@Override
	public boolean overlapped() {
		return ScoredChromosomeWindowList.overLappingExist(startList, stopList);
	}
}
