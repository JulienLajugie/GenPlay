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

import javax.swing.Action;

import edu.yu.einstein.genplay.gui.action.layer.sequenceLayer.NLACompare2NucleotideLists;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.NucleotideLayer;


/**
 * Menu containing all the actions available for a {@link NucleotideLayer}
 * @author Julien Lajugie
 */
public class NucleotideLayerMenu extends AbstractLayerMenu {

	private static final long serialVersionUID = 2877736062705248703L;  // generated ID


	public NucleotideLayerMenu(Layer<?> layer) {
		super(layer);
	}


	@Override
	protected Action[] getLayerMenuActions() {
		Action[] actions = {
				new NLACompare2NucleotideLists()
		};
		return actions;
	}
}
