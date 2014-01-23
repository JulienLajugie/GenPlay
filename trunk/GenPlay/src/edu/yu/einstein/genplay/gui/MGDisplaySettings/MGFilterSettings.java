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
package edu.yu.einstein.genplay.gui.MGDisplaySettings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.filters.FiltersData;
import edu.yu.einstein.genplay.gui.track.layer.Layer;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGFilterSettings implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4120007365169339324L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	private List<FiltersData> filtersList;			// List of filters
	private List<FiltersData> copiedFiltersList;	// List of filters


	/**
	 * Constructor of {@link MGFilterSettings}
	 */
	protected MGFilterSettings () {
		filtersList = new ArrayList<FiltersData>();
		copiedFiltersList = null;
	}


	/**
	 * When pasting a layer, associated filters settings to the copying layer must be given to the pasting layer.
	 * This method create duplicates of the settings related to the copied layer updated for the pasted layer.
	 * @param copiedLayer	the copied layer
	 * @param newLayer		the pasted layer
	 */
	public void copyData (Layer<?> copiedLayer, Layer<?> newLayer) {
		List<FiltersData> filterList = getFiltersForLayer(copiedLayer);
		if (filterList != null) {
			for (FiltersData data: filterList) {
				Layer<?>[] layer = {newLayer};
				FiltersData newData = new FiltersData(data.getMGFilter(), layer);
				if (!filterList.contains(newData)) {
					filtersList.add(newData);
				}
			}
		}
	}


	/**
	 * Create a copy of the information related to the given layer in the temporary list.
	 * This method is used when multi genome information cannot be serialized.
	 * @param layer the layer to save information
	 */
	public void copyTemporaryFilters(Layer<?> layer) {
		copiedFiltersList = getFiltersForLayer(layer);
	}


	/**
	 * When deleting a layer, all its settings must be deleted.
	 * The setting of a layer can be mixed with the ones of other tracks.
	 * Therefore, deleting settings must be processed carefully, taking into account the other layer.
	 * @param deleteLayer the deleted layer
	 */
	public void deleteData (Layer<?> deleteLayer) {
		List<FiltersData> filterList = getFiltersForLayer(deleteLayer);
		if (filterList != null) {
			for (FiltersData data: filterList) {
				Layer<?>[] layers = data.getLayers();
				if (layers.length == 1) {
					filtersList.remove(data);
				} else {
					Layer<?>[] newLayers = new Layer<?>[layers.length - 1];
					int cpt = 0;
					for (Layer<?> layer: layers) {
						if (!deleteLayer.toString().equals(layer.toString())) {
							newLayers[cpt] = layer;
							cpt++;
						}
					}
					data.setLayers(newLayers);
				}
			}
		}
	}


	/**
	 * @return all {@link MGFilter}
	 */
	public List<MGFilter> getAllMGFilters () {
		List<MGFilter> mgFiltersList = new ArrayList<MGFilter>();
		for (FiltersData filterData: filtersList) {
			mgFiltersList.add(filterData.getMGFilter());
		}
		return mgFiltersList;
	}


	/**
	 * @return a duplicate of the file filters List
	 */
	public List<FiltersData> getDuplicatedFileFiltersList() {
		List<FiltersData> duplicate = new ArrayList<FiltersData>();
		for (FiltersData data: filtersList) {
			duplicate.add(data.getDuplicate());
		}
		return duplicate;
	}


	/**
	 * Creates the list of filters according to a layer
	 * @param layer the layer
	 * @return		its list of filters
	 */
	public List<FiltersData> getFiltersForLayer (Layer<?> layer) {
		List<FiltersData> list = new ArrayList<FiltersData>();

		for (FiltersData data: filtersList) {
			Layer<?>[] layers = data.getLayers();
			for (Layer<?> currentLayer: layers) {
				if (currentLayer.toString().equals(layer.toString())) {
					list.add(data);
					break;
				}
			}
		}

		return list;
	}


	/**
	 * @return the filtersList
	 */
	public List<FiltersData> getFiltersList() {
		return filtersList;
	}


	/**
	 * Creates the list of {@link MGFilter} according to a layer
	 * @param layer the layer
	 * @return		its list of filters
	 */
	public List<MGFilter> getMGFiltersForTrack (Layer<?> layer) {
		List<MGFilter> mgFiltersList = new ArrayList<MGFilter>();

		for (FiltersData filterData: filtersList) {
			Layer<?>[] layers = filterData.getLayers();
			for (Layer<?> currentTrack: layers) {
				if (currentTrack.equals(layer)) {
					if (filterData.getMGFilter() != null) {
						mgFiltersList.add(filterData.getMGFilter());
					}
				}
			}
		}

		return mgFiltersList;
	}


	/**
	 * Copy the information from the temporary list to the actual list changing their target layer.
	 * It does not erase the temporary list in order to use it again later on.
	 * @param layer the new layer for the information
	 */
	public void pasteTemporaryFilters (Layer<?> layer) {
		if (copiedFiltersList != null) {
			for (FiltersData data: copiedFiltersList) {
				Layer<?>[] layers = {layer};
				FiltersData newData = new FiltersData(data.getMGFilter(), layers);
				filtersList.add(newData);
			}
		}
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		filtersList = (List<FiltersData>) in.readObject();
		copiedFiltersList = null;
	}


	/**
	 * When a new layer is loaded, the settings will still refer to the previous layer if this method is not called.
	 * It will replace the references to the old layer by the one of the new layer.
	 * @param oldLayer the old layer
	 * @param newLayer the new layer
	 */
	public void replaceTrack (Layer<?> oldLayer, Layer<?> newLayer) {
		for (FiltersData filter: filtersList) {
			filter.replaceLayer(oldLayer, newLayer);
		}
	}


	/**
	 * @param filtersList the filtersList to set
	 */
	public void setFiltersSettings(List<FiltersData> filtersList) {
		this.filtersList = filtersList;
	}


	/**
	 * Show the settings
	 */
	public void showSettings () {
		System.out.println("===== FILTERS");
		for (FiltersData data: filtersList) {
			System.out.println("ID: " + data.getIDForDisplay() + "; Filter: " + data.getFilterForDisplay());
		}
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(filtersList);
	}
}
