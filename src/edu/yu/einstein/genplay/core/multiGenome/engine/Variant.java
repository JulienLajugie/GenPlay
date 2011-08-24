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
package edu.yu.einstein.genplay.core.multiGenome.engine;


import java.io.Serializable;

import edu.yu.einstein.genplay.core.enums.VariantType;


/**
 * This interface must be used for every kind of variant.
 * Variants can be different but must implement the followings methods in order to be processed.
 *
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface Variant extends Serializable {

	
	// Attributes
	
	/**
	 * @return the full genome name
	 */
	public String getFullGenomeName();
	
	
	/**
	 * @param name the full genome name
	 */
	public void setFullGenomeName(String name);
	
	
	/**
	 * @return the raw genome name
	 */
	public String getRawGenomeName();
	
	
	/**
	 * @return the usual genome name
	 */
	public String getUsualGenomeName();
	

	/**
	 * @return the chromosome name
	 */
	public String getChromosomeName ();
	

	/**
	 * @return the variant type
	 */
	public VariantType getType ();
	

	/**
	 * @return the lenght of the variation
	 */
	public int getLength ();
	
	
	/**
	 * @return true if the variant is phased with the previous one
	 */
	public boolean isPhased ();
	

	/**
	 * @return true if the variation is present on the first allele
	 */
	public boolean isOnFirstAllele ();
	

	/**
	 * @return true if the variation is present on the second allele
	 */
	public boolean isOnSecondAllele ();

	

	// Positions

	/**
	 * @return the genome position
	 */
	public int getGenomePosition ();
	
	
	/**
	 * @return the next genome position
	 */
	public int getNextGenomePosition ();
	

	/**
	 * @return the reference genome position
	 */
	public int getReferenceGenomePosition ();
	

	/**
	 * @return the next reference genome position
	 */
	public int getNextReferenceGenomePosition ();
	
	
	/**
	 * @param position 	the offset position
	 * @return			the next reference genome position according to the offset position
	 */
	public int getNextReferenceGenomePosition (int position);
	

	/**
	 * @return the meta genome position
	 */
	public int getMetaGenomePosition ();
	

	/**
	 * @return the next meta genome position
	 */
	public int getNextMetaGenomePosition ();
	
	
	/**
	 * @param position the genome position
	 */
	public void setGenomePosition (int position);
	
	
	/**
	 * @param position 	the offset position
	 * @return			the next meta genome position according to the offset position
	 */
	public int getNextMetaGenomePosition (int position);
		
	

	// Offsets
	
	/**
	 * @return the extra offset value
	 */
	public int getExtraOffset ();
	

	/**
	 * @return the initial reference genome offset value
	 */
	public int getInitialReferenceOffset ();
	

	/**
	 * @return the next reference genome offset value
	 */
	public int getNextReferencePositionOffset ();
	

	/**
	 * @return the initial meta genome offset value
	 */
	public int getInitialMetaGenomeOffset ();
	

	/**
	 * @return the next meta genome offset value
	 */
	public int getNextMetaGenomePositionOffset ();
	

	/**
	 * @param offset value to add to the current extra offset
	 */
	public void addExtraOffset (int offset);
	

	/**
	 * @param offset initial reference genome offset value
	 */
	public void setInitialReferenceOffset (int offset);
	

	/**
	 * @param offset initial meta genome offset value
	 */
	public void setInitialMetaGenomeOffset (int offset);


	
	// Directly related to the VCF
	
	/**
	 * @return the position information object
	 */
	public MGPosition getPositionInformation ();
	

	/**
	 * @return the ID field
	 */
	public String getId ();
	

	/**
	 * @return the REF field
	 */
	public String getReference ();
	

	/**
	 * @return the ALT field
	 */
	public String getAlternative ();

	
	/**
	 * @return the QUAL field
	 */
	public Double getQuality ();
	

	/**
	 * @return the FILTER field
	 */
	public boolean getFilter ();
	

	/**
	 * @return the INFO field
	 */
	public String getInfo ();
	
	
	/**
	 * @param field an ID from the INFO field
	 * @return the value associated to the ID
	 */
	public Object getInfoValue (String field);
	

	/**
	 * @return the FORMAT field
	 */
	public String getFormat ();
	
	
	/**
	 * @return the full FORMAT values information
	 */
	public String getFormatValues ();
	

	/**
	 * @param field an ID from the FORMAT field
	 * @return the value associated to the ID
	 */
	public Object getFormatValue (String field);
	

	/**
	 * @return a description of the Variant
	 */
	public String toString ();
}