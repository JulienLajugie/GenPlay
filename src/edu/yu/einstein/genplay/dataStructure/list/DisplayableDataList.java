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
package edu.yu.einstein.genplay.dataStructure.list;

import edu.yu.einstein.genplay.dataStructure.genomeWindow.SimpleGenomeWindow;

/**
 * Interface to implement to generate a list of data adapted to the track resolution
 * @author Julien Lajugie
 * @param <T> Type of the data that will be returned to be displayed
 */
public interface DisplayableDataList<T> {

	/**
	 * @param genomeWindow {@link SimpleGenomeWindow} to display
	 * @param xRatio xRatio on the screen (ie ratio between the number of pixel and the number of base to display)
	 * @return a data list adapted to the track resolution
	 */
	public T getFittedData(SimpleGenomeWindow genomeWindow, double xRatio);
}
