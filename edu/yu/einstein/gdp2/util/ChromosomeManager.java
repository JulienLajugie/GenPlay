/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;

/**
 * The ChromosomeManager class provides tools to configure the chromosomes.
 * This class follows the design pattern <i>Singleton</i> 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ChromosomeManager implements Serializable, Iterable<Chromosome> {

	private static final long serialVersionUID = 8781043776370540275L;	// generated ID
	private static ChromosomeManager cmInstance = null;		// instance of the singleton
	private ArrayList<Chromosome> chromosomeArray = 
		createDefaultChromosomeArray();						// List of chromosomes
	private Hashtable<String, Integer> chromosomeHash;		// Hashtable indexed by chromosome name


	/**
	 * Private constructor of the singleton. Creates an instance of a {@link ChromosomeManager}.
	 */
	private ChromosomeManager() {
		super();
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
	 * Creates a default list of chromosomes
	 * @return
	 */
	private static ArrayList<Chromosome> createDefaultChromosomeArray() {
		ArrayList<Chromosome> defaultChromosomeArray = new ArrayList<Chromosome>();
		defaultChromosomeArray.add(new Chromosome("chr1", 249250621));
		defaultChromosomeArray.add(new Chromosome("chr2", 243199373));
		defaultChromosomeArray.add(new Chromosome("chr3", 198022430));
		defaultChromosomeArray.add(new Chromosome("chr4", 191154276));
		defaultChromosomeArray.add(new Chromosome("chr5", 180915260));
		defaultChromosomeArray.add(new Chromosome("chr6", 171115067));
		defaultChromosomeArray.add(new Chromosome("chr7", 159138663));
		defaultChromosomeArray.add(new Chromosome("chr8", 146364022));
		defaultChromosomeArray.add(new Chromosome("chr9", 141213431));
		defaultChromosomeArray.add(new Chromosome("chr10", 135534747));
		defaultChromosomeArray.add(new Chromosome("chr11", 135006516));
		defaultChromosomeArray.add(new Chromosome("chr12", 133851895));
		defaultChromosomeArray.add(new Chromosome("chr13", 115169878));
		defaultChromosomeArray.add(new Chromosome("chr14", 107349540));
		defaultChromosomeArray.add(new Chromosome("chr15", 102531392));
		defaultChromosomeArray.add(new Chromosome("chr16", 90354753));
		defaultChromosomeArray.add(new Chromosome("chr17", 81195210));
		defaultChromosomeArray.add(new Chromosome("chr18", 78077248));
		defaultChromosomeArray.add(new Chromosome("chr19", 59128983));
		defaultChromosomeArray.add(new Chromosome("chr20", 63025520));
		defaultChromosomeArray.add(new Chromosome("chr21", 48129895));
		defaultChromosomeArray.add(new Chromosome("chr22", 51304566));
		defaultChromosomeArray.add(new Chromosome("chrX", 155270560));
		defaultChromosomeArray.add(new Chromosome("chrY", 59373566));
		return defaultChromosomeArray;
	}

	
	/**
	 * @return an instance of a {@link ChromosomeManager}. 
	 * Makes sure that there is only one unique instance as specified in the singleton pattern
	 */
	public static ChromosomeManager getInstance() {
		if (cmInstance == null) {
			synchronized(ChromosomeManager.class) {
				if (cmInstance == null) {
					cmInstance = new ChromosomeManager();
				}
			}
		}
		return cmInstance;
	}


	/**
	 * Load the data of the manager.
	 * @param configurationFile configuration file
	 * @throws IOException
	 */
	public void loadConfigurationFile(File configurationFile) throws IOException {
		BufferedReader reader = null;
		try {
			// try to open the input file
			reader = new BufferedReader(new FileReader(configurationFile));
			// extract data
			chromosomeArray = new ArrayList<Chromosome>();
			String line = null;
			int i = 0;
			while((line = reader.readLine()) != null) {
				String[] splitedLine = line.split("\t");
				String name = splitedLine[0].trim();
				int length = Integer.parseInt(splitedLine[1].trim());
				chromosomeArray.add(new Chromosome(name, length));
				i++;
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
			chromosomeHash = new Hashtable<String, Integer>();
			for (int i = 0; i < chromosomeArray.size(); i++) {
				chromosomeHash.put(chromosomeArray.get(i).getName(), i);
			}
		}	
	}	


	/**
	 * @param chromosomeName String representing a chromosome.
	 * @return The index of this chromosome.
	 * @throws InvalidChromosomeException
	 * @throws ManagerDataNotLoadedException
	 */
	public short getIndex(String chromosomeName) throws InvalidChromosomeException, ManagerDataNotLoadedException {
		if (chromosomeArray == null) {
			throw new ManagerDataNotLoadedException();
		}
		if ((chromosomeName != null) && (chromosomeName.length() > 0) && (chromosomeArray != null)) {
			Integer result = chromosomeHash.get(chromosomeName);
			if (result != null) {
				return result.shortValue();
			}
		}
		// If nothing has been found
		throw new InvalidChromosomeException();
	}


	/**
	 * @param chromosome A Chromosome name
	 * @return A Chromosome with chromosomeStr as a name
	 * @throws InvalidChromosomeException
	 * @throws ManagerDataNotLoadedException
	 */
	public short getIndex(Chromosome chromosome) throws InvalidChromosomeException, ManagerDataNotLoadedException {
		if (chromosomeArray == null) {
			throw new ManagerDataNotLoadedException();
		}
		short index = (short)chromosomeArray.indexOf(chromosome);
		if (index == -1) {
			throw new InvalidChromosomeException();
		} else {
			return index;
		}
	}


	/**
	 * @param chromosomeStr A Chromosome name
	 * @return A Chromosome with chromosomeStr as a name
	 * @throws InvalidChromosomeException
	 * @throws ManagerDataNotLoadedException
	 */
	public Chromosome getChromosome(String chromosomeStr) throws InvalidChromosomeException, ManagerDataNotLoadedException {
		if (chromosomeArray == null) {
			throw new ManagerDataNotLoadedException();
		}
		Integer result = chromosomeHash.get(chromosomeStr);
		if (result != null) {
			return chromosomeArray.get(result.intValue());
		}
		// throw an exception if nothing found
		throw new InvalidChromosomeException();
	}


	/**
	 * @param index Index of a chromomsome.
	 * @return The Name of a chromosome. null if not found.
	 * @throws ManagerDataNotLoadedException
	 */
	public Chromosome getChromosome(short index) throws ManagerDataNotLoadedException {
		if (chromosomeArray == null) {
			throw new ManagerDataNotLoadedException();
		}
		if ((index >= 0) && (index < chromosomeArray.size())) {
			return chromosomeArray.get(index);
		}
		// If nothing has been found
		return null;
	}


	/**
	 * @return The value of the greatest chromosome index.
	 * @throws ManagerDataNotLoadedException
	 */
	public short chromosomeCount() throws ManagerDataNotLoadedException {
		if (chromosomeArray == null) {
			throw new ManagerDataNotLoadedException();
		}
		return (short)chromosomeArray.size();
	}


	/**
	 * @return The list with all the chromosomes.
	 * @throws ManagerDataNotLoadedException
	 */
	public Chromosome[] getAllChromosomes() throws ManagerDataNotLoadedException {
		if (chromosomeArray == null) {
			throw new ManagerDataNotLoadedException();
		}
		Chromosome[] res = new Chromosome[chromosomeArray.size()];
		for (int i = 0; i < chromosomeArray.size(); i++) {
			res[i] = chromosomeArray.get(i);  
		}
		return res;
	}


	@Override
	public Iterator<Chromosome> iterator() {
		return chromosomeArray.iterator();
	}
}