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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filters;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFilter;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.IDFilterInterface;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class FiltersData implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = 2767629722281248634L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	
	/** Index used for vcf file column */
	public static final int VCF_FILE_INDEX 	= 0;
	/** Index used for the vcf header id column */
	public static final int ID_INDEX 	= 1;
	/** Index used for the filter column */
	public static final int FILTER_INDEX 	= 2;
	/** Index used for track column */
	public static final int TRACK_INDEX 	= 3;


	private VCFHeaderType 		id;				// vcf header id
	private VCFFilter			filter;
	private String				nonIDName;		// 
	private Track<?>[] 			trackList;		// list of track

	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(id);
		out.writeObject(filter);
		out.writeObject(nonIDName);
		out.writeObject(trackList);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		id = (VCFHeaderType) in.readObject();
		filter = (VCFFilter) in.readObject();
		nonIDName = (String) in.readObject();
		trackList = (Track[]) in.readObject();
	}
	

	/**
	 * Constructor of {@link FiltersData}
	 */
	protected FiltersData() {
		this.id = null;
		this.filter = null;
		this.nonIDName = null;
		this.trackList = null;
	}


	/**
	 * Constructor of {@link FiltersData}
	 * @param genome		name of the genome
	 * @param variantList	list of variation
	 * @param colorList		list of color
	 * @param trackList		list of track
	 */
	protected FiltersData(VCFReader reader, VCFHeaderType id,
			IDFilterInterface filter, Track<?>[] trackList) {
		this.id = id;
		this.filter = new VCFFilter(filter, reader);
		this.nonIDName = null;
		this.trackList = trackList;
	}


	/**
	 * Constructor of {@link FiltersData}
	 * @param genome		name of the genome
	 * @param variantList	list of variation
	 * @param colorList		list of color
	 * @param trackList		list of track
	 */
	protected FiltersData(VCFReader reader, String nonIDName,
			IDFilterInterface filter, Track<?>[] trackList) {
		this.id = null;
		this.filter = new VCFFilter(filter, reader);
		this.nonIDName = nonIDName;
		this.trackList = trackList;
	}


	//////////////////// Setters

	/**
	 * @param id the id to set
	 */
	protected void setNonIdName (String nonIDName) {
		this.nonIDName = nonIDName;
	}


	/**
	 * @param id the id to set
	 */
	protected void setId(VCFHeaderType id) {
		this.id = id;
	}

	
	/**
	 * @param filter the VCF filter to set
	 */
	protected void setVCFFilter (VCFFilter filter) {
		this.filter = filter;
	}
	

	/**
	 * @param trackList the trackList to set
	 */
	protected void setTrackList(Track<?>[] trackList) {
		this.trackList = trackList;
	}


	//////////////////// Getters

	/**
	 * @return the VCF filter
	 */
	public VCFFilter getVCFFilter () {
		return filter;
	}
	
	/**
	 * @return the reader
	 */
	public VCFReader getReader() {
		return this.filter.getReader();
	}

	/**
	 * @return the id
	 */
	public String getNonIdName() {
		return nonIDName;
	}

	/**
	 * @return the id
	 */
	public VCFHeaderType getId() {
		return id;
	}

	/**
	 * @return the filter
	 */
	public IDFilterInterface getFilter() {
		return this.filter.getFilter();
	}

	/**
	 * @return the trackList
	 */
	public Track<?>[] getTrackList() {
		return trackList;
	}


	//////////////////// Getters for display

	/**
	 * @return the genome
	 */
	public String getReaderForDisplay() {
		return getReader().getFile().getName();
	}

	/**
	 * @return the variantList
	 */
	public String getIDForDisplay() {
		if (id != null) {
			return id.getId();
		} else {
			return nonIDName;
		}
	}

	/**
	 * @return the filter
	 */
	public String getFilterForDisplay() {
		return getFilter().toStringForDisplay();
	}

	/**
	 * @return the trackList
	 */
	public String getTrackListForDisplay() {
		String text = "";
		for (int i = 0; i < trackList.length; i++) {
			text += trackList[i];
			if (i < (trackList.length - 1)) {
				text += ", ";
			}
		}
		return text;
	}

}
