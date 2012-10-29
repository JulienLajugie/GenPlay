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
package edu.yu.einstein.genplay.core.multiGenome.synchronization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.manager.project.MultiGenomeProject;
import edu.yu.einstein.genplay.core.multiGenome.display.MGAlleleForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.display.MGAlleleReferenceForDisplay;


/**
 * This class manages allele information in order to simplify the synchronization process.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGAlleleHandler {

	private final MultiGenomeProject 					multiGenomeProject;		// The multi genome project.
	private final MGAlleleReferenceForDisplay			alleleReferenceDisplay;	// The allele for display of the reference genome
	private final Map<AlleleType, Integer> 				alleleIndexMap;			// A map between allele types and the genotype index they refer.
	private final Map<AlleleType, List<MGOffset>> 		alleleOffsetMap;		// A map between allele types and the list of offset they refer.
	private final Map<AlleleType, MGAlleleForDisplay> 	alleleDisplayMap;		// A map between allele types and the list of allele for display they refer.


	/**
	 * Constructor of {@link MGAlleleHandler}
	 * @param multiGenomeProject the multi genome project
	 */
	protected MGAlleleHandler (MultiGenomeProject multiGenomeProject) {
		this.multiGenomeProject = multiGenomeProject;
		this.alleleReferenceDisplay = multiGenomeProject.getMultiGenomeForDisplay().getReferenceGenome().getAllele();
		alleleIndexMap = new HashMap<AlleleType, Integer>();
		alleleOffsetMap = new HashMap<AlleleType, List<MGOffset>>();
		alleleDisplayMap = new HashMap<AlleleType, MGAlleleForDisplay>();
	}


	/**
	 * Initializes all maps
	 * @param chromosome		the current chromosome
	 * @param fullGenomeName	the current genome
	 * @param alleleIndex01		the genotype index for the first allele
	 * @param alleleIndex02		the genotype index for the second allele
	 */
	protected void initialize (Chromosome chromosome, String fullGenomeName, int alleleIndex01, int alleleIndex02) {
		MGGenome mgGenome = multiGenomeProject.getMultiGenome().getGenomeInformation(fullGenomeName);
		alleleOffsetMap.put(AlleleType.ALLELE01, mgGenome.getAlleleA().getOffsetList().get(chromosome));
		alleleOffsetMap.put(AlleleType.ALLELE02, mgGenome.getAlleleB().getOffsetList().get(chromosome));
		alleleDisplayMap.put(AlleleType.ALLELE01, multiGenomeProject.getMultiGenomeForDisplay().getGenomeInformation(fullGenomeName).getAlleleA());
		alleleDisplayMap.put(AlleleType.ALLELE02, multiGenomeProject.getMultiGenomeForDisplay().getGenomeInformation(fullGenomeName).getAlleleB());
		alleleIndexMap.put(AlleleType.ALLELE01, alleleIndex01);
		alleleIndexMap.put(AlleleType.ALLELE02, alleleIndex02);
	}


	/**
	 * @param alleleType an allele type
	 * @return the genotype index for the given allele
	 */
	protected int getAlleleIndex (AlleleType alleleType) {
		return alleleIndexMap.get(alleleType);
	}


	/**
	 * @param alleleType an allele type
	 * @return the list of offset for the given allele
	 */
	protected List<MGOffset> getOffsetList (AlleleType alleleType) {
		return alleleOffsetMap.get(alleleType);
	}


	/**
	 * @param alleleType an allele type
	 * @return the allele for display for the given allele
	 */
	protected MGAlleleForDisplay getAlleleForDisplay (AlleleType alleleType) {
		return alleleDisplayMap.get(alleleType);
	}


	/**
	 * @return the alleleReferenceDisplay
	 */
	protected MGAlleleReferenceForDisplay getAlleleReferenceDisplay() {
		return alleleReferenceDisplay;
	}

}
