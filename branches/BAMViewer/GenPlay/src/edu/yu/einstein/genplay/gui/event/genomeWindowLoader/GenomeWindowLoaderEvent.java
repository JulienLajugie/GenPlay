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

import java.util.EventObject;

import edu.yu.einstein.genplay.core.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEvent;


/**
 * The {@link GenomeWindow} event emitted by a {@link GenomeWindowLoaderEventsGenerator} object.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GenomeWindowLoaderEvent extends EventObject {

	private static final long serialVersionUID = -5909384700520572038L;	// generated ID

	/** If the window is moving forward (to the right, higher coordinates) */
	public static final int FORWARD = 1;
	/** If the window is moving backward (to the left, lower coordinates) */
	public static final int BACKWARD = -1;
	/** If the window is not moving backward (just in case it happens) */
	public static final int NOT_MOVING = 0;

	private final GenomeWindowLoaderEventsGenerator 	source;							// GenomeWindowEventsGenerator that emitted the event.
	private final GenomeWindowEvent 					genomeWindowEvent;				// The wrapped GenomeWindowEvent.
	private final GenomeWindowLoaderSettings			settings;						// The settings for extended windows.
	private final GenomeWindow							oldExtendedGenomeWindow;		// The former extended window.
	private final GenomeWindow							newExtendedGenomeWindow;		// The new extended window.
	private GenomeWindow								genomeWindowToKeep;				// The genome window to keep (null if nothing to keep).
	private GenomeWindow								genomeWindowToLoad;				// The genome window to load.
	private int 										direction;						// The moving direction.


	/**
	 * Creates an instance of {@link GenomeWindowLoaderEvent}
	 * @param source {@link GenomeWindowLoaderEventsGenerator} that emitted this event
	 * @param genomeWindowEvent the {@link GenomeWindowEvent} to wrap
	 * @param settings the {@link GenomeWindowLoaderSettings} to use
	 */
	public GenomeWindowLoaderEvent(GenomeWindowLoaderEventsGenerator source, GenomeWindowEvent genomeWindowEvent, GenomeWindowLoaderSettings settings) {
		super(source);
		this.source = source;
		this.genomeWindowEvent = genomeWindowEvent;
		this.settings = settings;
		oldExtendedGenomeWindow = getExtendedGenomeWindow(genomeWindowEvent.getOldWindow());
		newExtendedGenomeWindow = getExtendedGenomeWindow(genomeWindowEvent.getNewWindow());
		setDirection();
		initializeGenomeWindows();
	}


	/**
	 * @return the source
	 */
	@Override
	public final GenomeWindowLoaderEventsGenerator getSource() {
		return source;
	}


	/**
	 * @return the genomeWindowEvent
	 */
	public final GenomeWindowEvent getGenomeWindowEvent() {
		return genomeWindowEvent;
	}


	/**
	 * @return the settings
	 */
	public GenomeWindowLoaderSettings getSettings() {
		return settings;
	}


	/**
	 * Create the extended {@link GenomeWindow} from a {@link GenomeWindow}
	 * @param genomeWindow a {@link GenomeWindow}
	 * @return the extended {@link GenomeWindow}
	 */
	private GenomeWindow getExtendedGenomeWindow (GenomeWindow genomeWindow) {
		int start = genomeWindow.getStart();
		int stop = genomeWindow.getStop();
		//start -= getOffset(start, settings.getLeftMargin(), settings.getLeftMinimum(), settings.getLeftMaximum());
		//stop += getOffset(stop, settings.getRightMargin(), settings.getRightMinimum(), settings.getRightMaximum());

		start -= getOffset(genomeWindow.getSize(), settings.getLeftMargin(), settings.getLeftMinimum(), settings.getLeftMaximum());
		stop += getOffset(genomeWindow.getSize(), settings.getRightMargin(), settings.getRightMinimum(), settings.getRightMaximum());

		GenomeWindow extendedGenomeWindow = new GenomeWindow();
		extendedGenomeWindow.setChromosome(genomeWindow.getChromosome());
		extendedGenomeWindow.setStart(start);
		extendedGenomeWindow.setStop(stop);

		return extendedGenomeWindow;
	}


	/**
	 * @param value can be a position or a length
	 * @param margin the margin in percentage
	 * @param minimum the minimum in bp
	 * @param maximum the maximum in bp
	 * @return the offset of the position according to all parameters
	 */
	private int getOffset (int value, double margin, int minimum, int maximum) {
		int offset = (int) (value * margin);

		if (minimum != GenomeWindowLoaderSettings.NO_LIMIT) {
			if (offset < minimum) {
				offset = minimum;
			}
		}

		if (maximum != GenomeWindowLoaderSettings.NO_LIMIT) {
			if (offset > maximum) {
				offset = maximum;
			}
		}

		return offset;
	}


	/**
	 * Set the moving direction of the window.
	 */
	private void setDirection () {
		if (genomeWindowEvent.getNewWindow().getStart() > genomeWindowEvent.getOldWindow().getStart()) {
			direction = FORWARD;
		} else if (genomeWindowEvent.getNewWindow().getStart() < genomeWindowEvent.getOldWindow().getStart()) {
			direction = BACKWARD;
		} else {
			direction = NOT_MOVING;
		}
	}


	/**
	 * Initialize the {@link GenomeWindow} to load and to keep
	 */
	private void initializeGenomeWindows () {
		if (genomeWindowEvent.chromosomeChanged()) {								// Switching chromosome: first load
			genomeWindowToLoad = newExtendedGenomeWindow;
			genomeWindowToKeep = null;
		} else {
			int startToLoad = -1;
			int stopToLoad = -1;
			int startToKeep = -1;
			int stopToKeep = -1;
			if (direction == FORWARD) {
				startToKeep = newExtendedGenomeWindow.getStart();					// The new start is generally contained in the former genome window (which is already loaded).
				stopToKeep = oldExtendedGenomeWindow.getStop();						// Loaded data goes until the former stop window.

				// If the user typed a new position and jump out of the former window, the new start is higher than the old stop.
				if (startToKeep > stopToKeep) {
					genomeWindowToLoad = newExtendedGenomeWindow;					// Need to replace the whole content with the new window information
					genomeWindowToKeep = null;
				} else {															// In most of the case, the user scrolls and part of the new window is overlapping with the old window
					startToLoad = stopToKeep + 1;									// We want to start loading data right after the last one already loaded.
					stopToLoad = newExtendedGenomeWindow.getStop();					// Until the new stop.
				}
			} else if (direction == BACKWARD) {
				startToKeep = oldExtendedGenomeWindow.getStart();					// The old start is generally contained in the new genome window.
				stopToKeep = newExtendedGenomeWindow.getStop();						// The new stop is generally contained in the former genome window (which is already loaded).

				// If the user typed a new position and jump out of the former window, the new stop is lower than the old start
				if (stopToKeep < startToKeep) {
					genomeWindowToLoad = newExtendedGenomeWindow;					// Need to replace the whole content with the new window information
					genomeWindowToKeep = null;
				} else {															// In most of the case, the user scrolls and part of the new window is overlapping with the old window
					startToLoad = newExtendedGenomeWindow.getStart();				// We want to start loading data where the new window starts
					stopToLoad = startToKeep - 1;									// Until the first loaded data.
				}
			}

			genomeWindowToLoad = new GenomeWindow(newExtendedGenomeWindow.getChromosome(), startToLoad, stopToLoad);
			genomeWindowToKeep = new GenomeWindow(newExtendedGenomeWindow.getChromosome(), startToKeep, stopToKeep);
		}
	}


	/**
	 * @return the direction
	 */
	public int getDirection() {
		return direction;
	}


	/**
	 * @return the newExtendedGenomeWindow
	 */
	public GenomeWindow getNewExtendedGenomeWindow() {
		return newExtendedGenomeWindow;
	}


	/**
	 * @return the genomeWindowToKeep
	 */
	public GenomeWindow getGenomeWindowToKeep() {
		return genomeWindowToKeep;
	}


	/**
	 * @return the genomeWindowToLoad
	 */
	public GenomeWindow getGenomeWindowToLoad() {
		return genomeWindowToLoad;
	}

}
