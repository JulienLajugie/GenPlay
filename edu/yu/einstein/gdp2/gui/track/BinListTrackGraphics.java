/**
 * @author Julien Lajugie
 * @version 0.1
 */
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
	public void copyTo(TrackGraphics trackGraphics) {
		super.copyTo(trackGraphics);
		BinListTrackGraphics bltg = (BinListTrackGraphics) trackGraphics;
		bltg.data = this.data.deepClone();
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
