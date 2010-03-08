/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.enums.Nucleotide;
import yu.einstein.gdp2.core.list.DisplayableListOfLists;
import yu.einstein.gdp2.util.ZoomManager;


/**
 * A track showing a sequence of {@link Nucleotide}
 * @author Julien Lajugie
 * @version 0.1
 */
public class NucleotideListTrack extends Track {

	private static final long serialVersionUID = 8424429602220353656L; // generated ID
	private final DisplayableListOfLists<Nucleotide, Nucleotide[]> nucleotideList;	// list of {@link Nucleotide} to display in the track
	
	
	/**
	 * Creates an instance of {@link NucleotideListTrack}
	 * @param zoomManager a {@link ZoomManager}
	 * @param displayedGenomeWindow the displayed {@link GenomeWindow}
	 * @param trackNumber the number of the track
	 * @param nucleotideList list of {@link Nucleotide} to display in the track
	 */
	public NucleotideListTrack(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow, int trackNumber, DisplayableListOfLists<Nucleotide, Nucleotide[]> nucleotideList) {
		this.nucleotideList = nucleotideList;
		initComponent(zoomManager, displayedGenomeWindow, trackNumber);
	}
	
	
	@Override
	public Track copy() {
		Track copiedTrack = new NucleotideListTrack(trackGraphics.getZoomManager(), trackGraphics.genomeWindow, trackHandle.getTrackNumber(), nucleotideList);
		trackGraphics.copyTo(copiedTrack.trackGraphics);
		trackGraphics.repaint();
		copiedTrack.setPreferredHeight(getPreferredSize().height);
		return copiedTrack;	
	}

	
	@Override
	protected void initTrackGraphics(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow) {
		trackGraphics = new NucleotideListTrackGraphics(zoomManager, displayedGenomeWindow, nucleotideList);		
	}
}
