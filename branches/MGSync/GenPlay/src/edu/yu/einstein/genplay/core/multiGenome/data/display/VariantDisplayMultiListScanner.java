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
package edu.yu.einstein.genplay.core.multiGenome.data.display;

import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.comparator.VariantDisplayComparator;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantDisplayMultiListScanner {

	private final List<VariantDisplayList> lists;
	private final List<VariantDisplayListIterator> iterators;
	private final VariantDisplayComparator comparator;
	private List<Integer> currentIteratorIndexes;


	/**
	 * Constructor of {@link VariantDisplayMultiListScanner}
	 * @param lists the list of {@link VariantDisplayList} to scan
	 */
	public VariantDisplayMultiListScanner (List<VariantDisplayList> lists) {
		this.lists = lists;
		iterators = new ArrayList<VariantDisplayListIterator>();
		comparator = new VariantDisplayComparator();
		currentIteratorIndexes = new ArrayList<Integer>();

	}


	/**
	 * Initialize the scanner to go through all alleles of all lists.
	 */
	public void initialize () {
		initialize(-1);
	}


	/**
	 * Initialize the scanner to go through the given allele only but within all lists.
	 * @param allele an allele index (-1 for all allele)
	 */
	public void initialize (int allele) {
		int cpt = 0;
		for (VariantDisplayList current: lists) {
			if (allele > -1) {
				VariantDisplayListIterator iterator = null;
				if (allele < current.getVariants().size()) {
					iterator = new VariantDisplayListIterator(current, allele);
				}
				iterators.add(iterator);
				currentIteratorIndexes.add(cpt);
				cpt++;
			} else {
				for (int i = 0; i < current.getVariants().size(); i++) {
					VariantDisplayListIterator iterator = new VariantDisplayListIterator(current, i);
					iterators.add(iterator);
					currentIteratorIndexes.add(cpt);
					cpt++;
				}
			}
		}
	}


	/**
	 * @return true if there is an element at the next position, flase otherwise
	 */
	public boolean hasNext() {
		for (VariantDisplayListIterator iterator: iterators) {
			if ((iterator != null) && iterator.hasNext()) {
				return true;
			}
		}
		return false;
	}


	/**
	 * @return the next element in the list
	 */
	public List<Variant> next() {
		moveIterators();
		findClosestIteratorIndexes();
		return getCurrentVariants();
	}


	/**
	 * Defines the next closest iterator indexes.
	 */
	private void findClosestIteratorIndexes () {
		int index = findFirstValidIndex();
		if (index != -1) {
			newIteratorIndex(index);
			for (int i = 0; i < iterators.size(); i++) {
				if ((iterators.get(i) != null) && (i != index)) {
					int compare = comparator.compare(iterators.get(i).getCurrentVariant(), iterators.get(index).getCurrentVariant());
					if (compare == -1) {
						newIteratorIndex(i);
					} else if (compare == 0) {
						addIteratorIndex(i);
					}
				}
			}
		} else {
			currentIteratorIndexes = new ArrayList<Integer>();
		}
	}


	/**
	 * @return the first existing and valid iterator index
	 */
	private int findFirstValidIndex () {
		int index = -1;
		for (int i = 0; i < iterators.size(); i++) {
			if (iterators.get(i) != null) {
				return i;
			}
		}
		return index;
	}


	/**
	 * @return the list of the current variants found
	 */
	private List<Variant> getCurrentVariants () {
		List<Variant> result = new ArrayList<Variant>();
		for (Integer index: currentIteratorIndexes) {
			result.add(iterators.get(index).getCurrentVariant());
		}
		return result;
	}


	/**
	 * Create a new list of current iterator indexes with the given index
	 * @param index
	 */
	private void newIteratorIndex (int index) {
		currentIteratorIndexes = new ArrayList<Integer>();
		addIteratorIndex(index);
	}


	/**
	 * Add the given index to the current list of iterator indexes
	 * @param index
	 */
	private void addIteratorIndex (int index) {
		currentIteratorIndexes.add(index);
	}


	/**
	 * Moves current iterators to the next variant
	 */
	private void moveIterators () {
		for (Integer index: currentIteratorIndexes) {
			if (iterators.get(index).hasNext()) {
				iterators.get(index).next();
			} else {
				iterators.set(index, null);
			}
		}
	}


	/**
	 * @return the lists
	 */
	public List<VariantDisplayList> getLists() {
		return lists;
	}

}
