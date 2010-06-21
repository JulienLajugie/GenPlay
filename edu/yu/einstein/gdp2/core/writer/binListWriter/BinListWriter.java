/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.writer.binListWriter;

import java.io.File;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.writer.Writer;


/**
 * Abstract class that must be extended by the classes able to write a {@link BinList} into a file
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class BinListWriter implements Writer {
	
	protected final ChromosomeManager	chromosomeManager;	// ChromosomeManager
	protected final File 				outputFile;			// output file 
	protected final BinList				data;				// data to print
	protected final String				name;				// name of the BinList
	
	
	/**
	 * Creates an instance of {@link BinListWriter}.
	 * @param outputFile output {@link File}
	 * @param data {@link BinList} to write
	 * @param name a name for the {@link BinList}
	 */
	public BinListWriter(File outputFile, BinList data, String name) {
		this.chromosomeManager = ChromosomeManager.getInstance();
		this.outputFile = outputFile;
		this.data = data;
		this.name = name;
	}
}
