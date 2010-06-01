/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Graphics;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import yu.einstein.gdp2.core.GenomeWindow;

/**
 * An abstract class providing tools to draw a scored {@link TrackGraphics}
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class ScoredTrackGraphics extends TrackGraphics {
	
	private static final long serialVersionUID = 985376787707775754L;	// generated ID
	private static final boolean 	SHOW_HORIZONTAL_GRID = true;		// show the horizontal grid
	private static final int		HORIZONTAL_LINES_COUNT = 10;		// number of Y lines displayed
	protected double				yFactor;							// factor between the displayed intensity range and the screen height
	protected boolean				showHorizontalGrid;					// shows horizontal grid if true 
	protected int					horizontalLinesCount;				// number of horizontal lines
	protected double 				yMax;								// maximum score	
	protected double 				yMin;								// minimum score


	/**
	 * Creates an instance of {@link ScoredTrackGraphics}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param yMin minimum score
	 * @param yMax maximum score
	 */
	protected ScoredTrackGraphics(GenomeWindow displayedGenomeWindow, double yMin, double yMax) {
		super(displayedGenomeWindow);
		this.yMin = yMin;
		this.yMax = yMax;
		this.showHorizontalGrid = SHOW_HORIZONTAL_GRID;
		this.horizontalLinesCount = HORIZONTAL_LINES_COUNT;
	}

	
	/**
	 * Called when the ratio (height of the track / (yMax - y Min)) changes.
	 */
	abstract protected void yFactorChanged();


	/**
	 * Draws the y value of the middle of the track
	 * @param g
	 */
	abstract protected void drawScore(Graphics g);

	
	/**
	 * Draws the data of the track
	 * @param g {@link Graphics}
	 */
	protected abstract void drawData(Graphics g);
	
	
	@Override
	protected void paintComponent(Graphics g) {
		double newYFactor = (double)getHeight() / (double)(yMax - yMin);
		if (newYFactor != yFactor) {
			yFactor = newYFactor;
			yFactorChanged();
		}
		super.paintComponent(g);
	}
	
	
	@Override
	public void copyTo(TrackGraphics trackGraphics) {
		super.copyTo(trackGraphics);
		((ScoredTrackGraphics)trackGraphics).showHorizontalGrid = this.showHorizontalGrid;
		((ScoredTrackGraphics)trackGraphics).horizontalLinesCount = this.horizontalLinesCount;
		((ScoredTrackGraphics)trackGraphics).yMin = this.yMin;
		((ScoredTrackGraphics)trackGraphics).yMax = this.yMax;
	}
	
	
	@Override
	protected void drawTrack(Graphics g) {
		drawStripes(g);
		drawHorizontalLines(g);
		drawVerticalLines(g);
		drawData(g);
		drawScore(g);
		drawName(g);
		drawMiddleVerticalLine(g);
	}
	
	
	/**
	 * Draws horizontal lines on the track
	 * @param g {@link Graphics}
	 */
	protected void drawHorizontalLines(Graphics g) {
		if (showHorizontalGrid) {
			g.setColor(Color.LIGHT_GRAY);
			double scoreGapBetweenLineY = (yMax - yMin) / (double)horizontalLinesCount;
			double intensityFirstLineY = yMin - (yMin % scoreGapBetweenLineY);
			for(int i = 0; i <= horizontalLinesCount; i++) {
				double intensityLineY = i * scoreGapBetweenLineY + intensityFirstLineY;
				if (intensityLineY >= yMin) {
					int screenLineY = scoreToScreenPos(intensityLineY,getHeight());
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
	protected int scoreToScreenPos(double score, int height) {
		if (score < yMin) {
			return height;
		} else if (score > yMax) {
			return 0;
		} else {
			return (height - (int)Math.round((double)(score - yMin) * yFactor));
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
