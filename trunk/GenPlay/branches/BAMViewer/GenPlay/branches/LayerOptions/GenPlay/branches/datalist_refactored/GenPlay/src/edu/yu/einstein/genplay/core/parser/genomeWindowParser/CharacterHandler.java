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


/**
 * The {@link CharacterHandler} class handles a single character.
 * It defines what the character is, a character can be:
 * - a text value is composed of all capital and small letters (code 65 to 122)
 * - a number value is composed of number 0 to 9 (code 48 to 57) and eventually has commas and a dot (as decimal delimiter)
 * - a delimiter:
 * 		"tab" (code 9)
 * 		"-" (code 45)
 * 		":" (code 58)
 * 		";" (code 59)
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class CharacterHandler {

	private char 	character;		// The character.
	private boolean delimiter;		// True if the character is a delimiter.
	private boolean integerPart;	// True if the character is a part of an integer.
	private boolean integer;		// True if the character is a integer.
	private boolean letter;			// True if the character is a letter.


	/**
	 * Initialize the handler with a character.
	 * Defines its meaning.
	 * @param c the character
	 */
	public void initialize (char c) {
		character = c;
		delimiter = performDelimiter(character);
		integerPart = performIntegerPart(character);
		integer = performInteger(character);
		letter = performLetter(character);
	}


	/**
	 * Look whether the character is a delimiter or not
	 * @param c	the character
	 * @return	true if it is a delimiter, false otherwise
	 */
	private boolean performDelimiter (char c) {
		boolean result = false;
		// TAB 9
		// - 45
		// : 58
		// ; 59
		if ((c == 9) || (c == 45) || (c == 58) || (c == 59)) {
			result = true;
		}
		return result;
	}


	/**
	 * Look whether the character is part of an integer or not
	 * @param c	the character
	 * @return	true if it is a part of an integer, false otherwise
	 */
	private boolean performIntegerPart (char c) {
		boolean result = false;
		// space 32
		// , 44
		// . 46
		if ((c == 32) || (c == 44) || (c == 46)) {
			result = true;
		}
		return result;
	}


	/**
	 * Look whether the character is an integer or not
	 * @param c	the character
	 * @return	true if it is an integer, false otherwise
	 */
	private boolean performInteger (char c) {
		boolean result = false;
		// from 48 to 57
		if ((c > 47) && (c < 58)) {
			result = true;
		}
		return result;
	}


	/**
	 * Look whether the character is a letter or not
	 * @param c	the character
	 * @return	true if it is a letter, false otherwise
	 */
	private boolean performLetter (char c) {
		boolean result = false;
		// from 65 to 122
		if ((c > 64) && (c < 123)) {
			result = true;
		}
		return result;
	}


	/**
	 * @return the character
	 */
	public char getCharacter() {
		return character;
	}


	/**
	 * @return the delimiter
	 */
	public boolean isDelimiter() {
		return delimiter;
	}


	/**
	 * @return the integerPart
	 */
	public boolean isIntegerPart() {
		return integerPart;
	}


	/**
	 * @return the integer
	 */
	public boolean isInteger() {
		return integer;
	}


	/**
	 * @return the letter
	 */
	public boolean isLetter() {
		return letter;
	}

}
