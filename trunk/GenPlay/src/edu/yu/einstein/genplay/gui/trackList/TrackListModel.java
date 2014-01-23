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
package edu.yu.einstein.genplay.gui.trackList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowListener;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.Layer;

/**
 * Model that manages a list of tracks
 * @author Julien Lajugie
 */
public class TrackListModel implements Serializable {

	private static final long serialVersionUID = 6131854814047955278L; // generated serial id

	/**
	 * Creates a track list containing only empty tracks with the default
	 * project settings (number and height of tracks)
	 * @return a list of tracks
	 */
	private static Track[] createDefaultTrackList() {
		int trackCount = ProjectManager.getInstance().getProjectConfiguration().getTrackCount();
		Track[] tracks = new Track[trackCount];
		for (int i = 0; i < trackCount; i++) {
			tracks[i] = new Track(i + 1);
		}
		return tracks;
	}


	private final List<ListDataListener>	dataListeners;		// list of listeners that is notified each time a change to the data model occurs
	private Track[] 						tracks; 			// array of tracks displayed in the table


	/**
	 * Creates an instance of {@link TrackListModel}
	 */
	public TrackListModel() {
		this(createDefaultTrackList());
	}


	/**
	 * Creates an instance of {@link TrackListModel}
	 * @param tracks tracks of the model
	 */
	public TrackListModel(Track[] tracks) {
		dataListeners = new ArrayList<ListDataListener>();
		setTracks(tracks);
	}


	/**
	 * Adds a listener to the list that's notified each time a change to the data model occurs.
	 * @param dataListener
	 */
	public void addListDataListener(ListDataListener dataListener) {
		if (!dataListeners.contains(dataListener)) {
			dataListeners.add(dataListener);
		}
	}


	/**
	 * Deletes the track at the specified row
	 * @param row
	 */
	public void deleteTrack(int row) {
		// removes references to avoid memory leaks
		for (int i = row + 1; i < tracks.length; i++) {
			tracks[i - 1] = tracks[i];
		}
		tracks[tracks.length - 1] = new Track(tracks.length);
		updateTracksRegisteredToProjectWindow();
		// we notify the listeners that the data changed
		ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, row, row);
		notifyListeners(event);
	}


	/**
	 * Deletes the specified track if the track is present in the list
	 * @param track
	 */
	public void deleteTrack(Track track) {
		int trackIndex = indexOf(track);
		if (trackIndex != -1) {
			deleteTrack(trackIndex);
		}
	}


	/**
	 * @return the list of all the layers displayed in the {@link TrackListPanel}
	 */
	public List<Layer<?>> getAllLayers() {
		List<Layer<?>> allLayers = new ArrayList<Layer<?>>();
		for (Track currentTrack: tracks) {
			allLayers.addAll(currentTrack.getLayers());
		}
		return allLayers;
	}


	/**
	 * @param row
	 * @return the {@link Track} at the specified row
	 */
	public Track getTrack(int row) {
		return tracks[row];
	}


	/**
	 * @return the number of track
	 */
	public int getTrackCount() {
		return tracks.length;
	}


	/**
	 * @return the tracks displayed in the JTable
	 */
	public Track[] getTracks() {
		return tracks;
	}


	/**
	 * @param track a Track
	 * @return the index of the specified track in the data list if found.  -1 if the specified track is not found
	 */
	public int indexOf(Track track) {
		for (int i = 0; i < tracks.length; i++) {
			if (track == tracks[i]) {
				return i;
			}
		}
		return -1;
	}


	/**
	 * Insert a row right before the specified row
	 * @param row
	 * @param track track to insert
	 */
	public void insertTrack(int row, Track track) {
		for (int i = tracks.length - 2; i >= row; i--) {
			tracks[i + 1] = tracks[i];
		}
		tracks[row] = track;
		updateTracksRegisteredToProjectWindow();
		// we notify the listeners that the data changed
		ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, row, row);
		notifyListeners(event);
	}


	/**
	 * Notifies all the {@link ListDataListener} that the data changed
	 * @param event
	 */
	private void notifyListeners(ListDataEvent event) {
		switch (event.getType()) {
		case ListDataEvent.CONTENTS_CHANGED:
			for (ListDataListener listener: dataListeners) {
				listener.contentsChanged(event);
			}
		case ListDataEvent.INTERVAL_ADDED:
			for (ListDataListener listener: dataListeners) {
				listener.intervalAdded(event);
			}
		case ListDataEvent.INTERVAL_REMOVED:
			for (ListDataListener listener: dataListeners) {
				listener.intervalRemoved(event);
			}
		}
	}


	/**
	 * Removes a listener from the list that's notified each time a change to the data model occurs.
	 * @param dataListener
	 */
	public void removeListDataListener(ListDataListener dataListener)  {
		dataListeners.remove(dataListener);
	}


	/**
	 * Sets the {@link Track} at the specified row
	 * @param track a track
	 * @param row row where the track needs to be set
	 */
	public void setTrack(Track track, int row) {
		tracks[row] = track;
		updateTracksRegisteredToProjectWindow();
		// we notify the listeners that the data changed
		ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, row, row);
		notifyListeners(event);
	}


	/**
	 * @param tracks the tracks to display in the JTable
	 */
	public void setTracks(Track[] tracks) {
		this.tracks = tracks;
		updateTracksRegisteredToProjectWindow();
		// we notify the listeners that the data changed
		ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, tracks.length - 1);
		notifyListeners(event);
	}


	/**
	 * This method update the list of tracks registered as {@link GenomeWindowListener} in the
	 * {@link ProjectWindow}. This method should be used every time the list of track is modified.
	 * All the tracks not present in this model are removed from the list of listeners.
	 * All the tracks of this model that are not registered are added to the list of listeners.
	 */
	public void updateTracksRegisteredToProjectWindow() {
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		// removes all the track listener from the list of listeners of the project window
		for (GenomeWindowListener currentGWL: projectWindow.getGenomeWindowListeners()) {
			if (currentGWL instanceof Track) {
				projectWindow.removeGenomeWindowListener(currentGWL);
			}
		}
		// registers the tracks
		for (Track currentTrack: tracks) {
			projectWindow.addGenomeWindowListener(currentTrack);
		}
	}
}
