/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.menu;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.action.track.TAAddLayer;
import edu.yu.einstein.genplay.gui.action.track.TAAddLayerFromDAS;
import edu.yu.einstein.genplay.gui.action.track.TAAddVariantLayer;
import edu.yu.einstein.genplay.gui.action.track.TACopy;
import edu.yu.einstein.genplay.gui.action.track.TACut;
import edu.yu.einstein.genplay.gui.action.track.TADelete;
import edu.yu.einstein.genplay.gui.action.track.TAInsert;
import edu.yu.einstein.genplay.gui.action.track.TAPasteOrDrop;
import edu.yu.einstein.genplay.gui.action.track.TASaveAsImage;
import edu.yu.einstein.genplay.gui.action.track.TASaveTrack;
import edu.yu.einstein.genplay.gui.action.track.TATrackSettings;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.menu.layerMenu.LayerMenuFactory;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.trackList.TrackListActionMap;

/**
 * Contextual menu showed when a track handle is right clicked
 * @author Julien Lajugie
 */
public class TrackMenu extends JPopupMenu implements PopupMenuListener {

	private static final long serialVersionUID = -7063797741454351041L; // generated serial ID

	/**
	 *  Keys of the actions of this menu as registered in the {@link TrackListActionMap}
	 *  The menu items appears in the same order as in this array
	 *  A null key inserts a separator
	 */
	private static final String[] ACTION_KEYS = {
		TAAddLayer.ACTION_KEY,
		TAAddLayerFromDAS.ACTION_KEY,
		null,
		TACopy.ACTION_KEY,
		TACut.ACTION_KEY,
		TAPasteOrDrop.ACTION_KEY,
		null,
		TAInsert.ACTION_KEY,
		TADelete.ACTION_KEY,
		null,
		TASaveTrack.ACTION_KEY,
		TASaveAsImage.ACTION_KEY,
		null,
		TATrackSettings.ACTION_KEY
	};

	private Track 				selectedTrack; 			// selected track
	private final List<JMenu> 	layerMenus; 			// list containing all the layer menus available for the selected track
	private final Separator		layerMenusSeparator;	// separator that separate the layer menus from the other elements of the track menu

	/**
	 * Creates an instance of {@link TrackMenu}
	 */
	public TrackMenu() {
		super();
		layerMenus = new ArrayList<JMenu>();
		layerMenusSeparator = new Separator();
		ActionMap actionMap = TrackListActionMap.getActionMap();
		for (String currentKey: ACTION_KEYS) {
			if (currentKey == null) {
				addSeparator();
			} else {
				add(actionMap.get(currentKey));
			}
		}
		addPopupMenuListener(this);
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			add(new JMenuItem(actionMap.get(TAAddVariantLayer.ACTION_KEY)), 1);
		}
	}


	/**
	 * @return the selected track
	 */
	public Track getTrack() {
		return selectedTrack;
	}


	@Override
	public void popupMenuCanceled(PopupMenuEvent evt) {}


	/**
	 * Removes the menus associated to the layers of the selected track when the menu is about to become invisible
	 */
	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
		if (!layerMenus.isEmpty()) {
			for (JMenu currentMenu: layerMenus) {
				remove(currentMenu);
			}
			layerMenus.clear();
			remove(layerMenusSeparator);
		}
		TrackListActionMap.getActionMap().get(TAPasteOrDrop.ACTION_KEY).setEnabled(true);
		selectedTrack = null;
	}


	/**
	 * Displays the menus associated to the layers of the selected track when the menu is about to become invisible
	 */
	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent evt) {
		if (selectedTrack != null) {
			boolean isPasteEnable = MainFrame.getInstance().getTrackListPanel().isPasteEnable();
			TrackListActionMap.getActionMap().get(TAPasteOrDrop.ACTION_KEY).setEnabled(isPasteEnable);
			Layer<?>[] trackLayers = selectedTrack.getLayers().getLayers();
			if (trackLayers != null) {
				int lastIndex = getComponentCount();
				for (Layer<?> currentLayer: trackLayers) {
					JMenu layerMenu = LayerMenuFactory.createLayerMenu(currentLayer);
					if (layerMenu != null) {
						add(layerMenu);
						layerMenus.add(layerMenu);
					}
				}
				// if there is at least one layer menu we add a separator right on top of it
				if (!layerMenus.isEmpty()) {
					add(layerMenusSeparator, lastIndex);
				}
			}
		}
	}


	/**
	 * Sets the selected track
	 * @param track
	 */
	public void setTrack(Track track) {
		selectedTrack = track;
	}
}
