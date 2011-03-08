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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.track;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.enums.Nucleotide;
import edu.yu.einstein.genplay.core.list.DisplayableListOfLists;
import edu.yu.einstein.genplay.util.ColorConverters;


/**
 * A {@link TrackGraphics} part of a {@link NucleotideListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class NucleotideListTrackGraphics extends TrackGraphics<DisplayableListOfLists<Nucleotide, Nucleotide[]>> {

	private static final long serialVersionUID = -7170987212502378002L;				// generated ID
	private static final int NUCLEOTIDE_HEIGHT = 10;								// y position of the nucleotides on the track
	private int 		maxBaseWidth = 0;											// size on the screen of the widest base to display (in pixels) 
	private Integer 	baseUnderMouseIndex = null;									// index of the base under the mouse


	/**
	 * Creates an instance of {@link NucleotideListTrackGraphics}
	 * @param displayedGenomeWindow a {@link GenomeWindow} to display
	 * @param data a sequence of {@link Nucleotide} to display
	 */
	public NucleotideListTrackGraphics(GenomeWindow displayedGenomeWindow, DisplayableListOfLists<Nucleotide, Nucleotide[]> data) {
		super(displayedGenomeWindow, data);
		// compute the length in pixels of the widest base to display
		String[] bases = {"N", "A", "C", "G", "T"};
		for (String currBase: bases) {
			maxBaseWidth = Math.max(maxBaseWidth, fm.stringWidth(currBase));
		}
	}


	/**
	 * Draws the backgrounds of the nucleotide
	 * @param g {@link Graphics}
	 */
	private void drawNucleotideBackgrounds(Graphics g) {
		if (data != null) {
			int width = getWidth();
			int height = getHeight();
			long baseToPrintCount = genomeWindow.getSize();
			// if there is enough room to print something
			if (baseToPrintCount <= getWidth()) {
				Nucleotide[] nucleotides = data.getFittedData(genomeWindow, xFactor);
				for (int position = genomeWindow.getStart(); position <= genomeWindow.getStop(); position++) {
					int index = position - genomeWindow.getStart();
					if (nucleotides[index] != null) {
						Nucleotide nucleotide = nucleotides[index];
						// compute the position on the screen
						int x = genomePosToScreenPos(position);
						int nucleoWith = twoGenomePosToScreenWidth(position, position + 1);  
						// select a different color for each type of base
						if ((baseUnderMouseIndex != null) && (index == baseUnderMouseIndex)) {
							g.setColor(Color.WHITE);
						} else {
							g.setColor(ColorConverters.nucleotideToColor(nucleotide));							
						}
						g.fillRect(x, 0, nucleoWith, height);	
						if (nucleoWith >= 5) {
							g.setColor(Color.WHITE);
							g.drawRect(x, 0, nucleoWith, height - 1);	
						}
					}
				}		
			} else {
				// if we can't print all the bases we just print a message for the user
				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(0, 0, width, height);
				g.setColor(Color.black);
				g.drawString("Can't display sequence at this zoom level", 0, getHeight() - NUCLEOTIDE_HEIGHT);
			}
			g.setColor(Color.WHITE);
			g.drawLine(0, 0, width, 0);
			g.drawLine(0, height - 1, width, height - 1);			
		}
	}


	/**
	 * Draws the letter of the nucleotides
	 * @param g
	 */
	private void drawNucleotideLetters(Graphics g) {
		if (data != null) {
			long baseToPrintCount = genomeWindow.getSize();
			// if there is enough room to print something
			if (baseToPrintCount <= getWidth()) {
				Nucleotide[] nucleotides = data.getFittedData(genomeWindow, xFactor);
				for (int position = genomeWindow.getStart(); position <= genomeWindow.getStop(); position++) {
					int index = position - genomeWindow.getStart();
					if (nucleotides[index] != null) {
						Nucleotide nucleotide = nucleotides[index];
						if (maxBaseWidth * baseToPrintCount <= getWidth()) {
							// compute the position on the screen
							int x = genomePosToScreenPos(position);
							// select a different color for each type of base
							if ((baseUnderMouseIndex != null) && (index == baseUnderMouseIndex)) {
								g.setColor(Color.BLACK);
							} else {
								g.setColor(Color.WHITE);
							}
							g.drawString(String.valueOf(nucleotide.getCode()), x, getHeight() - NUCLEOTIDE_HEIGHT);							
						}
					}
				}			
			} else { 
				// if we can't print all the bases we just print a message for the user
				g.setColor(Color.black);
				g.drawString("Can't display sequence at this zoom level", 0, getHeight() - NUCLEOTIDE_HEIGHT);
			}
		}
	}


	@Override
	protected void drawTrack(Graphics g) {
		drawNucleotideBackgrounds(g);
		drawVerticalLines(g);
		drawNucleotideLetters(g);
		drawStripes(g);
		drawName(g);
		drawMiddleVerticalLine(g);
	}

	
	/**
	 * Resets the tooltip and the highlighted base when the mouse is dragged
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
		if (baseUnderMouseIndex != null) {
			baseUnderMouseIndex = null;
			setToolTipText(null);
			repaint();
		}		
	}
	

	/**
	 * Resets the tooltip and the highlighted base when the mouse exits the track
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		super.mouseExited(e);
		if (baseUnderMouseIndex != null) {
			baseUnderMouseIndex = null;
			setToolTipText(null);
			repaint();
		}
	}


	/**
	 * Sets the tooltip and the base with the mouse over when the mouse move
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		setToolTipText("");
		long baseToPrintCount = genomeWindow.getSize();
		Integer oldBaseUnderMouseIndex = baseUnderMouseIndex;
		baseUnderMouseIndex = null;		
		if (!getScrollMode()) {
			// if the zoom is too out we can't print the bases and so there is none under the mouse
			if (baseToPrintCount <= getWidth()) {
				// retrieve the position of the mouse
				Point mousePosition = e.getPoint();
				// retrieve the list of the printed nucleotides
				Nucleotide[] printedBases = data.getFittedData(genomeWindow, xFactor);
				// do nothing if there is no genes
				if (printedBases != null) {
					double distance = twoScreenPosToGenomeWidth(0, mousePosition.x);
					distance = Math.floor(distance);
					baseUnderMouseIndex = (int) distance;
					// we repaint the track only if the gene under the mouse changed
					if (((oldBaseUnderMouseIndex == null) && (baseUnderMouseIndex != null)) 
							|| ((oldBaseUnderMouseIndex != null) && (!oldBaseUnderMouseIndex.equals(baseUnderMouseIndex)))) {
						repaint();
					}				
				}
			}
			if (baseUnderMouseIndex != null) {
				setToolTipText(data.getFittedData(genomeWindow, xFactor)[baseUnderMouseIndex].name());
			}
		}
	}
}
