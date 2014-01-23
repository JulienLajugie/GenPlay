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
package edu.yu.einstein.genplay.core.multiGenome.utils;
import java.util.List;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.data.synchronization.MGSAllele;
import edu.yu.einstein.genplay.core.multiGenome.data.synchronization.MGSOffset;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.offsetList.IntArrayAsOffsetList;


/**
 * This class manages the shifting process in order to get a meta genome position from a genome position.
 * In a multi genome project, every position must be shifted in order to synchronize tracks.
 * @author Nicolas Fourel
 */
public class ShiftCompute {


	private static List<List<MGSOffset>> getOffsetList (String genome, AlleleType alleleType) {
		MGSAllele alleleInformation = null;

		if (FormattedMultiGenomeName.isReferenceGenome(genome)) {
			alleleInformation = ProjectManager.getInstance().getMultiGenomeProject().getMultiGenome().getReferenceGenome().getAllele();
		} else {
			alleleInformation = ProjectManager.getInstance().getMultiGenomeProject().getMultiGenome().getGenomeInformation(genome).getAllele(alleleType);
			if (alleleInformation == null) {
				System.err.println("Illegal use of the method \"ShiftCompute.computeShift\" with the parameter: " + alleleType + " , genome: " + genome);
				return null;
			}
		}

		List<List<MGSOffset>> offsetList = alleleInformation.getOffsetList();

		return offsetList;
	}


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
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		int chromosomeIndex = projectChromosomes.getIndex(chromosome);
		if (FormattedMultiGenomeName.isMetaGenome(outputGenomeName)) {
			List<List<MGSOffset>> chromosomeOffsetList = getOffsetList(inputGenomeName, inputAlleleType);
			if (chromosomeOffsetList != null) {
				IntArrayAsOffsetList offsetList = (IntArrayAsOffsetList) chromosomeOffsetList.get(chromosomeIndex);
				outputPosition = offsetList.getMetaGenomePosition(inputGenomePosition);
			}
		} else if (FormattedMultiGenomeName.isMetaGenome(inputGenomeName)) {
			List<List<MGSOffset>> chromosomeOffsetList = getOffsetList(outputGenomeName, inputAlleleType);
			if (chromosomeOffsetList != null) {
				IntArrayAsOffsetList offsetList = (IntArrayAsOffsetList) chromosomeOffsetList.get(chromosomeIndex);
				outputPosition = offsetList.getGenomePosition(inputGenomePosition);
			}
		} else {
			int inputPositionOnMetaGenome = getPosition(inputGenomeName, inputAlleleType, inputGenomePosition, chromosome, FormattedMultiGenomeName.META_GENOME_NAME);
			outputPosition = getPosition(FormattedMultiGenomeName.META_GENOME_NAME, inputAlleleType, inputPositionOnMetaGenome, chromosome, outputGenomeName);
		}

		return outputPosition;
	}

}
