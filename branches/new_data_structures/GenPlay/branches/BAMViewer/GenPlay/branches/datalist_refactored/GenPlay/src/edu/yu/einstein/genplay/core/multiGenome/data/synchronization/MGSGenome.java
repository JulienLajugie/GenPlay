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

import edu.yu.einstein.genplay.core.enums.AlleleType;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGSGenome implements Serializable {

	/** Default serial version ID */
	private static final long serialVersionUID = -910049079866828194L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version
	private String name;					// The name of the genome.
	private List<MGSAllele> alleles;		// The list of alleles.


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(name);
		out.writeObject(alleles);
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
		name = (String) in.readObject();
		alleles = (List<MGSAllele>) in.readObject();
	}


	/**
	 * Constructor of {@link MGSGenome}
	 * @param name name of the genome
	 */
	public MGSGenome (String name) {
		this.name = name;
		alleles = new ArrayList<MGSAllele>();
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * Sorts the alleles according to the position of the variation and their length (longer first when position is equal)
	 */
	public void sort() {
		for (MGSAllele allele: alleles) {
			allele.sort();
		}
	}


	/**
	 * Compacts the list of {@link MGSOffset}
	 * The lists are optimized for memory usage, however some parts of it can be empty.
	 * Calling this method will compact lists for both alleles.
	 */
	public void compact () {
		for (MGSAllele allele: alleles) {
			allele.compact();
		}
	}


	/**
	 * Looks for the allele at the given index.
	 * If the index too high, the right amount of {@link MGSAllele} will be added to the list to reach the given index.
	 * @param index an index in/out the list (starts at 0)
	 * @return the {@link MGSAllele} at the index
	 */
	public MGSAllele getAllele (int index) {
		int lastIndex = alleles.size() - 1;
		if (index > lastIndex) {
			for (int i = lastIndex; i < index; i++) {
				alleles.add(new MGSAllele());
			}
		}
		return alleles.get(index);
	}


	/**
	 * @param alleleType the {@link AlleleType}
	 * @return the right {@link MGSAllele} according to the given {@link AlleleType}, null otherwise
	 */
	public MGSAllele getAllele (AlleleType alleleType) {
		if (alleleType == AlleleType.ALLELE01) {
			return getAllele(0);
		} else if (alleleType == AlleleType.ALLELE02) {
			return getAllele(1);
		}
		return null;
	}


	/**
	 * @return the list of {@link MGSAllele}
	 */
	public List<MGSAllele> getAlleles () {
		return alleles;
	}


	/**
	 * Show the information of the {@link MGSGenome}
	 */
	public void show () {
		System.out.println("Genome: " + name);
		for (int i = 0; i < alleles.size(); i++) {
			System.out.println("Allele: " + (i + 1));
			alleles.get(i).show();
		}
	}

}
