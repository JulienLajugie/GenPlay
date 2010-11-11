/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Graphics;
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

	private static final long serialVersionUID = -5740813392910733205L; // generated ID
	
	
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
		drawStripes(g);
		drawVerticalLines(g);
		drawBackground(g);
		drawSNP(g);
		drawNucleotide(g);
		drawName(g);
		drawMiddleVerticalLine(g);	
	}
	
	
	/**
	 * Draws the nucleotide name on the left of the track
	 * @param g {@link Graphics}
	 */
	private void drawNucleotide(Graphics g) {
		g.setColor(Color.BLACK);
		int halfFontHeight = g.getFontMetrics().getHeight() / 2;
		int lineHeight = getHeight() / 4;
		int halfLineHeight = lineHeight / 2;
		int yPos = halfLineHeight + halfFontHeight;
		g.drawString("A", 5, yPos);
		yPos = halfLineHeight + halfFontHeight + lineHeight;
		g.drawString("C", 5, yPos);
		yPos = halfLineHeight + halfFontHeight + 2 * lineHeight;
		g.drawString("G", 5, yPos);
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
		g.setColor(Color.LIGHT_GRAY);
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
		int height = getHeight();
		g.setColor(Color.RED);
		List<SNP> snpList = data.getFittedData(genomeWindow, xFactor);
		if ((snpList != null) && (snpList.size() > 0)) {
			// loop for each SNPs
			for (int i = 0; i < snpList.size(); i++) {
				int xPos = genomePosToScreenPos(snpList.get(i).getPosition());
				g.drawLine(xPos, 0, xPos, height);
			}	
		}
	}
}
