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
 * Interface implemented by the layers that displays a score
 * (e.g. BinLayer, SCWLayer)
 * @author Julien Lajugie
 */
public interface ScoredLayer {

	/**
	 * @return the minimum displayed score
	 */
	abstract double getMinimumScoreToDisplay();


	/**
	 * @return the maximum displayed score
	 */
	abstract double getMaximumScoreToDisplay();


	/**
	 * @return the current displayed score (the score at the middle of the track)
	 */
	abstract Double getCurrentScoreToDisplay();
}
