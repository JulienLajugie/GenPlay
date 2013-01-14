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
package edu.yu.einstein.genplay.gui.dialog.layerSettings;

import java.awt.Color;

import edu.yu.einstein.genplay.gui.track.layer.ColoredLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;


/**
 * Class used to stores the data of a layer settings table row
 * @author Julien Lajugie
 */
public class LayerSettingsRow {

	private final Layer<?> 	layer;			// layer of the row
	private String 			layerName;		// name of the layer
	private Color 			layerColor;		// color of the layer
	private boolean 		isLayerVisible;	// true if the layer is visible
	private boolean 		isLayerActive;	// true if the layer is the active layer


	/**
	 * Creates an instance of {@link LayerSettingsRow}
	 * @param layer
	 */
	public LayerSettingsRow(Layer<?> layer) {
		this.layer = layer;
		layerName = layer.getName();
		if (layer instanceof ColoredLayer) {
			layerColor = ((ColoredLayer) layer).getColor();
		}
		isLayerVisible = layer.isVisible();
	}


	/**
	 * @return the layerName
	 */
	public String getLayerName() {
		return layerName;
	}


	/**
	 * @param layerName the layerName to set
	 */
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}


	/**
	 * @return the layerColor
	 */
	public Color getLayerColor() {
		return layerColor;
	}


	/**
	 * @param layerColor the layerColor to set
	 */
	public void setLayerColor(Color layerColor) {
		this.layerColor = layerColor;
	}


	/**
	 * @return the isLayerVisible
	 */
	public boolean isLayerVisible() {
		return isLayerVisible;
	}


	/**
	 * @param isLayerVisible the isLayerVisible to set
	 */
	public void setLayerVisible(boolean isLayerVisible) {
		this.isLayerVisible = isLayerVisible;
	}


	/**
	 * @return the isLayerActive
	 */
	public boolean isLayerActive() {
		return isLayerActive;
	}


	/**
	 * @param isLayerActive the isLayerActive to set
	 */
	public void setLayerActive(boolean isLayerActive) {
		this.isLayerActive = isLayerActive;
	}


	/**
	 * @return the layer
	 */
	public Layer<?> getLayer() {
		return layer;
	}
}
