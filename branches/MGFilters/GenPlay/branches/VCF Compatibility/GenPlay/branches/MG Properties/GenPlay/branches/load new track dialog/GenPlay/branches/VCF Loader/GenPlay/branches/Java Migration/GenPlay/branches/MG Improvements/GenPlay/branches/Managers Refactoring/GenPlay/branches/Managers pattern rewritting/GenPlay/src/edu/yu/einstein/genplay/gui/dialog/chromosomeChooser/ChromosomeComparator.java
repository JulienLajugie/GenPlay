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
package edu.yu.einstein.genplay.gui.dialog.chromosomeChooser;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * This class compare chromosome regarding their names.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ChromosomeComparator implements Comparator<String> {

	private Map<Character, Integer> charScore;	// Array to set the importance of every special character
	
	
	@Override
	public int compare(String o1, String o2) {
		initCharScore();
		if (chrExists(o1, o2)){
			return chrComparator(o1, o2);
		} else {
			return charComparator(o1, o2, 0);
		}
	}
	
	
	/**
	 * Initializes the character score array
	 */
	private void initCharScore () {
		charScore = new HashMap<Character, Integer>();
		charScore.put('X', 0);
		charScore.put('x', 0);
		charScore.put('Y', 1);
		charScore.put('y', 1);
		charScore.put('M', 2);
		charScore.put('m', 2);
		charScore.put('_', 3);
	}
	
	
	/**
	 * Looks for "chr" string qt the beginning of chromosome name
	 * @param o1	first object to compare
	 * @param o2	second object to compare
	 * @return		true if "chr" string exists in one or both of the input object.
	 */
	private boolean chrExists (String o1, String o2) {
		if (o1.substring(0, 3).equals("chr") || o2.substring(0, 3).equals("chr")){
			return true;
		}
		return false;
	}
	
	
	/**
	 * Compares chromosome regarding their number.
	 * @param o1	first object to compare
	 * @param o2	second object to compare
	 * @return		-1 if o1<o2 / 0 if o1=o2 / 1 if o1>o2
	 */
	private int chrComparator (String o1, String o2) {
		if (o1.substring(0, 3).equals("chr") && o2.substring(0, 3).equals("chr")){
			Integer i1 = getIntegerPart(o1, 3);
			Integer i2 = getIntegerPart(o2, 3);
			if (i1 != -1 && i2 !=-1) {
				return i1.compareTo(i2);
			} else if (i1 != -1 && i2 == -1) {
				return -1;
			} else if (i1 == -1 && i2 != -1) {
				return 1;
			} else {
				return charComparator(o1, o2, 3);
			}
		} else if (o1.substring(0, 3).equals("chr") && !o2.substring(0, 3).equals("chr")){
			return -1;
		} else {
			return 1;
		}
	}
	
	
	/**
	 * Checks if the end of a string is an integer.
	 * @param o		the string
	 * @param pos	the start position
	 * @return		true if it can be an integer
	 */
	private boolean isInteger (String o, int pos) {
		try {
			Integer.parseInt(o.substring(pos, o.length()));
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * Gets the integer part of a string
	 * @param o		the string
	 * @param pos	the start position
	 * @return		an integer or -1 if it is not successful
	 */
	private Integer getIntegerPart (String o, int pos) {
		if (isInteger(o, pos)) {
			return Integer.parseInt(o.substring(pos, o.length()));
		}
		return -1;
	}
	
	
	/**
	 * Checks if a char exists in a string for a specific position.
	 * @param o		the string
	 * @param pos	the position
	 * @return		true if it exists
	 */
	private boolean validChar (String o, int pos) {
		if (pos < o.length()) {
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * Compares character
	 * @param o1	first object to compare
	 * @param o2	second object to compare
	 * @param pos	character position
	 * @return		-1 if o1<o2 / 0 if o1=o2 / 1 if o1>o2
	 */
	private int charComparator (String o1, String o2, int pos) {
		int result = 0;
		int validity;
		boolean valid = true;
		int index = pos;
		while (valid) {
			validity = checkLength(o1, o2, index);
			switch (validity) {
			case 0:
				Character c1 = o1.charAt(index);
				Character c2 = o2.charAt(index);
				int res = charScoreComparator(c1, c2);
				if (res == 0) {
					index++;
				} else {
					if (res != -2){
						result = res;
					} else {
						result = c1.compareTo(c2);
					}
					valid = false;
				}
				break;
			case 1:
				result = 1;
				break;
			case -1:
				result = -1;
				break;
			case -2:
				result = 0;
				break;
			default:
				break;
			}
			if (validity != 0) {
				valid = false;
			}
		}
		return result;
	}
	
	
	/**
	 * Checks validity of a specific position between 2 strings.
	 * @param o1	first object to compare
	 * @param o2	second object to compare
	 * @param index	character position
	 * @return		0 if both are valid
	 * 				1 if o1 is valid but not o2
	 * 				-1 if o2 is valid but not o1
	 * 				-2 if both are not valid
	 */
	private int checkLength (String o1, String o2, int index) {
		int result = 0;
		if (validChar(o1, index) && validChar(o2, index)) {
			result = 0;
		} else {
			if (validChar(o1, index) && !validChar(o2, index)) {
				result = 1;
			} else if (!validChar(o1, index) && validChar(o2, index)) {
				result = -1;
			} else {
				result = -2;
			}
		}
		return result;
	}
	
	
	/**
	 * Compares character including special character
	 * @param c1	first character to compare
	 * @param c2	second character to compare
	 * @return		-1 if c1<c2 / 0 if c1=c2 / 1 if c1>c2
	 */
	private int charScoreComparator (Character c1, Character c2) {
		int result = c1.compareTo(c2);
		if (result != 0) {
			int specialResult1 = -1;
			int specialResult2 = -1;
			if (charScore.containsKey(c1) && charScore.containsKey(c2)) {
				specialResult1 = charScore.get(c1);
				specialResult2 = charScore.get(c2);
				if (specialResult1 < specialResult2) {
					result = -1;
				} else if (specialResult1 == specialResult2) {
					result = 0;
				} else {
					result = 1;
				}
			} else if (charScore.containsKey(c1) && !charScore.containsKey(c2)) {
				result = -1;
			} else if (!charScore.containsKey(c1) && charScore.containsKey(c2)) {
				result = 1;
			} else {
				result = -2;
			}
		}
		return result;
	}
	
}
