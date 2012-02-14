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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.yu.einstein.genplay.core.GenomeWindow;

/**
 * An abstract class providing common tools for the different kind of scored {@link Track} 
 * (ie: tracks having a value on the y axis)
 * @param <T> type of the data shown in the track
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class ScoredTrack<T> extends Track<T> {

	private static final long serialVersionUID = -4376731054381169516L; // generated ID
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
	protected ScoredTrack(int trackNumber, T data) {
		super(trackNumber, data);
	}
	
	
	/**
	 * @return the number of horizontal lines
	 */
	public final int getHorizontalLinesCount() {
		return ((ScoredTrackGraphics<?>)trackGraphics).getHorizontalLinesCount();
	}


	/**
	 * @return the color of the score
	 */
	public final Color getScoreColor() {
		return ((ScoredTrackGraphics<?>)trackGraphics).getScoreColor();
	}


	/**
	 * @return the position of the score
	 */
	public final int getScorePosition() {
		return ((ScoredTrackGraphics<?>)trackGraphics).getScorePosition();
	}


	/**
	 * @return the yMax
	 */
	public final double getYMax() {
		return ((ScoredTrackGraphics<?>)trackGraphics).getYMax();
	}	
	
	
	/**
	 * @return the yMin
	 */
	public final double getYMin() {
		return ((ScoredTrackGraphics<?>)trackGraphics).getYMin();
	}


	/**
	 * @return true if the horizontal grid is visible
	 */
	public final boolean isShowHorizontalGrid() {
		return ((ScoredTrackGraphics<?>)trackGraphics).isShowHorizontalGrid();
	}


	/**
	 * @param horizontalLinesCount the the number of horizontal lines to show
	 */
	public final void setHorizontalLinesCount(int horizontalLinesCount) {
		((ScoredTrackGraphics<?>)trackGraphics).setHorizontalLinesCount(horizontalLinesCount);
	}


	/**
	 * @param scoreColor the color of the score to set
	 */
	public final void setScoreColor(Color scoreColor) {
		((ScoredTrackGraphics<?>)trackGraphics).setScoreColor(scoreColor);
	}
	
	

	/**
	 * @param scorePosition the position of the score to set
	 */
	public final void setScorePosition(int scorePosition) {
		((ScoredTrackGraphics<?>)trackGraphics).setScorePosition(scorePosition);
	}


	/**
	 * @param showHorizontalGrid set to true to show the horizontal grid
	 */
	public final void setShowHorizontalGrid(boolean showHorizontalGrid) {
		((ScoredTrackGraphics<?>)trackGraphics).setShowHorizontalGrid(showHorizontalGrid) ;
	}
	
	
	/**
	 * @param yMax the yMax to set
	 */
	public final void setYMax(double yMax) {
		((ScoredTrackGraphics<?>)trackGraphics).setYMax(yMax);
	}


	/**
	 * @param yMin the yMin to set
	 */
	public final void setYMin(double yMin) {
		((ScoredTrackGraphics<?>)trackGraphics).setYMin(yMin);
	}
}
