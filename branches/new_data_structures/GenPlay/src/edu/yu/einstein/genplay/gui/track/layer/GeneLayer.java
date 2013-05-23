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
package edu.yu.einstein.genplay.gui.track.layer;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.dataScalerForTrackDisplay.GeneListScaler;
import edu.yu.einstein.genplay.gui.track.ScrollingManager;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.NumberFormats;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Layer displaying a {@link GeneList}
 * @author Julien Lajugie
 */
public class GeneLayer extends AbstractVersionedLayer<GeneList> implements Layer<GeneList>, VersionedLayer<GeneList>, ScoredLayer, MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long serialVersionUID = 3779631846077486596L; 		// generated ID
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;				// Saved format version
	private static final float 				SCORE_SATURATION = 0.02f;		// saturation of the score of the exon for the display
	private static final short				GENE_HEIGHT = 6;				// size of a gene in pixel
	private static final short				UTR_HEIGHT = 3;					// height of a UTR region of a gene in pixel
	private transient GeneListScaler		dataScaler;						// object that scales the list of genes for display
	private int 							firstLineToDisplay;				// number of the first line to be displayed
	private int 							geneLinesCount;					// number of line of genes
	private int 							mouseStartDragY = -1;			// position of the mouse when start dragging
	private Gene 							geneUnderMouse = null;			// gene under the cursor of the mouse
	private float 							min;							// minimum score of the GeneList to display
	private float							max;							// maximum score of the GeneList to display


	/**
	 * Creates an instance of {@link GeneLayer} with the same properties as the specified {@link GeneLayer}.
	 * The copy of the data is shallow.
	 * @param binLayer
	 */
	private GeneLayer(GeneLayer geneLayer) {
		super(geneLayer);
		firstLineToDisplay = geneLayer.firstLineToDisplay;
		geneLinesCount = geneLayer.geneLinesCount;
		mouseStartDragY = -1;
		geneUnderMouse = null;
		min = geneLayer.min;
		max = geneLayer.max;
	}


	/**
	 * Creates an instance of a {@link GeneLayer}
	 * @param track track containing the layer
	 * @param data data of the layer
	 * @param name name of the layer
	 */
	public GeneLayer(Track track, GeneList data, String name) {
		super(track, data, name);
		firstLineToDisplay = 0;
		geneLinesCount = 0;
		mouseStartDragY = -1;
		geneUnderMouse = null;
		setSaturatedMinMax();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public GeneLayer clone() {
		return new GeneLayer(this);
	}


	/**
	 * Draws the genes
	 * @param g {@link Graphics}
	 */
	@Override
	public void draw(Graphics g, int width, int height) {
		if (isVisible()) {
			// we retrieve the project window
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			// we retrieve the minimum and maximum scores displayed in the track
			float max = getTrack().getScore().getMaximumScore();
			float min = getTrack().getScore().getMinimumScore();
			// we print the gene names if the x ratio > MIN_X_RATIO_PRINT_NAME
			boolean isGeneNamePrinted = projectWindow.getXRatio() > GeneListScaler.MIN_X_RATIO_PRINT_NAME;
			// check that the data scaler is valid
			validateDataScaler();
			// Retrieve the genes to print
			List<ListView<Gene>> genesToPrint = dataScaler.getDataScaledForTrackDisplay();
			if ((genesToPrint != null) && (genesToPrint.size() > 0)){
				// Compute the maximum number of line displayable
				int displayedLineCount = 0;
				if (isGeneNamePrinted) {
					displayedLineCount = ((height - (2 * GENE_HEIGHT)) / (GENE_HEIGHT * 3)) + 1;
				} else {
					displayedLineCount = ((height - GENE_HEIGHT) / (GENE_HEIGHT * 2)) + 1;
				}
				// calculate how many scroll on the Y axis are necessary to show all the genes
				geneLinesCount = (genesToPrint.size() - displayedLineCount) + 2;
				// For each line of genes on the screen
				for (int i = 0; i < displayedLineCount; i++) {
					// Calculate the height of the gene
					int currentHeight;
					if (isGeneNamePrinted) {
						currentHeight = (i * (GENE_HEIGHT * 3)) + (2 * GENE_HEIGHT);
					} else {
						currentHeight = (i * (GENE_HEIGHT * 2)) + GENE_HEIGHT;
					}
					// Calculate which line has to be printed depending on the position of the scroll bar
					int currentLine = i + firstLineToDisplay;
					if (currentLine < genesToPrint.size()) {
						// For each gene of the current line
						if (genesToPrint.get(currentLine) != null) {
							for (Gene geneToPrint : genesToPrint.get(currentLine)) {
								// retrieve the screen x coordinate of the start and stop position
								int x1 = projectWindow.genomeToScreenPosition(geneToPrint.getStart());
								int x2 = projectWindow.genomeToScreenPosition(geneToPrint.getStop());
								if (x2 != 0) {
									// Choose the color depending on if the gene is under the mouse and on the strand
									boolean isHighlighted = ((geneUnderMouse != null) && (geneToPrint.equals(geneUnderMouse)));
									g.setColor(Colors.geneToColor(geneToPrint.getStrand(), isHighlighted));
									// Draw the gene
									g.drawLine(x1, currentHeight, x2, currentHeight);
									// Draw the name of the gene if the zoom is small enough
									if (isGeneNamePrinted) {
										String geneName = geneToPrint.getName();
										if (geneToPrint.getStart() < projectWindow.getGenomeWindow().getStart()) {
											int newX = (int)Math.round((geneToPrint.getStart() - projectWindow.getGenomeWindow().getStart()) * projectWindow.getXRatio());	// former method
											g.drawString(geneName, newX, currentHeight - 1);
										} else {
											g.drawString(geneName, x1, currentHeight - 1);
										}
									}
									// For each exon of the current gene
									if (geneToPrint.getExons() != null) {
										for (int j = 0; j < geneToPrint.getExons().size(); j++) {
											ScoredChromosomeWindow currentExon = geneToPrint.getExons().get(j);
											int exonX = projectWindow.genomeToScreenPosition(currentExon.getStart());
											if (currentExon.getStop() >= projectWindow.getGenomeWindow().getStart()) {
												int exonWidth = projectWindow.genomeToScreenPosition(currentExon.getStop()) - exonX;
												if (exonWidth < 1) {
													exonWidth = 1;
												}
												// if we have some exon score values
												if (!Float.isNaN(currentExon.getScore())) {
													g.setColor(Colors.scoreToColor(currentExon.getScore(), min, max));
												} else {
													g.setColor(Colors.geneToColor(geneToPrint.getStrand(), isHighlighted));
												}
												// case where the exon is not at all in a UTR (untranslated region)
												if ((currentExon.getStart() >= geneToPrint.getUTR5Bound()) && (currentExon.getStop() <= geneToPrint.getUTR3Bound())) {
													g.fillRect(exonX, currentHeight + 1, exonWidth, GENE_HEIGHT);
												} else {
													// case where the whole exon is in a UTR
													if ((currentExon.getStop() <= geneToPrint.getUTR5Bound()) || (currentExon.getStart() >= geneToPrint.getUTR3Bound())) {
														g.fillRect(exonX, currentHeight + 1, exonWidth, UTR_HEIGHT);
													} else {
														// case where the exon is in both UTR
														if ((currentExon.getStart() <= geneToPrint.getUTR5Bound()) && (currentExon.getStop() >= geneToPrint.getUTR3Bound())) {
															int UTR5Width = projectWindow.genomeToScreenPosition(geneToPrint.getUTR5Bound()) - exonX;
															int TRWidth = projectWindow.genomeToScreenPosition(geneToPrint.getUTR3Bound()) - exonX - UTR5Width;
															int UTR3Width = exonWidth - UTR5Width - TRWidth;
															g.fillRect(exonX, currentHeight + 1, UTR5Width, UTR_HEIGHT);
															g.fillRect(exonX + UTR5Width, currentHeight + 1, TRWidth, GENE_HEIGHT);
															g.fillRect(exonX + UTR5Width + TRWidth, currentHeight + 1, UTR3Width, UTR_HEIGHT);

														} else {
															// case where part of the exon is in the UTR and part is not
															if ((currentExon.getStart() <= geneToPrint.getUTR5Bound()) && (currentExon.getStop() >= geneToPrint.getUTR5Bound())) {
																// case where part is in the 5'UTR
																int UTRWidth = projectWindow.genomeToScreenPosition(geneToPrint.getUTR5Bound()) - exonX;
																g.fillRect(exonX, currentHeight + 1, UTRWidth, UTR_HEIGHT);
																g.fillRect(exonX + UTRWidth, currentHeight + 1, exonWidth - UTRWidth, GENE_HEIGHT);
															} else if ((currentExon.getStart() <= geneToPrint.getUTR3Bound()) && (currentExon.getStop() >= geneToPrint.getUTR3Bound())) {
																// case where part is in the 3' UTR
																int TRWidth = projectWindow.genomeToScreenPosition(geneToPrint.getUTR3Bound()) - exonX; // TRWidth is the with of the TRANSLATED region
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
				}
			}
		}
	}


	@Override
	public float getCurrentScoreToDisplay() {
		// we return null because they could be more than one score to display at a position
		return Float.NaN;
	}


	@Override
	public float getMaximumScoreToDisplay() {
		return max;
	}


	@Override
	public float getMinimumScoreToDisplay() {
		return min;
	}


	@Override
	public LayerType getType() {
		return LayerType.GENE_LAYER;
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		if (isVisible()) {
			// if a gene is double clicked
			if ((e.getClickCount() == 2) && (geneUnderMouse != null)) {
				// if the desktop is supported
				if ((getData().getGeneDBURL() != null) && (Desktop.isDesktopSupported())) {
					try {
						// we open a browser showing information on the gene
						Desktop.getDesktop().browse(new URI(getData().getGeneDBURL() + geneUnderMouse.getName()));
					} catch (Exception e1) {
						ExceptionManager.getInstance().caughtException(Thread.currentThread(), e1, "Error while opening the web browser");
					}
				}
			}
		}
	}


	/**
	 * Changes the scroll position of the panel when mouse dragged with the right button
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (isVisible()) {
			// we retrieve the project window
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			// we print the gene names if the x ratio > MIN_X_RATIO_PRINT_NAME
			boolean isGeneNamePrinted = projectWindow.getXRatio() > GeneListScaler.MIN_X_RATIO_PRINT_NAME;
			if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
				int distance = 0;
				if (isGeneNamePrinted) {
					distance = (mouseStartDragY - e.getY()) / (3 * GENE_HEIGHT);
				} else {
					distance = (mouseStartDragY - e.getY()) / (2 * GENE_HEIGHT);
				}
				if (Math.abs(distance) > 0) {
					if (((distance < 0) && ((distance + firstLineToDisplay) >= 0))
							|| ((distance > 0) && ((distance + firstLineToDisplay) <= geneLinesCount))) {
						firstLineToDisplay += distance;
						mouseStartDragY = e.getY();
						getTrack().repaint();
					}
				}
			}
		}
	}


	@Override
	public void mouseEntered(MouseEvent e) {}


	@Override
	public void mouseExited(MouseEvent e) {}


	/**
	 * Retrieves the gene under the cursor of the mouse if there is one
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		if (isVisible()) {
			if (!ScrollingManager.getInstance().isScrollingEnabled()) {
				// we retrieve the project window
				ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
				Gene oldGeneUnderMouse = geneUnderMouse;
				geneUnderMouse = null;
				// retrieve the position of the mouse
				Point mousePosition = e.getPoint();
				// check if the name of genes is printed
				boolean isGeneNamePrinted = projectWindow.getXRatio() > GeneListScaler.MIN_X_RATIO_PRINT_NAME;
				// check that the data scaler is valid
				validateDataScaler();
				// retrieve the list of the printed genes
				List<ListView<Gene>> printedGenes = dataScaler.getDataScaledForTrackDisplay();
				// do nothing if there is no genes
				if (printedGenes == null) {
					return;
				}
				// look for how many lines of genes are printed
				int displayedLineCount = printedGenes.size();

				// search if the mouse is on a line where there is genes printed on the track
				int mouseLine = -1;
				int i = 0;
				while ((mouseLine == -1) &&  (i < displayedLineCount)) {
					if (isGeneNamePrinted) {
						if ((mousePosition.y >= ((i * GENE_HEIGHT * 3) + GENE_HEIGHT)) &&
								(mousePosition.y <= ((i * GENE_HEIGHT * 3) + (3 * GENE_HEIGHT)))) {
							mouseLine = i;
						}
					} else {
						if ((mousePosition.y >= ((i * GENE_HEIGHT * 2) + GENE_HEIGHT)) &&
								(mousePosition.y <= ((i * GENE_HEIGHT * 2) + (2 * GENE_HEIGHT)))) {
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
							if ((mousePosition.x >= projectWindow.genomeToScreenPosition(currentGene.getStart())) &&
									(mousePosition.x <= projectWindow.genomeToScreenPosition(currentGene.getStop()))) {
								// we found a gene under the mouse
								geneUnderMouse = currentGene;
							}
							j++;
						}
					}
				}
				// unset the tool text and the mouse cursor if there is no gene under the mouse
				if (geneUnderMouse == null) {
					getTrack().getGraphicsPanel().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
					getTrack().getGraphicsPanel().setToolTipText(null);
				} else {
					// if there is a gene under the mouse we also check
					// if there is an exon with a score under the mouse cursor
					float scoreExonUnderMouse = Float.NaN;
					if ((geneUnderMouse.getExons() != null) && (geneUnderMouse.getExons().size() > 0)) {
						for (int k = 0; (k < geneUnderMouse.getExons().size()) && (Float.isNaN(scoreExonUnderMouse)); k++) {
							ScoredChromosomeWindow currentExon = geneUnderMouse.getExons().get(k);
							if ((mousePosition.x >= projectWindow.genomeToScreenPosition(currentExon.getStart())) &&
									(mousePosition.x <= projectWindow.genomeToScreenPosition(currentExon.getStop()))) {
								scoreExonUnderMouse = currentExon.getScore();
							}
						}
					}
					// set the cursor and the tooltip text if there is a gene under the mouse cursor
					getTrack().getGraphicsPanel().setCursor(new Cursor(Cursor.HAND_CURSOR));
					String toolTipText = "<html><b>" + geneUnderMouse.getName() + "</b><br>";
					GeneList geneList = getData();
					if (geneList.getGeneScoreType() != null) {
						toolTipText += "Score Type: <i>" + geneList.getGeneScoreType() + "</i><br>";
					}
					if (!Float.isNaN(geneUnderMouse.getScore())) {
						toolTipText += "Gene Score = <i>" + NumberFormats.getScoreFormat().format(geneUnderMouse.getScore()) + "</i><br>";
					}
					if (!Float.isNaN(scoreExonUnderMouse)) {
						toolTipText += "Exon Score = <i>" + NumberFormats.getScoreFormat().format(scoreExonUnderMouse) + "</i><br>";
					}
					toolTipText += "</html>";
					getTrack().getGraphicsPanel().setToolTipText(toolTipText);
				}
				// we repaint the track only if the gene under the mouse changed
				if (((oldGeneUnderMouse == null) && (geneUnderMouse != null))
						|| ((oldGeneUnderMouse != null) && (!oldGeneUnderMouse.equals(geneUnderMouse)))) {
					getTrack().repaint();
				}
			}
		}
	}


	/**
	 * Sets the variable mouseStartDragY when the user press the right button of the mouse
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (isVisible()) {
			if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
				mouseStartDragY = e.getY();
			}
		}
	}



	@Override
	public void mouseReleased(MouseEvent e) {}


	/**
	 * Changes the scroll position of the panel when the wheel of the mouse is used with the right button
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (isVisible()) {
			if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
				if (((e.getWheelRotation() < 0) && ((e.getWheelRotation() + firstLineToDisplay) >= 0))
						|| ((e.getWheelRotation() > 0) && ((e.getWheelRotation() + firstLineToDisplay) <= geneLinesCount))) {
					firstLineToDisplay += e.getWheelRotation();
					getTrack().repaint();
				}
			}
		}
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		min = in.readFloat();
		max = in.readFloat();
		firstLineToDisplay = 0;
		geneLinesCount = 0;
		mouseStartDragY = -1;
		geneUnderMouse = null;
	}


	@Override
	public void setData(GeneList data) {
		super.setData(data);
		// tells the track score object to auto-rescale the score axis
		if ((getTrack() != null) && (getTrack().getScore() != null)) {
			getTrack().getScore().autorescaleScoreAxis();
		}
	}


	@Override
	public void setData(GeneList data, String description) {
		super.setData(data, description);
		// tells the track score object to auto-rescale the score axis
		if ((getTrack() != null) && (getTrack().getScore() != null)) {
			getTrack().getScore().autorescaleScoreAxis();
		}
	}


	/**
	 *  Computes the minimum and maximum saturated values of the exon scores
	 */
	private void setSaturatedMinMax() {
		// put the scores of every exon in a big list
		List<Float> scoreList = new ArrayList<Float>();
		for (ListView<Gene> currentList: getData()) {
			if ((currentList != null) && (!currentList.isEmpty())) {
				for (Gene currentGene: currentList) {
					if (currentGene.getExons() != null) {
						for (ScoredChromosomeWindow currentExon: currentGene.getExons()) {
							if ((!Float.isNaN(currentExon.getScore())) && (currentExon.getScore() != 0)) {
								scoreList.add(currentExon.getScore());
							}
						}
					}
				}
			}
		}
		if (!scoreList.isEmpty()) {
			// sort the list
			Collections.sort(scoreList);
			int minIndex = (int)(SCORE_SATURATION * (scoreList.size() - 1));
			int maxIndex = (scoreList.size() - 1) - (int)((SCORE_SATURATION * scoreList.size()) - 1);
			min = scoreList.get(minIndex);
			max = scoreList.get(maxIndex);
		}
	}


	/**
	 * Checks that the data scaler is valid. Regenerates the data scaler if it's not valid
	 */
	private void validateDataScaler() {
		// if the data scaler is null or is not set to scale the current data we regenerate it
		if ((dataScaler == null) || (getData() != dataScaler.getDataToScale())) {
			FontMetrics fontMetrics = getTrack().getGraphicsPanel().getGraphics().getFontMetrics();
			dataScaler = new GeneListScaler(getData(), fontMetrics);
		}
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeFloat(min);
		out.writeFloat(max);
	}
}
