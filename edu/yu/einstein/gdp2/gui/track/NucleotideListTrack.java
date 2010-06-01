/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.enums.Nucleotide;
import yu.einstein.gdp2.core.list.DisplayableListOfLists;

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
	 * @param displayedGenomeWindow the displayed {@link GenomeWindow}
	 * @param trackNumber the number of the track
	 * @param nucleotideList list of {@link Nucleotide} to display in the track
	 */
	public NucleotideListTrack(GenomeWindow displayedGenomeWindow, int trackNumber, DisplayableListOfLists<Nucleotide, Nucleotide[]> nucleotideList) {
		this.nucleotideList = nucleotideList;
		initComponent(displayedGenomeWindow, trackNumber);
	}
	
	
	@Override
	public Track copy() {
		Track copiedTrack = new NucleotideListTrack(trackGraphics.genomeWindow, trackHandle.getTrackNumber(), nucleotideList);
		trackGraphics.copyTo(copiedTrack.trackGraphics);
		trackGraphics.repaint();
		copiedTrack.setPreferredHeight(getPreferredSize().height);
		return copiedTrack;	
	}

	
	@Override
	protected void initTrackGraphics(GenomeWindow displayedGenomeWindow) {
		trackGraphics = new NucleotideListTrackGraphics(displayedGenomeWindow, nucleotideList);		
	}
}
