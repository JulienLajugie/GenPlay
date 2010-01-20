/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list;

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

	private static final long serialVersionUID = 3989560975472825193L; // generated ID
	protected final ChromosomeManager chromosomeManager;	// ChromosomeManager


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
