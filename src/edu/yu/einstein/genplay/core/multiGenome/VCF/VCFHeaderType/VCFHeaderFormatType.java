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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType;

/**
 * This class manages the FORMAT VCF field type information.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFHeaderFormatType implements VCFHeaderAdvancedType {
	
	private String id;				// information ID
	private String description; 	// field description
	private int number;				// the number of values that can be included
	private Class<?> type;			// type of the value. Can be Integer, Float, Character, and String (and Flag for INFO field)
	
	
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
	public int getNumber() {
		return number;
	}
	

	@Override
	public void setNumber(int number) {
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

	
}