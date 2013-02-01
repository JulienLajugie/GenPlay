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
 * Allows to write a {@link GeneList} as a BED file.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GeneListAsBedWriter extends GeneListWriter implements Stoppable {

	private boolean needsToBeStopped = false;	// true if the writer needs to be stopped
	private boolean isGeneListScored = false;	// true if the gene list is scored


	/**
	 * Creates an instance of {@link GeneListAsBedWriter}.
	 * @param outputFile output {@link File}
	 * @param data {@link GeneList} to write
	 * @param name a name for the {@link GeneList}
	 */
	public GeneListAsBedWriter(File outputFile, GeneList data, String name) {
		super(outputFile, data, name);
	}


	@Override
	public void write() throws IOException, InterruptedException {
		isGeneListScored = isGeneListScored();
		BufferedWriter writer = null;
		try {
			boolean isMultiGenome = ProjectManager.getInstance().isMultiGenomeProject() && (fullGenomeName != null) && (allele != null);

			// try to create a output file
			writer = new BufferedWriter(new FileWriter(outputFile));
			// print the title of the graph
			writer.write("track type=bed name=" + name);
			writer.newLine();
			// print the data
			for (List<Gene> currentList : data) {
				if (currentList != null) {
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

						if ((start > -1) && (stop > -1)) {
							String lineToPrint = new String();
							lineToPrint = currentChromosome.toString();
							lineToPrint += "\t";
							lineToPrint += start;
							lineToPrint += "\t";
							lineToPrint += stop;
							lineToPrint += "\t";
							lineToPrint += currentGene.getName();
							lineToPrint += "\t";
							// add the RPKM of the gene for the score if there is one
							if (!isGeneListScored) {
								lineToPrint += "1";
							} else {
								Double score = currentGene.getGeneRPKM();
								if (score == null) {
									// if there is no score for the gene we put a default 1
									lineToPrint += "0";
								} else {
									lineToPrint += score;
								}
							}
							lineToPrint += "\t";
							lineToPrint += currentGene.getStrand().toString();
							lineToPrint += "\t";
							lineToPrint += currentGene.getUTR5Bound();
							lineToPrint += "\t";
							lineToPrint += currentGene.getUTR3Bound();
							// add "-" for itemRgb
							lineToPrint += "\t-\t";
							if ((currentGene.getExonStops() == null) || (currentGene.getExonStarts() == null)) {
								lineToPrint += "-\t-";
							} else {
								// exon count
								lineToPrint += currentGene.getExonStarts().length;
								lineToPrint += "\t";
								// exon lengths
								for (int i = 0; i < currentGene.getExonStops().length; i++) {
									//String size = "" + (currentGene.getExonStops()[i] - currentGene.getExonStarts()[i]);
									//size = size.replaceAll(",", "");
									lineToPrint += currentGene.getExonStops()[i] - currentGene.getExonStarts()[i];
									lineToPrint += ",";
								}
								// remove last comma
								lineToPrint = lineToPrint.substring(0, lineToPrint.length() - 1);
							}
							lineToPrint += "\t";
							// exon starts
							if (currentGene.getExonStarts() == null) {
								lineToPrint += "-";
							} else {
								for (int currentStart : currentGene.getExonStarts()) {
									lineToPrint += currentStart - currentGene.getStart();
									lineToPrint += ",";
								}
								// remove last comma
								lineToPrint = lineToPrint.substring(0, lineToPrint.length() - 1);
							}
							// exon scores
							if (currentGene.getExonScores() != null) {
								lineToPrint += "\t";
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
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}


	/**
	 * @return true if the genes are scored, false otherwise
	 */
	private boolean isGeneListScored() {
		for (List<Gene> currentList : data) {
			if (currentList != null) {
				for (Gene currentGene : currentList) {
					Double rpkm = currentGene.getGeneRPKM();
					if ((rpkm != null) && (rpkm != 0)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Stops the writer while it's writing a file
	 */
	@Override
	public void stop() {
		needsToBeStopped = true;
	}
}
