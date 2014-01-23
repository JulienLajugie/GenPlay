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
package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.repeatFamilyList;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.IO.dataReader.RepeatReader;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;


/**
 * Factory class for vending {@link SimpleRepeatFamilyList} objects.
 * @author Julien Lajugie
 */
public class SimpleRepeatFamilyListFactory {

	/**
	 * Creates a {@link RepeatFamilyList} from the data retrieved by the specified {@link RepeatReader}.
	 * @param repeatReader a {@link RepeatReader}
	 * @return a {@link RepeatFamilyList}
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws ObjectAlreadyBuiltException
	 * @throws InvalidChromosomeException
	 */
	public static RepeatFamilyList createRepeatList(RepeatReader repeatReader) throws InterruptedException, ExecutionException, CloneNotSupportedException, InvalidChromosomeException, ObjectAlreadyBuiltException, IOException {
		RepeatFamilyListBuilder builder = new RepeatFamilyListBuilder();
		while (repeatReader.readItem()) {
			ChromosomeWindow currentWindow = new SimpleChromosomeWindow(repeatReader.getStart(), repeatReader.getStop());
			builder.addElementToBuild(repeatReader.getChromosome(), repeatReader.getName(), currentWindow);
		}
		return new SimpleRepeatFamilyList(builder.getGenomicList());
	}
}
