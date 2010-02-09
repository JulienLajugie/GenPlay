/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.writer.binListWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.exception.BinListDifferentWindowSizeException;
import yu.einstein.gdp2.util.ChromosomeManager;


/**
 * Concatenates and saves a list of {@link BinList} in a file
 * @author Julien Lajugie
 * @version 0.1
 */
public class ConcatenateBinListWriter {

	private final ChromosomeManager cm;				// ChromosomeManager
	private final BinList[] 		binListArray;	// Array of the BinList to concatenate
	private final String[] 			nameArray;		// Name of the BinLists
	private final File				outputFile;		// File where to write the concatenation


	/**
	 * Creates an instance of {@link ConcatenateBinListWriter}
	 * @param cm {@link ChromosomeManager}
	 * @param binListArray an array of {@link BinList} to concatenate
	 * @param nameArray an array containing the name of the BinList
	 * @param outputFile {@link File} where to write the concatenation
	 */
	public ConcatenateBinListWriter(ChromosomeManager cm, BinList[] binListArray, String[] nameArray, File outputFile) {
		this.cm = cm;
		this.binListArray = binListArray;
		this.nameArray = nameArray;
		this.outputFile = outputFile;		
	}


	/**
	 * Concatenates and saves a list of {@link BinList} in a file
	 * @throws IOException
	 * @throws BinListDifferentWindowSizeException
	 */
	public void write() throws IOException, BinListDifferentWindowSizeException {
		if (binListArray.length > 0) {
			
			// check if the BinList all have the same bin size 
			int binSize = binListArray[0].getBinSize();
			for (BinList currentList: binListArray) {
				if (currentList.getBinSize() != binSize) {
					throw new BinListDifferentWindowSizeException();
				}
			}

			BufferedWriter writer = null;
			try {
				// try to create a output file
				writer = new BufferedWriter(new FileWriter(outputFile));

				// print the title of the graph
				writer.write("track type=concatained_file name=");
				for (int i = 0; i < nameArray.length; i++) {
					if (i != 0) {
						writer.write("_+_");
					}
					writer.write(nameArray[i]);
				}
				writer.newLine();

				// print header line
				writer.write("#chromo\tstart\tstop");
				for (int i = 0; i < nameArray.length; i++) {
					writer.write("\t" + nameArray[i]);
				}
				writer.newLine();
				
				for (Chromosome currentChromo: cm) {					
					int binCount = currentChromo.getLength() / binSize + 1; 
					int j = 0;
					while (j < binCount) {
						writer.write(currentChromo + "\t" + (j * binSize) + "\t" + ((j + 1) * binSize));
						for (BinList currentBinList: binListArray) {
							if ((currentBinList.get(currentChromo) != null) && (j < currentBinList.size(currentChromo))) {
								writer.write("\t" + currentBinList.get(currentChromo, j));
							} else {
								writer.write("\t0.0");
							}
						}
						writer.newLine();
						j++;						
					}					
				}
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
	}
}
