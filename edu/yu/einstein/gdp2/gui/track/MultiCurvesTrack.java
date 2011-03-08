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

import yu.einstein.gdp2.core.GenomeWindow;

/**
 * A track showing multiples {@link CurveTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class MultiCurvesTrack extends ScoredTrack<CurveTrack<?>[]> {

	private static final long serialVersionUID = -8961218330334104474L; // generated ID
	
	
	/**
	 * Creates an instance of {@link MultiCurvesTrack}
	 * @param displayedGenomeWindow the displayed {@link GenomeWindow}
	 * @param trackNumber the number of the track
	 * @param data array of {@link CurveTrack}
	 */
	public MultiCurvesTrack(GenomeWindow displayedGenomeWindow, int trackNumber, CurveTrack<?>[] data) {
		super(displayedGenomeWindow, trackNumber, data);
	}


	@Override
	protected TrackGraphics<CurveTrack<?>[]> createsTrackGraphics(GenomeWindow displayedGenomeWindow, CurveTrack<?>[] data) {
		return new MultiCurvesTrackGraphics(displayedGenomeWindow, data);
	}
}
