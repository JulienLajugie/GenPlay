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
package edu.yu.einstein.genplay.gui.MGDisplaySettings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFilter;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filters.FiltersData;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGFilterSettings implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4120007365169339324L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	private List<FiltersData> filtersList;	// List of filters


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(filtersList);
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
	}


	/**
	 * Constructor of {@link MGFilterSettings}
	 */
	protected MGFilterSettings () {
		filtersList = new ArrayList<FiltersData>();
	}


	/**
	 * @return the filtersList
	 */
	public List<FiltersData> getFiltersList() {
		return filtersList;
	}

	/**
	 * @param filtersList the filtersList to set
	 */
	public void setFiltersSettings(List<FiltersData> filtersList) {
		this.filtersList = filtersList;
		
	}


	/**
	 * Creates the list of VCF filters according to a track
	 * @param track the track
	 * @return		its list of filters
	 */
	public List<VCFFilter> getVCFFiltersForTrack (Track<?> track) {
		List<VCFFilter> vcfFiltersList = new ArrayList<VCFFilter>();

		for (FiltersData filterData: filtersList) {
			Track<?>[] trackList = filterData.getTrackList();
			for (Track<?> currentTrack: trackList) {
				if (currentTrack.equals(track)) {
					if (filterData.getVCFFilter() != null) {
						vcfFiltersList.add(filterData.getVCFFilter());
					}
				}
			}
		}
		
		return vcfFiltersList;
	}
	
	
	/**
	 * @return all VCF filters
	 */
	public List<VCFFilter> getAllVCFFilters () {
		List<VCFFilter> vcfFiltersList = new ArrayList<VCFFilter>();
		
		for (FiltersData filterData: filtersList) {
			vcfFiltersList.add(filterData.getVCFFilter());
		}
		
		return vcfFiltersList;	
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

}
