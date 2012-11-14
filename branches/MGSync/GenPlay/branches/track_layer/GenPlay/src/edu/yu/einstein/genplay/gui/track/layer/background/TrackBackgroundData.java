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
package edu.yu.einstein.genplay.gui.track.layer.background;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.gui.track.layer.LayeredTrackConstants;


/**
 * Data showed in the track background layer
 * @author Julien Lajugie
 */
public class TrackBackgroundData implements Serializable {

	private static final long serialVersionUID = 8077420492002770819L; 	//generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	private boolean	isVerticalGridVisible;		// vertical grid visible or hidden
	private int		verticalLineCount;			// number of vertical lines showed in the track
	private double 	scoreMax;					// maximum score displayed in the track
	private double 	scoreMin;					// minimum score displayed in the track
	private double	yFactor;					// factor between the displayed intensity range and the screen height
	private boolean	isHorizontalGridVisible;	// horizontal grid visible or hidden
	private int		horizontalLineCount;		// number of horizontal lines showed in the track


	/**
	 * Creates an instance of {@link TrackBackgroundData}
	 */
	public TrackBackgroundData() {
		this.setVerticalGridVisible(LayeredTrackConstants.IS_VERTICAL_GRID_VISIBLE);
		this.setVerticalLineCount(LayeredTrackConstants.VERTICAL_LINES_COUNT);
		this.setScoreMax(0);
		this.setScoreMin(0);
		this.setyFactor(0);
		this.setHorizontalGridVisible(LayeredTrackConstants.IS_HORIZONTAL_GRID_VISIBLE);
		this.setHorizontalLineCount(LayeredTrackConstants.HORIZONTAL_LINE_COUNT);
	}


	/**
	 * @return the number of horizontal lines showed in a track
	 */
	public int getHorizontalLineCount() {
		return horizontalLineCount;
	}


	/**
	 * @return the maximum score displayed in the track
	 */
	public double getScoreMax() {
		return scoreMax;
	}


	/**
	 * @return the minimum score displayed in the track
	 */
	public double getScoreMin() {
		return scoreMin;
	}


	/**
	 * @return the number of vertical lines showed in a track
	 */
	public int getVerticalLineCount() {
		return verticalLineCount;
	}


	/**
	 * @return the yFactor (ratio between the height of the track and the score range displayed)
	 */
	public double getyFactor() {
		return yFactor;
	}


	/**
	 * @return true if the horizontal grid is visible, false otherwise
	 */
	public boolean isHorizontalGridVisible() {
		return isHorizontalGridVisible;
	}


	/**
	 * @return true if the vertical grid is visible, false otherwise
	 */
	public boolean isVerticalGridVisible() {
		return isVerticalGridVisible;
	}


	/**
	 * Unserializes the save format version number
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		setVerticalGridVisible(in.readBoolean());
		setVerticalLineCount(in.readInt());
		setScoreMax(in.readDouble());
		setScoreMin(in.readDouble());
		setyFactor(in.readDouble());
		setHorizontalGridVisible(in.readBoolean());
		setHorizontalLineCount(in.readInt());
	}


	/**
	 * Sets if the horizontal lines of the background of the track is visible or hidden
	 * @param isHorizontalGridVisible set to true to show the horizontal lines, false otherwise
	 */
	public void setHorizontalGridVisible(boolean isHorizontalGridVisible) {
		this.isHorizontalGridVisible = isHorizontalGridVisible;
	}


	/**
	 * Sets the number of horizontal lines displayed in the background of a track
	 * @param horizontalLineCount the number of horizontal lines to set
	 */
	public void setHorizontalLineCount(int horizontalLineCount) {
		this.horizontalLineCount = horizontalLineCount;
	}


	/**
	 * Sets the maximum score displayed in the track
	 * @param scoreMax maximum score to set
	 */
	public void setScoreMax(double scoreMax) {
		this.scoreMax = scoreMax;
	}


	/**
	 * Sets the minimum score displayed in the track
	 * @param scoreMin minimum score to set
	 */
	public void setScoreMin(double scoreMin) {
		this.scoreMin = scoreMin;
	}


	/**
	 * Sets if the vertical lines of the background of the track is visible or hidden
	 * @param isVerticalGridVisible set to true to show the vertical lines, false otherwise
	 */
	public void setVerticalGridVisible(boolean isVerticalGridVisible) {
		this.isVerticalGridVisible = isVerticalGridVisible;
	}


	/**
	 * Sets the number of vertical lines displayed in the background of a track
	 * @param verticalLineCount the number of vertical lines to set
	 */
	public void setVerticalLineCount(int verticalLineCount) {
		this.verticalLineCount = verticalLineCount;
	}


	/**
	 * Sets the yFactor of the track (ratio between the height of the track and the score range displayed).
	 * @param yFactor the yFactor to set
	 */
	public void setyFactor(double yFactor) {
		this.yFactor = yFactor;
	}


	/**
	 * Saves the format version number during serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeBoolean(isVerticalGridVisible);
		out.writeInt(getVerticalLineCount());
		out.writeDouble(getScoreMax());
		out.writeDouble(getScoreMin());
		out.writeDouble(getyFactor());
		out.writeBoolean(isHorizontalGridVisible);
		out.writeInt(getHorizontalLineCount());
	}
}
