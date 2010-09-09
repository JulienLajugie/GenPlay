/**
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.scatterPlot;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import yu.einstein.gdp2.gui.popupMenu.ScatterPlotRightClickedMenu;

/**
 * Class to draw a scatter plot of the given data
 * @author Chirag Gorasia
 *
 */
public class ScatterPlotPanel extends JPanel implements MouseMotionListener, MouseListener, ChangeListener {

	private static final long serialVersionUID = 2641451624657624826L;	// generated serial ID

	private static double xMin;							// x-axis min value
	private static double yMin;							// y-axis min value
	private static double xMax;							// x-axis max value	
	private static double yMax;							// y-axis max value
	private static String xAxisName;					// x-axis name
	private static String yAxisName;					// y-axis name

	private static final int PREF_WIN_WIDTH = 1000;
	private static final int PREF_WIN_HEIGHT = 700;
	private static final int MIN_WIN_WIDTH = 700;
	private static final int MIN_WIN_HEIGHT = 500;

	private static final int LEFT_PAD = 100;			// Left side margin
	private static final int RIGHT_PAD_LABELS = 100;	// Right Pad to accomodate x-axis labels
	private static final int BOTTOM_PAD = 100;			// Bottom margin
	private static final int TOP_PAD_LABELS = 100;		// Top Pad to accomodate y-axis labels

	private static double yAxisStepSize;				// y-axis tick size
	private static double xAxisStepSize;				// x-axis tick size

	private static boolean xAxisGridLines;				// x-axis grid lines flag
	private static boolean yAxisGridLines;				// y-axis grid lines flag

	private static boolean barGraph;
	private static boolean curve;	
	private static boolean changeColors;

	private static List<ScatterPlotData> listOfGraphs = null;	// List of graphs to be plotted
	private static Color[] graphColor;							// Color of the graph
	private Random randomGen;		
	private static final DecimalFormat 	DF = new DecimalFormat("###,###,###.##");	// decimal format
	private static String[] graphNames;


	/**
	 * @return the graphColors
	 */
	public static Color[] getGraphColors() {
		return graphColor;
	}

	/** 
	 * Method that returns the graph data
	 * @return the graph data to return
	 */
	public static List<ScatterPlotData> getGraphList() {
		return listOfGraphs;
	}	

	/**
	 * @return the graphNames
	 */
	public static String[] getGraphNames() {
		graphNames = new String[listOfGraphs.size()];
		for (int i = 0; i < listOfGraphs.size(); i++) {
			graphNames[i] = listOfGraphs.get(i).getName();
		}
		return graphNames;
	}

	/** 
	 * Method that returns the X-axis max value
	 */
	protected static double getXAxisEnd() {
		return xMax;
	}

	/**
	 * @return the xAxisName
	 */
	public static String getxAxisName() {
		return xAxisName;
	}

	/** 
	 * Method that returns the X-axis min value
	 */
	protected static double getXAxisStart() {
		return xMin;
	}

	/** 
	 * Method that returns the X-axis tick size
	 */
	protected static double getXAxisStepSize() {
		return xAxisStepSize;
	}

	/** 
	 * Method that returns the Y-axis max value
	 */
	protected static double getYAxisEnd() {
		return yMax;
	}

	/**
	 * @return the yAxisName
	 */
	public static String getyAxisName() {
		return yAxisName;
	}

	/** 
	 * Method that returns the Y-axis min value
	 */
	protected static double getYAxisStart() {
		return yMin;
	}

	/** 
	 * Method that returns the Y-axis tick size
	 */
	protected static double getYAxisStepSize() {
		return yAxisStepSize;
	}

	/**
	 * @return the barGraph
	 */
	public static boolean isBarGraph() {
		return barGraph;
	}

	/**
	 * @return the changeColors
	 */
	public static boolean isChangeColors() {
		return changeColors;
	}	

	/**
	 * @return the curve
	 */
	public static boolean isCurve() {
		return curve;
	}

	/**
	 * @return the xAxisGridLines
	 */
	public static boolean isxAxisGridLines() {
		return xAxisGridLines;
	}	

	/**
	 * @return the yAxisGridLines
	 */
	public static boolean isyAxisGridLines() {
		return yAxisGridLines;
	}

	/**
	 * @param barGraph the barGraph to set
	 */
	public static void setBarGraph(boolean barGraph) {
		ScatterPlotPanel.barGraph = barGraph;
	}	

