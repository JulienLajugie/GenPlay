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
package edu.yu.einstein.genplay.dataStructure.list.geneList;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.list.GenomicDataList;


/**
 * A list of {@link Gene}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface GeneList extends GenomicDataList<Gene>, Serializable {


	/**
	 * Performs a deep clone of the current GeneList
	 * @return a new GeneList that is a deep copy of this one
	 */
	public GeneList deepClone();


	/**
	 * @return the URL of the gene database that contains information about the genes of this list
	 */
	public String getGeneDBURL();


	/**
	 * @return the type of the scores of the genes and exons of this list (RPKM, max, sum)
	 */
	public GeneScoreType getGeneScoreType();


	/**
	 * @return the {@link GeneSearcher} object that handles gene searches
	 */
	public GeneSearcher getGeneSearcher();


	/**
	 * Sets the URL of the gene database that contains information about the genes of this list.
	 * @param geneDBURL URL of the database containing information about the genes of this list
	 */
	public void setGeneDBURL(String geneDBURL);


	/**
	 * Sets the type of the scores of the genes and exons of this list (RPKM, max, sum)
	 * @param geneScoreType
	 */
	public void setGeneScoreType(GeneScoreType geneScoreType);


	/**
	 * For each chromosome, sorts the genes by position.
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public void sort() throws InterruptedException, ExecutionException;
}
