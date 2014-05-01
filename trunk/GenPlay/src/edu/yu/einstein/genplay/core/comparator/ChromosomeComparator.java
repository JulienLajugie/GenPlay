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
package edu.yu.einstein.genplay.core.comparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;


/**
 * This class compares chromosome regarding their names.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ChromosomeComparator implements Comparator<Chromosome> {

	private Map<Character, Integer> charScore;	// Array to set the importance of every special character


	@Override
	public int compare(Chromosome chromosome1, Chromosome chromosome2) {
		return compareChromosomeName(chromosome1.getName(), chromosome2.getName());
	}


	public int compareChromosomeName(String chromosomeName1, String chromosomeName2) {
		chromosomeName1 = chromosomeName1.toLowerCase();
		chromosomeName2 = chromosomeName2.toLowerCase();

		initCharScore();

		if (startsWithCHR(chromosomeName1) && startsWithCHR(chromosomeName2)) {			// if both strings start with "chr" pattern
			Integer i1 = getInteger(chromosomeName1, 3);								// gets the integer after the "chr" for the first string
			Integer i2 = getInteger(chromosomeName2, 3);								// gets the integer after the "chr" for the second string

			if ((i1 != null) && (i2 != null)) {											// if both have an integer
				return i1.compareTo(i2);												// regular integer comparison

			} else if ((i1 != null) && (i2 == null)) {									// if first string has an integer but second string
				return -1;																// the first string is before

			} else if ((i1 == null) && (i2 != null)) {									// if first string has not an integer but second string
				return 1;																// the second string is before

			} else {																	// if both have no integer
				Integer score1 = getSpecialCharCode(chromosomeName1);					// get the special character code (X,Y,M) after the "chr" of the first string
				Integer score2 = getSpecialCharCode(chromosomeName2);					// get the special character code (X,Y,M) after the "chr" of the second string

				if ((score1 != null) && (score2 != null)) {								// if they both have a special character
					return score1.compareTo(score2);									// regular integer comparison (special characters are related to an integer according to their importance)

				} else if ((score1 != null) && (score2 == null)) {						// if first string has a special character but second string
					return -1;															// the first string is before

				} else if ((score1 == null) && (score2 != null)) {						// if second string has not a special character but second string
					return 1;															// the second string is before
				} else {
					Integer index1 = getUnderScoreCharIndex(chromosomeName1);			// gets the index of the underscore for the first string
					Integer index2 = getUnderScoreCharIndex(chromosomeName2);			// gets the index of the underscore for the second string

					if ((index1 != null) && (index2 != null)) {							// if both strings contain an underscore
						Integer i3 = getInteger(chromosomeName1, 3, index1);			// gets the integer after the "chr" and before the "_" for the first string
						Integer i4 = getInteger(chromosomeName2, 3, index2);			// gets the integer after the "chr" and before the "_" for the second string

						if ((i3 != null) && (i4 != null)) {								// if both have an integer
							int result = i3.compareTo(i4);

							if (result == 0) {
								String newO1 = chromosomeName1.substring(index1 + 1, chromosomeName1.length());
								String newO2 = chromosomeName2.substring(index2 + 1, chromosomeName2.length());
								return compareChromosomeName(newO1, newO2);
							}

							return result;												// regular integer comparison

						} else if ((i3 != null) && (i4 == null)) {						// if first string has an integer but second string
							return -1;													// the first string is before

						} else if ((i3 == null) && (i4 != null)) {						// if first string has not an integer but second string
							return 1;													// the second string is before
						}

					} else if ((index1 != null) && (index2 == null)) {					// if first string has an integer but second string
						return -1;														// the first string is before

					} else if ((index1 == null) && (index2 != null)) {					// if first string has not an integer but second string
						return 1;
					}
				}
			}
		} else if (startsWithCHR(chromosomeName1) && !startsWithCHR(chromosomeName2)) {	// if first string starts with "chr" but second string
			return -1;																	// the first string is before

		} else if (!startsWithCHR(chromosomeName1) && startsWithCHR(chromosomeName2)) {	// if first string does not start with "chr" but second string
			return 1;																	// the second string is before
		}
		StringComparator stringComparator = new StringComparator();
		return stringComparator.compare(chromosomeName1, chromosomeName2);
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
	 * @param text			the string to look in
	 * @param pattern		the pattern to look with
	 * @param startIndex	the start position in the string
	 * @param stopIndex		the stop position in the string
	 * @return				true if the pattern is presents, false otherwise.
	 */
	private boolean isPatternPresent (String text, String pattern, int startIndex, int stopIndex) {
		if (stopIndex < text.length()) {
			return text.substring(startIndex, stopIndex).toLowerCase().equals(pattern);
		} else {
			return false;
		}
	}


	/**
	 * @param s the chromosome name
	 * @return	true if it starts with "chr", false otherwise.
	 */
	private boolean startsWithCHR (String s) {
		return isPatternPresent(s, "chr", 0, 3);
	}

}
