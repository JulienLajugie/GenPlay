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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.filters;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFilter;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderBasicType;
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

	private VCFFilter			filter;
	private Track<?>[] 			trackList;		// list of track


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(filter);
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
		filter = (VCFFilter) in.readObject();
		trackList = (Track[]) in.readObject();
	}


	/**
	 * Constructor of {@link FiltersData}
	 */
	protected FiltersData() {
		this.filter = null;
		this.trackList = null;
	}


	/**
	 * Constructor of {@link FiltersData}
	 * @param reader 		the VCF file
	 * @param filter 		the filter
	 * @param trackList		list of track
	 */
	public FiltersData(VCFFile reader, IDFilterInterface filter, Track<?>[] trackList) {
		this.filter = new VCFFilter(filter, reader);
		this.trackList = trackList;
	}


	//////////////////// Setters

	/**
	 * @param filter the VCF filter to set
	 */
	public void setVCFFilter (VCFFilter filter) {
		this.filter = filter;
	}


	/**
	 * @param trackList the trackList to set
	 */
	public void setTrackList(Track<?>[] trackList) {
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
	public VCFFile getReader() {
		return this.filter.getVCFFile();
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
		VCFHeaderType header = this.filter.getFilter().getHeaderType();
		IDFilterInterface filter = this.filter.getFilter();
		if (filter.getHeaderType() instanceof VCFHeaderBasicType) {
			return filter.getColumnName().toString();
		}
		return header.getId();
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


	/**
	 * @return a duplicate of the current object
	 */
	public FiltersData getDuplicate () {
		FiltersData duplicate = new FiltersData();
		duplicate.setVCFFilter(getVCFFilter().getDuplicate());
		duplicate.setTrackList(getTrackList());
		return duplicate;
	}

	
	/**
	 * When a new track is loaded, the settings will still refer to the previous track if this method is not called.
	 * It will replace the references to the old track by the one of the new track.
	 * @param oldTrack the old track
	 * @param newTrack the new track
	 */
	public void changeTrack (Track<?> oldTrack, Track<?> newTrack) {
		for (int i = 0; i < trackList.length; i++) {
			if (trackList[i].equals(oldTrack)) {
				trackList[i] = newTrack;
			}
		}
	}
}
