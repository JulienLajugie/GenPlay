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

import edu.yu.einstein.genplay.core.comparator.MGOffsetComparator;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.list.primitiveList.IntArrayAsOffsetList;


/**
 * @author Nicolas Fourel
 */
public class MGSReference implements Serializable {

	/** Default serial version ID */
	private static final long serialVersionUID = -7912709879126635029L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version
	private MGSAllele allele;		// The unique allele of the reference genome.


	/**
	 * Constructor of {@link MGSReference}
	 */
	public MGSReference () {
		allele = new MGSAllele();
	}


	/**
	 * Compacts the list of {@link MGSOffset}
	 */
	public void compact () {
		allele.compact();
	}


	/**
	 * @return the allele of the reference genome
	 */
	public MGSAllele getAllele() {
		return allele;
	}


	/**
	 * @return the name of the reference genome
	 */
	public String getName () {
		return ProjectManager.getInstance().getAssembly().getDisplayName();
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		allele = (MGSAllele) in.readObject();
	}


	/**
	 * Removes all duplicate from the lists of position in every chromosome.
	 * This algorithm keeps the last version of a duplicate.
	 * The last duplicate, according to the {@link MGOffsetComparator} contains the longest length.
	 */
	public void removeDuplicate () {
		List<List<MGSOffset>> chromosomeListOfList = allele.getOffsetList();									// get the chromosome list of offset list of the reference genome allele
		List<List<MGSOffset>> chromosomeListOfListTmp = new ArrayList<List<MGSOffset>>();						// create a temporary chromosome list of offset list

		int chromosomeListSize = ProjectManager.getInstance().getProjectChromosomes().getChromosomeList().size();		// get the number of chromosome
		for (int i = 0; i < chromosomeListSize; i++) {																	// loop from 0 to the number of chromosome (loop on the chromosomes)
			List<MGSOffset> listOfMGOffset = chromosomeListOfList.get(i);												// get the offset list of the current chromosome
			List<MGSOffset> listOfMGOffsetTmp = new IntArrayAsOffsetList();												// create a temporary offset list

			int currentIndex = 0;																						// set the current index to 0
			int nextIndex = currentIndex + 1;																			// set the next index to 1
			int lastIndex = listOfMGOffset.size() - 1;																	// set the last index to the offset list - 1

			while (currentIndex <= lastIndex) {																			// while the current index is under or equal to the last index
				MGSOffset currentMGOffset = listOfMGOffset.get(currentIndex);											// we get the current offset
				MGSOffset nextMGOffset = null;																			// the next offset is set to null by default
				if (nextIndex <= lastIndex) {																			// if the next index is valid (inferior or equal to the last index)
					nextMGOffset = listOfMGOffset.get(nextIndex);														// we get the next offset
					if (currentMGOffset.getPosition() != nextMGOffset.getPosition()) {									// if the position of the current index is different than the position of the next index
						listOfMGOffsetTmp.add(currentMGOffset);															// the current index is the last of a potential sequence of identical (according to the position) offsets, we can insert!
					}																									// if position are similar, we want to go to the next offset because it has a higher value (see MGOffsetComparator)
				} else {																								// if the next index is not valid, it means the current offset is the last one
					listOfMGOffsetTmp.add(currentMGOffset);																// we want to insert it
				}
				currentIndex++;																							// increase the current index
				nextIndex++;																							// increase the next index
			}
			chromosomeListOfListTmp.add(listOfMGOffsetTmp);																// we add the temporary offset list to the temporary chromosome list of offset list
		}

		allele.setOffsetList(chromosomeListOfListTmp);																	// we change the current chromosome list of offset by the temporary one
	}


	/**
	 * Show the information of the {@link MGSGenome}
	 */
	public void show () {
		System.out.println("Reference genome: " + getName());
		allele.show();
	}


	/**
	 * Sorts the allele according to the position of the variation
	 */
	public void sort() {
		allele.sort();
	}


	/**
	 * Synchronizes the position of the reference genome.
	 */
	public void synchronizePosition () {
		int chromosomeListSize = ProjectManager.getInstance().getProjectChromosomes().getChromosomeList().size();		// get the number of chromosome
		for (int i = 0; i < chromosomeListSize; i++) {																	// loop from 0 to the number of chromosome (loop on the chromosomes)
			List<MGSOffset> offsetList = allele.getOffsetList().get(i);
			int value = 0;
			for (int j = 0; j < offsetList.size(); j++) {
				MGSOffset currentOffset = offsetList.get(j);
				int position = currentOffset.getPosition() + 1;
				value += currentOffset.getValue();
				MGSOffset newOffset = new MGSOffset(position, value);
				offsetList.set(j, newOffset);
			}
		}
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(allele);
	}
}
