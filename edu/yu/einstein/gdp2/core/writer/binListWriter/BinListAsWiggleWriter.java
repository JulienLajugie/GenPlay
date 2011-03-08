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
import java.util.List;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.gui.statusBar.Stoppable;


/**
 * Allows to write a BinList as a wiggle file.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinListAsWiggleWriter extends BinListWriter implements Stoppable {

	private static final int ZERO_COUNT = 15;	// how many successive 0 values are needed to print a new a header line
	private boolean needsToBeStopped = false;	// true if the writer needs to be stopped 
	
	
	/**
	 * Creates an instance of {@link BinListAsWiggleWriter}.
	 * @param outputFile output {@link File}
	 * @param data {@link BinList} to write
	 * @param name a name for the {@link BinList}
	 */
	public BinListAsWiggleWriter(File outputFile, BinList data, String name) {
		super(outputFile, data, name);
	}


	@Override
	public void write() throws IOException, InterruptedException {
		BufferedWriter writer = null;
		try {
			// try to create a output file
			writer = new BufferedWriter(new FileWriter(outputFile));
			// print the title of the graph
			writer.write("track type=wiggle name=" + name);
			writer.newLine();
			int binSize = data.getBinSize();
			// print the data
			for(Chromosome currentChromosome: chromosomeManager) {
				if(data.get(currentChromosome) != null) {
					List<Double> currentList = data.get(currentChromosome);
					int j = 0;
					// loop until we find a value different from 0
					while ((j < currentList.size()) && (currentList.get(j) == 0)) {
						j++;
					}
					while (j < currentList.size()) {
						// we print a header
						writer.write("fixedStep chrom=" + currentChromosome.getName() + " start=" + j * binSize + " step=" + binSize + " span=" + binSize);
						writer.newLine();
						int cpt = 0;
						// if there is more than ZERO_COUNT 0 values we print a new header
						while ((j < currentList.size()) && ((cpt < ZERO_COUNT) || (currentList.get(j) == 0))) {
							// if the operation need to be stopped we close the writer and delete the file 
							if (needsToBeStopped) {
								writer.close();
								outputFile.delete();
								throw new InterruptedException();
							}
							if (currentList.get(j) == 0) {
								cpt++;
							} else {
								if (cpt != 0) {
									// case where there were less than ZERO_COUNT 0 values so we need to print them
									for (int k = 0; k < cpt; k++) {
										writer.write(Double.toString(0d));
										writer.newLine();						
									}
									cpt = 0;
								}
								writer.write(Double.toString(currentList.get(j)));
								writer.newLine();
							}
							j++;							
						}						
					}					
				}
			}
		} finally {
			if (writer != null) {
				writer.close();
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
