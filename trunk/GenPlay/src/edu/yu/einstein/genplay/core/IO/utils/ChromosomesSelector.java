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
package edu.yu.einstein.genplay.core.IO.utils;

import java.util.HashMap;
import java.util.Map;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;

/**
 * Used by extractors to define the chromosomes that need to be extracted.
 * @author Julien Lajugie
 */
public class ChromosomesSelector {

	/** Map of chromosome names associated with a boolean set to true if the chromosome should be extracted and false if not.
	 * If the map is null, all chromosomes need to be extracted
	 */
	private final Map<String, Boolean> chromosomeSelection;

	/** Index of the last selected chromosome */
	private final int indexLastChromosome;

	/** Save a reference to the {@link ProjectChromosomes} for fast retrieval */
	private final ProjectChromosomes projectChromosomes;


	/**
	 * Creates an instance of {@link ChromosomesSelector}
	 * @param chromosomeSelection chromosome to extract
	 */
	public ChromosomesSelector(boolean[] chromosomeSelection) {
		projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		this.chromosomeSelection = new HashMap<String, Boolean>();
		int indexLastChromosomeTmp = Integer.MAX_VALUE;
		for (int i = 0; i < projectChromosomes.size(); i++) {
			if (chromosomeSelection[i]) {
				indexLastChromosomeTmp = i;
			}
			this.chromosomeSelection.put(projectChromosomes.get(i).getName(), chromosomeSelection[i]);
		}
		indexLastChromosome = indexLastChromosomeTmp;
	}


	/**
	 * @param chromosomeName name of a chromosome
	 * @return true if the specified chromosome is after the last selected has already been extracted.
	 * The order of chromosomes is defined by the {@link ProjectChromosomes}
	 */
	public final boolean isExtractionDone(String chromosomeName) {
		try {
			return projectChromosomes.getIndex(chromosomeName) > indexLastChromosome;
		} catch (InvalidChromosomeException e) {
			return false;
		}
	}


	/**
	 * @param chromosomeName name of a chromosome
	 * @return true if the specified chromosome was set to be extracted. False otherwise.
	 */
	public final boolean isSelected(String chromosomeName) {
		if (chromosomeSelection == null) {
			return true;
		}
		Boolean isSelected = chromosomeSelection.get(chromosomeName);
		return (isSelected != null) && isSelected;
	}
}
