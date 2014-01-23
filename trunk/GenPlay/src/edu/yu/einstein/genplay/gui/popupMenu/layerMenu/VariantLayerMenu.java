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

import edu.yu.einstein.genplay.gui.action.multiGenome.VCFAction.MGAVCFStatistics;
import edu.yu.einstein.genplay.gui.action.multiGenome.convert.MGASCWLConvert;
import edu.yu.einstein.genplay.gui.action.multiGenome.export.MGAGlobalVCFExport;
import edu.yu.einstein.genplay.gui.action.multiGenome.properties.MGAFilterProperties;
import edu.yu.einstein.genplay.gui.action.multiGenome.update.MGAVCFApplyGenotype;
import edu.yu.einstein.genplay.gui.action.track.TAEditVariantLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.variantLayer.VariantLayer;

/**
 * Menu containing all the actions available for a {@link VariantLayer}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public class VariantLayerMenu extends AbstractLayerMenu {

	private static final long serialVersionUID = -3827457549561342225L; // generated ID


	/**
	 * Creates an instance of {@link VariantLayerMenu}
	 * @param layer the layer for the action
	 */
	public VariantLayerMenu(Layer<?> layer) {
		super(layer);
	}


	@Override
	protected Action[] getLayerMenuActions() {
		Action[] actions = {
				new TAEditVariantLayer(),
				new MGAVCFStatistics(),
				null,
				new MGAFilterProperties(),
				null,
				new MGAGlobalVCFExport(),
				new MGASCWLConvert(),
				new MGAVCFApplyGenotype()
		};
		return actions;
	}

}
