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
public class NucleotideListTrack extends Track<DisplayableListOfLists<Nucleotide, Nucleotide[]>> {

	private static final long serialVersionUID = 8424429602220353656L; // generated ID
	
	
	/**
	 * Creates an instance of {@link NucleotideListTrack}
	 * @param displayedGenomeWindow the displayed {@link GenomeWindow}
	 * @param trackNumber the number of the track
	 * @param data list of {@link Nucleotide} to display in the track
	 */
	public NucleotideListTrack(GenomeWindow displayedGenomeWindow, int trackNumber, DisplayableListOfLists<Nucleotide, Nucleotide[]> data) {
		super(displayedGenomeWindow, trackNumber, data);
	}


	@Override
	protected TrackGraphics<DisplayableListOfLists<Nucleotide, Nucleotide[]>> 
	createsTrackGraphics(GenomeWindow displayedGenomeWindow, DisplayableListOfLists<Nucleotide, Nucleotide[]> data) {
		return new NucleotideListTrackGraphics(displayedGenomeWindow, data);
	}
}
