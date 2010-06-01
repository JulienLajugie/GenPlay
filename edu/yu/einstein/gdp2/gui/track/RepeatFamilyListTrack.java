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
public final class RepeatFamilyListTrack extends Track {

	private static final long serialVersionUID = 2723805094901070252L; // generated ID	
	private final RepeatFamilyList repeatList;	// repeatList used at the creation
	
	
	/**
	 * Creates an instance of {@link RepeatFamilyListTrack}
	 * @param displayedGenomeWindow the displayed {@link GenomeWindow}
	 * @param trackNumber the number of the track
	 * @param repeatList the {@link RepeatFamilyList} to display
	 */
	public RepeatFamilyListTrack(GenomeWindow displayedGenomeWindow, int trackNumber, RepeatFamilyList repeatList) {
		this.repeatList = repeatList;
		initComponent(displayedGenomeWindow, trackNumber);
	}
	
	
	@Override
	public Track copy() {
		Track copiedTrack = new RepeatFamilyListTrack(trackGraphics.genomeWindow, trackHandle.getTrackNumber(), repeatList);
		trackGraphics.copyTo(copiedTrack.trackGraphics);
		trackGraphics.repaint();
		copiedTrack.setPreferredHeight(getPreferredSize().height);
		return copiedTrack;	
	}

	
	@Override
	protected void initTrackGraphics(GenomeWindow displayedGenomeWindow) {
		trackGraphics = new RepeatFamilyListTrackGraphics(displayedGenomeWindow, repeatList);		
	}
}
