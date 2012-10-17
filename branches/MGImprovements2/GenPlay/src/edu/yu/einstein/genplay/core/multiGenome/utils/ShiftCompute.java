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
	 * @param inputGenomeName		the genome name of the position to shift
	 * @param inputAlleleType		the allele of the genome
	 * @param inputGenomePosition	the position to shift
	 * @param chromosome			the chromosome where the position is
	 * @param outputGenomeName		the genome where the position has to be shifted
	 * @return						the position of a genome on another genome
	 */
	public static int getPosition (String inputGenomeName, AlleleType inputAlleleType, int inputGenomePosition, Chromosome chromosome, String outputGenomeName) {
		if (FormattedMultiGenomeName.isSameGenome(inputGenomeName, outputGenomeName) || (inputGenomePosition < 0)) {
			return inputGenomePosition;
		}

		int outputPosition = -1;

		if (FormattedMultiGenomeName.isMetaGenome(outputGenomeName)) {
			ChromosomeListOfLists<MGOffset> chromosomeOffsetList = getOffsetList(inputGenomeName, inputAlleleType);
			if (chromosomeOffsetList != null) {
				IntArrayAsOffsetList offsetList = (IntArrayAsOffsetList) chromosomeOffsetList.get(chromosome);
				outputPosition = offsetList.getMetaGenomePosition(inputGenomePosition);
			}
		} else if (FormattedMultiGenomeName.isMetaGenome(inputGenomeName)) {
			ChromosomeListOfLists<MGOffset> chromosomeOffsetList = getOffsetList(outputGenomeName, inputAlleleType);
			if (chromosomeOffsetList != null) {
				IntArrayAsOffsetList offsetList = (IntArrayAsOffsetList) chromosomeOffsetList.get(chromosome);
				outputPosition = offsetList.getGenomePosition(inputGenomePosition);
			}
		} else {
			int inputPositionOnMetaGenome = getPosition(inputGenomeName, inputAlleleType, inputGenomePosition, chromosome, FormattedMultiGenomeName.META_GENOME_NAME);
			outputPosition = getPosition(FormattedMultiGenomeName.META_GENOME_NAME, inputAlleleType, inputPositionOnMetaGenome, chromosome, outputGenomeName);
		}

		return outputPosition;
	}


	private static ChromosomeListOfLists<MGOffset> getOffsetList (String genome, AlleleType alleleType) {
		MGAllele alleleInformation = null;

		if (FormattedMultiGenomeName.isReferenceGenome(genome)) {
			alleleInformation = ProjectManager.getInstance().getMultiGenomeProject().getMultiGenome().getReferenceGenome().getAllele();
		} else {
			MGGenome genomeInformation = ProjectManager.getInstance().getMultiGenomeProject().getMultiGenome().getGenomeInformation(genome);
			if (alleleType == AlleleType.ALLELE01) {
				alleleInformation = genomeInformation.getAlleleA();
			} else if (alleleType == AlleleType.ALLELE02) {
				alleleInformation = genomeInformation.getAlleleB();
			} else {
				System.err.println("Illegal use of the method \"ShiftCompute.computeShift\" with the parameter: " + alleleType + " , genome: " + genome);
				return null;
			}
		}

		ChromosomeListOfLists<MGOffset> offsetList = alleleInformation.getOffsetList();

		return offsetList;
	}

}
