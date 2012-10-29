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

import edu.yu.einstein.genplay.core.enums.InequalityOperators;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.IDFilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.NumberIDFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.NumberIDFilterInterface;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class NumberUtility extends FilterUtility {


	@Override
	protected boolean passTest(IDFilterInterface filter, String value) {
		NumberIDFilterInterface current = getFilter(filter);
		Float f = toFloat(value);
		
		if (f != null) {
			Boolean result01 = FilterTester.passInequation(current.getInequation01(), current.getValue01(), f);
			Boolean result02 = null;
			if (current.getInequation02() != null) {
				result02 = FilterTester.passInequation(current.getInequation02(), current.getValue02(), f);
			}

			if (current.isCumulative()) {		// cumulative treatment
				if (result02 != null) {
					return (result01 & result02);
				}
				return result01;
			} else {				// non cumulative treatment
				if (result01 || result02) {
					return true;
				}
			}
		}

		return false;
	}


	@Override
	public boolean equals(IDFilterInterface filter, Object obj) {
		NumberIDFilterInterface current = getFilter(filter);

		if(filter == obj){
			return true;
		}
		if((obj == null) || (obj.getClass() != filter.getClass())) {
			return false;
		}

		// object must be Test at this point
		NumberIDFilter test = (NumberIDFilter)obj;

		return filter.getHeaderType().equals(test.getHeaderType()) && 
		compareInequation(current.getInequation01(), test.getInequation01()) &&
		compareInequation(current.getInequation02(), test.getInequation02()) &&
		current.getValue01() == test.getValue01() &&
		current.getValue02() == test.getValue02() &&
		current.isCumulative() == test.isCumulative() &&
		current.getGenomeNames() == test.getGenomeNames() &&
		current.getOperator() == test.getOperator();
	}
	
	
	/**
	 * Compares two equations
	 * @param inequation01 first equation
	 * @param inequation02 second equation
	 * @return true if they are equal, false if not
	 */
	private static boolean compareInequation (InequalityOperators inequation01, InequalityOperators inequation02) {
		if (inequation01 == null && inequation02 == null) {
			return true;
		} else if (inequation01 == null && inequation02 != null) {
			return false;
		} else if (inequation01 != null && inequation02 == null) {
			return false;
		} else {
			return inequation01.toString().equals(inequation02.toString());
		}
	}


	@Override
	public String getErrors(IDFilterInterface filter) {
		NumberIDFilterInterface current = getFilter(filter);

		String error = "";

		if (filter.getHeaderType() == null) {
			error += "ID missing;";
		}

		if (current.getInequation01() == null || current.getInequation01().equals(" ")) {
			error += "First inequation invalid;";
		}

		if (current.getValue01() == null) {
			error += "First value invalid;";
		}

		if (current.getInequation02() != null && current.getInequation02().equals(" ") && current.getValue02() != null) {
			error += "Second sign inequation missing";
		}

		if (current.getInequation02() != null && !current.getInequation02().equals(" ") && current.getValue02() == null) {
			error += "Second value missing";
		}

		if (error.equals("")) {
			return null;
		} else {
			return error;
		}
	}
	
	
	/**
	 * Checks if the filter is valid according to the current class
	 * @param filter the filter to check
	 * @return the casted filter if valid, null otherwise
	 */
	private NumberIDFilterInterface getFilter (IDFilterInterface filter) {
		if (filter instanceof NumberIDFilterInterface) {
			return (NumberIDFilterInterface) filter;
		}
		return null;
	}

}
