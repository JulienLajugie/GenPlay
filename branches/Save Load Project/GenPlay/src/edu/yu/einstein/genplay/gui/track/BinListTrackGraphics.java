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
import java.io.IOException;
import java.io.ObjectInputStream;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.binList.operation.BLOMaxScoreToDisplay;
import edu.yu.einstein.genplay.core.list.binList.operation.BLOMinScoreToDisplay;
import edu.yu.einstein.genplay.core.manager.ChromosomeManager;
import edu.yu.einstein.genplay.core.manager.ExceptionManager;
import edu.yu.einstein.genplay.gui.track.drawer.BinListDrawer;
import edu.yu.einstein.genplay.gui.track.drawer.CurveDrawer;


/**
 * A {@link TrackGraphics} part of a {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinListTrackGraphics extends CurveTrackGraphics<BinList> {

	private static final long 	serialVersionUID = 1745399422702517182L; // generated ID
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	
	
	/**
	 * Saves the format version number during serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
	}
	
	
	/**
	 * Unserializes the save format version number
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
	}
	
	
	/**
	 * Creates an instance of a {@link BinListTrackGraphics}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param binList {@link BinList}
	 */
	protected BinListTrackGraphics(GenomeWindow displayedGenomeWindow, BinList binList) {
		super(displayedGenomeWindow, binList, new BLOMinScoreToDisplay(binList).compute(), new BLOMaxScoreToDisplay(binList).compute());
	}


	@Override
	protected void drawData(Graphics g) {
		CurveDrawer cd = new BinListDrawer(g, getWidth(), getHeight(), genomeWindow, yMin, yMax, trackColor, typeOfGraph, data);
		cd.draw();
	}


	@Override
	protected void drawScore(Graphics g) {
		try {
			short currentChromosome = ChromosomeManager.getInstance().getIndex(genomeWindow.getChromosome());
			g.setColor(getScoreColor());
			int xMid = (int) genomeWindow.getMiddlePosition();
			double yMid = 0;
			int scoreYPosition = 0;
			if (getScorePosition() == BOTTOM_SCORE_POSITION) {
				scoreYPosition =  getHeight() - 2;
			} else if (getScorePosition() == TOP_SCORE_POSITION) {
				scoreYPosition = g.getFontMetrics().getHeight();
			}			
			if ((data.get(currentChromosome) != null) && ((xMid / data.getBinSize()) < data.size(currentChromosome))) {
				yMid = data.getScore(xMid);
			}
			g.drawString("y=" + SCORE_FORMAT.format(yMid), getWidth() / 2 + 3, scoreYPosition);
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while drawing the coordinates");
		}
	}
	

	@Override
	public CurveDrawer getDrawer(Graphics g, int trackWidth, int trackHeight, GenomeWindow genomeWindow, double scoreMin, double scoreMax) {
		return new BinListDrawer(g, trackWidth, trackHeight, genomeWindow, scoreMin, scoreMax, trackColor, typeOfGraph, data);
	}
	

	@Override
	protected double getMaxScoreToDisplay() {
		return new BLOMaxScoreToDisplay(data).compute();
	}


	@Override
	protected double getMinScoreToDisplay() {
		return new BLOMinScoreToDisplay(data).compute();
	}
}
