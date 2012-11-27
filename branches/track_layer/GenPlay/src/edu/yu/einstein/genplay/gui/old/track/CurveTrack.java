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
package edu.yu.einstein.genplay.gui.old.track;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.enums.GraphType;
import edu.yu.einstein.genplay.util.History;


/**
 * An abstract class providing common tools for the different kind of curve {@link Track}
 * @param <T> type of the data shown in the track
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class CurveTrack<T extends Serializable> extends ScoredTrack<T> implements VersionedTrack {
	
	private static final long serialVersionUID = 5068563286341191108L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	
	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
	}
	
	
	/**
	 * Constructor
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param trackNumber number of the track
	 * @param data data displayed in the track
	 */
	protected CurveTrack(int trackNumber, T data) {
		super(trackNumber, data);
	}
	

	/**
	 * @return the history of the current track.
	 */
	public History getHistory() {
		return ((CurveTrackGraphics<?>) trackGraphics).getHistory();
	}


	/**
	 * @return the color of the track
	 */
	public final Color getTrackColor() {
		return ((CurveTrackGraphics<?>)trackGraphics).getTrackColor();
	}


	/**
	 * @return the type of the graph
	 */
	public final GraphType getTypeOfGraph() {
		return ((CurveTrackGraphics<?>)trackGraphics).getTypeOfGraph();
	}
	
	
	/**
	 * @return true if the action redo is possible.
	 */
	public boolean isRedoable() {
		return ((CurveTrackGraphics<?>) trackGraphics).isRedoable();
	}

	
	/**
	 * @return true if the track can be reseted
	 */
	public boolean isResetable() {
		return ((CurveTrackGraphics<?>) trackGraphics).isResetable();
	}

	
	/**
	 * @return true if the action undo is possible.
	 */
	public boolean isUndoable() {
		return ((CurveTrackGraphics<?>) trackGraphics).isUndoable();
	}

	
	/**
	 * Redoes last action
	 */
	public void redoData() {
		((CurveTrackGraphics<?>) trackGraphics).redoData();
	}
	
	/**
	 * Resets the Data. Restore the original data
	 */
	public void resetData() {
		((CurveTrackGraphics<?>) trackGraphics).resetData();
	}

	
	/**
	 * Sets the data showed in the track
	 * @param data
	 * @param description description of the data
	 */
	public void setData(T data, String description) {
		((CurveTrackGraphics<T>) trackGraphics).setData(data, description);
	}

	
	/**
	 * Renames the track
	 * @param newName a new name for the track
	 */
	@Override
	public void setName(String newName) {
		// add the name of the track to the history
		getHistory().add("Track Name: \"" + newName + "\"",	new Color(0, 100, 0));
		super.setName(newName);
	}

	
	/**
	 * @param trackColor the color of the track to set
	 */
	public final void setTrackColor(Color trackColor) {
		((CurveTrackGraphics<?>)trackGraphics).setTrackColor(trackColor);
	}

	
	/**
	 * @param typeOfGraph the type of the graph to set
	 */
	public final void setTypeOfGraph(GraphType typeOfGraph) {
		((CurveTrackGraphics<?>)trackGraphics).setTypeOfGraph(typeOfGraph);
	}

	
	/**
	 * Disable the reset function
	 */
	public void deactivateReset () {
		((CurveTrackGraphics<?>) trackGraphics).deactivateReset();
	}
	
	
	/**
	 * Changes the undo count of the track
	 * @param undoCount
	 */
	public void setUndoCount(int undoCount) {
		((CurveTrackGraphics<?>) trackGraphics).setUndoCount(undoCount);
	}
	

	/**
	 * Undoes last action
	 */
	public void undoData() {
		((CurveTrackGraphics<?>) trackGraphics).undoData();
	}
}
