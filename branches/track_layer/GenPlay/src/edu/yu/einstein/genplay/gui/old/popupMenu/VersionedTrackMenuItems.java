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

import javax.swing.JMenuItem;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import edu.yu.einstein.genplay.gui.old.action.versionedTrack.VTAHistory;
import edu.yu.einstein.genplay.gui.old.action.versionedTrack.VTARedo;
import edu.yu.einstein.genplay.gui.old.action.versionedTrack.VTAReset;
import edu.yu.einstein.genplay.gui.old.action.versionedTrack.VTAUndo;
import edu.yu.einstein.genplay.gui.old.track.VersionedTrack;
import edu.yu.einstein.genplay.gui.old.trackList.TrackList;


/**
 * This class is useful for the popup menus implementing the VersionedTrack
 * interface.  It creates the "Show History", "Undo", "Redo", "Reset" menu items.
 * The method addItemsToMenu allows to add these items to a specified menu.
 * @author Julien Lajugie
 * @version 0.1
 */
public class VersionedTrackMenuItems implements PopupMenuListener {

	private final TrackMenu 	menu;			// menu containing the items
	private final TrackList		trackList;		// track list associated to the menu
	private final JMenuItem		jmiHistory;		// menu item show history
	private final JMenuItem		jmiRedo;		// menu item redo last action
	private final JMenuItem		jmiReset;		// menu item reset track
	private final JMenuItem		jmiUndo;		// menu item undo last action


	/**
	 * Creates an instance of {@link VersionedTrackMenuItems}. 
	 * This class is useful for the popup menu implementing the VersionedTrack
	 * interface.  It creates the "Show History", "Undo", "Redo", "Reset" menu items.
	 * The method addItemsToMenu allows to add these items to the specified menu.
	 * @param menu
	 * @param trackList {@link TrackList} associated to the menu
	 */
	public VersionedTrackMenuItems(TrackMenu menu, final TrackList trackList) {
		this.menu = menu;
		this.trackList = trackList;
		jmiHistory = new JMenuItem(menu.actionMap.get(VTAHistory.ACTION_KEY));
		jmiRedo = new JMenuItem(menu.actionMap.get(VTARedo.ACTION_KEY));
		jmiReset = new JMenuItem(menu.actionMap.get(VTAReset.ACTION_KEY));
		jmiUndo = new JMenuItem(menu.actionMap.get(VTAUndo.ACTION_KEY));
		menu.addPopupMenuListener(this);
	}


	/**
	 * Adds the menu items of the VersionedTrackMenu to the menu specified during construction
	 */
	public void addItemsToMenu() {
		menu.add(jmiUndo);
		menu.add(jmiRedo);
		menu.add(jmiReset);
		menu.add(jmiHistory);
	}


	/**
	 * Checks if the undo / redo / reset functions are available when the menu pop up.
	 * Enables or disables the corresponding menu item accordingly.
	 */
	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
		jmiUndo.setEnabled(((VersionedTrack)trackList.getSelectedTrack()).isUndoable());
		jmiRedo.setEnabled(((VersionedTrack)trackList.getSelectedTrack()).isRedoable());
		jmiReset.setEnabled(((VersionedTrack)trackList.getSelectedTrack()).isResetable());

	}


	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {}


	@Override
	public void popupMenuCanceled(PopupMenuEvent arg0) {}
}
