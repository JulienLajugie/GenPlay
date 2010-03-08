/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Graphics;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.enums.Nucleotide;
import yu.einstein.gdp2.core.list.DisplayableListOfLists;
import yu.einstein.gdp2.util.ZoomManager;


/**
 * A {@link TrackGraphics} part of a {@link NucleotideListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class NucleotideListTrackGraphics extends TrackGraphics {

	private static final long serialVersionUID = -7170987212502378002L;				// generated ID
	private final DisplayableListOfLists<Nucleotide, Nucleotide[]> nucleotideList;	// list of nucleotides to 
	private int maxBaseWidth = 0;													// size on the screen of the widest base to display (in pixels) 
	
	
	/**
	 * Creates an instance of {@link NucleotideListTrackGraphics}
	 * @param zoomManager a {@link ZoomManager}
	 * @param displayedGenomeWindow a {@link GenomeWindow} to display
	 * @param nucleotideList a sequence of {@link Nucleotide} to display
	 */
	public NucleotideListTrackGraphics(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow, DisplayableListOfLists<Nucleotide, Nucleotide[]> nucleotideList) {
		super(zoomManager, displayedGenomeWindow);
		this.nucleotideList = nucleotideList;
		// compute the length in pixels of the widest base to display
		String[] bases = {"N", "A", "C", "G", "T"};
		for (String currBase: bases) {
			maxBaseWidth = Math.max(maxBaseWidth, fm.stringWidth(currBase));
		}
	}

	
	@Override
	protected void drawTrack(Graphics g) {
		int baseToPrintCount = genomeWindow.getSize();
		// if there is enough room to print something
		if (maxBaseWidth * baseToPrintCount <= getWidth()) {
			Nucleotide[] nucleotides = nucleotideList.getFittedData(genomeWindow, xFactor);
			int j = 0;
			for (int i = genomeWindow.getStart(); i <= genomeWindow.getStop(); i++) {
				int y = genomePosToScreenPos(i);
				g.drawString(String.valueOf(nucleotides[j].getCode()), 0, y);
			}			
		}		
	}
}
