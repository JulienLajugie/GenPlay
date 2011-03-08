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
import edu.yu.einstein.genplay.core.enums.Nucleotide;
import edu.yu.einstein.genplay.core.list.DisplayableListOfLists;

/**
 * A track showing a sequence of {@link Nucleotide}
 * @author Julien Lajugie
 * @version 0.1
 */
public class NucleotideListTrack extends Track<DisplayableListOfLists<Nucleotide, Nucleotide[]>> {

	private static final long serialVersionUID = 8424429602220353656L; // generated ID
	
	
	/**
	 * Creates an instance of {@link NucleotideListTrack}
	 * @param displayedGenomeWindow the displayed {@link GenomeWindow}
	 * @param trackNumber the number of the track
	 * @param data list of {@link Nucleotide} to display in the track
	 */
	public NucleotideListTrack(GenomeWindow displayedGenomeWindow, int trackNumber, DisplayableListOfLists<Nucleotide, Nucleotide[]> data) {
		super(displayedGenomeWindow, trackNumber, data);
	}


	@Override
	protected TrackGraphics<DisplayableListOfLists<Nucleotide, Nucleotide[]>> 
	createsTrackGraphics(GenomeWindow displayedGenomeWindow, DisplayableListOfLists<Nucleotide, Nucleotide[]> data) {
		return new NucleotideListTrackGraphics(displayedGenomeWindow, data);
	}
}
