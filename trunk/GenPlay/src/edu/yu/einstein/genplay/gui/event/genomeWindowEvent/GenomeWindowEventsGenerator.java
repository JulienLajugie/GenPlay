/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.event.genomeWindowEvent;


/**
 * Should be Implemented by objects generating {@link GenomeWindowEvent}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface GenomeWindowEventsGenerator {

	/**
	 * Adds a {@link GenomeWindowListener} to the listener list
	 * @param genomeWindowListener {@link GenomeWindowListener} to add
	 */
	public void addGenomeWindowListener(GenomeWindowListener genomeWindowListener);


	/**
	 * @return an array containing all the {@link GenomeWindowListener} of the current instance
	 */
	public GenomeWindowListener[] getGenomeWindowListeners();


	/**
	 * Removes a {@link GenomeWindowListener} from the listener list
	 * @param genomeWindowListener {@link GenomeWindowListener} to remove
	 */
	public void removeGenomeWindowListener(GenomeWindowListener genomeWindowListener);
}
