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
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.ChromosomeWindow;
import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.list.chromosomeWindowList.ChromosomeWindowList;
import edu.yu.einstein.genplay.core.manager.ExceptionManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectZoom;
import edu.yu.einstein.genplay.core.multiGenome.stripeManagement.DisplayableVariant;
import edu.yu.einstein.genplay.core.multiGenome.stripeManagement.DisplayableVariantListCreator;
import edu.yu.einstein.genplay.core.multiGenome.stripeManagement.MultiGenomeStripes;
import edu.yu.einstein.genplay.gui.action.project.PAMultiGenomeSNP;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.toolTipStripe.ToolTipStripe;
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
	private MultiGenomeStripes 				multiGenomeStripes;				// stripes showing multi genome information (for MG project)
	private DisplayableVariantListCreator	displayableVariantListCreator;	// displayable variants list creator (for MG project)
	private String 							genomeName;						// genome on which the track is based (ie aligned on)
	private List<DisplayableVariant> 		displayableVariantList;			// list of variant for multi genome project
	private List<String>					stripeLegendText;				// stripes legend for multi genome track 
	private List<Color>						stripeLegendColor;				// stripes legend for multi genome track

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
	 * Draws displayable variants
	 * @param g	graphics object
	 */
	private void drawGenome (Graphics g) {
		int cptMix = 0;
		int cptBlank = 0;
		int cptOther = 0;
		int cptIns = 0;
		int cptDel = 0;
		int cptSNP = 0;

		// Requirement for variant list scan
		if (displayableVariantList.size() > 0) {

			// Set color for unused position and dead area
			Color noAlleleColor = new Color(Color.black.getRed(), Color.black.getGreen(), Color.black.getBlue(), multiGenomeStripes.getTransparency());
			Color blankZoneColor = new Color(Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue(), multiGenomeStripes.getTransparency());

			// Start variant list scan
			for (DisplayableVariant displayableVariant: displayableVariantList) {
				if (displayableVariant.getType().equals(VariantType.MIX)) {
					cptMix++;

					Color mixColor = new Color(Color.blue.getRed(), Color.blue.getGreen(), Color.blue.getBlue(), multiGenomeStripes.getTransparency());
					drawRect(g, displayableVariant, mixColor, mixColor);
				} else if (displayableVariant.getType().equals(VariantType.BLANK)) {
					cptBlank++;

					drawRect(g, displayableVariant, blankZoneColor, noAlleleColor);
				} else {
					cptOther++;

					if (displayableVariant.getType().equals(VariantType.DELETION)) {
						cptDel++;
					} else if (displayableVariant.getType().equals(VariantType.INSERTION)) {
						cptIns++;
					} else if (displayableVariant.getType().equals(VariantType.SNPS)) {
						cptSNP++;
					}

					// Color association
					Map<VariantType, Color> association = multiGenomeStripes.getColorAssociation().get(displayableVariant.getNativeVariant().getFullGenomeName());

					// Draws the stripe
					drawRect(g, displayableVariant, association.get(displayableVariant.getType()), noAlleleColor);

					// Draws the dead zone
					if (displayableVariant.deadZoneExists()) {
						drawRect(g, displayableVariant, blankZoneColor, noAlleleColor);
					}
				}
			}
			//System.out.println("mix: " + cptMix + ", blank: " + cptBlank + ", other: " + cptOther + "(" + cptIns + ", " + cptDel + ", " + cptSNP + ")");
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


	/**
	 * Draws stripes showing information for multi genome
	 * @param g
	 */
	protected void drawMultiGenomeInformation(Graphics g) {
		if (multiGenomeStripes != null) {
			if (ProjectManager.getInstance().getGenomeSynchronizer().dataHasBeenComputed()) {
				updateDisplayableVariantList(false);
				drawMultiGenomeLine(g);
				drawGenome(g);
			}
		}
	}


	/**
	 * Updates the list of displayable variant.
	 * According to the different parameters, the list creator determines if it has to recreate the list from zero or not.
	 * If the list must be fully recreated, no matter internal parameters, the parameter 'force' must be set at true.
	 * @param force if the list has to be recreated from the beginning.
	 */
	protected void updateDisplayableVariantList (boolean force) {
		displayableVariantListCreator.setGenomeNames(multiGenomeStripes.getRequiredGenomes());
		displayableVariantListCreator.setQuality((double) multiGenomeStripes.getQuality());
		if (force) {
			displayableVariantListCreator.SNPUpdate();
		}
		displayableVariantList = displayableVariantListCreator.getFittedData(genomeWindow, xFactor);

	}


	/**
	 * Draws the line on the middle of a multi genome track
	 * @param g	graphics object
	 */
	private void drawMultiGenomeLine (Graphics g) {
		Color color = new Color(Color.GRAY.getRed(), Color.GRAY.getGreen(), Color.GRAY.getBlue(), multiGenomeStripes.getTransparency());
		g.setColor(color);
		int y = getHeight() / 2;
		g.drawLine(0, y, getWidth(), y);
	}


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
		//System.out.println("TrackGraphics: drawHeaderTrack");
		
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
				multiGenomeStripes != null &&
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
			Map<String, Map<VariantType, Color>> colorMap = multiGenomeStripes.getColorAssociation();
			stripeLegendText = new ArrayList<String>();
			stripeLegendColor = new ArrayList<Color>();

			// Gets the sorted genome names list
			List <String> genomeNames = new ArrayList<String>(colorMap.keySet());
			Collections.sort(genomeNames);

			// Color for text
			Color textColor = Color.black;

			// Association text/color
			int genomeCounter = 0;
			for (String genomeName: genomeNames) {

				// Gets variant type / color mapping
				Map<VariantType, Color> colors = colorMap.get(genomeName);

				// Gets the real size of the list
				int colorsSize = colors.size();
				if (colors.containsKey(VariantType.INS)) { colorsSize--;}	// Ins/Insertion types refer to the same main type: insertion 
				if (colors.containsKey(VariantType.DEL)) { colorsSize--;}	// Del/Deletion types refer to the same main type: deletion

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
						if (type != VariantType.INS && type != VariantType.DEL) {	// these types are automatically added when user selects "insertion" or "deletion",
							// it is useless to treat them and redundant.
							colorCounter++;
							// Add the variant type shortcut
							if (type == VariantType.INSERTION) {
								stripeLegendText.add("I");
							} else if (type == VariantType.DELETION) {
								stripeLegendText.add("D");
							}
							stripeLegendColor.add(colors.get(type));		// add the chosen color

							if (colorCounter < colorsSize) {				// if there is other selected variation type
								stripeLegendText.add(", ");					// add a ", "
								stripeLegendColor.add(textColor);			// with the text color
							}
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
	 * Draws a rectangle symbolizing variants
	 * @param g			graphics object
	 * @param displayableVariant	the variant
	 * @param color		the color
	 */
	private void drawRect (Graphics g, DisplayableVariant displayableVariant, Color color, Color noAlleleColor) {
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
			Color newColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), multiGenomeStripes.getTransparency());
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
	 * @param pos position on the meta genome
	 * @return	the variant associated to the position if exists, null otherwise.
	 */
	private DisplayableVariant getDisplayableVariant(double pos) {
		DisplayableVariant displayableVariant = null;
		if (displayableVariantList != null) {
			for (DisplayableVariant current: displayableVariantList) {
				if (pos >= current.getStart() && pos < current.getStop()) {
					return current;
				}
			}
		}
		return displayableVariant;
	}


	/**
	 * @return the variantList
	 */
	public List<DisplayableVariant> getDisplayableVariantList() {
		return displayableVariantList;
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
	 * @return the stripeInformation
	 */
	protected MultiGenomeStripes getMultiGenomeStripes() {
		return multiGenomeStripes;
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
				DisplayableVariant displayableVariant = getDisplayableVariant(pos);
				if (displayableVariant != null) {
					ToolTipStripe toolTip = new ToolTipStripe(displayableVariantListCreator.getFullDisplayableVariantList());
					toolTip.show(displayableVariant, e.getXOnScreen(), e.getYOnScreen());
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
		multiGenomeStripes = (MultiGenomeStripes) in.readObject();
		displayableVariantListCreator = (DisplayableVariantListCreator) in.readObject();
		genomeName = (String) in.readObject();
		displayableVariantList = (List<DisplayableVariant>) in.readObject();
		fm = getFontMetrics(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE)); 
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
	 * @param multiGenomeStripes the stripeInformation to set
	 */
	protected void setMultiGenomeStripes(MultiGenomeStripes multiGenomeStripes) {
		PAMultiGenomeSNP multiGenomeSNP = new PAMultiGenomeSNP();
		multiGenomeSNP.setPreviousSetting(this.multiGenomeStripes);
		multiGenomeSNP.setNewSetting(multiGenomeStripes);
		multiGenomeSNP.actionPerformed(null);

		this.multiGenomeStripes = multiGenomeStripes;
		repaint();
	}


	/**
	 * Initializes attributes used for multi genome project.
	 */
	public void multiGenomeInitializing () {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			multiGenomeStripes = new MultiGenomeStripes();
			displayableVariantListCreator = new DisplayableVariantListCreator();
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
		out.writeObject(multiGenomeStripes);
		out.writeObject(displayableVariantListCreator);
		out.writeObject(genomeName);
		out.writeObject(displayableVariantList);
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
