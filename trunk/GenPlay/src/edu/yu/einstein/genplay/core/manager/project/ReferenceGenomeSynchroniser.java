/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.core.manager.project;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/**
 * This class symbolizes the reference genome in a multi genome project.
 * It stores all variation positions and manages their indexes for scanning.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ReferenceGenomeSynchroniser implements Serializable {

	private static final long serialVersionUID = -3079733967278577422L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private 		Map<String, List<Integer>> 		modifiedPosition;	// List of all position related to an indel
	private 		String 							currentChromosome;	// The current chromosome (for scanning uses)
	private 		int 							currentIndex;		// The current index (for variation position list scanning uses)
	private 		int 							previousIndex;		// The previous index (for variation position list scanning uses)
	private 		boolean 						validIndex;			// Stores the index validity


	/**
	 * Constructor of {@link ReferenceGenomeSynchroniser}
	 */
	protected ReferenceGenomeSynchroniser () {
		modifiedPosition = new TreeMap<String, List<Integer>>();
		currentIndex = 0;
		previousIndex = 0;
		validIndex = false;
	}


	/**
	 * Adds a chromosome in the list of variation positions
	 * @param chromosome the chromosome name
	 */
	protected void addChromosome (String chromosome) {
		setCurrentChromosome(chromosome);
		if (!modifiedPosition.containsKey(chromosome)) {
			//Development.increaseAddChromosome();
			modifiedPosition.put(chromosome, new ArrayList<Integer>());
		}
	}


	/**
	 * Adds a position, the current chromosome is already known
	 * @param position
	 */
	protected void addPosition (Integer position) {
		if (!modifiedPosition.get(currentChromosome).contains(position)) {
			modifiedPosition.get(currentChromosome).add(position);
		}
	}


	/**
	 * Adds a position, the current chromosome is already known
	 * @param position
	 */
	protected void addPosition (String chromosome, Integer position) {
		modifiedPosition.get(chromosome).add(position);
	}


	/**
	 * @return a set of the chromosome list names
	 */
	protected Set<String> getChromosomeList () {
		return modifiedPosition.keySet();
	}


	/**
	 * @return the current position
	 */
	protected int getCurrentPosition () {
		if (currentIndex < modifiedPosition.get(currentChromosome).size()) {
			return modifiedPosition.get(currentChromosome).get(currentIndex);
		}
		return -1;
	}


	/**
	 * @param chromosome the chromosome name
	 * @return the last position of a list of position according to a chromosome
	 */
	protected int getLastPosition (String chromosome) {
		int index = modifiedPosition.get(chromosome).size() - 1;
		return modifiedPosition.get(chromosome).get(index);
	}


	/**
	 * @return the previous position
	 */
	protected int getPreviousPosition () {
		if (previousIndex < modifiedPosition.get(currentChromosome).size()) {
			return modifiedPosition.get(currentChromosome).get(previousIndex);
		}
		return -1;
	}


	/**
	 * @return true if the current index is valid.
	 */
	protected boolean isValidIndex () {
		return validIndex;
	}


	/**
	 * Increases the current index and checks if it is valid.
	 */
	protected void nextIndex () {
		previousIndex = currentIndex;
		currentIndex++;
		if (currentIndex >= modifiedPosition.get(currentChromosome).size()) {
			validIndex = false;
		}
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
		modifiedPosition = (Map<String, List<Integer>>) in.readObject();
		currentChromosome = (String) in.readObject();
		currentIndex = in.readInt();
		previousIndex = in.readInt();
		validIndex = in.readBoolean();
	}


	/**
	 * Sets the current chromosome
	 * @param chromosome the chromosome name
	 */
	private void setCurrentChromosome (String chromosome) {
		currentChromosome = chromosome;
	}


	/**
	 * Initializes the list of variation position according to a chromosome
	 * @param chromosome the chromosome name
	 */
	protected void setList (String chromosome) {
		setCurrentChromosome(chromosome);
		sortPositionList();
		currentIndex = 0;
		setValidIndex();
	}


	/**
	 * Verifies if the current position is valid
	 */
	private void setValidIndex () {
		if (getCurrentPosition() == -1) {
			validIndex = false;
		} else {
			validIndex = true;
		}
	}


	/**
	 * Sorts the list of variation position and remove duplicates
	 */
	private void sortPositionList () {
		List<Integer> list = modifiedPosition.get(currentChromosome);
		Collections.sort(list);
		boolean valid = true;
		int i = 0;
		while (valid) {
			if ((i + 1) < list.size()) {
				if (list.get(i).equals(list.get(i+1))){
					list.remove(i+1);
				} else {
					i++;
				}
			} else {
				valid = false;
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
		out.writeObject(modifiedPosition);
		out.writeObject(currentChromosome);
		out.writeInt(currentIndex);
		out.writeInt(previousIndex);
		out.writeBoolean(validIndex);
	}

}
