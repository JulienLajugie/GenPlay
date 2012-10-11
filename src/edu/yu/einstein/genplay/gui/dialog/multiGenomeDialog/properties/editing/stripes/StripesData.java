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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.stripes;

import java.awt.Color;
import java.awt.FlowLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.display.MGAlleleForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.display.MGGenomeForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.display.MGVariantListForDisplay;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.trackList.TrackList;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class StripesData implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = 2604583442089053519L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	/** Index used for Genome column */
	public static final int GENOME_INDEX 	= 1;
	/** Index used for Allele column */
	public static final int ALLELE_INDEX 	= 2;
	/** Index used for variant column */
	public static final int VARIANT_INDEX 	= 3;
	/** Index used for track column */
	public static final int TRACK_INDEX 	= 4;


	private String 				genome;				// name of the genome
	private AlleleType			alleleType;			// type of allele (paternal, maternal or both)
	private List<VariantType> 	variationTypeList;	// list of variation
	private List<Color> 		colorList;			// list of color
	private Track<?>[] 			trackList;			// list of track


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(genome);
		out.writeObject(alleleType);
		out.writeObject(variationTypeList);
		out.writeObject(colorList);

		TrackList trackList = MainFrame.getInstance().getTrackList();
		for (Track<?> currentTrack: this.trackList) {
			currentTrack.removeTrackListener(trackList);
		}

		out.writeObject(this.trackList);

		// rebuild the references to the listener
		for (Track<?> currentTrack: this.trackList) {
			currentTrack.addTrackListener(trackList);
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
		genome = (String) in.readObject();
		alleleType = (AlleleType) in.readObject();
		variationTypeList = (List<VariantType>) in.readObject();
		colorList = (List<Color>) in.readObject();
		trackList = (Track<?>[]) in.readObject();
	}


	/**
	 * Constructor of {@link StripesData}
	 */
	protected StripesData() {
		this.genome = null;
		this.alleleType = null;
		this.variationTypeList = null;
		this.colorList = null;
		this.trackList = null;
	}


	/**
	 * Constructor of {@link StripesData}
	 * @param genome		name of the genome
	 * @param alleleType 	type of the allele
	 * @param variantList	list of variation
	 * @param colorList		list of color
	 * @param trackList		list of track
	 */
	public StripesData(String genome, AlleleType alleleType, List<VariantType> variantList,
			List<Color> colorList, Track<?>[] trackList) {
		this.genome = genome;
		this.alleleType = alleleType;
		this.variationTypeList = variantList;
		this.colorList = colorList;
		this.trackList = trackList;
	}


	//////////////////// Setters
	/**
	 * @param genome the genome to set
	 */
	public void setGenome(String genome) {
		this.genome = genome;
	}

	/**
	 * @param alleleType the alleleType to set
	 */
	public void setAlleleType(AlleleType alleleType) {
		this.alleleType = alleleType;
	}

	/**
	 * @param variantList the variantList to set
	 */
	public void setVariationTypeList(List<VariantType> variantList) {
		this.variationTypeList = variantList;
	}

	/**
	 * @param colorList the colorList to set
	 */
	public void setColorList(List<Color> colorList) {
		this.colorList = colorList;
	}

	/**
	 * @param trackList the trackList to set
	 */
	public void setTrackList(Track<?>[] trackList) {
		this.trackList = trackList;
	}


	//////////////////// Getters
	/**
	 * @return the genome
	 */
	public String getGenome() {
		return genome;
	}

	/**
	 * @return the alleleType
	 */
	public AlleleType getAlleleType() {
		return alleleType;
	}

	/**
	 * @return the variantList
	 */
	public List<VariantType> getVariationTypeList() {
		return variationTypeList;
	}

	/**
	 * @return the colorList
	 */
	public List<Color> getColorList() {
		return colorList;
	}

	/**
	 * @return the trackList
	 */
	public Track<?>[] getTrackList() {
		return trackList;
	}


	//////////////////// Getters for display
	/**
	 * @return the genome
	 */
	public String getGenomeForDisplay() {
		return genome;
	}

	/**
	 * @return the allele type
	 */
	public String getAlleleTypeForDisplay() {
		return alleleType.toString();
	}

	/**
	 * @return the variantList
	 */
	public JPanel getVariationTypeListForDisplay() {
		JPanel panel = new JPanel();
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
		panel.setLayout(layout);
		for (int i = 0; i < variationTypeList.size(); i++) {
			JLabel label = new JLabel(variationTypeList.get(i).toString());
			label.setForeground(colorList.get(i));
			panel.add(label);
			if (i < (variationTypeList.size() - 1)) {
				panel.add(new JLabel(", "));
			}
		}
		return panel;
	}

	/**
	 * @return the trackList
	 */
	public String getTrackListForDisplay() {
		String text = "";
		for (int i = 0; i < trackList.length; i++) {
			text += trackList[i];
			if (i < (trackList.length - 1)) {
				text += ", ";
			}
		}
		return text;
	}


	@Override
	public String toString () {
		String info = "";
		info += genome;
		info += " " + alleleType.toString();
		for (int i = 0; i < variationTypeList.size(); i++) {
			info += " [" + variationTypeList.get(i) + ", ";
			info += colorList.get(i) + "]";
		}
		info += " [";
		for (Track<?> track: trackList) {
			info += track + ", ";
		}
		info += "]";
		return info;
	}


	/**
	 * @param alleleType
	 * @return the list of variant list for display
	 */
	public List<MGVariantListForDisplay> getListOfVariantList (AlleleType alleleType) {
		List<MGVariantListForDisplay> listOfVariantList = new ArrayList<MGVariantListForDisplay>();

		MGGenomeForDisplay genomeForDisplay = ProjectManager.getInstance().getMultiGenomeProject().getMultiGenomeForDisplay().getGenomeInformation(genome);
		MGAlleleForDisplay alleleForDisplay = null;
		if (alleleType == AlleleType.ALLELE01) {
			alleleForDisplay = genomeForDisplay.getAlleleA();
		} else if (alleleType == AlleleType.ALLELE02) {
			alleleForDisplay = genomeForDisplay.getAlleleB();
		}

		Chromosome chromosome = ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome();
		for (VariantType variantType: variationTypeList) {
			MGVariantListForDisplay variantListForDisplay = alleleForDisplay.getVariantList(chromosome, variantType);
			listOfVariantList.add(variantListForDisplay);
		}

		return listOfVariantList;
	}


	@Override
	public boolean equals(Object obj) {
		if(this == obj){
			return true;
		}
		if((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		// object must be Test at this point
		StripesData test = (StripesData)obj;
		return genome.equals(test.getGenome()) &&
				(alleleType == test.getAlleleType()) &&
				hasSameVariationTypeList(test.getVariationTypeList()) &&
				hasSameColorList(test.getColorList()) &&
				hasSameTrackList(test.getTrackList());
	}


	/**
	 * Compare a variation type list to the current one
	 * @param variationTypeList	the variation list to compare
	 * @return	true if both list contain same values, false otherwise
	 */
	private boolean hasSameVariationTypeList (List<VariantType> variationTypeList) {
		if ((this.variationTypeList == null) && (variationTypeList == null)) {
			return true;
		} else if ((this.variationTypeList != null) && (variationTypeList == null)) {
			return false;
		} else if ((this.variationTypeList == null) && (variationTypeList != null)) {
			return false;
		} else {
			if (this.variationTypeList.size() != variationTypeList.size()) {
				return false;
			} else {
				for (VariantType variantType: variationTypeList) {
					if (!this.variationTypeList.contains(variantType)) {
						return false;
					}
				}
				return true;
			}
		}
	}


	/**
	 * Compare a list of color to the current one
	 * @param colorList	the list of color to compare
	 * @return	true if both list contain same values, false otherwise
	 */
	private boolean hasSameColorList (List<Color> colorList) {
		if ((this.colorList == null) && (colorList == null)) {
			return true;
		} else if ((this.colorList != null) && (colorList == null)) {
			return false;
		} else if ((this.colorList == null) && (colorList != null)) {
			return false;
		} else {
			if (this.colorList.size() != colorList.size()) {
				return false;
			} else {
				for (Color color: colorList) {
					if (!this.colorList.contains(color)) {
						return false;
					}
				}
				return true;
			}
		}
	}


	/**
	 * Compare a list of track to the current one
	 * @param trackList	the list of track to compare
	 * @return	true if both list contain same values, false otherwise
	 */
	private boolean hasSameTrackList (Track<?>[] trackList) {
		if ((this.trackList == null) && (trackList == null)) {
			return true;
		} else if ((this.trackList != null) && (trackList == null)) {
			return false;
		} else if ((this.trackList == null) && (trackList != null)) {
			return false;
		} else {
			if (this.trackList.length != trackList.length) {
				return false;
			} else {
				for (Track<?> track: trackList) {
					boolean contain = false;
					for (Track<?> currentTrack: this.trackList) {
						if (track.equals(currentTrack)) {
							contain = true;
						}
					}
					if (!contain) {
						return false;
					}
				}
				return true;
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
		for (int i = 0; i < trackList.length; i++) {
			if (trackList[i].equals(oldTrack)) {
				trackList[i] = newTrack;
			}
		}
	}

}
