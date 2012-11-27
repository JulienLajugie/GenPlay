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

import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.filters.FiltersData;
import edu.yu.einstein.genplay.gui.old.track.Track;

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
		copiedFiltersList = null;
	}


	/**
	 * Constructor of {@link MGFilterSettings}
	 */
	protected MGFilterSettings () {
		filtersList = new ArrayList<FiltersData>();
		copiedFiltersList = null;
	}


	/**
	 * @return the filtersList
	 */
	public List<FiltersData> getFiltersList() {
		return filtersList;
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
	 * @param filtersList the filtersList to set
	 */
	public void setFiltersSettings(List<FiltersData> filtersList) {
		this.filtersList = filtersList;
	}


	/**
	 * Create a copy of the information related to the given track in the temporary list.
	 * This method is used when multi genome information cannot be serialized.
	 * @param track the track to save information
	 */
	public void copyTemporaryFilters(Track<?> track) {
		this.copiedFiltersList = getFiltersForTrack(track);
	}


	/**
	 * Copy the information from the temporary list to the actual list changing their target track.
	 * It does not erase the temporary list in order to use it again later on.
	 * @param track the new track for the information
	 */
	public void pasteTemporaryFilters (Track<?> track) {
		if (copiedFiltersList != null) {
			for (FiltersData data: copiedFiltersList) {
				Track<?>[] tracks = {track};
				FiltersData newData = new FiltersData(data.getMGFilter(), tracks);
				filtersList.add(newData);
			}
		}
	}



	/**
	 * Creates the list of {@link MGFilter} according to a track
	 * @param track the track
	 * @return		its list of filters
	 */
	public List<MGFilter> getMGFiltersForTrack (Track<?> track) {
		List<MGFilter> mgFiltersList = new ArrayList<MGFilter>();

		for (FiltersData filterData: filtersList) {
			Track<?>[] trackList = filterData.getTrackList();
			for (Track<?> currentTrack: trackList) {
				if (currentTrack.equals(track)) {
					if (filterData.getMGFilter() != null) {
						mgFiltersList.add(filterData.getMGFilter());
					}
				}
			}
		}

		return mgFiltersList;
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
	 * Creates the list of filters according to a track
	 * @param track the track
	 * @return		its list of filters
	 */
	public List<FiltersData> getFiltersForTrack (Track<?> track) {
		List<FiltersData> list = new ArrayList<FiltersData>();

		for (FiltersData data: filtersList) {
			Track<?>[] trackList = data.getTrackList();
			for (Track<?> currentTrack: trackList) {
				if (currentTrack.toString().equals(track.toString())) {
					list.add(data);
					break;
				}
			}
		}

		return list;
	}


	/**
	 * When pasting a track, associated filters settings to the copying track must be given to the pasting track.
	 * This method create duplicates of the settings related to the copied track updated for the pasted track.
	 * @param copiedTrack	the copied track
	 * @param newTrack		the pasted track
	 */
	public void copyData (Track<?> copiedTrack, Track<?> newTrack) {
		List<FiltersData> filterList = getFiltersForTrack(copiedTrack);
		if (filterList != null) {
			for (FiltersData data: filterList) {
				Track<?>[] track = {newTrack};
				FiltersData newData = new FiltersData(data.getMGFilter(), track);
				if (!filterList.contains(newData)) {
					filtersList.add(newData);
				}
			}
		}
	}


	/**
	 * When deleting a track, all its settings must be deleted.
	 * The setting of a track can be mixed with the ones of other tracks.
	 * Therefore, deleting settings must be processed carefully, taking into account the other track.
	 * @param deleteTrack the deleted track
	 */
	public void deleteData (Track<?> deleteTrack) {
		List<FiltersData> filterList = getFiltersForTrack(deleteTrack);
		if (filterList != null) {
			for (FiltersData data: filterList) {
				Track<?>[] trackList = data.getTrackList();
				if (trackList.length == 1) {
					filtersList.remove(data);
				} else {
					Track<?>[] newTrackList = new Track<?>[trackList.length - 1];
					int cpt = 0;
					for (Track<?> track: trackList) {
						if (!deleteTrack.toString().equals(track.toString())) {
							newTrackList[cpt] = track;
							cpt++;
						}
					}
					data.setTrackList(newTrackList);
				}
			}
		}
	}


	/**
	 * When a new track is loaded, the settings will still refer to the previous track if this method is not called.
	 * It will replace the references to the old track by the one of the new track.
	 * @param oldTrack the old track
	 * @param newTrack the new track
	 */
	public void replaceTrack (Track<?> oldTrack, Track<?> newTrack) {
		for (FiltersData filter: filtersList) {
			filter.replaceTrack(oldTrack, newTrack);
		}
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
