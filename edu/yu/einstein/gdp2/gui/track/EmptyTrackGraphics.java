/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Graphics;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.util.ZoomManager;


/**
 * Graphics part of an empty track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class EmptyTrackGraphics extends TrackGraphics {

	private static final long serialVersionUID = 3893723568903136335L; // generated ID

	
	/**
	 * Creates an instance of {@link EmptyTrackGraphics}
	 * @param zoomManager a {@link ZoomManager}
	 * @param displayedGenomeWindow {@link GenomeWindow} currently displayed
	 */
	protected EmptyTrackGraphics(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow) {
		super(zoomManager, displayedGenomeWindow);
	}


	@Override
	protected void drawTrack(Graphics g) {
		drawStripes(g);
		drawVerticalLines(g);
		drawName(g);
		drawMiddleVerticalLine(g);
	}
}
