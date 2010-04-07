/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.list.SCWList.SCWListOperations;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.gui.track.drawer.CurveDrawer;
import yu.einstein.gdp2.gui.track.drawer.SCWListDrawer;
import yu.einstein.gdp2.util.ZoomManager;

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
	 * @param zoomManager a {@link ZoomManager}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param data displayed {@link ScoredChromosomeWindowList} 
	 */
	protected SCWListTrackGraphics(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow, ScoredChromosomeWindowList data) {
		super(zoomManager, displayedGenomeWindow, SCWListOperations.minScoreToDisplay(data), SCWListOperations.maxScoreToDisplay(data));
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
		double middleScore = 0;
		g.setColor(Color.red);
		double middlePosition = genomeWindow.getMiddlePosition();
		List<ScoredChromosomeWindow> list = data.getFittedData(genomeWindow, xFactor);
		if ((list != null) && (list.size() > 0)) {
			int i = 0;
			List<Double> values = new ArrayList<Double>();
			// we add the score of every window that exists on middlePosition
			while ((i < list.size()) && (list.get(i).getStart() < middlePosition)) {
				if (list.get(i).getStop() >= middlePosition) {
					values.add(list.get(i).getScore());
				}
				i++;
			}
			// we want to print the greatest score of the windows in the middle
			if (values.size() > 0) {
				middleScore = Collections.max(values);
			}			
		}
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
