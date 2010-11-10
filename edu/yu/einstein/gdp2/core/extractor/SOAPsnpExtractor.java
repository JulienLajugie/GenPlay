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
import yu.einstein.gdp2.core.SNPList.SNPList;
import yu.einstein.gdp2.core.enums.Nucleotide;
import yu.einstein.gdp2.core.generator.SNPListGenerator;
import yu.einstein.gdp2.core.list.ChromosomeArrayListOfLists;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.arrayList.IntArrayAsIntegerList;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.InvalidDataLineException;


/**
 * Extracts the SOAP SNP files
 * @author Julien Lajugie
 * @version 0.1
 */
public class SOAPsnpExtractor extends TextFileExtractor implements Serializable, SNPListGenerator {

	private static final long serialVersionUID = -7942385011427936L;	// generated ID
	private final ChromosomeListOfLists<Integer>	positionList;		// list of extracted position
	private final ChromosomeListOfLists<Nucleotide> firstBaseList;		// list of first base
	private final ChromosomeListOfLists<Integer> 	firstBaseCountList;	// list of first base count
	private final ChromosomeListOfLists<Nucleotide> secondBaseList;		// list of second base
	private final ChromosomeListOfLists<Integer> 	secondBaseCountList;// list of second base count
	private final ChromosomeListOfLists<Boolean> 	isSecondBaseSignificantList; // true if the second base is significant


	/**
	 * Creates an instance of {@link SOAPsnpExtractor}
	 * @param dataFile
	 * @param logFile
	 */
	public SOAPsnpExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		// initialize the lists
		positionList = new ChromosomeArrayListOfLists<Integer>();
		firstBaseList = new ChromosomeArrayListOfLists<Nucleotide>();
		firstBaseCountList = new ChromosomeArrayListOfLists<Integer>();
		secondBaseList = new ChromosomeArrayListOfLists<Nucleotide>();
		secondBaseCountList = new ChromosomeArrayListOfLists<Integer>();
		isSecondBaseSignificantList = new ChromosomeArrayListOfLists<Boolean>();
		// initialize the sublists
		for (int i = 0; i < chromosomeManager.size(); i++) {
			positionList.add(new IntArrayAsIntegerList());
			firstBaseList.add(new ArrayList<Nucleotide>());
			firstBaseCountList.add(new IntArrayAsIntegerList());
			secondBaseList.add(new ArrayList<Nucleotide>());
			secondBaseCountList.add(new IntArrayAsIntegerList());
			isSecondBaseSignificantList.add(new ArrayList<Boolean>());
		}
	}


	@Override
	protected boolean extractLine(String line) throws InvalidDataLineException {
		String[] splitedLine = line.split("\t");
		try {
			Chromosome chromosome = chromosomeManager.get(splitedLine[0].trim()) ;
			int chromosomeStatus = checkChromosomeStatus(chromosome);
			if (chromosomeStatus == AFTER_LAST_SELECTED) {
				return true;
			} else if (chromosomeStatus == NEED_TO_BE_SKIPPED) {
				return false;
			} else {
				int position = Integer.parseInt(splitedLine[1].trim());
				Nucleotide consensusGenotype = Nucleotide.get(splitedLine[3].trim().charAt(0));
				Nucleotide firstBase = Nucleotide.get(splitedLine[5].trim().charAt(0));
				int firstBaseCount = Integer.parseInt(splitedLine[7].trim());
				Nucleotide secondBase = Nucleotide.get(splitedLine[9].trim().charAt(0));
				int secondBaseCount = Integer.parseInt(splitedLine[11].trim());				
				boolean isSecondBaseSignificant = !consensusGenotype.equals(firstBase);
				positionList.add(chromosome, position);
				firstBaseList.add(chromosome, firstBase);
				firstBaseCountList.add(chromosome, firstBaseCount);
				secondBaseList.add(chromosome, secondBase);
				secondBaseCountList.add(chromosome, secondBaseCount);
				isSecondBaseSignificantList.add(chromosome, isSecondBaseSignificant);				
				lineCount++;
				return false;
			}
		} catch (InvalidChromosomeException e) {
			throw new InvalidDataLineException(line);
		}
	}


	@Override
	public SNPList toSNPList() throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return new SNPList(positionList, firstBaseList, firstBaseCountList, secondBaseList, secondBaseCountList, isSecondBaseSignificantList);
	}
}
