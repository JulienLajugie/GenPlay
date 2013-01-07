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

import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAAddConstant;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAAverage;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAChangeBinSize;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAConcatenate;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;

/**
 * Menu containing all the actions available for a {@link BinLayer}
 * @author Julien Lajugie
 */
public class BinLayerMenu extends AbstractLayerMenu {

	private static final long serialVersionUID = -3827457549561342225L; // generated ID


	/**
	 * 
	 * @param layer the layer for the action
	 */
	public BinLayerMenu(Layer<?> layer) {
		super(layer);
	}


	@Override
	protected Action[] getLayerMenuActions() {
		Action[] actions = {
				new BLAAddConstant(),
				new BLAAverage(),
				new BLAChangeBinSize(),
				new BLAConcatenate()
			};
		return actions;
	}
}
