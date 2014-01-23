/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.core.multiGenome.data.display;

import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;

/**
 * The {@link VariantDisplayMultiListScanner} iterates over several {@link VariantDisplayList} using independant {@link VariantDisplayListIterator}.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantDisplayMultiListScanner {

	private final List<VariantDisplayList> 			lists;				// The lists of variant display.
	private final List<VariantDisplayListIterator> 	iterators;			// The list of iterator.
	private int metaGenomePosition;										// The current meta genome position.


	/**
	 * Constructor of {@link VariantDisplayMultiListScanner}
	 * @param lists the list of {@link VariantDisplayList} to scan
	 */
	public VariantDisplayMultiListScanner (List<VariantDisplayList> lists) {
		this.lists = lists;
		iterators = new ArrayList<VariantDisplayListIterator>();
	}


	/**
	 * Initialize the scanner to go through all alleles of all lists.
	 */
	public void initializeAllAlleles () {
		initialize(null);
	}


	/**
	 * Initialize the scanner to go through one allele of all lists.
	 * @param allele the allele to scan
	 */
	public void initializeOneAllele (int allele) {
		int[] alleles = new int[1];
		alleles[0] = allele;
		initialize(alleles);
	}


	/**
	 * Initialize the scanner to go through alleles of all lists in a diploide genome.
	 * The scan will be therefore perform on the first two alleles.
	 */
	public void initializeDiploide () {
		int[] alleles = new int[2];
		alleles[0] = 0;
		alleles[1] = 1;
		initialize(alleles);
	}


	/**
	 * Initialize the scanner to go through the given allele only but within all lists.
	 * @param alleles an allele index (-1 for all allele)
	 */
	private void initialize (int[] alleles) {
		for (VariantDisplayList current: lists) {
			if (alleles != null) {
				for (int i = 0; i < alleles.length; i++) {
					if (alleles[i] < current.getVariants().size()) {
						VariantDisplayListIterator iterator = new VariantDisplayListIterator(current, alleles[i]);
						iterators.add(iterator);
					}
				}
			} else {
				for (int i = 0; i < current.getVariants().size(); i++) {
					VariantDisplayListIterator iterator = new VariantDisplayListIterator(current, i);
					iterators.add(iterator);
				}
			}
		}
		setPosition(0);
	}


	/**
	 * Set iterators on the right variant according to the given position.
	 * If no variant is found, the following one will be selected
	 * @param position
	 */
	public void setPosition (int position) {
		for (VariantDisplayListIterator iterator: iterators) {
			iterator.setIteratorPosition(position);
		}
		metaGenomePosition = getCurrentSmallestMetaGenomePosition();
	}


	/**
	 * Set the policy on scanning visible variants
	 * @param displayDependant true if the display has to be taken into account (non visible variant will be skipped while scanning), false for a full scan
	 */
	public void setDisplayDependancy (boolean displayDependant) {
		for (VariantDisplayListIterator iterator: iterators) {
			iterator.setDisplayDependant(displayDependant);
		}
	}


	/**
	 * @return true if there is at least one element at the next position, false otherwise
	 */
	public boolean hasNext() {
		for (VariantDisplayListIterator iterator: iterators) {
			if (iterator.hasVariantAfterPosition(metaGenomePosition)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * @return true if there is at least one element at the previous position, false otherwise
	 */
	public boolean hasPrevious() {
		for (VariantDisplayListIterator iterator: iterators) {
			if (iterator.hasVariantBeforePosition(metaGenomePosition)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * @return the next element in the list
	 */
	public List<Variant> next() {
		moveIteratorsForward();
		metaGenomePosition = getCurrentSmallestMetaGenomePosition();
		return getCurrentVariants();
	}


	/**
	 * @return the previous element in the list
	 */
	public List<Variant> previous() {
		moveIteratorsBackward();
		metaGenomePosition = getCurrentHighestMetaGenomePosition();
		return getCurrentVariants();
	}


	/**
	 * @return the smallest meta genome position from all iterators
	 */
	private int getCurrentSmallestMetaGenomePosition () {
		int position = 0;
		boolean firstIterator = true;
		for (VariantDisplayListIterator iterator: iterators) {
			Variant currentVariant = iterator.getCurrentVariant();
			if (currentVariant != null) {
				if (firstIterator) {
					position = currentVariant.getStart();
					firstIterator = false;
				} else {
					position = Math.min(position, currentVariant.getStart());
				}
			}
		}
		return position;
	}


	/**
	 * @return the highest meta genome position from all iterators
	 */
	private int getCurrentHighestMetaGenomePosition () {
		int position = 0;
		for (VariantDisplayListIterator iterator: iterators) {
			Variant currentVariant = iterator.getCurrentVariant();
			if (currentVariant != null) {
				position = Math.max(position, currentVariant.getStart());
			}
		}
		return position;
	}


	/**
	 * @return the list of the current variants found
	 */
	public List<Variant> getCurrentVariants () {
		List<Variant> result = new ArrayList<Variant>();
		for (VariantDisplayListIterator iterator: iterators) {
			Variant currentVariant = iterator.getCurrentVariant();
			if ((currentVariant != null) && (currentVariant.getStart() == metaGenomePosition)) {
				result.add(currentVariant);
			}
		}
		return result;
	}


	/**
	 * Moves current iterators to the next variant
	 */
	private void moveIteratorsForward () {
		for (VariantDisplayListIterator iterator: iterators) {
			int currentMetaGenomePosition = iterator.getCurrentMetaGenomePosition();
			if (currentMetaGenomePosition <= metaGenomePosition) {
				if (iterator.hasNext()) {
					iterator.next();
				}
			}
		}
	}


	/**
	 * Moves current iterators to the previous variant
	 */
	private void moveIteratorsBackward () {
		for (VariantDisplayListIterator iterator: iterators) {
			int currentMetaGenomePosition = iterator.getCurrentMetaGenomePosition();
			if (currentMetaGenomePosition >= metaGenomePosition) {
				if (iterator.hasPrevious()) {
					iterator.previous();
				}
			}
		}
	}


	/**
	 * @return the lists
	 */
	public List<VariantDisplayList> getLists() {
		return lists;
	}


	/**
	 * @param variant a {@link Variant}
	 * @return the {@link VariantDisplayList} of the given {@link Variant}, null if not found
	 */
	public VariantDisplayList getCurrentVariantDisplayList (Variant variant) {
		for (VariantDisplayListIterator iterator: iterators) {
			Variant currentVariant = iterator.getCurrentVariant();
			if (currentVariant != null){
				if (currentVariant.equals(variant)) {
					return iterator.getDisplayList();
				}
			}
		}
		return null;
	}


	/**
	 * @param variant a {@link Variant}
	 * @return the index of the given {@link Variant}, null if not found
	 */
	public int getCurrentVariantIndex (Variant variant) {
		for (VariantDisplayListIterator iterator: iterators) {
			Variant currentVariant = iterator.getCurrentVariant();
			if (currentVariant != null){
				if (currentVariant.equals(variant)) {
					return iterator.getCurrentIndex();
				}
			}
		}
		return -1;
	}


	/**
	 * @param variant a {@link Variant}
	 * @return the current genome name
	 */
	public String getCurrentGenomeName (Variant variant) {
		VariantDisplayList currentDisplayList = getCurrentVariantDisplayList(variant);
		if (currentDisplayList != null) {
			return currentDisplayList.getGenomeName();
		}
		return null;
	}

}
