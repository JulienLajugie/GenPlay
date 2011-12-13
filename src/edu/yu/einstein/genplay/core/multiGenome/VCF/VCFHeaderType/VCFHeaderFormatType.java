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
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages the FORMAT VCF field type information.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFHeaderFormatType implements VCFHeaderAdvancedType {
	
	private static final long serialVersionUID = -6443787383850651884L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 1;			// saved format version
	private String id;				// information ID
	private String description; 	// field description
	private String number;			// the number of values that can be included
	private Class<?> type;			// type of the value. Can be Integer, Float, Character, and String (and Flag for INFO field)
	private Map<Object, Integer> values;
	

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
		out.writeObject(values);
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
		values = (Map<Object, Integer>) in.readObject();
	}
	
	
	/**
	 * Constructor of {@link VCFHeaderFormatType}
	 */
	public VCFHeaderFormatType () {
		values = new HashMap<Object, Integer>();
	}
	
	
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
		//System.out.println(id + ": " + element.toString());
		int cpt = 0;
		if (values.containsKey(element)) {
			cpt = values.get(element) + 1;
		}
		values.put(element, cpt);
	}
	
	
	/**
	 * @return the values found for this header ID
	 */
	public Map<Object, Integer> getElements () {
		return values;
	}


	@Override
	public boolean acceptMoreElements() {
		if (values.size() <= VCFHeaderType.ELEMENT_LIMIT) {
			return true;
		}
		return false;
	}

}
