/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.writer.geneListWriter;

import java.io.File;
import java.io.IOException;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.util.ChromosomeManager;


/**
 * Abstract class that must be extended by the classes able to write a {@link GeneList} into a file
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class GeneListWriter {
	
	protected final ChromosomeManager	chromosomeManager;	// ChromosomeManager
	protected final File 				outputFile;			// output file 
	protected final GeneList			data;				// data to print
	protected final String				name;				// name of the GeneList
	
	
	/**
	 * Creates an instance of {@link GeneListWriter}.
	 * @param chromosomeManager a {@link ChromosomeManager}
	 * @param outputFile output {@link File}
	 * @param data {@link BinList} to write
	 * @param name a name for the {@link GeneList}
	 */
	public GeneListWriter(ChromosomeManager chromosomeManager, File outputFile, GeneList data, String name) {
		this.chromosomeManager = chromosomeManager;
		this.outputFile = outputFile;
		this.data = data;
		this.name = name;
	}
	
	
	/**
	 * Writes the {@link GeneList} in an output file
	 * @throws IOException
	 */
	public abstract void write() throws IOException;
}
