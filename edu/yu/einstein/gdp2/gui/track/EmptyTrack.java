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
 * An empty track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class EmptyTrack extends Track<Void> {

	private static final long serialVersionUID = 3508936560321856203L;	// generated ID
	
	
	/**
	 * Creates an instance of {@link EmptyTrack}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param trackNumber number of the track
	 */
	public EmptyTrack(GenomeWindow displayedGenomeWindow, int trackNumber) {
		super(displayedGenomeWindow, trackNumber, null);
	}


	@Override
	protected TrackGraphics<Void> createsTrackGraphics(GenomeWindow displayedGenomeWindow, Void data) {
		return new EmptyTrackGraphics(displayedGenomeWindow);
	}
}
