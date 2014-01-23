/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.core.IO.writer.SCWListWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;
import edu.yu.einstein.genplay.util.NumberFormats;


/**
 * Writes {@link SCWList} data into BedGraph files.
 * The windows with a score of 0 are also written
 * @author Julien Lajugie
 */
public final class SCWListAsBedGraphWith0Writer extends SCWListWriter implements Stoppable {

	private boolean needsToBeStopped = false;	// true if the writer needs to be stopped


	/**
	 * Creates an instance of {@link SCWListAsBedGraphWith0Writer}.
	 * @param outputFile output {@link File}
	 * @param data {@link BinList} to write
	 * @param name a name for the {@link BinList}
	 */
	public SCWListAsBedGraphWith0Writer(File outputFile, SCWList data, String name) {
		super(outputFile, data, name);
	}


	/**
	 * Stops the writer while it's writing a file
	 */
	@Override
	public void stop() {
		needsToBeStopped = true;
	}


	@Override
	public void write() throws IOException, InterruptedException {
		BufferedWriter writer = null;
		try {
			boolean isMultiGenome = ProjectManager.getInstance().isMultiGenomeProject() && (fullGenomeName != null) && (allele != null);

			// try to create a output file
			writer = new BufferedWriter(new FileWriter(outputFile));
			// print the title of the graph
			writer.write("track type=bedGraph name=" + name);
			writer.newLine();
			// print the data
			for(Chromosome currentChromosome: projectChromosomes) {
				if(data.get(currentChromosome) != null) {
					ListView<ScoredChromosomeWindow> currentList = data.get(currentChromosome);
					int currentChromosomeSize = currentChromosome.getLength();
					int lastStop = 0;
					for (ScoredChromosomeWindow currentWindow: currentList) {
						// if the operation need to be stopped we close the writer and delete the file
						if (needsToBeStopped) {
							writer.close();
							outputFile.delete();
							throw new InterruptedException();
						}

						int start = currentWindow.getStart();
						int stop = currentWindow.getStop();
						String score = NumberFormats.getWriterScoreFormat().format(currentWindow.getScore());
						if (stop > currentChromosomeSize) {
							stop = currentChromosomeSize;
						}
						if (isMultiGenome) {
							start = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, allele, start, currentChromosome, fullGenomeName);
							stop = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, allele, stop, currentChromosome, fullGenomeName);
						}
						// we subtract 1 because positions in bedgraph files are 0 based and GenPlay positions are 1-based
						start--;
						stop--;

						if ((start > -1) && (stop > -1)) {
							if (start != lastStop) {
								writer.write(currentChromosome.getName() + "\t" + lastStop + "\t" + start + "\t" + 0);
							}
							writer.write(currentChromosome.getName() + "\t" + start + "\t" + stop + "\t" + score);
							writer.newLine();
						}
						lastStop = stop;
					}
				}
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
