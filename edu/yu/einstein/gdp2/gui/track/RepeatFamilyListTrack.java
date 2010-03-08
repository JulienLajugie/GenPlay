/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.repeatFamilyList.RepeatFamilyList;
import yu.einstein.gdp2.util.ZoomManager;


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
	 * @param zoomManager a {@link ZoomManager}
	 * @param displayedGenomeWindow the displayed {@link GenomeWindow}
	 * @param trackNumber the number of the track
	 * @param repeatList the {@link RepeatFamilyList} to display
	 */
	public RepeatFamilyListTrack(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow, int trackNumber, RepeatFamilyList repeatList) {
		this.repeatList = repeatList;
		initComponent(zoomManager, displayedGenomeWindow, trackNumber);
	}
	
	
	@Override
	public Track copy() {
		Track copiedTrack = new RepeatFamilyListTrack(trackGraphics.getZoomManager(), trackGraphics.genomeWindow, trackHandle.getTrackNumber(), repeatList);
		trackGraphics.copyTo(copiedTrack.trackGraphics);
		trackGraphics.repaint();
		copiedTrack.setPreferredHeight(getPreferredSize().height);
		return copiedTrack;	
	}

	
	@Override
	protected void initTrackGraphics(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow) {
		trackGraphics = new RepeatFamilyListTrackGraphics(zoomManager, displayedGenomeWindow, repeatList);		
	}
}
