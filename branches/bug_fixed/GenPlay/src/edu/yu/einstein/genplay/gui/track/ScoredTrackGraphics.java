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

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.RoundingMode;
import java.security.InvalidParameterException;
import java.text.DecimalFormat;

import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * An abstract class providing tools to draw a scored {@link TrackGraphics}
 * @param <T> type of the data shown in the track
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class ScoredTrackGraphics<T> extends TrackGraphics<T> {
	
	private static final long serialVersionUID = 985376787707775754L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 1;			// saved format version
	
	/**
	 * The score of the track is drawn on top of the track
	 */
	public static final int TOP_SCORE_POSITION = 0;
	/**
	 * The score of the track is drawn on the bottom of the track
	 */
	public static final int BOTTOM_SCORE_POSITION = 1;
	
	private static final boolean 	SHOW_HORIZONTAL_GRID = true;			// show the horizontal grid
	private static final int		HORIZONTAL_LINES_COUNT = 10;			// number of Y lines displayed
	protected double				yFactor;								// factor between the displayed intensity range and the screen height
	protected boolean				showHorizontalGrid;						// shows horizontal grid if true 
	protected int					horizontalLinesCount;					// number of horizontal lines
	protected double 				yMax;									// maximum score	
	protected double 				yMin;									// minimum score
	protected boolean				isYAutoscale = true;						// true if the Y scale needs to be automatically set
	protected Color					scoreColor = Colors.RED;				// color of the score
	protected int 					scorePosition = BOTTOM_SCORE_POSITION; 	// position of the score (top or bottom)

	
	/**
	 * Creates an instance of {@link ScoredTrackGraphics}
	 * @param data data displayed in the track
	 * @param yMin minimum score
	 * @param yMax maximum score
	 */
	protected ScoredTrackGraphics(T data, double yMin, double yMax) {
		super(data);
		this.yMin = yMin;
		this.yMax = yMax;
		this.showHorizontalGrid = SHOW_HORIZONTAL_GRID;
		this.horizontalLinesCount = HORIZONTAL_LINES_COUNT;
	}
	
	
	/**
	 * Draws the data of the track
	 * @param g {@link Graphics}
	 */
	protected abstract void drawData(Graphics g);
	
	
	/**
	 * Draws horizontal lines on the track
	 * @param g {@link Graphics}
	 */
	protected void drawHorizontalLines(Graphics g) {
		if (showHorizontalGrid) {
			g.setColor(Colors.LIGHT_GREY);
			double scoreGapBetweenLineY = (yMax - yMin) / (double)horizontalLinesCount;
			double intensityFirstLineY = yMin - (yMin % scoreGapBetweenLineY);
			for(int i = 0; i <= horizontalLinesCount; i++) {
				double intensityLineY = ((double) i) * scoreGapBetweenLineY + intensityFirstLineY;
				if (intensityLineY >= yMin) {
					int screenLineY = scoreToScreenPos(intensityLineY,getHeight());
					g.drawLine(0, screenLineY, getWidth(), screenLineY);
					DecimalFormat formatter = new DecimalFormat("#.#####");
					formatter.setRoundingMode(RoundingMode.DOWN);
					String positionStr = formatter.format(intensityLineY);
					g.drawString(positionStr, 2, screenLineY);
				}
			}
		}
	}


	/**
	 * Draws the y value of the middle of the track
	 * @param g
	 */
	abstract protected void drawScore(Graphics g);


	@Override
	protected void drawTrack(Graphics g) {
		drawHorizontalLines(g);
		drawVerticalLines(g);
		drawData(g);
		drawMask(g);
		drawMultiGenomeInformation(g);
		drawScore(g);
		drawHeaderTrack(g);
		drawMiddleVerticalLine(g);
	}


	/**
	 * @return the number of horizontal lines
	 */
	public final int getHorizontalLinesCount() {
		return horizontalLinesCount;
	}


	/**
	 * @return the color of the score
	 */
	public final Color getScoreColor() {
		return scoreColor;
	}


	/**
	 * @return the position of the score
	 */
	public final int getScorePosition() {
		return scorePosition;
	}


	/**
	 * @return the yMax
	 */
	public final double getYMax() {
		return yMax;
	}


	/**
	 * @return the yMin
	 */
	public final double getYMin() {
		return yMin;
	}
	

	/**
	 * @return true if the Y scale is set automatically
	 */
	public boolean isYAutoscale() {
		return isYAutoscale;
	}


	/**
	 * @return true if the horizontal grid is visible
	 */
	public final boolean isShowHorizontalGrid() {
		return showHorizontalGrid;
	}


	@Override
	protected void paintComponent(Graphics g) {
		double newYFactor = (double)getHeight() / (double)(yMax - yMin);
		if (newYFactor != yFactor) {
			yFactor = newYFactor;
			yFactorChanged();
		}
		super.paintComponent(g);
	}


	/**
	 * Unserializes the save format version number
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		int version = in.readInt();
		yFactor = in.readDouble();
		showHorizontalGrid = in.readBoolean();
		horizontalLinesCount = in.readInt();
		yMax = in.readDouble();
		yMin = in.readDouble();
		scoreColor = (Color) in.readObject();
		scorePosition = in.readInt();
		if (version >= 1) {
			this.isYAutoscale = in.readBoolean();
		} else {
			this.isYAutoscale = true;
		}
	}


	/**
	 * @param score a double value
	 * @return the value on the screen
	 */
	protected int scoreToScreenPos(double score, int height) {
		if (score < yMin) {
			return height;
		} else if (score > yMax) {
			return 0;
		} else {
			return (height - (int)Math.round((double)(score - yMin) * yFactor));
		}
	}


	/**
	 * Set to true to have the Y scale set automatically
	 * @param yAutoscale
	 */
	public void setYAutoscale(boolean yAutoscale) {
		this.isYAutoscale = yAutoscale;
	}


	/**
	 * @param horizontalLinesCount the the number of horizontal lines to show
	 */
	public final void setHorizontalLinesCount(int horizontalLinesCount) {
		this.horizontalLinesCount = horizontalLinesCount;
		this.repaint();
	}


	/**
	 * @param scoreColor the color of the score to set
	 */
	public final void setScoreColor(Color scoreColor) {
		this.scoreColor = scoreColor;
		this.repaint();
	}


	/**
	 * @param scorePosition the position of the score to set
	 */
	public final void setScorePosition(int scorePosition) {
		if ((scorePosition == TOP_SCORE_POSITION) || (scorePosition == BOTTOM_SCORE_POSITION)) {
			this.scorePosition = scorePosition;
			this.repaint();
		} else {
			throw new InvalidParameterException("Invalid score position");
		}
	}


	/**
	 * @param showHorizontalGrid set to true to show the horizontal grid
	 */
	public final void setShowHorizontalGrid(boolean showHorizontalGrid) {
		this.showHorizontalGrid = showHorizontalGrid;
		this.repaint();
	}


	/**
	 * @param yMax the yMax to set
	 */
	public final void setYMax(double yMax) {
		this.yMax = yMax;
		this.repaint();
	}


	/**
	 * @param yMin the yMin to set
	 */
	public final void setYMin(double yMin) {
		this.yMin = yMin;
		this.repaint();
	}


	/**
	 * Saves the format version number during serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeDouble(yFactor);
		out.writeBoolean(showHorizontalGrid);
		out.writeInt(horizontalLinesCount);
		out.writeDouble(yMax);
		out.writeDouble(yMin);
		out.writeObject(scoreColor);
		out.writeInt(scorePosition);
		out.writeBoolean(isYAutoscale);
	}
	
	
	/**
	 * Called when the ratio (height of the track / (yMax - y SCWLAMin)) changes.
	 */
	abstract protected void yFactorChanged();
}
