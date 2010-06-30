/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.binList.BinList;

/**
 * A track containing a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinListTrack extends CurveTrack<BinList> {

	private static final long serialVersionUID = -395099043710070726L; // generated ID

	
	/**
	 * Creates an instance of {@link BinListTrack}
	 * @param displayedGenomeWindow the displayed {@link GenomeWindow}
	 * @param trackNumber the number of the track
	 * @param data the {@link BinList} showed in the track
	 */
	public BinListTrack(GenomeWindow displayedGenomeWindow, int trackNumber, BinList data) {
		super(displayedGenomeWindow, trackNumber, data);
	}


	@Override
	protected TrackGraphics<BinList> createsTrackGraphics(GenomeWindow displayedGenomeWindow, BinList data) {
		return new BinListTrackGraphics(displayedGenomeWindow, data);
	}
}
