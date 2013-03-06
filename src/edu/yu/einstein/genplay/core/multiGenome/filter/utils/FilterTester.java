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
package edu.yu.einstein.genplay.core.multiGenome.filter.utils;

import edu.yu.einstein.genplay.dataStructure.enums.InequalityOperators;
import edu.yu.einstein.genplay.dataStructure.enums.VCFColumnName;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class FilterTester {


	/**
	 * Checks if a pattern is found in an object that is supposed to be a string.
	 * @param object	the object associated to a {@link VCFColumnName} (not the full VCF line)
	 * @param pattern	the pattern
	 * @return			true if the pattern is found, false otherwise
	 */
	public static boolean isStringFound (Object object, String pattern) {
		boolean found = false;
		if (object != null) {
			found = isStringFound(object.toString(), pattern);
		} else {
			System.out.println("FilterTester.isStringFound()");
			System.out.println("value == null");
		}
		return found;
	}


	/**
	 * Checks if a pattern is found in a string.
	 * @param fullLine	the string associated to a {@link VCFColumnName} (not the full VCF line) 
	 * @param pattern	the pattern
	 * @return			true if the pattern is found, false otherwise
	 */
	public static boolean isStringFound (String fullLine, String pattern) {
		boolean found = false;
		if (fullLine.indexOf(pattern) != -1) {
			found =  true;
		}
		return found;
	}


	/**
	 * Performs the validation using a boolean "required" and a boolean "found".
	 * An attribute can be found or not, required or not.
	 * @param required	a boolean meaning the attribute is required or not
	 * @param found		a boolean meaning the attribute has been found or not
	 * @return			true if the attribute is required and found, or, not required and not found.
	 */
	public static boolean passTest (boolean required, boolean found) {
		if (required && found || !required && !found) {
			return true;
		}

		return false;
	}


	/**
	 * Compare to float in order to define if they correlate the inequation.
	 * @param inequation		an inequation
	 * @param referenceValue	a first value
	 * @param valueToCompare	a second value
	 * @return					true if both values correlate the inequation, false otherwise.
	 */
	public static boolean passInequation (InequalityOperators inequation, Float referenceValue, Float valueToCompare) {
		boolean valid = false;

		if (valueToCompare == null || referenceValue == null) {
			return valid;
		}

		if (valueToCompare < 0) {
			valueToCompare = valueToCompare * -1;
		}

		int result = valueToCompare.compareTo(referenceValue);

		if (inequation == InequalityOperators.EQUAL) {
			if (result == 0) {
				valid = true;
			}
		} else if (inequation == InequalityOperators.SUPERIOR) {
			if (result > 0) {
				valid = true;
			}
		} else if (inequation == InequalityOperators.SUPERIOR_OR_EQUAL) {
			if (result >= 0) {
				valid = true;
			}
		} else if (inequation == InequalityOperators.INFERIOR) {
			if (result < 0) {
				valid = true;
			}
		} else if (inequation == InequalityOperators.INFERIOR_OR_EQUAL) {
			if (result <= 0) {
				valid = true;
			}
		}

		return valid;
	}

}
