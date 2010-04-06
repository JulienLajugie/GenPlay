/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

/**
 * An abstract class providing common tools for the different kind of scored {@link Track} 
 * (ie: tracks having a value on the y axis)
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class ScoredTrack extends Track {

	private static final long serialVersionUID = -4376731054381169516L; // generated ID
	
	
	/**
	 * @return true if the horizontal grid is visible
	 */
	public final boolean isShowHorizontalGrid() {
		return ((ScoredTrackGraphics)trackGraphics).isShowHorizontalGrid();
	}


	/**
	 * @param showHorizontalGrid set to true to show the horizontal grid
	 */
	public final void setShowHorizontalGrid(boolean showHorizontalGrid) {
		((ScoredTrackGraphics)trackGraphics).setShowHorizontalGrid(showHorizontalGrid) ;
	}


	/**
	 * @return the number of horizontal lines
	 */
	public final int getHorizontalLinesCount() {
		return ((ScoredTrackGraphics)trackGraphics).getHorizontalLinesCount();
	}


	/**
	 * @param horizontalLinesCount the the number of horizontal lines to show
	 */
	public final void setHorizontalLinesCount(int horizontalLinesCount) {
		((ScoredTrackGraphics)trackGraphics).setHorizontalLinesCount(horizontalLinesCount);
	}	
	
	
	/**
	 * @return the yMin
	 */
	public final double getYMin() {
		return ((ScoredTrackGraphics)trackGraphics).getYMin();
	}


	/**
	 * @param yMin the yMin to set
	 */
	public final void setYMin(double yMin) {
		((ScoredTrackGraphics)trackGraphics).setYMin(yMin);
	}


	/**
	 * @return the yMax
	 */
	public final double getYMax() {
		return ((ScoredTrackGraphics)trackGraphics).getYMax();
	}


	/**
	 * @param yMax the yMax to set
	 */
	public final void setYMax(double yMax) {
		((ScoredTrackGraphics)trackGraphics).setYMax(yMax);
	}
}
