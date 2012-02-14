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

import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.operation.SCWLOMaxScoreToDisplay;
import edu.yu.einstein.genplay.core.list.SCWList.operation.SCWLOMinScoreToDisplay;
import edu.yu.einstein.genplay.gui.track.drawer.CurveDrawer;
import edu.yu.einstein.genplay.gui.track.drawer.SCWListDrawer;


/**
 * A {@link TrackGraphics} part of a {@link SCWListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SCWListTrackGraphics extends CurveTrackGraphics<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = -996344743923414353L; // generated ID
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
	 * Creates an instance of a {@link SCWListTrackGraphics}
	 * @param data displayed {@link ScoredChromosomeWindowList} 
	 */
	protected SCWListTrackGraphics(ScoredChromosomeWindowList data) {
		super(data, new SCWLOMinScoreToDisplay(data).compute(), new SCWLOMaxScoreToDisplay(data).compute());
	}
	
	
	@Override
	protected void drawData(Graphics g) {
		CurveDrawer cd = new SCWListDrawer(g, getWidth(), getHeight(), yMin, yMax, trackColor, typeOfGraph, data);
		cd.draw();		
	}


	@Override
	protected void drawScore(Graphics g) {
		g.setColor(getScoreColor());
		double middlePosition = projectWindow.getGenomeWindow().getMiddlePosition();
		double middleScore = data.getScore((int) middlePosition);
		int scoreYPosition = 0;
		if (getScorePosition() == BOTTOM_SCORE_POSITION) {
			scoreYPosition =  getHeight() - 2;
		} else if (getScorePosition() == TOP_SCORE_POSITION) {
			scoreYPosition = g.getFontMetrics().getHeight();
		}	
		g.drawString("y=" + SCORE_FORMAT.format(middleScore), getWidth() / 2 + 3, scoreYPosition);	
	}


	@Override
	public CurveDrawer getDrawer(Graphics g, int trackWidth, int trackHeight, double scoreMin, double scoreMax) {
		return new SCWListDrawer(g, trackWidth, trackHeight, scoreMin, scoreMax, trackColor, typeOfGraph, data);
	}


	@Override
	protected double getMaxScoreToDisplay() {
		return new SCWLOMaxScoreToDisplay(data).compute();
	}


	@Override
	protected double getMinScoreToDisplay() {
		return new SCWLOMinScoreToDisplay(data).compute();
	}
}
