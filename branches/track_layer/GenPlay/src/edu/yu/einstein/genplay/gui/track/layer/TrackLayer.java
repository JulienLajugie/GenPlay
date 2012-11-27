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

import java.awt.Graphics;
import java.io.IOException;
import java.io.Serializable;


/**
 * Interface implemented by the different track layers available in GenPlay 
 * @author Julien Lajugie
 * @param <T> type of data of the layer 
 */
public interface TrackLayer<T extends Serializable> extends Serializable {


	/**
	 * @return a deep copy (not a reference copy) of the layer
	 * @throws IOException
	 */
	public abstract TrackLayer<?> deepCopy() throws IOException;


	/**
	 * Paints the layer on the specified {@link Graphics} context
	 * @param g {@link Graphics} on which the track will be drawn
	 */
	abstract void drawLayer(Graphics g);


	/**
	 * @return the data of the layer
	 */
	public abstract T getData();


	/**
	 * @return the track containing the layer
	 */
	public abstract LayeredTrack getTrack();


	/**
	 * @return true if the layer needs to be hidden
	 */
	public abstract boolean isHidden();


	/**
	 * Sets the data of the layer 
	 * @param data data to set
	 */
	public abstract void setData(T data);


	/**
	 * Sets if the layer needs to be hidden
	 * @param isHidden set to true if the layer needs to be hidden
	 */
	public abstract void setHidden(boolean isHidden);


	/**
	 * Sets the {@link LayeredTrack} containing the layer
	 * @param track {@link LayeredTrack} track containing the layer
	 */
	public abstract void setTrack(LayeredTrack track);
}