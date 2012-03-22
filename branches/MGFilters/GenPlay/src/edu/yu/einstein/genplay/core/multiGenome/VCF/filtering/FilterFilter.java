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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class FilterFilter implements StringIDFilterInterface, Serializable {

	/** Generated default serial ID*/
	private static final long serialVersionUID = -2473654708635102953L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	
	private String			value;		// category of the filter (ALT QUAL FILTER INFO FORMAT)
	private boolean 		required;	// true if the value is required to pass the the filter

	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(value);
		out.writeBoolean(required);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		value = (String) in.readObject();
		required = in.readBoolean();
	}


	@Override
	public VCFHeaderType getID() {
		return null;
	}


	@Override
	public void setID(VCFHeaderType id) {}
	
	
	@Override
	public String getValue() {
		return value;
	}
	

	@Override
	public void setValue(String value) {
		this.value = value;
	}


	@Override
	public void setRequired(boolean required) {
		this.required = required;
	}


	@Override
	public boolean isRequired() {
		return required;
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
		
		if (value == null) {
			error += "Value missing;";
		}
		
		try {
			if (required) {
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


	@Override
	public void setCategory(String category) {}
	
	
	@Override
	public String getCategory() {
		return null;
	}

	
	@Override
	public boolean isValid(VariantInterface variant) {
		return false;
	}


	@Override
	public boolean isValid(Object value) {
		return false;
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
		FilterFilter test = (FilterFilter)obj;
		return value.equals(test.getValue()) &&
		required == test.isRequired();
	}
}
