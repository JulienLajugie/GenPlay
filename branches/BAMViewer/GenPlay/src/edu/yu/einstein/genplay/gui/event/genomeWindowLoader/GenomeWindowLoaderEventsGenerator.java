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
package edu.yu.einstein.genplay.gui.event.genomeWindowLoader;


/**
 * Should be Implemented by objects generating {@link GenomeWindowLoaderEvent}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface GenomeWindowLoaderEventsGenerator {

	/**
	 * Adds a {@link GenomeWindowLoaderListener} to the listener list
	 * @param genomeWindowLoaderListener {@link GenomeWindowLoaderListener} to add
	 */
	public void addGenomeWindowLoaderListener(GenomeWindowLoaderListener genomeWindowLoaderListener);


	/**
	 * @return an array containing all the {@link GenomeWindowLoaderListener} of the current instance
	 */
	public GenomeWindowLoaderListener[] getGenomeWindowLoaderListeners();


	/**
	 * Removes a {@link GenomeWindowLoaderListener} from the listener list
	 * @param genomeWindowLoaderListener {@link GenomeWindowLoaderListener} to remove
	 */
	public void removeGenomeWindowLoaderListener(GenomeWindowLoaderListener genomeWindowLoaderListener);
}
