/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package yu.einstein.gdp2.core.extractor;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.SNPList.SNPList;
import yu.einstein.gdp2.core.generator.SNPListGenerator;
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
