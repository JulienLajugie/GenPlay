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
package edu.yu.einstein.genplay.core.parser.genomeWindowParser;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * The {@link PositionParser} is a class that can parse a string in order to retrieve a text and number values.
 * Every parser is based on grammar rules, here are the ones for this parser:
 * - a text value is composed of all capital and small letters (code 65 to 122)
 * - a number value is composed of number 0 to 9 (code 48 to 57)
 * - the list of delimiter is:
 * 		"tab" (code 9)
 * 		"-" (code 45)
 * 		":" (code 58)
 * 		";" (code 59)
 * - a text followed by a number (eg: "hello 5") is considered as a text value
 * - a number followed by a text (eg: "5 hello") is considered as a number value
 * - a number value is an integer (all values out of integer bounds are excluded: -2,147,483,648 to 2,147,483,647 (inclusive))
 * - a number value can contain commas and a dot (as decimal delimiter) in its text format
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class PositionParser {

	private NumberFormat 		format;				// The number format object.
	private List<String> 		textElements;		// The list of text elements.
	private List<String> 		positionElements;	// The list of position (still as a strings).
	private List<Integer> 		numberElements;		// The list of position (as integers).
	private CharacterHandler 	currentCharacter;	// The current character handler.


	/**
	 * Constructor of {@link PositionParser}
	 */
	public PositionParser () {
		initialize();
	}


	/**
	 * Initializes the parser lists.
	 */
	private void initialize () {
		format = NumberFormat.getInstance(Locale.US);
		textElements = new ArrayList<String>();
		positionElements = new ArrayList<String>();
		numberElements = new ArrayList<Integer>();
		currentCharacter = new CharacterHandler();
	}


	/**
	 * Parse a string.
	 * @param s the string to parse
	 */
	public void parse (String s) {
		// Initialize the parser (reset the list).
		initialize();

		// Initialize local parser attributes
		int size = s.length();			// Get the size of the list (avoids several size() calls).
		Boolean isText = null;			// Says if the current string is a text element or a position element.
		String currentBuffer = "";		// The current element in process.

		// Parse
		for (int i = 0; i < size; i++) {
			currentCharacter.initialize(s.charAt(i));
			if (currentCharacter.isDelimiter()) {
				if (isText != null) {
					if (isText) {
						textElements.add(currentBuffer);
					} else {
						positionElements.add(currentBuffer);
					}
					isText = null;
					currentBuffer = "";
				}
			} else {
				currentBuffer += currentCharacter.getCharacter();
				if (currentCharacter.isLetter()) {
					if (isText == null) {
						isText = true;
					}
				} else if (currentCharacter.isInteger() || currentCharacter.isIntegerPart()) {
					if (isText == null) {
						isText = false;
					}
				}
			}
		}

		// Add the last element
		if (isText != null) {
			if (isText) {
				textElements.add(currentBuffer);
			} else {
				positionElements.add(currentBuffer);
			}
		}

		// Transform positions into numbers
		optimizeIntegerList();
	}


	/**
	 * Transforms the list of position (still as text format) in an integer list.
	 */
	private void optimizeIntegerList () {
		numberElements = new ArrayList<Integer>();

		for (String position: positionElements) {
			Number current = null;
			try {
				current = format.parse(position);
			} catch (Exception e) {}
			if (current != null) {
				int currentValue = current.intValue();
				if (isIntBound(currentValue)) {
					numberElements.add(current.intValue());
				}
			}
		}
		Collections.sort(numberElements);
	}


	/**
	 * Integer has a minimum value of -2,147,483,648 and a maximum value of 2,147,483,647 (inclusive).
	 * @param integer	an integer
	 * @return			true if the integer is in the integer bounds, false otherwise
	 */
	private boolean isIntBound (int integer) {
		return ((integer >= -2147483648) || (integer <= 2147483647));
	}


	/**
	 * @param list a list
	 * @return the description of the list
	 */
	private String getListDescription (List<?> list) {
		String description = "";
		int size = list.size();
		if (size == 0) {
			description = "The list is empty.";
		} else {
			for (int i = 0; i < size; i++) {
				description += (i + 1) + ": " + list.get(i).toString();
				if (i < (size - 1)) {
					description += "\n";
				}
			}
		}
		return description;
	}


	/**
	 * @return the textElements
	 */
	public List<String> getTextElements() {
		return textElements;
	}


	/**
	 * @return the numberElements
	 */
	public List<Integer> getNumberElements() {
		return numberElements;
	}


	/**
	 * Shows the content of the parser.
	 */
	public void show () {
		String info = "";
		info += "List of text elements:\n";
		info += getListDescription(textElements) + "\n";
		info += "List of position elements:\n";
		info += getListDescription(positionElements) + "\n";
		info += "List of number elements:\n";
		info += getListDescription(numberElements);
		System.out.println(info);
	}

}
