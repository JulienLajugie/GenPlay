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
import yu.einstein.gdp2.core.list.ChromosomeArrayListOfLists;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.arrayList.DoubleArrayAsDoubleList;
import yu.einstein.gdp2.core.list.arrayList.IntArrayAsIntegerList;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.InvalidDataLineException;


/**
 * A pair file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PairExtractor extends TextFileExtractor
implements Serializable, BinListGenerator {


	private static final long serialVersionUID = -2160273514926102255L; // generated ID

	private ChromosomeListOfLists<Integer>	positionList;		// list of position start
	private ChromosomeListOfLists<Double>	scoreList;			// list of scores


	/**
	 * Creates an instance of {@link PairExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public PairExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		positionList = new ChromosomeArrayListOfLists<Integer>();
		scoreList = new ChromosomeArrayListOfLists<Double>();
		// initialize the sublists
		for (int i = 0; i < chromosomeManager.size(); i++) {
			positionList.add(new IntArrayAsIntegerList());
			scoreList.add(new DoubleArrayAsDoubleList());
		}
	}


	/**
	 * Receives one line from the input file and extracts and adds the data in the lists
	 * @param extractedLine line read from the data file
	 * @return true when the extraction is done
	 * @throws InvalidDataLineException 
	 */
	@Override
	protected boolean extractLine(String extractedLine) throws InvalidDataLineException {
		if (extractedLine.trim().length() == 0) {
			return false;
		}
		// We don't want to extract the header lines
		// So we extract only if the line starts with a number
		try {
			Integer.parseInt(extractedLine.substring(0, 1));
		} catch (Exception e){
			return false;
		}

		String[] splitedLine = extractedLine.split("\t");
		if (splitedLine.length < 10) {
			throw new InvalidDataLineException(extractedLine);
		}
		String chromosomeField[] = splitedLine[2].split(":");
		if (chromosomeField.length != 2) {
			throw new InvalidDataLineException(extractedLine);
		}
		try {
			Chromosome chromosome = chromosomeManager.get(chromosomeField[0]);
			// checks if we need to extract the data on the chromosome
			int chromosomeStatus = checkChromosomeStatus(chromosome);
			if (chromosomeStatus == AFTER_LAST_SELECTED) {
				return true;
			} else if (chromosomeStatus == NEED_TO_BE_SKIPPED) {
				return false;
			} else {
				positionList.add(chromosome, Integer.parseInt(splitedLine[4]));
				scoreList.add(chromosome, Double.parseDouble(splitedLine[9]));
				lineCount++;
				return false;
			}
		} catch (InvalidChromosomeException e) {
			throw new InvalidDataLineException(extractedLine);
		}
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
	public BinList toBinList(int binSize, DataPrecision precision, ScoreCalculationMethod method) throws IllegalArgumentException, InterruptedException, ExecutionException {
		return new BinList(binSize, precision, method, positionList, scoreList);
	}
}
