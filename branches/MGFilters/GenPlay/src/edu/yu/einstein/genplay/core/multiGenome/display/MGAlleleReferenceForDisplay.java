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
import edu.yu.einstein.genplay.core.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.ReferenceVariant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGAllele;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGOffset;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGReference;
import edu.yu.einstein.genplay.gui.action.project.PAMultiGenome;


/**
 * This class contains all data for the display of the variations of the reference genome.
 * It does not contain an allele object but directly has the chromosome list of variant list (see {@link MGAllele}).
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGAlleleReferenceForDisplay implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = -2820418368770648809L;
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	private MGReference	 								genome;							// reference genome
	private ChromosomeListOfLists<VariantInterface> 	chromosomeListOfVariantList;	// list of variant for every chromosome


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(genome);
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
		genome = (MGReference) in.readObject();
		chromosomeListOfVariantList = (ChromosomeListOfLists<VariantInterface>) in.readObject();
	}


	/**
	 * Constructor of {@link MGAlleleReferenceForDisplay}
	 */
	protected MGAlleleReferenceForDisplay (MGReference genome) {
		this.genome = genome;
		chromosomeListOfVariantList = new ChromosomeArrayListOfLists<VariantInterface>();
		int chromosomeNumber = ProjectManager.getInstance().getProjectChromosome().size();
		for (int i = 0; i < chromosomeNumber; i++) {
			chromosomeListOfVariantList.add(i, new ArrayList<VariantInterface>());
		}
	}


	/**
	 * Initializes the list list of variation of the allele.
	 * Must be called only once and in the {@link PAMultiGenome}.
	 */
	public void initialize () {
		int chromosomeNumber = chromosomeListOfVariantList.size();
		for (int i = 0; i < chromosomeNumber; i++) {
			List<VariantInterface> variantList = chromosomeListOfVariantList.get(i);
			List<MGOffset> offsetList = genome.getAllele().getOffsetList().get(i);
			for (MGOffset offset: offsetList) {
				VariantInterface variant =  new ReferenceVariant(offset.getPosition(), offset.getValue(), i);
				variantList.add(variant);
			}
		}
	}


	/**
	 * @return the reference genome
	 */
	public MGReference getGenomeInformation() {
		return genome;
	}


	/**
	 * @param chromosome chromosome
	 * @return			the variant list for the given chromosome
	 */
	public List<VariantInterface> getVariantList (Chromosome chromosome) {
		return chromosomeListOfVariantList.get(chromosome);
	}


	/**
	 * Show the information of the {@link MGAlleleReferenceForDisplay}
	 */
	public void show () {
		for (int i = 0; i < chromosomeListOfVariantList.size(); i++) {
			System.out.println("Chromosome: " + ProjectManager.getInstance().getProjectChromosome().get(i).getName());
			List<VariantInterface> listOfVariantList = chromosomeListOfVariantList.get(i);
			int cpt = 0;
			for (VariantInterface variant: listOfVariantList) {
				if (cpt < 10) {
					variant.show();
					cpt++;
				}
			}
		}
	}

}
