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
					String lineToPrint = new String();
					lineToPrint = currentGene.getName();
					lineToPrint += "\t";
					lineToPrint += currentGene.getChromo().toString();
					lineToPrint += "\t";
					lineToPrint += currentGene.getStrand().toString();
					lineToPrint += "\t";
					lineToPrint += currentGene.getStart();
					lineToPrint += "\t";
					lineToPrint += currentGene.getStop();
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
