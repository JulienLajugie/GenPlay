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

import javax.swing.JMenuItem;

import edu.yu.einstein.genplay.gui.action.curveTrack.CTAAppearance;
import edu.yu.einstein.genplay.gui.track.CurveTrack;
import edu.yu.einstein.genplay.gui.trackList.TrackList;


/**
 * Abstract class. Popup menus for a {@link CurveTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class CurveTrackMenu extends ScoredTrackMenu {

	private static final long serialVersionUID = -767811267010609433L; 	// generated ID
	private final JMenuItem 				jmiAppearance;				// menu item appearance
	private final VersionedTrackMenuItems 	versionedTrackMenuItems;	// versioned track menu items
	
		
	/**
	 * Creates an instance of {@link CurveTrackMenu}
	 */
	public CurveTrackMenu(TrackList tl) {
		super(tl);		
		jmiAppearance= new JMenuItem(actionMap.get(CTAAppearance.ACTION_KEY));
		versionedTrackMenuItems = new VersionedTrackMenuItems(this, trackList);
		
		add(jmiAppearance);
		addSeparator();
		versionedTrackMenuItems.addItemsToMenu();
	}
}
