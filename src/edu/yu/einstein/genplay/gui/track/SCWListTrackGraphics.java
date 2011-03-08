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

import edu.yu.einstein.genplay.core.GenomeWindow;
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


	/**
	 * Creates an instance of a {@link SCWListTrackGraphics}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param data displayed {@link ScoredChromosomeWindowList} 
	 */
	protected SCWListTrackGraphics(GenomeWindow displayedGenomeWindow, ScoredChromosomeWindowList data) {
		super(displayedGenomeWindow, data, new SCWLOMinScoreToDisplay(data).compute(), new SCWLOMaxScoreToDisplay(data).compute());
	}
	
	
	@Override
	protected void drawData(Graphics g) {
		CurveDrawer cd = new SCWListDrawer(g, getWidth(), getHeight(), genomeWindow, yMin, yMax, trackColor, typeOfGraph, data);
		cd.draw();		
	}


	@Override
	protected void drawScore(Graphics g) {
		g.setColor(Color.red);
		double middlePosition = genomeWindow.getMiddlePosition();
		double middleScore = data.getScore((int) middlePosition);
		g.drawString("y=" + SCORE_FORMAT.format(middleScore), getWidth() / 2 + 3, getHeight() - 2);	
	}


	@Override
	public CurveDrawer getDrawer(Graphics g, int trackWidth, int trackHeight, GenomeWindow genomeWindow, double scoreMin, double scoreMax) {
		return new SCWListDrawer(g, trackWidth, trackHeight, genomeWindow, scoreMin, scoreMax, trackColor, typeOfGraph, data);
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
