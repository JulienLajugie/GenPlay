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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.ChromosomeWindow;
import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.list.chromosomeWindowList.ChromosomeWindowList;
import edu.yu.einstein.genplay.core.manager.ExceptionManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectZoom;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.IDFilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.display.DisplayableVariantListMaker;
import edu.yu.einstein.genplay.core.multiGenome.display.MGVariantListForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.MGPosition;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantComparator;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.stripes.StripesData;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.toolTipStripe.ToolTipStripeDialog;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEvent;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEventsGenerator;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowListener;


/**
 * Graphics part of a track
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 * @param <T> type of data
 */
public abstract class TrackGraphics<T> extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, GenomeWindowEventsGenerator {


	/**
	 * The ScrollModeThread class is used to scroll the track horizontally 
	 * when the scroll mode is on (ie when the middle button of the mouse is clicked)  
	 * @author Julien Lajugie
	 * @version 0.1
	 */
	private class ScrollModeThread extends Thread {
		@Override
		public void run() {
			synchronized (this) {
				Thread thisThread = Thread.currentThread();
				while (scrollModeThread == thisThread) {
					GenomeWindow newWindow = new GenomeWindow();
					newWindow.setChromosome(genomeWindow.getChromosome());
					newWindow.setStart(genomeWindow.getStart()- scrollModeIntensity);
					newWindow.setStop(genomeWindow.getStop() - scrollModeIntensity);
					if (newWindow.getMiddlePosition() < 0) {
						newWindow.setStart(-genomeWindow.getSize() / 2);
						newWindow.setStop(newWindow.getStart() + genomeWindow.getSize());
					} else if (newWindow.getMiddlePosition() > newWindow.getChromosome().getLength()) {
						newWindow.setStop(newWindow.getChromosome().getLength() + genomeWindow.getSize() / 2);
						newWindow.setStart(newWindow.getStop() - genomeWindow.getSize());
					}
					setGenomeWindow(newWindow);
					yield();
					try {
						if ((scrollModeIntensity == 1) || (scrollModeIntensity == -1)) {
							sleep(100);
						} else {
							sleep(10);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}


	private static final long serialVersionUID = -1930069442535000515L; 	// Generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;				// saved format version
	private static final int	 			VERTICAL_LINE_COUNT = 10;		// number of vertical lines to print
	private static final Color				LINE_COLOR = Color.lightGray;	// color of the lines
	private static final Color				MIDDLE_LINE_COLOR = Color.red;	// color of the line in the middle
	private static final Color				STRIPES_COLOR = Color.GRAY;		// color of the stripes
	private static final int				STRIPES_TRANSPARENCY = 150;		// transparency of the stripes
	protected static final String 			FONT_NAME = "ARIAL";			// name of the font
	protected static final int 				FONT_SIZE = 10;					// size of the font
	protected FontMetrics 		fm = 
		getFontMetrics(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE)); 		// FontMetrics to get the size of a string
	private List<GenomeWindowListener> 		gwListenerList;					// list of GenomeWindowListener
	private int 							verticalLineCount;				// number of vertical lines to print
	transient private int					mouseStartDragX = -1;			// position of the mouse when start dragging
	protected double						xFactor;						// factor between the genomic width and the screen width
	protected GenomeWindow					genomeWindow;					// the genome window displayed by the track
	transient private boolean				isScrollMode = false;			// true if the scroll mode is on
	transient private int					scrollModeIntensity = 0;		// Intensity of the scroll.
	transient private ScrollModeThread 		scrollModeThread; 				// Thread executed when the scroll mode is on
	private ChromosomeWindowList			stripeList = null;				// stripes to display on the track
	protected T 							data;							// data showed in the track
	private String 							genomeName;						// genome on which the track is based (ie aligned on)

	private DisplayableVariantListMaker		paternalVariantListMaker;		// displayable variants list creator (for MG project)
	private DisplayableVariantListMaker		maternalVariantListMaker;		// displayable variants list creator (for MG project)
	private List<String>					stripeLegendText;				// stripes legend for multi genome track (for MG project)
	private List<Color>						stripeLegendColor;				// stripes legend for multi genome track (for MG project)

	private List<StripesData>				stripesList;					// list of stripes to apply to this track (for MG project)
	private List<IDFilterInterface>			filtersList;					// list of filter to apply to this track (for MG project)
	private int								stripesOpacity;					// Transparency of the stripes

	/**
	 * Creates an instance of {@link TrackGraphics}
	 * @param displayedGenomeWindow {@link GenomeWindow} currently displayed
	 * @param data data showed in the track
	 */
	protected TrackGraphics(GenomeWindow displayedGenomeWindow, T data) {
		super();
		this.genomeWindow = displayedGenomeWindow;
		this.data = data;
		this.verticalLineCount = VERTICAL_LINE_COUNT;
		this.gwListenerList = new ArrayList<GenomeWindowListener>();
		setBackground(Color.white);
		setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		multiGenomeInitializing();
	}


	@Override
	public void addGenomeWindowListener(GenomeWindowListener genomeWindowListener) {
		gwListenerList.add(genomeWindowListener);		
	}


	/**
	 * This method is executed when the chromosome changes 
	 */
	protected void chromosomeChanged() {}


	/**
	 * @param mouseXPosition X position of the mouse on the track
	 * @return the scroll intensity
	 */
	public int computeScrollIntensity(int mouseXPosition) {
		double res = twoScreenPosToGenomeWidth(mouseXPosition, getWidth() / 2);
		if (res > 0) {
			return (int) (res / 10d) + 1;	
		} else {
			return (int) (res / 10d) - 1;
		}		
	}


	/**
	 * Draws the main line in the middle of the track
	 * @param g
	 */
	protected void drawMiddleVerticalLine(Graphics g) {
		int y1 = 0;
		int y2 = getHeight();
		g.setColor(MIDDLE_LINE_COLOR);
		int x = (int)Math.round(getWidth() / (double)2);
		g.drawLine(x, y1, x, y2);	
	}


	//////////////////////////////////////////////////////////////////////////////// Multi Genome


	/**
	 * Initializes attributes used for multi genome project.
	 */
	public void multiGenomeInitializing () {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			paternalVariantListMaker = new DisplayableVariantListMaker(genomeWindow, xFactor);
			maternalVariantListMaker = new DisplayableVariantListMaker(genomeWindow, xFactor);
		} else {
			paternalVariantListMaker = null;
			maternalVariantListMaker = null;
		}
		stripesList = null;
		filtersList = null;
	}
	
	
	/**
	 * Reset the list of the variant list makers
	 */
	public void resetVariantListMaker () {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			if (paternalVariantListMaker != null) {
				paternalVariantListMaker.resetList();
			}
			if (maternalVariantListMaker != null) {
				maternalVariantListMaker.resetList();
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
	protected void updateMultiGenomeInformation (List<StripesData> stripesList, List<IDFilterInterface> filtersList) {
		this.stripesList = stripesList;
		this.filtersList = filtersList;
		List<MGVariantListForDisplay> listOfPaternalVariantList = new ArrayList<MGVariantListForDisplay>();
		List<MGVariantListForDisplay> listOfMaternalVariantList = new ArrayList<MGVariantListForDisplay>();

		for (StripesData data: stripesList) {
			AlleleType alleleType = data.getAlleleType();
			boolean paternal = false;
			boolean maternal = false;
			if (alleleType == AlleleType.BOTH) {
				paternal = true;
				maternal = true;
			} else if (alleleType == AlleleType.PATERNAL) {
				paternal = true;
			} else if (alleleType == AlleleType.MATERNAL) {
				maternal = true;
			}

			if (paternal) {
				List<MGVariantListForDisplay> listOfVariantListTmp = data.getListOfVariantList(AlleleType.PATERNAL);
				for (MGVariantListForDisplay currentVariantList: listOfVariantListTmp) {
					listOfPaternalVariantList.add(currentVariantList);
				}
			}

			if (maternal) {
				List<MGVariantListForDisplay> listOfVariantListTmp = data.getListOfVariantList(AlleleType.MATERNAL);
				for (MGVariantListForDisplay currentVariantList: listOfVariantListTmp) {
					listOfMaternalVariantList.add(currentVariantList);
				}
			}
		}

		if (listOfPaternalVariantList.size() > 0) {
			paternalVariantListMaker.setListOfVariantList(listOfPaternalVariantList);
		} else {
			paternalVariantListMaker.setListOfVariantList(null);
		}

		if (listOfMaternalVariantList.size() > 0) {
			maternalVariantListMaker.setListOfVariantList(listOfMaternalVariantList);
		} else {
			maternalVariantListMaker.setListOfVariantList(null);
		}

		repaint();
	}


	/**
	 * Draws stripes showing information for multi genome
	 * @param g
	 */
	protected void drawMultiGenomeInformation(Graphics g) {
		if (stripesList != null) {
			if (stripesList.size() > 0) {
				stripesOpacity = 100 - MGDisplaySettings.getInstance().getVariousSettings().getTransparency();
				AlleleType trackAlleleType = getTrackAlleleType();
				if (trackAlleleType == AlleleType.BOTH) {
					int halfHeight = getHeight() / 2;
					Graphics paternalGraphic = g.create(0, 0, getWidth(), halfHeight);
					Graphics2D maternalGraphic = (Graphics2D) g.create(0, halfHeight, getWidth(), halfHeight);
					maternalGraphic.scale(1, -1);
					maternalGraphic.translate(0, -maternalGraphic.getClipBounds().height - 1);
					drawGenome(paternalGraphic, paternalVariantListMaker.getFittedData(genomeWindow, xFactor));
					drawGenome(maternalGraphic, maternalVariantListMaker.getFittedData(genomeWindow, xFactor));
					drawMultiGenomeLine(g);
				} else if (trackAlleleType == AlleleType.PATERNAL) {
					drawGenome(g, paternalVariantListMaker.getFittedData(genomeWindow, xFactor));
				} else if(trackAlleleType == AlleleType.MATERNAL) {
					drawGenome(g, maternalVariantListMaker.getFittedData(genomeWindow, xFactor));
				}
			}
		}
	}


	/**
	 * Draws the line on the middle of a multi genome track
	 * @param g	graphics object
	 */
	private void drawMultiGenomeLine (Graphics g) {
		Color color = new Color(Color.GRAY.getRed(), Color.GRAY.getGreen(), Color.GRAY.getBlue(), stripesOpacity);
		g.setColor(color);
		int y = getHeight() / 2;
		g.drawLine(0, y, getWidth(), y);
	}


	private void drawGenome (Graphics g, List<VariantInterface> variantList) {
		if (variantList!= null && variantList.size() > 0) {
			// Set color for unused position, dead area and mixed variant
			//Color noAlleleColor = new Color(Color.black.getRed(), Color.black.getGreen(), Color.black.getBlue(), stripesOpacity);
			//Color blankZoneColor = new Color(Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue(), stripesOpacity);
			Color mixColor = new Color(Color.blue.getRed(), Color.blue.getGreen(), Color.blue.getBlue(), stripesOpacity);

			for (VariantInterface variant: variantList) {
				VariantType type = variant.getType();
				Color color;
				if (type == VariantType.MIX) {
					color = mixColor;
				} else {
					String genomeName = variant.getVariantListForDisplay().getAlleleForDisplay().getGenomeInformation().getName();
					color = getStripeColor(genomeName, type);
				}
				drawVariant(g, variant, color);
			}
		}
	}


	/**
	 * Draws a rectangle symbolizing a variant
	 * @param g			graphics object
	 * @param variant	the variant
	 * @param color		the color of the stripe
	 */
	private void drawVariant (Graphics g, VariantInterface variant, Color color) {
		int start = variant.getStart();
		int stop;
		if (variant.getType() == VariantType.SNPS) {
			stop = start + 1;
		} else {
			stop = variant.getStop() + 1;
		}

		if (start < genomeWindow.getStart()) {
			start = genomeWindow.getStart();
		}
		if (stop > genomeWindow.getStop()) {
			stop = genomeWindow.getStop();
		}
		int x = genomePosToScreenPos(start);
		int width = twoGenomePosToScreenWidth(start, stop);
		if (width == 0) {
			width = 1;
		}
		int height = (int) ((variant.getScore() * getHeight()) / 100);

		Color newColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), stripesOpacity);
		g.setColor(newColor);

		g.fillRect(x, g.getClipBounds().height - height, width, height);
	}



	/*private void drawRect (Graphics g, DisplayableVariant displayableVariant, Color color, Color noAlleleColor) {
		// Sets position and length
		if (displayableVariant.getStart() >= genomeWindow.getStart() && displayableVariant.getStop() <= genomeWindow.getStop()) {

			int x = genomePosToScreenPos(displayableVariant.getStart());
			int width = twoGenomePosToScreenWidth(displayableVariant.getStart(), displayableVariant.getStop());
			// trick for stripe with too small width 
			if (width == 0) {
				width = 1;
			}
			int middle = getHeight() / 2;
			int height = (int) (displayableVariant.getQualityScore() * middle / 100);
			int top = middle - height;

			// Sets color
			Color newColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), stripesOpacity);
			g.setColor(newColor);

			// Draws the top half part of the track
			if (displayableVariant.isOnFirstAllele()) {
				g.setColor(newColor);
			} else {
				g.setColor(noAlleleColor);
			}
			g.fillRect(x, top, width, height);

			// Draws the bottom half part of the track
			if (displayableVariant.isOnSecondAllele()) {
				g.setColor(newColor);
			} else {
				g.setColor(noAlleleColor);
			}
			g.fillRect(x, middle, width, height);

			// Displays nucleotide for SNP variant
			if (displayableVariant.getType() == VariantType.SNPS) {
				Font font = g.getFont();

				if (font.getSize() <= width) {
					//Sets drawing parameters
					//int widthOffset = (width - font.getSize()) / 4;
					int widthOffset = (width / 2);
					int heightOffset = (middle + font.getSize()) / 2;
					String alternative = displayableVariant.getNativeVariant().getAlternative();
					String reference = displayableVariant.getNativeVariant().getReference();
					String nucleotide;
					g.setColor(Color.BLACK);

					// Draws nucleotide for the upper part
					if (displayableVariant.isOnFirstAllele()) {
						nucleotide = alternative;
					} else {
						nucleotide = reference;
					}
					g.drawString(nucleotide, x + widthOffset, heightOffset);

					// Draws nucleotide for the lower part
					if (displayableVariant.isOnSecondAllele()) {
						nucleotide = alternative;
					} else {
						nucleotide = reference;
					}
					g.drawString(nucleotide, x + widthOffset, middle + heightOffset);
				}
			}
		}
	}*/


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
	 * @return the list of required genomes for multi genome process
	 */
	private List<String> getGenomesListForMGStripe () {
		List<String> list = new ArrayList<String>();
		if (stripesList != null) {
			for (StripesData data: stripesList) {
				list.add(data.getGenome());
			}
		}
		return list;
	}


	/**
	 * @param genome	the genome name
	 * @return the map variant type/color defined for a genome
	 */
	private Map<VariantType, Color> getVariantColorMap (String genome) {
		Map<VariantType, Color> colors = new HashMap<VariantType, Color>();
		if (stripesList != null) {
			for (StripesData data: stripesList) {
				if (data.getGenome().equals(genome)) {
					for (int i = 0; i < data.getVariationTypeList().size(); i++) {
						colors.put(data.getVariationTypeList().get(i), data.getColorList().get(i));
					}
					break;
				}
			}
		}
		return colors;
	}


	/**
	 * @return the stripesList
	 */
	public List<StripesData> getStripesList() {
		return stripesList;
	}


	/**
	 * @return the filtersList
	 */
	public List<IDFilterInterface> getFiltersList() {
		return filtersList;
	}
	
	
	/**
	 * @return the allele type defined for the track
	 */
	private AlleleType getTrackAlleleType () {
		if (paternalVariantListMaker.getVariantList() != null && maternalVariantListMaker.getVariantList() != null) {
			return AlleleType.BOTH;
		} else if (paternalVariantListMaker.getVariantList() != null) {
			return AlleleType.PATERNAL;
		} else if (maternalVariantListMaker.getVariantList() != null) {
			return AlleleType.MATERNAL;
		}
		return null;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Draws the header of the track.
	 * The header of the track is composed of:
	 * - the name
	 * - the stripe legend (for multi-genome)
	 * It allows a fine width distribution between these 2 texts.
	 * @param g
	 */
	protected void drawHeaderTrack (Graphics g) {
		if (g == null) {
			g = getGraphics(); 
		}

		// Initializes the modified text length for display
		int trackTextDisplayWidth = 0;
		int legendTextDisplayWidth = 0;

		if (validTrackName() && initializeLegend()) {				// if both track name and legend have to be drawed
			int widthOffset = 2;									// space between the border of the rectangle and the text.
			int totalWidth = getWidth() - (widthOffset * 4);		// width of the track
			int trackNameWidth = fm.stringWidth(getName());			// width of the track name
			int legendWidth = fm.stringWidth(getLegend());			// width of the legend

			if ((trackNameWidth + legendWidth) > totalWidth) {		// if track name and legend are longer than the width available
				int diff = (trackNameWidth + legendWidth) - totalWidth;	// we calculate the difference
				trackTextDisplayWidth = (trackNameWidth - (diff / 2));	// we subtract its half to the track name length
				legendTextDisplayWidth = (legendWidth - (diff / 2));	// we subtract its half to the legend length

				// Fix the bug of the negative length
				// Value under 0 is considered as "no change" action further in the program
				if (trackTextDisplayWidth < 1) {
					trackTextDisplayWidth = 1;
				}
				if (legendTextDisplayWidth < 1) {
					legendTextDisplayWidth = 1;
				}
			}
		}

		// Calls methods for drawing
		drawName(g, trackTextDisplayWidth);
		drawLegend(g, legendTextDisplayWidth);

		//repaint(); // if uncommented, the activation/deactivation of the "Show Legend" option in the option dialog takes effect immediately!
	}


	/**
	 * @return true if the track name has to be draw, false otherwise.
	 */
	private boolean validTrackName () {
		return ((getName() != null) && (getName().trim().length() != 0));
	}


	/**
	 * @return true if the stripe legend has to be draw, false otherwise.
	 */
	private boolean validLegend () {
		return (ProjectManager.getInstance().isMultiGenomeProject() && 
				stripesList != null &&
				ProjectManager.getInstance().getProjectConfiguration().isLegend());
	}


	/**
	 * Draws the name of the track
	 * @param g
	 */
	private void drawName(Graphics g, int displayTextWidth) {
		if (validTrackName()) {
			int widthOffset = 2;												// space between the border of the rectangle and the text.
			int textWidth = fm.stringWidth(getName());							// text width on the screen

			String name = getName();
			if (displayTextWidth > 0 && textWidth > displayTextWidth) {			// if the display width of the full text is larger than the one given, the text has to be shorted.
				String newText = "";
				int charIndex = 0;
				while (fm.stringWidth(newText + "...") <= displayTextWidth) {	// we add char one by one to the new text until reaching the length limit
					newText += name.charAt(charIndex);
					charIndex++;
				}
				name = newText + "...";											// the track name is the modified name + "..."
				textWidth = displayTextWidth;									// the length of the text has changed too
			}


			int x = getWidth() - textWidth - (widthOffset * 2);					// starts the rectangle drawing (at the right border of the frame)
			int width = textWidth + (widthOffset * 2);							// width of the rectangle drawing
			int textHeight = fm.getHeight();									// height of the text

			// Draws
			g.setColor(getBackground());
			g.fillRect(x, 1, width, textHeight + widthOffset);
			g.setColor(Color.blue);
			g.drawRect(x, 1, width - 1, textHeight + widthOffset);
			g.drawString(name, x + widthOffset, textHeight);
		}
	}


	/**
	 * Draws the legend of the stripe in a multi genome project
	 * @param g
	 */
	private void drawLegend (Graphics g, int displayTextWidth) {
		if (initializeLegend()) {
			if (stripeLegendText.size() > 0) {
				int widthOffset = 2;												// space between the border of the rectangle and the text.
				int x = 1;															// starts the rectangle drawing (at the left border of the frame)
				int rectWidth = widthOffset * 2;									// width of the rectangle, initialized with the double offset (left and right)
				if (displayTextWidth > 0) {											// if the text length has changed
					rectWidth += displayTextWidth;									// we take it into account
				} else {															// if not
					rectWidth += fm.stringWidth(getLegend());						// it is the native one
				}
				int textHeight = fm.getHeight();									// height of the text

				// Draws
				g.setColor(getBackground());
				g.fillRect(x, 1, rectWidth, textHeight + widthOffset);
				g.setColor(Color.green);
				g.drawRect(x, 1, rectWidth - 1, textHeight + widthOffset);

				// Draws the legend (text containing various colors)
				x++;			// shift the start x position of the text by +1 (do not touch the rectangle)
				textHeight--;	// shift the start y position of the text by -1 (center of the rectangle)
				for (int i = 0; i < stripeLegendText.size(); i++) {									// scans all text fragment of the legend
					g.setColor(stripeLegendColor.get(i));											// set the Graphic with the right color (associated to the text fragment)
					if (i > 0) {																	// if it is not the first text to write
						x += fm.stringWidth(stripeLegendText.get(i - 1));							// we move the x position after the previous text
					}
					String text = stripeLegendText.get(i);											// here is the current fragment to draw
					if (displayTextWidth > 0 && (x + fm.stringWidth(text)) > displayTextWidth) {	// if the length of the text (given by x and the current length of text) is larger than the limit length
						String newText = "";
						int charIndex = 0;
						while ((x + fm.stringWidth(newText + "...")) <= displayTextWidth) {			// we add char one by one to the new text until reaching the length limit
							newText += text.charAt(charIndex);
							charIndex++;
						}
						text = newText + "...";														// the text to draw is the new one + "..."
						g.drawString(text, x, textHeight);											// draws the text
						break;																		// stops the scan (exit loop)
					}
					g.drawString(text, x, textHeight);
				}
			}
		}
	}


	/**
	 * Initializes the list of text and the list of their associated color in order to draw the legend
	 * @return true if it has been initialized, false otherwise. 
	 */
	private boolean initializeLegend () {
		if (validLegend()) {
			// Sets parameters
			stripeLegendText = new ArrayList<String>();
			stripeLegendColor = new ArrayList<Color>();

			// Gets the sorted genome names list
			List <String> genomeNames = getGenomesListForMGStripe();
			Collections.sort(genomeNames);

			// Color for text
			Color textColor = Color.black;

			// Association text/color
			int genomeCounter = 0;
			for (String genomeName: genomeNames) {

				// Gets variant type / color mapping
				Map<VariantType, Color> colors = getVariantColorMap(genomeName);

				// Gets the real size of the list
				int colorsSize = colors.size();

				// If variant type/color exist
				if (colorsSize > 0) {
					genomeCounter++;									// add a genome
					if (genomeCounter > 1) {
						stripeLegendText.add(" " + genomeName + " (");	// add a white space, the name and " ("
					} else {
						stripeLegendText.add(genomeName + " (");		// add the name and " ("
					}
					stripeLegendColor.add(textColor);					// add the text color for the genome

					// Association variant type/color
					int colorCounter = 0;

					for (VariantType type: colors.keySet()) {
						colorCounter++;
						// Add the variant type shortcut
						if (type == VariantType.INSERTION) {
							stripeLegendText.add("I");
						} else if (type == VariantType.DELETION) {
							stripeLegendText.add("D");
						} else if (type == VariantType.SNPS) {
							stripeLegendText.add("SNPs");
						}
						stripeLegendColor.add(colors.get(type));		// add the chosen color

						if (colorCounter < colorsSize) {				// if there is other selected variation type
							stripeLegendText.add(", ");					// add a ", "
							stripeLegendColor.add(textColor);			// with the text color
						}
					}

					stripeLegendText.add(")");							// add a ")" for closing
					stripeLegendColor.add(textColor);
				}
			}
			return true;
		} else {
			return false;
		}

	}


	/**
	 * @return the legend text
	 */
	private String getLegend () {
		String legend = "";
		if (stripeLegendText != null) {
			for (String text: stripeLegendText) {
				legend += text;
			}
		}
		return legend;
	}


	/**
	 * Draws stripes on the track
	 * @param g Graphics
	 */
	protected void drawStripes(Graphics g) {
		if (stripeList != null) {
			int height = getHeight();
			// create a transparent color for the stripes
			Color color = new Color(STRIPES_COLOR.getRed(), STRIPES_COLOR.getGreen(), STRIPES_COLOR.getBlue(), STRIPES_TRANSPARENCY);
			g.setColor(color);
			List<ChromosomeWindow> chromoStripeList = stripeList.getFittedData(genomeWindow, xFactor);//(start, stop);
			if (chromoStripeList != null) {
				for (ChromosomeWindow currentStripe: chromoStripeList) {
					int x = genomePosToScreenPos(currentStripe.getStart()); 
					int widthWindow = genomePosToScreenPos(currentStripe.getStop()) - x;
					if (widthWindow < 1) {
						widthWindow = 1;
					}
					g.fillRect(x, 0, widthWindow, height);
				}
			}
		}
	}


	/**
	 * Called by paintComponent. 
	 * Draws the track
	 * @param g {@link Graphics}
	 */
	abstract protected void drawTrack(Graphics g);


	/**
	 * Draws the vertical lines
	 * @param g {@link Graphics}
	 */
	protected void drawVerticalLines(Graphics g) {
		g.setColor(LINE_COLOR);
		double gap = getWidth() / (double)verticalLineCount;
		int y1 = 0;
		int y2 = getHeight();
		for (int i = 0; i < verticalLineCount; i++) {
			int x = (int)Math.round(i * gap);
			g.drawLine(x, y1, x, y2);
		}
	}


	/**
	 * @param genomePosition a position on the genome
	 * @return the absolute position on the screen (can be > than the screen width)
	 */
	protected int genomePosToScreenPos(int genomePosition) {
		return (int)Math.round((double)(genomePosition - genomeWindow.getStart()) * xFactor);
	}


	/**
	 * @return the data showed in the track
	 */
	public T getData() {
		return data;
	}


	/**
	 * @param x position on the meta genome
	 * @param y y position on the track
	 * @return	the variant associated to the position if exists, null otherwise.
	 */
	private VariantInterface getDisplayableVariant(double x, double y) {
		List<VariantInterface> variantList = null;
		AlleleType trackAlleleType = getTrackAlleleType();
		if (trackAlleleType == AlleleType.BOTH) {
			if (y <= getHeight() / 2) {
				variantList = paternalVariantListMaker.getFittedData(genomeWindow, xFactor);
			} else {
				variantList = maternalVariantListMaker.getFittedData(genomeWindow, xFactor);
			}
		} else if (trackAlleleType == AlleleType.PATERNAL) {
			variantList = paternalVariantListMaker.getFittedData(genomeWindow, xFactor);
		} else if (trackAlleleType == AlleleType.PATERNAL) {
			variantList = maternalVariantListMaker.getFittedData(genomeWindow, xFactor);
		}
		VariantInterface variant = null;
		if (variantList != null) {
			for (VariantInterface current: variantList) {
				if (x >= current.getStart() && x <= current.getStop()) {
					return current;
				}
			}
		}
		return variant;
	}


	/**
	 * @return the genomeName
	 */
	public String getGenomeName() {
		return genomeName;
	}


	/**
	 * @return the displayed {@link GenomeWindow}
	 */
	public GenomeWindow getGenomeWindow() {
		return genomeWindow;
	}


	@Override
	public GenomeWindowListener[] getGenomeWindowListeners() {
		GenomeWindowListener[] genomeWindowListeners = new GenomeWindowListener[gwListenerList.size()];
		return gwListenerList.toArray(genomeWindowListeners);
	}


	/**
	 * @return true if the scroll mode is on
	 */
	public boolean getScrollMode() {
		return isScrollMode;
	}


	/**
	 * @return the stripe list of the track
	 */
	public ChromosomeWindowList getStripes() {
		return stripeList;		
	}


	/**
	 * @return the verticalLineCount
	 */
	public int getVerticalLineCount() {
		return verticalLineCount;
	}


	/**
	 * Calls firePropertyChange with a new {@link GenomeWindow} center where the mouse double clicked.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// double left click
		if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 2) && (!isScrollMode)) {
			// Compute the distance from the cursor to the center of the screen
			double distance = twoScreenPosToGenomeWidth(getWidth() / 2, e.getX());
			distance = Math.floor(distance);
			GenomeWindow newWindow = new GenomeWindow();
			newWindow.setChromosome(genomeWindow.getChromosome());
			newWindow.setStart(genomeWindow.getStart()+ (int) distance);
			newWindow.setStop(genomeWindow.getStop() + (int) distance);
			if ((newWindow.getMiddlePosition()) >= 0 && (newWindow.getMiddlePosition() <= newWindow.getChromosome().getLength())) {
				setGenomeWindow(newWindow);
			}
		}
		if (e.getButton() == MouseEvent.BUTTON3) {
			if (ProjectManager.getInstance().isMultiGenomeProject()) {
				double pos = screenPosToGenomePos(e.getX());
				VariantInterface variant = getDisplayableVariant(pos, e.getY());
				if (variant != null) {
					MGPosition positionInformation = variant.getFullVariantInformation();
					AlleleType trackAlleleType = getTrackAlleleType();
					List<VariantInterface> variantList = null;
					if (trackAlleleType == AlleleType.BOTH) {
						variantList = paternalVariantListMaker.getVariantList();
						for (VariantInterface currentVariant: maternalVariantListMaker.getVariantList()) {
							variantList.add(currentVariant);
						}
						Collections.sort(variantList, new VariantComparator());
					} else if (trackAlleleType == AlleleType.PATERNAL) {
						variantList = paternalVariantListMaker.getVariantList();
					} else if (trackAlleleType == AlleleType.PATERNAL) {
						variantList = maternalVariantListMaker.getVariantList();
					}
					ToolTipStripeDialog toolTip = new ToolTipStripeDialog(variantList);
					toolTip.show(positionInformation, e.getXOnScreen(), e.getYOnScreen());
				} else {
					System.out.println(pos + ": no variant");
				}
			}
		}
	}


	/**
	 * Update the {@link GenomeWindow} when the track is dragged horizontally.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
			double distance = twoScreenPosToGenomeWidth(e.getX(), mouseStartDragX);
			if ((distance > 1) || (distance < -1)) {
				GenomeWindow newWindow = new GenomeWindow();
				newWindow.setChromosome(genomeWindow.getChromosome());
				newWindow.setStart(genomeWindow.getStart()+ (int) distance);
				newWindow.setStop(genomeWindow.getStop() + (int) distance);
				if (newWindow.getMiddlePosition() < 0) {
					newWindow.setStart(-genomeWindow.getSize() / 2);
					newWindow.setStop(newWindow.getStart() + genomeWindow.getSize());
				} else if (newWindow.getMiddlePosition() > newWindow.getChromosome().getLength()) {
					newWindow.setStop(newWindow.getChromosome().getLength() + genomeWindow.getSize() / 2);
					newWindow.setStart(newWindow.getStop() - genomeWindow.getSize());
				}
				setGenomeWindow(newWindow);
				mouseStartDragX = e.getX();
			}
		}		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		if (isScrollMode) {
			setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
			scrollModeThread = new ScrollModeThread();
			scrollModeThread.start();
		} else {
			setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (isScrollMode) {
			scrollModeThread = null;
		}
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		if ((isScrollMode) && (getMousePosition() != null)) {
			scrollModeIntensity = computeScrollIntensity(getMousePosition().x);
		}
	}


	/**
	 * Sets the variable mouseStartDragX when the user press the button 1 of the mouse.
	 * Activates/deactivates the scroll mode when the middle button of the mouse is released
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
			mouseStartDragX = e.getX();
		}
		// Activates/deactivates scroll mode
		if (e.getButton() == MouseEvent.BUTTON2) { // click on middle button
			isScrollMode = !isScrollMode;
			if (isScrollMode) {
				setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
				scrollModeIntensity = computeScrollIntensity(getMousePosition().x);
				scrollModeThread = new ScrollModeThread();
				scrollModeThread.start();
				firePropertyChange("scrollMode", false, true);
			} else {
				setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				scrollModeThread = null;
				firePropertyChange("scrollMode", true, false);
			}
		}
	}


	@Override
	public void mouseReleased(MouseEvent e) {}


	/**
	 * Update the {@link GenomeWindow} when the mouse wheel is used
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int newZoom = 0;
		int weelRotation = Math.abs(e.getWheelRotation());
		boolean isZoomIn = e.getWheelRotation() > 0;
		for (int i = 0; i < weelRotation; i++) {
			// retrieve the only instance of the singleton ZoomManager
			ProjectZoom projectZoom = ProjectManager.getInstance().getProjectZoom();
			if (isZoomIn) {
				newZoom = projectZoom.getZoomIn(genomeWindow.getSize());
			} else {
				newZoom = projectZoom.getZoomOut(genomeWindow.getSize());
			}
			newZoom = Math.min(genomeWindow.getChromosome().getLength() * 2, newZoom);
		}
		GenomeWindow newWindow = new GenomeWindow();
		newWindow.setChromosome(genomeWindow.getChromosome());
		newWindow.setStart((int)(genomeWindow.getMiddlePosition() - newZoom / 2));
		newWindow.setStop(newWindow.getStart() + newZoom);
		setGenomeWindow(newWindow);
	}


	/**
	 * Sets the variable xFactor
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		double newXFactor = (double)getWidth() / (double)(genomeWindow.getStop() - genomeWindow.getStart());
		if (newXFactor != xFactor) {
			xFactor = newXFactor;
			xFactorChanged();
		}
		drawTrack(g);
	}


	@Override
	public void removeGenomeWindowListener(GenomeWindowListener genomeWindowListener) {
		gwListenerList.remove(genomeWindowListener);		
	}


	/**
	 * Save the track as a PNG image.
	 * @param file output file
	 */
	public void saveAsImage(File file) {
		BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		paint(g);
		try {         
			ImageIO.write(image, "PNG", file);
		}catch(Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while saving the tracks as an image");
		}		
	}


	/**
	 * @param x position on the screen
	 * @return position on the genome 
	 */
	protected double screenPosToGenomePos(int x) {
		double distance = twoScreenPosToGenomeWidth(0, x);
		double genomePosition = genomeWindow.getStart() + Math.floor(distance);
		return genomePosition;
	}


	/**
	 * @param genomeName the genomeName to set
	 */
	public void setGenomeName(String genomeName) {
		this.genomeName = genomeName;
	}


	/**
	 * Sets the {@link GenomeWindow} displayed.
	 * @param newGenomeWindow new displayed {@link GenomeWindow}
	 */
	public void setGenomeWindow(GenomeWindow newGenomeWindow) {
		if (!newGenomeWindow.equals(genomeWindow)) {
			GenomeWindow oldGenomeWindow = genomeWindow;
			genomeWindow = newGenomeWindow;
			// we notify the listeners
			GenomeWindowEvent evt = new GenomeWindowEvent(this, oldGenomeWindow, genomeWindow);
			for (GenomeWindowListener currentListener: gwListenerList) {
				currentListener.genomeWindowChanged(evt);
			}
			if (!genomeWindow.getChromosome().equals(oldGenomeWindow.getChromosome())) {
				chromosomeChanged();
			}
			repaint();
		}
	}


	/**
	 * Sets the scroll mode
	 * @param scrollMode
	 */
	public void setScrollMode(boolean scrollMode) {
		isScrollMode = scrollMode;
	}


	/**
	 * shows stripes on the track
	 * @param stripeList a {@link ChromosomeWindowList}
	 */
	public void setStripes(ChromosomeWindowList stripeList) {
		this.stripeList = stripeList;
		repaint();
	}


	/**
	 * @param verticalLineCount the verticalLineCount to set
	 */
	public void setVerticalLineCount(int verticalLineCount) {
		this.verticalLineCount = verticalLineCount;
		repaint();
	}


	/**
	 * @param genomePositionStart start position on the genome
	 * @param genomePositionStop stop position on the genome
	 * @return the width on the screen between this two positions
	 */
	protected int twoGenomePosToScreenWidth(int genomePositionStart, int genomePositionStop) {
		double x1 = ((double)(genomePositionStart - genomeWindow.getStart())) * xFactor;
		double x2 = ((double)(genomePositionStop - genomeWindow.getStart())) * xFactor;
		double distance = Math.abs(x1 - x2);
		return (int) Math.ceil(distance);
	}


	/**
	 * @param x1 position 1 on the screen
	 * @param x2 position 2 on the screen
	 * @return the distance in base pair between the screen position x1 and x2 
	 */
	protected double twoScreenPosToGenomeWidth(int x1, int x2) {
		double distance = ((double)(x2 - x1) / (double)getWidth() * (double)(genomeWindow.getStop() - genomeWindow.getStart()));
		return distance;
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(gwListenerList);
		out.writeInt(verticalLineCount);
		out.writeDouble(xFactor);
		out.writeObject(genomeWindow);
		out.writeObject(stripeList);
		out.writeObject(data);
		out.writeObject(genomeName);
		out.writeObject(paternalVariantListMaker);
		out.writeObject(stripeLegendText);
		out.writeObject(stripeLegendColor);
		out.writeObject(stripesList);
		out.writeObject(filtersList);
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
		gwListenerList = (List<GenomeWindowListener>) in.readObject();
		verticalLineCount = in.readInt();
		xFactor = in.readDouble();
		genomeWindow = (GenomeWindow) in.readObject();
		stripeList = (ChromosomeWindowList) in.readObject();
		data = (T) in.readObject();
		genomeName = (String) in.readObject();
		paternalVariantListMaker = (DisplayableVariantListMaker) in.readObject();
		stripeLegendText = (List<String>) in.readObject();
		stripeLegendColor = (List<Color>) in.readObject();
		stripesList = (List<StripesData>) in.readObject();
		filtersList = (List<IDFilterInterface>) in.readObject();
		stripesOpacity = in.readInt();

		fm = getFontMetrics(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE)); 
	}


	/**
	 * This method is executed when the xFactor changes 
	 */
	protected void xFactorChanged() {
		if (isScrollMode) {
			if (getMousePosition(true) != null) {
				scrollModeIntensity = computeScrollIntensity(getMousePosition().x);
			}
		}
	}


	/**
	 * This function is called when the track is deleted.
	 * Removes all the listeners.  Can be overridden.
	 */
	protected void delete() {
		for (PropertyChangeListener curList: getPropertyChangeListeners())	{
			removePropertyChangeListener(curList);
		}	
	}
}
