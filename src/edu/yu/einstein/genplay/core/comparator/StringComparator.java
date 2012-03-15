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


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class StringComparator implements Comparator<String> {


	@Override
	public int compare(String o1, String o2) {
		return compareWords(o1, o2);
	}
	

	/**
	 * Compares two string taking into account numbers than can be present at the end of each string.
	 * @param s1 first string
	 * @param s2 second string
	 * @return		-1 if s1 is before, 1 if after, 0 if they are equal
	 */
	public int compareWords(String s1, String s2) {
		int index = 0;
		while (index < s1.length() && index < s2.length()) {			// while the index is lower than the length of both string
			String c1 = s1.substring(index, (index + 1));				// gets the character at the current index for the first string
			String c2 = s2.substring(index, (index + 1));				// gets the character at the current index for the second string

			Integer i1 = null;											// Tries to parse the current characters into integer value
			Integer i2 = null;
			try {
				i1 = Integer.parseInt(c1);
			} catch (Exception e) {}
			try {
				i2 = Integer.parseInt(c2);
			} catch (Exception e) {}

			if (i1 != null && i2 != null ) {							// If both current characters are integer
				Integer i3 = getFullIntegerPart(s1, index);				// gets the full integer present in the string
				Integer i4 = getFullIntegerPart(s2, index);

				int compare = i3.compareTo(i4);							// regular integer comparison

				if (compare == 0) {										// if they are equal, string comparison must continue
					index += i3.toString().length();					// the new index continues after the integer
				} else {
					return compare;										// if they are not equal, we return the comparison result
				}

			} else if (i1 != null && i2 == null ) {						// if there is an integer in the first string but in the second string
				return -1;												// the first string is before
			} else if (i1 == null && i2 != null ) {						// if there is an integer in the second string but in the first string
				return 1;												// the first string is after
			} else {													// if there is no integer, we continue the scan
				index++;												// increase index by 1
			}

			if (index >= s1.length() && index < s2.length()) {			// if the first string is shorter than the second 
				return -1;												// the first string is before
			} else if (index < s1.length() && index >= s2.length()) {	// if the first string is longer than the second
				return 1;												// the first string is after
			}

			int result = c1.compareToIgnoreCase(c2);					// compares characters
			if (result != 0) {											// if they are not equal
				return result;											// we return the result
			}
		}
		return 0;														// if scan is here, both sting are equal.
	}


	/**
	 * This methods looks for the full integer part in a string from a start index.
	 * @param s		the string
	 * @param index	index of the first integer
	 * @return		the full integer starting at the index
	 */
	private Integer getFullIntegerPart (String s, int index) {
		Integer result = null;									// Initialize the result to null
		int nextIndex = index + 1;								// Next index is initialized with index + 1
		while (nextIndex <= s.length()) {						// while the next index is shorter or equal to the string length
			String text = s.substring(index, nextIndex);		// gets the sub string from the string (index to next index)
			try {
				result = Integer.parseInt(text);				// tries to get the integer part
			} catch (Exception e) {								// if there is no integer part
				return result;									// we return result (that contains the previous integer part or null)
			}
			nextIndex++;										// if it worked, we keep looking in the string increasing the next index
		}
		return result;											// return the result of the scan
	}
}