/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;

/**
 * A track containing a {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SCWListTrack extends CurveTrack<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = -2203140318940911180L; // generated ID

	
	/**
	 * Creates an instance of {@link SCWListTrack}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param trackNumber number of the track
	 * @param data {@link ScoredChromosomeWindowList} showed in the track
	 */
	public SCWListTrack(GenomeWindow displayedGenomeWindow, int trackNumber,  ScoredChromosomeWindowList data) {
		super(displayedGenomeWindow, trackNumber, data);
	}


	@Override
	protected TrackGraphics<ScoredChromosomeWindowList> createsTrackGraphics(GenomeWindow displayedGenomeWindow, ScoredChromosomeWindowList data) {
		return new SCWListTrackGraphics(displayedGenomeWindow, data);
	}
}
