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
import java.util.Collections;
import java.util.List;

import edu.yu.einstein.genplay.core.comparator.MGOffsetComparator;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.IntArrayAsOffsetList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicDataArrayList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicListView;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGSAllele implements Serializable {

	/** Default serial version ID */
	private static final long serialVersionUID = -3160689645132714945L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version
	private GenomicListView<MGSOffset> offsetList;					// List of offset organized by chromosome


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(offsetList);
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
		offsetList = (GenomicListView<MGSOffset>) in.readObject();
	}


	/**
	 * Constructor of {@link MGSAllele}
	 */
	public MGSAllele () {
		offsetList = new GenomicDataArrayList<MGSOffset>();
		int chromosomeListSize = ProjectManager.getInstance().getProjectChromosome().getChromosomeList().size();
		for (int i = 0; i < chromosomeListSize; i++) {
			offsetList.add(new IntArrayAsOffsetList());
		}
	}


	/**
	 * @param offsetList the offsetList to set
	 */
	public void setOffsetList(GenomicListView<MGSOffset> offsetList) {
		this.offsetList = offsetList;
	}


	/**
	 * @return the offsetList
	 */
	public GenomicListView<MGSOffset> getOffsetList() {
		return offsetList;
	}


	/**
	 * Sorts the list of offset for every chromosome.
	 * The sorting is done with the {@link MGOffsetComparator} comparator.
	 */
	public void sort () {
		int chromosomeListSize = ProjectManager.getInstance().getProjectChromosome().getChromosomeList().size();
		MGOffsetComparator comparator = new MGOffsetComparator();
		for (int i = 0; i < chromosomeListSize; i++) {
			Collections.sort(offsetList.get(i), comparator);
		}
	}


	/**
	 * Compacts the list of {@link MGSOffset}
	 */
	public void compact () {
		List<Chromosome> chromosomeList = ProjectManager.getInstance().getProjectChromosome().getChromosomeList();
		for (Chromosome chromosome: chromosomeList) {
			((IntArrayAsOffsetList)offsetList.get(chromosome)).compact();
		}
	}


	/**
	 * Show the information of the {@link MGSAllele}
	 */
	public void show () {
		List<Chromosome> chromosomeList = ProjectManager.getInstance().getProjectChromosome().getChromosomeList();
		for (Chromosome chromosome: chromosomeList) {
			if (offsetList.get(chromosome).size() > 0) {
				System.out.println("Chromosome: " + chromosome.getName());
				int cpt = 0;
				for (MGSOffset offset: offsetList.get(chromosome)) {
					if (cpt < 10) {
						offset.show();
						cpt++;
					}
				}
			}
		}
		/*System.out.println("Chromosome: " + chromosomeList.get(0).getName());
		for (MGOffset offset: offsetList.get(chromosomeList.get(0))) {
			offset.show();
		}*/
	}
}
