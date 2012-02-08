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
public class FlagIDFilter implements IDFilterInterface, Serializable {

	/** Generated default serial ID*/
	private static final long serialVersionUID = -2692600453534744380L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	
	private VCFHeaderType 	ID;			// ID of the filter
	private String			category;	// category of the filter (ALT QUAL FILTER INFO FORMAT)
	private boolean 		required;	// true if the value is required to pass the the filter

	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(ID);
		out.writeObject(category);
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
		ID = (VCFHeaderType) in.readObject();
		category = (String) in.readObject();
		required = in.readBoolean();
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
	 * @param required the required to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}


	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}


	@Override
	public String toStringForDisplay() {
		String text = "must be ";
		if (required) {
			text += "present";
		} else {
			text += "absent";
		}
		return text;
	}


	@Override
	public String getErrors() {
		String error = "";
		
		if (ID == null) {
			error += "ID missing;";
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
	public void setCategory(String category) {
		this.category = category;
	}
	
	
	@Override
	public String getCategory() {
		return category;
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
		FlagIDFilter test = (FlagIDFilter)obj;
		return ID.getId().equals(test.getID().getId()) && 
		category.equals(test.getCategory()) &&
		required == test.isRequired();
	}
}
