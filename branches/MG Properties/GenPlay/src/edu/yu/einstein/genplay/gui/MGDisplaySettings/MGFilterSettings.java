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

import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.IDFilter;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filters.FiltersData;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGFilterSettings {

	
	private List<FiltersData> filtersList;	// List of filters

	
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
	 * Creates the list of filters according to a track
	 * @param track the track
	 * @return		its list of filters
	 */
	public List<IDFilter> getFiltersForTrack (Track<?> track) {
		List<IDFilter> list = new ArrayList<IDFilter>();
		
		for (FiltersData data: filtersList) {
			Track<?>[] trackList = data.getTrackList();
			for (Track<?> currentTrack: trackList) {
				if (currentTrack.equals(track)) {
					list.add(data.getFilter());
					break;
				}
			}
		}
		
		return list;
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
