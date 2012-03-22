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
package edu.yu.einstein.genplay.core.chromosome;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * This class compares chromosome regarding their names.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ChromosomeComparator implements Comparator<String> {

	private Map<Character, Integer> charScore;	// Array to set the importance of every special character


	@Override
	public int compare(String o1, String o2) {
		initCharScore();

		if (startsWithCHR(o1) && startsWithCHR(o2)) {			// if both strings start with "chr" pattern
			Integer i1 = getInteger(o1, 3);						// gets the integer after the "chr" for the first string
			Integer i2 = getInteger(o2, 3);						// gets the integer after the "chr" for the second string

			if (i1 != null && i2 != null) {						// if both have an integer
				return i1.compareTo(i2);						// regular integer comparison

			} else if (i1 != null && i2 == null) {				// if first string has an integer but second string
				return -1;										// the first string is before

			} else if (i1 == null && i2 != null) {				// if first string has not an integer but second string
				return 1;										// the second string is before

			} else {											// if both have no integer
				Integer score1 = getSpecialCharCode(o1);		// get the special character code (X,Y,M) after the "chr" of the first string
				Integer score2 = getSpecialCharCode(o2);		// get the special character code (X,Y,M) after the "chr" of the second string

				if (score1 != null && score2 != null) {			// if they both have a special character
					return score1.compareTo(score2);			// regular integer comparison (special characters are related to an integer according to their importance)

				} else if (score1 != null && score2 == null) {	// if first string has a special character but second string 
					return -1;									// the first string is before

				} else if (score1 == null && score2 != null) {	// if second string has not a special character but second string
					return 1;									// the second string is before
				} else {
					Integer index1 = getUnderScoreCharIndex(o1);	// gets the index of the underscore for the first string
					Integer index2 = getUnderScoreCharIndex(o2);	// gets the index of the underscore for the second string

					if (index1 != null && index2 != null) {			// if both strings contain an underscore
						Integer i3 = getInteger(o1, 3, index1);		// gets the integer after the "chr" and before the "_" for the first string
						Integer i4 = getInteger(o2, 3, index2);		// gets the integer after the "chr" and before the "_" for the second string

						if (i3 != null && i4 != null) {				// if both have an integer
							int result = i3.compareTo(i4);

							if (result == 0) {
								String newO1 = o1.substring(index1 + 1, o1.length());
								String newO2 = o2.substring(index2 + 1, o2.length());
								return compare(newO1, newO2);
							}

							return result;							// regular integer comparison

						} else if (i3 != null && i4 == null) {		// if first string has an integer but second string
							return -1;								// the first string is before

						} else if (i3 == null && i4 != null) {		// if first string has not an integer but second string
							return 1;								// the second string is before
						}

					} else if (index1 != null && index2 == null) {		// if first string has an integer but second string
						return -1;										// the first string is before

					} else if (index1 == null && index2 != null) {		// if first string has not an integer but second string
						return 1;
					}
				}
			}
		} else if (startsWithCHR(o1) && !startsWithCHR(o2)) {	// if first string starts with "chr" but second string
			return -1;											// the first string is before

		} else if (!startsWithCHR(o1) && startsWithCHR(o2)) {	// if first string does not start with "chr" but second string
			return 1;											// the second string is before
		}
		// if both do not start with "chr" pattern
		//return o1.compareToIgnoreCase(o2);						// regular string comparison

		return compareWords(o1, o2);
	}


	/**
	 * Initializes the character score array
	 */
	private void initCharScore () {
		charScore = new HashMap<Character, Integer>();
		charScore.put('x', 0);
		charScore.put('y', 1);
		charScore.put('m', 2);
		charScore.put('_', 3);
	}


	/**
	 * @param s the chromosome name
	 * @return	true if it starts with "chr", false otherwise.
	 */
	private boolean startsWithCHR (String s) {
		return isPatternPresent(s, "chr", 0, 3);
	}


	/**
	 * @param text			the string to look in
	 * @param pattern		the pattern to look with
	 * @param startIndex	the start position in the string
	 * @param stopIndex		the stop position in the string
	 * @return				true if the pattern is presents, false otherwise.
	 */
	private boolean isPatternPresent (String text, String pattern, int startIndex, int stopIndex) {
		return text.substring(startIndex, stopIndex).toLowerCase().equals(pattern);
	}


	/**
	 * @param s				the string
	 * @param startIndex	the start index
	 * @param stopIndex		the stop index
	 * @return				the integer part of the string, null otherwise
	 */
	private Integer getInteger (String s, int startIndex, int stopIndex) {
		Integer result;
		try {
			result = Integer.parseInt(s.substring(startIndex, stopIndex));
		} catch (Exception e) {
			result = null;
		}
		return result;
	}


	/**
	 * @param s				the string
	 * @param startIndex	the start index
	 * @return				the integer part of the string, null otherwise
	 */
	private Integer getInteger (String s, int startIndex) {
		return getInteger(s, startIndex, s.length());
	}


	/**
	 * Looks for the score of the special character (if it exists) at the index 3 of a string.
	 * This method is used for chromosome name like "chr..." (chrX, chrY, chrM...).
	 * @param s	the string
	 * @return	the score of the special character
	 */
	private Integer getSpecialCharCode (String s) {
		char c = s.charAt(3);
		Integer score = null;
		if (charScore.containsKey(c)) {
			score = charScore.get(c);
		}
		return score;
	}


	/**
	 * @param s the string
	 * @return	the index of the first occurence of the underscore in the string, null it it does not exist
	 */
	private Integer getUnderScoreCharIndex (String s) {
		Integer index = s.indexOf("_");
		if (index > -1) {
			return index;
		}
		return null;
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