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
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.IO.extractor;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPInputStream;

import edu.yu.einstein.genplay.core.generator.BinListGenerator;
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.binList.BinList;



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
		ois.close();
		for (int i = 0; i < extractedBinList.size(); i++) {
			int chromoStatus = checkChromosomeStatus(projectChromosome.get(i));
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
	public BinList toBinList(int binSize, ScorePrecision precision, ScoreOperation method) throws IllegalArgumentException {
		return extractedBinList;
	}
}
