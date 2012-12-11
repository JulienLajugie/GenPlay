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

import javax.swing.ActionMap;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import edu.yu.einstein.genplay.gui.action.track.TACopy;
import edu.yu.einstein.genplay.gui.action.track.TACut;
import edu.yu.einstein.genplay.gui.action.track.TADelete;
import edu.yu.einstein.genplay.gui.action.track.TAInsert;
import edu.yu.einstein.genplay.gui.action.track.TAPaste;
import edu.yu.einstein.genplay.gui.action.track.TARename;
import edu.yu.einstein.genplay.gui.action.track.TASaveAsImage;
import edu.yu.einstein.genplay.gui.action.track.TASetHeight;
import edu.yu.einstein.genplay.gui.action.track.TASetVerticalLineCount;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * Contextual menu showed when a track handle is right clicked
 * @author Julien Lajugie
 * @version 0.1
 */
public class TrackMenu extends JPopupMenu implements PopupMenuListener {

	private static final long serialVersionUID = -7063797741454351041L; // generated serial ID
	
	/**
	 *  keys of the actions of this menu
	 */
	private static final String[] ACTION_KEYS = {
		TACopy.ACTION_KEY,
		TACut.ACTION_KEY,
		TADelete.ACTION_KEY,
		TAInsert.ACTION_KEY,
		TAPaste.ACTION_KEY,
		TARename.ACTION_KEY,
		TASaveAsImage.ACTION_KEY,
		TASetHeight.ACTION_KEY,
		TASetVerticalLineCount.ACTION_KEY
	};
	private Track track;

	/**
	 * Creates an instance of {@link TrackMenu}
	 * @param actionMap action map containing the the actions of the menu
	 */
	public TrackMenu(ActionMap actionMap) {
		for (String currentKey: ACTION_KEYS) {
			add(actionMap.get(currentKey));
		}
	}

	
	public void setTrack(Track track) {
		track = track;
	}
	
	public Track getTrack() {
		return track;
	}
	
	@Override
	public void popupMenuCanceled(PopupMenuEvent evt) {}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
		
		
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent evt) {
		// TODO Auto-generated method stub
		
	}

}
