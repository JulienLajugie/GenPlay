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


/**
 * A type of indel for vcf
 * @author Nicolas Fourel
 * @author Julien Lajugie
 * @version 0.1
 */
public enum TrackEventType {


	/**
	 * Event generated when a track is selected
	 */
	SELECTED ("track selected"),
	
	/**
	 * Event generated when a track is unselected
	 */
	UNSELECTED ("track unselected"),
	
	/**
	 * Event generated when the track size is set to default size
	 */
	SIZE_SET_TO_DEFAULT ("track size set to default"),

	/**
	 * Event generated when a track is clicked with the right button of the mouse
	 */
	RIGHT_CLICKED ("track right clicked"),

	/**
	 * Event generated when a track is dragged
	 */
	DRAGGED ("track dragged"),

	/**
	 * Event generated when the track is released (drag ended)
	 */
	RELEASED ("track released"),

	/**
	 * Event generated when a track is resized
	 */
	RESIZED ("track resized"),

	/**
	 * Event generated when the track scroll mode has been turned on
	 */
	SCROLL_MODE_TURNED_ON ("track scroll mode turned on"),

	/**
	 * Event generated when the track scroll mode has been turned off
	 */
	SCROLL_MODE_TURNED_OFF ("track scroll mode turned of"),
	
	/**
	 * Event generated when the color of the track changed
	 */
	COLOR_CHANGED ("track color changed"),
	
	/**
	 * Event generated when the track graph type changed
	 */
	GRAPH_TYPE_CHANGED("track graph type changed"),
	
	/**
	 * Event generated when the track has been set for deletion
	 */
	SET_FOR_DELETION("track has been set for deletion");


	private final String eventDescription;


	/**
	 * Private constructor. Creates an instance of {@link TrackEventType}
	 * @param eventDescription description of the event
	 */
	private TrackEventType(String eventDescription) {
		this.eventDescription = eventDescription;
	}


	@Override
	public String toString() {
		return eventDescription;
	}
}
