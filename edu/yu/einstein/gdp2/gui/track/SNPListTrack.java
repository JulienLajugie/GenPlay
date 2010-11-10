/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.SNPList.SNPList;


/**
 * A track showing SNPS
 * @author Julien Lajugie
 * @version 0.1
 */
public class SNPListTrack extends Track<SNPList> {

	private static final long serialVersionUID = -7650676029551779351L; // generated ID

	
	/**
	 * Creates an instance of {@link SNPListTrack}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param trackNumber number of the track
	 * @param data {@link SNPList} showed in the track
	 */
	public SNPListTrack(GenomeWindow displayedGenomeWindow, int trackNumber, SNPList data) {
		super(displayedGenomeWindow, trackNumber, data);
	}
	
	
	@Override
	public TrackGraphics<SNPList> createsTrackGraphics(GenomeWindow displayedGenomeWindow, SNPList data) {
		return new SNPListTrackGraphics(displayedGenomeWindow, data);
	}

}
