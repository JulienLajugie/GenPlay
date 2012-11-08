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

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGAllele;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGGenome;


/**
 * This class represents an allele of a genome.
 * It contains a pointer to its genome ({@link MGGenome}) and knows what kind of allele it is ({@link AlleleType}).
 * The lists of variations are stored for every chromosome in a chromosome list of list (like the {@link MGAllele}.
 * Actually, three lists of variations are stored for each chromosome. They are created according to the type of variant they store:
 * - insertions
 * - deletions
 * - SNPs
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGAlleleForDisplay implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = -2820418368770648809L;
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private static final int INSERTION_INDEX 	= 0;					// index for insertions
	private static final int DELETION_INDEX 	= 1;					// index for deletions
	private static final int SNPS_INDEX 		= 2;					// index for SNPs
	private static final int NO_CALL_INDEX 		= 3;					// index for no calls

	private MGGenome genomeInformation;													// the genome information object
	private AlleleType allele;															// the allele type of this allele
	private ChromosomeListOfLists<MGVariantListForDisplay> chromosomeListOfVariantList;	// the lists of variation stored for each chromosome


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(genomeInformation);
		out.writeObject(allele);
		out.writeObject(chromosomeListOfVariantList);
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
		genomeInformation = (MGGenome) in.readObject();
		allele = (AlleleType) in.readObject();
		chromosomeListOfVariantList = (ChromosomeListOfLists<MGVariantListForDisplay>) in.readObject();
	}


	/**
	 * Constructor of {@link MGAlleleForDisplay}
	 */
	protected MGAlleleForDisplay (MGGenome genome, AlleleType allele) {
		this.genomeInformation = genome;
		this.allele = allele;
		chromosomeListOfVariantList = new ChromosomeArrayListOfLists<MGVariantListForDisplay>();
		ProjectChromosome projectChromosome =ProjectManager.getInstance().getProjectChromosome();
		int chromosomeNumber = projectChromosome.size();
		for (int i = 0; i < chromosomeNumber; i++) {
			Chromosome chromosome = projectChromosome.get(i);
			List<MGVariantListForDisplay> elements = new ArrayList<MGVariantListForDisplay>();
			elements.add(new MGVariantListForDisplay(this, chromosome, VariantType.INSERTION));
			elements.add(new MGVariantListForDisplay(this, chromosome, VariantType.DELETION));
			elements.add(new MGVariantListForDisplay(this, chromosome, VariantType.SNPS));
			elements.add(new MGVariantListForDisplay(this, chromosome, VariantType.NO_CALL));
			chromosomeListOfVariantList.add(i, elements);
		}
	}


	/**
	 * @return the genomeInformation
	 */
	public MGGenome getGenomeInformation() {
		return genomeInformation;
	}


	/**
	 * @return the allele
	 */
	public AlleleType getAlleleType() {
		return allele;
	}


	/**
	 * 
	 * @param chromosome chromosome
	 * @param type 		type of variation
	 * @return			the variant list object for the given chromosome and variation type
	 */
	public MGVariantListForDisplay getVariantList (Chromosome chromosome, VariantType type) {
		List<MGVariantListForDisplay> listOfVariantList = chromosomeListOfVariantList.get(chromosome);
		if (type == VariantType.INSERTION) {
			return listOfVariantList.get(INSERTION_INDEX);
		} else if (type == VariantType.DELETION) {
			return listOfVariantList.get(DELETION_INDEX);
		} else if (type == VariantType.SNPS) {
			return listOfVariantList.get(SNPS_INDEX);
		} else if (type == VariantType.NO_CALL) {
			return listOfVariantList.get(NO_CALL_INDEX);
		} else {
			return null;
		}
	}


	/**
	 * Show the information of the {@link MGAlleleForDisplay}
	 */
	public void show () {
		for (int i = 0; i < chromosomeListOfVariantList.size(); i++) {
			System.out.println("Chromosome: " + ProjectManager.getInstance().getProjectChromosome().get(i).getName());
			List<MGVariantListForDisplay> listOfVariantList = chromosomeListOfVariantList.get(i);
			listOfVariantList.get(INSERTION_INDEX).show();
			listOfVariantList.get(DELETION_INDEX).show();
			listOfVariantList.get(SNPS_INDEX).show();
			listOfVariantList.get(NO_CALL_INDEX).show();
		}
	}

}
