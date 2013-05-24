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
 * 		"-" (code 45)  (if not preceded by a delimiter and if not first character, otherwise it's considered as a minus sign)
 * 		":" (code 58)
 * 		";" (code 59)
 * 
 * @author Nicolas Fourel
 */
public class CharacterHandler {

	private char 				currentChar;	// The character.
	private boolean 			isDelimiter;	// True if the character is a delimiter.
	private boolean 			isIntegerPart;	// True if the character is a part of an integer.
	private boolean 			isInteger;		// True if the character is a integer.
	private boolean 			isLetter;		// True if the character is a letter.


	/**
	 * @return the character
	 */
	public char getCharacter() {
		return currentChar;
	}


	/**
	 * Initialize the handler with a character.
	 * Defines its meaning.
	 * @param currentChar the character
	 * @param previousChar the previous character. (needed to determine if a '-' is a separator or a minus sign). Can be null.
	 */
	public void initialize (char currentChar, CharacterHandler previousChar) {
		this.currentChar = currentChar;
		isDelimiter = performDelimiter(currentChar, previousChar);
		isIntegerPart = performIntegerPart(currentChar, previousChar);
		isInteger = performInteger(currentChar);
		isLetter = performLetter(currentChar);
	}


	/**
	 * @return true if the character is a delimiter
	 */
	public boolean isDelimiter() {
		return isDelimiter;
	}


	/**
	 * @return true if the character is a integer
	 */
	public boolean isInteger() {
		return isInteger;
	}


	/**
	 * @return true if the character is a integerPart
	 */
	public boolean isIntegerPart() {
		return isIntegerPart;
	}


	/**
	 * @return true if the character is a letter
	 */
	public boolean isLetter() {
		return isLetter;
	}


	/**
	 * Look whether the character is a delimiter or not
	 * @param c	the character
	 * @return	true if it is a delimiter, false otherwise
	 * @param previousChar the previous character. (needed to determine if a '-' is a separator or a minus sign). Can be null.
	 */
	private boolean performDelimiter (char c, CharacterHandler previousChar) {
		// TAB 9
		// : 58
		// ; 59
		// - 45
		return ((c == 9) || (c == 58) || (c == 59) || ((c == 45) && (previousChar != null ) && (!previousChar.isDelimiter)));
	}


	/**
	 * Look whether the character is an integer or not
	 * @param c	the character
	 * @return	true if it is an integer, false otherwise
	 */
	private boolean performInteger (char c) {
		// from 48 to 57
		return ((c > 47) && (c < 58));
	}


	/**
	 * Look whether the character is part of an integer or not
	 * @param c	the character
	 * @return	true if it is a part of an integer, false otherwise
	 * @param previousChar the previous character. (needed to determine if a '-' is a separator or a minus sign). Can be null.
	 */
	private boolean performIntegerPart (char c, CharacterHandler previousChar) {
		// space 32
		// , 44
		// . 46
		// - 45
		return ((c == 32) || (c == 44) || (c == 46) || ((c == 45) && ((previousChar == null ) || (previousChar.isDelimiter))));
	}


	/**
	 * Look whether the character is a letter or not
	 * @param c	the character
	 * @return	true if it is a letter, false otherwise
	 */
	private boolean performLetter (char c) {
		// from 65 to 122
		return ((c > 64) && (c < 123));
	}

}
