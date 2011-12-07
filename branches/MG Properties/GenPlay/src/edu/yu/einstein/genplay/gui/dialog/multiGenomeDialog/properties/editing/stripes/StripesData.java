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
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class StripesData {

	/** Index used for Genome column */
	public static final int GENOME_INDEX 	= 0;
	/** Index used for variant column */
	public static final int VARIANT_INDEX 	= 1;
	/** Index used for track column */
	public static final int TRACK_INDEX 	= 2;


	private String 				genome;				// name of the genome
	private List<VariantType> 	variationTypeList;	// list of variation
	private List<Color> 		colorList;			// list of color
	private Track<?>[] 			trackList;			// list of track


	/**
	 * Constructor of {@link StripesData}
	 */
	protected StripesData() {
		this.genome = null;
		this.variationTypeList = null;
		this.colorList = null;
		this.trackList = null;
	}


	/**
	 * Constructor of {@link StripesData}
	 * @param genome		name of the genome
	 * @param variantList	list of variation
	 * @param colorList		list of color
	 * @param trackList		list of track
	 */
	protected StripesData(String genome, List<VariantType> variantList,
			List<Color> colorList, Track<?>[] trackList) {
		this.genome = genome;
		this.variationTypeList = variantList;
		this.colorList = colorList;
		this.trackList = trackList;
	}


	//////////////////// Setters
	/**
	 * @param genome the genome to set
	 */
	protected void setGenome(String genome) {
		this.genome = genome;
	}

	/**
	 * @param variantList the variantList to set
	 */
	protected void setVariationTypeList(List<VariantType> variantList) {
		this.variationTypeList = variantList;
	}

	/**
	 * @param colorList the colorList to set
	 */
	protected void setColorList(List<Color> colorList) {
		this.colorList = colorList;
	}

	/**
	 * @param trackList the trackList to set
	 */
	protected void setTrackList(Track<?>[] trackList) {
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


	public String toString () {
		String info = "";
		info += genome;
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

}
