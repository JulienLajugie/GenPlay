/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.enums.GraphicsType;
import yu.einstein.gdp2.util.ZoomManager;

/**
 * An abstract class providing common tools for different kind of curve {@link Track}
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class CurveTrack extends Track {

	private static final long serialVersionUID = 5068563286341191108L;	// generated ID

	@Override
	abstract public Track copy();


	@Override
	abstract protected void initTrackGraphics(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow);
	
	
	/**
	 * @return true if the horizontal grid is visible
	 */
	public final boolean isShowHorizontalGrid() {
		return ((CurveTrackGraphics)trackGraphics).isShowHorizontalGrid();
	}


	/**
	 * @param showHorizontalGrid set to true to show the horizontal grid
	 */
	public final void setShowHorizontalGrid(boolean showHorizontalGrid) {
		((CurveTrackGraphics)trackGraphics).setShowHorizontalGrid(showHorizontalGrid) ;
	}


	/**
	 * @return the number of horizontal lines
	 */
	public final int getHorizontalLinesCount() {
		return ((CurveTrackGraphics)trackGraphics).getHorizontalLinesCount();
	}


	/**
	 * @param horizontalLinesCount the the number of horizontal lines to show
	 */
	public final void setHorizontalLinesCount(int horizontalLinesCount) {
		((CurveTrackGraphics)trackGraphics).setHorizontalLinesCount(horizontalLinesCount);
	}


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


	/**
	 * @return the yMin
	 */
	public final double getYMin() {
		return ((CurveTrackGraphics)trackGraphics).getYMin();
	}


	/**
	 * @param yMin the yMin to set
	 */
	public final void setYMin(double yMin) {
		((CurveTrackGraphics)trackGraphics).setYMin(yMin);
	}


	/**
	 * @return the yMax
	 */
	public final double getYMax() {
		return ((CurveTrackGraphics)trackGraphics).getYMax();
	}


	/**
	 * @param yMax the yMax to set
	 */
	public final void setYMax(double yMax) {
		((CurveTrackGraphics)trackGraphics).setYMax(yMax);
	}
}
