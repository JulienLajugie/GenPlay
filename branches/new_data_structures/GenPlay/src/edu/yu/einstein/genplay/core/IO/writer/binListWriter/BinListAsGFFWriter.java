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

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;



/**
 * Allows to write a BinList as a GFF file.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinListAsGFFWriter extends BinListWriter implements Stoppable {

	private boolean needsToBeStopped = false;	// true if the writer needs to be stopped


	/**
	 * Creates an instance of {@link BinListAsGFFWriter}.
	 * @param outputFile output {@link File}
	 * @param data {@link BinList} to write
	 * @param name a name for the {@link BinList}
	 */
	public BinListAsGFFWriter(File outputFile, BinList data, String name) {
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
			writer.write("#track type=GFF name=" + name);
			writer.write("##GFF");
			writer.newLine();
			int binSize = data.getBinSize();
			// print the data
			for(Chromosome currentChromosome: projectChromosome) {
				if(data.get(currentChromosome) != null) {
					ListView<ScoredChromosomeWindow> currentList = data.get(currentChromosome);
					int currentChromosomeSize = currentChromosome.getLength();
					for (int j = 0; j < currentList.size(); j++) {
						// if the operation need to be stopped we close the writer and delete the file
						if (needsToBeStopped) {
							writer.close();
							outputFile.delete();
							throw new InterruptedException();
						}
						// we don't print the line if the score is 0
						if (currentList.get(j).getScore() != 0) {
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
								//writer.write(currentChromosome.getName() + "\t-\t-\t" + (j * binSize) + "\t" + ((j + 1) * binSize) + "\t" + currentList.get(j) + "\t+\t-\t-");
								writer.write(currentChromosome.getName() + "\t-\t-\t" + start + "\t" + stop + "\t" + currentList.get(j).getScore() + "\t+\t-\t-");
								writer.newLine();
							}
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
}
