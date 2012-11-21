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
package edu.yu.einstein.genplay.gui.trackList;

import java.io.Serializable;

import edu.yu.einstein.genplay.gui.track.layer.LayeredTrack;

/**
 * Model that manage a list of track
 * @author Julien Lajugie
 */
public class TrackListModel implements Serializable {

	private static final long serialVersionUID = 6131854814047955278L; // generated id
	private LayeredTrack[] tracks; // array of tracks displayed in the table


	/**
	 * Creates an instance of {@link TrackListModel}
	 * @param tracks tracks to display in the JTable
	 */
	public TrackListModel(LayeredTrack[] tracks) {
		setTracks(tracks);
	}


	/**
	 * Delete the track at the specified row
	 * @param row
	 */
	public void deleteTrack(int row) {
		for (int i = row + 1; i < tracks.length; i++) {
			tracks[i - 1] = tracks[i];
		}
		tracks[tracks.length - 1] = new LayeredTrack(tracks.length);
	}


	/**
	 * @return the number of track
	 */
	public int getTrackCount() {
		return tracks.length;
	}


	/**
	 * @param row
	 * @return the {@link LayeredTrack} at the specified row
	 */
	public LayeredTrack getTrack(int row) {
		return getTrack(row);
	}


	/**
	 * @return the tracks displayed in the JTable
	 */
	public LayeredTrack[] getTracks() {
		return tracks;
	}


	/**
	 * Insert a row right before the specified row
	 * @param row
	 * @param track track to insert
	 */
	public void insertTrack(int row, LayeredTrack track) {
		for (int i = tracks.length - 2; i >= row; i--) {
			tracks[i + 1] = tracks[i];
		}
		tracks[row] = track;
	}


	/**
	 * Sets the {@link LayeredTrack} at the specified row
	 * @param track a track
	 * @param row row where the track needs to be set
	 */
	public void setTrack(LayeredTrack track, int row) {
		getTracks()[row] = track;
	}


	/**
	 * @param tracks the tracks to display in the JTable
	 */
	public void setTracks(LayeredTrack[] tracks) {
		this.tracks = tracks;
	}
}
