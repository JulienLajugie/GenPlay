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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filterDialog.filters;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderBasicType;
import edu.yu.einstein.genplay.core.multiGenome.filter.FilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.IDFilterInterface;
import edu.yu.einstein.genplay.gui.track.layer.Layer;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class FiltersData implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = 2767629722281248634L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	/** Index used for layer column */
	public static final int LAYER_INDEX 	= 1;
	/** Index used for the vcf header id column */
	public static final int ID_INDEX 	= 2;
	/** Index used for the filter column */
	public static final int FILTER_INDEX 	= 3;
	/** Index used for vcf file column */
	public static final int VCF_FILE_INDEX 	= 4;

	private MGFilter	filter;
	private Layer<?>[] 	layers;		// list of layers


	/**
	 * Constructor of {@link FiltersData}
	 */
	protected FiltersData() {
		filter = null;
		layers = null;
	}


	/**
	 * Constructor of {@link FiltersData}
	 * @param filter 		the {@link MGFilter}
	 * @param layers		list of layers
	 */
	public FiltersData(MGFilter filter, Layer<?>[] layers) {
		this.filter = filter.getDuplicate();
		this.layers = layers;
	}


	/**
	 * @return a duplicate of the current object
	 */
	public FiltersData getDuplicate () {
		FiltersData duplicate = new FiltersData();
		duplicate.setMGFilter(getMGFilter().getDuplicate());
		duplicate.setLayers(getLayers());
		return duplicate;
	}


	/**
	 * @return the filter
	 */
	public FilterInterface getFilter() {
		return filter.getFilter();
	}


	//////////////////// Setters

	/**
	 * @return the filter
	 */
	public String getFilterForDisplay() {
		return getFilter().toStringForDisplay();
	}


	/**
	 * @return the variantList
	 */
	public String getIDForDisplay() {
		if (filter.getFilter() instanceof IDFilterInterface) {
			IDFilterInterface filter = (IDFilterInterface) this.filter.getFilter();
			if (filter.getHeaderType() instanceof VCFHeaderBasicType) {
				return filter.getColumnName().toString();
			}
			return filter.getHeaderType().getId();
		}
		return filter.getFilter().getName();
	}


	//////////////////// Getters

	/**
	 * @return the VCF filter
	 */
	public MGFilter getMGFilter () {
		return filter;
	}


	/**
	 * @return the reader
	 */
	public VCFFile getReader() {
		if (filter instanceof VCFFilter) {
			return ((VCFFilter)filter).getVCFFile();
		}
		return null;
	}


	/**
	 * @return the genome
	 */
	public String getReaderForDisplay() {
		return getReader().getFile().getName();
	}


	/**
	 * @return the list of layers
	 */
	public Layer<?>[] getLayers() {
		return layers;
	}


	//////////////////// Getters for display

	/**
	 * @return the layers list for display
	 */
	public String getLayersForDisplay() {
		String text = "";
		for (int i = 0; i < layers.length; i++) {
			text += layers[i];
			if (i < (layers.length - 1)) {
				text += ", ";
			}
		}
		return text;
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		filter = (MGFilter) in.readObject();
		layers = (Layer[]) in.readObject();
	}


	/**
	 * When a new layer is loaded, the settings will still refer to the previous layer if this method is not called.
	 * It will replace the references to the old layer by the one of the new layer.
	 * @param oldLayer the old layer
	 * @param newLayer the new layer
	 */
	public void replaceLayer (Layer<?> oldLayer, Layer<?> newLayer) {
		for (int i = 0; i < layers.length; i++) {
			if (layers[i].equals(oldLayer)) {
				layers[i] = newLayer;
			}
		}
	}


	/**
	 * @param filter the VCF filter to set
	 */
	public void setMGFilter (MGFilter filter) {
		this.filter = filter;
	}


	/**
	 * @param layers the layers to set
	 */
	public void setLayers(Layer<?>[] layers) {
		this.layers = layers;
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(filter);
		out.writeObject(layers);
	}
}
