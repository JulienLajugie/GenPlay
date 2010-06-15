/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.manager;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.exception.InvalidChromosomeException;


/**
 * The ChromosomeManager class provides tools to load and access and list of {@link Chromosome}.
 * This class follows the design pattern <i>Singleton</i> 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ChromosomeManager implements Serializable, Iterable<Chromosome> {

	private static final long serialVersionUID = 8781043776370540275L;	// generated ID
	private static ChromosomeManager 			instance = null;		// unique instance of the singleton
	private final List<Chromosome> 				chromsomeList;			// List of chromosome 
	private final Hashtable<String, Integer> 	chromosomeHash;			// Hashtable indexed by chromosome name
	private long genomomeLength = 0;
	
	/**
	 * @return an instance of a {@link ChromosomeManager}. 
	 * Makes sure that there is only one unique instance as specified in the singleton pattern
	 */
	public static ChromosomeManager getInstance() {
		if (instance == null) {
			synchronized(ChromosomeManager.class) {
				if (instance == null) {
					instance = new ChromosomeManager();
				}
			}
		}
		return instance;
	}
	

	/**
	 * Private constructor of the singleton. Creates an instance of a {@link ChromosomeManager}.
	 */
	private ChromosomeManager() {
		super();
		chromsomeList = new ArrayList<Chromosome>();
		chromosomeHash = new Hashtable<String, Integer>();
		// create the default chromosome configuration
		createDefaultChromosomeArray();
		for (int i = 0; i < chromsomeList.size(); i++) {
			chromosomeHash.put(chromsomeList.get(i).getName(), i);
		}
		computeGenomeSize();
	}



	/**
	 * Creates a default list of chromosomes
	 */
	private void createDefaultChromosomeArray() {
		// the actual default configuration correspond to the hg19 assembly
		chromsomeList.add(new Chromosome("chr1", 249250621));
		chromsomeList.add(new Chromosome("chr2", 243199373));
		chromsomeList.add(new Chromosome("chr3", 198022430));
		chromsomeList.add(new Chromosome("chr4", 191154276));
		chromsomeList.add(new Chromosome("chr5", 180915260));
		chromsomeList.add(new Chromosome("chr6", 171115067));
		chromsomeList.add(new Chromosome("chr7", 159138663));
		chromsomeList.add(new Chromosome("chr8", 146364022));
		chromsomeList.add(new Chromosome("chr9", 141213431));
		chromsomeList.add(new Chromosome("chr10", 135534747));
		chromsomeList.add(new Chromosome("chr11", 135006516));
		chromsomeList.add(new Chromosome("chr12", 133851895));
		chromsomeList.add(new Chromosome("chr13", 115169878));
		chromsomeList.add(new Chromosome("chr14", 107349540));
		chromsomeList.add(new Chromosome("chr15", 102531392));
		chromsomeList.add(new Chromosome("chr16", 90354753));
		chromsomeList.add(new Chromosome("chr17", 81195210));
		chromsomeList.add(new Chromosome("chr18", 78077248));
		chromsomeList.add(new Chromosome("chr19", 59128983));
		chromsomeList.add(new Chromosome("chr20", 63025520));
		chromsomeList.add(new Chromosome("chr21", 48129895));
		chromsomeList.add(new Chromosome("chr22", 51304566));
		chromsomeList.add(new Chromosome("chrX", 155270560));
		chromsomeList.add(new Chromosome("chrY", 59373566));
	}


	/**
	 * Compute the size of the genome
	 */
	private synchronized void computeGenomeSize() {
		genomomeLength = 0;
		for (Chromosome currenChromosome: chromsomeList) {
			genomomeLength += currenChromosome.getLength();
		}
	}
	
	
	/**
	 * @return the lenght of the genome in bp
	 */
	public long getGenomeLength() {
		return genomomeLength;
	}
	
	
	/**
	 * @param index index of a {@link Chromosome}
	 * @return the first chromosome with the specified index
	 * @throws InvalidChromosomeException
	 */
	public Chromosome get(int index) {
		if ((index >= 0) && (index < chromsomeList.size())) {
			return chromsomeList.get(index);
		}
		// throw an exception if nothing found
		throw new InvalidChromosomeException();
	}


	/**
	 * @param chromosomeName name of a {@link Chromosome}
	 * @return the first chromosome having the specified name
	 * @throws InvalidChromosomeException
	 */
	public Chromosome get(String chromosomeName) throws InvalidChromosomeException {
		Integer result = chromosomeHash.get(chromosomeName);
		if (result != null) {
			return chromsomeList.get(result.intValue());
		}
		// throw an exception if nothing found
		throw new InvalidChromosomeException();
	}	


	/**
	 * @param chromosome a {@link Chromosome}
	 * @return the index of the specified chromosome
	 * @throws InvalidChromosomeException
	 */
	public short getIndex(Chromosome chromosome) throws InvalidChromosomeException {
		short index = (short)chromsomeList.indexOf(chromosome);
		if (index == -1) {
			// if nothing has been found
			throw new InvalidChromosomeException();
		} else {
			return index;
		}
	}


	/**
	 * @param chromosomeName name of a chromosome.
	 * @return the index of the first chromosome having the specified name
	 * @throws InvalidChromosomeException
	 */
	public short getIndex(String chromosomeName) throws InvalidChromosomeException {
		if ((chromosomeName != null) && (chromosomeName.length() > 0)) {
			Integer result = chromosomeHash.get(chromosomeName);
			if (result != null) {
				return result.shortValue();
			}
		}
		// If nothing has been found
		throw new InvalidChromosomeException();
	}


	@Override
	public Iterator<Chromosome> iterator() {
		return chromsomeList.iterator();
	}


	/**
	 * Loads the data of the manager.
	 * @param configurationFile configuration file
	 * @throws IOException
	 */
	public void loadConfigurationFile(File configurationFile) throws IOException {
		BufferedReader reader = null;
		try {
			// try to open the input file
			reader = new BufferedReader(new FileReader(configurationFile));
			// remove all the data in the chromosome list
			chromsomeList.clear();
			// extract data
			String line = null;
			int i = 0;
			while((line = reader.readLine()) != null) {
				String[] splitedLine = line.split("\t");
				String name = splitedLine[0].trim();
				int length = Integer.parseInt(splitedLine[1].trim());
				chromsomeList.add(new Chromosome(name, length));
				i++;
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
			chromosomeHash.clear();
			for (int i = 0; i < chromsomeList.size(); i++) {
				chromosomeHash.put(chromsomeList.get(i).getName(), i);
			}
			// compute the size of the genome
			computeGenomeSize();
		}	
	}


	/**
	 * Methods used for the serialization of the singleton object.
	 * The readResolve method is called when ObjectInputStream has 
	 * read an object from the stream and is preparing to return it 
	 * to the caller.
	 * See javadocs for more information
	 * @return the unique instance of the singleton
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		return getInstance();
	}


	/**
	 * @return the size of the list of chromosome (ie: the number of chromosomes)
	 */
	public int size() {
		return chromsomeList.size();
	}


	/**
	 * @return an array containing all the chromosomes of the manager
	 */
	public Chromosome[] toArray() {
		Chromosome[] returnArray = new Chromosome[chromsomeList.size()];
		return chromsomeList.toArray(returnArray);
	}
}