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
package edu.yu.einstein.genplay.gui.MGDisplaySettings;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.dataStructure.enums.CoordinateSystemType;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.variantLayer.VariantLayer;
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
		variousSettings = new MGVariousSettings();
	}


	/**
	 * @return the filterSettings
	 */
	public MGFilterSettings getFilterSettings() {
		return filterSettings;
	}


	/**
	 * @return the variousSettings
	 */
	public MGVariousSettings getVariousSettings() {
		return variousSettings;
	}


	/**
	 * Create a copy of the information related to the given layer in temporary lists.
	 * This method is used when multi genome information cannot be serialized.
	 * @param layer the layer to save information
	 */
	public void copyTemporaryTrack (Layer<?> layer) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			filterSettings.copyTemporaryFilters(layer);
		}
	}


	/**
	 * Copy the information from the temporary lists to the actual list changing their target layer.
	 * It does not erase the temporary lists in order to use them again later on.
	 * @param layer the new layer for the information
	 */
	public void pasteTemporaryTrack (Layer<?> layer) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			filterSettings.pasteTemporaryFilters(layer);
		}
	}



	/**
	 * When a new layer is loaded, the settings will still refer to the previous layer if this method is not called.
	 * It will replace the references to the old layer by the one of the new layer.
	 * @param oldTrack the old layer
	 * @param newTrack the new layer
	 */
	public void replaceTrack (Layer<?> oldTrack, Layer<?> newTrack) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			filterSettings.replaceTrack(oldTrack, newTrack);
		}
	}


	/**
	 * When pasting a layer, associated stripes settings to the copying layer must be given to the pasting layer.
	 * This method create duplicates of the settings related to the copied layer updated for the pasted layer.
	 * @param copiedTrack	the copied layer
	 * @param newTrack		the pasted layer
	 */
	public void copyTrack (Layer<?> copiedTrack, Layer<?> newTrack) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			filterSettings.copyData(copiedTrack, newTrack);
		}
	}


	/**
	 * When deleting a layer, all its settings must be deleted.
	 * The setting of a layer can be mixed with the ones of other tracks.
	 * Therefore, deleting settings must be processed carefully, taking into account the other layer.
	 * @param deleteTrack the deleted layer
	 */
	public void deleteTrack (Layer<?> deleteTrack) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			filterSettings.deleteData(deleteTrack);
		}
	}


	/**
	 * Restore multi genome information to a layer
	 * @param layer the layer
	 */
	public void restoreInformation (Layer<?> layer) {
		if (ProjectManager.getInstance().isMultiGenomeProject() && (layer instanceof VariantLayer)) {
			List<MGFilter> filterList = filterSettings.getMGFiltersForTrack(layer);
			((VariantLayer)layer).getGenomeDrawer().setFiltersList(filterList);
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
	}

}
