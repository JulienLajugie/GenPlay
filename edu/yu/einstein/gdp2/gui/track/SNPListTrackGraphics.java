/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.List;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.SNP;
import yu.einstein.gdp2.core.SNPList.SNPList;


/**
 *  A {@link TrackGraphics} part of a track showing SNPS
 * @author Julien Lajugie
 * @version 0.1
 */
public class SNPListTrackGraphics extends TrackGraphics<SNPList> {

	private static final long serialVersionUID = -5740813392910733205L; 				// generated ID	
	private static final DecimalFormat COUNT_FORMAT = new DecimalFormat("###,###,###"); // format for the count
	private static final Color BACKGROUND_COLOR = new Color(255, 200, 165);
	private static final Color THYMINE_COLOR = new Color(255, 80, 255);					// color for thymine bases
	private static final Color CYTOSINE_COLOR = new Color(255, 80, 0);					// color for cytosine bases
	private static final Color ADENINE_COLOR = Color.blue;								// color for adenine bases
	private static final Color GUANINE_COLOR = new Color(80, 80, 0);					// color for guanine bases
	
	
	/**
	 * Creates an instance of {@link SNPListTrackGraphics}
	 * @param displayedGenomeWindow
	 * @param data
	 */
	protected SNPListTrackGraphics(GenomeWindow displayedGenomeWindow, SNPList data) {
		super(displayedGenomeWindow, data);
	}

	
	@Override
	protected void drawTrack(Graphics g) {
		drawBackground(g);
		drawStripes(g);
		drawVerticalLines(g);
		drawMiddleVerticalLine(g);
		drawSNP(g);
		drawNucleotide(g);
		drawName(g);
	}
	
	
	/**
	 * Draws the nucleotide name on the left of the track
	 * @param g {@link Graphics}
	 */
	private void drawNucleotide(Graphics g) {
		int halfFontHeight = g.getFontMetrics().getHeight() / 2;
		int lineHeight = getHeight() / 4;
		int halfLineHeight = lineHeight / 2;
		int yPos = halfLineHeight + halfFontHeight;
		g.setColor(ADENINE_COLOR);
		g.drawString("A", 5, yPos);
		g.setColor(CYTOSINE_COLOR);
		yPos = halfLineHeight + halfFontHeight + lineHeight;
		g.drawString("C", 5, yPos);
		g.setColor(GUANINE_COLOR);
		yPos = halfLineHeight + halfFontHeight + 2 * lineHeight;
		g.drawString("G", 5, yPos);
		g.setColor(THYMINE_COLOR);
		yPos = halfLineHeight + halfFontHeight + 3 * lineHeight;
		g.drawString("T", 5, yPos);
	}


	/**
	 * Draws the background of the track
	 * @param g {@link Graphics}
	 */
	private void drawBackground(Graphics g) {
		int width = getWidth();
		int lineHeight = getHeight() / 4;
		g.setColor(BACKGROUND_COLOR);
		for (int i = 0; i < 4; i++) {
			if (i % 2 == 0) {
				g.fillRect(0, i * lineHeight, width, lineHeight);
			}
		}
	}
	
	
	/**
	 * Draws the SNPs
	 * @param g {@link Graphics}
	 */
	private void drawSNP(Graphics g) {
		List<SNP> snpList = data.getFittedData(genomeWindow, xFactor);
		if ((snpList != null) && (snpList.size() > 0)) {
			// loop for each SNPs
			for (int i = 0; i < snpList.size(); i++) {
				// retrieve current SNP
				SNP currentSNP = snpList.get(i);
				// format base counts as strings 
				String firstBaseString = COUNT_FORMAT.format(currentSNP.getFirstBaseCount());
				String secondBaseString = COUNT_FORMAT.format(currentSNP.getSecondBaseCount());
				// compute the largest string width to print for the current SNP
				int firstBaseWidth = g.getFontMetrics().stringWidth(firstBaseString);
				int secondBaseWidth = g.getFontMetrics().stringWidth(secondBaseString);
				int widthSNP = Math.max(firstBaseWidth, secondBaseWidth);
				// check if there is enough space to print the values for the current SNP
				if (xFactor / widthSNP > 1) {
					drawDetailedSNP(g, currentSNP);
				} else {
					drawDenseSNP(g, currentSNP);
				}
			}	
		}
	}


	private void drawDenseSNP(Graphics g, SNP currentSNP) {
		int xPos = genomePosToScreenPos(currentSNP.getPosition());
		g.setColor(Color.GREEN);
		g.drawLine(xPos, 0, xPos, getHeight());
	}


	private void drawDetailedSNP(Graphics g, SNP currentSNP) {
		int halfFontHeight = g.getFontMetrics().getHeight() / 2;
		int lineHeight = getHeight() / 4;
		int halfLineHeight = lineHeight / 2;

		int xPos = genomePosToScreenPos(currentSNP.getPosition());
		int yPos = 0;		
		// print first base
		switch (currentSNP.getFirstBase()) {
		case ADENINE:
			yPos = halfLineHeight + halfFontHeight;
			break;
		case CYTOSINE:
			yPos = halfLineHeight + halfFontHeight + lineHeight;
			break;
		case GUANINE:
			yPos = halfLineHeight + halfFontHeight + 2 * lineHeight;
			break;
		case THYMINE:
			yPos = halfLineHeight + halfFontHeight + 3 * lineHeight;
			break;
		}
		String countStr = COUNT_FORMAT.format(currentSNP.getFirstBaseCount());
		g.setColor(Color.RED);
		g.drawString(countStr, xPos, yPos);
		
		// print second base
		switch (currentSNP.getSecondBase()) {
		case ADENINE:
			yPos = halfLineHeight + halfFontHeight;
			break;
		case CYTOSINE:
			yPos = halfLineHeight + halfFontHeight + lineHeight;
			break;
		case GUANINE:
			yPos = halfLineHeight + halfFontHeight + 2 * lineHeight;
			break;
		case THYMINE:
			yPos = halfLineHeight + halfFontHeight + 3 * lineHeight;
			break;
		}
		countStr = COUNT_FORMAT.format(currentSNP.getSecondBaseCount());
		if (currentSNP.isSecondBaseSignificant()) {
			g.setColor(Color.BLUE);
		} else {
			g.setColor(Color.BLACK);
		}
		g.drawString(countStr, xPos, yPos);
	}
}
