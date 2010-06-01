/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import yu.einstein.gdp2.core.GenomeWindow;

/**
 * A {@link TrackGraphics} part of a {@link MultiCurvesTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class MultiCurvesTrackGraphics extends ScoredTrackGraphics implements PropertyChangeListener {

	private static final long serialVersionUID = 6508763050002286457L; // generated ID
	private final CurveTrack[] curveTracks; // array of curve tracks


	/**
	 * Creates an instance of {@link MultiCurvesTrackGraphics}
	 * @param displayedGenomeWindow the displayed {@link GenomeWindow}
	 * @param curveTracks array of {@link CurveTrack}
	 */
	public MultiCurvesTrackGraphics(GenomeWindow displayedGenomeWindow, CurveTrack[] curveTracks) {
		super(displayedGenomeWindow, 0, 1);
		this.curveTracks = curveTracks;
		// add repaint listeners so the multicurves track is repainted when on of the curves track is repainted
		for (Track currentTrack: curveTracks) {
			currentTrack.trackGraphics.addPropertyChangeListener(this);
		}
		setYMin(findYMin());
		setYMax(findYMax());
	}


	@Override
	protected void drawData(Graphics g) {
		for (int i = curveTracks.length; i > 0; i--) {
			CurveTrackGraphics ctg = (CurveTrackGraphics) curveTracks[i - 1].trackGraphics;
			ctg.getDrawer(g, getWidth(), getHeight(), genomeWindow, yMin, yMax).draw();
		}
	}


	@Override
	protected void drawScore(Graphics g) {}


	@Override
	protected void yFactorChanged() { 
		repaint();
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if ((propertyName.equals("binList")) || (propertyName.equals("trackColor")) || (propertyName.equals("typeOfGraph"))) {
			repaint();
		}
	}
	
	
	/**
	 * @return the smallest yMin value of the {@link CurveTrack} showed in this track 
	 */
	private double findYMin() {
		double min = Double.POSITIVE_INFINITY;
		for (CurveTrack currentCtg: curveTracks) {
			min = Math.min(min, currentCtg.getYMin());
		}
		return min;
	}

	
	/**
	 * @return the greatest yMax value of the {@link CurveTrack} showed in this track
	 */
	private double findYMax() {
		double max = Double.NEGATIVE_INFINITY;
		for (CurveTrack currentCtg: curveTracks) {
			max = Math.max(max, currentCtg.getYMax());
		}
		return max;
	}
}
