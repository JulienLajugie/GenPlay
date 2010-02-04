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
import yu.einstein.gdp2.core.enums.Strand;
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
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.list.geneList.GeneListGenerator;
import yu.einstein.gdp2.core.list.repeatFamilyList.RepeatFamilyList;
import yu.einstein.gdp2.core.list.repeatFamilyList.RepeatFamilyListGenerator;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.InvalidDataLineException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;
import yu.einstein.gdp2.util.ChromosomeManager;


/**
 * A BED file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public class BedExtractor extends TextFileExtractor 
implements Serializable, RepeatFamilyListGenerator, ChromosomeWindowListGenerator, 
ScoredChromosomeWindowListGenerator, GeneListGenerator, BinListGenerator {

	private static final long serialVersionUID = 7967902877674655813L; // generated ID

	private ChromosomeListOfLists<Integer>	startList;		// list of position start
	private ChromosomeListOfLists<Integer>	stopList;		// list of position stop
	private ChromosomeListOfLists<String> 	nameList;		// list of name
	private ChromosomeListOfLists<Double>	scoreList;		// list of scores
	private ChromosomeListOfLists<Strand> 	strandList;		// list of strand
	private ChromosomeListOfLists<int[]> 	exonStartsList;	// list of list of exon starts
	private ChromosomeListOfLists<int[]> 	exonStopsList;	// list of list of exon stops
	private ChromosomeListOfLists<double[]>	exonScoresList;	// list of list of exon scores


	/**
	 * Creates an instance of {@link BedExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 * @param chromosomeManager a {@link ChromosomeManager}
	 */
	public BedExtractor(File dataFile, File logFile, ChromosomeManager chromosomeManager) {
		super(dataFile, logFile, chromosomeManager);
		// initialize the lists
		startList = new ChromosomeArrayListOfLists<Integer>(chromosomeManager);
		stopList = new ChromosomeArrayListOfLists<Integer>(chromosomeManager);
		nameList = new ChromosomeArrayListOfLists<String>(chromosomeManager);
		scoreList = new ChromosomeArrayListOfLists<Double>(chromosomeManager);
		strandList = new ChromosomeArrayListOfLists<Strand>(chromosomeManager);
		exonStartsList = new ChromosomeArrayListOfLists<int[]>(chromosomeManager);
		exonStopsList = new ChromosomeArrayListOfLists<int[]>(chromosomeManager);
		exonScoresList = new ChromosomeArrayListOfLists<double[]>(chromosomeManager);
		// initialize the sublists
		for (int i = 0; i < chromosomeManager.chromosomeCount(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			nameList.add(new ArrayList<String>());
			scoreList.add(new DoubleArrayAsDoubleList());
			strandList.add(new ArrayList<Strand>());
			exonStartsList.add(new ArrayList<int[]>());
			exonStopsList.add(new ArrayList<int[]>());
			exonScoresList.add(new ArrayList<double[]>());
		}
	}


	@Override
	protected void extractLine(String extractedLine) throws ManagerDataNotLoadedException, InvalidDataLineException {
		String[] splitedLine = extractedLine.split("\t");
		if (splitedLine.length == 1) {
			splitedLine = extractedLine.split(" ");
		}
		if (splitedLine.length < 3) {
			throw new InvalidDataLineException(extractedLine);
		}

		try {
			Chromosome chromosome = chromosomeManager.getChromosome(splitedLine[0]) ;
			int start = Integer.parseInt(splitedLine[1].trim());
			startList.add(chromosome, start);
			int stop = Integer.parseInt(splitedLine[2].trim());
			stopList.add(chromosome, stop);
			if (splitedLine.length > 3) {
				String name = splitedLine[3].trim();
				nameList.add(chromosome, name);
				if (splitedLine.length > 4) {
					double score = Double.parseDouble(splitedLine[4].trim());
					scoreList.add(chromosome, score);
					if (splitedLine.length > 5) {
						Strand strand = Strand.get(splitedLine[5].trim());
						strandList.add(chromosome, strand);
						if (splitedLine.length > 11) {
							if ((!splitedLine[10].trim().equals("-")) && (!splitedLine[11].trim().equals("-"))) {
								String[] exonStartsStr = splitedLine[11].split(",");
								String[] exonLengthsStr = splitedLine[10].split(",");
								int[] exonStarts = new int[exonLengthsStr.length];
								int[] exonStops = new int[exonLengthsStr.length];
								for (int i = 0; i < exonLengthsStr.length; i++) {
									exonStarts[i] = Integer.parseInt(exonStartsStr[i]);
									exonStops[i] = exonStarts[i] + Integer.parseInt(exonLengthsStr[i]);
								}
								exonStartsList.add(chromosome, exonStarts);
								exonStopsList.add(chromosome, exonStops);
								if (splitedLine.length > 12) {
									String[] exonScoresStr = splitedLine[12].split(",");
									double[] exonScores = new double[exonScoresStr.length];
									for (int i = 0; i < exonScoresStr.length; i++) {
										exonScores[i] = Double.parseDouble(exonScoresStr[i]);
									}
									exonScoresList.add(chromosome, exonScores);
								}
							}
						}
					}
				}
			}
			lineCount++;
		} catch (InvalidChromosomeException e) {
			throw new InvalidDataLineException(extractedLine);
		}
	}


	@Override
	public BinList toBinList(int binSize, DataPrecision precision, ScoreCalculationMethod method) throws IllegalArgumentException {
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
	public RepeatFamilyList toRepeatFamilyList() throws ManagerDataNotLoadedException, InvalidChromosomeException {
		return new RepeatFamilyList(chromosomeManager, startList, stopList, nameList);
	}


	@Override
	public GeneList toGeneList() throws ManagerDataNotLoadedException, InvalidChromosomeException {
		return new GeneList(chromosomeManager, nameList, strandList, startList, stopList, exonStartsList, exonStopsList, exonScoresList);
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
