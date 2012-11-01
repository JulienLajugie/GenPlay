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
public interface TrackLayer<T> {

	
	/**
	 * draws the data
	 */
	abstract void drawData();
	
	
	/**
	 * @return the type of layer 
	 */
	public abstract TrackLayerType getType();
	
	
	/**
	 * @return the data of the layer
	 */
	public abstract T getData();
	
	
	/**
	 * Sets the data of the layer 
	 * @param data data to set
	 */
	public abstract void setData(T data);
	
	
	/**
	 * Register a track a container track to the layer
	 * This method is used by a track to register itself to the layer.
	 */
	abstract void setTrack();
}
