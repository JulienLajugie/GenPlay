/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.extractor;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.SNPList.SNPList;
import yu.einstein.gdp2.core.enums.Nucleotide;
import yu.einstein.gdp2.core.generator.SNPListGenerator;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.InvalidDataLineException;


/**
 * Extracts the dbSNP files
 * @author Julien Lajugie
 * @version 0.1
 */
public class dbSNPExtractor extends TextFileExtractor implements Serializable, SNPListGenerator {

	private static final long serialVersionUID = 968798875579059098L;	// generated ID
/*	private final ChromosomeListOfLists<Integer>	positionList;		// list of extracted position
	private final ChromosomeListOfLists<Nucleotide> firstBaseList;		// list of first base
	private final ChromosomeListOfLists<Integer> 	firstBaseCountList;	// list of first base count
	private final ChromosomeListOfLists<Nucleotide> secondBaseList;		// list of second base
	private final ChromosomeListOfLists<Integer> 	secondBaseCountList;// list of second base count
	private final ChromosomeListOfLists<Boolean> 	isSecondBaseSignificantList; // true if the second base is significant
*/	
	
	/**
	 * 
	 * @param dataFile
	 * @param logFile
	 */
	public dbSNPExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	protected boolean extractLine(String line) throws InvalidDataLineException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SNPList toSNPList() throws InvalidChromosomeException,
			InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
