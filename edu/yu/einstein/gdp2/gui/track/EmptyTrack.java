/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import yu.einstein.gdp2.core.GenomeWindow;

/**
 * An empty track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class EmptyTrack extends Track {

	private static final long serialVersionUID = 3508936560321856203L;	// generated ID
	
	
	/**
	 * Creates an instance of {@link EmptyTrack}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param trackNumber number of the track
	 */
	public EmptyTrack(GenomeWindow displayedGenomeWindow, int trackNumber) {
		initComponent(displayedGenomeWindow, trackNumber);
	}


	@Override
	public Track copy() {
		Track copiedTrack = new EmptyTrack(trackGraphics.genomeWindow, trackHandle.getTrackNumber());
		trackGraphics.copyTo(copiedTrack.trackGraphics);
		trackGraphics.repaint();
		copiedTrack.setPreferredHeight(getPreferredSize().height);
		return copiedTrack;		
	}

	
	@Override
	protected void initTrackGraphics(GenomeWindow displayedGenomeWindow) {
		trackGraphics = new EmptyTrackGraphics(displayedGenomeWindow);
	}
}
