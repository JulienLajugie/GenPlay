/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.enums.GraphicsType;
import yu.einstein.gdp2.gui.track.CurveTrack;


/**
 * Abstract class. Draws the data of a {@link CurveTrack}. 
 * Must be extended by the drawers of the different kind of {@link CurveTrack} subclasses
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class CurveDrawer {

	protected final Graphics 		graphics;		// Graphics of a track
	protected final int 			trackWidth;		// width of a track
	protected final int 			trackHeight;	// height of a track
	protected final GenomeWindow 	genomeWindow;	// GenomeWindow of a track
	protected final double 			scoreMin;		// score minimum to display
	protected final double 			scoreMax;		// score maximum to display
	protected final Color 			trackColor;		// color of the curve
	protected final GraphicsType 	typeOfGraph;	// type of graph
	protected final double 			xRatio;			// ratio between the width of a track and the number of base pair to display
	protected final double 			yRatio;			// ratio between the height of a track and the distance from yMin to yMax
	
	
	/**
	 * Draws a bar graphics.
	 */
	abstract protected void drawBarGraphics();


	/**
	 * Draws a point graphics.
	 */
	abstract protected void drawCurveGraphics();


	/**
	 * Draws a curve graphics.
	 */
	abstract protected void drawPointGraphics();


	/**
	 * Draws a dense graphics.
	 */
	abstract protected void drawDenseGraphics();
	
	
	/**
	 * Creates an instance of {@link CurveDrawer}
	 * @param graphics {@link Graphics} of a track
	 * @param trackWidth width of a track 
	 * @param trackHeight height of a track
	 * @param genomeWindow {@link GenomeWindow} of a track
	 * @param scoreMin score minimum to display
	 * @param scoreMax score maximum to display
	 * @param trackColor color of the curve
	 * @param typeOfGraph type of graph
	 */
	public CurveDrawer (Graphics graphics, int trackWidth, int trackHeight, GenomeWindow genomeWindow, double scoreMin, double scoreMax, Color trackColor, GraphicsType typeOfGraph) {
		this.graphics = graphics;
		this.trackWidth = trackWidth;
		this.trackHeight = trackHeight;
		this.genomeWindow = genomeWindow;
		this.scoreMin = scoreMin;
		this.scoreMax = scoreMax;
		this.trackColor = trackColor;
		this.typeOfGraph = typeOfGraph;
		this.xRatio = (double)trackWidth / (double)(genomeWindow.getStop() - genomeWindow.getStart());
		this.yRatio = (double)trackHeight / (double)(scoreMax - scoreMin);
	}
		

	/**
	 * @param score a double value
	 * @return the value on the screen
	 */
	protected int scoreToScreenPos(double score) {
		if (score < scoreMin) {
			return trackHeight;
		} else if (score > scoreMax) {
			return 0;
		} else {
			return (trackHeight - (int)Math.round((double)(score - scoreMin) * yRatio));
		}
	}
	
	
	/**
	 * @param genomePosition a position on the genome
	 * @return the absolute position on the screen (can be > than the screen width)
	 */
	protected int genomePosToScreenPos(int genomePosition) {
		return (int)Math.round((double)(genomePosition - genomeWindow.getStart()) * xRatio);
	}
	
	
	/**
	 * Draws the data. 
	 */
	public void draw() {
		Graphics2D g2D = (Graphics2D)graphics;	
		switch(typeOfGraph) {
		case BAR:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			drawBarGraphics();
			break;
		case CURVE:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			drawCurveGraphics();
			break;
		case POINTS:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			drawPointGraphics();
			break;
		case DENSE:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			drawDenseGraphics();
			break;
		}
	}	
}
