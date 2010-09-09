/**
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.scatterPlot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;


/**
 * Axis of a scatter plot
 * @author Julien Lajugie
 * @version 0.1
 */
public class ScatterPlotAxis implements Serializable {
	
	private static final long serialVersionUID = 487345102079879893L;	// generated ID
	private static final int 	HALF_MAJOR_TICK_SIZE = 4;	// half size of the major ticks
	private static final int 	HALF_MINOR_TICK_SIZE = 2;	// half size of the minor ticks
	private double 				min;						// minimum of the axis 
	private double 				max;						// maximum of the axis
	private double 				majorUnit;					// major unit of the ticks
	private double 				minorUnit;					// minor unit of the ticks
	private boolean				showGrid = false;			// set to true to show the grid associated with this axis
	private boolean 			isLogScale = false;			// true if the axis is on a log scale
	private final boolean 		direction;					// direction of the axis
	private String 				name;						// name of the axis
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
	 * @param min minimum of the axis
	 * @param max maximum of the axis
	 * @param direction direction of the axis
	 * @param name name of the axis
	 */
	public ScatterPlotAxis(double dataMin, double dataMax, boolean direction, String name) {
		this.direction = direction;
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
		double position = 0;
		if (direction == HORIZONTAL) {
			int width = clip.width;
			int padX = clip.x;
			position = (dataValue - min) * width / (max - min) + padX;
		} else {
			int height = clip.height;
			int padY = clip.y;
			position = height - ((dataValue - min) * height / (max - min)) + padY;
		}
		return (int) position;
	}
	
	
	public void drawAxis(Graphics g, Rectangle clip) {
		if (direction == HORIZONTAL) {
			drawHorizontalAxis(g, clip);
		} else {
			drawVerticalAxis(g, clip);
		}
	}

	
	/**
	 * Draws the horizontal axis 
	 * @param g {@link Graphics} to draw the axis
	 * @param clip part of the {@link Graphics} where to draw the axis
	 */
	private void drawHorizontalAxis(Graphics g, Rectangle clip) {
		// the axis are black
		g.setColor(Color.BLACK);
		// compute the y0 position on the screen
		int y0Pos = clip.y + clip.height;
		// draw the axis line
		g.drawLine(clip.x, y0Pos, clip.width + clip.x, y0Pos);
//		// search the data value of the first tick
//		double firstTick = ((int) (min / minorUnit)) * minorUnit;
//		int lastTickScreenPos = dataValueToScreenPosition(firstTick, clip);
//		boolean isMajorTick = firstTick % majorUnit == 0;
//		g.drawLine(lastTickScreenPos, y0Pos - , lastTickScreenPos, y2);
//		for (double tick = firstTick; tick < max; tick += minorUnit) {
//			if (tick != firstTick) {
//				if () {
//					
//				}
//			} else {
//				
//			}
//		}
	}
	
	
	/**
	 * Draws the vertical axis 
	 * @param g {@link Graphics} to draw the axis
	 * @param clip part of the {@link Graphics} where to draw the axis
	 */
	private void drawVerticalAxis(Graphics g, Rectangle clip) {
		// the axis are black
		g.setColor(Color.BLACK);
		// compute the x0 position on the screen
		int x0Pos = clip.x;
		// draw the axis line
		g.drawLine(x0Pos, clip.y, x0Pos, clip.y + clip.height);		
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
			while (dataMax / maxTmp >= 10) {
				maxTmp *= 10;
			}
			max = maxTmp;
			while (max < dataMax) {
				max += dataMax;
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
			while (dataMin / minTmp >= 10) {
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
		while (range / majorUnit >= 100) {
			majorUnit *= 10;
		}
		minorUnit = majorUnit / 10;
	}

	
	/**
	 * @return the direction of the axis
	 */
	public final boolean getDirection() {
		return direction;
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
	 * @param showGrid show the grid if the parameter's value is true
	 */
	public final void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}
}
