/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.writer.SCWListWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.gui.statusBar.Stoppable;


/**
 * Writes a {@link ScoredChromosomeWindowList} as a BedGraph file.
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWListAsBedGraphWriter extends SCWListWriter implements Stoppable {

	private boolean needsToBeStopped = false;	// true if the writer needs to be stopped 


	/**
	 * Creates an instance of {@link SCWListAsBedGraphWriter}.
	 * @param outputFile output {@link File}
	 * @param data {@link ScoredChromosomeWindowList} to write
	 * @param name a name for the data
	 */
	public SCWListAsBedGraphWriter(File outputFile, ScoredChromosomeWindowList data, String name) {
		super(outputFile, data, name);
	}


	@Override
	public void write() throws IOException, InterruptedException {
		BufferedWriter writer = null;
		try {
			// try to create a output file
			writer = new BufferedWriter(new FileWriter(outputFile));
			// print the title of the graph
			writer.write("track type=bedGraph name=" + name);
			writer.newLine();
			// print the data
			for(Chromosome currentChromosome: chromosomeManager) {
				List<ScoredChromosomeWindow> currentList = data.get(currentChromosome);
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
							writer.write(currentChromosome.getName() + "\t" + currentWindow.getStart() + "\t" + currentWindow.getStop() + "\t" + currentWindow.getScore());
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
	 * Stops the writer while it's writing a file
	 */
	@Override
	public void stop() {
		needsToBeStopped = true;
	}
}
