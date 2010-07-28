/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.extractor;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.enums.Strand;
import yu.einstein.gdp2.core.generator.BinListGenerator;
import yu.einstein.gdp2.core.generator.ChromosomeWindowListGenerator;
import yu.einstein.gdp2.core.generator.RepeatFamilyListGenerator;
import yu.einstein.gdp2.core.generator.ScoredChromosomeWindowListGenerator;
import yu.einstein.gdp2.core.list.ChromosomeArrayListOfLists;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.arrayList.DoubleArrayAsDoubleList;
import yu.einstein.gdp2.core.list.arrayList.IntArrayAsIntegerList;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.list.repeatFamilyList.RepeatFamilyList;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.InvalidDataLineException;


/**
 * A GFF file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GFFExtractor extends TextFileExtractor 
implements Serializable, RepeatFamilyListGenerator, ChromosomeWindowListGenerator, 
ScoredChromosomeWindowListGenerator, BinListGenerator {

	private static final long serialVersionUID = -2798372250708609794L; // generated ID

	private ChromosomeListOfLists<Integer>	startList;		// list of position start
	private ChromosomeListOfLists<Integer>	stopList;		// list of position stop
	private ChromosomeListOfLists<String> 	nameList;		// list of name
	private ChromosomeListOfLists<Double>	scoreList;		// list of scores
	private ChromosomeListOfLists<Strand> 	strandList;		// list of strand


	/**
	 * Creates an instance of {@link GFFExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public GFFExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		// initialize the lists
		startList = new ChromosomeArrayListOfLists<Integer>();
		stopList = new ChromosomeArrayListOfLists<Integer>();
		nameList = new ChromosomeArrayListOfLists<String>();
		scoreList = new ChromosomeArrayListOfLists<Double>();
		strandList = new ChromosomeArrayListOfLists<Strand>();
		// initialize the sublists
		for (int i = 0; i < chromosomeManager.size(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			nameList.add(new ArrayList<String>());
			scoreList.add(new DoubleArrayAsDoubleList());
			strandList.add(new ArrayList<Strand>());
		}
	}


	/**
	 * Receives one line from the input file and extracts and adds the data in the lists
	 * @param extractedLine line read from the data file  
	 * @throws InvalidDataLineException 
	 */
	@Override
	protected void extractLine(String extractedLine) throws InvalidDataLineException {
		String[] splitedLine = extractedLine.split("\t");
		if (splitedLine.length == 1) {
			splitedLine = extractedLine.split(" ");
		}
		if (splitedLine.length < 7) {
			throw new InvalidDataLineException(extractedLine);
		}

		try {
			Chromosome chromosome = chromosomeManager.get(splitedLine[0]) ;
			nameList.add(chromosome, splitedLine[2]);
			startList.add(chromosome, Integer.parseInt(splitedLine[3]));
			stopList.add(chromosome, Integer.parseInt(splitedLine[4]));
			scoreList.add(chromosome, Double.parseDouble(splitedLine[5]));
			strandList.add(chromosome, Strand.get(splitedLine[6]));
			lineCount++;
		} catch (InvalidChromosomeException e) {
			throw new InvalidDataLineException(extractedLine);
		}
	}


	@Override
	public RepeatFamilyList toRepeatFamilyList() throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return new RepeatFamilyList(startList, stopList, nameList);
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
		return new BinList(binSize, precision, method, startList, stopList, scoreList);
	}
	
	
	@Override
	public boolean overlapped() {
		return ScoredChromosomeWindowList.overLappingExist(startList, stopList);
	}
}
