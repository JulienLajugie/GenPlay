/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.extractor;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.ChromosomeArrayListOfLists;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.BinListGenerator;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.InvalidDataLineException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;
import yu.einstein.gdp2.util.ChromosomeManager;


/**
 * A pair file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PairExtractor extends Extractor
implements Serializable, BinListGenerator {


	private static final long serialVersionUID = -2160273514926102255L; // generated ID

	private ChromosomeListOfLists<Integer>	positionList;		// list of position start
	private ChromosomeListOfLists<Double>	scoreList;			// list of scores


	/**
	 * Creates an instance of {@link PairExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 * @param chromosomeManager a {@link ChromosomeManager}
	 */
	public PairExtractor(File dataFile, File logFile, ChromosomeManager chromosomeManager) {
		super(dataFile, logFile, chromosomeManager);
		positionList = new ChromosomeArrayListOfLists<Integer>(chromosomeManager);
		scoreList = new ChromosomeArrayListOfLists<Double>(chromosomeManager);
		// initialize the sublists
		for (int i = 0; i < chromosomeManager.chromosomeCount(); i++) {
			positionList.add(new ArrayList<Integer>());
			scoreList.add(new ArrayList<Double>());
		}
	}


	/**
	 * Receives one line from the input file and extracts and adds the data in the lists
	 * @param extractedLine line read from the data file  
	 * @throws ManagerDataNotLoadedException 
	 * @throws InvalidDataLineException 
	 */
	@Override
	protected void extractLine(String extractedLine) throws ManagerDataNotLoadedException, InvalidDataLineException {
		if (extractedLine.trim().length() == 0) {
			return;
		}
		// We don't want to extract the header lines
		// So we extract only if the line starts with a number
		try {
			Integer.parseInt(extractedLine.substring(0, 1));
		} catch (Exception e){
			return;
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
			Chromosome chromosome = chromosomeManager.getChromosome(chromosomeField[0]);
			positionList.add(chromosome, Integer.parseInt(splitedLine[4]));
			scoreList.add(chromosome, Double.parseDouble(splitedLine[9]));
			lineCount++;
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
	public BinList toBinList(int binSize, DataPrecision precision, ScoreCalculationMethod method) throws IllegalArgumentException {
		return new BinList(chromosomeManager, binSize, precision, method, positionList, scoreList);
	}
}
