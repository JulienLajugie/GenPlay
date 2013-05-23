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
package edu.yu.einstein.genplay.gui.customComponent.scatterPlot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;

import edu.yu.einstein.genplay.dataStructure.enums.LogBase;
import edu.yu.einstein.genplay.util.NumberFormats;
import edu.yu.einstein.genplay.util.Utils;



/**
 * Axis of a scatter plot
 * @author Julien Lajugie
 * @version 0.1
 */
public class ScatterPlotAxis implements Serializable {

	private static final long serialVersionUID = 487345102079879893L;	// generated ID
	private static final int 	HALF_MAJOR_TICK_SIZE = 4;	// half size of the major ticks
	private static final int 	HALF_MINOR_TICK_SIZE = 2;	// half size of the minor ticks
	private final boolean 		orientation;				// orientation of the axis
	private double 				min;						// minimum of the axis
	private double 				max;						// maximum of the axis
	private double 				majorUnit;					// major unit of the ticks
	private double 				minorUnit;					// minor unit of the ticks
	private boolean				showGrid = true;			// set to true to show the grid associated with this axis
	private boolean 			isLogScale = false;			// true if the axis is on a log scale
	private LogBase				logBase = LogBase.BASE_10;	// the base of the log scale
	private String 				name;						// name of the axis
	private int 				position;					// position of the axis (y position for an horizontal axis and x position for a vertical one)

	/**
	 * Horizontal axis
	 */
	public static final boolean HORIZONTAL = true;
	/**
	 * Vertical axis
	 */
	public static final boolean VERTICAL = false;


	/**
	 * Creates an instance of a {@link ScatterPlotAxis}
	 * @param dataMin minimum of the axis
	 * @param dataMax maximum of the axis
	 * @param orientation orientation of the axis
	 * @param name name of the axis
	 */
	public ScatterPlotAxis(double dataMin, double dataMax, boolean orientation, String name) {
		this.orientation = orientation;
		this.name = name;
		findDefaultMin(dataMin);
		findDefaultMax(dataMax);
		findDefaultUnits();
	}


	/**
	 * Translates a  data value to its position on the screen
	 * @param dataValue a value
	 * @param clip area where the graph is plotted
	 * @return the position on the screen on the axis
	 */
	protected int dataValueToScreenPosition(double dataValue, Rectangle clip) {
		double max = this.max;
		if (isLogScale) {
			if (this.max > 0) {
				max = Utils.log(logBase, this.max);
			}
			if (dataValue <= 0) {
				dataValue = 0;
			} else {
				dataValue = Utils.log(logBase, dataValue);
			}
		}

		double position = 0;
		if (orientation == HORIZONTAL) {
			int padX = clip.x;
			int width = clip.width;
			if (min == max) {
				position = padX;
			} else {
				position = (((dataValue - min) * width) / (max - min)) + padX;
			}
		} else {
			int height = clip.height;
			int padY = clip.y;
			if (min == max) {
				position = padY;
			} else {
				position = (height - (((dataValue - min) * height) / (max - min))) + padY;
			}
		}
		return (int) position;
	}


	/**
	 * Draws the axis
	 * @param g {@link Graphics}
	 * @param clip rectangle where the chart is printed
	 */
	protected void drawAxis(Graphics g, Rectangle clip) {
		// the axis are black
		g.setColor(Color.BLACK);
		// draw the axis line
		if (orientation == HORIZONTAL) {
			g.drawLine(clip.x, position, clip.width + clip.x, position);
		} else {
			g.drawLine(position, clip.y, position, clip.y + clip.height);
		}
	}


	/**
	 * Draws the grid if the showGrid option is set to true
	 * @param g {@link Graphics}
	 * @param clip rectangle where the chart is printed
	 */
	protected void drawGrid(Graphics g, Rectangle clip) {
		if (showGrid) {
			g.setColor(Color.LIGHT_GRAY);
			double firstLine = ((int) (min / majorUnit)) * majorUnit;
			for (double i = firstLine; i <= max; i += majorUnit) {
				int pos = dataValueToScreenPosition(i, clip);
				if (orientation == HORIZONTAL) {
					g.drawLine(pos, clip.y, pos, clip.y + clip.height);
				} else {
					g.drawLine(clip.x, pos, clip.width + clip.x, pos);
				}
			}
		}
	}


	/**
	 * Draws the major ticks with the numbers
	 * @param g {@link Graphics}
	 * @param clip rectangle where the chart is printed
	 */
	protected void drawMajorUnit(Graphics g, Rectangle clip) {
		g.setColor(Color.BLACK);
		double firstTick = ((int) (min / majorUnit)) * majorUnit;
		Integer lastLabelPosition = null;
		// height of the font
		int labelHeight = g.getFontMetrics().getHeight();
		for (double i = firstTick; i <= max; i += majorUnit) {
			int pos = dataValueToScreenPosition(i, clip);
			String label = NumberFormats.getScoreFormat().format(i);
			int labelWidth = g.getFontMetrics().stringWidth(label);
			if (orientation == HORIZONTAL) {
				g.drawLine(pos, position - HALF_MAJOR_TICK_SIZE, pos, position + HALF_MAJOR_TICK_SIZE);
				// compute the positions of the label
				int labelXPosition = pos;
				int labelYPosition = clip.y + clip.height + HALF_MAJOR_TICK_SIZE + labelHeight;
				// draw the label only if there is enough room
				if ((lastLabelPosition == null) || ((lastLabelPosition + labelWidth) < labelXPosition)) {
					g.drawString(label, labelXPosition, labelYPosition);
					lastLabelPosition = labelXPosition;
				}
			} else {
				g.drawLine(position - HALF_MAJOR_TICK_SIZE, pos, position + HALF_MAJOR_TICK_SIZE, pos);
				// compute the positions of the label
				int labelXPosition = clip.x - HALF_MAJOR_TICK_SIZE - labelWidth;
				int labelYPosition = (int) (pos + (labelHeight / (double) 4));
				// draw the label only if there is enough room
				if ((lastLabelPosition == null) || ((lastLabelPosition - labelHeight) > labelYPosition)) {
					g.drawString(label, labelXPosition, labelYPosition);
					lastLabelPosition = labelYPosition;
				}
			}
		}
	}


