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

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;

import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.Layer;

/**
 * Factory that creates a {@link JMenu} that inherit from {@link AbstractLayerMenu} with the actions for the specified layer
 * @author Julien Lajugie
 */
public class LayerMenuFactory {

	/**
	 * Creates a {@link JMenu} that inherit from {@link AbstractLayerMenu} with the actions for the specified layer
	 * @param layer a {@link Layer}
	 * @return a
	 * @throws IllegalArgumentException
	 */
	public static AbstractLayerMenu createLayerMenu(Layer<?> layer) {
		switch (layer.getType()) {
		case BIN_LAYER:
			return new BinLayerMenu(layer);
		case GENE_LAYER:
			return new GeneLayerMenu(layer);
		case MASK_LAYER:
			return new MaskLayerMenu(layer);
		case NUCLEOTIDE_LAYER:
			return new NucleotideLayerMenu(layer);
		case REPEAT_FAMILY_LAYER:
			return new RepeatLayerMenu(layer);
		case SIMPLE_SCW_LAYER:
			return new SCWLayerMenu(layer);
		case VARIANT_LAYER:
			return new VariantLayerMenu(layer);
		default :
			return null;
		}
	}


	/**
	 * @param track
	 * @return A list of the layer menus for all the layers of the specified track
	 */
	public static List<JMenu> createLayerMenusForTrack(Track track) {
		List<JMenu> menus = new ArrayList<JMenu>();
		if (track != null) {
			Layer<?>[] trackLayers = track.getLayers().getLayers();
			if (trackLayers != null) {
				for (Layer<?> currentLayer: trackLayers) {
					JMenu layerMenu = LayerMenuFactory.createLayerMenu(currentLayer);
					// active layer is in fold and italic
					if (layerMenu != null) {
						if (track.getActiveLayer() == currentLayer) {
							layerMenu.setFont(layerMenu.getFont().deriveFont(Font.ITALIC | Font.BOLD));
						} else {
							// all layers are in bold
							layerMenu.setFont(layerMenu.getFont().deriveFont(Font.BOLD));
						}
						menus.add(layerMenu);
					}
				}
			}
		}
		return menus;
	}
}
