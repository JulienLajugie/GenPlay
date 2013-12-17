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

import java.io.Serializable;

import edu.yu.einstein.genplay.util.History;


/**
 * Inteface implemented by layers that are versioned (possessed an history and actions can be undone / redone / reseted)
 * @author Julien Lajugie
 * @param <T> type of the data displayed in the layer
 */
public interface VersionedLayer<T extends Serializable> {

	/**
	 * Disables the reset function of the layer
	 */
	public void deactivateReset();


	/**
	 * @return the history of the layer
	 */
	public abstract History getHistory();


	/**
	 * @return true if the redo action is available for the layer, false otherwise
	 */
	public boolean isRedoable();


	/**
	 * @return true if the reset action is available for the layer, false otherwise
	 */
	public boolean isResetable();


	/**
	 * @return true if the undo action is available for the layer, false otherwise
	 */
	public boolean isUndoable();


	/**
	 * Redo the last undone action on the layer
	 */
	public void redo();


	/**
	 * Reset the layer to its initial state
	 */
	public void reset();


	/**
	 * Sets the count of undo available
	 * @param undoCount the number of actions that can be undone
	 */
	public void setUndoCount(int undoCount);


	/**
	 * Undo the last action
	 */
	public void undo();
}
