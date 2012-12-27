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
package edu.yu.einstein.genplay.core.multiGenome.data.synchronization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGSMultiGenome implements Serializable {

	/** Default serial version ID */
	private static final long serialVersionUID = 6093066740612842875L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version
	private MGSReference reference;
	private List<MGSGenome> genomes;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(reference);
		out.writeObject(genomes);
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
		reference = (MGSReference) in.readObject();
		genomes = (List<MGSGenome>) in.readObject();
	}


	/**
	 * Constructor of {@link MGSMultiGenome}
	 * @param genomeNames full name of the genomes
	 */
	public MGSMultiGenome (List<String> genomeNames) {
		this.reference = new MGSReference();
		genomes = new ArrayList<MGSGenome>();
		for (String genomeName: genomeNames) {
			genomes.add(new MGSGenome(genomeName));
		}
	}


	/**
	 * @param genomeName genome full name
	 * @return	the genome information object
	 */
	public MGSGenome getGenomeInformation (String genomeName) {
		return genomes.get(getGenomeIndex(genomeName));
	}


	/**
	 * @return	the list of genome information
	 */
	public List<MGSGenome> getGenomeInformation () {
		return genomes;
	}


	/**
	 * Gets the index
	 * @param genomeName
	 * @return the index of the genome name
	 */
	public int getGenomeIndex (String genomeName) {
		int index = 0;
		for (MGSGenome genome: genomes) {
			if (genome.getName().equals(genomeName)) {
				return index;
			}
			index++;
		}
		return -1;
	}


	/**
	 * Sorts the alleles of every genomes according to the position of the variation
	 */
	public void sort() {
		reference.sort();
		for (MGSGenome genome: genomes) {
			genome.sort();
		}
	}


	/**
	 * Compacts the lists of position of the alleles of every genomes and chromosomes
	 */
	public void compactLists() {
		reference.compact();
		for (MGSGenome genome: genomes) {
			genome.compact();
		}
	}


	/**
	 * @return the referenceGenome
	 */
	public MGSReference getReferenceGenome() {
		return reference;
	}


	/**
	 * Show the information of the {@link MGSMultiGenome}
	 */
	public void show () {
		reference.show();
		for (MGSGenome genome: genomes) {
			genome.show();
		}
	}

}
