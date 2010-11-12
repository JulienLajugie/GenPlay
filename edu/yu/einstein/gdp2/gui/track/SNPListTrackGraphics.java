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
import yu.einstein.gdp2.core.enums.Nucleotide;
import yu.einstein.gdp2.util.ColorConverters;


/**
 *  A {@link TrackGraphics} part of a track showing SNPS
 * @author Julien Lajugie
 * @version 0.1
 */
public class SNPListTrackGraphics extends TrackGraphics<SNPList> {

	private static final long serialVersionUID = -5740813392910733205L; 				// generated ID	
	private static final DecimalFormat COUNT_FORMAT = new DecimalFormat("###,###,###"); // format for the count
	private static final Color BACKGROUND_COLOR = new Color(255, 200, 165);				// color of the stripes in the background
	private static final Color FIRST_BASE_COLOR = new Color(0, 0, 200);					// color of the first base
	private static final Color SECOND_BASE_COLOR = new Color(200, 0, 0);				// color of the second base
	private static final Color FIRST_BASE_COLOR2 = new Color(0, 200, 0);				// color of the first base when the second base is not significant
	private static final Color SECOND_BASE_COLOR2 = Color.BLACK;						// color of a non significant second base


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
		int halfLineHeight = getHeight() / 8;
		int yPos = halfLineHeight + halfFontHeight;
		int leftXPos = 5;
		int rightXPos = getWidth() - 10;
		g.setColor(ColorConverters.nucleotideToColor(Nucleotide.ADENINE));
		g.drawString("A", leftXPos, yPos);
		g.drawString("A", rightXPos, yPos);
		g.setColor(ColorConverters.nucleotideToColor(Nucleotide.CYTOSINE));
		yPos = halfLineHeight + halfFontHeight + lineHeight;
		g.drawString("C", leftXPos, yPos);
		g.drawString("C", rightXPos, yPos);
		g.setColor(ColorConverters.nucleotideToColor(Nucleotide.GUANINE));
		yPos = halfLineHeight + halfFontHeight + 2 * lineHeight;
		g.drawString("G", leftXPos, yPos);
		g.drawString("G", rightXPos, yPos);
		g.setColor(ColorConverters.nucleotideToColor(Nucleotide.THYMINE));
		yPos = halfLineHeight + halfFontHeight + 3 * lineHeight;
		g.drawString("T", leftXPos, yPos);
		g.drawString("T", rightXPos, yPos);
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
			if (i % 2 == 1) {
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


	/**
	 * Draws the dense representation of a specified SNP
	 * @param g {@link Graphics}
	 * @param currentSNP {@link SNP} to draw
	 */
	private void drawDenseSNP(Graphics g, SNP currentSNP) {
		// x position of the stripe
		int xPos = genomePosToScreenPos(currentSNP.getPosition());
		// width of the stripe
		int width = twoGenomePosToScreenWidth(currentSNP.getPosition(), currentSNP.getPosition() + 1);
		// height of the stripe
		int lineHeight = getHeight() / 4;
		// we search for the y position of the stripe for the first base
		int yPos = 0;
		switch (currentSNP.getFirstBase()) {
		case ADENINE:
			yPos = 0;
			break;
		case CYTOSINE:
			yPos = lineHeight;
			break;
		case GUANINE:
			yPos = 2 * lineHeight;
			break;
		case THYMINE:
			yPos = 3 * lineHeight;
			break;
		}
		// we set the color of the first base depending on if the second base is significant
		if (currentSNP.isSecondBaseSignificant()) {
			g.setColor(FIRST_BASE_COLOR);
		} else {
			g.setColor(FIRST_BASE_COLOR2);
		}
		// we draw the first base
		g.fillRect(xPos, yPos, width, lineHeight);
		// we draw the second base only if it's significant
		if (currentSNP.isSecondBaseSignificant()) {
			// we search for the second base y position
			yPos = 0;
			switch (currentSNP.getSecondBase()) {
			case ADENINE:
				yPos = 0;
				break;
			case CYTOSINE:
				yPos = lineHeight;
				break;
			case GUANINE:
				yPos = 2 * lineHeight;
				break;
			case THYMINE:
				yPos = 3 * lineHeight;
				break;
			}
			// we set the color and draw the second base
			g.setColor(SECOND_BASE_COLOR);
			g.fillRect(xPos, yPos, width, lineHeight);
		}
	}


	/**
	 * Draws the detailed representation of a specified SNP
	 * @param g {@link Graphics}
	 * @param currentSNP {@link SNP} to draw
	 */
	private void drawDetailedSNP(Graphics g, SNP currentSNP) {		
		// half height of the font
		int halfFontHeight = g.getFontMetrics().getHeight() / 2;
		// height of a line 
		int lineHeight = getHeight() / 4;		
		int halfLineHeight = getHeight() / 8;
		int xPos = genomePosToScreenPos(currentSNP.getPosition());
		int yPos = 0;
		// draw first base
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
		if (currentSNP.isSecondBaseSignificant()) {
			g.setColor(FIRST_BASE_COLOR);
		} else {
			g.setColor(FIRST_BASE_COLOR2);
		}
		g.drawString(countStr, xPos, yPos);

		// draw second base
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
			g.setColor(SECOND_BASE_COLOR);
		} else {
			g.setColor(SECOND_BASE_COLOR2);
		}
		g.drawString(countStr, xPos, yPos);
	}
}