	/**
	 * @param changeColors the changeColors to set
	 */
	public static void setChangeColors(boolean changeColors) {
		ScatterPlotPanel.changeColors = changeColors;
	}

	/**
	 * @param curve the curve to set
	 */
	public static void setCurve(boolean curve) {
		ScatterPlotPanel.curve = curve;
	}	

	/**
	 * @param color the color to set
	 * @param index the index whose color is to be set
	 */
	public static void setGraphColors(Color color, int index) {
		graphColor[index] = color;
	}

	/** 
	 * Method to set the X-axis max and min value
	 */
	protected static void setXAxis(double xMin, double xMax) {
		ScatterPlotPanel.xMin = xMin;
		ScatterPlotPanel.xMax = xMax;
	}

	/**
	 * @param xAxisGridLines the xAxisGridLines to set
	 */
	public static void setxAxisGridLines(boolean xAxisGridLines) {
		ScatterPlotPanel.xAxisGridLines = xAxisGridLines;
	}

	/**
	 * @param xAxisName the xAxisName to set
	 */
	public static void setxAxisName(String xAxisName) {
		ScatterPlotPanel.xAxisName = xAxisName;
	}

	/** 
	 * Method to set the X-axis tick size
	 */
	protected static void setXAxisStepSize(double xAxisStepSize) {
		ScatterPlotPanel.xAxisStepSize = xAxisStepSize;
	}

	/** 
	 * Method to set the Y-axis max and min value
	 */
	protected static void setYAxis(double yMin, double yMax) {
		ScatterPlotPanel.yMin = yMin;
		ScatterPlotPanel.yMax = yMax;
	}	

	/**
	 * @param yAxisGridLines the yAxisGridLines to set
	 */
	public static void setyAxisGridLines(boolean yAxisGridLines) {
		ScatterPlotPanel.yAxisGridLines = yAxisGridLines;		
	}	

	/**
	 * @param yAxisName the yAxisName to set
	 */
	public static void setyAxisName(String yAxisName) {
		ScatterPlotPanel.yAxisName = yAxisName;
	}

	/** 
	 * Method to set the Y-axis tick size
	 */
	protected static void setYAxisStepSize(double yAxisStepSize) {
		ScatterPlotPanel.yAxisStepSize = yAxisStepSize;
	}

