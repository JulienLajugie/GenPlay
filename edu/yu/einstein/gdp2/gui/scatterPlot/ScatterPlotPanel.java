/**
 * @author Chirag Gorasia
 * @version 0.1
 */

package yu.einstein.gdp2.gui.scatterPlot;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import yu.einstein.gdp2.gui.popupMenu.ScatterPlotRightClickedMenu;

/**
 * Class to draw a scatter plot of the given data
 * @author Chirag
 *
 */

public class ScatterPlotPanel extends JPanel implements MouseMotionListener, MouseListener {
	
	private static final long serialVersionUID = 2641451624657624826L;	// generated serial ID
	
	private static double xMin;							// x-axis min value
	private static double yMin;							// y-axis min value
	private static double xMax;							// x-axis max value	
	private static double yMax;							// y-axis max value
	
	private static final int LEFT_PAD = 100;			// Left side margin
	private static final int RIGHT_PAD = 200;			// Right side margin
	private static final int RIGHT_PAD_LABELS = 100;	// Right Pad to accomodate x-axis labels
	private static final int TOP_PAD = 200;				// Top margin
	private static final int BOTTOM_PAD = 100;			// Bottom margin
	private static final int TOP_PAD_LABELS = 100;		// Top Pad to accomodate y-axis labels
	
	private static double yAxisStepSize;				// y-axis tick size
	private static double xAxisStepSize;				// x-axis tick size
	
	private List<ScatterPlotData> listOfGraphs = null;	// List of graphs to be plotted
	private Color[] graphColor;							// Color of the graph
	private Random randomGen;
	private static final DecimalFormat 	DF = new DecimalFormat("###,###,###.##");	// decimal format
		
			
	public ScatterPlotPanel(List<ScatterPlotData> listOfGraphs) {
		this.listOfGraphs = listOfGraphs;
		graphColor = new Color[listOfGraphs.size()];
		randomGen = new Random();
		for (int i = 0; i < graphColor.length; i++) {			
			int red = randomGen.nextInt(255);
			int green = randomGen.nextInt(255);
			int blue = randomGen.nextInt(255);
			graphColor[i] = new Color(red, green, blue);
		}		
		findDefaultAxisBounds();
		setBackground(Color.white);
		setPreferredSize(new Dimension(500, 500));
		setMinimumSize(new Dimension(500, 500));
		setVisible(true);
		setBorder(BorderFactory.createEmptyBorder(TOP_PAD, RIGHT_PAD, TOP_PAD_LABELS, RIGHT_PAD_LABELS));
		addMouseMotionListener(this);
		addMouseListener(this);
	}
	
	/** 
	 * Method to set the X-axis max and min value
	 */
	protected static void setXAxis(double xMin, double xMax) {
		ScatterPlotPanel.xMin = xMin;
		ScatterPlotPanel.xMax = xMax;
		//System.out.println("ScatterPlotPanel.xMin: " + ScatterPlotPanel.xMin + " ScatterPlotPanel.xMax: " + ScatterPlotPanel.xMax);
	}	
	
	/** 
	 * Method to set the Y-axis max and min value
	 */
	protected static void setYAxis(double yMin, double yMax) {
		ScatterPlotPanel.yMin = yMin;
		ScatterPlotPanel.yMax = yMax;
		//System.out.println("ScatterPlotPanel.yMin: " + ScatterPlotPanel.yMin + " ScatterPlotPanel.yMax: " + ScatterPlotPanel.yMax);
	}
	
	/** 
	 * Method to set the Y-axis tick size
	 */
	protected static void setYAxisStepSize(double yAxisStepSize) {
		ScatterPlotPanel.yAxisStepSize = yAxisStepSize;
		//System.out.println("ScatterPlotPanel.yAxisStepSize: " + ScatterPlotPanel.yAxisStepSize);
	}
	
	/** 
	 * Method that returns the Y-axis tick size
	 */
	protected static double getYAxisStepSize() {
		return yAxisStepSize;
	}
	
	/** 
	 * Method to set the X-axis tick size
	 */
	protected static void setXAxisStepSize(double xAxisStepSize) {
		ScatterPlotPanel.xAxisStepSize = xAxisStepSize;
	}
	
	/** 
	 * Method that returns the X-axis tick size
	 */
	protected static double getXAxisStepSize() {
		return xAxisStepSize;
	}
	
	/** 
	 * Method that returns the X-axis max value
	 */
	protected static double getXAxisEnd() {
		return xMax;
	}
	
	/** 
	 * Method that returns the X-axis min value
	 */
	protected static double getXAxisStart() {
		return xMin;
	}
	
	/** 
	 * Method that returns the Y-axis min value
	 */
	protected static double getYAxisStart() {
		return yMin;
	}
	
	/** 
	 * Method that returns the Y-axis max value
	 */
	protected static double getYAxisEnd() {
		return yMax;
	}
	
