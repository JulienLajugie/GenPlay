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
package edu.yu.einstein.genplay.gui.track;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.list.binList.BinList;

/**
 * A track containing a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinListTrack extends CurveTrack<BinList> {

	private static final long serialVersionUID = -395099043710070726L; // generated ID

	
	/**
	 * Creates an instance of {@link BinListTrack}
	 * @param displayedGenomeWindow the displayed {@link GenomeWindow}
	 * @param trackNumber the number of the track
	 * @param data the {@link BinList} showed in the track
	 */
	public BinListTrack(GenomeWindow displayedGenomeWindow, int trackNumber, BinList data) {
		super(displayedGenomeWindow, trackNumber, data);
	}


	@Override
	protected TrackGraphics<BinList> createsTrackGraphics(GenomeWindow displayedGenomeWindow, BinList data) {
		return new BinListTrackGraphics(displayedGenomeWindow, data);
	}
}