	public static void showDialog(Component parent, final List<ScatterPlotData> listOfGraphs) {
		final JDialog jf = new JDialog();
		jf.setModal(true);
		jf.setTitle("Scatter Plot");		
		ScatterPlotPanel scatPlotPanel = new ScatterPlotPanel(listOfGraphs);
		jf.setContentPane(scatPlotPanel);
		jf.setPreferredSize(new Dimension(PREF_WIN_WIDTH, PREF_WIN_HEIGHT));
		jf.setMinimumSize(new Dimension(MIN_WIN_WIDTH, MIN_WIN_HEIGHT));
		jf.pack();
		jf.setLocationRelativeTo(parent);		
		jf.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ScatterPlotPanel.listOfGraphs.clear();	
				jf.dispose();
			}			
		});
		jf.setVisible(true);
	}

	private ScatterPlotPanel(List<ScatterPlotData> listOfGraphs) {
		ScatterPlotPanel.listOfGraphs = listOfGraphs;
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
		setMinimumSize(new Dimension(400, 400));
		setVisible(true);
		//setBorder(BorderFactory.createEmptyBorder(TOP_PAD, RIGHT_PAD, BOTTOM_PAD, RIGHT_PAD_LABELS));
		addMouseMotionListener(this);
		addMouseListener(this);		
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
		double incrementX = getXAxisStart();
		boolean axisFlag = true;

		// X-Axis Labels
		if (getXAxisStart() < 0) {
			lastXTextStopPos = Integer.MIN_VALUE;
			while (incrementX >= getXAxisStart() && incrementX < -10) {
				p = new Point(getTranslatedPoint(incrementX, getYAxisStart()));
				String stringToPrint = DF.format(incrementX);
				if (p.x >= lastXTextStopPos && incrementX >= getXAxisStart()) {
					if (isxAxisGridLines() == true && axisFlag != true) {
						g.setColor(new Color (190,190,190));
						g.drawLine(p.x, p.y, p.x, getTranslatedPoint(0,getYAxisEnd()).y);
					}
					g.setColor(Color.black);
					g.drawString(stringToPrint, p.x, p.y + TOP_PAD_LABELS/5);
					g.drawLine(p.x, p.y-4, p.x, p.y+4);
					lastXTextStopPos = p.x + g.getFontMetrics().stringWidth(stringToPrint)+10;
				}					
				incrementX = incrementX + getXAxisStepSize();
				axisFlag = false;
			}
		}

		axisFlag = true;
		incrementX = 0;
		lastXTextStopPos = 0;
		while (incrementX <= getXAxisEnd()) {
			p = new Point(getTranslatedPoint(incrementX, getYAxisStart()));
			String stringToPrint = DF.format(incrementX);
			if (p.x >= lastXTextStopPos && incrementX >= getXAxisStart()) {
				if (isxAxisGridLines() == true && axisFlag != true) {
					g.setColor(new Color (190,190,190));
					g.drawLine(p.x, p.y, p.x, getTranslatedPoint(0,getYAxisEnd()).y);
				}
				g.setColor(Color.black);
				g.drawString(stringToPrint, p.x, p.y + TOP_PAD_LABELS/5);
				g.drawLine(p.x, p.y-4, p.x, p.y+4);
				lastXTextStopPos = p.x + g.getFontMetrics().stringWidth(stringToPrint)+10;					
			}					
			incrementX = incrementX + getXAxisStepSize();
			axisFlag = false;
		}
		// Y-Axis Labels
		axisFlag = true;
		if (incrementY > getYAxisEnd()) {
			incrementY = getXAxisEnd()/2;
		}
		while (incrementY <= getYAxisEnd()) {
			p = new Point(getTranslatedPoint(minXPoint,incrementY));
			String stringToPrint = DF.format(incrementY);
			int stringWidth = g.getFontMetrics().stringWidth(stringToPrint);
			if (p.y <= lastYTextStopPos  && (!stringToPrint.equals("0"))) {
				if (isyAxisGridLines() == true && axisFlag != true) {
					g.setColor(new Color (190,190,190));
					g.drawLine(getTranslatedPoint(getXAxisStart(),0).x, p.y, getTranslatedPoint(getXAxisEnd(),0).x, p.y);
				}
				g.setColor(Color.black);
				g.drawString(stringToPrint, p.x - stringWidth - 10, p.y);
				g.drawLine(p.x-4, p.y, p.x+4, p.y);
				lastYTextStopPos = p.y - g.getFontMetrics().getHeight();					
			}					
			incrementY = incrementY + getYAxisStepSize();	
			axisFlag = false;
		}			
	}

	/**
	 * Method to draw a bar graph for the data points
	 * @param g (Graphics)
	 */
	protected void drawBarGraph(Graphics g) {
		Point current, next;
		for (int i = 0; i < listOfGraphs.size(); i++) {
			g.setColor(graphColor[i]);
			for (int j = 0; j < listOfGraphs.get(i).getData().length-1; j++) {
				current = new Point(getTranslatedPoint(listOfGraphs.get(i).getData()[j][0], listOfGraphs.get(i).getData()[j][1]));
				next = new Point(getTranslatedPoint(listOfGraphs.get(i).getData()[j+1][0], listOfGraphs.get(i).getData()[j][1]));
				if (listOfGraphs.get(i).getData()[j][0] <= getXAxisEnd() && listOfGraphs.get(i).getData()[j][1] <= getYAxisEnd() && listOfGraphs.get(i).getData()[j][1] >= getYAxisStart() && listOfGraphs.get(i).getData()[j][0] >= getXAxisStart() && Math.abs(listOfGraphs.get(i).getData()[j][0]-listOfGraphs.get(i).getData()[j+1][0]) <= getXAxisStepSize()) {
					g.drawLine(current.x, current.y, next.x, next.y);
					g.drawLine(current.x, current.y, current.x, getTranslatedPoint(current.x, 0).y);
					g.drawLine(next.x, next.y, next.x, getTranslatedPoint(current.x, 0).y);
				} else if (listOfGraphs.get(i).getData()[j][0] <= getXAxisEnd() && listOfGraphs.get(i).getData()[j][1] > getYAxisEnd() && listOfGraphs.get(i).getData()[j][1] >= getYAxisStart() && listOfGraphs.get(i).getData()[j][0] >= getXAxisStart() && Math.abs(listOfGraphs.get(i).getData()[j][0]-listOfGraphs.get(i).getData()[j+1][0]) <= getXAxisStepSize()) {
					g.drawLine(current.x, getTranslatedPoint(current.x, getYAxisEnd()).y, current.x, getTranslatedPoint(current.x, 0).y);
					g.drawLine(next.x, getTranslatedPoint(current.x, getYAxisEnd()).y, next.x, getTranslatedPoint(current.x, 0).y);
				}
			}
		}
	}

	/**
	 * Method to draw a curve for the data points
	 * @param g (Graphics)
	 */
	protected void drawCurve(Graphics g) {
		Point current, nexttonext;
		for (int i = 0; i < listOfGraphs.size(); i++) {
			g.setColor(graphColor[i]);
			for (int j = 0; j < listOfGraphs.get(i).getData().length-1; j++) {
				current = new Point(getTranslatedPoint(listOfGraphs.get(i).getData()[j][0], listOfGraphs.get(i).getData()[j][1]));				
				nexttonext = new Point(getTranslatedPoint(listOfGraphs.get(i).getData()[j+1][0], listOfGraphs.get(i).getData()[j+1][1]));
				if (listOfGraphs.get(i).getData()[j][0] <= getXAxisEnd() && listOfGraphs.get(i).getData()[j][1] <= getYAxisEnd() && listOfGraphs.get(i).getData()[j][1] >= getYAxisStart() && listOfGraphs.get(i).getData()[j][0] >= getXAxisStart() && Math.abs(listOfGraphs.get(i).getData()[j][0]-listOfGraphs.get(i).getData()[j+1][0]) <= getXAxisStepSize() && listOfGraphs.get(i).getData()[j+1][1] <= getYAxisEnd()) {
					g.drawLine(current.x, current.y, nexttonext.x, nexttonext.y);
					g.drawString(".", current.x, current.y);										
				}
			}
		}
	}
	/**
	 * Draws the Legend for the scatter plot
	 * @param g {@link Graphics}
	 */
	protected void drawLegend(Graphics g) {
		Point p = new Point(getWidth() - getWidth()/4, 100);
		g.setColor(Color.black);
		int lineHeight = g.getFontMetrics().getHeight() + 2; // height of a line
		g.drawString("X-Axis: " + getxAxisName(), p.x, p.y);
		g.drawString("Y-Axis: " + getyAxisName(), p.x, p.y + lineHeight);
		for (int i = 0; i < listOfGraphs.size(); i++) {
			Color graphColor = listOfGraphs.get(i).getColor();
			String graphName = listOfGraphs.get(i).getName();
			g.setColor(graphColor);
			int yLine = p.y + (i + 2) * lineHeight; // y position of the current line
			g.drawLine(p.x, yLine - 5, p.x + 25, yLine - 5);
			g.setColor(Color.black);
			g.drawString(graphName, p.x + 30, yLine);
		}
	}
