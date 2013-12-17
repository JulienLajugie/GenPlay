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

import java.util.List;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface VCFHeaderElementRecord extends VCFHeaderType {
	
	
	/**
	 * Checks if the header ID accepts more elements
	 * @return true if it can have more elements, false otherwise.
	 */
	public boolean acceptMoreElements ();
	
	
	/**
	 * Add an element to the list of element of the header ID
	 * @param element the element
	 */
	public void addElement (Object element);
	
	
	/**
	 * @return the values found for this header ID
	 */
	public List<Object> getElements ();
	
	
}
