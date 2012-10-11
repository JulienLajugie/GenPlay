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
package edu.yu.einstein.genplay.core.multiGenome.synchronization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.core.enums.AlleleType;


/**
 * This class represents a genome, it has a name and two alleles ({@link MGAllele}.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGGenome implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = 5586375868869637887L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private String 		name;			// full name of the genome
	private MGAllele 	alleleA;		// first allele of the genome
	private MGAllele 	alleleB;		// second allele of the genome


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(name);
		out.writeObject(alleleA);
		out.writeObject(alleleB);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		name = (String) in.readObject();
		alleleA = (MGAllele) in.readObject();
		alleleB = (MGAllele) in.readObject();
	}


	/**
	 * Constructor of {@link MGGenome}
	 * @param name full name of the genome
	 */
	protected MGGenome (String name) {
		this.name = name;
		alleleA = new MGAllele();
		alleleB = new MGAllele();
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the allele A of the genome
	 */
	public MGAllele getAlleleA() {
		return alleleA;
	}


	/**
	 * @return the allele B of the genome
	 */
	public MGAllele getAlleleB() {
		return alleleB;
	}


	/**
	 * @param alleleType the first or second allele
	 * @return the {@link MGAllele} according to the requested allele
	 */
	public MGAllele getAllele (AlleleType alleleType) {
		if (alleleType == AlleleType.ALLELE01) {
			return getAlleleA();
		} else if (alleleType == AlleleType.ALLELE02) {
			return getAlleleB();
		}
		return null;
	}


	/**
	 * Sorts the alleles according to the position of the variation and their length (longer first when position is equal)
	 */
	public void sort() {
		alleleA.sort();
		alleleB.sort();
	}


	/**
	 * Compacts the list of {@link MGOffset}
	 * The lists are optimized for memory usage, however some parts of it can be empty.
	 * Calling this method will compact lists for both alleles.
	 */
	public void compact () {
		alleleA.compact();
		alleleB.compact();
	}


	/**
	 * Show the information of the {@link MGGenome}
	 */
	public void show () {
		System.out.println("Genome: " + name);
		System.out.println("Allele A");
		alleleA.show();
		System.out.println("Allele B");
		alleleB.show();
	}

}
