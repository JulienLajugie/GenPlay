/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.enums.GraphicsType;
import yu.einstein.gdp2.util.ZoomManager;

/**
 * An abstract class providing tools to draw a curve {@link TrackGraphics}
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class CurveTrackGraphics extends TrackGraphics implements MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long serialVersionUID = -9200672145021160494L;				// generated ID
	private static final boolean 		SHOW_HORIZONTAL_GRID = true;				// show the horizontal grid
	private static final int			HORIZONTAL_LINES_COUNT = 10;				// number of Y lines displayed
	private static final Color			TRACK_COLOR = Color.black;					// default color
	private static final GraphicsType 	TYPE_OF_GRAPH = GraphicsType.BAR;			// type of graph
	protected static final DecimalFormat SCORE_FORMAT = new DecimalFormat("#.##");	// decimal format for the score

	private double				yFactor;						// factor between the displayed intensity range and the screen height
	private boolean				showHorizontalGrid;				// shows horizontal grid if true 
	private int					horizontalLinesCount;			// number of horizontal lines
	protected Color				trackColor;						// color of the graphics
	protected GraphicsType 		typeOfGraph;					// type graphics
	protected double 			yMax;							// maximum score	
	protected double 			yMin;							// minimum score


	/**
	 * Called when the ratio (height of the track / (yMax - y Min)) changes.
	 */
	abstract protected void yFactorChanged();


	/**
	 * Draws a bar graphics.
	 * @param g {@link Graphics}
	 */
	abstract protected void drawBarGraphics(Graphics g);


	/**
	 * Draws a point graphics.
	 * @param g {@link Graphics}
	 */
	abstract protected void drawCurveGraphics(Graphics g);


	/**
	 * Draws a curve graphics.
	 * @param g {@link Graphics}
	 */
	abstract protected void drawPointGraphics(Graphics g);


	/**
	 * Draws a dense graphics.
	 * @param g {@link Graphics}
	 */
	abstract protected void drawDenseGraphics(Graphics g);


	/**
	 * Draws the y value of the middle of the track
	 * @param g
	 */
	abstract protected void drawScore(Graphics g);


	/**
	 * Constructor.
	 * @param zoomManager {@link ZoomManager}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param yMin minimum score
	 * @param yMax maximum score
	 */
	protected CurveTrackGraphics(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow, double yMin, double yMax) {
		super(zoomManager, displayedGenomeWindow);
		this.yMin = yMin;
		this.yMax = yMax;
		this.showHorizontalGrid = SHOW_HORIZONTAL_GRID;
		this.horizontalLinesCount = HORIZONTAL_LINES_COUNT;
		this.trackColor = TRACK_COLOR;
		this.typeOfGraph = TYPE_OF_GRAPH;
	}

	@Override
	protected void paintComponent(Graphics g) {
		double newYFactor = (double)getHeight() / (double)(yMax - yMin);
		if (newYFactor != yFactor) {
			yFactor = newYFactor;
			yFactorChanged();
		}
		super.paintComponent(g);
	}



	/* (non-Javadoc)
	 * @see yu.einstein.gdp2.gui.track.TrackGraphics#drawTrack(java.awt.Graphics)
	 */
	@Override
	protected void drawTrack(Graphics g) {
		drawStripes(g);
		drawHorizontalLines(g);
		drawVerticalLines(g);
		// We check if the displayed data changed 
		Graphics2D g2D = (Graphics2D)g;	
		switch(typeOfGraph) {
		case BAR:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			drawBarGraphics(g);
			break;
		case CURVE:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			drawCurveGraphics(g);
			break;
		case POINTS:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			drawPointGraphics(g);
			break;
		case DENSE:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			drawDenseGraphics(g);
			break;
		}
		drawScore(g);
		drawName(g);
		drawMiddleVerticalLine(g);
	}


	@Override
	public void copyTo(TrackGraphics trackGraphics) {
		super.copyTo(trackGraphics);
		((CurveTrackGraphics)trackGraphics).showHorizontalGrid = this.showHorizontalGrid;
		((CurveTrackGraphics)trackGraphics).horizontalLinesCount = this.horizontalLinesCount;
		((CurveTrackGraphics)trackGraphics).trackColor = this.trackColor;
		((CurveTrackGraphics)trackGraphics).typeOfGraph = this.typeOfGraph;
		((CurveTrackGraphics)trackGraphics).yMin = this.yMin;
		((CurveTrackGraphics)trackGraphics).yMax = this.yMax;
	}


	/**
	 * Draws horizontal lines on the track
	 * @param g {@link Graphics}
	 */
	private void drawHorizontalLines(Graphics g) {
		if ((showHorizontalGrid) && (typeOfGraph != GraphicsType.DENSE)){
			g.setColor(Color.LIGHT_GRAY);
			double scoreGapBetweenLineY = (yMax - yMin) / (double)horizontalLinesCount;
			double intensityFirstLineY = yMin - (yMin % scoreGapBetweenLineY);
			for(int i = 0; i <= horizontalLinesCount; i++) {
				double intensityLineY = i * scoreGapBetweenLineY + intensityFirstLineY;
				if (intensityLineY >= yMin) {
					int screenLineY = scoreToScreenPos(intensityLineY);
					g.drawLine(0, screenLineY, getWidth(), screenLineY);
					DecimalFormat formatter = new DecimalFormat("#.#");
					formatter.setRoundingMode(RoundingMode.DOWN);
					String positionStr = formatter.format(intensityLineY);
					g.drawString(positionStr, 2, screenLineY);
				}
			}
		}
	}


	/**
	 * @param score a double value
	 * @return the value on the screen
	 */
	protected int scoreToScreenPos(double score) {
		if (score < yMin) {
			return getHeight();
		} else if (score > yMax) {
			return 0;
		} else {
			return (getHeight() - (int)Math.round((double)(score - yMin) * yFactor));
		}
	}


	/**
	 * @return true if the horizontal grid is visible
	 */
	public final boolean isShowHorizontalGrid() {
		return showHorizontalGrid;
	}


	/**
	 * @param showHorizontalGrid set to true to show the horizontal grid
	 */
	public final void setShowHorizontalGrid(boolean showHorizontalGrid) {
		this.showHorizontalGrid = showHorizontalGrid;
		this.repaint();
	}


	/**
	 * @return the number of horizontal lines
	 */
	public final int getHorizontalLinesCount() {
		return horizontalLinesCount;
	}


	/**
	 * @param horizontalLinesCount the the number of horizontal lines to show
	 */
	public final void setHorizontalLinesCount(int horizontalLinesCount) {
		this.horizontalLinesCount = horizontalLinesCount;
		this.repaint();
	}


	/**
	 * @return the color of the track
	 */
	public final Color getTrackColor() {
		return trackColor;
	}


	/**
	 * @param trackColor the color of the track to set
	 */
	public final void setTrackColor(Color trackColor) {
		this.trackColor = trackColor;
		this.repaint();
	}


	/**
	 * @return the type of the graph
	 */
	public final GraphicsType getTypeOfGraph() {
		return typeOfGraph;
	}


	/**
	 * @param typeOfGraph the type of the graph to set
	 */
	public final void setTypeOfGraph(GraphicsType typeOfGraph) {
		this.typeOfGraph = typeOfGraph;
		this.repaint();
	}


	/**
	 * @return the yMin
	 */
	public final double getYMin() {
		return yMin;
	}


	/**
	 * @param yMin the yMin to set
	 */
	public final void setYMin(double yMin) {
		this.yMin = yMin;
		this.repaint();
	}


	/**
	 * @return the yMax
	 */
	public final double getYMax() {
		return yMax;
	}


	/**
	 * @param yMax the yMax to set
	 */
	public final void setYMax(double yMax) {
		this.yMax = yMax;
		this.repaint();
	}
}
