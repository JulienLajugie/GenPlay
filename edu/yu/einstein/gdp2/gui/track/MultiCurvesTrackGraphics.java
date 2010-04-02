/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Graphics;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.gui.event.repaintEvent.RepaintEvent;
import yu.einstein.gdp2.gui.event.repaintEvent.RepaintListener;
import yu.einstein.gdp2.util.ZoomManager;


/**
 * A {@link TrackGraphics} part of a {@link MultiCurvesTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class MultiCurvesTrackGraphics extends TrackGraphics implements RepaintListener {

	private static final long serialVersionUID = 6508763050002286457L; // generated ID
	private final CurveTrack[] curveTracks; // array of curve tracks
	
	
	/**
	 * Creates an instance of {@link MultiCurvesTrackGraphics}
	 * @param zoomManager a {@link ZoomManager}
	 * @param displayedGenomeWindow the displayed {@link GenomeWindow}
	 * @param curveTracks array of {@link CurveTrack}
	 */
	public MultiCurvesTrackGraphics(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow, CurveTrack[] curveTracks) {
		super(zoomManager, displayedGenomeWindow);
		this.curveTracks = curveTracks;
		// add repaint listeners so the multicurves track is repainted when on of the curves track is repainted
		for (Track currentTrack: curveTracks) {
			currentTrack.trackGraphics.addRepaintListener(this);
		}
		setIgnoreRepaint(true);
	}

	
	@Override
	protected void drawTrack(Graphics g) {
		drawStripes(g);
		drawVerticalLines(g);
		drawData(g);
		drawName(g);
		drawMiddleVerticalLine(g);
	}

	
	/**
	 * Draws the data of the {@link CurveTrack}
	 * @param g
	 */
	private void drawData(Graphics g) {
		for (int i = curveTracks.length; i > 0; i--) {
			CurveTrackGraphics ctg = (CurveTrackGraphics) curveTracks[i - 1].trackGraphics;
			ctg.drawData(g);
		}		
	}


	@Override
	public void componentRepainted(RepaintEvent evt) {
		drawTrack(getGraphics());		
	}
}
