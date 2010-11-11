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
public class NucleotideListTrackGraphics extends TrackGraphics<DisplayableListOfLists<Nucleotide, Nucleotide[]>> {

	private static final long serialVersionUID = -7170987212502378002L;				// generated ID
	private static final int NUCLEOTIDE_HEIGHT = 10;								// y position of the nucleotides on the track
	private static final Color BACKGROUND_COLOR = new Color(190, 220, 200);			// background color of the track
	//private static final Color BACKGROUND_COLOR = new Color(255, 200, 165);
	private static final Color ANY_COLOR = Color.black;								// color for "N" bases
	private static final Color THYMINE_COLOR = new Color(255, 80, 255);				// color for thymine bases
	private static final Color CYTOSINE_COLOR = new Color(255, 80, 0);				// color for cytosine bases
	private static final Color ADENINE_COLOR = Color.blue;							// color for adenine bases
	private static final Color GUANINE_COLOR = new Color(80, 80, 0);				// color for guanine bases
	private int maxBaseWidth = 0;													// size on the screen of the widest base to display (in pixels) 


	/**
	 * Creates an instance of {@link NucleotideListTrackGraphics}
	 * @param displayedGenomeWindow a {@link GenomeWindow} to display
	 * @param data a sequence of {@link Nucleotide} to display
	 */
	public NucleotideListTrackGraphics(GenomeWindow displayedGenomeWindow, DisplayableListOfLists<Nucleotide, Nucleotide[]> data) {
		super(displayedGenomeWindow, data);
		// compute the length in pixels of the widest base to display
		String[] bases = {"N", "A", "C", "G", "T"};
		for (String currBase: bases) {
			maxBaseWidth = Math.max(maxBaseWidth, fm.stringWidth(currBase));
		}
	}


	/**
	 * Draws a dense representation of a specified {@link Nucleotide} (ie: a colored stripe)
	 * @param g {@link Graphics}
	 * @param nucleotide {@link Nucleotide} to draw
	 * @param position position of the nucleotide
	 */
	private void drawDenseNucleotide(Graphics g, Nucleotide nucleotide, int position) {
		// compute the position on the screen
		int x = genomePosToScreenPos(position);
		int nucleoWith = twoGenomePosToScreenWidth(position, position + 1);  
		// select a different color for each type of base
		switch (nucleotide) {
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
		g.fillRect(x, 0, nucleoWith, getHeight());
	}

	
	/**
	 * Draws a detailed representation of a specified {@link Nucleotide} (ie: a letter)
	 * @param g {@link Graphics}
	 * @param nucleotide {@link Nucleotide} to draw
	 * @param position position of the nucleotide
	 */
	private void drawDetailedNucleotide(Graphics g, Nucleotide nucleotide, int position) {
			// compute the position on the screen
			int x = genomePosToScreenPos(position);
			// select a different color for each type of base
			switch (nucleotide) {
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
			g.drawString(String.valueOf(nucleotide.getCode()), x, getHeight() - NUCLEOTIDE_HEIGHT);
	}
	
	
	/**
	 * Draws the {@link Nucleotide}
	 * @param g
	 */
	private void drawNucleotides(Graphics g) {
		if (data != null) {
			long baseToPrintCount = genomeWindow.getSize();
			g.setColor(Color.black);
			// if there is enough room to print something
			if (baseToPrintCount <= getWidth()) {
				Nucleotide[] nucleotides = data.getFittedData(genomeWindow, xFactor);
				int j = 0;
				for (int i = genomeWindow.getStart(); i <= genomeWindow.getStop(); i++) {
					if (nucleotides[j] != null) {
						if (maxBaseWidth * baseToPrintCount <= getWidth()) {
							drawDetailedNucleotide(g, nucleotides[j], i);
						} else {
							drawDenseNucleotide(g, nucleotides[j], i);
						}
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
	

	@Override
	protected void drawTrack(Graphics g) {
		setBackground(BACKGROUND_COLOR);
		drawStripes(g);
		drawVerticalLines(g);
		drawNucleotides(g);
		drawName(g);
		drawMiddleVerticalLine(g);
	}
}
