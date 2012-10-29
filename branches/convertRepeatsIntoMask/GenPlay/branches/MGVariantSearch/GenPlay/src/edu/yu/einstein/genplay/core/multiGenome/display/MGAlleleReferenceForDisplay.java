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
import java.util.Collections;
import java.util.List;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.comparator.VariantComparator;
import edu.yu.einstein.genplay.core.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.arrayList.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.Variant;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGAllele;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGReference;
import edu.yu.einstein.genplay.gui.action.multiGenome.synchronization.MGASynchronizing;


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

	private MGReference	 							genome;							// reference genome
	private ChromosomeListOfLists<Variant> 			chromosomeListOfVariantList;	// list of variant for every chromosome
	private ChromosomeListOfLists<Integer> 	chromosomeListOfVariantIndex;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(genome);
		out.writeObject(chromosomeListOfVariantList);
		out.writeObject(chromosomeListOfVariantIndex);
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
		chromosomeListOfVariantList = (ChromosomeListOfLists<Variant>) in.readObject();
		chromosomeListOfVariantIndex = (ChromosomeListOfLists<Integer>) in.readObject();
	}


	/**
	 * Constructor of {@link MGAlleleReferenceForDisplay}
	 */
	protected MGAlleleReferenceForDisplay (MGReference genome) {
		this.genome = genome;
		chromosomeListOfVariantList = new ChromosomeArrayListOfLists<Variant>();
		chromosomeListOfVariantIndex = new ChromosomeArrayListOfLists<Integer>();
		int chromosomeNumber = ProjectManager.getInstance().getProjectChromosome().size();
		for (int i = 0; i < chromosomeNumber; i++) {
			chromosomeListOfVariantList.add(i, new ArrayList<Variant>());
			chromosomeListOfVariantIndex.add(i, new IntArrayAsIntegerList());
		}
	}


	/**
	 * Initializes the list list of variation of the allele.
	 * Must be called only once and in the {@link MGASynchronizing}.
	 */
	public void initialize () {
		int chromosomeNumber = chromosomeListOfVariantList.size();
		for (int i = 0; i < chromosomeNumber; i++) {
			List<Variant> variantList = chromosomeListOfVariantList.get(i);
			Collections.sort(variantList, new VariantComparator());								// sorts the list

			List<Variant> newVariantList = new ArrayList<Variant>();
			int currentIndex = 0;
			int size = variantList.size();
			while (currentIndex < size) {
				int nextIndex = getNextInvolvedIndex(variantList, currentIndex);
				Variant variant = null;
				if (currentIndex == nextIndex) {
					variant = variantList.get(currentIndex);
				} else {
					variant = getDominantVariant(variantList, currentIndex, nextIndex);
				}
				newVariantList.add(variant);
				currentIndex = nextIndex + 1;
			}
			chromosomeListOfVariantList.set(i, newVariantList);
		}
	}


	/**
	 * Goes to the next index involved in an overlap
	 * @param list		the list of {@link Variant}
	 * @param index		the index to start the search
	 * @return			the last index involved in the potential current overlap
	 */
	private int getNextInvolvedIndex (List<Variant> list, int index) {
		int nextIndex = index + 1;
		if (nextIndex < list.size()) {
			if (list.get(index).getReferenceGenomePosition() == list.get(nextIndex).getReferenceGenomePosition()) {
				return getNextInvolvedIndex(list, nextIndex);
			}
		}
		return index;
	}


	private Variant getDominantVariant (List<Variant> list, int firstIndex, int secondIndex) {
		Variant variant = null;
		for (int i = firstIndex; i <= secondIndex; i++) {
			if ((variant == null) || (Math.abs(variant.getLength()) < Math.abs(list.get(i).getLength()))) {
				variant = list.get(i);
			}
		}
		return variant;
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
	public List<Variant> getVariantList (Chromosome chromosome) {
		return chromosomeListOfVariantList.get(chromosome);
	}


	/**
	 * Generate the list of indexes.
	 * All reference positions are stored in a sorted order.
	 * Their index in the list can be used to retrieve information in other list made the same way.
	 * This way, only the {@link MGAlleleReferenceForDisplay} contains this "master" index list.
	 */
	public void createIndexLists () {
		int chromosomeNumber = ProjectManager.getInstance().getProjectChromosome().size();
		for (int i = 0; i < chromosomeNumber; i++) {
			List<Variant> currentVariantlist = chromosomeListOfVariantList.get(i);
			List<Integer> currentIndexList = chromosomeListOfVariantIndex.get(i);
			for (Variant currentVariant: currentVariantlist) {
				int referencePosition = currentVariant.getReferenceGenomePosition();
				currentIndexList.add(referencePosition);
			}
		}
	}


	/**
	 * @param chromosome	a chromosome
	 * @param position a reference genome position
	 * @return the index of the given reference position.
	 */
	public int getPositionIndex (Chromosome chromosome, int position) {
		return ((IntArrayAsIntegerList) chromosomeListOfVariantIndex.get(chromosome)).getIndex(position);
	}


	/**
	 * @param chromosome	a chromosome
	 * @return the number of position for the whole allele
	 */
	public int getPositionIndexSize (Chromosome chromosome) {
		return chromosomeListOfVariantIndex.get(chromosome).size();
	}


	/**
	 * Show the information of the {@link MGAlleleReferenceForDisplay}
	 */
	public void show () {
		for (int i = 0; i < chromosomeListOfVariantList.size(); i++) {
			System.out.println("Chromosome: " + ProjectManager.getInstance().getProjectChromosome().get(i).getName());
			List<Variant> listOfVariantList = chromosomeListOfVariantList.get(i);
			((IntArrayAsIntegerList) chromosomeListOfVariantIndex.get(i)).show();
			int cpt = 0;
			for (Variant variant: listOfVariantList) {
				if (cpt < 20) {
					variant.show();
					cpt++;
				}
			}
		}
	}

}
