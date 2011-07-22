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


import edu.yu.einstein.genplay.core.enums.VariantType;


/**
 * This interface must be used for every kind of variant.
 * Variants can be different but must implement the followings methods in order to be processed.
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface MGPosition {

	
	// Attributes
	
	public String getFullGenomeName();
	
	public void setFullGenomeName(String name);
	
	public String getRawGenomeName();
	
	public String getUsualGenomeName();

	public String getChromosomeName ();

	public VariantType getType ();

	public int getLength ();
	
	public boolean isPhased ();

	public boolean isOnFirstAllele ();

	public boolean isOnSecondAllele ();

	

	// Positions

	public int getGenomePosition ();
	
	public int getNextGenomePosition ();

	public int getReferenceGenomePosition ();

	public int getNextReferenceGenomePosition ();
	
	public int getNextReferenceGenomePosition (int position);

	public int getMetaGenomePosition ();

	public int getNextMetaGenomePosition ();
	
	public void setGenomePosition (int position);
	
	public int getNextMetaGenomePosition (int position);
	
	

	// Offsets

	public int getExtraOffset ();

	public int getInitialReferenceOffset ();

	public int getNextReferencePositionOffset ();

	public int getInitialMetaGenomeOffset ();

	public int getNextMetaGenomePositionOffset ();

	public void addExtraOffset (int offset);

	public void setInitialReferenceOffset (int offset);

	public void setInitialMetaGenomeOffset (int offset);


	
	// Directly related to the VCF
	
	//public void setPositionInformation (Chromosome chromosome, Map<String, Object> line);
	
	public MGPositionInformation getPositionInformation ();

	public String getId ();

	public String getReference ();

	public String getAlternative ();

	public Double getQuality ();

	public boolean getFilter ();

	public String getInfo ();
	
	public Object getInfoValue (String field);

	public String getFormat ();
	
	public String getFormatValues ();

	public Object getFormatValue (String field);


}