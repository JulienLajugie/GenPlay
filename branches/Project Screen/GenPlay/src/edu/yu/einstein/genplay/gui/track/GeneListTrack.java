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
import edu.yu.einstein.genplay.core.list.geneList.GeneList;

/**
 * A track containing a {@link GeneList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class GeneListTrack extends Track<GeneList> {

	private static final long serialVersionUID = 907497013953591152L; // generated ID

	
	/**
	 * Creates an instance of {@link GeneListTrack}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param trackNumber number of the track
	 * @param data {@link GeneList} showed in the track
	 */
	public GeneListTrack(GenomeWindow displayedGenomeWindow, int trackNumber,  GeneList data) {
		super(displayedGenomeWindow, trackNumber, data);
	}
	
	/*public GeneListTrack(GenomeWindow displayedGenomeWindow, int trackNumber,  GeneList data, String genome) {
		super(displayedGenomeWindow, trackNumber, data, genome);
	}*/


	@Override
	protected TrackGraphics<GeneList> createsTrackGraphics(GenomeWindow displayedGenomeWindow, GeneList data) {
		return new GeneListTrackGraphics(displayedGenomeWindow, data);
	}
}