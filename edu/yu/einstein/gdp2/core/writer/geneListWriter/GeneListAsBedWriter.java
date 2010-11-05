/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.writer.geneListWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import yu.einstein.gdp2.core.Gene;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.gui.statusBar.Stoppable;


/**
 * Allows to write a {@link GeneList} as a BED file.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GeneListAsBedWriter extends GeneListWriter implements Stoppable {

	private boolean needsToBeStopped = false;	// true if the writer needs to be stopped 

	
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
		BufferedWriter writer = null;
		try {
			// try to create a output file
			writer = new BufferedWriter(new FileWriter(outputFile));
			// print the title of the graph
			writer.write("track type=bed name=" + name);
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
					String lineToPrint = new String();
					lineToPrint = currentGene.getChromo().toString();
					lineToPrint += "\t";
					lineToPrint += currentGene.getStart();
					lineToPrint += "\t";
					lineToPrint += currentGene.getStop();
					lineToPrint += "\t";
					lineToPrint += currentGene.getName();
					lineToPrint += "\t";
					// add the RPKM of the gene for the score if there is one
					Double score = currentGene.getGeneRPKM();
					if (score == null) {
						lineToPrint += "0";
					} else {
						lineToPrint += score;
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
