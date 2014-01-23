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
package edu.yu.einstein.genplay.dataStructure.genome;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains clade information
 * @author Nicolas Fourel
 */
public class Clade implements Serializable, Comparable<Clade> {

	private static final long 	serialVersionUID = -898740770908389562L;	// generated ID
	private final Map<String, Genome>		genomeList;		// the genome list
	private final String					name; 			// Name of the clade


	/**
	 * Constructor of {@link Clade}
	 * @param name	name of the clade
	 */
	public Clade (String name) {
		genomeList = new HashMap<String, Genome>();
		this.name = name;
	}


	/**
	 * Add a genome to the clade.
	 * If the genome is already existing, this method will try to add the assembly.
	 * @param genome	genome to add
	 */
	public void addGenome (Genome genome) {
		if (!genomeList.containsKey(genome.getName())){
			genomeList.put(genome.getName(), genome);
		} else {
			for (Assembly assembly: genome.getAssemblyList().values()){
				genomeList.get(genome.getName()).addAssembly(assembly);
			}
		}
	}


	/**
	 * @return the genome list
	 */
	public Map<String, Genome> getGenomeList() {
		return genomeList;
	}


	/**
	 * @return the clade name
	 */
	public String getName() {
		return name;
	}


	@Override
	public String toString() {
		return getName();
	}


	/**
	 * Clades are ordered by their names
	 */
	@Override
	public int compareTo(Clade otherClade) {
		return getName().compareTo(otherClade.getName());
	}
}
