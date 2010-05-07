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
import yu.einstein.gdp2.core.list.ChromosomeArrayListOfLists;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowListGenerator;
import yu.einstein.gdp2.core.list.arrayList.DoubleArrayAsDoubleList;
import yu.einstein.gdp2.core.list.arrayList.IntArrayAsIntegerList;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.BinListGenerator;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowListGenerator;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.InvalidDataLineException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;
import yu.einstein.gdp2.util.ChromosomeManager;

/**
 * A bedGraph file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BedGraphExtractor extends TextFileExtractor 
implements Serializable, ChromosomeWindowListGenerator, ScoredChromosomeWindowListGenerator, BinListGenerator {

	private static final long serialVersionUID = 7106474719716124894L; // generated ID
	private ChromosomeListOfLists<Integer>	startList;		// list of position start
	private ChromosomeListOfLists<Integer>	stopList;		// list of position stop
	private ChromosomeListOfLists<Double>	scoreList;		// list of scores


	/**
	 * Creates an instance of a {@link BedGraphExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 * @param chromosomeManager a {@link ChromosomeManager}
	 */
	public BedGraphExtractor(File dataFile, File logFile, ChromosomeManager chromosomeManager) {
		super(dataFile, logFile, chromosomeManager);
		// initialize the lists
		startList = new ChromosomeArrayListOfLists<Integer>(chromosomeManager);
		stopList = new ChromosomeArrayListOfLists<Integer>(chromosomeManager);
		scoreList = new ChromosomeArrayListOfLists<Double>(chromosomeManager);
		// initialize the sublists
		for (int i = 0; i < chromosomeManager.chromosomeCount(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			scoreList.add(new DoubleArrayAsDoubleList());
		}
	}


	/**
	 * Receives one line from the input file and extracts and adds 
	 * a chromosome, a position start, a position stop and a score to the lists.
	 * @param Extractedline line read from the data file  
	 * @throws ManagerDataNotLoadedException 
	 * @throws InvalidDataLineException 
	 */
	@Override
	protected void extractLine(String extractedLine) throws ManagerDataNotLoadedException, InvalidDataLineException {
		String[] splitedLine = extractedLine.split("\t");
		if (splitedLine.length < 4) {
			throw new InvalidDataLineException(extractedLine);
		}
		try {
			Chromosome chromosome = chromosomeManager.getChromosome(splitedLine[0]) ;
			int start = Integer.parseInt(splitedLine[1].trim());
			int stop = Integer.parseInt(splitedLine[2].trim());
			double score = Double.parseDouble(splitedLine[3].trim());
			if (score != 0) {
				startList.add(chromosome, start);
				stopList.add(chromosome, stop);
				scoreList.add(chromosome, score);
				lineCount++;
			}
		} catch (InvalidChromosomeException e) {
			throw new InvalidDataLineException(extractedLine);
		}
	}


	@Override
	public BinList toBinList(int binSize, DataPrecision precision, ScoreCalculationMethod method) throws IllegalArgumentException, InterruptedException, ExecutionException {
		return new BinList(chromosomeManager, binSize, precision, method, startList, stopList, scoreList);
	}


	@Override
	public ScoredChromosomeWindowList toScoredChromosomeWindowList() throws ManagerDataNotLoadedException, InvalidChromosomeException {
		return new ScoredChromosomeWindowList(chromosomeManager, startList, stopList, scoreList);
	}


	@Override
	public ChromosomeWindowList toChromosomeWindowList() throws ManagerDataNotLoadedException, InvalidChromosomeException {
		return new ChromosomeWindowList(chromosomeManager, startList, stopList);
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
}
