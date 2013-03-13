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
package edu.yu.einstein.genplay.gui.dataScalerForTrackDisplay;

import java.io.Serializable;

import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;


/**
 * Interface inherited by classes that rescales data for the display
 * @param <T> type of the data to scale
 * @param <U> type of the data returned by the scaling method
 * @author Julien Lajugie
 */
public interface DataScalerForTrackDisplay<T, U> extends Serializable {

	/**
	 * @return the data rescaled to be displayed in the track for the {@link ProjectWindow}
	 */
	public U getDataScaledForTrackDisplay();


	/**
	 * @return the data to scale for track display
	 */
	public T getDataToScale();
}
