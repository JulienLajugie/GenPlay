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
package edu.yu.einstein.genplay.gui.popupMenu;

import edu.yu.einstein.genplay.gui.track.MultiCurvesTrack;
import edu.yu.einstein.genplay.gui.trackList.TrackList;


/**
 * A popup menu for a {@link MultiCurvesTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class MultiCurvesTrackMenu extends ScoredTrackMenu {

	private static final long serialVersionUID = -1076083160387134431L;	// generated ID
	
	
	/**
	 * Creates an instance of a {@link MultiCurvesTrackMenu}
	 * @param tl {@link TrackList}
	 */
	public MultiCurvesTrackMenu(TrackList tl) {
		super(tl);
	}
}
