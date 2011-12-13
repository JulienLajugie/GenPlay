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

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.stripes.StripesData;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGStripeSettings implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = -8887751815193182599L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	
	private List<StripesData> stripesList;	// List of settings for stripes display

	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(stripesList);
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
		stripesList = (List<StripesData>) in.readObject();
	}
	
	
	/**
	 * Constructor of {@link MGStripeSettings}
	 */
	protected MGStripeSettings () {
		stripesList = new ArrayList<StripesData>();
	}
	
	
	/**
	 * @return the stripesList
	 */
	public List<StripesData> getStripesList() {
		return stripesList;
	}

	
	/**
	 * @param stripesList the stripesList to set
	 */
	public void setStripesSettings(List<StripesData> stripesList) {
		this.stripesList = stripesList;
	}
	
	
	/**
	 * Creates the list of stripes according to a track
	 * @param track the track
	 * @return		its list of stripes
	 */
	public List<StripesData> getStripesForTrack (Track<?> track) {
		List<StripesData> list = new ArrayList<StripesData>();
		
		for (StripesData data: stripesList) {
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
	 * Show the settings
	 */
	public void showSettings () {
		System.out.println("===== STRIPES");
		for (StripesData data: stripesList) {
			System.out.println("Genome: " + data.getGenomeForDisplay() + "; Stripes: " + data.getVariationTypeList());
		}
	}
	
}
