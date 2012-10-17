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
package edu.yu.einstein.genplay.gui.event.trackEvent;

import java.util.EventObject;

import edu.yu.einstein.genplay.core.enums.TrackEventEnum;


/**
 * The {@link TrackEvent} event emitted by a {@link TrackEventsGenerator} object.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class TrackEvent extends EventObject {

	private static final long serialVersionUID = -5909384700520572038L;	// generated ID
	private final TrackEventsGenerator 	source;		// TrackEventsGenerator that emitted the event
	private final TrackEventEnum 		oldEvent;	// old event
	private final TrackEventEnum 		newEvent;	// new event


	/**
	 * Creates an instance of {@link TrackEvent}
	 * @param source {@link TrackEventsGenerator} that emitted this event
	 * @param oldEvent value of the {@link TrackEventEnum} before changes
	 * @param newEvent value of the {@link TrackEventEnum} after changes
	 */
	public TrackEvent(TrackEventsGenerator source, TrackEventEnum oldEvent, TrackEventEnum newEvent) {
		super(source);
		this.source = source;
		this.oldEvent = oldEvent;
		this.newEvent = newEvent;
	}


	/**
	 * @return the source
	 */
	@Override
	public final TrackEventsGenerator getSource() {
		return source;
	}


	/**
	 * @return the oldEvent
	 */
	public final TrackEventEnum getOldEvent() {
		return oldEvent;
	}


	/**
	 * @return the newEvent
	 */
	public final TrackEventEnum getNewEvent() {
		return newEvent;
	}


	/**
	 * @return true if the new and old events are different, false otherwise
	 */
	public boolean eventChanged() {
		if ((oldEvent == null) && (newEvent != null)) {
			return true;
		}
		return !oldEvent.equals(newEvent);
	}

}
