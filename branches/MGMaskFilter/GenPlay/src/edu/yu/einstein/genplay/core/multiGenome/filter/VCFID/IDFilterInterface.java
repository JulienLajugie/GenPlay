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
package edu.yu.einstein.genplay.core.multiGenome.filter.VCFID;

import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.filter.FilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.filter.utils.FormatFilterOperatorType;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface IDFilterInterface extends FilterInterface {


	/**
	 * @return the header type of the filter
	 */
	public VCFHeaderType getHeaderType ();


	/**
	 * Sets the header type of the filter
	 * @param header the ID
	 */
	public void setHeaderType (VCFHeaderType header);


	/**
	 * @return the {@link VCFColumnName} enum
	 */
	public VCFColumnName getColumnName ();


	/**
	 * Sets the list of genomes to apply the filter
	 * @param genomeNames
	 */
	public void setGenomeNames (List<String> genomeNames);


	/**
	 * @return the genome names
	 */
	public List<String> getGenomeNames ();


	/**
	 * @param operator the operator to use to filter the genomes
	 */
	public void setOperator (FormatFilterOperatorType operator);


	/**
	 * @return the operator to use to filter the genomes
	 */
	public FormatFilterOperatorType getOperator ();


	/**
	 * Gives a string for display use of the filter
	 * @return a string
	 */
	@Override
	public String toStringForDisplay ();


	/**
	 * Checks for all errors
	 * @return the string of errors if exists or null otherwise
	 */
	@Override
	public String getErrors ();


	/**
	 * Checks if the object (purpose of the filter) is valid according to the filter
	 * @param value the line of a VCF with columns and values
	 * @return true if it is valid, false otherwise;
	 */
	@Override
	public boolean isValid (Map<String, Object> value);


	/**
	 * Equals method
	 * @param obj
	 * @return true if the parameter is equal to the instance
	 */
	@Override
	public boolean equals(Object obj);


	/**
	 * @return a duplicate of the current object
	 */
	@Override
	public IDFilterInterface getDuplicate ();
}
