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
package yu.einstein.gdp2.core.list;

import yu.einstein.gdp2.core.GenomeWindow;

/**
 * Interface to implement to generate a list of data displayable on the screen
 * @author Julien Lajugie
 * @version 0.1
 * @param <T> Type of displayable data
 */
public interface DisplayableDataList<T> {

	/**
	 * @param genomeWindow {@link GenomeWindow} to display
	 * @param xRatio xRatio on the screen (ie ratio between the number of pixel and the number of base to display) 
	 * @return a data list adapted to the screen resolution
	 */
	public T getFittedData(GenomeWindow genomeWindow, double xRatio);
}
