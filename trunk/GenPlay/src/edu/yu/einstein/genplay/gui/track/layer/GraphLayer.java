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

import java.io.Serializable;

import edu.yu.einstein.genplay.dataStructure.enums.GraphType;


/**
 * Interface implemented by the layers that shows graph with scores
 * (e.g. BinLayer, SCWLayer)
 * @author Julien Lajugie
 */
public interface GraphLayer extends Cloneable, Serializable, ScoredLayer {

	/**
	 * @return A copy of this layer
	 */
	public GraphLayer clone();


	/**
	 * @return the type of graph displayed
	 */
	public abstract GraphType getGraphType();


	/**
	 * @param graphType graph type to set
	 */
	public abstract void setGraphType(GraphType graphType);
}
