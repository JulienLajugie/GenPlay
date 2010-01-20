/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.util.ZoomManager;


/**
 * A track containing a {@link GeneList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class GeneListTrack extends Track {

	private static final long serialVersionUID = 907497013953591152L; // generated ID
	private final GeneList data; 	// GeneList used to create the track

	
	/**
	 * Creates an instance of {@link GeneListTrack}
	 * @param zoomManager a {@link ZoomManager}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param trackNumber number of the track
	 * @param data {@link GeneList} showed in the track
	 */
	public GeneListTrack(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow, int trackNumber,  GeneList data) {
		this.data = data;
		initComponent(zoomManager, displayedGenomeWindow, trackNumber);
	}
	
	
	/* (non-Javadoc)
	 * @see yu.einstein.gdp2.gui.track.Track#copy()
	 */
	@Override
	public Track copy() {
		Track copiedTrack = new GeneListTrack(trackGraphics.getZoomManager(), trackGraphics.genomeWindow, trackHandle.getTrackNumber(), data);
		trackGraphics.copyTo(copiedTrack.trackGraphics);
		trackGraphics.repaint();
		copiedTrack.setPreferredHeight(getPreferredSize().height);
		return copiedTrack;		
	}

	
	/* (non-Javadoc)
	 * @see yu.einstein.gdp2.gui.track.Track#createTrackGraphics(yu.einstein.gdp2.util.ZoomManager, yu.einstein.gdp2.core.GenomeWindow)
	 */
	@Override
	protected void initTrackGraphics(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow) {
		trackGraphics = new GeneListTrackGraphics(zoomManager, displayedGenomeWindow, data);
	}
	
	
	/**
	 * @return the data
	 */
	public GeneList getData() {
		return data;
	}
}
