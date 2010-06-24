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

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


public class ScatterPlotPanel extends JPanel implements MouseMotionListener, MouseListener {
	
	private static final long serialVersionUID = 2641451624657624826L;	// generated serial ID
	
	private static double xMin;
	private static double yMin;
	private static double xMax;
	private static double yMax;
	
	private static final int LEFT_PAD = 100;
	private static final int RIGHT_PAD = 200;
	private static final int RIGHT_PAD_LABELS = 100;
	private static final int TOP_PAD = 200;
	private static final int BOTTOM_PAD = 100;
	private static final int TOP_PAD_LABELS = 100;
	
	private static double yAxisStepSize;
	private static double xAxisStepSize;
	
	private final List<ScatterPlotData> listOfGraphs;
	private Color[] graphColor;
	private Random randomGen;
	private static final DecimalFormat 	DF = new DecimalFormat("###,###,###");	// decimal format
		
			
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
	
	public static void setXAxis(double xMin, double xMax) {
		ScatterPlotPanel.xMin = xMin;
		ScatterPlotPanel.xMax = xMax;
	}
	
	public static void setYAxis(double yMin, double yMax) {
		ScatterPlotPanel.yMin = yMin;
		ScatterPlotPanel.yMax = yMax;
	}
	
	public static void setYAxisStepSize(double yAxisStepSize) {
		ScatterPlotPanel.yAxisStepSize = yAxisStepSize;
	}
	
	protected static double getYAxisStepSize() {
		return yAxisStepSize;
	}
	
	public static void setXAxisStepSize(double xAxisStepSize) {
		ScatterPlotPanel.xAxisStepSize = xAxisStepSize;
	}
	
	protected static double getXAxisStepSize() {
		return xAxisStepSize;
	}
	
	protected static double getXAxisEnd() {
		return xMax;
	}
	
	protected static double getXAxisStart() {
		return xMin;
	}
	
	protected static double getYAxisStart() {
		return yMin;
	}
	
	protected static double getYAxisEnd() {
		return yMax;
	}
	
	protected void drawLegend(Graphics g) {
		Point p = getTranslatedPoint(getXAxisEnd()-10, getYAxisEnd()-10);
		for (int i = 0; i < listOfGraphs.size(); i++) {
			g.setColor(graphColor[i]);
			g.drawString("     -------", p.x, p.y+i);
			g.setColor(Color.black);
			g.drawString("Graph " + Integer.parseInt(listOfGraphs.get(i).getGraphName())+1, p.x+50, p.y);
		}
	}
	
	public Point getTranslatedPoint(double x, double y) {
		Point translatedPoint = new Point();
		double realWidth = getWidth() - LEFT_PAD - RIGHT_PAD;
		double realHeigth = getHeight() - TOP_PAD - BOTTOM_PAD;		
		translatedPoint.x = (int) ((realWidth * (x - getXAxisStart())) / (getXAxisEnd() - getXAxisStart())) + LEFT_PAD;
		translatedPoint.y = getHeight() - (int) ((realHeigth * (y - getYAxisStart())) / (getYAxisEnd() - getYAxisStart())) - TOP_PAD;		
		return translatedPoint;
	}
	
	public Point getOriginalPoint(Point p) {
		Point retPoint = new Point();
		retPoint.x = (int) (((p.x - LEFT_PAD) * (getXAxisEnd() - getXAxisStart()) / (getWidth() - LEFT_PAD - RIGHT_PAD)) + getXAxisStart());
		retPoint.y = (int) (-1 * ((p.y - getHeight() + TOP_PAD) * (getYAxisEnd() - getYAxisStart()) / (getHeight() - TOP_PAD - BOTTOM_PAD)) + getYAxisStart());
		return retPoint;
	}
	
	public void drawAxes(Graphics g) {
		g.drawRect(1, 1, getWidth()-3, getHeight()-3);
		
		Point yAxisStart = getTranslatedPoint(0d, yMin);
		Point yAxisEnds = getTranslatedPoint(0d, yMax);
		g.drawLine(yAxisStart.x, yAxisStart.y, yAxisEnds.x, yAxisEnds.y);
		
		Point xAxisStart = getTranslatedPoint(xMin, 0d);
		Point xAxisStop = getTranslatedPoint(xMax, 0d);
		g.drawLine(xAxisStart.x, xAxisStart.y, xAxisStop.x, xAxisStop.y);
		Point p;
		
		int lastXTextStopPos = 0, lastYTextStopPos = Integer.MAX_VALUE;
		double incrementY = 0;
		double incrementX = 0;
		
		for (int i = 0; i < listOfGraphs.size(); i++) {
			for (int j = 0; j < listOfGraphs.get(i).getDataPoints().length; j++) {
				// X-Axis Labels
				if (incrementX <= getXAxisEnd()) {
					p = new Point(getTranslatedPoint(incrementX, -10));
					if (p.x >= lastXTextStopPos) {
						g.drawString(Integer.toString((int) incrementX), p.x, p.y + TOP_PAD_LABELS/5);
						g.drawString("I", p.x, p.y + TOP_PAD_LABELS/20);
						lastXTextStopPos = p.x + g.getFontMetrics().stringWidth(Integer.toString((int)listOfGraphs.get(i).getDataPoints()[j][0]))+5;
					}					
					incrementX = incrementX + getXAxisStepSize();
				}
				// Y-Axis Labels
				if (incrementY > getYAxisEnd()) {
					incrementY = getXAxisEnd()/2;
				}
				while (incrementY <= getYAxisEnd()) {
					p = new Point(getTranslatedPoint(0,incrementY));
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
		}				
	}
	
	public void plotPoints(Graphics g) {
		Point p;
		double barWidth = listOfGraphs.get(0).getDataPoints()[1][0] - listOfGraphs.get(0).getDataPoints()[0][0];
		String pointLabel = "_";
		while (barWidth > 1) {
			pointLabel += "_";
			barWidth--;
		}
		for (int i = 0; i < listOfGraphs.size(); i++) {
			g.setColor(graphColor[i]);
			for (int j = 0; j < listOfGraphs.get(i).getDataPoints().length; j++) {
				p = new Point(getTranslatedPoint(listOfGraphs.get(i).getDataPoints()[j][0], listOfGraphs.get(i).getDataPoints()[j][1]));
				if (listOfGraphs.get(i).getDataPoints()[j][0] <= getXAxisEnd() && listOfGraphs.get(i).getDataPoints()[j][1] <= getYAxisEnd()) {
					g.drawString(pointLabel, p.x, p.y);
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
