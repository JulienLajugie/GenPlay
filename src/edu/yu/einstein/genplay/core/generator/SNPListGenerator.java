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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.generator;

import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.SNPList.SNPList;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;



/**
 * The interface SNPListGenerator could be implemented by the class able to create a {@link SNPList}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface SNPListGenerator extends Generator {

	
	/**
	 * @return a new SNPList created from the extracted data
	 * @throws InvalidChromosomeException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public SNPList toSNPList() throws InvalidChromosomeException, InterruptedException, ExecutionException;
}
