/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Graphics;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOMaxScoreToDisplay;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOMinScoreToDisplay;
import yu.einstein.gdp2.gui.track.drawer.CurveDrawer;
import yu.einstein.gdp2.gui.track.drawer.SCWListDrawer;

/**
 * A {@link TrackGraphics} part of a {@link SCWListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SCWListTrackGraphics extends CurveTrackGraphics {

	private static final long serialVersionUID = -996344743923414353L; // generated ID
	private final ScoredChromosomeWindowList data; // data showed


	/**
	 * Creates an instance of a {@link SCWListTrackGraphics}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param data displayed {@link ScoredChromosomeWindowList} 
	 */
	protected SCWListTrackGraphics(GenomeWindow displayedGenomeWindow, ScoredChromosomeWindowList data) {
		super(displayedGenomeWindow, new SCWLOMinScoreToDisplay(data).compute(), new SCWLOMaxScoreToDisplay(data).compute());
		this.data = data;
		// we don't want the max equals to the min
		if (yMin == yMax) {
			if (yMin > 0) {
				setYMin(0);
			} else if (yMax < 0) {
				setYMax(0);
			}
		}
	}


	@Override
	protected void drawScore(Graphics g) {
		g.setColor(Color.red);
		double middlePosition = genomeWindow.getMiddlePosition();
		double middleScore = data.getScore((int) middlePosition);
		g.drawString("y=" + SCORE_FORMAT.format(middleScore), getWidth() / 2 + 3, getHeight() - 2);	
	}


	@Override
	protected void yFactorChanged() {
		repaint();		
	}


	@Override
	protected void drawData(Graphics g) {
		CurveDrawer cd = new SCWListDrawer(g, getWidth(), getHeight(), genomeWindow, yMin, yMax, trackColor, typeOfGraph, data);
		cd.draw();		
	}


	@Override
	public CurveDrawer getDrawer(Graphics g, int trackWidth, int trackHeight, GenomeWindow genomeWindow, double scoreMin, double scoreMax) {
		return new SCWListDrawer(g, trackWidth, trackHeight, genomeWindow, scoreMin, scoreMax, trackColor, typeOfGraph, data);
	}
}
