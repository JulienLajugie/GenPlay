/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.repeatFamilyList.RepeatFamilyList;

/**
 * A track containing a {@link RepeatFamilyList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class RepeatFamilyListTrack extends Track<RepeatFamilyList> {

	private static final long serialVersionUID = 2723805094901070252L; // generated ID	
	
	
	/**
	 * Creates an instance of {@link RepeatFamilyListTrack}
	 * @param displayedGenomeWindow the displayed {@link GenomeWindow}
	 * @param trackNumber the number of the track
	 * @param data the {@link RepeatFamilyList} to display
	 */
	public RepeatFamilyListTrack(GenomeWindow displayedGenomeWindow, int trackNumber, RepeatFamilyList data) {
		super(displayedGenomeWindow, trackNumber, data);
	}


	@Override
	protected TrackGraphics<RepeatFamilyList> createsTrackGraphics(GenomeWindow displayedGenomeWindow, RepeatFamilyList data) {
		return new RepeatFamilyListTrackGraphics(displayedGenomeWindow, data);
	}
}
