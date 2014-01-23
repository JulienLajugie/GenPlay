/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.enums.VCFColumnName;

/**
 * This class manages headers that don't always have defined ID such as:
 * - ALT
 * - QUAL
 * - FILTER
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFHeaderBasicType implements VCFHeaderType, VCFHeaderElementRecord {

	private static final long serialVersionUID = 7171924074043506204L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private	VCFColumnName columnCategory;
	private String description; 	// field description
	private List<Object> elements;


	@Override
	public boolean acceptMoreElements() {
		if (elements.size() <= VCFHeaderType.ELEMENT_LIMIT) {
			return true;
		}
		return false;
	}


	@Override
	public void addElement(Object element) {
		if (elements == null) {
			elements = new ArrayList<Object>();
		}
		if (!elements.contains(element)) {
			elements.add(element);
		}
	}


	@Override
	public String getAsOriginalLine() {
		return "";
	}


	@Override
	public VCFColumnName getColumnCategory() {
		return columnCategory;
	}


	@Override
	public String getDescription() {
		return description;
	}


	/**
	 * @return the values found for this header ID
	 */
	@Override
	public List<Object> getElements () {
		return elements;
	}


	@Override
	public String getId() {
		return null;
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		columnCategory = (VCFColumnName) in.readObject();
		description = (String) in.readObject();
		elements = (List<Object>) in.readObject();
	}


	@Override
	public void setColumnCategory(VCFColumnName columnCategory) {
		this.columnCategory = columnCategory;
	}


	@Override
	public void setDescription(String description) {
		this.description = description;
	}


	@Override
	public void setId(String id) {}


	@Override
	public String toString () {
		return getColumnCategory() + ": " + description;
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(columnCategory);
		out.writeObject(description);
		out.writeObject(elements);
	}
}
