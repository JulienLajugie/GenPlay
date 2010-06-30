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
public final class EmptyTrack extends Track<Void> {

	private static final long serialVersionUID = 3508936560321856203L;	// generated ID
	
	
	/**
	 * Creates an instance of {@link EmptyTrack}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param trackNumber number of the track
	 */
	public EmptyTrack(GenomeWindow displayedGenomeWindow, int trackNumber) {
		super(displayedGenomeWindow, trackNumber, null);
	}


	@Override
	protected TrackGraphics<Void> createsTrackGraphics(GenomeWindow displayedGenomeWindow, Void data) {
		return new EmptyTrackGraphics(displayedGenomeWindow);
	}
}
