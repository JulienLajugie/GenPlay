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
import java.text.DecimalFormat;

import edu.yu.einstein.genplay.core.GenomeWindow;


/**
 * The graphics part of the ruler
 * @author Julien Lajugie
 * @version 0.1
 */
public final class RulerGraphics extends TrackGraphics<Void> {

	private static final long serialVersionUID = 1612257945809961448L; // Generated ID
	private static final int 			LINE_COUNT = 10;						// Number of line to print (must be an even number)
	private static final Color			LINE_COLOR = Color.lightGray;			// color of the lines
	private static final Color			TEXT_COLOR = Color.black;				// color of the text
	private static final Color			MIDDLE_LINE_COLOR = Color.red;			// color of the line in the middle
	private static final int 			MAJOR_TEXT_HEIGHT = 11;					// height of the absolute position text
	private static final int 			MINOR_TEXT_HEIGHT = 2;					// height of the relative position text
	private static final DecimalFormat 	DF = new DecimalFormat("###,###,###");	// decimal format


	/**
	 * Creates an instance of {@link RulerGraphics}
	 * @param genomeWindow displayed {@link GenomeWindow}
	 */
	public RulerGraphics(GenomeWindow genomeWindow) {
		super(genomeWindow, null);
		setVisible(true);
	}

	
	@Override
	protected void drawTrack(Graphics g) {
		drawRelativeUnits(g);
		drawAbsoluteUnits(g);		
	}
	

	/**
	 * Draws the absolute units. 
	 * @param g {@link Graphics}
	 */
	private void drawAbsoluteUnits(Graphics g) {
		int width = getWidth();
		int halfWidth = (int)Math.round(width / 2d);
		int height = getHeight();
		int positionStart = genomeWindow.getStart();
		int positionStop = genomeWindow.getStop();

		g.setColor(MIDDLE_LINE_COLOR);
		int yText = height - MAJOR_TEXT_HEIGHT;
		String stringToPrint = DF.format(positionStart); 
		g.drawString(stringToPrint, 2, yText);
		stringToPrint = DF.format((positionStart + positionStop) / 2); 
		g.drawString(stringToPrint, halfWidth + 3, yText);
		stringToPrint = DF.format(positionStop); 
		g.drawString(stringToPrint, width - fm.stringWidth(stringToPrint) - 1, yText);
	}


	/**
	 * Draws the relative units.
	 * @param g {@link Graphics}
	 */
	private void drawRelativeUnits(Graphics g) {
		int height = getHeight();
		int positionStart = genomeWindow.getStart();
		int positionStop = genomeWindow.getStop();
		int y = height - MINOR_TEXT_HEIGHT;
		int lastTextStopPos = 0;
		double gap = getWidth() / (double)LINE_COUNT;
		for (int i = 0; i < LINE_COUNT; i++) {
			int x1 = (int)Math.round(i * gap);
			int x2 = (int)Math.round((2 * i + 1) * gap / 2d);
			int distanceFromMiddle = Math.abs(i - LINE_COUNT / 2) * (positionStop - positionStart) / LINE_COUNT;
			String stringToPrint = DF.format(distanceFromMiddle);
			if (x1 >= lastTextStopPos) {
				g.setColor(TEXT_COLOR);
				g.drawString(stringToPrint, x1 + 2, y);
				lastTextStopPos = x1 + fm.stringWidth(stringToPrint) + 2;
			} else {
				g.setColor(LINE_COLOR);
				g.drawLine(x1, y, x1, height);
			}
			g.setColor(LINE_COLOR);
			g.drawLine(x2, y, x2, height);	
		}	
	}
}
