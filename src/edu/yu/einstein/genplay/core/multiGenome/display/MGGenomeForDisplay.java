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

import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGGenome;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGGenomeForDisplay implements Serializable {
	
	
	/** Generated serial version ID */
	private static final long serialVersionUID = 322995634084485127L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private MGGenome	 		genome;			// genome
	private MGAlleleForDisplay 	alleleA;		// first allele of the genome
	private MGAlleleForDisplay 	alleleB;		// second allele of the genome
	
	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(genome);
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
		genome = (MGGenome) in.readObject();
		alleleA = (MGAlleleForDisplay) in.readObject();
		alleleB = (MGAlleleForDisplay) in.readObject();
	}
	
	
	/**
	 * Constructor of {@link MGGenomeForDisplay}
	 * @param name full name of the genome
	 */
	protected MGGenomeForDisplay (MGGenome genome) {
		this.genome = genome;
		alleleA = new MGAlleleForDisplay(genome);
		alleleB = new MGAlleleForDisplay(genome);
	}

	
	/**
	 * @return the genome synchronizer
	 */
	public MGGenome getGenome() {
		return genome;
	}
	
	
	/**
	 * @return the allele A of the genome
	 */
	public MGAlleleForDisplay getAlleleA() {
		return alleleA;
	}


	/**
	 * @return the allele B of the genome
	 */
	public MGAlleleForDisplay getAlleleB() {
		return alleleB;
	}

	
	/**
	 * Show the information of the {@link MGGenomeForDisplay}
	 */
	public void show () {
		System.out.println("Genome: " + genome.getName());
		System.out.println("Allele A");
		alleleA.show();
		System.out.println("Allele B");
		alleleB.show();
	}

}
