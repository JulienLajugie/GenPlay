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

import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGGenome;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGAlleleForDisplay {
	
	private static final int INSERTION_INDEX = 0;
	private static final int DELETION_INDEX = 1;
	private static final int SNPS_INDEX = 2;
	
	private MGGenome genomeInformation;
	private ChromosomeListOfLists<MGVariantListForDisplay> chromosomeListOfVariantList;
	


	/**
	 * Constructor of {@link MGAlleleForDisplay}
	 */
	protected MGAlleleForDisplay (MGGenome genome) {
		this.genomeInformation = genome;
		chromosomeListOfVariantList = new ChromosomeArrayListOfLists<MGVariantListForDisplay>();
		ProjectChromosome projectChromosome =ProjectManager.getInstance().getProjectChromosome(); 
		int chromosomeNumber = projectChromosome.size();
		for (int i = 0; i < chromosomeNumber; i++) {
			Chromosome chromosome = projectChromosome.get(i);
			List<MGVariantListForDisplay> elements = new ArrayList<MGVariantListForDisplay>();
			elements.add(new MGVariantListForDisplay(this, chromosome, VariantType.INSERTION));
			elements.add(new MGVariantListForDisplay(this, chromosome, VariantType.DELETION));
			elements.add(new MGVariantListForDisplay(this, chromosome, VariantType.SNPS));
			chromosomeListOfVariantList.add(i, elements);
		}
	}
	

	/**
	 * @return the genomeInformation
	 */
	public MGGenome getGenomeInformation() {
		return genomeInformation;
	}


	/**
	 * 
	 * @param chromosome chromosome
	 * @param type 		type of variation
	 * @return			the variant list object for the given chromosome and variation type
	 */
	public MGVariantListForDisplay getVariantList (Chromosome chromosome, VariantType type) {
		List<MGVariantListForDisplay> listOfVariantList = chromosomeListOfVariantList.get(chromosome);
		if (type == VariantType.INSERTION) {
			return listOfVariantList.get(INSERTION_INDEX);
		} else if (type == VariantType.DELETION) {
			return listOfVariantList.get(DELETION_INDEX);
		} else if (type == VariantType.SNPS) {
			return listOfVariantList.get(SNPS_INDEX);
		} else {
			return null;
		}
	}
	
	
	/**
	 * Show the information of the {@link MGAlleleForDisplay}
	 */
	public void show () {
		for (int i = 0; i < chromosomeListOfVariantList.size(); i++) {
			System.out.println("Chromosome: " + ProjectManager.getInstance().getProjectChromosome().get(i).getName());
			List<MGVariantListForDisplay> listOfVariantList = chromosomeListOfVariantList.get(i);
			listOfVariantList.get(INSERTION_INDEX).show();
			listOfVariantList.get(DELETION_INDEX).show();
			listOfVariantList.get(SNPS_INDEX).show();
		}
	}
	
}
