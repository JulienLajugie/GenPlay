/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.dataStructure.list.genomicDataList;

import java.io.Serializable;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;


/**
 * This class represents an immutable list of object organized by chromosome
 * @param <T> type of the objects stored in the list
 * @author Julien Lajugie
 */
public interface ImmutableGenomicDataList <T> extends Serializable, Iterable<List<T>> {


	/**
	 * @param chromosome index of a chromosome
	 * @param index
	 * @return the data with the specified index on the specified chromosome
	 * @throws InvalidChromosomeException
	 */
	public T get(Chromosome chromosome, int index) throws InvalidChromosomeException;


	/**
	 * @param chromosomeIndex index of a chromosome
	 * @param elementIndex
	 * @return the data with the specified index on the specified chromosome
	 */
	public T get(int chromosomeIndex, int elementIndex);


	/**
	 * @param chromosome a {@link Chromosome}
	 * @return a view to the list associated to the specified {@link Chromosome}. The returned list is immutable (read only)
	 * @throws InvalidChromosomeException
	 */
	public List<T> getView(Chromosome chromosome) throws InvalidChromosomeException;


	/**
	 * @param chromosomeIndex index of a chromosome
	 * @return a view to the list at the specified index. The returned list is immutable (read only)
	 */
	public List<T> getView(int chromosomeIndex);


	/**
	 * @return the number of chromosome lists.
	 */
	public int size();


	/**
	 * @param chromosome
	 * @return the size of the list for a specified chromosome
	 * @throws InvalidChromosomeException
	 */
	public int size(Chromosome chromosome) throws InvalidChromosomeException;


	/**
	 * @param chromosomeIndex index of a chromosome
	 * @return the size of the list for the specified chromosome
	 */
	public int size(int chromosomeIndex);
}
