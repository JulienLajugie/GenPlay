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
package edu.yu.einstein.genplay.core.multiGenome.VCF;

import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.list.arrayList.ByteArrayAsBooleanList;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.IDFilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.MGPosition;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFFilter {

	private IDFilterInterface 		filter;				// the filter
	private VCFFile				reader;				// the file reader
	
	private ByteArrayAsBooleanList 	booleanList;		// list of boolean meaning whether variants pass the filter or not

	
	/**
	 * Constructor of {@link VCFFilter}
	 * @param filter the filter
	 * @param reader the reader
	 */
	public VCFFilter (IDFilterInterface filter, VCFFile reader) {
		this.filter = filter;
		this.reader = reader;
	}

	
	/**
	 * @param variant the variant
	 * @return true if the variant is valid, false otherwise
	 */
	public boolean isValid (VariantInterface variant) {
		MGPosition information = variant.getFullVariantInformation();
		if (information != null) {
			if (reader.equals(information.getReader())) {
				int index = reader.getPositionList().getIndex(information.getPos());
				if (index != -1) {
					return booleanList.get(index);
				}
			}
		}
		return false;
	}
	
	
	/**
	 * Looks for the index of the variant in the array, call this method and use the index to look for this variant in other arrays!
	 * @param variant 	the variant
	 * @return			the index of the variant
	 */
	public int getVariantIndex (VariantInterface variant) {
		int index = -1;
		MGPosition information = variant.getFullVariantInformation();
		if (information != null) {
			if (reader.equals(information.getReader())) {
				index = reader.getPositionList().getIndex(information.getPos());
			}
		}
		return index;
	}
	

	/**
	 * @return the filter
	 */
	public IDFilterInterface getFilter() {
		return filter;
	}


	/**
	 * @return the booleanList
	 */
	public ByteArrayAsBooleanList getBooleanList() {
		return booleanList;
	}
	
	
	/**
	 * @return the reader
	 */
	public VCFFile getReader() {
		return reader;
	}

	
	/**
	 * @param filter the filter to set
	 */
	public void setFilter(IDFilterInterface filter) {
		this.filter = filter;
	}


	/**
	 * @param reader the reader to set
	 */
	public void setReader(VCFFile reader) {
		this.reader = reader;
	}


	/**
	 * Analyzes lines from VCF file in order to determine if variation pass the filter.
	 * @param results list of VCF lines delimited by columns (must contains the column of the filter)
	 */
	public void generateFilter (List<Map<String, Object>> results) {
		reader.initializesPositionList();

		if (results != null) {
			booleanList = new ByteArrayAsBooleanList(reader.getPositionList().size());
			String columnName = filter.getColumnName().toString();
			for (int i = 0; i < results.size(); i++) {
				boolean valid = filter.isValid(results.get(i).get(columnName));
				booleanList.set(i, valid);
			}
		}
	}
	
	
	/**
	 * Shows information about the VCF filter
	 */
	public void show () {
		String info = "";
		info += "VCF File: " + reader.getFile().getName() + "\n";
		info += "Filter display: " + filter.toStringForDisplay() + "\n";
		System.out.println(info);
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj){
			return true;
		}
		if((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		
		// object must be Test at this point
		VCFFilter test = (VCFFilter)obj;
		return reader.equals(test.getReader()) &&
		filter.equals(test.getFilter());
	}
}
