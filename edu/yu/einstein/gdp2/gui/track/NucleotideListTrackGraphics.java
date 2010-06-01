/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Graphics;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.enums.Nucleotide;
import yu.einstein.gdp2.core.list.DisplayableListOfLists;

/**
 * A {@link TrackGraphics} part of a {@link NucleotideListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class NucleotideListTrackGraphics extends TrackGraphics {

	private static final long serialVersionUID = -7170987212502378002L;				// generated ID
	private static final int NUCLEOTIDE_HEIGHT = 10;								// y position of the nucleotides on the track
	private static final Color BACKGROUND_COLOR = new Color(190, 220, 200);			// background color of the track
	//private static final Color BACKGROUND_COLOR = new Color(255, 200, 165);
	private static final Color ANY_COLOR = Color.black;								// color for "N" bases
	private static final Color THYMINE_COLOR = new Color(255, 80, 255);				// color for thymine bases
	private static final Color CYTOSINE_COLOR = new Color(255, 80, 0);				// color for cytosine bases
	private static final Color ADENINE_COLOR = Color.blue;							// color for adenine bases
	private static final Color GUANINE_COLOR = new Color(80, 80, 0);				// color for guanine bases
	private final DisplayableListOfLists<Nucleotide, Nucleotide[]> nucleotideList;	// list of nucleotides to 
	private int maxBaseWidth = 0;													// size on the screen of the widest base to display (in pixels) 


	/**
	 * Creates an instance of {@link NucleotideListTrackGraphics}
	 * @param displayedGenomeWindow a {@link GenomeWindow} to display
	 * @param nucleotideList a sequence of {@link Nucleotide} to display
	 */
	public NucleotideListTrackGraphics(GenomeWindow displayedGenomeWindow, DisplayableListOfLists<Nucleotide, Nucleotide[]> nucleotideList) {
		super(displayedGenomeWindow);
		this.nucleotideList = nucleotideList;
		// compute the length in pixels of the widest base to display
		String[] bases = {"N", "A", "C", "G", "T"};
		for (String currBase: bases) {
			maxBaseWidth = Math.max(maxBaseWidth, fm.stringWidth(currBase));
		}
	}


	@Override
	protected void drawTrack(Graphics g) {
		setBackground(BACKGROUND_COLOR);
		drawStripes(g);
		drawVerticalLines(g);
		drawNucleotides(g);
		drawName(g);
		drawMiddleVerticalLine(g);
	}


	/**
	 * Draws the {@link Nucleotide}
	 * @param g
	 */
	private void drawNucleotides(Graphics g) {
		if (nucleotideList != null) {
			long baseToPrintCount = genomeWindow.getSize();
			g.setColor(Color.black);
			// if there is enough room to print something
			if (maxBaseWidth * baseToPrintCount <= getWidth()) {
				Nucleotide[] nucleotides = nucleotideList.getFittedData(genomeWindow, xFactor);
				int j = 0;
				for (int i = genomeWindow.getStart(); i <= genomeWindow.getStop(); i++) {
					if (nucleotides[j] != null) {
						// compute the position on the screen
						int x = genomePosToScreenPos(i);
						// select a different color for each type of base
						switch (nucleotides[j]) {
						case THYMINE:
							g.setColor(THYMINE_COLOR);
							break;
						case CYTOSINE:
							g.setColor(CYTOSINE_COLOR);
							break;
						case ADENINE:
							g.setColor(ADENINE_COLOR);
							break;
						case GUANINE:
							g.setColor(GUANINE_COLOR);
							break;
						default:
							g.setColor(ANY_COLOR);
							break;
						}
						g.drawString(String.valueOf(nucleotides[j].getCode()), x, getHeight() - NUCLEOTIDE_HEIGHT);
					}
					j++;
				}			
			} else { 
				// if we can't print all the bases we just print a message for the user
				g.setColor(Color.black);
				g.drawString("Can't display sequence at this zoom level", 0, getHeight() - NUCLEOTIDE_HEIGHT);
			}
		}
	}
}