	/**
	 * Draws the minor ticks
	 * @param g {@link Graphics}
	 * @param clip rectangle where the chart is printed
	 */
	protected void drawMinorUnit(Graphics g, Rectangle clip) {
		g.setColor(Color.DARK_GRAY);
		double firstTick = ((int) (min / minorUnit)) * minorUnit;
		for (double i = firstTick; i <= max; i += minorUnit) {
			int pos = dataValueToScreenPosition(i, clip);
			if (orientation == HORIZONTAL) {
				g.drawLine(pos, position - HALF_MINOR_TICK_SIZE, pos, position + HALF_MINOR_TICK_SIZE);
			} else {
				g.drawLine(position - HALF_MINOR_TICK_SIZE, pos, position + HALF_MINOR_TICK_SIZE, pos);
			}
		}
	}


	/**
	 * Finds and sets the default maximum of the axis considering the maximum value of the data
	 * @param dataMax maximum value of the data
	 */
	private void findDefaultMax(double dataMax) {
		// if there is no positive values the minimum is 0
		if (dataMax <= 0) {
			max = 0;
		} else {
			int maxTmp = 1;
			while ((dataMax / maxTmp) >= 10) {
				maxTmp *= 10;
			}
			max = maxTmp;
			while (max < dataMax) {
				max += maxTmp;
			}
		}
	}


	/**
	 * Finds and sets the default minimum of the axis considering the minimum value of the data
	 * @param dataMin minimum value of the data
	 */
	private void findDefaultMin(double dataMin) {
		if (dataMin >= 0) {
			// if there is no negative values the maximum is 0
			min = 0;
		} else {
			int minTmp = -1;
			while ((dataMin / minTmp) >= 10) {
				minTmp *= 10;
			}
			min = minTmp;
			while (min > dataMin) {
				min += minTmp;
			}
		}
	}


	/**
	 * Finds and sets the default major and minor units
	 */
	private void findDefaultUnits() {
		majorUnit = 1;
		double range = max - min;
		while ((range / majorUnit) >= 100) {
			majorUnit *= 10;
		}
		minorUnit = majorUnit / 2;
	}


	/**
	 * @return the logBase
	 */
	public final LogBase getLogBase() {
		return logBase;
	}


	/**
	 * @return the majorUnit of the ticks
	 */
	public final double getMajorUnit() {
		return majorUnit;
	}


	/**
	 * @return the maximum of the axis
	 */
	public final double getMax() {
		return max;
	}


	/**
	 * @return the minimum of the axis
	 */
	public final double getMin() {
		return min;
	}


	/**
	 * @return the minorUnit of the ticks
	 */
	public final double getMinorUnit() {
		return minorUnit;
	}


	/**
	 * @return the name of the axis
	 */
	public final String getName() {
		return name;
	}


	/**
	 * @return the orientation of the axis
	 */
	public final boolean getOrientation() {
		return orientation;
	}


	/**
	 * @return the position of the axis (y position for an horizontal axis and x position for a vertical one)
	 */
	public final int getPosition() {
		return position;
	}


	/**
	 * @return the true if the axis is in a log scale
	 */
	public final boolean isLogScale() {
		return isLogScale;
	}


	/**
	 * @return the true if the grid needs to be shown
	 */
	public final boolean isShowGrid() {
		return showGrid;
	}


	/**
	 * @param logBase the logBase to set
	 */
	public final void setLogBase(LogBase logBase) {
		this.logBase = logBase;
	}


	/**
	 * @param isLogScale the isLogScale to set
	 */
	public final void setLogScale(boolean isLogScale) {
		this.isLogScale = isLogScale;
	}


	/**
	 * @param majorUnit the majorUnit of the ticks
	 */
	public final void setMajorUnit(double majorUnit) {
		this.majorUnit = majorUnit;
	}


	/**
	 * @param max the maximum of the axis
	 */
	public final void setMax(double max) {
		this.max = max;
	}


	/**
	 * @param min the minimum of the axis
	 */
	public final void setMin(double min) {
		this.min = min;
	}


	/**
	 * @param minorUnit the minorUnit of the ticks
	 */
	public final void setMinorUnit(double minorUnit) {
		this.minorUnit = minorUnit;
	}


	/**
	 * @param name the name of the axis
	 */
	public final void setName(String name) {
		this.name = name;
	}


	/**
	 * @param position the position of the axis to set (y position for an horizontal axis and x position for a vertical one)
	 */
	public final void setPosition(int position) {
		this.position = position;
	}


	/**
	 * @param showGrid show the grid if the parameter's value is true
	 */
	public final void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}
}
