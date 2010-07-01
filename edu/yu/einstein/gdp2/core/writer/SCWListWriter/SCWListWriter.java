/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.writer.SCWListWriter;

import java.io.File;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.writer.Writer;


/**
 * Abstract class that must be extended by the classes able to write a {@link ScoredChromosomeWindowList} into a file
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class SCWListWriter implements Writer {
	
	protected final ChromosomeManager			chromosomeManager;	// ChromosomeManager
	protected final File 						outputFile;			// output file 
	protected final ScoredChromosomeWindowList	data;				// data to print
	protected final String						name;				// name of the BinList
	
	
	/**
	 * Constructor
	 * @param outputFile output {@link File}
	 * @param data {@link ScoredChromosomeWindowList} to write
	 * @param name a name for the data
	 */
	public SCWListWriter(File outputFile, ScoredChromosomeWindowList data, String name) {
		this.chromosomeManager = ChromosomeManager.getInstance();
		this.outputFile = outputFile;
		this.data = data;
		this.name = name;
	}
}
