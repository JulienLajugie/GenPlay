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
import yu.einstein.gdp2.core.enums.Strand;
import yu.einstein.gdp2.core.generator.GeneListGenerator;
import yu.einstein.gdp2.core.list.ChromosomeArrayListOfLists;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.arrayList.IntArrayAsIntegerList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.InvalidDataLineException;


/**
 * A GdpGene file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpGeneExtractor extends TextFileExtractor implements Serializable, GeneListGenerator {

	private static final long serialVersionUID = 7967902877674655813L; // generated ID

	private ChromosomeListOfLists<Integer>	startList;		// list of position start
	private ChromosomeListOfLists<Integer>	stopList;		// list of position stop
	private ChromosomeListOfLists<String> 	nameList;		// list of name
	private ChromosomeListOfLists<Strand> 	strandList;		// list of strand
	private ChromosomeListOfLists<int[]> 	exonStartsList;	// list of list of exon starts
	private ChromosomeListOfLists<int[]> 	exonStopsList;	// list of list of exon stops
	private ChromosomeListOfLists<double[]>	exonScoresList;	// list of list of exon scores
	private String							searchURL;		// url of the gene database for the search


	/**
	 * Creates an instance of a {@link GdpGeneExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public GdpGeneExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		// initialize the lists
		startList = new ChromosomeArrayListOfLists<Integer>();
		stopList = new ChromosomeArrayListOfLists<Integer>();
		nameList = new ChromosomeArrayListOfLists<String>();
		strandList = new ChromosomeArrayListOfLists<Strand>();
		exonStartsList = new ChromosomeArrayListOfLists<int[]>();
		exonStopsList = new ChromosomeArrayListOfLists<int[]>();
		exonScoresList = new ChromosomeArrayListOfLists<double[]>();
		// initialize the sublists
		for (int i = 0; i < chromosomeManager.size(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			nameList.add(new ArrayList<String>());
			strandList.add(new ArrayList<Strand>());
			exonStartsList.add(new ArrayList<int[]>());
			exonStopsList.add(new ArrayList<int[]>());
			exonScoresList.add(new ArrayList<double[]>());
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
		if (extractedLine.trim().substring(0, 10).equalsIgnoreCase("searchURL=")) {
			searchURL = extractedLine.split("\"")[1].trim();
			return false;
		} else {
			String[] splitedLine = extractedLine.split("\t");
			if (splitedLine.length == 1) {
				splitedLine = extractedLine.split(" ");
			}
			if (splitedLine.length < 3) {
				throw new InvalidDataLineException(extractedLine);
			}
			try {
				Chromosome chromosome = chromosomeManager.get(splitedLine[1]) ;
				// checks if we need to extract the data on the chromosome
				int chromosomeStatus = checkChromosomeStatus(chromosome);
				if (chromosomeStatus == AFTER_LAST_SELECTED) {
					return true;
				} else if (chromosomeStatus == NEED_TO_BE_SKIPPED) {
					return false;
				} else {
					String name = splitedLine[0].trim();
					nameList.add(chromosome, name);
					Strand strand = Strand.get(splitedLine[2].trim().charAt(0));
					strandList.add(chromosome, strand);
					int start = Integer.parseInt(splitedLine[3].trim());
					startList.add(chromosome, start);
					int stop = Integer.parseInt(splitedLine[4].trim());
					stopList.add(chromosome, stop);
					String[] exonStartsStr = splitedLine[5].split(",");
					String[] exonStopsStr = splitedLine[6].split(",");
					int[] exonStarts = new int[exonStartsStr.length];
					int[] exonStops = new int[exonStartsStr.length];
					for (int i = 0; i < exonStartsStr.length; i++) {
						exonStarts[i] = Integer.parseInt(exonStartsStr[i].trim());
						exonStops[i] = Integer.parseInt(exonStopsStr[i].trim());
					}
					exonStartsList.add(chromosome, exonStarts);
					exonStopsList.add(chromosome, exonStops);
					if (splitedLine.length > 7) {
						String[] exonScoresStr = splitedLine[7].split(",");
						double[] exonScores = new double[exonScoresStr.length];
						for (int i = 0; i < exonScoresStr.length; i++) {
							exonScores[i] = Double.parseDouble(exonScoresStr[i]);
						}
						exonScoresList.add(chromosome, exonScores);
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
	public GeneList toGeneList() throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return new GeneList(nameList, strandList, startList, stopList, exonStartsList, exonStopsList, exonScoresList, searchURL);
	}
}
