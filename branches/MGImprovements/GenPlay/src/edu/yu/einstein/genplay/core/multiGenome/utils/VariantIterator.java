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
package edu.yu.einstein.genplay.core.multiGenome.utils;

import java.util.List;
import java.util.ListIterator;

import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantDisplay;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantIterator implements ListIterator<VariantDisplay> {

	private final 	List<VariantDisplay> 	list;
	private 		VariantDisplayPolicy 	variantDisplayPolicy;
	private final 	int 					size;
	private 		int 					currentIndex;
	private 		int						tmpIndex;


	/**
	 * Constructor of {@link VariantIterator}
	 * @param list a list of {@link VariantDisplay}
	 * @param variantDisplayPolicy the variant display policy
	 */
	public VariantIterator (List<VariantDisplay> list, VariantDisplayPolicy variantDisplayPolicy) {
		this.list = list;
		this.size = list.size();
		this.variantDisplayPolicy = variantDisplayPolicy;
		currentIndex = 0;
		tmpIndex = -1;
	}

	/**
	 * Set the display policy for the iteration
	 * @param variantDisplayPolicy the variant display policy
	 */
	public void setVariantDisplayPolicy (VariantDisplayPolicy variantDisplayPolicy) {
		this.variantDisplayPolicy = variantDisplayPolicy;
	}

	@Override
	public void add(VariantDisplay e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasNext() {
		int nextIndex = currentIndex + 1;
		boolean found = false;
		while ((nextIndex < size) && (tmpIndex == -1)) {
			found = variantDisplayPolicy.isShown(list.get(nextIndex));
			if (found) {
				tmpIndex = nextIndex;
			} else {
				nextIndex++;
			}
		}
		return found;
	}

	@Override
	public boolean hasPrevious() {
		int previousIndex = currentIndex - 1;
		boolean found = false;
		while ((previousIndex >= 0) && (tmpIndex == -1)) {
			found = variantDisplayPolicy.isShown(list.get(previousIndex));
			if (found) {
				tmpIndex = previousIndex;
			} else {
				previousIndex--;
			}
		}
		return found;
	}

	@Override
	public VariantDisplay next() {
		if (tmpIndex > -1) {
			currentIndex = tmpIndex;
			tmpIndex = -1;
			return list.get(currentIndex);
		}
		return null;
	}

	@Override
	public int nextIndex() {
		return tmpIndex;
	}

	@Override
	public VariantDisplay previous() {
		if (tmpIndex > -1) {
			currentIndex = tmpIndex;
			tmpIndex = -1;
			return list.get(currentIndex);
		}
		return null;
	}

	@Override
	public int previousIndex() {
		return tmpIndex;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(VariantDisplay e) {
		currentIndex = variantDisplayPolicy.getVariantIndex(e);
	}

	/**
	 * @return the current {@link VariantDisplay}
	 */
	public VariantDisplay getCurrent() {
		VariantDisplay current = null;
		if ((currentIndex >= 0) && (currentIndex < list.size()) && variantDisplayPolicy.isShown(list.get(currentIndex))) {
			current = list.get(currentIndex);
		}
		return current;
	}

}
