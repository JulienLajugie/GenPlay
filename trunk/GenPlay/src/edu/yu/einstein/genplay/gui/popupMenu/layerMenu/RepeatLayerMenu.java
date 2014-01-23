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
package edu.yu.einstein.genplay.gui.popupMenu.layerMenu;

import javax.swing.Action;

import edu.yu.einstein.genplay.gui.action.layer.repeatLayer.RFLAGenerateMask;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.RepeatLayer;

/**
 * Menu containing all the actions available for a {@link RepeatLayer}
 * @author Julien Lajugie
 */
public class RepeatLayerMenu extends AbstractLayerMenu {

	private static final long serialVersionUID = -1479583400782469870L; // generated serial ID


	/**
	 * Creates an instance of {@link RepeatLayerMenu}
	 * @param layer
	 */
	public RepeatLayerMenu(Layer<?> layer) {
		super(layer);
	}

	
	@Override
	protected Action[] getLayerMenuActions() {
		Action[] actions = {
				new RFLAGenerateMask()
		};
		return actions;
	}
}
