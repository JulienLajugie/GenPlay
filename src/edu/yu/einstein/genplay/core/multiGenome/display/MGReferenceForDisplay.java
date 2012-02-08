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

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGReference;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGReferenceForDisplay implements Serializable {
	
	
	/** Generated serial version ID */
	private static final long serialVersionUID = 322995634084485127L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private MGReference	 				genome;		// reference genome
	private MGAlleleReferenceForDisplay allele;
	
	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(genome);
		out.writeObject(allele);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		genome = (MGReference) in.readObject();
		allele = (MGAlleleReferenceForDisplay) in.readObject();
	}
	
	
	/**
	 * Constructor of {@link MGReferenceForDisplay}
	 */
	protected MGReferenceForDisplay () {
		this.genome = ProjectManager.getInstance().getMultiGenomeProject().getMultiGenome().getReferenceGenome();
		allele = new MGAlleleReferenceForDisplay(genome);
	}

	
	/**
	 * @return the genome synchronizer
	 */
	public MGReference getGenome() {
		return genome;
	}
	
	
	/**
	 * @return the allele A of the genome
	 */
	public MGAlleleReferenceForDisplay getAllele() {
		return allele;
	}

	
	/**
	 * Show the information of the {@link MGReferenceForDisplay}
	 */
	public void show () {
		System.out.println("Reference genome: " + genome.getName());
		System.out.println("Allele");
		allele.show();
	}

}