	/**
	 * Method to draw the Legend for the scatter plot
	 * @param g (Graphics)
	 */
	protected void drawLegend(Graphics g) {
		Point p = getTranslatedPoint(getXAxisEnd()-10, getYAxisEnd()-10);
		for (int i = 0; i < listOfGraphs.size(); i++) {
			g.setColor(graphColor[i]);
			g.drawString("     -------", p.x, p.y+i*10);
			g.setColor(Color.black);
			g.drawString(listOfGraphs.get(i).getGraphName(), p.x+50, p.y+i*10);
		}
	}
	
	/**
	 * Method to translate the co-ordinates of the data point to the point on screen
	 * @param x (double)
	 * @param y (double)
	 * @return Point
	 */
	protected Point getTranslatedPoint(double x, double y) {
		Point translatedPoint = new Point();
		double realWidth = getWidth() - LEFT_PAD - RIGHT_PAD;
		double realHeigth = getHeight() - TOP_PAD - BOTTOM_PAD;		
		translatedPoint.x = (int) ((realWidth * (x - getXAxisStart())) / (getXAxisEnd() - getXAxisStart())) + LEFT_PAD;
		translatedPoint.y = getHeight() - (int) ((realHeigth * (y - getYAxisStart())) / (getYAxisEnd() - getYAxisStart())) - TOP_PAD;		
		return translatedPoint;
	}
	
	/**
	 * Method that returns the original data Point
	 * @param p (Point)
	 * @return Point
	 */
	protected Point getOriginalPoint(Point p) {
		Point retPoint = new Point();
		retPoint.x = (int) (((p.x - LEFT_PAD) * (getXAxisEnd() - getXAxisStart()) / (getWidth() - LEFT_PAD - RIGHT_PAD)) + getXAxisStart());
		retPoint.y = (int) (-1 * ((p.y - getHeight() + TOP_PAD) * (getYAxisEnd() - getYAxisStart()) / (getHeight() - TOP_PAD - BOTTOM_PAD)) + getYAxisStart());
		return retPoint;
	}
	
	/**
	 * Method to draw the x and y axes for the plot
	 * @param g (Graphics)
	 */
	protected void drawAxes(Graphics g) {
		g.drawRect(1, 1, getWidth()-3, getHeight()-3);
		double minXPoint = getXAxisStart() < 0 ? 0 : getXAxisStart();
		Point yAxisStart = getTranslatedPoint(minXPoint, getYAxisStart());
		Point yAxisEnds = getTranslatedPoint(minXPoint, getYAxisEnd());
		g.drawLine(yAxisStart.x, yAxisStart.y, yAxisEnds.x, yAxisEnds.y);
		
		Point xAxisStart = getTranslatedPoint(getXAxisStart(), getYAxisStart());
		Point xAxisStop = getTranslatedPoint(getXAxisEnd(), getYAxisStart());
		g.drawLine(xAxisStart.x, xAxisStart.y, xAxisStop.x, xAxisStop.y);
		Point p;
		
		int lastXTextStopPos = 0, lastYTextStopPos = Integer.MAX_VALUE;
		double incrementY = getYAxisStart();
		double incrementX = 0;
		
			// X-Axis Labels
			if (getXAxisStart() < 0) {
				lastXTextStopPos = Integer.MAX_VALUE;
				while (incrementX >= getXAxisStart()) {
					p = new Point(getTranslatedPoint(incrementX, getYAxisStart() - 10));
					String stringToPrint = DF.format(incrementX);
					if (p.x <= lastXTextStopPos) {
						g.drawString(stringToPrint, p.x, p.y + TOP_PAD_LABELS/5);
						g.drawString("I", p.x, p.y + TOP_PAD_LABELS/20);
						lastXTextStopPos = p.x + g.getFontMetrics().stringWidth(stringToPrint)+10;
					}					
					incrementX = incrementX - getXAxisStepSize();
				}
			}
			
			incrementX = 0;
			lastXTextStopPos = 0;
			while (incrementX <= getXAxisEnd()) {
				p = new Point(getTranslatedPoint(incrementX, getYAxisStart() - 10));
				String stringToPrint = DF.format(incrementX);
				if (p.x >= lastXTextStopPos && incrementX >= getXAxisStart()) {
					g.drawString(stringToPrint, p.x, p.y + TOP_PAD_LABELS/5);
					g.drawString("I", p.x, p.y + TOP_PAD_LABELS/20);
					lastXTextStopPos = p.x + g.getFontMetrics().stringWidth(stringToPrint)+10;
				}					
				incrementX = incrementX + getXAxisStepSize();
			}
			// Y-Axis Labels
			if (incrementY > getYAxisEnd()) {
				incrementY = getXAxisEnd()/2;
			}
			while (incrementY <= getYAxisEnd()) {
				p = new Point(getTranslatedPoint(minXPoint,incrementY));
				String stringToPrint = DF.format(incrementY);
				int stringWidth = g.getFontMetrics().stringWidth(stringToPrint);
				if (p.y <= lastYTextStopPos) {
					g.drawString(stringToPrint, p.x - stringWidth - 10, p.y);
					g.drawLine(p.x-4, p.y, p.x+4, p.y);
					lastYTextStopPos = p.y - g.getFontMetrics().getHeight();
				}					
				incrementY = incrementY + getYAxisStepSize();					
			}			
		}
	
