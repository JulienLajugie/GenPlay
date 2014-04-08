/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.track.layer.variantLayer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.core.multiGenome.data.display.VariantDisplayList;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.MixVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.ReferenceVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.VariantDisplay;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.VariantType;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.VariantLayerDisplaySettings;
import edu.yu.einstein.genplay.util.colors.Colors;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
class MultiGenomeVariantDrawer implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = -6140085563221274861L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;				// saved format version

	private ProjectWindow		projectWindow;					// instance of the genome window manager
	private MultiGenomeDrawer 	drawer;
	private AlleleType			currentDrawingAllele;
	private int					variantOpacity;					// Transparency of the stripes


	/**
	 * Constructor of {@link MultiGenomeVariantDrawer}
	 * @param drawer
	 */
	protected MultiGenomeVariantDrawer (MultiGenomeDrawer drawer) {
		projectWindow = ProjectManager.getInstance().getProjectWindow();
		setDrawer(drawer);
		variantOpacity = 0;
	}


	/**
	 * Draws the edge of a deletion stripe.
	 * @param g			graphics object
	 * @param x			x coordinate
	 * @param y			y coordinate
	 * @param width		width of the stripe
	 * @param height	height of the stripe
	 */
	private void drawDeletion (Graphics g, int x, int y, int width, int height) {
		if (MGDisplaySettings.DRAW_DELETION_EDGE == MGDisplaySettings.YES_MG_OPTION) {	// checks if the option is activated
			float dash1[] = {5.0f};						// length of the lines
			BasicStroke line = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dash1, 0.0f); // creates a stroke line
			Graphics2D g2d = (Graphics2D) g.create(); 	// create a temporary graphic
			g2d.setStroke(line);						// give the stroke line to the graphic
			g2d.setColor(Colors.BLACK);					// color of the edge (black)
			g2d.drawRect(x, y, width - 1, height);		// draw the edge all around the stripe
		}
	}


	/**
	 * Draws the list of variation for a genome
	 * @param g				Graphics object
	 * @param width			width of the graphics
	 * @param height		height of the graphics
	 * @param genomeWindow  genome window currently displayed
	 * @param variantList	list of variants
	 */
	protected void drawGenome (Graphics g, int width, int height, GenomeWindow genomeWindow, List<VariantDisplay> variants) {
		if ((variants != null) && (variants.size() > 0)) {
			Color mixColor = new Color(Colors.BLUE.getRed(), Colors.BLUE.getGreen(), Colors.BLUE.getBlue());			// color for mixed variant
			for (int i = 0; i < variants.size(); i++) {
				VariantDisplay variant = variants.get(i);
				VariantType type = variant.getVariant().getType();			// gets its type
				Color color;
				if (type == VariantType.REFERENCE_INSERTION) {
					color = MGDisplaySettings.REFERENCE_INSERTION_COLOR;
				} else if (type == VariantType.REFERENCE_DELETION) {
					color = MGDisplaySettings.REFERENCE_DELETION_COLOR;
				} else if (type == VariantType.REFERENCE_SNP) {
					color = MGDisplaySettings.REFERENCE_SNP_COLOR;
				} else if (type == VariantType.NO_CALL) {
					color = MGDisplaySettings.NO_CALL_COLOR;
				} else if (type == VariantType.MIX) {
					color = mixColor;
				} else  {
					color = getVariantColor(variant.getGenomeName(), type);																		// in order to get which color has been defined
				}

				drawVariant(g, height, variant, color, genomeWindow);	// draw the variant
			}
		}
	}


	/**
	 * Draws the edge of an insertion stripe.
	 * @param g			graphics object
	 * @param x			x coordinate
	 * @param y			y coordinate
	 * @param width		width of the stripe
	 * @param height	height of the stripe
	 */
	private void drawInsertion (Graphics g, int x, int y, int width, int height) {
		if (MGDisplaySettings.DRAW_INSERTION_EDGE == MGDisplaySettings.YES_MG_OPTION) {	// checks if the option is activated
			Graphics gTmp = g.create();				// creates a temporary graphics
			gTmp.setColor(Colors.BLACK);			// color of the edge (black)
			gTmp.drawRect(x, y, width, height);		// the edge here is a simple line all around the stripe
		}
	}


	/**
	 * Draws the letters (nucleotides) over the stripe.
	 * @param g					graphics object
	 * @param graphicsHeigth	height of the graphics object
	 * @param x					x coordinate
	 * @param width				width of the stripe
	 * @param height			height of the stripe
	 * @param variantDisplay	variant
	 * @param nucleotideNumber	number of nucleotide to display
	 */
	private void drawLetters (Graphics g, int graphicsHeigth, int x, int width, int height, VariantDisplay variantDisplay, int nucleotideNumber) {
		Variant variant = variantDisplay.getVariant();
		VariantType variantType = variant.getType();		// gets the variant type
		if (	((variantType == VariantType.INSERTION) && 	(MGDisplaySettings.DRAW_INSERTION_LETTERS 	== MGDisplaySettings.YES_MG_OPTION)) ||	// checks all options in order to determine if the letters must be drawn
				((variantType == VariantType.DELETION) 	&& 	(MGDisplaySettings.DRAW_DELETION_LETTERS 	== MGDisplaySettings.YES_MG_OPTION)) ||
				((variantType == VariantType.SNPS) 		&& 	(MGDisplaySettings.DRAW_SNP_LETTERS 		== MGDisplaySettings.YES_MG_OPTION)) ||
				((variant instanceof ReferenceVariant)	&& 	(MGDisplaySettings.DRAW_REFERENCE_LETTERS 	== MGDisplaySettings.YES_MG_OPTION))) {

			// if the letters must be drawn
			double windowWidth = width / nucleotideNumber;									// calculate the size of window (here, the window is the width of a nucleotide on the screen)
			FontMetrics fm = g.getFontMetrics();											// get the font metrics
			if ((fm.getHeight() < height) && (fm.stringWidth("A") < windowWidth)) {			// verifies if the height of the font is smaller than the height of the stripe AND if the width of a reference letter (A) is smaller than a window size
				String letters = variantDisplay.getVariantSequence();
				g.setColor(Colors.BLACK);													// set the color of the letters
				int letterY = (int) (graphicsHeigth - (height / 2d));						// define where the draw will start on the Y axis
				Graphics2D g2d = (Graphics2D) g.create();									// we reverse all coordinates to display the letter on the right way
				if (currentDrawingAllele == AlleleType.ALLELE02) {
					g2d.scale(1, -1);
					letterY *= -1;
					letterY -= fm.getHeight() / 2d;
				} else {
					letterY += fm.getHeight() / 2d;
				}

				int firstNucleotide = projectWindow.screenToGenomePosition(x) - variant.getStart();	// retrieve the position of the first displayed nucleotide in the variant
				firstNucleotide = Math.max(0, firstNucleotide);

				for (int i = 0; i < nucleotideNumber; i++) {								// for all the nucleotide that are supposed to be displayed
					String letter = "-";													// the default letter is the question mark
					if ((letters != "-") && ((i + firstNucleotide) < letters.length())) {	// if the letters are different to the question mark and if the current index is smaller than the string length
						letter = letters.charAt(i + firstNucleotide) + "";					// we get the current character
					}
					int xC = (int) Math.round(x + (i * windowWidth) + ((windowWidth - fm.stringWidth(letter)) * 0.5));	// the horizontal position from where the draw starts: x (of the stripe) + size of a window * current window number + (windows width - letter width) / 2 (for the middle)
					g2d.drawString(letter, xC, letterY);							// we draw the letter
				}
			}
		}
	}


	/**
	 * Draws the line on the middle of a multi genome track
	 * @param g	graphics object
	 */
	protected void drawMultiGenomeLine (Graphics g, int width, int height) {
		Color color = new Color(Colors.GREY.getRed(), Colors.GREY.getGreen(), Colors.GREY.getBlue(), variantOpacity);
		g.setColor(color);
		int y = height / 2;
		g.drawLine(0, y, width, y);
	}


	/**
	 * Draw a light gray mask over the track with a text
	 * @param g		graphics object
	 * @param height height of the graphics
	 * @param text	a text to display
	 */
	protected void drawMultiGenomeMask (Graphics g, int height, String text) {
		g.setColor(Colors.RED);
		g.drawString(text, 10, height -5);
	}


	/**
	 * Draw the filter pattern
	 * @param g			graphics object
	 * @param x			x coordinate
	 * @param width		width of the stripe
	 * @param height	height of the stripe
	 */
	private void drawFilterPattern (Graphics g, int x, int y, int width, int height) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Colors.GREY);
		g.drawLine(x, y, x + width, y + height);
		g.drawLine(x, y + height, x + width, y);
	}


	/**
	 * Draw a blank of synchronization.
	 * A blank of synchronization is drawn on the full height of the clip.
	 * @param g		graphics object
	 * @param x		x coordinate
	 * @param width	width of the stripe
	 * @param height height of the graphics
	 */
	private void drawReference (Graphics g, int x, int width, int height) {
		g.fillRect(x, 0, width, height);
	}


	/**
	 * Draws a rectangle symbolizing a variant
	 * @param g					graphics object
	 * @param height			height of the graphics
	 * @param variantDisplay	the variant
	 * @param color				the color of the stripe
	 * @param genomeWindow		the displayed genome window
	 */
	private void drawVariant (Graphics g, int height, VariantDisplay variantDisplay, Color color, GenomeWindow genomeWindow) {
		Variant variant = variantDisplay.getVariant();

		// Get start and stop position
		int start = variant.getStart();
		int stop = variant.getStop();

		// Fits the start and stop position to the screen
		if ((start < genomeWindow.getStart()) && (stop > genomeWindow.getStart())) {	// if the variant starts before the left edge of the track but stop after
			start = genomeWindow.getStart();										// the drawing must start from the left edge of the track
		}
		if ((start < genomeWindow.getStop()) && (stop > genomeWindow.getStop())) {		// if the variant start before the right edge of the track and ends up further,
			stop = genomeWindow.getStop();											// the drawing must stop at the right edge of the track
		}

		// Transform the start and stop position to screen coordinates
		int x = projectWindow.genomeToScreenPosition(start);							// get the position where the variant starts
		int width = projectWindow.genomeToScreenWidth(stop - start);					// get the width of the variant on the screen

		if (width == 0) {															// if the width is 0 pixel,
			width = 1;																// we set it to 1 in order to be seen
		}

		// Get the height of the clip and of the stripe
		int variantHeight = getVariantHeight(variant, height);																	// Instantiate the int for the height of the variant

		// Sets the stripe color
		Color newColor;
		if ((drawer.getVariantUnderMouse() != null) && drawer.getVariantUnderMouse().equals(variant)) {		// if there is a variant under the mouse
			newColor = Colors.stripeFilter(color);									// we change the color of the variant
		} else {																	// if not
			newColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), variantOpacity);	// we use the defined color taking into account the opacity
		}
		g.setColor(newColor);														// we set the graphic object color

		// if it is not a blank of synchronization
		int y = height - variantHeight;												// y represents the top left corner of the stripes, the axis goes down to the bottom

		// Draws the variant
		int nucleotideNumber;
		if (variant instanceof ReferenceVariant) {					// drawing a reference stripe requires a different method (shorter and more simple)
			if (color != null) {						// if color is null, it means we don't want to draw the reference
				drawReference(g, x, width, height);
			}
		} else {
			g.fillRect(x, y, width, variantHeight);									// draw the stripe

			// Draws the edge line of stripes
			if (variant.getType() == VariantType.INSERTION) {					// the edge of an insertion and a deletion are different
				drawInsertion(g, x, y, width, variantHeight);
			} else if (variant.getType() == VariantType.DELETION) {
				drawDeletion(g, x, y, width, variantHeight);
			}
		}

		if (variantDisplay.getDisplay() == VariantDisplayList.SHOW_FILTER) {
			drawFilterPattern(g, x, y, width, variantHeight);
		}
		nucleotideNumber = stop - start;
		if (nucleotideNumber == 0) {
			nucleotideNumber = 1;
		}

		// Draw the variant letters
		if (variant.getType() != VariantType.MIX) {
			drawLetters(g, height, x, width, variantHeight, variantDisplay, nucleotideNumber);					// draw the letters (nucleotides) over the stripe
		}
	}



	/**
	 * Gets the color defined for a variant according to its type and a genome
	 * @param genome	the genome name
	 * @param type		the variant type
	 * @return			the associated color
	 */
	private Color getVariantColor (String genome, VariantType type) {
		Color color = null;
		for (VariantLayerDisplaySettings data: drawer.getVariantDataList()) {
			if (data.getGenome().equals(genome)) {
				int variantIndex = data.getVariationTypeList().indexOf(type);
				if (variantIndex != -1) {
					color = data.getColorList().get(variantIndex);
					break;
				}
			}
		}

		return color;
	}


	protected int getVariantHeight (Variant variant, int clipHeight) {
		int height;																	// Instantiate the int for the height of the variant
		if (variant instanceof ReferenceVariant) {					// drawing a reference stripe requires a different method (shorter and more simple)
			height = clipHeight;
		} else {

			int score;																	// get the score of the variant
			if (variant instanceof MixVariant) {
				score = 101;
			} else {
				score = (int) variant.getScore();										// get the score of the variant
			}

			if (score > 100) {															// if the score is higher than 100,
				height = clipHeight;													// the variant height is the height of the clip
			} else {																	// if it is less than 100
				height = (score * clipHeight) / 100;									// the variant height is the percentage between its score and the height clip
			}


		}
		return height;
	}


	/**
	 * Initializes the variant opacity
	 */
	protected void initializeStripesOpacity () {
		variantOpacity = MGDisplaySettings.getInstance().getVariousSettings().getColorOpacity();						// gets the opacity for the stripes
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		currentDrawingAllele = (AlleleType) in.readObject();
		variantOpacity = in.readInt();
		projectWindow = ProjectManager.getInstance().getProjectWindow();
	}


	/**
	 * @param allele set the current allele
	 */
	protected void setCurrentAllele (AlleleType allele) {
		currentDrawingAllele = allele;
	}


	/**
	 * @param drawer the drawer to set
	 */
	protected void setDrawer(MultiGenomeDrawer drawer) {
		this.drawer = drawer;
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(currentDrawingAllele);
		out.writeInt(variantOpacity);
	}
}
