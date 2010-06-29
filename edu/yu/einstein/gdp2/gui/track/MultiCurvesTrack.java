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
public class MultiCurvesTrack extends ScoredTrack {

	private static final long serialVersionUID = -8961218330334104474L; // generated ID
	private final CurveTrack<?>[] curveTracks; // array of curve tracks
	
	/**
	 * Creates an instance of {@link MultiCurvesTrack}
	 * @param displayedGenomeWindow the displayed {@link GenomeWindow}
	 * @param trackNumber the number of the track
	 * @param curveTracks array of {@link CurveTrack}
	 */
	public MultiCurvesTrack(GenomeWindow displayedGenomeWindow, int trackNumber, CurveTrack<?>[] curveTracks) {
		this.curveTracks = curveTracks;
		initComponent(displayedGenomeWindow, trackNumber);
	}
	
	
	@Override
	public Track copy() {
		Track copiedTrack = new MultiCurvesTrack(trackGraphics.genomeWindow, trackHandle.getTrackNumber(), curveTracks);
		trackGraphics.copyTo(copiedTrack.trackGraphics);
		trackGraphics.repaint();
		copiedTrack.setPreferredHeight(getPreferredSize().height);
		return copiedTrack;		
	}

	
	@Override
	protected void initTrackGraphics(GenomeWindow displayedGenomeWindow) {
		trackGraphics = new MultiCurvesTrackGraphics(displayedGenomeWindow, curveTracks);
	}
}
