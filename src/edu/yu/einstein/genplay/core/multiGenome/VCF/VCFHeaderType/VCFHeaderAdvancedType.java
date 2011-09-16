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

/**
 * This class declares methods required for advanced VCF type field:
 * - INFO
 * - FORMAT
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface VCFHeaderAdvancedType extends VCFHeaderType {
	
	
	/**
	 * @return the number
	 */
	public String getNumber();

	
	/**
	 * @param number the number to set
	 */
	public void setNumber(String number);

	
	/**
	 * @return the type
	 */
	public Class<?> getType();

	
	/**
	 * @param type the type to set
	 */
	public void setType(Class<?> type);
	
	
}
