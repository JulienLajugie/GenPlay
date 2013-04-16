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

import java.io.IOException;

import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.exception.exceptions.ExtractorNotInitializedException;

/**
 * Interface defining common methods for the different kind of readers that can read data files.
 * @author Julien Lajugie
 */
public interface DataReader {

	/**
	 * Reads a new item (eg: {@link Gene}, {@link ScoredChromosomeWindow}, etc..) in a file.
	 * Items can be spread on more than one line.
	 * @return false when EOF is reached. True otherwise
	 * @throws ExtractorNotInitializedException If this method is called before the extractor has been initialized
	 * @throws IOException If an I/O error occurs
	 * @throws DataLineException If there is a problem in the data of the file.
	 */
	public boolean readItem() throws ExtractorNotInitializedException, IOException, DataLineException;
}
