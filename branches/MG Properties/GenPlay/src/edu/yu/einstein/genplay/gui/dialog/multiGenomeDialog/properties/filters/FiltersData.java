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

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.IDFilter;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class FiltersData {

	/** Index used for vcf file column */
	public static final int VCF_FILE_INDEX 	= 0;
	/** Index used for the vcf header id column */
	public static final int ID_INDEX 	= 1;
	/** Index used for the filter column */
	public static final int FILTER_INDEX 	= 2;
	/** Index used for track column */
	public static final int TRACK_INDEX 	= 3;

	private VCFReader			reader;			// vcf reader
	private VCFHeaderType 		id;				// vcf header id
	private IDFilter			filter;			// filter value
	private Track<?>[] 			trackList;		// list of track


	/**
	 * Constructor of {@link FiltersData}
	 */
	protected FiltersData() {
		this.reader = null;
		this.id = null;
		this.filter = null;
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
			IDFilter filter, Track<?>[] trackList) {
		this.reader = reader;
		this.id = id;
		this.filter = filter;
		this.trackList = trackList;
	}


	//////////////////// Setters
	
	/**
	 * @param reader the reader to set
	 */
	protected void setReader(VCFReader reader) {
		this.reader = reader;
	}

	/**
	 * @param id the id to set
	 */
	protected void setId(VCFHeaderType id) {
		this.id = id;
	}

	/**
	 * @param filter the filter to set
	 */
	protected void setFilter(IDFilter filter) {
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
	 * @return the reader
	 */
	public VCFReader getReader() {
		return reader;
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
	public IDFilter getFilter() {
		return filter;
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
		return reader.getFile().getName();
	}
	
	/**
	 * @return the variantList
	 */
	public String getIDForDisplay() {
		return id.getId();
	}

	/**
	 * @return the filter
	 */
	public String getFilterForDisplay() {
		return filter.toStringForDisplay();
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
