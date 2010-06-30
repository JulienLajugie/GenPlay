/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.geneList.GeneList;

/**
 * A track containing a {@link GeneList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class GeneListTrack extends Track<GeneList> {

	private static final long serialVersionUID = 907497013953591152L; // generated ID

	
	/**
	 * Creates an instance of {@link GeneListTrack}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param trackNumber number of the track
	 * @param data {@link GeneList} showed in the track
	 */
	public GeneListTrack(GenomeWindow displayedGenomeWindow, int trackNumber,  GeneList data) {
		super(displayedGenomeWindow, trackNumber, data);
	}


	@Override
	protected TrackGraphics<GeneList> createsTrackGraphics(GenomeWindow displayedGenomeWindow, GeneList data) {
		return new GeneListTrackGraphics(displayedGenomeWindow, data);
	}
}
