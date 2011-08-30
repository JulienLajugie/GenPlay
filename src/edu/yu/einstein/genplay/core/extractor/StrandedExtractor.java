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
package edu.yu.einstein.genplay.core.extractor;

import edu.yu.einstein.genplay.core.enums.Strand;


/**
 * Interface that should be implemented by all the extractor that have an information on the strand
 * @author Julien Lajugie
 * @version 0.1
 */
public interface StrandedExtractor {

	/**
	 * @param aStrand a {@link Strand}
	 * @return true if the specified strand is selected
	 */
	public boolean isStrandSelected(Strand aStrand);
	
	
	/**
	 * @param strandToSelect select the specified strand. Set the parameter to null to select both strands
	 */
	public void selectStrand(Strand strandToSelect);
	
	
	/**
	 * @return the {@link ReadLengthAndShiftHandler} that will compute the position 
	 * of read by applying the shift and the read length values
	 */
	public ReadLengthAndShiftHandler getReadLengthAndShiftHandler();
	
	
	/**
	 * Sets the handler that will compute the position of read by applying the shift
	 * and the read length values
	 * @param handler ReadLengthAndShiftHandler
	 */
	public void setReadLengthAndShiftHandler(ReadLengthAndShiftHandler handler);
}
