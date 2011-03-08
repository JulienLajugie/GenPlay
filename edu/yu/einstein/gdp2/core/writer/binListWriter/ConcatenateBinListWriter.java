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
package yu.einstein.gdp2.core.writer.binListWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.writer.Writer;
import yu.einstein.gdp2.exception.BinListDifferentWindowSizeException;
import yu.einstein.gdp2.gui.statusBar.Stoppable;


/**
 * Concatenates and saves a list of {@link BinList} in a file
 * @author Julien Lajugie
 * @version 0.1
 */
public class ConcatenateBinListWriter implements Writer, Stoppable {

	private final BinList[] 		binListArray;			// array of the BinList to concatenate
	private final String[] 			nameArray;				// name of the BinLists
	private final File				outputFile;				// file where to write the concatenation
	private boolean					needsToBeStopped = false;// true if the writing need to be stopped 
	

	/**
	 * Creates an instance of {@link ConcatenateBinListWriter}
	 * @param binListArray an array of {@link BinList} to concatenate
	 * @param nameArray an array containing the name of the BinList
	 * @param outputFile {@link File} where to write the concatenation
	 */
	public ConcatenateBinListWriter(BinList[] binListArray, String[] nameArray, File outputFile) {
		this.binListArray = binListArray;
		this.nameArray = nameArray;
		this.outputFile = outputFile;		
	}


	/**
	 * Concatenates and saves a list of {@link BinList} in a file
	 * @throws IOException
	 * @throws BinListDifferentWindowSizeException
	 * @throws InterruptedException 
	 */
	public void write() throws IOException, BinListDifferentWindowSizeException, InterruptedException {
		if (binListArray.length > 0) {
			
			// check if the BinList all have the same bin size 
			int binSize = binListArray[0].getBinSize();
			for (BinList currentList: binListArray) {
				if (currentList.getBinSize() != binSize) {
					throw new BinListDifferentWindowSizeException();
				}
			}

			BufferedWriter writer = null;
			try {
				// try to create a output file
				writer = new BufferedWriter(new FileWriter(outputFile));

				// print the title of the graph
				writer.write("track type=concatained_file name=");
				for (int i = 0; i < nameArray.length; i++) {
					if (i != 0) {
						writer.write("_+_");
					}
					writer.write(nameArray[i]);
				}
				writer.newLine();

				// print header line
				writer.write("#chromo\tstart\tstop");
				for (int i = 0; i < nameArray.length; i++) {
					writer.write("\t" + nameArray[i]);
				}
				writer.newLine();
				
				for (Chromosome currentChromo: ChromosomeManager.getInstance()) {					
					int binCount = currentChromo.getLength() / binSize + 1; 
					int j = 0;
					while (j < binCount) {
						writer.write(currentChromo + "\t" + (j * binSize) + "\t" + ((j + 1) * binSize));
						for (BinList currentBinList: binListArray) {
							// if the operation need to be stopped we close the writer and delete the file 
							if (needsToBeStopped) {
								writer.close();
								outputFile.delete();
								throw new InterruptedException();
							}							
							if ((currentBinList.get(currentChromo) != null) && (j < currentBinList.size(currentChromo))) {
								writer.write("\t" + currentBinList.get(currentChromo, j));
							} else {
								writer.write("\t0.0");
							}
						}
						writer.newLine();
						j++;						
					}					
				}
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
	}


	/**
	 * Stops the writer while it's writing a file
	 */
	@Override
	public void stop() {
		needsToBeStopped = true;		
	}
}
