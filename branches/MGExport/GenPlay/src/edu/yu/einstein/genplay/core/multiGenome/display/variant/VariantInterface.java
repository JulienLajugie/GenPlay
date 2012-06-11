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
package edu.yu.einstein.genplay.core.multiGenome.display.variant;

import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.display.MGVariantListForDisplay;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface VariantInterface {

	
	/**
	 * @return the associated variant list for display object
	 */
	public MGVariantListForDisplay getVariantListForDisplay ();
	
	
	/**
	 * @return the position of the variation on the reference genome
	 */
	public int getReferenceGenomePosition ();
	
	
	/**
	 * @return the length of the variation
	 */
	public int getLength ();
	
	
	/**
	 * @return the allele type of the variant
	 */
	public AlleleType getAlleleType ();
	
	
	/**
	 * @return the score of the variation
	 */
	public float getScore ();
	
	
	/**
	 * @return the position on the reference genome of the variation where the current variation is phased with
	 */
	public int phasedWithPos ();
	
	
	/**
	 * @return the type of the variation
	 */
	public VariantType getType ();
	
	
	/**
	 * @return the start position of the variation on the meta genome
	 */
	public int getStart ();
	
	
	/**
	 * @return the start position of the variation on the meta genome
	 */
	public int getStop ();
	
	
	/**
	 * @return information of the variant (from the vcf)
	 */
	public MGPosition getVariantInformation ();
	
	
	/**
	 * @return all information of the variant (from the vcf) (includes all other genomes format information)
	 */
	public MGPosition getFullVariantInformation ();
	
	
	/**
	 * Show the information of the variant
	 */
	public void show ();
	
	
	/**
	 * Equals method
	 * @param obj
	 * @return true if the parameter is equal to the instance
	 */
	public boolean equals(Object obj);
}
