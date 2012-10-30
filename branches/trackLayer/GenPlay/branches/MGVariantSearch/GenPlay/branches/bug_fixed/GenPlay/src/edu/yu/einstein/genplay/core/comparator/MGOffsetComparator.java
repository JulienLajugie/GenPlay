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

import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGOffset;

/**
 * Comparator for {@link MGOffset}.
 * The comparator compares first the position between both {@link MGOffset} and then, if equal, their values.
 * The {@link MGOffset} having the highest value is higher than the other {@link MGOffset}.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGOffsetComparator implements Comparator<MGOffset> {

	@Override
	public int compare(MGOffset o1, MGOffset o2) {
		int position1 = o1.getPosition();				// gets the position of the first object
		int position2 = o2.getPosition();				// gets the position of the second object
		
		if (position1 == position2) {					// if positions are equal
			int value1 = o1.getValue();					// gets the value of the first object
			int value2 = o1.getValue();					// gets the value of the second object
			
			if (value1 == value2) {						// if values are equal
				return 0;								// object are equal
			} else if (value1 > value2) {				// if the first value is higher than the second value,
				return 1;								// the first object is higher
			} else {									// if the first value is lower than the second value,
				return -1;								// the first object is lower
			}
		} else if (position1 > position2) {				// if the first position is higher than the second position,
			return 1;									// the first object is higher
		} else {										// if the first position is lower than the second position,
			return -1;									// the first object is lower
		}
	}

}
