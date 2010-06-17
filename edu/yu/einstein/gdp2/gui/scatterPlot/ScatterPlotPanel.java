package yu.einstein.gdp2.gui.scatterPlot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ScatterPlotPanel extends JPanel{
	
	private static final long serialVersionUID = 2641451624657624826L;	// generated serial ID
	private double xMin;
	private double yMin;
	private double xMax;
	private double yMax;
	private static final int LEFT_PAD = 100;
	private static final int RIGHT_PAD = 100;
	private static final int TOP_PAD = 100;
	private static final int BOTTOM_PAD = 100;
	private double yAxisStepSize;
	private final List<ScatterPlotData> listOfGraphs;
		
	public ScatterPlotPanel(List<ScatterPlotData> listOfGraphs) {
		this.listOfGraphs = listOfGraphs;
		findDefaultAxisBounds();
		setBackground(Color.white);
		setPreferredSize(new Dimension(500, 500));
		setVisible(true);
		//setBorder(BorderFactory.createLineBorder(Color.red));
		setBorder(BorderFactory.createEmptyBorder(TOP_PAD, LEFT_PAD, BOTTOM_PAD, RIGHT_PAD));
	}
	
	public void setXAxis(double xMin, double xMax) {
		this.xMin = xMin;
		this.xMax = xMax;
	}
	
	public void setYAxis(double yMin, double yMax) {
		this.yMin = yMin;
		this.yMax = yMax;
	}
	
	private void setYAxisStepSize(double yAxisStepSize) {
		this.yAxisStepSize = yAxisStepSize;
	}
	
	private double getYAxisSteSize() {
		return yAxisStepSize;
	}
	
	private double getXAxisEnd() {
		return xMax;
	}
	
	private double getXAxisStart() {
		return xMin;
	}
	
	private double getYAxisStart() {
		return yMin;
	}
	
	private double getYAxisEnd() {
		return yMax;
	}
	
//	private int getTranslatedHeight() {
//		int xHeight = (int) (((double) (getHeight() - TOP_PAD - BOTTOM_PAD) * getYAxisEnd()) / (getYAxisEnd() - getYAxisStart()));
//		if (xHeight <= 0)
//			xHeight = 0;
//		return xHeight;
//	}
//	
//	private int getTranslatedWidth() {
//		int xWidth = (int) (((double) (getWidth() - LEFT_PAD - RIGHT_PAD) * getXAxisEnd()) / (getXAxisEnd() - getXAxisStart()));
//		if (xWidth <= 0)
//			xWidth = 0;
//		return xWidth;
//	}
//	
	
	public Point getTranslatedPoint(double x, double y) {
		Point translatedPoint = new Point();
		double realWidth = getWidth() - LEFT_PAD - RIGHT_PAD;
		double realHeigth = getHeight() - TOP_PAD - BOTTOM_PAD;		
		translatedPoint.x = (int) ((realWidth * (x - getXAxisStart())) / (getXAxisEnd() - getXAxisStart())) + LEFT_PAD;
		translatedPoint.y = getHeight() - (int) ((realHeigth * (y - getYAxisStart())) / (getYAxisEnd() - getYAxisStart())) - TOP_PAD;

		/*translatedPoint.x = (translatedPoint.x < 0) ? 0 : translatedPoint.x;
		translatedPoint.x = (translatedPoint.x > realWidth) ? (int) realWidth : translatedPoint.x;
		translatedPoint.y = (translatedPoint.y < 0) ? 0 : translatedPoint.y;
		translatedPoint.y = (translatedPoint.y > realHeigth) ? (int) realHeigth : translatedPoint.y;*/
		return translatedPoint;
	}
	
	
	public void drawAxes(Graphics g) {
		g.drawRect(1, 1, getWidth()-3, getHeight()-3);
		
		System.out.println(xMin);
		
		Point yAxisStart = getTranslatedPoint(0d, yMin);
		Point yAxisEnds = getTranslatedPoint(0d, yMax);
		g.drawLine(yAxisStart.x, yAxisStart.y, yAxisEnds.x, yAxisEnds.y);
		
		Point xAxisStart = getTranslatedPoint(xMin, 0d);
		Point xAxisStop = getTranslatedPoint(xMax, 0d);
		g.drawLine(xAxisStart.x, xAxisStart.y, xAxisStop.x, xAxisStop.y);
		Point p;
		for (int i = 0; i < listOfGraphs.size(); i++) {
			for (int j = 0; j < listOfGraphs.get(i).getDataPoints().length; j++) {
				p = new Point(getTranslatedPoint(listOfGraphs.get(i).getDataPoints()[j][0], listOfGraphs.get(i).getDataPoints()[j][1]));
				//g.drawString(Integer.toString(i), i+xWidth, (j+xHeight));
				//System.out.println("i+xWidth: " + (i+xWidth) + "j+xHeight :" + (j+xHeight));
				g.drawString(Double.toString(listOfGraphs.get(i).getDataPoints()[j][0]), p.x, p.y);
				//System.out.println("0 at x: " + p.x + " y: " + p.y);
			}			
		}
		
				
//		int xHeight = getTranslatedHeight();
//		int xWidth = getTranslatedWidth();
//		g.drawString("Old", 5, 5);
//		g.drawString("New", 5+xWidth, 5+xHeight);
//		System.out.println("New at x: " + 5+xWidth + " y: " + 5+xHeight);
//		g.drawLine(xWidth, xHeight, xWidth, -((int) getYAxisEnd() + xHeight - getHeight() - TOP_PAD));
//		g.drawLine(xWidth, xHeight, (int) getXAxisEnd() + xWidth, xHeight);
////		g.drawString("X1", 0 + LEFT_PAD, xHeight);
////		g.drawString("XY1010", 0 + LEFT_PAD, 0 + TOP_PAD);
////		g.drawString("X2", getWidth() - RIGHT_PAD, xHeight);
////		g.drawString("Y1", xWidth, (0 + BOTTOM_PAD));
////		g.drawString("Y2", xWidth, getHeight() - TOP_PAD);
//		//g.drawLine(0 + LEFT_PAD, xHeight, getWidth() - RIGHT_PAD, xHeight);
//		//g.drawLine(xWidth, 0 + BOTTOM_PAD, xWidth, getHeight() - TOP_PAD);
//		System.out.println("xheight: " + xHeight + " getWidth() - RIGHT_PAD: " + (getWidth() - RIGHT_PAD));
//		System.out.println("xWidth: " + xWidth + " getHeight() - TOP_PAD: " + (getHeight() - TOP_PAD));
////		System.out.println(xMin);
//		System.out.println(xHeight + " " + (getWidth() - LEFT_PAD - RIGHT_PAD));		
	}
	
	public void plotPoints(Graphics g) {
		Point p;
		double barWidth = listOfGraphs.get(0).getDataPoints()[1][0] - listOfGraphs.get(0).getDataPoints()[0][0];
		String pointLabel = "_";
		while (barWidth >= 1) {
			pointLabel += "_";
			barWidth--;
		}
		for (int i = 0; i < listOfGraphs.size(); i++) {
			for (int j = 0; j < listOfGraphs.get(i).getDataPoints().length; j++) {
				p = new Point(getTranslatedPoint(listOfGraphs.get(i).getDataPoints()[j][0], listOfGraphs.get(i).getDataPoints()[j][1]));
				//g.drawString(Integer.toString(i), i+xWidth, (j+xHeight));
				//System.out.println("i+xWidth: " + (i+xWidth) + "j+xHeight :" + (j+xHeight));
				g.drawString(pointLabel, p.x, p.y);
				//System.out.println("0 at x: " + p.x + " y: " + p.y);
			}			
		}		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		drawAxes(g);
		plotPoints(g);
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
		setXAxis(xminOfMin, xmaxOfMax);
		setYAxis(yminOfMin, ymaxOfMax);
		int magnitudeCounter = 0;
		int temp = (int)ymaxOfMax;
		while (temp > 0) {
			magnitudeCounter++;
			temp /= 10;
		}
		setYAxisStepSize(Math.pow(10,magnitudeCounter));
//		int xMinNegative = (xminOfMin < 0)?1:0;
//		int xMaxNegative = (xmaxOfMax < 0)?1:0;
//		int yMaxNegative = (xmaxOfMax < 0)?1:0;
//		int yMinNegative = (yminOfMin < 0)?1:0;
		
	}	
	
	public static void main(String args[]) {
		JFrame jf = new JFrame();
		jf.setMinimumSize(new Dimension(LEFT_PAD + RIGHT_PAD + 100, TOP_PAD + BOTTOM_PAD + 100));
		List<ScatterPlotData> listOfGraphs = new ArrayList<ScatterPlotData>();
		double[][] test = new double[5][2];
		for (int i=0; i<5; i++) {
			test[i][0] = i*100;
			test[i][1] = i*100;
		}
		test[0][0] = -100;
		test[0][1] = -100;
		test[1][0] = -500;
		test[1][1] = -300;
		String name = "one";
		ScatterPlotData scatterPlotData = new ScatterPlotData(test, name);
		listOfGraphs.add(scatterPlotData);
		listOfGraphs.add(scatterPlotData);
		listOfGraphs.add(scatterPlotData);
		ScatterPlotPanel scatterPlotPanel = new ScatterPlotPanel(listOfGraphs);
		jf.add(scatterPlotPanel);
		jf.setSize(800, 800);
		jf.setVisible(true);
	}
}
