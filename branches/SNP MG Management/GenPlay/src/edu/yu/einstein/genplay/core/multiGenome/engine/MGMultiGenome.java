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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.multiGenome.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.manager.ProjectManager;
import edu.yu.einstein.genplay.core.manager.multiGenomeManager.ReferenceGenomeManager;
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
public class MGMultiGenome {

	private Map<String, MGGenome> 	multiGenomeInformation;	// Genomes information: key are genome raw names
	private Map<String, List<File>> genomeFileAssociation;	// Mapping between genome names and their files.


	/**
	 * Constructor of {@link MGMultiGenome}
	 */
	public MGMultiGenome () {
		multiGenomeInformation = new HashMap<String, MGGenome>();
	}


	/**
	 * Initializes the multi genome information
	 * -> can be run only one time
	 * @param genomeFileAssociation mapping between genome names and their files
	 */
	public void init (Map<String, List<File>> genomeFileAssociation) {
		this.genomeFileAssociation = genomeFileAssociation;
	}


	/**
	 * Initializes the multi genome information at any runs of the action process
	 * -> can be run several times
	 */
	public void initMultiGenomeInformation () {
		for (String genomeName: genomeFileAssociation.keySet()) {
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
			if (!genomeName.equals(ReferenceGenomeManager.getInstance().getReferenceName())) {
				info.add(multiGenomeInformation.get(genomeName).getChromosomeInformation(chromosome));
			}
		}
		return info;
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
	 * @return the list of genome names
	 */
	public List<String> getGenomeNameList () {
		List<String> list = new ArrayList<String>(genomeFileAssociation.keySet()); 
		Collections.sort(list);
		return list;
	}
	
	
	/**
	 * @param genomeName 	name of a genome
	 * @return				list of file related to the genome name
	 */
	public List<File> getFiles (String genomeName) {
		return genomeFileAssociation.get(genomeName);
	}


	/**
	 * @return the total number of genome
	 */
	private int getGenomeNumber () {
		return genomeFileAssociation.size();
	}


	/**
	 * Creates an array with all genome names association.
	 * Used for display.
	 * @return	genome names association array
	 */
	public Object[] getFormattedGenomeArray () {
		String[] names = new String[getGenomeNumber() + 1];
		names[0] = ReferenceGenomeManager.getInstance().getReferenceName();
		int index = 1;
		List<String> namesList = getGenomeNameList();
		for (String name: namesList) {
			names[index] = name;
			index++;
		}
		return names;
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