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
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.old.popupMenu;

import edu.yu.einstein.genplay.gui.old.track.BinListTrack;
import edu.yu.einstein.genplay.gui.old.track.EmptyTrack;
import edu.yu.einstein.genplay.gui.old.track.GeneListTrack;
import edu.yu.einstein.genplay.gui.old.track.MultiCurvesTrack;
import edu.yu.einstein.genplay.gui.old.track.NucleotideListTrack;
import edu.yu.einstein.genplay.gui.old.track.RepeatFamilyListTrack;
import edu.yu.einstein.genplay.gui.old.track.SCWListTrack;
import edu.yu.einstein.genplay.gui.old.track.SNPListTrack;
import edu.yu.einstein.genplay.gui.old.track.Track;
import edu.yu.einstein.genplay.gui.old.trackList.TrackList;

/**
 * Creates an instance of a subclass of {@link TrackMenu} depending on the instance of the 
 * selected {@link Track} of a {@link TrackList} 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class TrackMenuFactory {
	
	/**
	 * @param tl a {@link TrackList}
	 * @return an instance of a subclass of {@link TrackMenu} depending on the  
	 * instance of the selected {@link Track} of a {@link TrackList} 
	 */
	public static TrackMenu getTrackMenu (TrackList tl) {
		if (tl.getSelectedTrack() instanceof EmptyTrack) {
			return new EmptyTrackMenu(tl);
		} else if (tl.getSelectedTrack() instanceof BinListTrack) {
			return new BinListTrackMenu(tl);
		} else if (tl.getSelectedTrack() instanceof RepeatFamilyListTrack) {
			return new RepeatFamilyListTrackMenu(tl);
		} else if (tl.getSelectedTrack() instanceof SCWListTrack) {
			return new SCWListTrackMenu(tl);
		} else if (tl.getSelectedTrack() instanceof GeneListTrack) {
			return new GeneListTrackMenu(tl);
		} else if (tl.getSelectedTrack() instanceof NucleotideListTrack) {
			return new NucleotideListTrackMenu(tl);
		} else if (tl.getSelectedTrack() instanceof MultiCurvesTrack) {
			return new MultiCurvesTrackMenu(tl);
		} else if (tl.getSelectedTrack() instanceof SNPListTrack) {
			return new SNPListTrackMenu(tl);
		} else {
			System.out.println("getTrackMenu: " + tl.getSelectedTrack());
			return null;
		}
	}
}
