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
package edu.yu.einstein.genplay.core.multiGenome.utils;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.arrayList.IntArrayAsOffsetList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGAllele;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGGenome;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGOffset;


/**
 * This class manages the shifting process in order to get a meta genome position from a genome position.
 * In a multi genome project, every position must be shifted in order to synchronize tracks. 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ShiftCompute {


	/**
	 * Calculates the meta genome position according to the given genome position
	 * @param genome			the raw genome name (containing the genome position)
	 * @param chromosome		the chromosome (containing the genome position)
	 * @param alleleType 		the allele (paternal or maternal)
	 * @param genomePosition	the given genome position
	 * @return					the corresponding meta genome position
	 */
	public static int computeShift (String genome, Chromosome chromosome, AlleleType alleleType, int genomePosition) {
		if (genome.equals(ProjectManager.getInstance().getAssembly().getDisplayName())) {
			return computeShiftForReferenceGenome(chromosome, genomePosition);
		}
		MGGenome genomeInformation = ProjectManager.getInstance().getMultiGenome().getMultiGenome().getGenomeInformation(genome);

		MGAllele alleleInformation = null;
		if (alleleType == AlleleType.PATERNAL) {
			alleleInformation = genomeInformation.getAlleleA();
		} else if (alleleType == AlleleType.MATERNAL) {
			alleleInformation = genomeInformation.getAlleleB();
		} else {
			System.err.println("Illegal use of the method \"ShiftCompute.computeShift\" with the parameter: " + alleleType);
			return -1;
		}

		ChromosomeListOfLists<MGOffset> offsetList = alleleInformation.getOffsetList();


		int metaGenomePosition = ((IntArrayAsOffsetList)offsetList.get(chromosome)).getMetaGenomePosition(genomePosition);

		// Return the final shifted position
		return metaGenomePosition;
	}


	/**
	 * Calculates the meta genome position according to the given genome position
	 * @param genome				the raw genome name (containing the genome position)
	 * @param chromosome			the chromosome (containing the genome position)
	 * @param alleleType 		the allele (paternal or maternal)
	 * @param metaGenomePosition	the given genome position
	 * @return						the corresponding genome position
	 */
	public static int computeReversedShift (String genome, Chromosome chromosome, AlleleType alleleType, int metaGenomePosition) {
		// Return the final shifted position
		System.err.println("The method \"ShiftCompute.computeReversedShift\" is not instanciated yet");
		return 0;
	}


	/**
	 * Calculates the meta genome position according to the given reference genome position
	 * @param chromosome				the chromosome (containing the reference genome position)
	 * @param referenceGenomePosition	the given reference genome position
	 * @return							the corresponding meta genome position
	 */
	public static int computeShiftForReferenceGenome (Chromosome chromosome, int referenceGenomePosition) {
		ChromosomeListOfLists<MGOffset> offsetList = ProjectManager.getInstance().getMultiGenome().getMultiGenome().getReferenceGenome().getAllele().getOffsetList();
		int metaGenomePosition = ((IntArrayAsOffsetList)offsetList.get(chromosome)).getMetaGenomePosition(referenceGenomePosition);
		// Return the final shifted position
		return metaGenomePosition;
	}

}