	/**
	 * Method to plot the data points
	 * @param g (Graphics)
	 */
	protected void plotPoints(Graphics g) {
		Point current, next;
		for (int i = 0; i < listOfGraphs.size(); i++) {
			g.setColor(graphColor[i]);
			for (int j = 0; j < listOfGraphs.get(i).getDataPoints().length-1; j++) {
				current = new Point(getTranslatedPoint(listOfGraphs.get(i).getDataPoints()[j][0], listOfGraphs.get(i).getDataPoints()[j][1]));
				next = new Point(getTranslatedPoint(listOfGraphs.get(i).getDataPoints()[j+1][0], listOfGraphs.get(i).getDataPoints()[j][1]));
				if (listOfGraphs.get(i).getDataPoints()[j][0] <= getXAxisEnd() && listOfGraphs.get(i).getDataPoints()[j][1] <= getYAxisEnd() && listOfGraphs.get(i).getDataPoints()[j][1] >= getYAxisStart() && listOfGraphs.get(i).getDataPoints()[j][0] >= getXAxisStart()) {
					//System.out.println("current.x: " + current.x + " current.y: " + current.y + " next.x: " + next.x + " next.y: " + next.y);
					g.drawLine(current.x, current.y, next.x, next.y);
				}
			}
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
        setBackground(Color.white);	
		drawAxes(g);
		plotPoints(g);
		drawLegend(g);
	}
	
	/**
	 * Private method to find the default bounds for the axes for the initial plot
	 */
	private void findDefaultAxisBounds() {
		double xmaxOfMax, ymaxOfMax;
		double xminOfMin, yminOfMin;
		xminOfMin = listOfGraphs.get(0).getDataPoints()[0][0];
		yminOfMin = listOfGraphs.get(0).getDataPoints()[0][1];
		xmaxOfMax = listOfGraphs.get(0).getDataPoints()[0][0];
		ymaxOfMax = listOfGraphs.get(0).getDataPoints()[0][1];
		for (int i = 0; i < listOfGraphs.size(); i++) {
			for (int j = 0; j < listOfGraphs.get(i).getDataPoints().length; j++) {				
				if (xminOfMin > listOfGraphs.get(i).getDataPoints()[j][0])
					xminOfMin = listOfGraphs.get(i).getDataPoints()[j][0];
				if (yminOfMin > listOfGraphs.get(i).getDataPoints()[j][1])
					yminOfMin = listOfGraphs.get(i).getDataPoints()[j][1];
				if (xmaxOfMax < listOfGraphs.get(i).getDataPoints()[j][0])
					xmaxOfMax = listOfGraphs.get(i).getDataPoints()[j][0];
				if (ymaxOfMax < listOfGraphs.get(i).getDataPoints()[j][1])
					ymaxOfMax = listOfGraphs.get(i).getDataPoints()[j][1];
			}
		}
		int magnitudeCounter = 0;
		int temp = (int)ymaxOfMax;
		while (temp > 0) {
			temp /= 10;
			magnitudeCounter++;
		}
		setYAxisStepSize(Math.pow(10,magnitudeCounter-1));
		setXAxisStepSize(10);
		setXAxis(xminOfMin, xmaxOfMax + getXAxisStepSize());
		setYAxis(yminOfMin, ymaxOfMax + getYAxisStepSize());
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		Point p = new Point();
		p = getOriginalPoint(e.getPoint());
		this.setToolTipText("(" + p.x + "," + p.y + ")");
		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {		
		if (e.getButton() == 1) {
			@SuppressWarnings("unused")
			AxisOption axisOption = null;
			Point p = getTranslatedPoint(xMin, 0d);
			if (e.getX() <= 100 && e.getX() >= 50 && e.getY() >= TOP_PAD_LABELS && e.getY() <= getHeight() - TOP_PAD_LABELS - BOTTOM_PAD && (e.getClickCount() == 2)) {
				axisOption = new AxisOption("Y-Axis");
				repaint();
			}		
			if (e.getX() >= 100 && e.getX() <= getWidth() -  RIGHT_PAD && e.getY() >= p.y && e.getY() <= p.y + 50 && (e.getClickCount() == 2)) {
				axisOption = new AxisOption("X-Axis");
				repaint();
			}
		} else if (e.getButton() == 3){
			ScatterPlotRightClickedMenu sprc = new ScatterPlotRightClickedMenu(this);
			sprc.show(this, e.getX(), e.getY());
		}
			
	}

	@Override
	public void mouseDragged(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}	
}
