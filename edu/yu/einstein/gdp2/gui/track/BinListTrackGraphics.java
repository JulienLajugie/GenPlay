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
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Graphics;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOMaxScoreToDisplay;
import yu.einstein.gdp2.core.list.binList.operation.BLOMinScoreToDisplay;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.gui.track.drawer.BinListDrawer;
import yu.einstein.gdp2.gui.track.drawer.CurveDrawer;

/**
 * A {@link TrackGraphics} part of a {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinListTrackGraphics extends CurveTrackGraphics<BinList> {

	private static final long 	serialVersionUID = 1745399422702517182L; // generated ID

	
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
			g.setColor(Color.red);
			int xMid = (int) genomeWindow.getMiddlePosition();
			double yMid = 0;
			if ((data.get(currentChromosome) != null) && ((xMid / data.getBinSize()) < data.size(currentChromosome))) {
				yMid = data.getScore(xMid);
			}
			g.drawString("y=" + SCORE_FORMAT.format(yMid), getWidth() / 2 + 3,	getHeight() - 2);
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
