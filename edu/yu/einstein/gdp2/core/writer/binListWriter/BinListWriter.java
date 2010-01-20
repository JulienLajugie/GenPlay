/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.writer.binListWriter;

import java.io.File;
import java.io.IOException;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.util.ChromosomeManager;


/**
 * Abstract class that must be extended by the classes able to write a {@link BinList} into a file
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class BinListWriter {
	
	protected final ChromosomeManager	chromosomeManager;	// ChromosomeManager
	protected final File 				outputFile;			// output file 
	protected final BinList				data;				// data to print
	protected final String				name;				// name of the BinList
	
	
	/**
	 * Creates an instance of {@link BinListWriter}.
	 * @param chromosomeManager a {@link ChromosomeManager}
	 * @param outputFile output {@link File}
	 * @param data {@link BinList} to write
	 * @param name a name for the {@link BinList}
	 */
	public BinListWriter(ChromosomeManager chromosomeManager, File outputFile, BinList data, String name) {
		this.chromosomeManager = chromosomeManager;
		this.outputFile = outputFile;
		this.data = data;
		this.name = name;
	}
	
	
	/**
	 * Writes the {@link BinList} in an output file
	 * @throws IOException
	 */
	public abstract void write() throws IOException;
}
