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
package edu.yu.einstein.genplay.gui.popupMenu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import edu.yu.einstein.genplay.gui.action.repeatFamilyTrack.RFTAConvertIntoMask;
import edu.yu.einstein.genplay.gui.old.track.RepeatFamilyListTrack;
import edu.yu.einstein.genplay.gui.old.trackList.TrackList;


/**
 * A popup menu for a {@link RepeatFamilyListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class RepeatFamilyListTrackMenu extends TrackMenu {

	private static final long serialVersionUID = -4797259442922136696L; // generated ID
	private final JMenu			jmOperation;							// category operation

	private final JMenuItem		jmiConvertIntoMask;						// menu convert into mask

	
	/**
	 * Creates an instance of a {@link RepeatFamilyListTrackMenu}
	 * @param tl {@link TrackList}
	 */
	public RepeatFamilyListTrackMenu(TrackList tl) {
		super(tl);
		
		jmOperation = new JMenu("Operation");
		jmiConvertIntoMask = new JMenuItem(actionMap.get(RFTAConvertIntoMask.ACTION_KEY));
		
		add(jmOperation, 0);
		add(new Separator(), 1);
		
		jmOperation.add(jmiConvertIntoMask);
	}
}
