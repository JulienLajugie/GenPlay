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
package edu.yu.einstein.genplay.core.writer.geneListWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.yu.einstein.genplay.core.Gene;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;



/**
 * Allows to write a {@link GeneList} as a GdpGene file.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GeneListAsGdpGeneWriter extends GeneListWriter implements Stoppable {

	private boolean needsToBeStopped = false;	// true if the writer needs to be stopped


	/**
	 * Creates an instance of {@link GeneListAsGdpGeneWriter}.
	 * @param outputFile output {@link File}
	 * @param data {@link GeneList} to write
	 * @param name a name for the {@link GeneList}
	 */
	public GeneListAsGdpGeneWriter(File outputFile, GeneList data, String name) {
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
			writer.write("track type=GdpGene name=" + name);
			writer.newLine();
			// print the data
			for (List<Gene> currentList : data) {
				for (Gene currentGene : currentList) {
					// if the operation need to be stopped we close the writer and delete the file
					if (needsToBeStopped) {
						writer.close();
						outputFile.delete();
						throw new InterruptedException();
					}
					Chromosome currentChromosome = currentGene.getChromo();
					int currentChromosomeSize = currentChromosome.getLength();
					int start = currentGene.getStart();
					int stop = currentGene.getStop();
					if (stop > currentChromosomeSize) {
						stop = currentChromosomeSize;
					}

					if (isMultiGenome) {
						start = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, allele, start, currentChromosome, fullGenomeName);
						stop = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, allele, stop, currentChromosome, fullGenomeName);
					}

					if ((start != -1) && (stop != -1)) {
						String lineToPrint = new String();
						lineToPrint = currentGene.getName();
						lineToPrint += "\t";
						lineToPrint += currentChromosome.toString();
						lineToPrint += "\t";
						lineToPrint += currentGene.getStrand().toString();
						lineToPrint += "\t";
						lineToPrint += start;
						lineToPrint += "\t";
						lineToPrint += stop;
						lineToPrint += "\t";
						if (currentGene.getExonStarts() == null) {
							lineToPrint += "-";
						} else {
							for (int currentStart : currentGene.getExonStarts()) {
								lineToPrint += currentStart;
								lineToPrint += ",";
							}
							// remove last comma
							lineToPrint = lineToPrint.substring(0, lineToPrint.length() - 1);
						}
						lineToPrint += "\t";
						if (currentGene.getExonStops() == null) {
							lineToPrint += "-";
						} else {
							for (int currentStop : currentGene.getExonStops()) {
								lineToPrint += currentStop;
								lineToPrint += ",";
							}
							// remove last comma
							lineToPrint = lineToPrint.substring(0, lineToPrint.length() - 1);
						}
						lineToPrint += "\t";
						if (currentGene.getExonScores() != null) {
							for (double currentScore : currentGene.getExonScores()) {
								lineToPrint += currentScore;
								lineToPrint += ",";
							}
							// remove last comma
							lineToPrint = lineToPrint.substring(0, lineToPrint.length() - 1);
						}
						writer.write(lineToPrint);
						writer.newLine();
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
