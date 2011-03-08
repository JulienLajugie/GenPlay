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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.track;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.io.Serializable;
import java.text.DecimalFormat;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.enums.GraphicsType;
import edu.yu.einstein.genplay.core.manager.ConfigurationManager;
import edu.yu.einstein.genplay.core.manager.ExceptionManager;
import edu.yu.einstein.genplay.core.manager.URRManager;
import edu.yu.einstein.genplay.gui.track.drawer.CurveDrawer;
import edu.yu.einstein.genplay.util.History;



/**
 * An abstract class providing tools to draw a curve {@link TrackGraphics}
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class CurveTrackGraphics<T extends Serializable> extends ScoredTrackGraphics<T> implements MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long 				serialVersionUID = -9200672145021160494L;	// generated ID
	private static final Color				TRACK_COLOR = Color.black;					// default color
	private static final GraphicsType 		TYPE_OF_GRAPH = GraphicsType.BAR;			// type of graph
	protected static final DecimalFormat 	SCORE_FORMAT = new DecimalFormat("#.###");	// decimal format for the score
	protected Color							trackColor;									// color of the graphics
	protected GraphicsType 					typeOfGraph;								// type graphics
	protected History 						history = null; 							// history containing a description of the
	protected URRManager<T> 				urrManager; 								// manager that handles the undo / redo / reset of the track
		
	
	/**
	 * Creates an instance of {@link CurveTrackGraphics}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param data data displayed in the track
	 * @param yMin minimum score
	 * @param yMax maximum score
	 */
	protected CurveTrackGraphics(GenomeWindow displayedGenomeWindow, T data, double yMin, double yMax) {
		super(displayedGenomeWindow, data, yMin, yMax);
		this.trackColor = TRACK_COLOR;
		this.typeOfGraph = TYPE_OF_GRAPH;
		this.data = data;
		this.history = new History();
		urrManager = new URRManager<T>(ConfigurationManager.getInstance().getUndoCount(), data);
	}


	/**
	 * Draws the horizontal lines if the curve is not a dense graphics
	 */
	@Override
	protected void drawHorizontalLines(Graphics g) {
		if (typeOfGraph != GraphicsType.DENSE) {
			super.drawHorizontalLines(g);
		}
	}

	
	/**
	 * Returns the drawer used to print the data of this track and link it to the specified {@link Graphics}
	 * @param g {@link Graphics} of a track
	 * @param trackWidth width of a track 
	 * @param trackHeight height of a track
	 * @param genomeWindow {@link GenomeWindow} of a track
	 * @param scoreMin score minimum 
	 * @param scoreMax score maximum
	 * @return the drawer used to paint the data of this track
	 */
	public abstract CurveDrawer getDrawer(Graphics g, int trackWidth, int trackHeight, GenomeWindow genomeWindow, double scoreMin, double scoreMax);


	/**
	 * @return the history of the current track.
	 */
	protected History getHistory() {
		return history;
	}


	/**
	 * @return the maximum value to display
	 */
	protected abstract double getMaxScoreToDisplay();


	/**
	 * @return the minmum value to display
	 */
	protected abstract double getMinScoreToDisplay();
	
	
	/**
	 * @return the color of the track
	 */
	protected final Color getTrackColor() {
		return trackColor;
	}
	

	/**
	 * @return the type of the graph
	 */
	protected final GraphicsType getTypeOfGraph() {
		return typeOfGraph;
	}
	
	
	/**
	 * @return true if the action redo is possible
	 */
	protected boolean isRedoable() {
		return urrManager.isRedoable();
	}
	

	/**
	 * @return true if the track can be reseted
	 */
	protected boolean isResetable() {
		return urrManager.isResetable();
	}

	
	/**
	 * @return true if the action undo is possible
	 */
	protected boolean isUndoable() {
		return urrManager.isUndoable();
	}

	
	/**
	 * Redoes last action
	 */
	protected void redoData() {
		try {
			if (isRedoable()) {
				data = urrManager.redo();
				yMin = getMinScoreToDisplay();
				yMax = getMaxScoreToDisplay();
				repaint();
				history.redo();
			}
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while redoing");
			history.setLastAsError();
		}
	}

	
	/**
	 * Resets the data 
	 * Copies the value of the original data into the current value
	 */
	protected void resetData() {
		try {
			if (isResetable()) {
				data = urrManager.reset();
				yMin = getMinScoreToDisplay();
				yMax = getMaxScoreToDisplay();
				repaint();
				history.reset();
			}
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while reseting");
			history.setLastAsError();
		}
	}

	
	/**
	 * Sets the data showed in the track
	 * @param data the data showed in the track
	 * @param description description of the data
	 */
	protected void setData(T data, String description) {
		if (data != null) {
			try {
				history.add(description);
				urrManager.set(data);
				this.data = data;
				yMin = getMinScoreToDisplay();
				yMax = getMaxScoreToDisplay();
				repaint();
			} catch (Exception e) {
				ExceptionManager.handleException(getRootPane(), e, "Error while updating the track");
				history.setLastAsError();
			}
		}
	}
	

	/**
	 * @param trackColor the color of the track to set
	 */
	protected final void setTrackColor(Color trackColor) {
		firePropertyChange("trackColor", this.trackColor, trackColor);
		this.trackColor = trackColor;
		this.repaint();
	}

	/**
	 * @param typeOfGraph the type of the graph to set
	 */
	protected final void setTypeOfGraph(GraphicsType typeOfGraph) {
		firePropertyChange("typeOfGraph", this.typeOfGraph, typeOfGraph);
		this.typeOfGraph = typeOfGraph;
		this.repaint();
	}

	
	/**
	 * Changes the undo count of the track
	 * @param undoCount
	 */
	protected void setUndoCount(int undoCount) {
		urrManager.setLength(undoCount);		
	}
	
	
	/**
	 * Undoes last action
	 */
	protected void undoData() {
		try {
			if (isUndoable()) {
				data = urrManager.undo();
				yMin = getMinScoreToDisplay();
				yMax = getMaxScoreToDisplay();
				repaint();
				history.undo();
			}
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while undoing");
			history.setLastAsError();
		}
	}
	
	
	@Override
	protected void yFactorChanged() {
		repaint();
	}
}
