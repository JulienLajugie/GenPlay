/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.extractor;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.enums.Strand;
import yu.einstein.gdp2.core.generator.BinListGenerator;
import yu.einstein.gdp2.core.generator.ChromosomeWindowListGenerator;
import yu.einstein.gdp2.core.generator.GeneListGenerator;
import yu.einstein.gdp2.core.generator.RepeatFamilyListGenerator;
import yu.einstein.gdp2.core.generator.ScoredChromosomeWindowListGenerator;
import yu.einstein.gdp2.core.list.ChromosomeArrayListOfLists;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.arrayList.DoubleArrayAsDoubleList;
import yu.einstein.gdp2.core.list.arrayList.IntArrayAsIntegerList;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.list.repeatFamilyList.RepeatFamilyList;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.InvalidDataLineException;
import yu.einstein.gdp2.util.Utils;


/**
 * A BED file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public class BedExtractor extends TextFileExtractor 
implements Serializable, StrandedExtractor, RepeatFamilyListGenerator, ChromosomeWindowListGenerator, 
ScoredChromosomeWindowListGenerator, GeneListGenerator, BinListGenerator {

	private static final long serialVersionUID = 7967902877674655813L; // generated ID
	private final ChromosomeListOfLists<Integer>	startList;		// list of position start
	private final ChromosomeListOfLists<Integer>	stopList;		// list of position stop
	private final ChromosomeListOfLists<String> 	nameList;		// list of name
	private final ChromosomeListOfLists<Double>		scoreList;		// list of scores
	private final ChromosomeListOfLists<Strand> 	strandList;		// list of strand
	private final ChromosomeListOfLists<Integer>	UTR5BoundList;	// list of translation 5' bounds
	private final ChromosomeListOfLists<Integer>	UTR3BoundList;	// list of translation 3' bounds
	private final ChromosomeListOfLists<int[]> 		exonStartsList;	// list of list of exon starts
	private final ChromosomeListOfLists<int[]> 		exonStopsList;	// list of list of exon stops
	private final ChromosomeListOfLists<double[]>	exonScoresList;	// list of list of exon scores
	private String									searchURL;		// url of the gene database for the search
	private Strand 									selectedStrand;	// strand to extract, null for both
	private int 									strandShift;	// value of the shift to perform on the selected strand


	/**
	 * Creates an instance of {@link BedExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public BedExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		// initialize the lists
		startList = new ChromosomeArrayListOfLists<Integer>();
		stopList = new ChromosomeArrayListOfLists<Integer>();
		nameList = new ChromosomeArrayListOfLists<String>();
		scoreList = new ChromosomeArrayListOfLists<Double>();
		strandList = new ChromosomeArrayListOfLists<Strand>();
		UTR5BoundList = new ChromosomeArrayListOfLists<Integer>();
		UTR3BoundList = new ChromosomeArrayListOfLists<Integer>();
		exonStartsList = new ChromosomeArrayListOfLists<int[]>();
		exonStopsList = new ChromosomeArrayListOfLists<int[]>();
		exonScoresList = new ChromosomeArrayListOfLists<double[]>();
		// initialize the sublists
		for (int i = 0; i < chromosomeManager.size(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			nameList.add(new ArrayList<String>());
			scoreList.add(new DoubleArrayAsDoubleList());
			strandList.add(new ArrayList<Strand>());
			UTR5BoundList.add(new IntArrayAsIntegerList());
			UTR3BoundList.add(new IntArrayAsIntegerList());
			exonStartsList.add(new ArrayList<int[]>());
			exonStopsList.add(new ArrayList<int[]>());
			exonScoresList.add(new ArrayList<double[]>());
		}
	}


	@Override
	protected boolean extractLine(String extractedLine) throws InvalidDataLineException {
		if ((extractedLine.trim().length() >= 10) && (extractedLine.trim().substring(0, 10).equalsIgnoreCase("searchURL="))) {
			searchURL = extractedLine.split("\"")[1].trim();
			return false;
		} else {
			String[] splitedLine = Utils.parseLineTabOnly(extractedLine);
			if (splitedLine.length < 3) {
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
					Strand strand = null;
					if (splitedLine.length > 5) {
						strand = Strand.get(splitedLine[5].trim().charAt(0));
					}
					if ((strand == null) || (isStrandSelected(strand))) {
						strandList.add(chromosome, strand);
						int start = getShiftedPosition(strand, chromosome, Integer.parseInt(splitedLine[1].trim()));
						startList.add(chromosome, start);
						int stop = getShiftedPosition(strand, chromosome, Integer.parseInt(splitedLine[2].trim()));
						stopList.add(chromosome, stop);
						if (splitedLine.length > 3) {
							String name = splitedLine[3].trim();
							if (!name.trim().equals("-")) {
								nameList.add(chromosome, name);
							}
							if (splitedLine.length > 4) {
								if (!splitedLine[4].trim().equals("-")) {
									double score = Double.parseDouble(splitedLine[4].trim());
									scoreList.add(chromosome, score);
								}
								if (splitedLine.length > 7) {
									int UTR5Bound;
									int UTR3Bound;
									try { 
										UTR5Bound = getShiftedPosition(strand, chromosome, Integer.parseInt(splitedLine[6].trim()));
										UTR3Bound = getShiftedPosition(strand, chromosome, Integer.parseInt(splitedLine[7].trim()));
									} catch (NumberFormatException e) {
										UTR5Bound = start;
										UTR3Bound = stop;
									}
									UTR5BoundList.add(chromosome, UTR5Bound);
									UTR3BoundList.add(chromosome, UTR3Bound);
									if (splitedLine.length > 11) {
										if ((!splitedLine[10].trim().equals("-")) && (!splitedLine[11].trim().equals("-"))) {
											String[] exonStartsStr = splitedLine[11].split(",");
											String[] exonLengthsStr = splitedLine[10].split(",");
											int[] exonStarts = new int[exonLengthsStr.length];
											int[] exonStops = new int[exonLengthsStr.length];
											for (int i = 0; i < exonLengthsStr.length; i++) {
												exonStarts[i] = Integer.parseInt(exonStartsStr[i]) + start;
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
								} else {
									// if the file contains no infomation about the UTR sites
									UTR5BoundList.add(chromosome, start);
									UTR3BoundList.add(chromosome, stop);
								}
							}
						}
					}
					lineCount++;
					return false;
				}
			} catch (InvalidChromosomeException e) {
				throw new InvalidDataLineException(extractedLine);
			}
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
	public RepeatFamilyList toRepeatFamilyList() throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return new RepeatFamilyList(startList, stopList, nameList);
	}


	@Override
	public GeneList toGeneList() throws InvalidChromosomeException, InterruptedException, ExecutionException {
		boolean areExonsScored = false;
		// check if there is values in the exon scores list
		for (List<double[]> currentExonScores: exonScoresList) {
			if ((currentExonScores != null) && (!currentExonScores.isEmpty())) {
				areExonsScored = true;
			}
		}
		// if there is no exon score value we check if there is a general gene score value
		if (!areExonsScored) {
			boolean areGenesScored = false;
			for (int i = 0; i < scoreList.size() && !areGenesScored; i++) {
				for (int j = 0; j < scoreList.size(i) && !areGenesScored; j++) {
					if ((scoreList.get(i, j) != 0) && (scoreList.get(i, j) != 1)) {
						areGenesScored = true;
					}
				}
			}
			// in this case (where there is no exon specific score values but there is 
			// genes score values) we attribute the gene score values as exon scores
			if (areGenesScored) {			
				for (int i = 0; i < scoreList.size(); i++) {
					for (int j = 0; j < scoreList.size(i); j++) {
						double[] scoreToAdd = {scoreList.get(i, j)};
						exonScoresList.get(i).add(scoreToAdd);
					}
				}
			}
		}
		return new GeneList(nameList, strandList, startList, stopList, UTR5BoundList, UTR3BoundList, exonStartsList, exonStopsList, exonScoresList, searchURL);
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
