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
package edu.yu.einstein.genplay.core.reader;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.chromosomeWindow.ScoredChromosomeWindow;


/**
 * Interface defining method for extractors that can read {@link ScoredChromosomeWindow}
 * @author Julien Lajugie
 */
public interface SCWReader {

	/**
	 * Reads the next scored chromosome window from a file and returns it.  Returns null if its the end of the file
	 * @return a {@link ScoredChromosomeWindow}
	 */
	public ScoredChromosomeWindow readScoredChromosomeWindow();


	/**
	 * @return the chromosome of the last extracted {@link ScoredChromosomeWindow}
	 */
	public Chromosome getCurrentChromosome();
}
