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
package yu.einstein.gdp2.core.extractor;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.enums.Strand;


/**
 * Interface that should be implemented by all the extractor that have an information on the strand
 * @author Julien Lajugie
 * @version 0.1
 */
public interface StrandedExtractor {

	/**
	 * @return true if the specified strand is selected
	 */
	public boolean isStrandSelected(Strand aStrand);
	
	
	/**
	 * @param strandToSelect select the specified strand. Set the parameter to null to select both strands
	 */
	public void selectStrand(Strand strandToSelect);
	
	
	/**
	 * @param shiftValue shift value to set
	 */
	public void setStrandShift(int shiftValue);
	

	/**
	 * Returns the shifted position on a specified chromosome and a specified strand
	 * @param strand current {@link Strand}
	 * @param chromosome current {@link Chromosome}
	 * @param position a position
	 */
	public int getShiftedPosition(Strand strand, Chromosome chromosome, int position);
}
