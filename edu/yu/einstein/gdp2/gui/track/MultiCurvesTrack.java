/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import yu.einstein.gdp2.core.GenomeWindow;

/**
 * A track showing multiples {@link CurveTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class MultiCurvesTrack extends ScoredTrack<CurveTrack<?>[]> {

	private static final long serialVersionUID = -8961218330334104474L; // generated ID
	
	
	/**
	 * Creates an instance of {@link MultiCurvesTrack}
	 * @param displayedGenomeWindow the displayed {@link GenomeWindow}
	 * @param trackNumber the number of the track
	 * @param data array of {@link CurveTrack}
	 */
	public MultiCurvesTrack(GenomeWindow displayedGenomeWindow, int trackNumber, CurveTrack<?>[] data) {
		super(displayedGenomeWindow, trackNumber, data);
	}


	@Override
	protected TrackGraphics<CurveTrack<?>[]> createsTrackGraphics(GenomeWindow displayedGenomeWindow, CurveTrack<?>[] data) {
		return new MultiCurvesTrackGraphics(displayedGenomeWindow, data);
	}
}
