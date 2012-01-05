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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;




/**
 * This class manages the genome information in a multi genome project.
 * The genome information is all variation information for a specific genome.
 * Those information are mainly the list of {@link MGChromosomeOld} object.
 * 
 * This class can be considered as a "sub-class" of {@link MGMultiGenomeOld}.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGGenomeOld implements Serializable {

	private static final long serialVersionUID = -8473586977950413283L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private		String							genomeName;				// The full genome information
	private 	Map<Chromosome, MGChromosomeOld> 	genomeInformation;		// Chromosomes information of the genome
		
	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(genomeName);
		out.writeObject(genomeInformation);		
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
		genomeName = (String) in.readObject();
		genomeInformation = (Map<Chromosome, MGChromosomeOld>) in.readObject();
	}
	

	/**
	 * Constructor of {@link MGGenomeOld}
	 * @param genomeName the name of the genome
	 */
	protected MGGenomeOld (String genomeName) {
		this.genomeName = genomeName;
		genomeInformation = new HashMap<Chromosome, MGChromosomeOld>();
		for (Chromosome chromosome: ProjectManager.getInstance().getProjectChromosome().getChromosomeList()) {
			genomeInformation.put(chromosome, new MGChromosomeOld(this, chromosome));
		}
	}


	/**
	 * Adds a variant according to a chromosome.
	 * @param chromosome 	the chromosome
	 * @param variant 		the variant
	 */
	protected void addVariant (Chromosome chromosome, Variant variant) {
		getChromosomeInformation(chromosome).addVariant(variant);
	}
	
	
	/**
	 * Adds a variant according to a chromosome.
	 * @param chromosome 	the chromosome
	 * @param position 		the position on the reference genome
	 * @param variant 		the variant
	 */
	protected void addBlank (Chromosome chromosome, int position, Variant variant) {
		getChromosomeInformation(chromosome).addBlank(position, variant);
	}


	/**
	 * @param chromosome 	the related chromosome
	 * @return				valid chromosome containing position information
	 */
	protected MGChromosomeOld getChromosomeInformation (Chromosome chromosome) {
		/*if (genomeInformation.get(chromosome) == null &&
				ProjectChromosome.CHROMOSOME_LOADING_OPTION == ProjectChromosome.SEQUENTIAL) {
			System.err.println("A null pointer exception can appear because of the CHROMOSOME_LOADING_OPTION set to SEQUENTIAL");
		}*/
		return genomeInformation.get(chromosome);
	}
	
	
	/**
	 * Refreshes chromosome references from a chromosome list information.
	 * @param chromosomeList the chromosome list
	 */
	protected void refreshChromosomeReferences (List<Chromosome> chromosomeList) {
		//System.out.println("----- Genome name: " + genomeName);
		Map<Chromosome, MGChromosomeOld> newGenomeInformation = new HashMap<Chromosome, MGChromosomeOld>();
		
		for (Chromosome newChromosome: chromosomeList) {
			
			for (Chromosome previousChromosome: genomeInformation.keySet()) {
				
				if (newChromosome.getName().equals(previousChromosome.getName())) {
					MGChromosomeOld mgChromosome = genomeInformation.get(previousChromosome);
					newGenomeInformation.put(newChromosome, mgChromosome);
					
					//System.out.println(previousChromosome.getName() + " (" + previousChromosome.getLength() + ") -> " + 
							//newChromosome.getName() + " (" + newChromosome.getLength() + ")");
				}
			}
		}
		
		genomeInformation = newGenomeInformation;
	}


	/**
	 * @param chromosome	the chromosome
	 * @param position		the position
	 * @return				the type of a specified position according to the chromosome
	 */
	protected VariantType getType (Chromosome chromosome, Integer position) {
		return getChromosomeInformation(chromosome).getType(position);
	}


	/**
	 * @return the genomeInformation
	 */
	public Map<Chromosome, MGChromosomeOld> getGenomeInformation() {
		return genomeInformation;
	}


	/**
	 * @return the genomeFullName
	 */
	public String getGenomeName() {
		return genomeName;
	}

	
	/**
	 * @param chromosome	a chromosome
	 * @param position 		reference genome position
	 * @return				the position information according to the chromosome and the position
	 */
	protected MGPositionOld getMGPositionOld (Chromosome chromosome, int position) {
		return genomeInformation.get(chromosome).getMGPositionOld(position);
	}
	
	
	/**
	 * Shows chromosome information.
	 */
	protected void showData () {
		for (Chromosome chromosome: genomeInformation.keySet()) {
			System.out.println("= chromosome name: " + chromosome.getName() + " (" + chromosome.getLength() + ")");
			getChromosomeInformation(chromosome).showData();
		}
	}
}
