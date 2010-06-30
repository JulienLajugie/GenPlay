/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.io.Serializable;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.enums.GraphicsType;
import yu.einstein.gdp2.util.History;

/**
 * An abstract class providing common tools for the different kind of curve {@link Track}
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class CurveTrack<T extends Serializable> extends ScoredTrack<T> {
	
	private static final long serialVersionUID = 5068563286341191108L;	// generated ID

	
	/**
	 * Constructor
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param trackNumber number of the track
	 * @param data data displayed in the track
	 */
	protected CurveTrack(GenomeWindow displayedGenomeWindow, int trackNumber, T data) {
		super(displayedGenomeWindow, trackNumber, data);
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
	public final GraphicsType getTypeOfGraph() {
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
	public void redo() {
		((CurveTrackGraphics<?>) trackGraphics).redoData();
	}
	
	/**
	 * Resets the Data. Restore the original data
	 */
	public void resetBinList() {
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
	public final void setTypeOfGraph(GraphicsType typeOfGraph) {
		((CurveTrackGraphics<?>)trackGraphics).setTypeOfGraph(typeOfGraph);
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
	public void undo() {
		((CurveTrackGraphics<?>) trackGraphics).undoData();
	}
}
