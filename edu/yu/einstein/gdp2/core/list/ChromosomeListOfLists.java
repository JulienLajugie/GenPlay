/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list;

import java.io.Serializable;
import java.util.List;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;

/**
 * This class represents a generic list organized by chromosome.
 * @author Julien Lajugie
 * @version 0.1
 */
public interface ChromosomeListOfLists<T> extends Cloneable, Serializable, List<List<T>> {

//TODO Write a method to return the number of chromosomes in the list. 
	
	/**
	 * Adds an element to the list of the specified chromosome
	 * @param chromosome chromosome of the item 
	 * @param element element to add
	 * @throws ManagerDataNotLoadedException
	 * @throws InvalidChromosomeException
	 */
	public void add(Chromosome chromosome, T element) throws ManagerDataNotLoadedException, InvalidChromosomeException;
	

	/**
	 * @param chromosome a {@link Chromosome}
	 * @return the list associated to the specified {@link Chromosome} 
	 * @throws ManagerDataNotLoadedException
	 * @throws InvalidChromosomeException
	 */
	public List<T> get(Chromosome chromosome) throws ManagerDataNotLoadedException, InvalidChromosomeException;

	
	/**
	 * @param chromosome index of a chromosome
	 * @param index
	 * @return the data with the specified index on the specified chromosome
	 * @throws ManagerDataNotLoadedException
	 * @throws InvalidChromosomeException
	 */
	public T get(Chromosome chromosome, int index) throws ManagerDataNotLoadedException, InvalidChromosomeException;
	
	
	/**
	 * @param chromosomeIndex index of a chromosome
	 * @param elementIndex 
	 * @return the data with the specified index on the specified chromosome
	 */
	public T get(int chromosomeIndex, int elementIndex);
	
	
	/**
	 * Sets the element on the specified index of the specified {@link Chromosome} 
	 * @param chromosome a {@link Chromosome}
	 * @param index
	 * @param element element to set
	 */
	public void set(Chromosome chromosome, int index, T element) throws ManagerDataNotLoadedException, InvalidChromosomeException;
	


	/**
	 * Sets the list of elements on the specified {@link Chromosome} 
	 * @param chromosome a {@link Chromosome}
	 * @param list list to set
	 */
	public void set(Chromosome chromosome, List<T> list) throws ManagerDataNotLoadedException, InvalidChromosomeException;

	
	/**
	 * Sets the element on the specified index of the specified {@link Chromosome} 
	 * @param chromosomeIndex
	 * @param elementIndex
	 * @param element value to set
	 */
	public void set(int chromosomeIndex, int elementIndex, T element);
	
	
	/**
	 * @param index index of a chromosome
	 * @return the size of the list for the specified chromosome
	 */
	public int size(int index);

	
	/**
	 * @param chromosome
	 * @return the size of the list for a specified chromosome
	 * @throws ManagerDataNotLoadedException
	 * @throws InvalidChromosomeException
	 */
	public int size(Chromosome chromosome) throws ManagerDataNotLoadedException, InvalidChromosomeException;
}