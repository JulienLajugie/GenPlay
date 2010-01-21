/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.List;

import yu.einstein.gdp2.core.Gene;
import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.enums.Strand;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.list.geneList.GeneListOperations;
import yu.einstein.gdp2.util.ZoomManager;


/**
 * A {@link TrackGraphics} part of a {@link GeneListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class GeneListTrackGraphics extends TrackGraphics {

	private static final long serialVersionUID = 1372400925707415741L; // generated ID
	private static final double	MIN_X_RATIO_PRINT_NAME = GeneList.MIN_X_RATIO_PRINT_NAME;
	private static final short	GENE_HEIGHT = 6;					// size of a gene in pixel
	private final GeneList 		geneList;							// list of gene to print
	private int 				firstLineToDisplay = 0;				// number of the first line to be displayed
	private int 				geneLinesCount = 0;					// number of line of genes
	private int 				mouseStartDragY = -1;				// position of the mouse when start dragging


	/**
	 * 
	 * @param zoomManager
	 * @param displayedGenomeWindow
	 * @param geneList
	 */
	protected GeneListTrackGraphics(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow, GeneList geneList) {
		super(zoomManager, displayedGenomeWindow);
		this.geneList = geneList;
		firstLineToDisplay = 0;
		geneList.setFontMetrics(fm);
		GeneListOperations.indexScores(geneList);
	}


	@Override
	protected void xFactorChanged() {
		firstLineToDisplay = 0;
		super.xFactorChanged();
	}


	@Override
	protected void chromosomeChanged() {
		firstLineToDisplay = 0;
		super.chromosomeChanged();
	}
	
	
	@Override
	protected void drawTrack(Graphics g) {
		drawStripes(g);
		drawVerticalLines(g);
		drawGenes(g);
		drawName(g);
		drawMiddleVerticalLine(g);
	}


	/**
	 * Draws the genes
	 * @param g {@link Graphics}
	 */
	private void drawGenes(Graphics g) {
		// we print the gene names if the x ratio > MIN_X_RATIO_PRINT_NAME 
		boolean isGeneNamePrinted = xFactor > MIN_X_RATIO_PRINT_NAME;
		// Retrieve the genes to print
		List<List<Gene>> genesToPrint = geneList.getFittedData(genomeWindow, xFactor);
		if ((genesToPrint != null) && (genesToPrint.size() > 0)){
			// Compute the maximum number of line displayable
			int displayedLineCount = 0;
			if (isGeneNamePrinted) {
				displayedLineCount = (getHeight() - 2 * GENE_HEIGHT) / (GENE_HEIGHT * 3) + 1;
			} else {				
				displayedLineCount = (getHeight() - GENE_HEIGHT) / (GENE_HEIGHT * 2) + 1;
			}			
			// calculate how many scroll on the Y axis are necessary to show all the genes
			geneLinesCount = genesToPrint.size() - displayedLineCount + 2;			
			// For each line of genes on the screen
			for (int i = 0; i < displayedLineCount; i++) {
				// Calculate the height of the gene
				int currentHeight;				
				if (isGeneNamePrinted) {
					currentHeight = i * (GENE_HEIGHT * 3) + 2 * GENE_HEIGHT;
				} else {
					currentHeight = i * (GENE_HEIGHT * 2) + GENE_HEIGHT;					
				}
				// Calculate which line has to be printed depending on the position of the scroll bar
				int currentLine = i + firstLineToDisplay;
				if (currentLine < genesToPrint.size()) {
					// For each gene of the current line
					for (Gene geneToPrint : genesToPrint.get(currentLine)) {
						// Choose the color depending on the strand
						if (geneToPrint.getStrand() == Strand.FIVE) {
							g.setColor(Color.RED);
						} else {
							g.setColor(Color.BLUE);
						}
						// Draw the gene
						int x1 = genomePosToScreenPos(geneToPrint.getTxStart());
						int x2 = genomePosToScreenPos(geneToPrint.getTxStop());
						g.drawLine(x1, currentHeight, x2, currentHeight);
						// Draw the name of the gene if the zoom is small enough
						if (isGeneNamePrinted) {
							String geneName = geneToPrint.getName();
							g.drawString(geneName, x1, currentHeight - 1);
						}
						// For each exon of the current gene
						if (geneToPrint.getExonStarts() != null) {
							for (int j = 0; j < geneToPrint.getExonStarts().length; j++) {
								int exonX = genomePosToScreenPos(geneToPrint.getExonStarts()[j]);
								int exonWidth = genomePosToScreenPos(geneToPrint.getExonStops()[j]) - exonX;
								if (exonWidth < 1){
									exonWidth = 1;
								}
								// if we have some exon score values
								if (geneToPrint.getExonScores() != null) {
									// if we have just one exon score
									if (geneToPrint.getExonScores().length == 1) {
										g.setColor(scoreToColor(geneToPrint.getExonScores()[0], 0, 1000));
									} else { // if we have values for each exon
										g.setColor(scoreToColor(geneToPrint.getExonScores()[j], 0, 1000));
									}
								}
								g.fillRect(exonX, currentHeight + 1, exonWidth, GENE_HEIGHT);
							}
						}
					}
				}
			}
		}
	}


	/**
	 * Changes the scroll position of the panel when the wheel of the mouse is used with the right button
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
			if (((e.getWheelRotation() < 0) && (e.getWheelRotation() + firstLineToDisplay >= 0)) 
					|| ((e.getWheelRotation() > 0) && (e.getWheelRotation() + firstLineToDisplay <= geneLinesCount))) {
				firstLineToDisplay += e.getWheelRotation();
				repaint();
			}		
		} else {
			super.mouseWheelMoved(e);
		}
	}


	/**
	 * Sets the variable mouseStartDragY when the user press the right button of the mouse
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
			mouseStartDragY = e.getY();
		}		
	}


	/**
	 * Changes the scroll position of the panel when mouse dragged with the right button
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
		// we print the gene names if the x ratio > MIN_X_RATIO_PRINT_NAME 
		boolean isGeneNamePrinted = xFactor > MIN_X_RATIO_PRINT_NAME;
		if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
			int distance = 0;
			if (isGeneNamePrinted) {
				distance = (mouseStartDragY - e.getY()) / (3 * GENE_HEIGHT);				
			} else {
				distance = (mouseStartDragY - e.getY()) / (2 * GENE_HEIGHT);
			}
			if (Math.abs(distance) > 0) {
				if (((distance < 0) && (distance + firstLineToDisplay >= 0)) 
						|| ((distance > 0) && (distance + firstLineToDisplay <= geneLinesCount))) {
					firstLineToDisplay += distance;
					mouseStartDragY = e.getY();
					repaint();
				}
			}
		}		
	}
}
