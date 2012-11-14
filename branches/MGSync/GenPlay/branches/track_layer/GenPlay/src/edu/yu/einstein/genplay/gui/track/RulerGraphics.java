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
import java.awt.Graphics;
import java.text.DecimalFormat;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * The graphics part of the ruler
 * @author Julien Lajugie
 * @version 0.1
 */
public final class RulerGraphics extends TrackGraphics<Void> {

	private static final long serialVersionUID = 1612257945809961448L; // Generated ID
	private static final int 			LINE_COUNT = 10;							// Number of line to print (must be an even number)
	private static final Color			LINE_COLOR = Colors.LIGHT_GREY;				// color of the lines
	private static final Color			TEXT_COLOR = Colors.BLACK;					// color of the text
	private static final Color			MIDDLE_LINE_COLOR = Colors.RED;				// color of the line in the middle
	private static final int 			MAJOR_TEXT_HEIGHT = 11;						// height of the absolute position text
	private static final int 			MINOR_TEXT_HEIGHT = 2;						// height of the relative position text
	private static final DecimalFormat 	DF = new DecimalFormat("###,###,###");		// decimal format

	private int currentMiddlePosition;

	/**
	 * Creates an instance of {@link RulerGraphics}
	 */
	public RulerGraphics() {
		super(null);
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
		// Set graphic parameters
		int width = getWidth();
		int halfWidth = (int)Math.round(width / 2d);
		int height = getHeight();
		int yText = height - MAJOR_TEXT_HEIGHT;
		g.setColor(MIDDLE_LINE_COLOR);

		// Set positions
		int positionStart = projectWindow.getGenomeWindow().getStart();
		int positionStop = projectWindow.getGenomeWindow().getStop();
		currentMiddlePosition = (positionStart + positionStop) / 2;
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			Chromosome currentChromosome = projectWindow.getGenomeWindow().getChromosome();
			String genomeName = FormattedMultiGenomeName.getFullNameWithoutAllele(MGDisplaySettings.SELECTED_GENOME);
			AlleleType inputAlleleType = FormattedMultiGenomeName.getAlleleName(MGDisplaySettings.SELECTED_GENOME);
			positionStart = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, inputAlleleType, positionStart, currentChromosome, genomeName);
			positionStop = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, inputAlleleType, positionStop, currentChromosome, genomeName);
			currentMiddlePosition = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, inputAlleleType, currentMiddlePosition, currentChromosome, genomeName);
		}

		// Draw units
		String stringToPrint = getFormattedNumber(positionStart);
		g.drawString(stringToPrint, 2, yText);
		stringToPrint = getFormattedNumber(currentMiddlePosition);
		g.drawString(stringToPrint, halfWidth + 3, yText);
		stringToPrint = getFormattedNumber(positionStop);
		g.drawString(stringToPrint, width - fm.stringWidth(stringToPrint) - 1, yText);
	}


	private String getFormattedNumber (int position) {
		if (position == -1000000000) {
			return "-";
		}
		return DF.format(position);
	}


	/**
	 * Draws the relative units.
	 * @param g {@link Graphics}
	 */
	private void drawRelativeUnits(Graphics g) {
		int height = getHeight();
		int positionStart = projectWindow.getGenomeWindow().getStart();
		int positionStop = projectWindow.getGenomeWindow().getStop();
		int y = height - MINOR_TEXT_HEIGHT;
		int lastTextStopPos = 0;
		double gap = getWidth() / (double)LINE_COUNT;
		for (int i = 0; i < LINE_COUNT; i++) {
			int x1 = (int)Math.round(i * gap);
			int x2 = (int)Math.round((((2 * i) + 1) * gap) / 2d);
			int distanceFromMiddle = (Math.abs(i - (LINE_COUNT / 2)) * (positionStop - positionStart)) / LINE_COUNT;
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


	/**
	 * @return the current middle position
	 */
	protected int getCurrentMiddlePosition () {
		return currentMiddlePosition;
	}
}
