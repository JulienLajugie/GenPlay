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
package edu.yu.einstein.genplay.core.multiGenome.stripeManagement;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.enums.VariantType;


/**
 * This class stores information about multi genome information stripes to display.
 * Each track have an instance of this class.
 * Stripes information are specific of genomes and VCF files, they are about:
 * - association between variant type and color
 * - transparency
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MultiGenomeStripes implements Serializable {

	private static final long serialVersionUID = -4999509228556102365L; // generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private Map<String, Map<VariantType, Color>> 	colorAssociation;	// Association between variant type and color


	/**
	 * Constructor of {@link MultiGenomeStripes}
	 */
	public MultiGenomeStripes () {
		colorAssociation = new HashMap<String, Map<VariantType,Color>>();
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
	 * @return the colorAssociation
	 */
	public Map<String, Map<VariantType, Color>> getColorAssociation() {
		return colorAssociation;
	}


	/**
	 * @return the list of the required raw genome names
	 */
	public Map<String, List<VariantType>> getRequiredGenomes () {
		Map<String, List<VariantType>> genomes = new HashMap<String, List<VariantType>>();
		for (String fullGenomeName: colorAssociation.keySet()) {
			if (!fullGenomeName.equals(ProjectManager.getInstance().getAssembly().getDisplayName())) {
				if (hasBeenRequired(fullGenomeName)) {
					List<VariantType> types = new ArrayList<VariantType>();
					for (VariantType type: colorAssociation.get(fullGenomeName).keySet()) {
						types.add(type);
					}
					genomes.put(fullGenomeName, types);
				}
			}
		}
		return genomes;
	}


	/**
	 * @param genome the full genome name
	 * @return true if it has to be displayed
	 */
	public boolean hasBeenRequired (String genome) {
		if (colorAssociation.get(genome).size() > 0) {
			return true;
		} else {
			return false;
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
		colorAssociation = (Map<String, Map<VariantType, Color>>) in.readObject();
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


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(colorAssociation);
	}

}
