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
package edu.yu.einstein.genplay.gui.event.genomeWindowEvent;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.ChromosomeWindow;
import edu.yu.einstein.genplay.core.GenomeWindow;


/**
 * The {@link GenomeWindow} event emitted by a {@link GenomeWindowEventsGenerator} object.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GenomeWindowEvent {
	
	private final GenomeWindowEventsGenerator 	source;		// GenomeWindowEventsGenerator that emitted the event
	private final GenomeWindow 			oldWindow;	// old GenomeWindow
	private final GenomeWindow 			newWindow;	// new GenomeWindow
	
	
	/**
	 * Creates an instance of {@link GenomeWindowEvent}
	 * @param source {@link GenomeWindowEventsGenerator} that emitted this event
	 * @param oldWindow value of the {@link GenomeWindow} before changes
	 * @param newWindow value of the {@link GenomeWindow} after changes
	 */
	public GenomeWindowEvent(GenomeWindowEventsGenerator source, GenomeWindow oldWindow, GenomeWindow newWindow) {
		this.source = source;
		this.oldWindow = oldWindow;
		this.newWindow = newWindow;
	}


	/**
	 * @return the source
	 */
	public final GenomeWindowEventsGenerator getSource() {
		return source;
	}


	/**
	 * @return the oldWindow
	 */
	public final GenomeWindow getOldWindow() {
		return oldWindow;
	}


	/**
	 * @return the newWindow
	 */
	public final GenomeWindow getNewWindow() {
		return newWindow;
	}
	
	
	/**
	 * @return true if the size of the {@link GenomeWindow} changed
	 */
	public boolean didZoomChanged() {
		return oldWindow.getSize() != newWindow.getSize();
	}
	
	
	/**
	 * @return true if the {@link Chromosome} of the {@link GenomeWindow} changed
	 */
	public boolean didChromosomeChanged() {
		return !oldWindow.getChromosome().equals(newWindow.getChromosome());
	}
	
	
	/**
	 * @return true if the {@link ChromosomeWindow} of the {@link GenomeWindow} changed
	 */
	public boolean didChromosomeWindowChanged() {
		return !((ChromosomeWindow)oldWindow).equals((ChromosomeWindow)newWindow);
	}
	
}
