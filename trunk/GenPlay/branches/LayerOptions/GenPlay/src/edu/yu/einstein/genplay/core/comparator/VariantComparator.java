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
package edu.yu.einstein.genplay.core.comparator;

import java.util.Comparator;

import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.ReferenceVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;


/**
 * This class is a comparator for variant.
 * The comparison is made according to the reference genome position.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantComparator implements Comparator<Variant> {

	@Override
	public int compare(Variant o1, Variant o2) {
		int position1 = o1.getReferenceGenomePosition();
		int position2 = o2.getReferenceGenomePosition();
		//int position1 = o1.getStart();
		//int position2 = o2.getStart();
		if (position1 < position2) {
			return -1;
		} else if (position1 == position2) {
			boolean isReference01 = (o1 instanceof ReferenceVariant);
			boolean isReference02 = (o2 instanceof ReferenceVariant);
			if (isReference01 && !isReference02) {
				return 1;
			} else if (!isReference01 && isReference02) {
				return -1;
			}

			int length1 = o1.getLength();
			int length2 = o2.getLength();
			if (length1 > length2) {
				return -1;				// the longest variant is first
			} else if (length1 < length2) {
				return 1;
			}
			return 0;
		} else {
			return 1;
		}
	}

}
