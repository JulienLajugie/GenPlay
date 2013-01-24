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

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import edu.yu.einstein.genplay.core.enums.CoordinateSystemType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.variants.VariantData;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.colors.Colors;


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

	/** Draw the variant that have been filtered */
	public static int DRAW_FILTERED_VARIANT = NO_MG_OPTION;

	/** Insertion stripes can be drawn with an edge line */
	public static int DRAW_INSERTION_EDGE = YES_MG_OPTION;

	/** Deletion stripes can be drawn with an edge line */
	public static int DRAW_DELETION_EDGE = YES_MG_OPTION;

	/** Draw the insertion reference genome stripes (blank of synchronization) */
	public static int DRAW_REFERENCE_INSERTION = YES_MG_OPTION;

	/** Draw the deletion reference genome stripes */
	public static int DRAW_REFERENCE_DELETION = YES_MG_OPTION;

	/** Draw the SNP reference genome stripes */
	public static int DRAW_REFERENCE_SNP = YES_MG_OPTION;

	/** Draw the inserted nucleotides over an insertion stripe */
	public static int DRAW_INSERTION_LETTERS = YES_MG_OPTION;

	/** Draw the deleted nucleotides over a deletion stripe */
	public static int DRAW_DELETION_LETTERS = YES_MG_OPTION;

	/** Draw the replaced nucleotide over a SNP stripe */
	public static int DRAW_SNP_LETTERS = YES_MG_OPTION;

	/** Draw the reference genome stripes letters */
	public static int DRAW_REFERENCE_LETTERS = YES_MG_OPTION;

	/** Color of the reference insertion stripes */
	public static Color REFERENCE_INSERTION_COLOR = new Color(Colors.BLACK.getRed(), Colors.BLACK.getGreen(), Colors.BLACK.getBlue());

	/** Color of the reference deletion stripes */
	public static Color REFERENCE_DELETION_COLOR = new Color(Colors.BLACK.getRed(), Colors.BLACK.getGreen(), Colors.BLACK.getBlue());

	/** Color of the reference SNP stripes */
	public static Color REFERENCE_SNP_COLOR = new Color(Colors.BLACK.getRed(), Colors.BLACK.getGreen(), Colors.BLACK.getBlue());

	/** Color of the no call stripes */
	public static Color NO_CALL_COLOR = new Color(Colors.GREY.getRed(), Colors.GREY.getGreen(), Colors.GREY.getBlue());

	/** Current selected coordinate genome system */
	public static String SELECTED_GENOME = CoordinateSystemType.METAGENOME.toString();
	//private static String SELECTED_GENOME = null;


	private static MGDisplaySettings 	instance;	// Instance of the class

	private MGFilterSettings 	filterSettings; 	// All settings about the filters
	private MGVariantSettings 	variantSettings; 	// All settings about the stripes
	private MGVariousSettings 	variousSettings;	// All settings about various settings
	private String savedCoordinate;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(instance);
		out.writeObject(filterSettings);
		out.writeObject(variantSettings);
		out.writeObject(variousSettings);

		out.writeInt(DRAW_FILTERED_VARIANT);
		out.writeInt(DRAW_INSERTION_EDGE);
		out.writeInt(DRAW_DELETION_EDGE);
		out.writeInt(DRAW_REFERENCE_INSERTION);
		out.writeInt(DRAW_REFERENCE_DELETION);
		out.writeInt(DRAW_REFERENCE_SNP);
		out.writeInt(DRAW_INSERTION_LETTERS);
		out.writeInt(DRAW_DELETION_LETTERS);
		out.writeInt(DRAW_SNP_LETTERS);
		out.writeInt(DRAW_REFERENCE_LETTERS);

		out.writeObject(SELECTED_GENOME);
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
		variantSettings = (MGVariantSettings) in.readObject();
		variousSettings = (MGVariousSettings) in.readObject();

		DRAW_FILTERED_VARIANT = in.readInt();
		DRAW_INSERTION_EDGE = in.readInt();
		DRAW_DELETION_EDGE = in.readInt();
		DRAW_REFERENCE_INSERTION = in.readInt();
		DRAW_REFERENCE_DELETION = in.readInt();
		DRAW_REFERENCE_SNP = in.readInt();
		DRAW_INSERTION_LETTERS = in.readInt();
		DRAW_DELETION_LETTERS = in.readInt();
		DRAW_SNP_LETTERS = in.readInt();
		DRAW_REFERENCE_LETTERS = in.readInt();

		savedCoordinate = (String) in.readObject();
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
		variantSettings = new MGVariantSettings();
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
	public MGVariantSettings getVariantSettings() {
		return variantSettings;
	}


	/**
	 * @return the variousSettings
	 */
	public MGVariousSettings getVariousSettings() {
		return variousSettings;
	}


	/**
	 * Create a copy of the information related to the given track in temporary lists.
	 * This method is used when multi genome information cannot be serialized.
	 * @param track the track to save information
	 */
	public void copyTemporaryTrack (Track track) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			variantSettings.copyTemporaryStripes(track);
			filterSettings.copyTemporaryFilters(track);
		}
	}


	/**
	 * Copy the information from the temporary lists to the actual list changing their target track.
	 * It does not erase the temporary lists in order to use them again later on.
	 * @param track the new track for the information
	 */
	public void pasteTemporaryTrack (Track track) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			variantSettings.pasteTemporaryStripes(track);
			filterSettings.pasteTemporaryFilters(track);
		}
	}



	/**
	 * When a new track is loaded, the settings will still refer to the previous track if this method is not called.
	 * It will replace the references to the old track by the one of the new track.
	 * @param oldTrack the old track
	 * @param newTrack the new track
	 */
	public void replaceTrack (Track oldTrack, Track newTrack) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			filterSettings.replaceTrack(oldTrack, newTrack);
			variantSettings.replaceTrack(oldTrack, newTrack);
		}
	}


	/**
	 * When pasting a track, associated stripes settings to the copying track must be given to the pasting track.
	 * This method create duplicates of the settings related to the copied track updated for the pasted track.
	 * @param copiedTrack	the copied track
	 * @param newTrack		the pasted track
	 */
	public void copyTrack (Track copiedTrack, Track newTrack) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			filterSettings.copyData(copiedTrack, newTrack);
			variantSettings.copyData(copiedTrack, newTrack);
		}
	}


	/**
	 * When deleting a track, all its settings must be deleted.
	 * The setting of a track can be mixed with the ones of other tracks.
	 * Therefore, deleting settings must be processed carefully, taking into account the other track.
	 * @param deleteTrack the deleted track
	 */
	public void deleteTrack (Track deleteTrack) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			filterSettings.deleteData(deleteTrack);
			variantSettings.deleteData(deleteTrack);
		}
	}


	/**
	 * Restore multi genome information to a track
	 * @param track the track
	 */
	public void restoreInformation (Track track) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			List<MGFilter> filterList = filterSettings.getMGFiltersForTrack(track);
			List<VariantData> stripeList = variantSettings.getVariantsForTrack(track);
			track.getMultiGenomeDrawer().setVariantDataList(stripeList);
			track.getMultiGenomeDrawer().setFiltersList(filterList);
		}
	}


	/**
	 * @return true if references have to be included, false otherwise
	 */
	public boolean includeReferences () {
		return DRAW_REFERENCE_INSERTION == YES_MG_OPTION;
	}

	/**
	 * @return true if filtered variants have to be included, false otherwise
	 */
	public boolean includeFilteredVariants () {
		return DRAW_FILTERED_VARIANT == YES_MG_OPTION;
	}

	/**
	 * @return true is no call "." have to be included, false otherwise
	 */
	public boolean includeNoCall () {
		return true;
	}


	/**
	 * @param display
	 * @return true if the display is has to be shown, false otherwise
	 */
	/*public boolean isShown (byte display) {
		if (display == VariantDisplayList.SHOW) {
			return true;
		}

		if (display == VariantDisplayList.SHOW_FILTER) {
			if (DRAW_FILTERED_VARIANT == YES_MG_OPTION) {
				return true;
			}
		}

		return false;
	}*/


	/**
	 * 
	 * @param include true if references have to be included, false otherwise
	 */
	public void setReferencePolicy (boolean include) {
		int option = NO_MG_OPTION;
		if (include) {
			option = YES_MG_OPTION;
		}
		MGDisplaySettings.DRAW_REFERENCE_INSERTION = option;
		MGDisplaySettings.DRAW_REFERENCE_DELETION = option;
		MGDisplaySettings.DRAW_REFERENCE_SNP = option;
	}


	/**
	 * Restores the saved genome coordinate system after loading a project.
	 */
	public void restoreGenomeCoordinate () {
		MainFrame.getInstance().setNewGenomeCoordinate(savedCoordinate);
	}

	/**
	 * Show the settings
	 */
	public void showSettings () {
		variousSettings.showSettings();
		filterSettings.showSettings();
		variantSettings.showSettings();
	}

}
