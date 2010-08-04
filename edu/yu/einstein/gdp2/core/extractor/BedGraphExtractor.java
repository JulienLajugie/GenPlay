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
	 */
	public BedGraphExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		// initialize the lists
		startList = new ChromosomeArrayListOfLists<Integer>();
		stopList = new ChromosomeArrayListOfLists<Integer>();
		scoreList = new ChromosomeArrayListOfLists<Double>();
		// initialize the sublists
		for (int i = 0; i < chromosomeManager.size(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			scoreList.add(new DoubleArrayAsDoubleList());
		}
	}


	/**
	 * Receives one line from the input file and extracts and adds 
	 * a chromosome, a position start, a position stop and a score to the lists.
	 * @param Extractedline line read from the data file 
	 * @return true when the extraction is done
	 * @throws ManagerDataNotLoadedException 
	 * @throws InvalidDataLineException 
	 */
	@Override
	protected boolean extractLine(String extractedLine) throws InvalidDataLineException {
		String[] splitedLine = extractedLine.split("\t");
		if (splitedLine.length < 4) {
			throw new InvalidDataLineException(extractedLine);
		}
		try {
			Chromosome chromosome = chromosomeManager.get(splitedLine[0]) ;
			int chromosomeStatus = checkChromosomeStatus(chromosome);
			if (chromosomeStatus == AFTER_LAST_SELECTED) {
				return true;
			} else if (chromosomeStatus == NEED_TO_BE_SKIPPED) {
				return false;
			} else {
				int start = Integer.parseInt(splitedLine[1].trim());
				int stop = Integer.parseInt(splitedLine[2].trim());
				double score = Double.parseDouble(splitedLine[3].trim());
				if (score != 0) {
					startList.add(chromosome, start);
					stopList.add(chromosome, stop);
					scoreList.add(chromosome, score);
					lineCount++;
				}
				return false;
			}
		} catch (InvalidChromosomeException e) {
			throw new InvalidDataLineException(extractedLine);
		}
	}


	@Override
	public BinList toBinList(int binSize, DataPrecision precision, ScoreCalculationMethod method) throws IllegalArgumentException, InterruptedException, ExecutionException {
		return new BinList(binSize, precision, method, startList, stopList, scoreList);
	}


	@Override
	public ScoredChromosomeWindowList toScoredChromosomeWindowList(ScoreCalculationMethod scm) throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return new ScoredChromosomeWindowList(startList, stopList, scoreList, scm);
	}


	@Override
	public ChromosomeWindowList toChromosomeWindowList() throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return new ChromosomeWindowList(startList, stopList);
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
	public boolean overlapped() {
		return ScoredChromosomeWindowList.overLappingExist(startList, stopList);
	}
}
