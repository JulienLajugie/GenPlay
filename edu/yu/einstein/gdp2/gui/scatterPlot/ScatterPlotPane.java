/**
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.scatterPlot;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;

import yu.einstein.gdp2.core.enums.GraphicsType;


/**
 * Dialog window showing a scatter plot chart.
 * @author Chirag Gorasia
 * @author Julien Lajugie
 * @version 0.1
 */
public class ScatterPlotPane extends JPanel {

	private static final long serialVersionUID = -7553855067333108714L; // generated ID
	private static final int 	DEFAULT_WIDTH = 1000;		// preferred width of the dialog
	private static final int 	DEFAULT_HEIGHT = 700;		// preferred height of the dialog
	private static final int 	MINIMUM_WIDTH = 700;		// minimum width of the dialog
	private static final int 	MINIMUM_HEIGHT = 500;		// minimum height of the dialog
	private static final int 	PAD = 100; 					// padding between the graph and the dialog
	private static final int 	LENGEND_PAD = 20;			// padding between the legend and the dialog
	private static final int	LEGEND_INSET = 10;			// inset between the legend rectangle and the legend text
	private static final Color 	LEGEND_BACKGROUND = 
		new Color(228, 236, 247);							// background color
	private static final String X_AXIS_PREFIX = "X-Axis";	// prefix name of the x axis
	private static final String Y_AXIS_PREFIX = "Y-Axis";	// prefix name of the y axis
	private static final DecimalFormat 	DF = 
		new DecimalFormat("###,###,###.##");				// decimal format	
	private final ScatterPlotAxis 			xAxis;			// x axis
	private final ScatterPlotAxis 			yAxis;			// y axis
	private final List<ScatterPlotData> 	data;			// data to plot
	private GraphicsType 					chartType;		// type of chart
	private final int 						legendWidth;	// with of the legend

	
	/**
	 * Shows a {@link ScatterPlotPane}.
	 * @param parent parent component. Can be null
	 * @param xAxisName name of the X-Axis
	 * @param yAxisName name of the Y-Axis
	 * @param chartData data to plot
	 */
	public static void showDialog(Component parent, String xAxisName, String yAxisName, List<ScatterPlotData> chartData) {
		ScatterPlotPane scatterPlotPane = new ScatterPlotPane(parent, xAxisName, yAxisName, chartData);
		JDialog scatterPlotDialog = new JDialog();
		scatterPlotDialog.setContentPane(scatterPlotPane);
		scatterPlotDialog.setModal(true);
		scatterPlotDialog.setTitle("Scatter Plot");
		scatterPlotDialog.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		scatterPlotDialog.setMinimumSize(new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT));
		scatterPlotDialog.pack();
		scatterPlotDialog.setResizable(false);
		scatterPlotDialog.setLocationRelativeTo(parent);
		scatterPlotDialog.setVisible(true);
		scatterPlotDialog.dispose();
	}


	/**
	 * Private constructor. Creates an instance of a {@link ScatterPlotData}
	 * @param parent parent component. Can be null
	 * @param xAxisName name of the X-Axis
	 * @param yAxisName name of the Y-Axis
	 * @param data data to plot
	 */
	private ScatterPlotPane(Component parent, String xAxisName, String yAxisName, List<ScatterPlotData> data) {		
		this.data = data;
		double[] dataBounds = findDataBounds(data);
		this.xAxis = new ScatterPlotAxis(dataBounds[0], dataBounds[1], ScatterPlotAxis.HORIZONTAL, xAxisName);
		this.yAxis = new ScatterPlotAxis(dataBounds[2], dataBounds[3], ScatterPlotAxis.VERTICAL, yAxisName);
		this.chartType = GraphicsType.BAR;
		this.legendWidth = computeLegendWidth();
		// show the menu when right click
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {	
				if (e.getButton() == 3){
					ScatterPlotMenu spm = new ScatterPlotMenu(ScatterPlotPane.this);
					spm.show(ScatterPlotPane.this, e.getX(), e.getY());
				}	
			}
		});
		//  show the data values for the mouse position
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Point p = getOriginalPoint(e.getPoint());
				setToolTipText("(" + p.x + " : " + DF.format(p.y) + ")");
			}
		});
		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		setBackground(Color.WHITE);
	}
	
	
	/**
	 * @return the width of the legend text in pixels
	 */
	private int computeLegendWidth() {
		FontMetrics fm = getFontMetrics(getFont());
		int legendWidth = fm.stringWidth(X_AXIS_PREFIX + xAxis.getName());
		legendWidth = Math.max(legendWidth, fm.stringWidth(Y_AXIS_PREFIX + yAxis.getName()));
		for (ScatterPlotData currentPlot: data) {
			if (data != null) {
				legendWidth = Math.max(legendWidth, fm.stringWidth(currentPlot.getName()) + 30);
			}
		}
		return legendWidth;
	}


	/**
	 * Method to plot the data points as a bar chart
	 * @param g (Graphics)
	 */
	private void drawBarGraphics(Graphics g) {
		for (ScatterPlotData currentScatterPlot: data) {
			g.setColor(currentScatterPlot.getColor());
			for (int j = 0; j < currentScatterPlot.getData().length - 1; j++) {
				Point current = null;
				Point next = null;
				if (((currentScatterPlot.getData()[j][0] >= xAxis.getMin())
						&& (currentScatterPlot.getData()[j][0] <= xAxis.getMax())
				/*&& (Math.abs(currentScatterPlot.getData()[j][0] - currentScatterPlot.getData()[j + 1][0]) <= getXAxisStepSize())*/)) {
					if (currentScatterPlot.getData()[j][1] > yAxis.getMax()) {
						current = new Point(getTranslatedPoint(currentScatterPlot.getData()[j][0], yAxis.getMax()));
						next = new Point(getTranslatedPoint(currentScatterPlot.getData()[j + 1][0], yAxis.getMax()));
					} else if (currentScatterPlot.getData()[j][1] < yAxis.getMin()) {
						current = new Point(getTranslatedPoint(currentScatterPlot.getData()[j][0], yAxis.getMin()));
						next = new Point(getTranslatedPoint(currentScatterPlot.getData()[j + 1][0], yAxis.getMin()));
					} else {
						current = new Point(getTranslatedPoint(currentScatterPlot.getData()[j][0], currentScatterPlot.getData()[j][1]));
						next = new Point(getTranslatedPoint(currentScatterPlot.getData()[j + 1][0], currentScatterPlot.getData()[j][1]));
					}
					g.drawLine(current.x, current.y, next.x, next.y);
					g.drawLine(current.x, current.y, current.x, getTranslatedPoint(current.x, 0).y);
					g.drawLine(next.x, next.y, next.x, getTranslatedPoint(current.x, 0).y);
				}
			}
		}
	}


	/**
	 * Method to plot the data points as a curve chart
	 * @param g (Graphics)
	 */
	private void drawCurveGraphics(Graphics g) {
		for (ScatterPlotData currentScatterPlot: data) {
			g.setColor(currentScatterPlot.getColor());
			for (int j = 0; j < currentScatterPlot.getData().length - 1; j++) {
				Point current = new Point(getTranslatedPoint(currentScatterPlot.getData()[j][0], currentScatterPlot.getData()[j][1]));				
				Point nexttonext = new Point(getTranslatedPoint(currentScatterPlot.getData()[j + 1][0], currentScatterPlot.getData()[j + 1][1]));
				if ((currentScatterPlot.getData()[j][0] >= xAxis.getMin()) 
						&& (currentScatterPlot.getData()[j][0] <= xAxis.getMax())
						&& (currentScatterPlot.getData()[j][1] >= yAxis.getMin()) 
						&& (currentScatterPlot.getData()[j][1] <= yAxis.getMax()) 
						//&& (Math.abs(currentScatterPlot.getData()[j][0] - currentScatterPlot.getData()[j + 1][0]) <= getXAxisStepSize()) 
						&& (currentScatterPlot.getData()[j + 1][1] <= yAxis.getMax())) {
					g.drawLine(current.x, current.y, nexttonext.x, nexttonext.y);
					g.drawString(".", current.x, current.y);										
				}
			}
		}
	}


	/**
	 * Draws the scatter plots. The style is defined by the graphics type parameter.
	 * @param g {@link Graphics} object of the component
	 * @param clip rectangle where to draw the chart
	 */
	private void drawGraphics(Graphics g, Rectangle clip, GraphicsType graphicsType) {
		switch (graphicsType) {
		case BAR:
			drawBarGraphics(g);
			break;
		case CURVE:
			drawCurveGraphics(g);
			break;
		case DENSE:
			throw new IllegalArgumentException("Can't draw a dense scatter curve");
		case POINTS:
			drawPointGraphics(g);
			break;
		}
	}


	/**
	 * Draws the Legend for the scatter plot
	 * @param g {@link Graphics}
	 */
	private void drawLegend(Graphics g) {
		// set the color of the legend rectangle
		g.setColor(LEGEND_BACKGROUND);
		int lineHeight = g.getFontMetrics().getHeight() + 2; // height of a legend line
		// draw the legend rectangle
		g.fillRect(getWidth() - legendWidth - LENGEND_PAD - (LEGEND_INSET * 2), LENGEND_PAD, legendWidth + (LEGEND_INSET * 2), ((data.size() + 2 ) * lineHeight) + (LEGEND_INSET * 2));
		// search the position where to start the text of the legend
		Point p = new Point(getWidth() - legendWidth - LENGEND_PAD - LEGEND_INSET, LENGEND_PAD + LEGEND_INSET + g.getFontMetrics().getHeight());
		// draw the axis names
		g.setColor(Color.BLACK);
		g.drawString(X_AXIS_PREFIX + xAxis.getName(), p.x, p.y); // draw X-Axis legend
		g.drawString(Y_AXIS_PREFIX + yAxis.getName(), p.x, p.y + lineHeight); // draw Y-Axis legend
		// draw graph name legend
		for (int i = 0; i < data.size(); i++) {
			Color graphColor = data.get(i).getColor();
			String graphName = data.get(i).getName();
			g.setColor(graphColor);
			int yLine = p.y + (i + 2) * lineHeight; // y position of the current line
			g.drawLine(p.x, yLine - 5, p.x + 25, yLine - 5); // draw a line with the color of the graph
			g.setColor(Color.BLACK);
			g.drawString(graphName, p.x + 30, yLine); // draw the name of the graph
		}
	}


	/**
	 * Method to plot the data points as a point chart
	 * @param g (Graphics)
	 */
	private void drawPointGraphics(Graphics g) {
		for (ScatterPlotData currentScatterPlot: data) {
			g.setColor(currentScatterPlot.getColor());
			for (int j = 0; j < currentScatterPlot.getData().length - 1; j++) {
				Point current = new Point(getTranslatedPoint(currentScatterPlot.getData()[j][0], currentScatterPlot.getData()[j][1]));
				Point next = new Point(getTranslatedPoint(currentScatterPlot.getData()[j + 1][0], currentScatterPlot.getData()[j][1]));
				if ((currentScatterPlot.getData()[j][0] >= xAxis.getMin())
						&& (currentScatterPlot.getData()[j][0] <= xAxis.getMax())
						&& (currentScatterPlot.getData()[j][1] >= yAxis.getMin())
						&& (currentScatterPlot.getData()[j][1] <= yAxis.getMax())
				/*&& (Math.abs(currentScatterPlot.getData()[j][0] - currentScatterPlot.getData()[j + 1][0]) <= getXAxisStepSize()*/) {
					g.drawLine(current.x, current.y, next.x, next.y);
				}
			}
		}
	}


	/**
	 * @param data
	 * @return the bounds of the specified data in an array of double organized this way:<br>{xMin, xMax, yMin, yMax}
	 */
	private double[] findDataBounds(List<ScatterPlotData> data) {
		double[] bounds = new double[4];
		bounds[0] = Double.POSITIVE_INFINITY;
		bounds[1] = Double.NEGATIVE_INFINITY;
		bounds[2] = Double.POSITIVE_INFINITY;
		bounds[3] = Double.NEGATIVE_INFINITY;
		for (ScatterPlotData currentPlot: data) {
			if (data != null) {
				for (int i = 0; i < currentPlot.getData().length; i++) {
					bounds[0] = Math.min(bounds[0], currentPlot.getData()[i][0]);
					bounds[1] = Math.max(bounds[1], currentPlot.getData()[i][0]);
					bounds[2] = Math.min(bounds[2], currentPlot.getData()[i][1]);
					bounds[3] = Math.max(bounds[3], currentPlot.getData()[i][1]);
				}
			}
		}
		return bounds;
	}	


	/**
	 * @return the chart type
	 */
	public final GraphicsType getChartType() {
		return chartType;
	}


	/**
	 * @return the data of the charts
	 */
	public final List<ScatterPlotData> getData() {
		return data;
	}


	/**
	 * @return an array of String containing the name of the {@link ScatterPlotData} 
	 */
	public String[] getGraphNames() {
		String[] names = new String[data.size()]; 
		for (int i = 0; i < data.size(); i++) {
			names[i] = data.get(i).getName();			
		}
		return names;
	}


	/**
	 * @param p {@link Point} of a screen position
	 * @return original data Point corresponding to the specified screen position
	 */
	private Point getOriginalPoint(Point p) {
		Point retPoint = new Point();
		retPoint.x = (int) (((p.x - PAD) * (xAxis.getMax() - xAxis.getMin()) / (getWidth() - 2 * PAD)) + xAxis.getMin());
		retPoint.y = (int) (-1 * ((p.y - getHeight() + PAD) * (yAxis.getMax() - yAxis.getMin()) / (getHeight() - 2 * PAD)) + yAxis.getMin());
		return retPoint;
	}


	/**
	 * Method to translate the coordinates of the data point to the point on screen
	 * @param x x component of a data point
	 * @param y y component of a data point
	 * @return Point
	 */
	private Point getTranslatedPoint(double x, double y) {
		Point translatedPoint = new Point();
		int realWidth = getWidth() - (2 * PAD);
		int realHeight = getHeight() - (2 * PAD);
		Rectangle clip = new Rectangle(PAD, PAD, realWidth, realHeight);
		translatedPoint.x = xAxis.dataValueToScreenPosition(x, clip);
		translatedPoint.y = yAxis.dataValueToScreenPosition(y, clip);
		return translatedPoint;
	}


	/**
	 * @return the xAxis
	 */
	public final ScatterPlotAxis getxAxis() {
		return xAxis;
	}


	/**
	 * @return the yAxis
	 */
	public final ScatterPlotAxis getyAxis() {
		return yAxis;
	}


	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Rectangle clip = new Rectangle(PAD, PAD, getWidth() - (2 * PAD), getHeight()- (2 * PAD));
		xAxis.drawAxis(g, clip);
		yAxis.drawAxis(g, clip);
		drawGraphics(g, clip, chartType);
		drawLegend(g);
	}
	
	
	/**
	 * @param chartType the chart type to set
	 */
	public final void setChartType(GraphicsType chartType) {
		this.chartType = chartType;
	}
}
