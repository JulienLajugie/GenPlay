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
package edu.yu.einstein.genplay.gui.track.layer;


/**
 * Interface implemented by the different track layers available in GenPlay 
 * @author Julien Lajugie
 * @param <T> type of data of the layer 
 */
public class TrackLayer<T> {

	private T data;
	private LayeredTrack track;
	private TrackDrawer trackDrawer;
	private boolean isHiddent;

	
	/**
	 * draws the layer
	 */
	protected void draw() {
		trackDrawer.drawTrack();
	}


	/**
	 * @return the data of the layer
	 */
	public T getData() {
		return data;
	}


	/**
	 * Sets the data of the layer 
	 * @param data data to set
	 */
	public void setData(T data) {
		this.data = data;
	}


	/**
	 * Register a container track to the layer
	 * This method is used by a track to register itself to the layer.
	 * @param track {@link LayeredTrack} to set
	 */
	public void setTrack(LayeredTrack track) {
		this.track = track;
	}


	/**
	 * @return true if the layer needs to be hidden
	 */
	public boolean isHidden() {
		return isHiddent;
	}


	/**
	 * Sets if the layer needs to be hidden
	 * @param isHidden set to true if the layer needs to be hidden
	 */
	public void setHidden(boolean isHidden) {
		this.isHiddent = isHidden;
	}


	/**
	 * @return a deep copy (not a reference copy) of the layer
	 */
	public TrackLayer<T> deepCopy() {
		// TODO: create method
		return null;
	}
}
