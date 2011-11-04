/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.track;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.yu.einstein.genplay.core.Gene;
import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.manager.ExceptionManager;
import edu.yu.einstein.genplay.core.manager.URRManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.util.ColorConverters;
import edu.yu.einstein.genplay.util.History;




/**
 * A {@link TrackGraphics} part of a {@link GeneListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class GeneListTrackGraphics extends TrackGraphics<GeneList> {

	private static final long serialVersionUID = 1372400925707415741L; 		// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private static final double				MIN_X_RATIO_PRINT_NAME = 
		GeneList.MIN_X_RATIO_PRINT_NAME;									// the name of the genes are printed if the ratio is higher than this value			
	private static final double 			SCORE_SATURATION = 0.01d;		// saturation of the score of the exon for the display
	private static final short				GENE_HEIGHT = 6;				// size of a gene in pixel
	private static final short				UTR_HEIGHT = 3;					// height of a UTR region of a gene in pixel
	protected static final DecimalFormat 	SCORE_FORMAT = 
		new DecimalFormat("#.###");											// decimal format for the score
	private int 							firstLineToDisplay = 0;			// number of the first line to be displayed
	private int 							geneLinesCount = 0;				// number of line of genes
	private int 							mouseStartDragY = -1;			// position of the mouse when start dragging
	private Gene 							geneUnderMouse = null;			// gene under the cursor of the mouse
	private double 							min;							// min score of a GeneList
	private double							max;							// max score of a GeneList
	protected History 						history = null; 				// history containing a description of the action made on the track
	protected URRManager<GeneList> 			urrManager; 					// manager that handles the undo / redo / reset of the track

	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeInt(geneLinesCount);
		out.writeDouble(min);
		out.writeDouble(max);
		out.writeObject(history);
		out.writeObject(urrManager);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		mouseStartDragY = -1;
		geneUnderMouse = null;
		firstLineToDisplay = 0;
		in.readInt();
		geneLinesCount = in.readInt();
		min = in.readDouble();
		max = in.readDouble();
		history = (History) in.readObject();
		urrManager = (URRManager<GeneList>) in.readObject();
	}
	
	
	/**
	 * Creates an instance of {@link GeneListTrackGraphics}
	 * @param displayedGenomeWindow a {@link GenomeWindow} to display
	 * @param data a list of genes
	 */
	protected GeneListTrackGraphics(GenomeWindow displayedGenomeWindow, GeneList data) {
		super(displayedGenomeWindow, data);
		try {
			setSaturatedMinMax();
		} catch (Exception e) {
			e.printStackTrace();
		}
		firstLineToDisplay = 0;
		this.data.setFontMetrics(fm);
		this.history = new History();
		urrManager = new URRManager<GeneList>(ProjectManager.getInstance().getProjectConfiguration().getUndoCount(), data);
	}


	@Override
	protected void chromosomeChanged() {
		firstLineToDisplay = 0;
		super.chromosomeChanged();
	}


	/**
	 * Draws the genes
	 * @param g {@link Graphics}
	 */
	private void drawGenes(Graphics g) {
		// we print the gene names if the x ratio > MIN_X_RATIO_PRINT_NAME 
		boolean isGeneNamePrinted = xFactor > MIN_X_RATIO_PRINT_NAME;
		// Retrieve the genes to print
		List<List<Gene>> genesToPrint = data.getFittedData(genomeWindow, xFactor);
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
						// retrieve the screen x coordinate of the start and stop position
						int x1 = genomePosToScreenPos(geneToPrint.getStart());
						int x2 = genomePosToScreenPos(geneToPrint.getStop());
						// Choose the color depending on if the gene is under the mouse and on the strand
						boolean isHighlighted = ((geneUnderMouse != null) && (geneToPrint.equals(geneUnderMouse)));
						g.setColor(ColorConverters.geneToColor(geneToPrint.getStrand(), isHighlighted));
						// Draw the gene
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
								if (exonWidth < 1) {
									exonWidth = 1;
								}
								// if we have some exon score values
								if (geneToPrint.getExonScores() != null) {
									// if we have just one exon score
									if (geneToPrint.getExonScores().length == 1) {
										g.setColor(ColorConverters.scoreToColor(geneToPrint.getExonScores()[0], min, max));
									} else { // if we have values for each exon
										g.setColor(ColorConverters.scoreToColor(geneToPrint.getExonScores()[j], min, max));
									}
								}
								// case where the exon is not at all in a UTR (untranslated region) 
								if ((geneToPrint.getExonStarts()[j] >= geneToPrint.getUTR5Bound()) && (geneToPrint.getExonStops()[j] <= geneToPrint.getUTR3Bound())) {
									g.fillRect(exonX, currentHeight + 1, exonWidth, GENE_HEIGHT);
								} else {
									// case where the whole exon is in a UTR
									if ((geneToPrint.getExonStops()[j] <= geneToPrint.getUTR5Bound()) || (geneToPrint.getExonStarts()[j] >= geneToPrint.getUTR3Bound())) {
										g.fillRect(exonX, currentHeight + 1, exonWidth, UTR_HEIGHT);										
									} else {
										// case where the exon is in both UTR
										if ((geneToPrint.getExonStarts()[j] <= geneToPrint.getUTR5Bound()) && (geneToPrint.getExonStops()[j] >= geneToPrint.getUTR3Bound())) {
											int UTR5Width = genomePosToScreenPos(geneToPrint.getUTR5Bound()) - exonX;
											int TRWidth = genomePosToScreenPos(geneToPrint.getUTR3Bound()) - exonX - UTR5Width;
											int UTR3Width = exonWidth - UTR5Width - TRWidth; 
											g.fillRect(exonX, currentHeight + 1, UTR5Width, UTR_HEIGHT);
											g.fillRect(exonX + UTR5Width, currentHeight + 1, TRWidth, GENE_HEIGHT);
											g.fillRect(exonX + UTR5Width + TRWidth, currentHeight + 1, UTR3Width, UTR_HEIGHT);

										} else {								
											// case where part of the exon is in the UTR and part is not
											if ((geneToPrint.getExonStarts()[j] <= geneToPrint.getUTR5Bound()) && (geneToPrint.getExonStops()[j] >= geneToPrint.getUTR5Bound())) {
												// case where part is in the 5'UTR
												int UTRWidth = genomePosToScreenPos(geneToPrint.getUTR5Bound()) - exonX;
												g.fillRect(exonX, currentHeight + 1, UTRWidth, UTR_HEIGHT);
												g.fillRect(exonX + UTRWidth, currentHeight + 1, exonWidth - UTRWidth, GENE_HEIGHT);											
											} else if ((geneToPrint.getExonStarts()[j] <= geneToPrint.getUTR3Bound()) && (geneToPrint.getExonStops()[j] >= geneToPrint.getUTR3Bound())) {
												// case where part is in the 3' UTR 
												int TRWidth = genomePosToScreenPos(geneToPrint.getUTR3Bound()) - exonX; // TRWidth is the with of the TRANSLATED region
												g.fillRect(exonX, currentHeight + 1, TRWidth, GENE_HEIGHT);
												g.fillRect(exonX + TRWidth, currentHeight + 1, exonWidth - TRWidth, UTR_HEIGHT);											
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}


	@Override
	protected void drawTrack(Graphics g) {
		drawVerticalLines(g);
		drawGenes(g);
		drawStripes(g);
		drawMultiGenomeInformation(g);
		drawHeaderTrack(g);
		drawMiddleVerticalLine(g);
	}


	/**
	 * @return the history of the current track.
	 */
	protected History getHistory() {
		return history;
	}


	/**
	 * @return true if the action redo is possible
	 */
	protected boolean isRedoable() {
		return urrManager.isRedoable();
	}


	/**
	 * @return true if the track can be reseted
	 */
	protected boolean isResetable() {
		return urrManager.isResetable();
	}


	/**
	 * @return true if the action undo is possible
	 */
	protected boolean isUndoable() {
		return urrManager.isUndoable();
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		// if a gene is double clicked
		if ((e.getClickCount() == 2) && (geneUnderMouse != null)) {
			// if the desktop is supported
			if ((data.getSearchURL() != null) && (Desktop.isDesktopSupported())) {
				try {
					// we open a browser showing information on the gene
					Desktop.getDesktop().browse(new URI(data.getSearchURL() + geneUnderMouse.getName()));
				} catch (Exception e1) {
					ExceptionManager.handleException(getRootPane(), e1, "Error while opening the web browser");
				}
			}
		} else { // else default action
			super.mouseClicked(e);
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


	/**
	 * Retrieves the gene under the cursor of the mouse if there is one
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		if (!getScrollMode()) {
			Gene oldGeneUnderMouse = geneUnderMouse;
			geneUnderMouse = null;
			// retrieve the position of the mouse
			Point mousePosition = e.getPoint();
			// check if the name of genes is printed
			boolean isGeneNamePrinted = xFactor > MIN_X_RATIO_PRINT_NAME;
			// retrieve the list of the printed genes
			List<List<Gene>> printedGenes = data.getFittedData(genomeWindow, xFactor);
			// do nothing if there is no genes
			if (printedGenes == null) {
				return;
			}
			// look for how many lines of genes are printed
			int displayedLineCount = 0;
			if (isGeneNamePrinted) {
				displayedLineCount = (getHeight() - 2 * GENE_HEIGHT) / (GENE_HEIGHT * 3) + 1;
			} else {				
				displayedLineCount = (getHeight() - GENE_HEIGHT) / (GENE_HEIGHT * 2) + 1;
			}	

			// search if the mouse is on a line where there is genes printed on the track
			int mouseLine = -1;
			int i = 0;
			while ((mouseLine == -1) &&  (i < displayedLineCount)) {
				if (isGeneNamePrinted) {
					if ((mousePosition.y >= i * GENE_HEIGHT * 3 + GENE_HEIGHT) &&
							(mousePosition.y <= i * GENE_HEIGHT * 3 + 3 * GENE_HEIGHT)) {
						mouseLine = i;
					}
				} else {
					if ((mousePosition.y >= i * GENE_HEIGHT * 2 + GENE_HEIGHT) &&
							(mousePosition.y <= i * GENE_HEIGHT * 2 + 2 * GENE_HEIGHT)) {
						mouseLine = i;
					}				
				}
				i++;
			}
			// if we found something
			if (mouseLine != -1) {
				// line of genes where the mouse is
				mouseLine += firstLineToDisplay;
				if (mouseLine < printedGenes.size()) {
					// search if the x position of the mouse is on a gene too
					int j = 0;
					while ((j < printedGenes.get(mouseLine).size()) && (geneUnderMouse == null)) {
						Gene currentGene = printedGenes.get(mouseLine).get(j);
						if (mousePosition.x >= genomePosToScreenPos(currentGene.getStart()) &&
								(mousePosition.x <= genomePosToScreenPos(currentGene.getStop()))) {
							// we found a gene under the mouse
							geneUnderMouse = currentGene;
						}
						j++;
					}
				}
			}
			// unset the tool text and the mouse cursor if there is no gene under the mouse
			if (geneUnderMouse == null) {
				setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				setToolTipText(null);
			} else {
				// if there is a gene under the mouse we also check 
				// if there is an exon with a score under the mouse cursor
				Double scoreUnderMouse = null;
				if ((geneUnderMouse.getExonScores() != null) && (geneUnderMouse.getExonScores().length > 0)) { 
					for (int k = 0; (k < geneUnderMouse.getExonStarts().length) && (scoreUnderMouse == null); k++) {
						if (mousePosition.x >= genomePosToScreenPos(geneUnderMouse.getExonStarts()[k]) &&
								(mousePosition.x <= genomePosToScreenPos(geneUnderMouse.getExonStops()[k]))) {
							if (geneUnderMouse.getExonScores().length == 1) {	
								scoreUnderMouse = geneUnderMouse.getExonScores()[0];
							} else {
								scoreUnderMouse = geneUnderMouse.getExonScores()[k];
							}
						}
					}
				}
				// set the cursor and the tooltip text if there is a gene under the mouse cursor
				setCursor(new Cursor(Cursor.HAND_CURSOR));
				if (scoreUnderMouse == null) {
					// if there is a gene but no exon score
					setToolTipText(geneUnderMouse.getName());
				} else {
					// if there is a gene and an exon score
					setToolTipText(geneUnderMouse.getName() + ": " +  SCORE_FORMAT.format(scoreUnderMouse));
				}
			}
			// we repaint the track only if the gene under the mouse changed
			if (((oldGeneUnderMouse == null) && (geneUnderMouse != null)) 
					|| ((oldGeneUnderMouse != null) && (!oldGeneUnderMouse.equals(geneUnderMouse)))) {
				repaint();
			}
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
	 * Redoes last action
	 */
	protected void redoData() {
		try {
			if (isRedoable()) {
				data = urrManager.redo();
				repaint();
				history.redo();
			}
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while redoing");
			history.setLastAsError();
		}
	}
	

	/**
	 * Resets the data 
	 * Copies the value of the original data into the current value
	 */
	protected void resetData() {
		try {
			if (isResetable()) {
				data = urrManager.reset();
				repaint();
				history.reset();
			}
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while reseting");
			history.setLastAsError();
		}
	}
	
	
	/**
	 * Sets the data showed in the track
	 * @param data the data showed in the track
	 * @param description description of the data
	 */
	protected void setData(GeneList data, String description) {
		if (data != null) {
			try {
				history.add(description);
				urrManager.set(data);
				this.data = data;
				repaint();
			} catch (Exception e) {
				ExceptionManager.handleException(getRootPane(), e, "Error while updating the track");
				history.setLastAsError();
			}
		}
	}

	
	/**
	 *  Computes the minimum and maximum saturated values of the exon scores
	 */
	private void setSaturatedMinMax() {
		// put the scores of every exon in a big list
		List<Double> scoreList = new ArrayList<Double>();
		for (List<Gene> currentList: data) {
			if ((currentList != null) && (!currentList.isEmpty())) {
				for (Gene currentGene: currentList) {
					if (currentGene.getExonScores() != null) {
						for (double currentScore: currentGene.getExonScores()) {
							if (currentScore != 0) {
								scoreList.add(currentScore);
							}
						}

					}
				}
			}
		}
		if (!scoreList.isEmpty()) {
			// sort the list
			Collections.sort(scoreList);

			int minIndex = (int)(SCORE_SATURATION * scoreList.size());
			int maxIndex = scoreList.size() - (int)(SCORE_SATURATION * scoreList.size());

			min = scoreList.get(minIndex - 1);
			max = scoreList.get(maxIndex - 1);
		}
	}

	
	/**
	 * Changes the undo count of the track
	 * @param undoCount
	 */
	protected void setUndoCount(int undoCount) {
		urrManager.setLength(undoCount);		
	}
	
	
	/**
	 * Undoes last action
	 */
	protected void undoData() {
		try {
			if (isUndoable()) {
				data = urrManager.undo();
				repaint();
				history.undo();
			}
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while undoing");
			history.setLastAsError();
		}
	}
	
	
	@Override
	protected void xFactorChanged() {
		firstLineToDisplay = 0;
		super.xFactorChanged();
	}
}