//	/**
//	 * Method to draw the Legend for the scatter plot
//	 * @param g (Graphics)
//	 */
//	protected void drawLegend(Graphics g) {
//		//Point p = getTranslatedPoint(getXAxisEnd()-1.5*getXAxisStepSize(), getYAxisEnd()-getXAxisStepSize());
//		Point p = new Point(getWidth() - getWidth()/4, 100);
//		//Point q = getTranslatedPoint(100, 100);
//		g.setColor(Color.black);
//		g.drawString("X-Axis: " + getxAxisName(), p.x, p.y);
//		g.drawString("Y-Axis: " + getyAxisName(), p.x, p.y+15);
//		//Point p = getTranslatedPoint(0d,-100d);
//		for (int i = 0; i < listOfGraphs.size(); i++) {
//			g.setColor(graphColor[i]);
//			g.drawString("------- ", p.x, p.y+i*15+30);
//			g.setColor(Color.black);
//			g.drawString(listOfGraphs.get(i).getGraphName(), p.x+30, p.y+i*15+30);
//		}
//	}

	/**
	 * Private method to find the default bounds for the axes for the initial plot
	 */
	private void findDefaultAxisBounds() {
		double xmaxOfMax, ymaxOfMax;
		double xminOfMin, yminOfMin;
		xminOfMin = listOfGraphs.get(0).getData()[0][0];
		yminOfMin = 0;
		xmaxOfMax = listOfGraphs.get(0).getData()[0][0];
		ymaxOfMax = listOfGraphs.get(0).getData()[0][1];
		for (int i = 0; i < listOfGraphs.size(); i++) {
			for (int j = 0; j < listOfGraphs.get(i).getData().length; j++) {				
				if (xminOfMin > listOfGraphs.get(i).getData()[j][0])
					xminOfMin = listOfGraphs.get(i).getData()[j][0];
				if (yminOfMin > listOfGraphs.get(i).getData()[j][1])
					yminOfMin = listOfGraphs.get(i).getData()[j][1];
				if (xmaxOfMax < listOfGraphs.get(i).getData()[j][0])
					xmaxOfMax = listOfGraphs.get(i).getData()[j][0];
				if (ymaxOfMax < listOfGraphs.get(i).getData()[j][1])
					ymaxOfMax = listOfGraphs.get(i).getData()[j][1];
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

	/**
	 * Method that returns the original data Point
	 * @param p (Point)
	 * @return Point
	 */
	protected Point getOriginalPoint(Point p) {
		Point retPoint = new Point();
		retPoint.x = (int) (((p.x - LEFT_PAD) * (getXAxisEnd() - getXAxisStart()) / (getWidth() - LEFT_PAD - RIGHT_PAD_LABELS)) + getXAxisStart());
		retPoint.y = (int) (-1 * ((p.y - getHeight() + TOP_PAD_LABELS) * (getYAxisEnd() - getYAxisStart()) / (getHeight() - TOP_PAD_LABELS - BOTTOM_PAD)) + getYAxisStart());
		return retPoint;
	}	

	/**
	 * Method to translate the coordinates of the data point to the point on screen
	 * @param x (double)
	 * @param y (double)
	 * @return Point
	 */
	protected Point getTranslatedPoint(double x, double y) {
		Point translatedPoint = new Point();
		double realWidth = getWidth() - LEFT_PAD - RIGHT_PAD_LABELS;
		double realHeight = getHeight() - TOP_PAD_LABELS - BOTTOM_PAD;		
		translatedPoint.x = (int) ((realWidth * (x - getXAxisStart())) / (getXAxisEnd() - getXAxisStart())) + LEFT_PAD;
		translatedPoint.y = getHeight() - (int) ((realHeight * (y - getYAxisStart())) / (getYAxisEnd() - getYAxisStart())) - TOP_PAD_LABELS;		
		return translatedPoint;
	}

	@Override
	public void mouseClicked(MouseEvent e) {		
		if (e.getButton() == 3){
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
	public void mouseMoved(MouseEvent e) {
		Point p = new Point();
		p = getOriginalPoint(e.getPoint());
		this.setToolTipText("(" + p.x + " : " + DF.format(p.y) + ")");
		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
	}


	@Override
	public void mousePressed(MouseEvent e) {}


	@Override
	public void mouseReleased(MouseEvent e) {}


	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		setBackground(Color.white);
		drawAxes(g);
		if (isChangeColors() == true) {
			setChangeColors(false);			
		}
		if (isBarGraph() == true) {			
			drawBarGraph(g);
		} else if (isCurve() == true) {
			drawCurve(g);
		} else {
			plotPoints(g);
		}
		drawLegend(g);
	}


	/**
	 * Method to plot the data points
	 * @param g (Graphics)
	 */
	protected void plotPoints(Graphics g) {
		Point current, next;
		for (int i = 0; i < listOfGraphs.size(); i++) {
			g.setColor(graphColor[i]);
			for (int j = 0; j < listOfGraphs.get(i).getData().length-1; j++) {
				current = new Point(getTranslatedPoint(listOfGraphs.get(i).getData()[j][0], listOfGraphs.get(i).getData()[j][1]));
				next = new Point(getTranslatedPoint(listOfGraphs.get(i).getData()[j+1][0], listOfGraphs.get(i).getData()[j][1]));
				if (listOfGraphs.get(i).getData()[j][0] <= getXAxisEnd() && listOfGraphs.get(i).getData()[j][1] <= getYAxisEnd() && listOfGraphs.get(i).getData()[j][1] >= getYAxisStart() && listOfGraphs.get(i).getData()[j][0] >= getXAxisStart() && Math.abs(listOfGraphs.get(i).getData()[j][0]-listOfGraphs.get(i).getData()[j+1][0]) <= getXAxisStepSize()) {
					g.drawLine(current.x, current.y, next.x, next.y);
				}
			}
		}
	}	

	@Override
	public void stateChanged(ChangeEvent ce) {}
}