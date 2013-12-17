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
package edu.yu.einstein.genplay.core.DAS;


/**
 * An annotation type as described in the DAS 1.53 specifications:
 * <br/><a href="http://www.biodas.org/documents/spec.html">http://www.biodas.org/documents/spec.html</a>
 * @author Julien Lajugie
 * @version 0.1
 */
public class DASType {
	private String ID;				// unique id for the annotation type 
	private String category;		// functional grouping to related types
	private String method;			// indicates the method (subtype) for the feature type 
	private String preferredFormat; // preferred format
	
	@Override
	public String toString() {
		return ID;
	}
	
	
	/**
	 * @return the iD
	 */
	public final String getID() {
		return ID;
	}
	
	
	/**
	 * @param iD the iD to set
	 */
	public final void setID(String iD) {
		ID = iD;
	}
	
	
	/**
	 * @return the category
	 */
	public final String getCategory() {
		return category;
	}
	
	
	/**
	 * @param category the category to set
	 */
	public final void setCategory(String category) {
		this.category = category;
	}
	
	
	/**
	 * @return the method
	 */
	public final String getMethod() {
		return method;
	}
	
	
	/**
	 * @param method the method to set
	 */
	public final void setMethod(String method) {
		this.method = method;
	}


	/**
	 * @return the preferredFormat
	 */
	public String getPreferredFormat() {
		return preferredFormat;
	}
	
	
	/**
	 * @param preferredFormat the preferredFormat to set
	 */
	public void setPreferredFormat(String preferredFormat) {
		this.preferredFormat = preferredFormat;
	}
}
