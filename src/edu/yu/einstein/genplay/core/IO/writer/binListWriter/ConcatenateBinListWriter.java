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
package edu.yu.einstein.genplay.core.IO.writer.binListWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.yu.einstein.genplay.core.IO.writer.Writer;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.exception.exceptions.BinListDifferentWindowSizeException;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;



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

	private String					fullGenomeName;		// the genome name (multi genome project only)
	private AlleleType				allele;				// the allele type (multi genome project only)


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


	@Override
	public void setMultiGenomeCoordinateSystem (String genome, AlleleType allele) {
		this.fullGenomeName = genome;
		this.allele = allele;
	}


	/**
	 * Concatenates and saves a list of {@link BinList} in a file
	 * @throws IOException
	 * @throws BinListDifferentWindowSizeException
	 * @throws InterruptedException
	 */
	@Override
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
				boolean isMultiGenome = ProjectManager.getInstance().isMultiGenomeProject() && (fullGenomeName != null) && (allele != null);

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

				for (Chromosome currentChromosome: ProjectManager.getInstance().getProjectChromosome()) {
					int currentChromosomeSize = currentChromosome.getLength();
					int binCount = (currentChromosomeSize / binSize) + 1;
					int j = 0;
					while (j < binCount) {
						int start = j * binSize;
						int stop = start + binSize;
						if (stop > currentChromosomeSize) {
							stop = currentChromosomeSize;
						}

						if (isMultiGenome) {
							start = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, allele, start, currentChromosome, fullGenomeName);
							stop = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, allele, stop, currentChromosome, fullGenomeName);
						}

						if ((start > -1) && (stop > -1)) {
							writer.write(currentChromosome + "\t" + start + "\t" + stop);
							for (BinList currentBinList: binListArray) {
								// if the operation need to be stopped we close the writer and delete the file
								if (needsToBeStopped) {
									writer.close();
									outputFile.delete();
									throw new InterruptedException();
								}
								if ((currentBinList.get(currentChromosome) != null) && (j < currentBinList.size(currentChromosome))) {
									writer.write("\t" + currentBinList.get(currentChromosome, j));
								} else {
									writer.write("\t0.0");
								}
							}
							writer.newLine();
						}
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
