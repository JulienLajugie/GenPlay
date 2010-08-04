/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.extractor;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPInputStream;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.generator.BinListGenerator;
import yu.einstein.gdp2.core.list.binList.BinList;


/**
 * A Serialized BinList file extractor
 * @author Julien Lajugie
 * @version 0.1
 */
public class SerializedBinListExtractor extends Extractor implements BinListGenerator, Serializable {

	private static final long serialVersionUID = 1920229861641233827L;	// generated ID
	private BinList extractedBinList = null;	 // BinList extracted from the file
	
	
	/**
	 * Creates an instance of {@link SerializedBinListExtractor}
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public SerializedBinListExtractor(File dataFile, File logFile) {
		super(dataFile, logFile);
	}

	
	@Override
	public void extract() throws FileNotFoundException, IOException, ClassNotFoundException, InterruptedException, ExecutionException {
		startTime = System.currentTimeMillis();		
		FileInputStream fis = new FileInputStream(dataFile);
		GZIPInputStream gz = new GZIPInputStream(fis);
		ObjectInputStream ois = new ObjectInputStream(gz);
		extractedBinList = (BinList)ois.readObject();
		for (int i = 0; i < extractedBinList.size(); i++) {
			int chromoStatus = checkChromosomeStatus(chromosomeManager.get(i));
			if ((chromoStatus == AFTER_LAST_SELECTED) || (chromoStatus == NEED_TO_BE_SKIPPED)) {
				extractedBinList.set(i, null);
			}
		}
		// we want to regenerate the accelerator BinList and the statistics
		// so we need to recreate a BinList
		extractedBinList = new BinList(extractedBinList.getBinSize(), extractedBinList.getPrecision(), extractedBinList);
	}

	
	@Override
	public boolean isBinSizeNeeded() {
		return false;
	}


	@Override
	public boolean isCriterionNeeded() {
		return false;
	}
	

	@Override
	public boolean isPrecisionNeeded() {
		return false;
	}

	
	@Override
	public BinList toBinList(int binSize, DataPrecision precision, ScoreCalculationMethod method) throws IllegalArgumentException {
		return extractedBinList;
	}
}
