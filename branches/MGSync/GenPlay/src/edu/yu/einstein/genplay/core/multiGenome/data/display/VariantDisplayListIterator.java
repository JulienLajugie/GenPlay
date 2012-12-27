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

import java.util.List;

import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.MixVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantDisplayListIterator {

	private final List<Variant> variantList;
	private final byte[] display;
	private int currentIndex;


	/**
	 * Constructor of {@link VariantDisplayListIterator}
	 * @param displayList
	 * @param alleleIndex
	 */
	public VariantDisplayListIterator (VariantDisplayList displayList, int alleleIndex) {
		this.variantList = displayList.getVariants().get(alleleIndex);
		this.display = displayList.getDisplay()[alleleIndex];
		currentIndex = -1;
	}


	/**
	 * @return true if there is an element at the next position, false otherwise
	 */
	public boolean hasNext() {
		return inBound(currentIndex + 1);
	}


	/**
	 * @return the next element in the list
	 */
	public Variant next() {
		currentIndex++;
		return getCurrentVariant();
	}


	/**
	 * @return the current {@link Variant}
	 */
	public Variant getCurrentVariant () {
		if (inBound()) {
			return variantList.get(currentIndex);
		}
		return null;
	}


	private boolean inBound () {
		return inBound(currentIndex);
	}

	private boolean inBound (int index) {
		if ((index >= 0) && (index < variantList.size())) {
			return true;
		}
		return false;
	}

	/**
	 * @return the display policy of the current variant
	 */
	public byte getCurrentDisplay () {
		Variant current = variantList.get(currentIndex);
		if (current instanceof MixVariant) {
			return VariantDisplayList.SHOW;
		}
		return display[currentIndex];
	}

}
