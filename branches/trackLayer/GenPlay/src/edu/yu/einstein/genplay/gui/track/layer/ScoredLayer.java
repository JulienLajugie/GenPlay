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

import java.awt.Color;

import edu.yu.einstein.genplay.core.enums.GraphicsType;


/**
 * Interface implemented by the layers that shows graphics with scores 
 * (e.g. BinLayer, SCWLayer)
 * @author Julien Lajugie
 */
public interface ScoredLayer {

	
	/**
	 * @return the minimum displayed score
	 */
	abstract double getMinimumScore();
	
	
	/**
	 * Sets the minimum displayed score
	 * @param minimumScore minimum score to set
	 */
	abstract void setMinimumScore(double minimumScore);
	
	
	/**
	 * @return the maximum displayed score
	 */
	abstract double getMaximumScore();
	
	
	/**
	 * Sets the maximum displayed score
	 * @param maximumScore maximum score to set
	 */
	abstract void setMaximumScore(double maximumScore);
	
	
	/**
	 * @return the color of the layer
	 */
	public abstract Color getColor();
	
	
	/**
	 * @param layerColor color of the layer to set
	 */
	public abstract void setColor(Color layerColor);
	
	
	/**
	 * @return the type of graphics displayed
	 */
	public abstract GraphicsType getGraphicsType();
	
	
	/**
	 * @param graphicsType graphics type to set
	 */
	public abstract void setGraphicsType(GraphicsType graphicsType); 
}