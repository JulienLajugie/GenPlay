/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;
import yu.einstein.gdp2.util.ChromosomeManager;

/**
 * This class represents a generic list organized by chromosome.
 * @author Julien Lajugie
 * @version 0.1
 */
public class ChromosomeArrayListOfLists<T> extends ArrayList<List<T>> implements Cloneable, Serializable, ChromosomeListOfLists<T> {

	private static final long serialVersionUID = 3989560975472825193L; 	// generated ID
	protected final ChromosomeManager 	chromosomeManager;				// ChromosomeManager
	private Chromosome[] 				savedChromosomes = null;		// Chromosomes of the chromosome manager saved before serialization


	/**
	 * Creates an instance of {@link ChromosomeArrayListOfLists}
	 * @param chromosomeManager a {@link ChromosomeManager}
	 */
	public ChromosomeArrayListOfLists(ChromosomeManager chromosomeManager) {
		super();
		this.chromosomeManager = chromosomeManager;
	}


	/**
	 * @return the {@link ChromosomeManager}
	 */
	public ChromosomeManager getChromosomeManager() {
		return chromosomeManager;
	}


	/**
	 * Saves the chromosomes of the {@link ChromosomeManager} before serialization
	 * @param out {@link ObjectOutputStream}
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		savedChromosomes = chromosomeManager.getAllChromosomes();
		out.defaultWriteObject();
	}


	/**
	 * Checks after unserialization if the current chromosome manager is the same than
	 * the one used when the object was serialized.
	 * If not, retrieves only the corresponding chromosomes of the chromosome managers 
	 * @param in {@link ObjectInputStream}
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		// check if the current chromosome manager and the one used when the object was serialized are similar 
		boolean sameManager = true;
		for (short i = 0; i < chromosomeManager.chromosomeCount(); i++) {
			if ((savedChromosomes == null) || (i >= savedChromosomes.length) || (!savedChromosomes[i].equals(chromosomeManager.getChromosome(i)))) {
				sameManager = false;
			}
		}
		// if the managers are different we look for chromosomes that might have been moved inside the list
		if (sameManager == false) {
			// copy the current list and copy it in a temporary list
			ArrayList<List<T>> listTmp = new ArrayList<List<T>>(this);
			this.clear();
			for (short i = 0; i < chromosomeManager.chromosomeCount(); i++) {
				boolean chromosomeFound = false;
				int j = 0;
				while ((!chromosomeFound) && (j < savedChromosomes.length)) {
					if (savedChromosomes[j].equals(chromosomeManager.getChromosome(i))) {
						chromosomeFound = true;
					} else {
						j++;
					}
				}
				if (chromosomeFound) {
					this.add(listTmp.get(j));
				} else {
					this.add(null);
				}
			}			
		}
	}


	@Override
	public void add(Chromosome chromosome, T element) throws ManagerDataNotLoadedException, InvalidChromosomeException {
		get(chromosomeManager.getIndex(chromosome)).add(element);
	}


	@Override
	public List<T> get(Chromosome chromosome) throws ManagerDataNotLoadedException, InvalidChromosomeException {
		return get(chromosomeManager.getIndex(chromosome));
	}


	@Override
	public T get(int chromosomeIndex, int elementIndex) {
		return get(chromosomeIndex).get(elementIndex);
	}


	@Override
	public T get(Chromosome chromosome, int index) throws ManagerDataNotLoadedException, InvalidChromosomeException {
		return get(chromosomeManager.getIndex(chromosome)).get(index);
	}


	@Override
	public void set(Chromosome chromosome, int index, T element) throws ManagerDataNotLoadedException, InvalidChromosomeException {
		get(chromosomeManager.getIndex(chromosome)).set(index, element);
	}


	@Override
	public void set(Chromosome chromosome, List<T> list) throws ManagerDataNotLoadedException, InvalidChromosomeException {
		set(chromosomeManager.getIndex(chromosome), list);
	}


	@Override
	public void set(int chromosomeIndex, int elementIndex, T element) {
		get(chromosomeIndex).set(elementIndex, element);
	}


	@Override
	public int size(int index) {
		return get(index).size();
	}


	@Override
	public int size(Chromosome chromosome) throws ManagerDataNotLoadedException, InvalidChromosomeException {
		return get(chromosomeManager.getIndex(chromosome)).size();
	}
}
