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
package edu.yu.einstein.genplay.core.IO.reader;

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;


/**
 * Interface defining method for extractors that can read genes
 * @author Julien Lajugie
 */
public interface GeneReader {

	/**
	 * @return the chromosome of the last gene read
	 */
	public Chromosome getCurrentChromosome();


	/**
	 * @return the URL of the database containing informations on the genes
	 */
	public String getGeneDBURL();


	/**
	 * @return the type of the scores of the genes and exons of this list (RPKM, max, sum)
	 */
	public GeneScoreType getGeneScoreType();


	/**
	 * Reads the next gene from a file and returns it.  Returns null if its the end of the file
	 * @return a {@link Gene}
	 */
	public Gene readGene();
}
