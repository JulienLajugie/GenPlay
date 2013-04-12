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
package edu.yu.einstein.genplay.core.IO.dataReader;

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Interface defining method for data readers that can read elements needed to create {@link Gene} objects.
 * @author Julien Lajugie
 */
public interface GeneReader extends DataReader, ChromosomeWindowReader, SCWReader {


	@Override
	public Chromosome getChromosome();


	/**
	 * @return the exons of the last extracted item
	 */
	public ListView<ScoredChromosomeWindow> getExons();


	/**
	 * @return the URL of the database containing informations on the genes
	 */
	public String getGeneDBURL();


	/**
	 * @return the type of the scores of the genes and exons of this list (RPKM, max, sum)
	 */
	public GeneScoreType getGeneScoreType();


	/**
	 * @return the name of the last extracted item
	 */
	public String getName();


	@Override
	public Float getScore();


	@Override
	public Integer getStart();


	@Override
	public Integer getStop();


	/**
	 * @return the strand of the last extracted item
	 */
	public Strand getStrand();


	/**
	 * @return the UTR3 boundary position of the last extracted item
	 */
	public Integer getUTR3Bound();


	/**
	 * @return the UTR5 boundary position of the last extracted item
	 */
	public Integer getUTR5Bound();
}
