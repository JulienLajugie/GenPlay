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

import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.GenotypeIDFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.IDFilterInterface;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GenotypeUtility extends FilterUtility {


	@Override
	protected boolean passTest(IDFilterInterface filter, String value) {
		GenotypeIDFilter current = getFilter(filter);
		
		if (value.length() == 3) {
			String phase = value.substring(1, 2);
			if (phase.equals("/") && current.canBeUnPhased() || phase.equals("|") && current.canBePhased()) {
				String firstAllele = value.substring(0, 1);
				String secondAllele = value.substring(2);
				
				if (firstAllele.equals(".") || secondAllele.equals(".")) {
					return true;
				}
				
				if (firstAllele.equals(secondAllele) && current.getOption() == GenotypeIDFilter.HOMOZYGOTE_OPTION) {
					return true;
				}
				
				if (!firstAllele.equals(secondAllele) && current.getOption() == GenotypeIDFilter.HETEROZYGOTE_OPTION) {
					return true;
				}
			}
		}
		
		return false;
	}


	@Override
	public boolean equals(IDFilterInterface filter, Object obj) {
		GenotypeIDFilter current = getFilter(filter);
		
		if(current == obj){
			return true;
		}
		if((obj == null) || (obj.getClass() != current.getClass())) {
			return false;
		}

		// object must be Test at this point
		GenotypeIDFilter test = (GenotypeIDFilter)obj;
		return current.getHeaderType().equals(test.getHeaderType()) && 
		current.getOption() == test.getOption() &&
		current.canBePhased() == test.canBePhased() &&
		current.canBeUnPhased() == test.canBeUnPhased() &&
		current.getGenomeNames() == test.getGenomeNames() &&
		current.getOperator() == test.getOperator();
	}


	@Override
	public String getErrors(IDFilterInterface filter) {
		GenotypeIDFilter current = getFilter(filter);
		
		String error = "";

		if (current.getHeaderType() == null) {
			error += "ID missing;";
		}

		if (current.getOption() != GenotypeIDFilter.HETEROZYGOTE_OPTION && current.getOption() != GenotypeIDFilter.HOMOZYGOTE_OPTION) {
			error += "Please select the option: heterozygote or homozygote";
		}
		
		if (!current.canBePhased() && !current.canBeUnPhased()) {
			error += "Please select a phasing constraint";
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
	private GenotypeIDFilter getFilter (IDFilterInterface filter) {
		if (filter instanceof GenotypeIDFilter) {
			return (GenotypeIDFilter) filter;
		}
		return null;
	}
}
