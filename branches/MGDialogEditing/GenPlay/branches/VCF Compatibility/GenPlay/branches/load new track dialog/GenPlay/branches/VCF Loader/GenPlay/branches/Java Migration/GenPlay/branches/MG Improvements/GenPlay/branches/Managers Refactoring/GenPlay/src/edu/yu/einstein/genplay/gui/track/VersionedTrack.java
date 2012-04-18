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
package edu.yu.einstein.genplay.gui.track;

import edu.yu.einstein.genplay.util.History;


/**
 * Interface implemented by tracks that can be undone / redone
 * reseted and that contains an history of operations
 * @author Julien Lajugie
 * @version 0.1
 */
public interface VersionedTrack {
	
	
	/**
	 * @return the history of the current track.
	 */
	public History getHistory();


	/**
	 * @return true if the action redo is possible
	 */
	public boolean isRedoable();


	/**
	 * @return true if the track can be reseted
	 */
	public boolean isResetable();


	/**
	 * @return true if the action undo is possible
	 */
	public boolean isUndoable();

	
	/**
	 * Redoes last action
	 */
	public void redoData();
	

	/**
	 * Resets the data 
	 * Copies the value of the original data into the current value
	 */
	public void resetData();


	
	/**
	 * Changes the undo count of the track
	 * @param undoCount
	 */
	public void setUndoCount(int undoCount);
	
	
	/**
	 * Undoes last action
	 */
	public void undoData();
}
