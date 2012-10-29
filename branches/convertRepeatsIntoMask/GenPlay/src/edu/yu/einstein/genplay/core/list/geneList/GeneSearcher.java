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
package edu.yu.einstein.genplay.core.list.geneList;

import java.io.Serializable;

import edu.yu.einstein.genplay.core.Gene;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;



/**
 * Searches genes from gene names
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GeneSearcher implements Serializable {

	private static final long serialVersionUID = 2905806587397044885L;			// generated ID
	private final ChromosomeListOfLists<Gene> 	geneList;						// GeneList where to search the genes
	private String 								lastSearchedGeneName = null;	// name of the last searched gene
	private Gene 								lastGeneFound = null;			// last found gene
	private int 								lastFoundChromoIndex;			// index of the chromosome of the last found gene
	private int 								lastFoundGeneIndex;				// index of the last found gene on the chromosome
	private boolean 							wholeWord = false;				// true for a whole word search
	private boolean 							caseSensitive = false;			// true for a case sensitive search


	/**
	 * Creates an instance of {@link GeneSearcher}
	 * @param geneList {@link GeneList} where to search the genes
	 */
	public GeneSearcher(ChromosomeListOfLists<Gene> geneList) {
		super();
		this.geneList = geneList;
	}


	/**
	 * @return the name of the last searched gene
	 */
	public String getLastSearchedGeneName() {
		return lastSearchedGeneName;
	}


	/**
	 * @param gene a {@link Gene}
	 * @param geneName a name
	 * @return true if the Gene is a searched gene.
	 * The definition of a searched gene depends on the
	 * parameters about the case sensitivity and if only whole word are accepted
	 */
	private boolean isASearchedGene(Gene gene, String geneName) {
		if ((gene == null) || (geneName == null) || (geneName.isEmpty())) {
			return false;
		}
		if ((lastGeneFound != null) &&
				(gene.getStart() == lastGeneFound.getStart()) &&
				(gene.getStop() == lastGeneFound.getStop())) {
			// case where the current gene has the same start and stop positions as the last gene
			return false;
		}
		// case where it's a whole word search
		if (wholeWord) {
			if (caseSensitive) {
				// case where the search is case sensitive
				return gene.getName().equals(geneName);
			} else {
				// case where the search is not case sensitive
				return gene.getName().equalsIgnoreCase(geneName);
			}
		} else {
			// case where it's not a whole word search
			if (caseSensitive) {
				// case where the search is case sensitive
				return gene.getName().startsWith(geneName);
			} else {
				// case where the search is not case sensitive
				return gene.getName().toLowerCase().startsWith(geneName.toLowerCase());
			}
		}
	}


	/**
	 * @return true if the search is case sensitive. False otherwise
	 */
	public final boolean isCaseSensitive() {
		return caseSensitive;
	}


	/**
	 * @param chromoIndex an index of a chromosome
	 * @param geneIndex an index on a chromosome
	 * @return true if the two parameters correspond to the indexes of the last found chromosome
	 */
	private boolean isLastFoundGene(int chromoIndex, int geneIndex) {
		return (chromoIndex == lastFoundChromoIndex) && (geneIndex == lastFoundGeneIndex);
	}


	/**
	 * @return true if we search only the whole word matches. False otherwise
	 */
	public final boolean isWholeWorld() {
		return wholeWord;
	}


	/**
	 * Searches the first Gene that corresponds to the specified name.
	 * Returns null if nothing is found
	 * @param geneName a gene name
	 * @return the first Gene that corresponds to the specified name, null if nothing is found
	 */
	public Gene search(String geneName) {
		if ((geneName == null) || (geneName.isEmpty())) {
			// if the input name is blank we return null
			return null;
		}
		lastSearchedGeneName = geneName;
		lastGeneFound = null;
		int i = 0;
		while ((lastGeneFound == null) && (i < geneList.size())) {
			int j = 0;
			while ((lastGeneFound == null) && (j < geneList.size(i))) {
				if (isASearchedGene(geneList.get(i, j), geneName)) {
					// case where a gene is found
					lastGeneFound = geneList.get(i, j);
					lastFoundChromoIndex = i;
					lastFoundGeneIndex = j;
				}
				j++;
			}
			i++;
		}
		return lastGeneFound;
	}


	/**
	 * @return the next gene (starting from the last found gene)
	 * that has a name that correspond to the searched name.
	 * Null is returned if nothing is found
	 */
	public Gene searchNextMatch() {
		if (lastGeneFound == null) {
			return null;
		}
		boolean found = false;
		int i = lastFoundChromoIndex;
		int j = lastFoundGeneIndex + 1;
		while (!found && !isLastFoundGene(i, j)) {
			while (!found && (j < geneList.size(i)) && !isLastFoundGene(i, j)) {
				found = isASearchedGene(geneList.get(i, j), lastSearchedGeneName);
				if (!found) {
					j++;
				}
			}
			if ((!found) &&	!isLastFoundGene(i, j)) {
				i++;
				if (i >= geneList.size()) {
					i = 0;
				}
				j = 0;
			}
		}
		if (found) {
			lastFoundChromoIndex = i;
			lastFoundGeneIndex = j;
			lastGeneFound = geneList.get(i, j);
		}
		return lastGeneFound;
	}


	/**
	 * @return the previous gene (starting from the last found gene)
	 * that has a name that correspond to the searched name
	 * Null is returned if nothing is found
	 */
	public Gene searchPreviousMatch() {
		if (lastGeneFound == null) {
			return null;
		}
		boolean found = false;
		int i = lastFoundChromoIndex;
		int j = lastFoundGeneIndex - 1;
		while (!found && !isLastFoundGene(i, j)) {
			while (!found && (j >= 0) && !isLastFoundGene(i, j)) {
				found = isASearchedGene(geneList.get(i, j), lastSearchedGeneName);
				if (!found) {
					j--;
				}
			}
			if ((!found) && !isLastFoundGene(i, j)) {
				i--;
				if (i < 0) {
					i = geneList.size() - 1;
				}
				j = geneList.size(i) - 1;
			}
		}
		if (found) {
			lastFoundChromoIndex = i;
			lastFoundGeneIndex = j;
			lastGeneFound = geneList.get(i, j);
		}
		return lastGeneFound;
	}


	/**
	 * @return the next gene (starting from the last found gene), null is returned if nothing is found.
	 */
	public Gene searchNextGene() {
		if (lastGeneFound == null) {
			return null;
		}

		int i = lastFoundChromoIndex;
		int j = lastFoundGeneIndex + 1;
		if (j < geneList.get(i).size()) {
			lastFoundChromoIndex = i;
			lastFoundGeneIndex = j;
			lastGeneFound = geneList.get(i, j);
		}

		return lastGeneFound;
	}


	/**
	 * @return the previous gene (starting from the last found gene), null is returned if nothing is found.
	 */
	public Gene searchPreviousGene() {
		if (lastGeneFound == null) {
			return null;
		}

		int i = lastFoundChromoIndex;
		int j = lastFoundGeneIndex - 1;
		if (j >= 0) {
			lastFoundChromoIndex = i;
			lastFoundGeneIndex = j;
			lastGeneFound = geneList.get(i, j);
		}

		return lastGeneFound;
	}


	/**
	 * @param caseSensitive set to true for a case sensitive search
	 * @return a new result for the search with the new parameter. Null if nothing found
	 */
	public Gene setCaseSensitive(boolean caseSensitive) {
		if (this.caseSensitive != caseSensitive) {
			this.caseSensitive = caseSensitive;
			if (isASearchedGene(lastGeneFound, lastSearchedGeneName)) {
				return lastGeneFound;
			} else {
				return search(lastSearchedGeneName);
			}
		} else {
			return lastGeneFound;
		}
	}



	/**
	 * @param wholeWorld set to true for a whole word search
	 * @return a new result for the search with the new parameter. Null if nothing found
	 */
	public Gene setWholeWord(boolean wholeWorld) {
		if (this.wholeWord != wholeWorld) {
			this.wholeWord = wholeWorld;
			if (isASearchedGene(lastGeneFound, lastSearchedGeneName)) {
				return lastGeneFound;
			} else {
				return search(lastSearchedGeneName);
			}
		} else {
			return lastGeneFound;
		}
	}


	/**
	 * @return the lastGeneFound
	 */
	public Gene getLastGeneFound() {
		return lastGeneFound;
	}

}
