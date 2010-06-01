/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.enums.GraphicsType;
import yu.einstein.gdp2.gui.track.drawer.CurveDrawer;


/**
 * An abstract class providing tools to draw a curve {@link TrackGraphics}
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class CurveTrackGraphics extends ScoredTrackGraphics implements MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long 				serialVersionUID = -9200672145021160494L;	// generated ID
	private static final Color				TRACK_COLOR = Color.black;					// default color
	private static final GraphicsType 		TYPE_OF_GRAPH = GraphicsType.BAR;			// type of graph
	protected static final DecimalFormat 	SCORE_FORMAT = new DecimalFormat("#.##");	// decimal format for the score
	protected Color							trackColor;									// color of the graphics
	protected GraphicsType 					typeOfGraph;								// type graphics


	/**
	 * Creates an instance of {@link CurveTrackGraphics}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param yMin minimum score
	 * @param yMax maximum score
	 */
	protected CurveTrackGraphics(GenomeWindow displayedGenomeWindow, double yMin, double yMax) {
		super(displayedGenomeWindow, yMin, yMax);
		this.trackColor = TRACK_COLOR;
		this.typeOfGraph = TYPE_OF_GRAPH;
	}
	
	
	/**
	 * Returns the drawer used to print the data of this track and link it to the specified {@link Graphics}
	 * @param g {@link Graphics} of a track
	 * @param trackWidth width of a track 
	 * @param trackHeight height of a track
	 * @param genomeWindow {@link GenomeWindow} of a track
	 * @param scoreMin score minimum 
	 * @param scoreMax score maximum
	 * @return the drawer used to paint the data of this track
	 */
	public abstract CurveDrawer getDrawer(Graphics g, int trackWidth, int trackHeight, GenomeWindow genomeWindow, double scoreMin, double scoreMax);


	@Override
	public void copyTo(TrackGraphics trackGraphics) {
		super.copyTo(trackGraphics);
		((CurveTrackGraphics)trackGraphics).trackColor = this.trackColor;
		((CurveTrackGraphics)trackGraphics).typeOfGraph = this.typeOfGraph;
	}

	
	/**
	 * Draws the horizontal lines if the curve is not a dense graphics
	 */
	@Override
	protected void drawHorizontalLines(Graphics g) {
		if (typeOfGraph != GraphicsType.DENSE) {
			super.drawHorizontalLines(g);
		}
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
		firePropertyChange("trackColor", this.trackColor, trackColor);
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
		firePropertyChange("typeOfGraph", this.typeOfGraph, typeOfGraph);
		this.typeOfGraph = typeOfGraph;
		this.repaint();
	}
}
