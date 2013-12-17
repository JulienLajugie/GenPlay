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



/**
 * The {@link TrackEvent} event emitted by a {@link TrackEventsGenerator} object.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class TrackEvent extends EventObject {

	private static final long serialVersionUID = -5909384700520572038L;	// generated ID
	private final TrackEventsGenerator 	source;			// TrackEventsGenerator that emitted the event
	private final TrackEventType 		trackEventType;	// type of the event


	/**
	 * Creates an instance of {@link TrackEvent}
	 * @param source {@link TrackEventsGenerator} that emitted this event
	 * @param eventType type of the event that generated the {@link TrackEvent}
	 */
	public TrackEvent(TrackEventsGenerator source, TrackEventType eventType) {
		super(source);
		this.source = source;
		this.trackEventType = eventType;
	}


	/**
	 * @return the source
	 */
	@Override
	public final TrackEventsGenerator getSource() {
		return source;
	}


	/**
	 * @return the type of the event that generated the {@link TrackEvent}
	 */
	public final TrackEventType getEventType() {
		return trackEventType;
	}
}
