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
package edu.yu.einstein.genplay.dataStructure.gene;

import java.io.Serializable;

import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * The {@link Gene} class provides a representation of a gene.
 * @author Julien Lajugie
 */
public interface Gene extends Serializable, ScoredChromosomeWindow {


	/**
	 * @return a ListView with the exons of the gene
	 */
	public ListView<ScoredChromosomeWindow> getExons();


	/**
	 * @return the name of the gene
	 */
	public String getName();


	/**
	 * @return the strand of the gene
	 */
	public Strand getStrand();


	/**
	 * @return the 3' boundary of the translation of the gene
	 */
	public int getUTR3Bound();


	/**
	 * @return the 5' boundary of the translation of the gene
	 */
	public int getUTR5Bound();
}
