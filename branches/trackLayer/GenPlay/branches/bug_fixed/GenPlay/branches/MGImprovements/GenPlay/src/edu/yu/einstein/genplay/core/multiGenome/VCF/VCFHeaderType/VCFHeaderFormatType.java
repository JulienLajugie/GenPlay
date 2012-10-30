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
package edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.enums.VCFColumnName;

/**
 * This class manages the FORMAT VCF field type information.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFHeaderFormatType implements VCFHeaderAdvancedType, VCFHeaderElementRecord {
	
	private static final long serialVersionUID = -6443787383850651884L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 1;			// saved format version
	private String id;				// information ID
	private String description; 	// field description
	private String number;			// the number of values that can be included
	private Class<?> type;			// type of the value. Can be Integer, Float, Character, and String (and Flag for INFO field)
	private List<Object> elements;
	

	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(id);
		out.writeObject(description);
		out.writeObject(number);
		out.writeObject(type);
		out.writeObject(elements);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked") // Check for "values" object reading.
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		int savedVersion = in.readInt();
		id = (String) in.readObject();
		description = (String) in.readObject();
		if (savedVersion == 0) { // in version 0 number was an integer
			number = Integer.toString(in.readInt());
		} else {
			number = (String) in.readObject();
		}
		type = (Class<?>) in.readObject();	
		elements = (List<Object>) in.readObject();
	}
	
	
	/**
	 * Constructor of {@link VCFHeaderFormatType}
	 */
	public VCFHeaderFormatType () {
		elements = new ArrayList<Object>();
	}
	
	
	@Override
	public VCFColumnName getColumnCategory() {
		return VCFColumnName.FORMAT;
	}


	@Override
	public void setColumnCategory(VCFColumnName columnCategory) {}
	
	
	@Override
	public String getId() {
		return id;
	}

	
	@Override
	public void setId(String id) {
		this.id = id;
	}

	
	@Override
	public String getDescription() {
		return description;
	}

	
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	
	@Override
	public String getNumber() {
		return number;
	}
	

	@Override
	public void setNumber(String number) {
		this.number = number;
	}

	
	@Override
	public Class<?> getType() {
		return type;
	}

	
	@Override
	public void setType(Class<?> type) {
		this.type = type;
	}


	@Override
	public void addElement(Object element) {
		if (!elements.contains(element)) {
			elements.add(element);
		}
	}
	
	
	/**
	 * @return the values found for this header ID
	 */
	public List<Object> getElements () {
		return elements;
	}


	@Override
	public boolean acceptMoreElements() {
		if (elements.size() <= VCFHeaderType.ELEMENT_LIMIT) {
			return true;
		}
		return false;
	}

	
	@Override
	public String toString () {
		return getColumnCategory() + " - " + id + ": " + description;
	}
	
	
	@Override
	public String getAsOriginalLine() {
		String line = "";
		line += "##FORMAT=<ID=";
		line += id;
		line += ",Number=";
		line += number;
		line += ",Type=";
		if (type == Integer.class) {
			line += "Integer";
		} else if (type == Float.class) {
			line += "Float";
		} else if (type == Boolean.class) {
			line += "Flag";
		} else if (type == char.class) {
			line += "Character";
		} else if (type == String.class) {
			line += "String";
		}
		line += ",Description=\"";
		line += description;
		line += "\">";
		return line;
	}

}
