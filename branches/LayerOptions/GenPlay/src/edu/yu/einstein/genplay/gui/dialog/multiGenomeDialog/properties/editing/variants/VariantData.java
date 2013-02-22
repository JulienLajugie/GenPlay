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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.variants;

import java.awt.Color;
import java.awt.FlowLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.VariantType;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantData implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = 2604583442089053519L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	/** Index used for Genome column */
	public static final int GENOME_INDEX 	= 1;
	/** Index used for Allele column */
	public static final int ALLELE_INDEX 	= 2;
	/** Index used for variant column */
	public static final int VARIANT_INDEX 	= 3;

	private String 				genome;				// name of the genome
	private AlleleType			alleleType;			// type of allele (paternal, maternal or both)
	private List<VariantType> 	variationTypeList;	// list of variation
	private List<Color> 		colorList;			// list of color
	private boolean				hasChanged;


	/**
	 * Constructor of {@link VariantData}
	 */
	protected VariantData() {
		genome = null;
		alleleType = null;
		variationTypeList = null;
		colorList = null;
	}


	/**
	 * Constructor of {@link VariantData}
	 * @param genome		name of the genome
	 * @param alleleType 	type of the allele
	 * @param variantList	list of variation
	 * @param colorList		list of color
	 */
	public VariantData(String genome, AlleleType alleleType, List<VariantType> variantList, List<Color> colorList) {
		this.genome = genome;
		this.alleleType = alleleType;
		variationTypeList = variantList;
		this.colorList = colorList;
		hasChanged = false;
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
		VariantData test = (VariantData)obj;
		return genome.equals(test.getGenome()) &&
				(alleleType == test.getAlleleType()) &&
				hasSameVariationTypeList(test.getVariationTypeList()) &&
				hasSameColorList(test.getColorList());
	}


	/**
	 * @return the alleleType
	 */
	public AlleleType getAlleleType() {
		return alleleType;
	}


	/**
	 * @return the allele type
	 */
	public String getAlleleTypeForDisplay() {
		return alleleType.toString();
	}


	/**
	 * @return the colorList
	 */
	public List<Color> getColorList() {
		return colorList;
	}


	//////////////////// Getters
	/**
	 * @return the genome
	 */
	public String getGenome() {
		return genome;
	}


	/**
	 * @return the hasChanged
	 */
	public boolean hasChanged() {
		return hasChanged;
	}


	/**
	 * @param hasChanged the hasChanged to set
	 */
	public void setHasChanged(boolean hasChanged) {
		this.hasChanged = hasChanged;
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
	public List<VariantType> getVariationTypeList() {
		return variationTypeList;
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
	}


	/**
	 * @param alleleType the alleleType to set
	 */
	public void setAlleleType(AlleleType alleleType) {
		this.alleleType = alleleType;
	}


	/**
	 * @param colorList the colorList to set
	 */
	public void setColorList(List<Color> colorList) {
		this.colorList = colorList;
	}


	//////////////////// Setters
	/**
	 * @param genome the genome to set
	 */
	public void setGenome(String genome) {
		this.genome = genome;
	}


	/**
	 * @param variantList the variantList to set
	 */
	public void setVariationTypeList(List<VariantType> variantList) {
		variationTypeList = variantList;
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
		return info;
	}


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
	}


	/**
	 * @return a description of the {@link VariantData} settings
	 */
	public String getDescription() {
		String description = (genome + " (");		// add the name and " ("

		for (int i = 0; i < variationTypeList.size(); i++) {
			VariantType type = variationTypeList.get(i);
			// Add the variant type shortcut
			if (type == VariantType.INSERTION) {
				description += "I";
			} else if (type == VariantType.DELETION) {
				description += "D";
			} else if (type == VariantType.SNPS) {
				description += "SNPs";
			}
			if (i < (variationTypeList.size() - 1)) {
				description += ", ";
			}
		}

		description += ")";							// add a ")" for closing

		return description;
	}
}