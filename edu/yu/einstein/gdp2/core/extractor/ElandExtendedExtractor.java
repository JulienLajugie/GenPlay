/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.extractor;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.generator.BinListGenerator;
import yu.einstein.gdp2.core.list.ChromosomeArrayListOfLists;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.arrayList.IntArrayAsIntegerList;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.InvalidDataLineException;


/**
 * A Eland Extended file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ElandExtendedExtractor extends TextFileExtractor implements Serializable, BinListGenerator {

	private static final long serialVersionUID = 8952410963820358882L;	// generated ID

	private ChromosomeListOfLists<Integer>	positionList;		// list of position
	private int[][] 						matchTypeCount; 	// number of lines with 0,1,2 mistakes per chromosome
	private int 							NMCount = 0;		// Non matched line count
	private int 							QCCount = 0;		// quality control line count
	private int 							multiMatchCount = 0;// multi-match line count 


	/**
	 * Creates an instance of {@link ElandExtendedExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public ElandExtendedExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
		positionList = new ChromosomeArrayListOfLists<Integer>();
		for (int i = 0; i < chromosomeManager.size(); i++) {
			positionList.add(new IntArrayAsIntegerList());
		}
		matchTypeCount = new int[chromosomeManager.size()][3];		
		for(short i = 0; i < chromosomeManager.size(); i++) {
			for(short j = 0; j < 3; j++)
				matchTypeCount[i][j] = 0;
		}
	}


	@Override
	protected void logExecutionInfo() {
		super.logExecutionInfo();
		// display statistics
		if(logFile != null) {
			try {
				// initialize the number of read per chromosome and the data for statistics
				int total0M = 0, total1M = 0, total2M = 0;
				BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
				DecimalFormat df = new DecimalFormat("##.#");
				writer.write("NM: " + NMCount);
				writer.newLine();
				writer.write("Percentage of NM: " + df.format((double)NMCount / totalCount * 100) + "%");
				writer.newLine();
				writer.write("QC: " + QCCount);
				writer.newLine();
				writer.write("Percentage of QC: " + df.format((double)QCCount / totalCount * 100) + "%");
				writer.newLine();
				writer.write("Multi match: " + multiMatchCount);
				writer.newLine();
				writer.write("Percentage of multimatch: " + df.format((double)multiMatchCount / totalCount * 100) + "%");				
				writer.newLine();
				writer.write("Chromosome\t0MM\t1MM\t2MM\tTotal");
				writer.newLine();
				for(short i = 0; i < chromosomeManager.size(); i++) {
					writer.write(chromosomeManager.get(i) + 
							"\t\t" + df.format((double)matchTypeCount[i][0] / lineCount*100) + 
							"%\t" + df.format((double)matchTypeCount[i][1] / lineCount*100) + 
							"%\t" + df.format((double)matchTypeCount[i][2] / lineCount*100) + 
							"%\t" + df.format((double)(matchTypeCount[i][0] + matchTypeCount[i][1] + matchTypeCount[i][2]) / lineCount*100) + "%");
					writer.newLine();
					total0M+=matchTypeCount[i][0];
					total1M+=matchTypeCount[i][1];
					total2M+=matchTypeCount[i][2];
				}
				writer.write("Total:\t" + df.format((double)total0M/lineCount*100) + 
						"%\t" + df.format((double)total1M/lineCount*100) + 
						"%\t" + df.format((double)total2M/lineCount*100) + 
				"%\t\t100%");
				writer.newLine();
				writer.close();
			} catch (IOException e) {

			}
		}
	}


	@Override
	protected boolean extractLine(String extractedLine) throws InvalidDataLineException {
		byte[] line = extractedLine.getBytes();
		byte[] matchChar = new byte[4]; 
		byte[] chromoChar = new byte[64];
		byte[] positionChar = new byte[10];
		short match0MNumber, match1MNumber, match2MNumber, chromoNumber;
		Chromosome chromo;
		int positionNumber;

		if (line[0] == '\0') {
			throw new InvalidDataLineException(extractedLine);
		}

		// skip first field
		int i = 0;
		while (line[i] != '\t') {
			i++;
		}
		// skip second field
		i++;
		while (line[i] != '\t') {
			i++;
		}
		// try to extract the number of match 0M
		i++;
		int j = 0;
		while ((line[i] != '\t') && (line[i] != ':')) {
			matchChar[j] = line[i];
			i++;
			j++;
		}
		// case where we don't found a match
		if (line[i] == '\t') {
			if (matchChar[0] == 'N') {
				NMCount++;
			} else if (matchChar[0] == 'Q') {
				QCCount++;
			}
			throw new InvalidDataLineException(extractedLine);
		}
		match0MNumber = Short.parseShort(new String(matchChar, 0, j));
		// try to extract the number of match 1M
		i++;
		j = 0;
		while (line[i] != ':') {
			matchChar[j] = line[i];
			i++;
			j++;
		}
		match1MNumber = Short.parseShort(new String(matchChar, 0, j));
		// try to extract the number of match 2M
		i++;
		j = 0;
		while (line[i] != '\t') {
			matchChar[j] = line[i];
			i++;
			j++;
		}
		match2MNumber = Short.parseShort(new String(matchChar, 0, j));
		// we only want lines that correspond to our criteria
		if (match0MNumber + match1MNumber + match2MNumber != 1) {
			multiMatchCount++;
			throw new InvalidDataLineException(extractedLine);
		}

		while ((i < line.length) && (line[i] != '.'))  {
			chromoChar[j] = line[i];
			i++;
			j++;
		}

		// if we reach the end of the line now there is no data to extract
		if (i == line.length) {
			throw new InvalidDataLineException(extractedLine);
		}
		try {
			chromo = chromosomeManager.get(new String(chromoChar, 0, j).trim());
		} catch (InvalidChromosomeException e) {
			return false;
		}
		// checks if we need to extract the data on the chromosome
		int chromosomeStatus = checkChromosomeStatus(chromo);
		if (chromosomeStatus == AFTER_LAST_SELECTED) {
			return true;
		} else if (chromosomeStatus == NEED_TO_BE_SKIPPED) {
			return false;
		} else {
			chromoNumber = chromosomeManager.getIndex(chromo);


			// try to extract the position number
			i+=4;  // we want to get rid of 'fa:'
			j = 0;
			while ((line[i] != 'F') && (line[i] != 'R')) {
				positionChar[j] = line[i];
				i++;
				j++;
			}
			positionNumber = Integer.parseInt(new String(positionChar, 0, j));
			// add data for the statistics
			matchTypeCount[chromoNumber][0] += match0MNumber;
			matchTypeCount[chromoNumber][1] += match1MNumber;
			matchTypeCount[chromoNumber][2] += match2MNumber;
			// add the data
			positionList.add(chromo, positionNumber);
			lineCount++;
			return false;
		}
	}


	@Override
	public boolean isBinSizeNeeded() {
		return true;
	}


	@Override
	public boolean isCriterionNeeded() {
		return false;
	}


	@Override
	public boolean isPrecisionNeeded() {
		return true;
	}


	@Override
	public BinList toBinList(int binSize, DataPrecision precision, ScoreCalculationMethod method) throws IllegalArgumentException, InterruptedException, ExecutionException {
		return new BinList(binSize, precision, positionList);
	}
}
