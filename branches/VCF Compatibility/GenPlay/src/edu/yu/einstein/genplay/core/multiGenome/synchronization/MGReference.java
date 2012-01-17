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

import java.util.List;

import edu.yu.einstein.genplay.core.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.arrayList.IntArrayAsOffsetList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGReference {

	private MGAllele allele;	// single allele of the reference genome


	/**
	 * Constructor of {@link MGReference}
	 */
	protected MGReference () {
		allele = new MGAllele();
	}


	/**
	 * @return the allele of the reference genome
	 */
	public MGAllele getAllele() {
		return allele;
	}


	/**
	 * Sorts the allele according to the position of the variation 
	 */
	public void sort() {
		allele.sort();
	}


	/**
	 * Removes all duplicate from the lists of position in every chromosome.
	 * This algorithm keeps the last version of a duplicate.
	 * The last duplicate, according to the {@link MGOffsetComparator} contains the longest length.
	 */
	public void removeDuplicate () {
		ChromosomeListOfLists<MGOffset> chromosomeListOfList = allele.getOffsetList();									// get the chromosome list of offset list of the reference genome allele
		ChromosomeListOfLists<MGOffset> chromosomeListOfListTmp = new ChromosomeArrayListOfLists<MGOffset>();			// create a temporary chromosome list of offset list

		int chromosomeListSize = ProjectManager.getInstance().getProjectChromosome().getChromosomeList().size();		// get the number of chromosome
		for (int i = 0; i < chromosomeListSize; i++) {																	// loop from 0 to the number of chromosome (loop on the chromosomes)
			List<MGOffset> listOfMGOffset = chromosomeListOfList.get(i);												// get the offset list of the current chromosome
			List<MGOffset> listOfMGOffsetTmp = new IntArrayAsOffsetList();												// create a temporary offset list

			int currentIndex = 0;																						// set the current index to 0
			int nextIndex = currentIndex + 1;																			// set the next index to 1
			int lastIndex = listOfMGOffset.size() - 1;																	// set the last index to the offset list - 1

			while (currentIndex <= lastIndex) {																			// while the current index is under or equal to the last index 
				MGOffset currentMGOffset = listOfMGOffset.get(currentIndex);											// we get the current offset
				MGOffset nextMGOffset = null;																			// the next offset is set to null by default
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
	 * Compacts the list of {@link MGOffset}
	 */
	public void compact () {
		allele.compact();
	}
	
	
	/**
	 * Synchronizes the position of the reference genome.
	 */
	public void synchronizePosition () {
		int chromosomeListSize = ProjectManager.getInstance().getProjectChromosome().getChromosomeList().size();		// get the number of chromosome
		for (int i = 0; i < chromosomeListSize; i++) {																	// loop from 0 to the number of chromosome (loop on the chromosomes)
			List<MGOffset> offsetList = allele.getOffsetList().get(i);
			int value = 0;
			for (int j = 0; j < offsetList.size(); j++) {
				MGOffset currentOffset = offsetList.get(j);
				int position = currentOffset.getPosition() + 1;
				value += currentOffset.getValue();
				MGOffset newOffset;
				if (j == 0) {
					newOffset = new MGOffset(position, value);
				} else {
					newOffset = new MGOffset(position, value);
				}
				offsetList.set(j, newOffset);
			}
		}
	}


	/**
	 * Show the information of the {@link MGGenome}
	 */
	public void show () {
		System.out.println("Reference genome: " + ProjectManager.getInstance().getAssembly().getDisplayName());
		System.out.println("Allele");
		allele.show();
	}

}
