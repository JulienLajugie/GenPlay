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
package edu.yu.einstein.genplay.core.multiGenome.VCF.VCFStatistics;

import java.util.Map;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface VCFFileStatistics extends VCFStatistics {


	/**
	 * Add a genome name to the list of genome name
	 * @param genomeName a full genome name
	 */
	public void addGenomeName (String genomeName);


	/**
	 * @param sample 	sample or genome name
	 * @return			the statistics related to that sample
	 */
	public VCFSampleStatistics getSampleStatistics (String sample);


	/**
	 * @return the genomeStatistics
	 */
	public Map<String, VCFSampleStatistics> getGenomeStatistics();


	/**
	 * increment the numberOfSNPs
	 */
	public void incrementNumberOfSNPs();


	/**
	 * increment the numberOfShortInsertions
	 */
	public void incrementNumberOfShortInsertions();


	/**
	 * increment the numberOfLongInsertions
	 */
	public void incrementNumberOfLongInsertions();


	/**
	 * increment the numberOfShortDeletions
	 */
	public void incrementNumberOfShortDeletions();


	/**
	 * increment the numberOfLongDeletions
	 */
	public void incrementNumberOfLongDeletions();


	/**
	 * increment the numberOfLines
	 */
	public void incrementNumberOfLines();

}
