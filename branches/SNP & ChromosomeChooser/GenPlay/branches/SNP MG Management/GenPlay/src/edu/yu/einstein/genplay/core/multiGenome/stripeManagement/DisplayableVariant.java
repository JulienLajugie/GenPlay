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
package edu.yu.einstein.genplay.core.multiGenome.stripeManagement;

import edu.yu.einstein.genplay.core.ChromosomeWindow;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.engine.Variant;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface DisplayableVariant {


	/**
	 * @return the variantPosition
	 */
	public Variant getNativeVariant();


	/**
	 * @return the type
	 */
	public VariantType getType();


	/**
	 * @return the start position
	 */
	public int getStart();


	/**
	 * @return the stop position
	 */
	public int getStop();


	/**
	 * @return the deadZone
	 */
	public ChromosomeWindow getDeadZone();


	/**
	 * @return true if a dead zone exist
	 */
	public boolean deadZoneExists ();


	/**
	 * @return the isOnFirstAllele
	 */
	public boolean isOnFirstAllele();


	/**
	 * @return the isOnSecondAllele
	 */
	public boolean isOnSecondAllele();


	/**
	 * @return the qualityScore
	 */
	public Double getQualityScore();

	
	/**
	 * 
	 * @param displayableVariant variant object to compare
	 * @return	-1 	if the current variant starts before the one to compare
	 * 			 0 	if they start at the same position
	 * 			 1 	if the current variant starts after the one to compare
	 */
	public int compareTo (DisplayableVariant displayableVariant);


	/**
	 * Shows variant information
	 */
	public void show ();
	
}
