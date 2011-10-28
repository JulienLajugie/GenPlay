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
package edu.yu.einstein.genplay.core.multiGenome.engine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;


/**
 * This class manages the multi genome information in a multi genome project.
 * It gathers all genome information of the project.
 * The main information is the list of {@link MGGenome}.
 * The second one is the mapping between the full genome name (see description of {@link FormattedMultiGenomeName}) and their associated VCF file.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGMultiGenome implements Serializable {

	private static final long serialVersionUID = -1420140357798946704L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private Map<String, MGGenome> 			multiGenomeInformation;	// Genomes information: key are genome raw names

	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(multiGenomeInformation);
		
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
		multiGenomeInformation = (Map<String, MGGenome>) in.readObject();
	}

	
	/**
	 * Constructor of {@link MGMultiGenome}
	 */
	public MGMultiGenome () {
		multiGenomeInformation = new HashMap<String, MGGenome>();
	}


	/**
	 * Initializes the multi genome information at any runs of the action process
	 * -> can be run several times
	 * @param genomeNames list of genome names
	 */
	public void initMultiGenomeInformation (List<String> genomeNames) {
		multiGenomeInformation = new HashMap<String, MGGenome>();
		
		for (String genomeName: genomeNames) {
			multiGenomeInformation.put(genomeName, new MGGenome(genomeName));
		}
		String referenceGenomeFullName = ProjectManager.getInstance().getAssembly().getDisplayName();
		multiGenomeInformation.put(referenceGenomeFullName, new MGGenome(referenceGenomeFullName));
	}

	
	/**
	 * Adds a variant according to a chromosome.
	 * @param genome 		the genome name
	 * @param chromosome 	the chromosome
	 * @param variant 		the variant
	 */
	public void addVariant (String genome, Chromosome chromosome, Variant variant) {
		multiGenomeInformation.get(genome).addVariant(chromosome, variant);
	}
	
	
	/**
	 * Adds a variant according to a chromosome.
	 * @param genome 		the genome name
	 * @param chromosome 	the chromosome
	 * @param position 		the position on the reference genome
	 * @param variant 		the variant
	 */
	public void addBlank (String genome, Chromosome chromosome, int position, Variant variant) {
		multiGenomeInformation.get(genome).addBlank(chromosome, position, variant);
	}


	/**
	 * @param chromosome 	a chromosome
	 * @return 				list of chromosome information for every genomes according to the given chromosome
	 */
	public List<MGChromosome> getChromosomeInformationList (Chromosome chromosome) {
		List<MGChromosome> info = new ArrayList<MGChromosome>();
		for (String genomeName: multiGenomeInformation.keySet()) {
			if (!genomeName.equals(ProjectManager.getInstance().getAssembly().getDisplayName())) {
				info.add(multiGenomeInformation.get(genomeName).getChromosomeInformation(chromosome));
			}
		}
		return info;
	}

	
	/**
	 * Refreshes chromosome references from a chromosome list information.
	 * @param chromosomeList the chromosome list
	 */
	public void refreshChromosomeReferences (List<Chromosome> chromosomeList) {
		for (MGGenome genome: multiGenomeInformation.values()) {
			genome.refreshChromosomeReferences(chromosomeList);
		}
	}
	

	/**
	 * @param genome		a genome name
	 * @param chromosome	a chromosome
	 * @return				the chromosome information object according to a genome and a chromosome.
	 */
	public MGChromosome getChromosomeInformation (String genome, Chromosome chromosome) {
		return multiGenomeInformation.get(genome).getChromosomeInformation(chromosome);
	}
	
	
	/**
	 * @param genome		a genome name
	 * @return				the genome information object according to a genome.
	 */
	public MGGenome getGenomeInformation (String genome) {
		return multiGenomeInformation.get(genome);
	}


	/**
	 * @return the multiGenomeInformation
	 */
	public Map<String, MGGenome> getMultiGenomeInformation() {
		return multiGenomeInformation;
	}
	
	
	/**
	 * @param genomeName 	a genome name
	 * @param chromosome	a chromosome
	 * @param position 		reference genome position
	 * @return				the position information according to the genome name, the chromosome and the position
	 */
	public MGPosition getMGPosition (String genomeName, Chromosome chromosome, int position) {
		return multiGenomeInformation.get(genomeName).getMGPosition(chromosome, position);
	}


	/**
	 * Shows content information
	 */
	public void showData () {
		System.out.println("===== Data");
		for (String genomeName: multiGenomeInformation.keySet()) {
			System.out.println("Genome name: " + genomeName);
			multiGenomeInformation.get(genomeName).showData();
		}
	}

}
