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
package edu.yu.einstein.genplay.core.IO.writer.geneListWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;

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
import edu.yu.einstein.genplay.util.NumberFormats;


/**
 * Writes {@link GeneList} data into BED files.
 * @author Julien Lajugie
 */
public final class GeneListAsBedWriter extends GeneListWriter implements Stoppable {

	/**
	 * @param gene a Gene
	 * @return true if the gene is scored, false otherwise
	 */
	private final static boolean areExonsScored(Gene gene) {
		for (ScoredChromosomeWindow currentExon: gene.getExons()) {
			if (!Float.isNaN(currentExon.getScore())) {
				return true;
			}
		}
		return false;
	}


	/**
	 * @param gene a Gene
	 * @param chromosome the chromosome of the gene
	 * @return a bed format string representing the specified gene
	 */
	public final static String geneToString(Gene gene, Chromosome chromosome) {
		return geneToString(gene, chromosome,
				gene.getStart(), gene.getStop(),
				gene.getUTR5Bound(), gene.getUTR3Bound(),
				true);
	}


	/**
	 * @param gene a gene
	 * @param chromosome the chromosome of the gene
	 * @param start the start position of the gene after translation on appropriate genome (for multigenome)
	 * @param stop the stop position of the gene after translation on appropriate genome (for multigenome)
	 * @param UTR5Bound the UTR5 bound position of the gene after translation on appropriate genome (for multigenome)
	 * @param UTR3Bound the UTR3 bound position of the gene after translation on appropriate genome (for multigenome)
	 * @param isGeneListScored true if the gene is scores
	 * @return a bed format string representing the specified gene
	 */
	private final static String geneToString(Gene gene, Chromosome chromosome,
			int start, int stop,
			int UTR5Bound, int UTR3Bound,
			boolean isGeneListScored) {
		String lineToPrint = "";
		// retrieve the number formats for the scores
		NumberFormat numberFormat = NumberFormats.getWriterScoreFormat();

		lineToPrint = chromosome.toString();
		lineToPrint += "\t";
		lineToPrint += start;
		lineToPrint += "\t";
		lineToPrint += stop;
		lineToPrint += "\t";
		lineToPrint += gene.getName();
		lineToPrint += "\t";
		// add the RPKM of the gene for the score if there is one
		if (!isGeneListScored) {
			lineToPrint += "1";
		} else {
			float score = gene.getScore();
			if (Float.isNaN(score)) {
				// if there is no score for the gene we put a default 1
				lineToPrint += "0";
			} else {
				lineToPrint += numberFormat.format(score);
			}
		}
		lineToPrint += "\t";
		lineToPrint += gene.getStrand().toString();
		lineToPrint += "\t";
		lineToPrint += UTR5Bound;
		lineToPrint += "\t";
		lineToPrint += UTR3Bound;
		// add "-" for itemRgb
		lineToPrint += "\t-\t";
		if (gene.getExons() == null) {
			lineToPrint += "-\t-\t-";
		} else {
			// exon count
			lineToPrint += gene.getExons().size();
			lineToPrint += "\t";
			// exon lengths
			for (int i = 0; i < gene.getExons().size(); i++) {
				lineToPrint += gene.getExons().get(i).getStop() - gene.getExons().get(i).getStart();
				lineToPrint += ",";
			}
			// remove last comma
			lineToPrint = lineToPrint.substring(0, lineToPrint.length() - 1);
			lineToPrint += "\t";
			// exon starts
			for (ScoredChromosomeWindow currentExon: gene.getExons()) {
				int currentStart = currentExon.getStart();
				lineToPrint += currentStart - gene.getStart();
				lineToPrint += ",";
			}
			// remove last comma
			lineToPrint = lineToPrint.substring(0, lineToPrint.length() - 1);
			// exon scores
			if (areExonsScored(gene)) {
				lineToPrint += "\t";
				for (ScoredChromosomeWindow currentExon: gene.getExons()) {
					float currentScore = currentExon.getScore();
					if (Float.isNaN(currentScore)) {
						currentScore = 0;
					}
					lineToPrint += numberFormat.format(currentScore);
					lineToPrint += ",";
				}
				// remove last comma
				lineToPrint = lineToPrint.substring(0, lineToPrint.length() - 1);
			}
		}
		return lineToPrint;
	}


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
			for (Chromosome currentChromosome: projectChromosomes) {
				ListView<Gene> currentList = data.get(currentChromosome);
				if (currentList != null) {
					for (Gene currentGene : currentList) {
						// if the operation need to be stopped we close the writer and delete the file
						if (needsToBeStopped) {
							writer.close();
							outputFile.delete();
							throw new InterruptedException();
						}
						int start = currentGene.getStart();
						int stop = currentGene.getStop();
						int UTR5Bound = currentGene.getUTR5Bound();
						int UTR3Bound = currentGene.getUTR3Bound();

						if (isMultiGenome) {
							start = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, allele, start, currentChromosome, fullGenomeName);
							stop = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, allele, stop, currentChromosome, fullGenomeName);
							UTR5Bound = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, allele, UTR5Bound, currentChromosome, fullGenomeName);
							UTR3Bound = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, allele, UTR3Bound, currentChromosome, fullGenomeName);
						}
						// we subtract 1 because positions in bed files are 0 based and GenPlay positions are 1-based
						start--;
						stop--;
						UTR5Bound--;
						UTR3Bound--;
						if ((start > -1) && (stop > -1)) {
							String lineToPrint = geneToString(currentGene, currentChromosome, start, stop, UTR5Bound, UTR3Bound, isGeneListScored);
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
