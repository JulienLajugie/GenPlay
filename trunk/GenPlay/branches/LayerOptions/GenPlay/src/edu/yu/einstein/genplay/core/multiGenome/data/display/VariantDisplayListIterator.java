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

import java.util.Iterator;
import java.util.List;

import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.MixVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;

/**
 * The {@link VariantDisplayListIterator} iterates over a {@link VariantDisplayList} for a specific allele.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantDisplayListIterator implements Iterator<Variant> {

	private final VariantDisplayList 	displayList;			// The list of variant display.
	private final List<Variant> 		variantList;			// The list of variant.
	private final byte[] 				display;				// The display policy of the variants.
	private boolean 					displayDependant;		// When display dependant, the iterator does not stop on hidden variants.
	private int 						currentIndex;			// The current index while iterating.


	/**
	 * Constructor of {@link VariantDisplayListIterator}
	 * @param displayList
	 * @param alleleIndex
	 */
	public VariantDisplayListIterator (VariantDisplayList displayList, int alleleIndex) {
		this.displayList = displayList;
		this.variantList = displayList.getVariants().get(alleleIndex);
		this.display = displayList.getDisplay()[alleleIndex];
		currentIndex = -1;
		displayDependant = false;
	}


	/**
	 * @return true if there is an element at the next position, false otherwise
	 */
	@Override
	public boolean hasNext() {
		if (displayDependant) {
			return hasVariantAfterPosition(getCurrentMetaGenomePosition());
		}
		return inBound(currentIndex + 1) && isVariantVisible(currentIndex + 1);
	}


	/**
	 * @param metaGenomePosition a position on the meta genome
	 * @return true if there is a variant after the given meta genome position, false otherwise
	 */
	public boolean hasVariantAfterPosition (int metaGenomePosition) {
		boolean found = false;
		int index = currentIndex;
		while (inBound(index) && !found) {
			if ((variantList.get(index).getStart() > metaGenomePosition) && isVariantVisible(index)) {
				found = true;
			}
			index++;
		}
		return found;
	}


	/**
	 * @return true if there is an element at the previous position, false otherwise
	 */
	public boolean hasPrevious() {
		if (displayDependant) {
			return hasVariantBeforePosition(getCurrentMetaGenomePosition());
		}
		return inBound(currentIndex - 1) && isVariantVisible(currentIndex - 1);
	}


	/**
	 * @param metaGenomePosition a position on the meta genome
	 * @return true if there is a variant before the given meta genome position, false otherwise
	 */
	public boolean hasVariantBeforePosition (int metaGenomePosition) {
		boolean found = false;
		int index = currentIndex;
		while (inBound(index) && !found) {
			if ((variantList.get(index).getStart() < metaGenomePosition) && isVariantVisible(index)) {
				found = true;
			}
			index--;
		}
		return found;
	}


	/**
	 * @return the next element in the list
	 */
	@Override
	public Variant next() {
		moveIndexForward();
		return getCurrentVariant();
	}


	/**
	 * @return the previous element in the list
	 */
	public Variant previous() {
		moveIndexBackward();
		return getCurrentVariant();
	}


	/**
	 * Move the index forward
	 */
	private void moveIndexForward () {
		boolean moved = false;
		int index = currentIndex + 1;
		while (inBound(index) && !moved) {
			if (isVariantVisible(index)) {
				moved = true;
				currentIndex = index;
			}
			index++;
		}
	}


	/**
	 * Move the index backward
	 */
	private void moveIndexBackward () {
		boolean moved = false;
		int index = currentIndex - 1;
		while (inBound(index) && !moved) {
			if (isVariantVisible(index)) {
				moved = true;
				currentIndex = index;
			}
			index--;
		}
	}


	/**
	 * @return the current {@link Variant}
	 */
	public Variant getCurrentVariant () {
		if (inBound() && isCurrentVariantVisible()) {
			return variantList.get(currentIndex);
		}
		return null;
	}


	/**
	 * @return the current meta genome position
	 */
	public int getCurrentMetaGenomePosition () {
		if (inBound()) {
			return variantList.get(currentIndex).getStart();
		}
		return 0;
	}


	/**
	 * @return true if the current index is in the list bounds, false otherwise
	 */
	private boolean inBound () {
		return inBound(currentIndex);
	}


	/**
	 * @param index and index
	 * @return	true if the given index is in the list bounds, false otherwise
	 */
	private boolean inBound (int index) {
		if ((index >= 0) && (index < variantList.size())) {
			return true;
		}
		return false;
	}


	/**
	 * @param index the index of a variant
	 * @return the display policy of the current variant
	 */
	private byte getVariantDisplay (int index) {
		Variant current = variantList.get(index);
		if (current instanceof MixVariant) {
			return VariantDisplayList.SHOW;
		}
		return display[index];
	}


	/**
	 * @return the display policy of the current variant
	 */
	public byte getCurrentDisplay () {
		return getVariantDisplay(currentIndex);
	}


	@Override
	public void remove() {}


	/**
	 * Recursive function. Seeks the index of the variant for the given position in order to set the current index.
	 * If no variant is at the position, the next variant is then selected.
	 * @param position	a position on the meta genome
	 */
	public void setIteratorPosition (int position) {
		int index = -1;
		if (variantList.size() > 0) {
			index = getIndex(variantList, position, 0, variantList.size() - 1);
		}
		currentIndex = index;
	}


	/**
	 * Recursive function. Returns the index where the value is found
	 * or the index right after if the exact value is not found.
	 * @param value			value
	 * @param indexStart	start index (in the data array)
	 * @param indexStop		stop index (in the data array)
	 * @return the index where the start value of the window is found or the index right after if the exact value is not found
	 */
	private int getIndex (List<Variant> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else {
			int start = list.get(indexStart + middle).getStart();
			int stop = list.get(indexStart + middle).getStop();
			if ((value >= start) && (value < stop)) {
				return indexStart + middle;
			} else if (value > start) {
				return getIndex(list, value, indexStart + middle + 1, indexStop);
			} else {
				return getIndex(list, value, indexStart, indexStart + middle);
			}
		}
	}


	/**
	 * @return true if the current {@link Variant} is shown, false otherwise
	 */
	private boolean isCurrentVariantVisible () {
		return isVariantVisible(currentIndex);
	}


	/**
	 * @param index the index of a {@link Variant}
	 * @return true if the {@link Variant} at the given index is shown, false otherwise
	 */
	private boolean isVariantVisible (int index) {
		if (displayDependant) {
			return getVariantDisplay(index) >= 0;
		}
		return true;
	}

	/**
	 * @return the displayList
	 */
	public VariantDisplayList getDisplayList() {
		return displayList;
	}


	/**
	 * @param displayDependant the displayDependant to set
	 */
	public void setDisplayDependant(boolean displayDependant) {
		this.displayDependant = displayDependant;
	}


	/**
	 * @return the currentIndex
	 */
	public int getCurrentIndex() {
		return currentIndex;
	}

}
