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
package edu.yu.einstein.genplay.core.IO.writer.geneListWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.yu.einstein.genplay.core.IO.utils.TrackLineHeader;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
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


	/**
	 * @param gene a Gene
	 * @return true if the gene is scored, false otherwise
	 */
	private final boolean areExonsScored(Gene gene) {
		for (ScoredChromosomeWindow currentExon: gene.getExons()) {
			if (currentExon.getScore() != Float.NaN) {
				return true;
			}
		}
		return false;
	}


	/**
	 * @return true if the genes are scored, false otherwise
	 */
	private boolean isGeneListScored() {
		for (ListView<Gene> currentList : data) {
			if (currentList != null) {
				for (Gene currentGene : currentList) {
					Float score = currentGene.getScore();
					if ((score != null) && (score != 0)) {
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


	@Override
	public void write() throws IOException, InterruptedException {
		isGeneListScored = isGeneListScored();
		BufferedWriter writer = null;
		try {
			boolean isMultiGenome = ProjectManager.getInstance().isMultiGenomeProject() && (fullGenomeName != null) && (allele != null);
			// try to create a output file
			writer = new BufferedWriter(new FileWriter(outputFile));
			// print the "track" header of the file
			TrackLineHeader trackLineHeader = new TrackLineHeader(); // header "track" line
			trackLineHeader.setName(name);
			trackLineHeader.setGeneDBURL(data.getGeneDBURL());
			trackLineHeader.setGeneScoreType(data.getGeneScoreType());
			String trackLine = trackLineHeader.generateTrackLine();
			if (trackLineHeader != null) {
				writer.write(trackLine);
				writer.newLine();
			}

			// print the data
			for (Chromosome currentChromosome: projectChromosome) {
				ListView<Gene> currentList = data.get(currentChromosome);
				if (currentList != null) {
					for (Gene currentGene : currentList) {
						// if the operation need to be stopped we close the writer and delete the file
						if (needsToBeStopped) {
							writer.close();
							outputFile.delete();
							throw new InterruptedException();
						}
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
								Float score = currentGene.getScore();
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
							if (currentGene.getExons() == null) {
								lineToPrint += "-\t-\t-";
							} else {
								// exon count
								lineToPrint += currentGene.getExons().size();
								lineToPrint += "\t";
								// exon lengths
								for (int i = 0; i < currentGene.getExons().size(); i++) {
									//String size = "" + (currentGene.getExonStops()[i] - currentGene.getExonStarts()[i]);
									//size = size.replaceAll(",", "");
									lineToPrint += currentGene.getExons().get(i).getStop() - currentGene.getExons().get(i).getStart();
									lineToPrint += ",";
								}
								// remove last comma
								lineToPrint = lineToPrint.substring(0, lineToPrint.length() - 1);
								lineToPrint += "\t";
								// exon starts
								for (ScoredChromosomeWindow currentExon: currentGene.getExons()) {
									int currentStart = currentExon.getStart();
									lineToPrint += currentStart - currentGene.getStart();
									lineToPrint += ",";
								}
								// remove last comma
								lineToPrint = lineToPrint.substring(0, lineToPrint.length() - 1);
								// exon scores
								if (areExonsScored(currentGene)) {
									lineToPrint += "\t";
									for (ScoredChromosomeWindow currentExon: currentGene.getExons()) {
										float currentScore = currentExon.getScore();
										if (currentScore == Float.NaN) {
											currentScore = 0;
										}
										lineToPrint += currentScore;
										lineToPrint += ",";
									}
									// remove last comma
									lineToPrint = lineToPrint.substring(0, lineToPrint.length() - 1);
								}
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
}
