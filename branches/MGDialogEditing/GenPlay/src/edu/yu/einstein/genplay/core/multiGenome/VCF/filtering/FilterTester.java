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
package edu.yu.einstein.genplay.core.multiGenome.VCF.filtering;

import edu.yu.einstein.genplay.core.enums.InequalityOperators;
import edu.yu.einstein.genplay.core.enums.VCFColumnName;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
class FilterTester {

	
	/**
	 * Checks if a pattern is found in an object that is supposed to be a string.
	 * @param object	the object associated to a {@link VCFColumnName} (not the full VCF line)
	 * @param pattern	the pattern
	 * @return			true if the pattern is found, false otherwise
	 */
	protected static boolean isStringFound (Object object, String pattern) {
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
	protected static boolean isStringFound (String fullLine, String pattern) {
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
	protected static boolean passTest (boolean required, boolean found) {
		if (required && found || !required && !found) {
			return true;
		}
		
		return false;
	}

	
	/**
	 * Retrieves the float value within a string.
	 * According to the column, the value can be the full line associated to the current column, or part of it.
	 * @param columnName	the {@link VCFColumnName} associated to the line
	 * @param line			line associated to the {@link VCFColumnName}
	 * @param IDName		the name of the ID to look for
	 * @return				the float value of the ID, null otherwise
	 */
	protected static Float getFloatValue (VCFColumnName columnName, String line, String IDName) {
		String result = getStringValue(columnName, line, IDName);
		Float f = null;
		if (result != null) {
			try {
				f = Float.parseFloat(result.toString());
			} catch (Exception e) {}
		}
		return f;
	}
	
	
	/**
	 * Retrieves a String value within a string.
	 * According to the column, the value can be the full line associated to the current column, or part of it.
	 * @param columnName	the {@link VCFColumnName} associated to the line
	 * @param line			line associated to the {@link VCFColumnName}
	 * @param IDName		the name of the ID to look for
	 * @return				the string value of the ID, null otherwise
	 */
	protected static String getStringValue (VCFColumnName columnName, String line, String IDName) {
		String result = null;
		
		if (columnName == VCFColumnName.ALT) {				// Columns ALT, QUAL, FILTER are not composed of different ID
			result = line;									// the value to get is necessary the full line!

		} else if (columnName == VCFColumnName.QUAL) {
			result = line;
			
		} else if (columnName == VCFColumnName.FILTER) {
			result = line;
			
		} else if (columnName == VCFColumnName.INFO) {		// Columns INFO and FORMAT gather different ID (; or : delimited)
			result = getInfoValue(line, IDName);			// a more complex process is used to locate and retireve the ID value
			
		} else if (columnName == VCFColumnName.FORMAT) {
			System.out.println("NumberIDFilter getValue FORMAT not supported");		// must be developped
			// TODO

		}
		
		return result;
	}
	
	
	/**
	 * Gets the value according to the INFO field and a specific field
	 * @param info	the INFO string
	 * @param field	the specific field
	 * @return		the value of the specific field of the INFO field
	 */
	private static String getInfoValue (String info, String field) {
		String result = null;
		
		if (info != null && field != null) {
			int indexInString = info.indexOf(field);
			if (indexInString != -1) {
				int start = indexInString + field.length() + 1;
				int stop = info.indexOf(";", start);
				if (stop == -1) {
					stop = info.length();
				}
				result = info.substring(start, stop);
			}
		}
		
		return result;
	}
	
	
	/**
	 * Compare to float in order to define if they correlate the inequation.
	 * @param inequation		an inequation
	 * @param referenceValue	a first value
	 * @param valueToCompare	a second value
	 * @return					true if both values correlate the inequation, false otherwise.
	 */
	protected static boolean passInequation (InequalityOperators inequation, Float referenceValue, Float valueToCompare) {
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
