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
package edu.yu.einstein.genplay.gui.popupMenu.layerMenu;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JSeparator;

import edu.yu.einstein.genplay.gui.popupMenu.TrackMenu;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.VersionedLayer;

/**
 * Abstract class extended by the different types of layer menus.
 * A layer menu is a menu containing all the menu items available for the selected layer type
 * @author Julien Lajugie
 */
public abstract class AbstractLayerMenu extends JMenu {

	private static final long serialVersionUID = -8960096087763445313L; // generated serial ID


	/**
	 * Creates an instance of {@link TrackMenu}
	 * @param layer {@link Layer} associated to this menu
	 */
	public AbstractLayerMenu(Layer<?> layer) {
		super(layer.getName());
		for (Action currentAction: getLayerMenuActions()) {
			if (currentAction == null) {
				addSeparator();
			} else {
				currentAction.putValue("Layer", layer);
				add(currentAction);
			}
		}
		if (layer instanceof VersionedLayer) {
			JMenu versionedMenu = new VersionedLayerMenu((VersionedLayer<?>) layer);
			add(versionedMenu, 0);
			add(new JSeparator(), 1);
		}
	}


	/**
	 * @return the list of all the actions available for this menu.
	 * The menu items appears in the same order as the actions in this array.
	 * A null action inserts a separator.
	 */
	protected abstract Action[] getLayerMenuActions();
}
