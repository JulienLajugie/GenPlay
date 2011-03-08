/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
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
public class MultiCurvesTrackGraphics extends ScoredTrackGraphics<CurveTrack<?>[]> implements PropertyChangeListener {

	private static final long serialVersionUID = 6508763050002286457L; // generated ID


	/**
	 * Creates an instance of {@link MultiCurvesTrackGraphics}
	 * @param displayedGenomeWindow the displayed {@link GenomeWindow}
	 * @param data array of {@link CurveTrack}
	 */
	public MultiCurvesTrackGraphics(GenomeWindow displayedGenomeWindow, CurveTrack<?>[] data) {
		super(displayedGenomeWindow, data, 0, 1);
		// add repaint listeners so the multicurves track is repainted when on of the curves track is repainted
		for (Track<?> currentTrack: data) {
			currentTrack.trackGraphics.addPropertyChangeListener(this);
		}
		setYMin(findYMin());
		setYMax(findYMax());
	}


	@Override
	protected void drawData(Graphics g) {
		for (int i = data.length; i > 0; i--) {
			CurveTrackGraphics<?> ctg = (CurveTrackGraphics<?>) data[i - 1].trackGraphics;
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
		for (CurveTrack<?> currentCtg: data) {
			min = Math.min(min, currentCtg.getYMin());
		}
		return min;
	}

	
	/**
	 * @return the greatest yMax value of the {@link CurveTrack} showed in this track
	 */
	private double findYMax() {
		double max = Double.NEGATIVE_INFINITY;
		for (CurveTrack<?> currentCtg: data) {
			max = Math.max(max, currentCtg.getYMax());
		}
		return max;
	}
}
