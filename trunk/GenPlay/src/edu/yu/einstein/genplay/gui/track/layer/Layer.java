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
package edu.yu.einstein.genplay.gui.track.layer;

import java.awt.Graphics;
import java.io.IOException;
import java.io.Serializable;

import edu.yu.einstein.genplay.gui.track.Drawer;
import edu.yu.einstein.genplay.gui.track.Track;


/**
 * Interface implemented by the different track layers available in GenPlay
 * @author Julien Lajugie
 * @param <T> type of data of the layer
 */
public interface Layer<T extends Serializable> extends Cloneable, Serializable, Drawer {


	/**
	 * @return A copy of this layer. The data copy is shallow.
	 */
	public abstract Layer<T> clone();


	/**
	 * @return a deep copy (not a reference copy) of the layer
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public abstract Layer<?> deepCopy() throws IOException, ClassNotFoundException;


	/**
	 * Paints the layer on the specified {@link Graphics} context
	 * @param g {@link Graphics} on which the layer will be drawn
	 * @param width width of the graphics to draw
	 * @param height height of the graphics to draw
	 */
	@Override
	public void draw(Graphics g, int width, int height);


	/**
	 * @return the data of the layer
	 */
	public abstract T getData();


	/**
	 * @return the name of the layer
	 */
	public abstract String getName();


	/**
	 * @return the track containing the layer
	 */
	public abstract Track getTrack();


	/**
	 * @return the type of the layer
	 */
	public abstract LayerType getType();


	/**
	 * @return true if the layer is visible, false if hidden
	 */
	public abstract boolean isVisible();


	/**
	 * Sets the data of the layer
	 * @param data data to set
	 */
	public abstract <U extends T> void setData(U data);


	/**
	 * Sets the name of the layer
	 * @param name name of the layer to set
	 */
	public abstract void setName(String name);


	/**
	 * Sets the {@link Track} containing the layer
	 * @param track {@link Track} track containing the layer
	 */
	public abstract void setTrack(Track track);


	/**
	 * Sets if the layer is visible or hidden
	 * @param isVisible set to true if the layer is visible, false if hidden
	 */
	public abstract void setVisible(boolean isVisible);
}
