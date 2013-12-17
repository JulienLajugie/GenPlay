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

import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.FlagIDFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.IDFilterInterface;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class FlagUtility extends FilterUtility {


	@Override
	protected boolean passTest(IDFilterInterface filter, String value) {
		FlagIDFilter current = getFilter(filter);
		
		boolean found = FilterTester.isStringFound(value, current.getHeaderType().getId());
		return FilterTester.passTest(current.isRequired(), found);
	}


	@Override
	public boolean equals(IDFilterInterface filter, Object obj) {
		FlagIDFilter current = getFilter(filter);
		
		if(current == obj){
			return true;
		}
		if((obj == null) || (obj.getClass() != current.getClass())) {
			return false;
		}

		// object must be Test at this point
		FlagIDFilter test = (FlagIDFilter)obj;
		return current.getHeaderType().equals(test.getHeaderType()) && 
		current.isRequired() == test.isRequired() &&
		current.getGenomeNames() == test.getGenomeNames() &&
		current.getOperator() == test.getOperator();
	}


	@Override
	public String getErrors(IDFilterInterface filter) {
		FlagIDFilter current = getFilter(filter);
		
		String error = "";

		if (filter.getHeaderType() == null) {
			error += "ID missing;";
		}

		try {
			if (current.isRequired()) {
				// instantiation control of the boolean
			}
		} catch (Exception e) {
			error += "Boolean missing;";
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
	private FlagIDFilter getFilter (IDFilterInterface filter) {
		if (filter instanceof FlagIDFilter) {
			return (FlagIDFilter) filter;
		}
		return null;
	}
}
