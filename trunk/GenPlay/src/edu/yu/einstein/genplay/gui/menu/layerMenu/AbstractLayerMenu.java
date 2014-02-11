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
package edu.yu.einstein.genplay.gui.menu.layerMenu;

import java.awt.Color;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import edu.yu.einstein.genplay.gui.menu.TrackMenu;
import edu.yu.einstein.genplay.gui.track.layer.ColoredLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.VersionedLayer;
import edu.yu.einstein.genplay.util.colors.Colors;

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
		boolean enable = layer.isVisible();

		// if the layer is a coloredlayer the menu color will be the same as the layer
		if (layer instanceof ColoredLayer) {
			Color menuColor = ((ColoredLayer) layer).getColor();
			setForeground(menuColor);
		}
		setBackground(Colors.TRACK_HANDLE_BACKGROUND);
		// if the layer is a versioned layer we add the versionlayer sub-menu
		if (layer instanceof VersionedLayer) {
			VersionedLayerMenu.addVersionedOptionsToMenu(this,(VersionedLayer<?>) layer, enable);
			add(new JSeparator());
		}
		// add all the menu items for the layer
		for (Action currentAction: getLayerMenuActions()) {
			if (currentAction == null) {
				addSeparator();
			} else {
				currentAction.putValue("Layer", layer);
				JMenuItem jmi = new JMenuItem(currentAction);
				jmi.setEnabled(enable);
				add(jmi);
			}
		}
	}


	/**
	 * @return the list of all the actions available for this menu.
	 * The menu items appears in the same order as the actions in this array.
	 * A null action inserts a separator.
	 */
	protected abstract Action[] getLayerMenuActions();
}
