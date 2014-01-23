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
package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList;

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.bin.BinListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListBuilder;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;

/**
 * Builder that creates BinList objects.
 * @author Julien Lajugie
 */
public class BinListBuilder extends SCWListBuilder{


	/**
	 * Creates an instance of {@link SCWListBuilder}
	 * @param binSize size of the bins of the result list
	 * @throws CloneNotSupportedException
	 */
	public BinListBuilder(int binSize) throws CloneNotSupportedException {
		super(new BinListViewBuilder(binSize));

	}


	/**
	 * Adds an element to the {@link SCWList} to be built.
	 * The start of the window added is equal to: (index of the element * bin size) <br>
	 * The stop of the window added is equal to: ((index of the element + 1) * bin size)
	 * @param chromosome chromosome of the element to add
	 * @param score score of the window to add
	 * @throws InvalidChromosomeException if the chromosome is not valid
	 * @throws ObjectAlreadyBuiltException if the SCWList has already been created
	 */
	public void addElementToBuild(Chromosome chromosome, float score) throws InvalidChromosomeException, ObjectAlreadyBuiltException {
		BinListViewBuilder binLVB = (BinListViewBuilder) builders.getBuilder(chromosome);
		binLVB.addElementToBuild(score);
	}
}
