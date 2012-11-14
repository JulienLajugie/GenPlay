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
package edu.yu.einstein.genplay.core.writer.SCWListWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.chromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;



/**
 * Writes a {@link ScoredChromosomeWindowList} as a BedGraph file.
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWListAsBedWriter extends SCWListWriter implements Stoppable {

	private boolean needsToBeStopped = false;	// true if the writer needs to be stopped


	/**
	 * Creates an instance of {@link SCWListAsBedWriter}.
	 * @param outputFile output {@link File}
	 * @param data {@link ScoredChromosomeWindowList} to write
	 * @param name a name for the data
	 */
	public SCWListAsBedWriter(File outputFile, ScoredChromosomeWindowList data, String name) {
		super(outputFile, data, name);
	}


	@Override
	public void write() throws IOException, InterruptedException {
		BufferedWriter writer = null;
		try {
			boolean isMultiGenome = ProjectManager.getInstance().isMultiGenomeProject() && (fullGenomeName != null) && (allele != null);

			// try to create a output file
			writer = new BufferedWriter(new FileWriter(outputFile));
			// print the title of the graph
			writer.write("track type=bed name=" + name);
			writer.newLine();
			// print the data
			for(Chromosome currentChromosome: projectChromosome) {
				List<ScoredChromosomeWindow> currentList = data.get(currentChromosome);
				int currentChromosomeSize = currentChromosome.getLength();
				if (currentList != null) {
					for (ScoredChromosomeWindow currentWindow: currentList){
						// if the operation need to be stopped we close the writer and delete the file
						if (needsToBeStopped) {
							writer.close();
							outputFile.delete();
							throw new InterruptedException();
						}
						// we don't print the line if the score is 0
						if (currentWindow.getScore() != 0) {
							int start = currentWindow.getStart();
							int stop = currentWindow.getStop();
							if (stop > currentChromosomeSize) {
								stop = currentChromosomeSize;
							}

							if (isMultiGenome) {
								start = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, allele, start, currentChromosome, fullGenomeName);
								stop = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, allele, stop, currentChromosome, fullGenomeName);
							}

							if ((start != -1) && (stop != -1)) {
								//writer.write(currentChromosome.getName() + "\t" + currentWindow.getStart() + "\t" + currentWindow.getStop() + "\t-\t" + currentWindow.getScore());
								writer.write(currentChromosome.getName() + "\t" + start + "\t" + stop + "\t-\t" + currentWindow.getScore());
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


	/**
	 * Stops the writer while it's writing a file
	 */
	@Override
	public void stop() {
		needsToBeStopped = true;
	}
}
