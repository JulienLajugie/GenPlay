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

import java.awt.Graphics;

import edu.yu.einstein.genplay.core.GenomeWindow;



/**
 * Graphics part of an empty track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class EmptyTrackGraphics extends TrackGraphics<Void> {

	private static final long serialVersionUID = 3893723568903136335L; // generated ID

	
	/**
	 * Creates an instance of {@link EmptyTrackGraphics}
	 * @param displayedGenomeWindow {@link GenomeWindow} currently displayed
	 */
	protected EmptyTrackGraphics(GenomeWindow displayedGenomeWindow) {
		super(displayedGenomeWindow, null);
	}


	@Override
	protected void drawTrack(Graphics g) {
		drawVerticalLines(g);
		drawStripes(g);
		drawName(g);
		drawMiddleVerticalLine(g);
	}
}
