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
package edu.yu.einstein.genplay.core.multiGenome.display;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGGenome;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGMultiGenome;

/**
 * This class is the entry point of the data structure about the variations display.
 * It has quiet the same structure as the {@link MGMultiGenome}.
 * In this whole structure, everything is focused on the display of the variation and cannot be used for synchronization position calculation.
 * 1 - the {@link MGMultiGenomeForDisplay}: it gathers all data of the genomes in a list of {@link MGGenomeForDisplay}
 * 2 - the {@link MGGenomeForDisplay}: it gathers data for a specific genome in two {@link MGAlleleForDisplay}
 * 3 - the {@link MGAlleleForDisplay}: it gathers lists of variations ({@link MGVariantListForDisplay} for every chromosome
 * 4 - the {@link MGVariantListForDisplay}: contains the list of variations and methods to handle them
 * 
 * This class also contains the display data for the reference genome: {@link MGReferenceForDisplay}.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGMultiGenomeForDisplay implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = 7460760950740721596L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version
	private MGReferenceForDisplay		referenceGenome;			// reference genome for display
	private List<MGGenomeForDisplay> 	genomesInformation;			// list of the genomes information
	
	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(referenceGenome);
		out.writeObject(genomesInformation);
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
		referenceGenome = (MGReferenceForDisplay) in.readObject();
		genomesInformation = (List<MGGenomeForDisplay>) in.readObject();
	}
	
	
	/**
	 * Constructor of {@link MGMultiGenomeForDisplay}
	 * @param genomes
	 */
	public MGMultiGenomeForDisplay (List<MGGenome> genomes) {
		referenceGenome = new MGReferenceForDisplay();
		genomesInformation = new ArrayList<MGGenomeForDisplay>();
		for (MGGenome genome: genomes) {
			genomesInformation.add(new MGGenomeForDisplay(genome));
		}
	}
	
	
	/**
	 * @return the referenceGenome
	 */
	public MGReferenceForDisplay getReferenceGenome() {
		return referenceGenome;
	}


	/**
	 * @param genomeName genome name
	 * @return	the genome information object
	 */
	public MGGenomeForDisplay getGenomeInformation (String genomeName) {
		return genomesInformation.get(getGenomeIndex(genomeName));
	}
	
	
	/**
	 * Gets the index of a genome in the genome information list
	 * @param genomeName	the name of the genome
	 * @return				its index in the list
	 */
	private int getGenomeIndex (String genomeName) {
		int index = 0;
		
		for (MGGenomeForDisplay genome: genomesInformation) {
			if (genome.getGenome().getName().equals(genomeName)) {
				return index;
			}
			index++;
		}
		return -1;
	}
	
	
	/**
	 * Show the information of the {@link MGMultiGenomeForDisplay}
	 */
	public void show () {
		referenceGenome.show();
		for (MGGenomeForDisplay genome: genomesInformation) {
			genome.show();
		}
	}
	
}
