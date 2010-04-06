/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;

import yu.einstein.gdp2.core.enums.GraphicsType;

/**
 * An abstract class providing common tools for the different kind of curve {@link Track}
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class CurveTrack extends ScoredTrack {

	private static final long serialVersionUID = 5068563286341191108L;	// generated ID


	/**
	 * @return the color of the track
	 */
	public final Color getTrackColor() {
		return ((CurveTrackGraphics)trackGraphics).getTrackColor();
	}


	/**
	 * @param trackColor the color of the track to set
	 */
	public final void setTrackColor(Color trackColor) {
		((CurveTrackGraphics)trackGraphics).setTrackColor(trackColor);
	}


	/**
	 * @return the type of the graph
	 */
	public final GraphicsType getTypeOfGraph() {
		return ((CurveTrackGraphics)trackGraphics).getTypeOfGraph();
	}


	/**
	 * @param typeOfGraph the type of the graph to set
	 */
	public final void setTypeOfGraph(GraphicsType typeOfGraph) {
		((CurveTrackGraphics)trackGraphics).setTypeOfGraph(typeOfGraph);
	}
}
