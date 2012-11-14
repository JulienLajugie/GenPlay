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

import java.util.List;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface VCFGenomeIndexer {


	/**
	 * @param genomeRawName a raw name of a genome
	 * @return the index of the column within a VCF file for the one the given genome name is related to.
	 */
	public int getIndexFromRawGenomeName (String genomeRawName);


	/**
	 * @param genomeFullName a raw name of a genome
	 * @return the index of the column within a VCF file for the one the given genome name is related to.
	 */
	public int getIndexFromFullGenomeName (String genomeFullName);


	/**
	 * @param index index of a column in a VCF file
	 * @return the raw name of the genome related to the index of a column
	 */
	public String getGenomeRawName (int index);


	/**
	 * @return the list of genome raw names
	 */
	public List<String> getGenomeRawNames ();

}
