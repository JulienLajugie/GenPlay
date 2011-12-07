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

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.engine.Variant;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class StringIDFilter implements IDFilter {

	
	private VCFHeaderType 	ID;			// ID of the filter
	private String			category;	// category of the filter (ALT QUAL FILTER INFO FORMAT)
	private String 			value;		// value of the filter
	private boolean 		required;	// true if the value is required to pass the the filter

	@Override
	public boolean passFilter(String genomeFullName, Variant variant) {
		String result = getValue(genomeFullName, variant);
		
		if (required && result != null) {
			return true;
		}
		
		return false;
	}


	@Override
	public VCFHeaderType getID() {
		return ID;
	}


	@Override
	public void setID(VCFHeaderType id) {
		this.ID = id;
	}


	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	
	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}

	
	/**
	 * @param required the required to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	
	@Override
	public String toStringForDisplay() {
		String text = "must ";
		if (required) {
			text += "contains ";
		} else {
			text += "not contains ";
		}
		text += value;
		return text;
	}
	
	
	@Override
	public String getErrors() {
		String error = "";
		
		if (ID == null) {
			error += "ID missing;";
		}
		
		if (value == null) {
			error += "Value missing";
		} else if (value.isEmpty()) {
			error += "Value empty";
		}
		
		try {
			if (required) {
				// instantiation control of the boolean
			}
		} catch (Exception e) {
			error += "Bollean missing;";
		}
		
		if (error.equals("")) {
			return null;
		} else {
			return error;
		}
	}


	@Override
	public void setCategory(String category) {
		this.category = category;
	}
	
	
	/**
	 * Get the value associated to the ID in the variant information.
	 * @param genomeFullName full genome name
	 * @param variant			variant for retrieving information
	 * @return					the value of the ID for specific variant and genome (if apply) or null if not found
	 */
	private String getValue (String genomeFullName, Variant variant) {
		String result = null;
		if (category.equals("ALT")) {
			System.out.println("StringIDFilter getValue ALT not supported");
			
		} else if (category.equals("QUAL")) {
			System.out.println("StringIDFilter getValue QUAL not supported");
			
		} else if (category.equals("FILTER")) {
			System.out.println("StringIDFilter getValue FILTER not supported");
			
		} else if (category.equals("INFO")) {
			result = variant.getPositionInformation().getInfoValue(ID.getId()).toString();
			
		} else if (category.equals("FORMAT")) {
			String rawName = FormattedMultiGenomeName.getRawName(genomeFullName);
			result = variant.getPositionInformation().getFormatValue(rawName, ID.getId()).toString();
			
		}
		
		if (result != null) {
			if (result.indexOf(value) == -1) {
				result = null;
			}
		}
		
		return result;
	}

}
