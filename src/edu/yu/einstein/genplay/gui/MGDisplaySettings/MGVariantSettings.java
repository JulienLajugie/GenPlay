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

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.variants.VariantData;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGVariantSettings implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = -8887751815193182599L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	private List<VariantData> variantsList;			// List of settings for variants display
	private List<VariantData> copiedStripesList;	// List of settings for variants display


	/**
	 * Constructor of {@link MGVariantSettings}
	 */
	protected MGVariantSettings () {
		variantsList = new ArrayList<VariantData>();
		copiedStripesList = null;
	}


	/**
	 * When pasting a track, associated stripes settings to the copying track must be given to the pasting track.
	 * This method create duplicates of the settings related to the copied track updated for the pasted track.
	 * @param copiedTrack	the copied track
	 * @param newTrack		the pasted track
	 */
	public void copyData (Track copiedTrack, Track newTrack) {
		List<VariantData> stripeList = getVariantsForTrack(copiedTrack);
		if (stripeList != null) {
			for (VariantData data: stripeList) {
				Track[] track = {newTrack};
				VariantData newData = new VariantData(data.getGenome(), data.getAlleleType(), data.getVariationTypeList(), data.getColorList(), track);
				if (!stripeList.contains(newData)) {
					variantsList.add(newData);
				}
			}
		}
	}


	/**
	 * Create a copy of the information related to the given track in the temporary list.
	 * This method is used when multi genome information cannot be serialized.
	 * @param track the track to save information
	 */
	public void copyTemporaryStripes(Track track) {
		copiedStripesList = getVariantsForTrack(track);
	}


	/**
	 * When deleting a track, all its settings must be deleted.
	 * The setting of a track can be mixed with the ones of other tracks.
	 * Therefore, deleting settings must be processed carefully, taking into account the other track.
	 * @param deleteTrack the deleted track
	 */
	public void deleteData (Track deleteTrack) {
		List<VariantData> stripeList = getVariantsForTrack(deleteTrack);
		if (stripeList != null) {
			for (VariantData data: stripeList) {
				Track[] trackList = data.getTrackList();
				if (trackList.length == 1) {
					variantsList.remove(data);
				} else {
					Track[] newTrackList = new Track[trackList.length - 1];
					int cpt = 0;
					for (Track track: trackList) {
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
	 * Creates the list of stripes according to a track
	 * @param track the track
	 * @return		its list of stripes
	 */
	public List<VariantData> getVariantsForTrack (Track track) {
		List<VariantData> list = new ArrayList<VariantData>();

		for (VariantData data: variantsList) {
			Track[] trackList = data.getTrackList();
			for (Track currentTrack: trackList) {
				if (currentTrack.toString().equals(track.toString())) {
					list.add(data);
					break;
				}
			}
		}

		return list;
	}


	/**
	 * @return the stripesList
	 */
	public List<VariantData> getVariantsList() {
		return variantsList;
	}


	/**
	 * Copy the information from the temporary list to the actual list changing their target track.
	 * It does not erase the temporary list in order to use it again later on.
	 * @param track the new track for the information
	 */
	public void pasteTemporaryStripes (Track track) {
		if (copiedStripesList != null) {
			for (VariantData data: copiedStripesList) {
				Track[] tracks = {track};
				VariantData newData = new VariantData(data.getGenome(), data.getAlleleType(), data.getVariationTypeList(), data.getColorList(), tracks);
				variantsList.add(newData);
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
		variantsList = (List<VariantData>) in.readObject();
		copiedStripesList = null;
	}


	/**
	 * When a new track is loaded, the settings will still refer to the previous track if this method is not called.
	 * It will replace the references to the old track by the one of the new track.
	 * @param oldTrack the old track
	 * @param newTrack the new track
	 */
	public void replaceTrack (Track oldTrack, Track newTrack) {
		for (VariantData stripe: variantsList) {
			stripe.replaceTrack(oldTrack, newTrack);
		}
	}


	/**
	 * @param variantsList the stripesList to set
	 */
	public void setVariantsSettings(List<VariantData> variantsList) {
		this.variantsList = variantsList;
	}


	/**
	 * Show the settings
	 */
	public void showSettings () {
		System.out.println("===== VARIANTS");
		for (VariantData data: variantsList) {
			System.out.println("Genome: " + data.getGenomeForDisplay() + "; Stripes: " + data.getVariationTypeList());
		}
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(variantsList);
	}
}
