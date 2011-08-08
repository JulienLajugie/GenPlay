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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.multiGenome.stripeManagement;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import edu.yu.einstein.genplay.core.enums.VariantType;


/**
 * This class stores information about multi genome information stripes to display.
 * Each track have an instance of this class.
 * Stripes information are specific of genomes and VCF files, they are about:
 * - association between variant type and color
 * - transparency
 * @author Nicolas Fourel
 */
public class MultiGenomeStripe implements Serializable {

	private static final long serialVersionUID = -4999509228556102365L; // generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private Map<String, Map<VariantType, Color>> 	colorAssociation;	// Association between variant type and color
	private int 									transparency;		// Transparency (0 -> 100)
	private int										quality;			// Quality threshold (0 -> 100)
	private String 									genomeName;			// Genome name
	
	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(colorAssociation);
		out.writeInt(transparency);
		out.writeInt(quality);
		out.writeObject(genomeName);
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
		colorAssociation = (Map<String, Map<VariantType, Color>>) in.readObject(); 
		transparency = in.readInt();
		quality = in.readInt();
		genomeName = (String) in.readObject();
	}
	
	
	/**
	 * Constructor of {@link MultiGenomeStripe}
	 */
	public MultiGenomeStripe () {
		colorAssociation = new HashMap<String, Map<VariantType,Color>>();
		transparency = 50;
	}
	
	
	/**
	 * @return the colorAssociation
	 */
	public Map<String, Map<VariantType, Color>> getColorAssociation() {
		return colorAssociation;
	}


	/**
	 * Sets stripe type and color association 
	 * @param genomeName	genome formatted name
	 * @param association	stripe type and color association
	 */
	public void addColorInformation (String genomeName, Map<VariantType, Color> association) {
		colorAssociation.put(genomeName, association);
	}
	
	
	/**
	 * Initializes color stripes.
	 */
	private void initStripes () {
		colorAssociation = new HashMap<String, Map<VariantType,Color>>();
	}
	
	
	/**
	 * @return the genomeName
	 */
	public String getGenomeName() {
		return genomeName;
	}


	/**
	 * @param genomeName the genomeName to set
	 */
	public void setGenomeName(String genomeName) {
		this.genomeName = genomeName;
		initStripes();
	}


	/**
	 * @return the transparency
	 */
	public int getTransparency() {
		return transparency;
	}


	/**
	 * @param alpha the transparency to set
	 */
	public void setTransparency(int alpha) {
		this.transparency = alpha * 100 / 255;
	}
	
	
	/**
	 * @return the quality
	 */
	public int getQuality() {
		return quality;
	}


	/**
	 * @param quality the quality to set
	 */
	public void setQuality(int quality) {
		this.quality = quality;
	}


	/**
	 * Shows information about color/variant type association.
	 */
	public void showInformation () {
		for (String name: colorAssociation.keySet()) {
			for (VariantType type: colorAssociation.get(name).keySet()) {
				System.out.println(name + " - " + type + ": " + colorAssociation.get(name).get(type));
			}
		}
	}
	
}