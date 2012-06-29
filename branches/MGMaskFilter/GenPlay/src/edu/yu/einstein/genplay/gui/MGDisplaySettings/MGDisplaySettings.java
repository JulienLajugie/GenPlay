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

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.track.Track;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGDisplaySettings implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = 1442202260363430870L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	/** Enable a MG option */
	public static final int YES_MG_OPTION = 1;

	/** Disable a MG option */
	public static final int NO_MG_OPTION = 0;

	/** Blank of synchronization are involved by insertion in genomes on the reference genome */
	public static int INCLUDE_BLANK_OPTION = YES_MG_OPTION;

	/** Insertion stripes can be drawn with an edge line */
	public static int DRAW_INSERTION_EDGE = YES_MG_OPTION;

	/** Deletion stripes can be drawn with an edge line */
	public static int DRAW_DELETION_EDGE = YES_MG_OPTION;

	/** Draw the inserted nucleotides over an insertion stripe */
	public static int DRAW_INSERTION_LETTERS = YES_MG_OPTION;

	/** Draw the deleted nucleotides over a deletion stripe */
	public static int DRAW_DELETION_LETTERS = YES_MG_OPTION;

	/** Draw the replaced nucleotide over a SNP stripe */
	public static int DRAW_SNP_LETTERS = YES_MG_OPTION;


	private static MGDisplaySettings 	instance;			// Instance of the class

	private MGFilterSettings 	filterSettings; 	// All settings about the filters
	private MGStripeSettings 	stripeSettings; 	// All settings about the stripes
	private MGVariousSettings 	variousSettings;	// All settings about various settings


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(instance);
		out.writeObject(filterSettings);
		out.writeObject(stripeSettings);
		out.writeObject(variousSettings);

		out.writeInt(INCLUDE_BLANK_OPTION);
		out.writeInt(DRAW_INSERTION_EDGE);
		out.writeInt(DRAW_DELETION_EDGE);
		out.writeInt(DRAW_INSERTION_LETTERS);
		out.writeInt(DRAW_DELETION_LETTERS);
		out.writeInt(DRAW_SNP_LETTERS);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		instance = (MGDisplaySettings) in.readObject();
		filterSettings = (MGFilterSettings) in.readObject();
		stripeSettings = (MGStripeSettings) in.readObject();
		variousSettings = (MGVariousSettings) in.readObject();

		INCLUDE_BLANK_OPTION = in.readInt();
		DRAW_INSERTION_EDGE = in.readInt();
		DRAW_DELETION_EDGE = in.readInt();
		DRAW_INSERTION_LETTERS = in.readInt();
		DRAW_DELETION_LETTERS = in.readInt();
		DRAW_SNP_LETTERS = in.readInt();
	}


	/**
	 * @return an instance of a {@link MGDisplaySettings}. 
	 * Makes sure that there is only one unique instance as specified in the singleton pattern
	 */
	public static MGDisplaySettings getInstance() {
		if (instance == null) {
			instance = new MGDisplaySettings();
		}
		return instance;
	}


	/**
	 * Constructor of {@link MGDisplaySettings}
	 */
	private MGDisplaySettings () {
		filterSettings = new MGFilterSettings();
		stripeSettings = new MGStripeSettings();
		variousSettings = new MGVariousSettings();
	}


	/**
	 * @return the filterSettings
	 */
	public MGFilterSettings getFilterSettings() {
		return filterSettings;
	}


	/**
	 * @return the stripeSettings
	 */
	public MGStripeSettings getStripeSettings() {
		return stripeSettings;
	}


	/**
	 * @return the variousSettings
	 */
	public MGVariousSettings getVariousSettings() {
		return variousSettings;
	}


	/**
	 * When a new track is loaded, the settings will still refer to the previous track if this method is not called.
	 * It will replace the references to the old track by the one of the new track.
	 * @param oldTrack the old track
	 * @param newTrack the new track
	 */
	public void newTrack (Track<?> oldTrack, Track<?> newTrack) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			filterSettings.changeTrack(oldTrack, newTrack);
			stripeSettings.changeTrack(oldTrack, newTrack);
		}
	}
	
	
	/**
	 * When pasting a track, associated stripes settings to the copying track must be given to the pasting track.
	 * This method create duplicates of the settings related to the copied track updated for the pasted track.
	 * @param copiedTrack	the copied track
	 * @param newTrack		the pasted track
	 */
	public void pasteTrack (Track<?> copiedTrack, Track<?> newTrack) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			filterSettings.pasteData(copiedTrack, newTrack);
			stripeSettings.pasteData(copiedTrack, newTrack);
		}
	}
	
	
	/**
	 * When deleting a track, all its settings must be deleted.
	 * The setting of a track can be mixed with the ones of other tracks.
	 * Therefore, deleting settings must be processed carefully, taking into account the other track.
	 * @param deleteTrack the deleted track
	 */
	public void deleteTrack (Track<?> deleteTrack) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			filterSettings.deleteData(deleteTrack);
			stripeSettings.deleteData(deleteTrack);
		}
	}
	


	/**
	 * Show the settings
	 */
	public void showSettings () {
		variousSettings.showSettings();
		filterSettings.showSettings();
		stripeSettings.showSettings();
	}

}
