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
package edu.yu.einstein.genplay.gui.track.drawer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.comparator.VariantComparator;
import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFilter;
import edu.yu.einstein.genplay.core.multiGenome.display.DisplayableVariantListMaker;
import edu.yu.einstein.genplay.core.multiGenome.display.MGVariantListForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.MGPosition;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.stripes.StripesData;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.toolTipStripe.ToolTipStripeDialog;
import edu.yu.einstein.genplay.gui.track.TrackGraphics;
import edu.yu.einstein.genplay.util.colors.Colors;
import edu.yu.einstein.genplay.util.colors.GenPlayColor;

/**
 * The multi genome drawer is in charge of drawing variation stripes for multi genome project.
 * A track can display stripes of one or two alleles. If the display for both allele is required, the track is then horizontally split.
 * The list makers are in charge to create the list of variant.
 * Drawing a stripe consist on:
 * - drawing the stripe from the specific height and width, genome positions must be translated to the screen positions
 * - drawing the letters (nucleotide) for each stripe when they are known
 * - changing the stripe display when the mouse goes over a stripe
 * - displaying an information dialog about a variant when the user clicks on it
 * @author Nicolas Fourel
 * @version 0.1
 * @param <T>
 */
public class MultiGenomeDrawer<T> implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = 2329957235585775255L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;				// saved format version

	private ProjectWindow					projectWindow;					// instance of the genome window manager

	private DisplayableVariantListMaker		allele01VariantListMaker;		// displayable variants list creator (for MG project)
	private DisplayableVariantListMaker		allele02VariantListMaker;		// displayable variants list creator (for MG project)

	private List<VCFFilter>					vcfFiltersList;					// list of filters that will apply rules of filtering
	private List<StripesData>				stripesList;					// list of stripes to apply to this track (for MG project)
	private int								stripesOpacity;					// Transparency of the stripes
	private VariantInterface 				variantUnderMouse = null;		// Special display when the mouse is over a variant stripe
	private boolean 						serializeStripeList = true;		// enable the stripe lists serialization (see methods for further information)


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(allele01VariantListMaker);
		out.writeObject(allele02VariantListMaker);
		out.writeBoolean(serializeStripeList);
		if (serializeStripeList) {
			out.writeObject(stripesList);
		}
		out.writeInt(stripesOpacity);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		allele01VariantListMaker = (DisplayableVariantListMaker) in.readObject();
		allele02VariantListMaker = (DisplayableVariantListMaker) in.readObject();
		serializeStripeList = in.readBoolean();
		if (serializeStripeList) {
			stripesList = (List<StripesData>) in.readObject();
		}
		stripesOpacity = in.readInt();
		projectWindow = ProjectManager.getInstance().getProjectWindow();
	}


	/**
	 * Constructor of {@link MultiGenomeDrawer}
	 */
	public MultiGenomeDrawer () {
		projectWindow = ProjectManager.getInstance().getProjectWindow();
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			allele01VariantListMaker = new DisplayableVariantListMaker(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
			allele02VariantListMaker = new DisplayableVariantListMaker(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
		} else {
			allele01VariantListMaker = null;
			allele02VariantListMaker = null;
		}
		stripesList = null;
		vcfFiltersList = new ArrayList<VCFFilter>();
	}


	/**
	 * Reset the list of the variant list makers
	 */
	public void resetVariantListMaker () {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			if (allele01VariantListMaker != null) {
				allele01VariantListMaker.resetList(vcfFiltersList);
			}
			if (allele02VariantListMaker != null) {
				allele02VariantListMaker.resetList(vcfFiltersList);
			}
		}
	}


	/**
	 * Updates information for multi genome project.
	 * These information are about:
	 * - stripes
	 * - filters
	 * @param stripesList list of stripes
	 * @param filtersList list of filters
	 */
	public void updateMultiGenomeInformation (List<StripesData> stripesList, List<VCFFilter> filtersList) {
		this.stripesList = stripesList;
		this.vcfFiltersList = filtersList;

		List<MGVariantListForDisplay> allele01VariantLists = new ArrayList<MGVariantListForDisplay>();		// initializes a temporary list of variant for the first allele
		List<MGVariantListForDisplay> allele02VariantLists = new ArrayList<MGVariantListForDisplay>();		// initializes a temporary list of variant for the second allele

		for (StripesData data: stripesList) {							// scans all stripes data

			// Checks wich alleles must be processed
			AlleleType alleleType = data.getAlleleType();				// get the allele type defined for the current stripe data
			boolean allele01 = false;									// initializes a boolean in order to know if we need to process data for the first allele
			boolean allele02 = false;									// initializes a boolean in order to know if we need to process data for the second allele
			if (alleleType == AlleleType.BOTH) {						// if the defined allele type is BOTH, both allele must be processed
				allele01 = true;
				allele02 = true;
			} else if (alleleType == AlleleType.ALLELE01) {				// if the defined allele type is ALLELE01, only the first allele will be processed
				allele01 = true;
			} else if (alleleType == AlleleType.ALLELE02) {				// if the defined allele type is ALLELE02, only the second allele will be processed
				allele02 = true;
			}

			// Gathers variant that have been found
			if (allele01) {												// if the first allele must be processed
				List<MGVariantListForDisplay> listOfVariantListTmp = data.getListOfVariantList(AlleleType.ALLELE01); 	// we ask the stripe data object the list of variant for this allele
				for (MGVariantListForDisplay currentVariantList: listOfVariantListTmp) {								// we add all the variants to the global temporary list for the first allele
					allele01VariantLists.add(currentVariantList);
				}
			}

			if (allele02) {												// if the second allele must be processed
				List<MGVariantListForDisplay> listOfVariantListTmp = data.getListOfVariantList(AlleleType.ALLELE02); 	// we ask the stripe data object the list of variant for this allele
				for (MGVariantListForDisplay currentVariantList: listOfVariantListTmp) {								// we add all the variants to the global temporary list for the second allele
					allele02VariantLists.add(currentVariantList);
				}
			}
		}

		if (allele01VariantLists.size() > 0 || allele02VariantLists.size() > 0) {

		}

		// Sets the list maker with the new list of variant
		allele01VariantListMaker.setListOfVariantList(allele01VariantLists, vcfFiltersList);	// we set the list maker with the temporary list
		allele02VariantListMaker.setListOfVariantList(allele02VariantLists, vcfFiltersList);	// we set the list maker with the temporary list

		//repaint();
	}





	///////////////////////////////////////////////////////////////// Stripes drawing

	/**
	 * Draws stripes showing information for multi genome.
	 * The method checks if the track must show both allele or only one, in order to split the track or not.
	 * @param g				graphics object
	 * @param genomeWindow 	the genome window
	 * @param xFactor 		the x factor
	 */
	public void drawMultiGenomeInformation(Graphics g, GenomeWindow genomeWindow, double xFactor) {
		if (stripesList != null && stripesList.size() > 0) {																// if there are stripes
			stripesOpacity = MGDisplaySettings.getInstance().getVariousSettings().getColorOpacity();						// gets the opacity for the stripes
			AlleleType trackAlleleType = getTrackAlleleType();																// get the allele type of the track
			if (trackAlleleType == AlleleType.BOTH) {																		// if both allele must be displayed
				int halfHeight = g.getClipBounds().height / 2;																// calculates the half of the height track
				Graphics allele01Graphic = g.create(0, 0, g.getClipBounds().width, halfHeight);								// create a graphics for the first allele that correspond to the upper half of the track
				Graphics2D allele02Graphic = (Graphics2D) g.create(0, halfHeight, g.getClipBounds().width, halfHeight);		// create a 2D graphics for the second allele that correspond to the lower half of the track
				allele02Graphic.scale(1, -1);																				// all Y axis (vertical) coordinates must be reversed for the second allele
				allele02Graphic.translate(0, -allele02Graphic.getClipBounds().height - 1);									// translates all coordinates of the graphic for the second allele
				drawGenome(allele01Graphic, allele01VariantListMaker.getFittedData(genomeWindow, xFactor), genomeWindow); 	// draw the stripes for the first allele
				drawGenome(allele02Graphic, allele02VariantListMaker.getFittedData(genomeWindow, xFactor), genomeWindow);	// draw the stripes for the second allele
				drawMultiGenomeLine(g);																						// draw a line in the middle of the track to distinguish upper and lower half. 
			} else if (trackAlleleType == AlleleType.ALLELE01) {															// if the first allele only must be displayed
				drawGenome(g, allele01VariantListMaker.getFittedData(genomeWindow, xFactor), genomeWindow);	// draw its stripes
			} else if(trackAlleleType == AlleleType.ALLELE02) {																// if the second allele only must be displayed
				drawGenome(g, allele02VariantListMaker.getFittedData(genomeWindow, xFactor), genomeWindow);	// draw its stripes
			}
		}
	}


	/**
	 * Draws the line on the middle of a multi genome track
	 * @param g	graphics object
	 */
	private void drawMultiGenomeLine (Graphics g) {
		Color color = new Color(Colors.GREY.getRed(), Colors.GREY.getGreen(), Colors.GREY.getBlue(), stripesOpacity);
		g.setColor(color);
		int y = g.getClipBounds().height / 2;
		g.drawLine(0, y, g.getClipBounds().width, y);
	}


	/**
	 * Draws the list of variation for a genome
	 * @param g				Graphics object
	 * @param variantList	list of variants
	 */
	private void drawGenome (Graphics g, List<VariantInterface> variantList, GenomeWindow genomeWindow) {
		if (variantList != null && variantList.size() > 0) {													// if the variation list has at least one variant
			// Set color for unused position, dead area and mixed variant
			//Color noAlleleColor = new Color(Color.black.getRed(), Color.black.getGreen(), Color.black.getBlue(), stripesOpacity);
			Color blankZoneColor = new Color(Colors.BLACK.getRed(), Colors.BLACK.getGreen(), Colors.BLACK.getBlue(), stripesOpacity);	// color for blank of synchronization
			Color mixColor = new Color(Colors.BLUE.getRed(), Colors.BLUE.getGreen(), Colors.BLUE.getBlue(), stripesOpacity);			// color for mixed variant

			for (VariantInterface variant: variantList) {		// scans all variant
				VariantType type = variant.getType();			// gets its type
				Color color;
				if (type == VariantType.BLANK) {				// defines its color according to its type
					color = blankZoneColor;
				} else if (type == VariantType.MIX) {
					color = mixColor;
				} else  {
					String genomeName = variant.getVariantListForDisplay().getAlleleForDisplay().getGenomeInformation().getName(); 	// gets the genome name of the variant
					color = getStripeColor(genomeName, type);																		// in order to get which color has been defined
				}
				drawVariant(g, variant, color, genomeWindow);	// draw the variant
			}
		}
	}


	/**
	 * Draws a rectangle symbolizing a variant
	 * @param g			graphics object
	 * @param variant	the variant
	 * @param color		the color of the stripe
	 */
	private void drawVariant (Graphics g, VariantInterface variant, Color color, GenomeWindow genomeWindow) {
		// Get start and stop position
		int start = variant.getStart();
		int stop;
		if (variant.getType() == VariantType.SNPS) {		// in case of a SNP, no need to use getStop function (that uses dichotomic algorithm on the full variant list)
			stop = start + 1;								// the stop is one nucleotide further
		} else {											// if not,
			stop = variant.getStop() + 1;					// needs to call getStop
		}

		// Fits the start and stop position to the screen
		if (start < genomeWindow.getStart() && stop > genomeWindow.getStart()) {	// if the variant starts before the left edge of the track but stop after 
			start = genomeWindow.getStart();										// the drawing must start from the left edge of the track
		}
		if (start < genomeWindow.getStop() && stop > genomeWindow.getStop()) {		// if the variant start before the right edge of the track and ends up further,
			stop = genomeWindow.getStop();											// the drawing must stop at the right edge of the track
		}

		// Transform the start and stop position to screen coordinates
		int x = projectWindow.genomePosToScreenXPos(start);							// get the position where the variant starts
		int width = projectWindow.twoGenomePosToScreenWidth(start, stop);			// get the width of the variant on the screen

		if (width == 0) {															// if the width is 0 pixel,
			width = 1;																// we set it to 1 in order to be seen
		}

		// Get the height of the clip and of the stripe
		int clipHeight = g.getClipBounds().height;									// get the height of the clip
		int score = (int) variant.getScore();										// get the score of the variant
		int height;																	// Instantiate the int for the height of the variant
		if (score > 100) {															// if the score is higher than 100,
			height = clipHeight;													// the variant height is the height of the clip
		} else {																	// if it is less than 100
			height = (score * clipHeight) / 100;									// the variant height is the percentage between its score and the height clip
		}

		// Sets the stripe color
		Color newColor;
		if (variantUnderMouse != null && variantUnderMouse.equals(variant)) {		// if there is a variant under the mouse
			newColor = GenPlayColor.stripeFilter(color);							// we change the color of the variant
		} else {																	// if not
			newColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), stripesOpacity);	// we use the defined color taking into account the opacity
		}
		g.setColor(newColor);														// we set the graphic object color

		// Draws the variant
		if (variant.getType() == VariantType.BLANK) {								// drawing a blank of synchronization requires a different method (shorter and more simple) 
			drawBlank(g, x, width);
		} else {																	// if it is not a blank of synchronization
			int y = clipHeight - height;											// y represents the top left corner of the stripes, the axis goes to the bottom

			// Draws the edge line of stripes
			if (variant.getType() == VariantType.INSERTION) {						// the edge of an insertion and a deletion are different
				drawInsertion(g, x, y, width, height);
			} else if (variant.getType() == VariantType.DELETION) {
				drawDeletion(g, x, y, width, height);
			}

			// Draws the stripe
			g.fillRect(x, y, width, height);										// draw the stripe

			// Draw the variant letters
			drawLetters(g, x, width, height, variant, stop - start);				// draw the letters (nucleotides) over the stripe
		}
	}


	/**
	 * Draw a blank of synchronization.
	 * A blank of synchronization is drawn on the full height of the clip.
	 * @param g		graphics object
	 * @param x		x coordinate
	 * @param width	width of the stripe
	 */
	private void drawBlank (Graphics g, int x, int width) {
		g.fillRect(x, 0, width, g.getClipBounds().height);
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
			gTmp.setColor(Colors.BLACK);				// color of the edge (black)
			gTmp.drawRect(x, y, width, height);		// the edge here is a simple line all around the stripe
		}
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
			g2d.setColor(Colors.BLACK);		// color of the edge (black)
			g2d.drawRect(x, y, width - 1, height);		// draw the edge all around the stripe
		}
	}


	/**
	 * Draws the letters (nucleotides) over the stripe.
	 * @param g					graphics object
	 * @param x					x coordinate
	 * @param width				width of the stripe
	 * @param height			height of the stripe
	 * @param variant			variant
	 * @param nucleotideNumber	number of nucleotide to display
	 */
	private void drawLetters (Graphics g, int x, int width, int height, VariantInterface variant, int nucleotideNumber) {
		boolean draw = false;								// boolean to check if drawing letters is required
		VariantType variantType = variant.getType();		// gets the variant type
		if (	variantType == VariantType.INSERTION 	&& 		MGDisplaySettings.DRAW_INSERTION_LETTERS 	== MGDisplaySettings.YES_MG_OPTION ||	// checks all options in order to determine if the letters must be drawn
				variantType == VariantType.DELETION 	&& 		MGDisplaySettings.DRAW_DELETION_LETTERS 	== MGDisplaySettings.YES_MG_OPTION ||
				variantType == VariantType.SNPS 		&& 		MGDisplaySettings.DRAW_SNP_LETTERS 			== MGDisplaySettings.YES_MG_OPTION) {
			draw = true;
		}

		if (draw) {																			// if the letters must be drawn
			double windowWidth = width / nucleotideNumber;									// calculate the size of window (here, the windo is the width of a nucleotide on the screen)
			FontMetrics fm = g.getFontMetrics();											// get the font metrics
			if (fm.getHeight() < height && fm.stringWidth("A") < windowWidth) {				// verifies if the height of the font is smaller than the height of the stripe AND if the width of a reference letter (A) is smaller than a window size
				MGPosition fullInformation = variant.getFullVariantInformation();			// gets the full information of the variant
				if (fullInformation != null) {												// if they exist (variant can be MIX which does not have full information)
					String letters = fullInformation.getAlternative();						// we first get the ALT field (by default)
					if (letters.charAt(0) == '<') {											// if the first character is '<', it a SV variation and the sequence is not provided
						letters = "?";														// the letter is the question mark
					} else if (variantType == VariantType.DELETION) {						// if the variant is a deletion, 
						letters = fullInformation.getReference().substring(1);				// the deleted nucleotides are provided by the REF field (we do not want the first one)
					} else if (variantType == VariantType.INSERTION) {						// if the variant is an insertion,
						letters = letters.substring(1);										// we already have the right field but we don't ant the first letter
					}
					g.setColor(Colors.BLACK);												// set the color of the letters
					int letterHeight = (height + fm.getHeight()) / 2;						// define where the draw will start on the Y axis
					for (int i = 0; i < nucleotideNumber; i++) {							// for all the nucleotide that are supposed to be displayed
						String letter = "?";												// the default letter is the question mark
						if (letters != "?" && i < letters.length()) {						// if the letters are different to the question mark and if the current index is smaller than the string length
							letter = letters.charAt(i) + "";								// we get the current character
						}
						int xC = (int) Math.round(x + i * windowWidth + (windowWidth - fm.stringWidth(letter)) * 0.5);	// the horizontal position from where the draw starts: x (of the stripe) + size of a window * current window number + (windows width - letter width) / 2 (for the middle)
						if (getTrackAlleleType() == AlleleType.BOTH && allele02VariantListMaker.getVariantList().contains(variant)) { // if both allele must be drawn and if the current variant is contained in the second allele variant list,
							Graphics2D g2d = (Graphics2D) g.create();						// we reverse all coordinates to display the letter on the right way
							g2d.scale(1, -1);
							g2d.translate(0, -g2d.getClipBounds().height - 1);
							g2d.drawString(letter, xC, letterHeight);						// we draw the letter
						} else {
							g.drawString(letter, xC, letterHeight);							// we draw the letter
						}
					}
				}
			}
		}

	}

	/////////////////////////////////////////////////////////////////

	/**
	 * Display the content of a variant in the tool tip stripe dialog
	 * @param trackHeight 
	 * @param e mouse event
	 */
	public void toolTipStripe (int trackHeight, MouseEvent e) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {												// we must be in a multi genome project
			double pos = projectWindow.screenXPosToGenomePos(TrackGraphics.getTrackGraphicsWidth(), e.getX());	// we translate the position on the screen into a position on the genome
			VariantInterface variant = getDisplayableVariant(trackHeight, pos, e.getY());						// we get the variant (Y is needed to know if the variant is on the upper or lower half of the track)
			if (variant != null) {																				// if a variant has been found
				AlleleType trackAlleleType = getTrackAlleleType();												// we get the allele type of the track
				List<VariantInterface> variantList = null;														// we will try to get the full list of variant displayed on the whole track (we want to move from a variant to another one no matter the allele)
				if (trackAlleleType == AlleleType.BOTH) {														// if both allele are displayed,
					variantList = getCopyOfVariantList(allele01VariantListMaker.getVariantList());				// create a copy of the variant list of the first allele
					for (VariantInterface currentVariant: allele02VariantListMaker.getVariantList()) {			// we add all variant from the variant list of the second allele
						variantList.add(currentVariant);
					}
					Collections.sort(variantList, new VariantComparator());										// we sort the global list
				} else if (trackAlleleType == AlleleType.ALLELE01) {											// if the first allele only is displayed
					variantList = getCopyOfVariantList(allele01VariantListMaker.getVariantList());				// we get the copy of its list
				} else if (trackAlleleType == AlleleType.ALLELE02) {											// if the second allele only is displayed
					variantList = getCopyOfVariantList(allele02VariantListMaker.getVariantList());				// we get the copy of its list
				}
				ToolTipStripeDialog toolTip = new ToolTipStripeDialog(variantList);								// we create the information dialog
				toolTip.show(variant, e.getXOnScreen(), e.getYOnScreen());							// we show it
			}
		}
	}


	/**
	 * Checks if the mouse is over a variant and says whether the track graphics must repaint or not 
	 * @param trackHeight 
	 * @param e the mouse event
	 * @return	true if the track must be repainted, false otherwise
	 */
	public boolean isOverVariant (int trackHeight, MouseEvent e) {
		if (ProjectManager.getInstance().isMultiGenomeProject() && allele01VariantListMaker != null && allele02VariantListMaker != null) {	// if we are in multi genome project
			double pos = projectWindow.screenXPosToGenomePos(TrackGraphics.getTrackGraphicsWidth(), e.getX());	// we translate the position on the screen into a position on the genome
			VariantInterface variant = getDisplayableVariant(trackHeight, pos, e.getY());						// we get the variant (Y is needed to know if the variant is on the upper or lower half of the track)
			if (variant != null) {																				// if a variant has been found
				variantUnderMouse = variant;																	// the mouse is on this variant (we save it)
				return true;																					// we return true
			} else if (variantUnderMouse != null) {																// no variant has been found but one was already defined (the mouse is just getting out of the stripe)
				variantUnderMouse = null;																		// there is no variant under the mouse anymore
				return true;
			}
		}
		return false;
	}


	/**
	 * Checks if the track has to be repaint when the mouse exit from it.
	 * Basically, it has to be repaint if a variant was under the mouse in order to not highlight it.
	 * @return true if the track has to be repaint, false otherwise
	 */
	public boolean hasToBeRepaintAfterExit () {
		if (variantUnderMouse != null) {
			variantUnderMouse = null;
			return true;
		}
		return false;
	}

	/////////////////////////////////////////////////////////////////


	/**
	 * @return the stripesList
	 */
	public List<StripesData> getStripesList() {
		return stripesList;
	}


	/**
	 * @return the vcfFiltersList
	 */
	public List<VCFFilter> getFiltersList() {
		return vcfFiltersList;
	}


	/**
	 * @return the list of required genomes for multi genome process
	 */
	public List<String> getGenomesListForMGStripe () {
		List<String> list = new ArrayList<String>();
		if (stripesList != null) {
			for (StripesData data: stripesList) {
				if (!list.contains(data.getGenome())) {
					list.add(data.getGenome());
				}
			}
		}
		return list;
	}


	/**
	 * Gets the color defined for a variant according to its type and a genome
	 * @param genome	the genome name
	 * @param type		the variant type
	 * @return			the associated color
	 */
	private Color getStripeColor (String genome, VariantType type) {
		Color color = null;
		for (StripesData data: stripesList) {
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


	/**
	 * @param genome	the genome name
	 * @return the map variant type/color defined for a genome
	 */
	public Map<VariantType, Color> getVariantColorMap (String genome) {
		Map<VariantType, Color> colors = new HashMap<VariantType, Color>();
		if (stripesList != null) {
			for (StripesData data: stripesList) {
				if (data.getGenome().equals(genome)) {
					for (int i = 0; i < data.getVariationTypeList().size(); i++) {
						colors.put(data.getVariationTypeList().get(i), data.getColorList().get(i));
					}
				}
			}
		}
		return colors;
	}


	/**
	 * @param trackHeight the track graphics height
	 * @param x position on the meta genome
	 * @param y y position on the track
	 * @return	the variant associated to the position if exists, null otherwise.
	 */
	public VariantInterface getDisplayableVariant(int trackHeight, double x, double y) {
		List<VariantInterface> variantList = null;					// instantiate a list that will lead to the right variant list
		AlleleType trackAlleleType = getTrackAlleleType();			// we get the allele type of the track
		if (trackAlleleType != null) {
			if (trackAlleleType == AlleleType.BOTH) {					// if both allele are displayed, we must distinguish on which allele the variant is
				if (y <= trackHeight / 2) {				// if Y is less than the half of the track height
					variantList = allele01VariantListMaker.getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());	// we have to look on the first allele list
				} else {												// if Y is more than the half of the track height
					variantList = allele02VariantListMaker.getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());	// we have to look on the second allele list
				}
			} else if (trackAlleleType == AlleleType.ALLELE01) {		// if the first allele only is displayed
				variantList = allele01VariantListMaker.getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());	// we look on the first allele list
			} else if (trackAlleleType == AlleleType.ALLELE02) {		// if the second allele only is displayed
				variantList = allele02VariantListMaker.getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());	// we look on the second allele list
			}
		}

		VariantInterface variant = null;
		if (variantList != null) {									// if a variant list has been found
			for (VariantInterface current: variantList) {			// we scan all of its variant
				if (current.getType() == VariantType.SNPS) {		// special case for SNP
					if (x == current.getStart()) {					// a SNP is defined for a start but return the stop as start + 1. However, is present on the start position only, it does not include the stop!
						return current;
					}
				} else {
					if (x >= current.getStart() && x <= current.getStop()) {	// if X is included in a variant,
						return current;											// we have found it!
					}
				}
			}
		}
		return variant;												// we return the variant (or null if it has not been found)
	}



	/**
	 * @return the allele type defined for the track
	 */
	public AlleleType getTrackAlleleType () {
		if (stripesList != null) {
			boolean allele01 = false;
			boolean allele02 = false;
			for (StripesData stripe: stripesList) {
				AlleleType type = stripe.getAlleleType();
				if (type == AlleleType.BOTH) {					// if both allele are required
					return AlleleType.BOTH;
				} else if (type == AlleleType.ALLELE01) {		// if the first allele only is required
					allele01 = true;
				} else if (type == AlleleType.ALLELE02) {		// if the second allele only is required
					allele02 = true;
				}
			}

			if (allele01 & allele02) {
				return AlleleType.BOTH;
			} else if (allele01) {
				return AlleleType.ALLELE01;
			} else if (allele02) {
				return AlleleType.ALLELE02;
			}
		}
		return null;
	}


	/**
	 * Creates a copy of a variant list
	 * @param variantList variant list to copy
	 * @return copy of the variant list given in parameter
	 */
	private List<VariantInterface> getCopyOfVariantList (List<VariantInterface> variantList) {
		List<VariantInterface> copy = new ArrayList<VariantInterface>();
		for (VariantInterface currentVariant: variantList) {
			copy.add(currentVariant);
		}
		return copy;
	}


	/**
	 * Enable the serialization of the stripes list for multi genome project.
	 * The stripe list serialization generates serialization errors when lists refer to same tracks.
	 * The lists are then not serialized and managed by the {@link MGDisplaySettings}.
	 * This must be performed for copy/cut/paste actions, no issue in saving/loading a whole project.
	 * This is a temporary fix while understanding more the serialization issue.
	 */
	public void enableStripeListSerialization () {
		serializeStripeList = true;
	}


	/**
	 * Disable the serialization of the stripes list for multi genome project.
	 */
	public void disableStripeListSerialization () {
		serializeStripeList = false;
	}

}
