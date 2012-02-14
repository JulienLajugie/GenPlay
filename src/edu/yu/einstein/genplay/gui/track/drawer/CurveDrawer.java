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
package edu.yu.einstein.genplay.gui.track.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import edu.yu.einstein.genplay.core.enums.GraphicsType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.gui.track.CurveTrack;



/**
 * Abstract class. Draws the data of a {@link CurveTrack}. 
 * Must be extended by the drawers of the different kind of {@link CurveTrack} subclasses
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class CurveDrawer {

	protected final Graphics 		graphics;		// Graphics of a track
	protected final int 			trackWidth;		// width of a track
	protected final int 			trackHeight;	// height of a track
	protected final double 			scoreMin;		// score minimum to display
	protected final double 			scoreMax;		// score maximum to display
	protected final Color 			trackColor;		// color of the curve
	protected final GraphicsType 	typeOfGraph;	// type of graph
	protected final ProjectWindow 	projectWindow;	// instance of the genome window manager
	protected final double 			yRatio;			// ratio between the height of a track and the distance from yMin to yMax
	
	
	/**
	 * Draws a bar graphics.
	 */
	abstract protected void drawBarGraphics();


	/**
	 * Draws a point graphics.
	 */
	abstract protected void drawCurveGraphics();


	/**
	 * Draws a curve graphics.
	 */
	abstract protected void drawPointGraphics();


	/**
	 * Draws a dense graphics.
	 */
	abstract protected void drawDenseGraphics();
	
	
	/**
	 * Creates an instance of {@link CurveDrawer}
	 * @param graphics {@link Graphics} of a track
	 * @param trackWidth width of a track 
	 * @param trackHeight height of a track
	 * @param scoreMin score minimum to display
	 * @param scoreMax score maximum to display
	 * @param trackColor color of the curve
	 * @param typeOfGraph type of graph
	 */
	public CurveDrawer (Graphics graphics, int trackWidth, int trackHeight, double scoreMin, double scoreMax, Color trackColor, GraphicsType typeOfGraph) {
		this.graphics = graphics;
		this.trackWidth = trackWidth;
		this.trackHeight = trackHeight;
		this.scoreMin = scoreMin;
		this.scoreMax = scoreMax;
		this.trackColor = trackColor;
		this.typeOfGraph = typeOfGraph;
		this.projectWindow = ProjectManager.getInstance().getProjectWindow();
		this.yRatio = (double)trackHeight / (double)(scoreMax - scoreMin);
	}
	

	/**
	 * @param score a double value
	 * @return the value on the screen
	 */
	protected int scoreToScreenPos(double score) {
		if (score < scoreMin) {
			return trackHeight;
		} else if (score > scoreMax) {
			return 0;
		} else {
			return (trackHeight - (int)Math.round((double)(score - scoreMin) * yRatio));
		}
	}
	
	
	/**
	 * Draws the data. 
	 */
	public void draw() {
		Graphics2D g2D = (Graphics2D)graphics;	
		switch(typeOfGraph) {
		case BAR:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			drawBarGraphics();
			break;
		case CURVE:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			drawCurveGraphics();
			break;
		case POINTS:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			drawPointGraphics();
			break;
		case DENSE:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			drawDenseGraphics();
			break;
		}
	}	
}
