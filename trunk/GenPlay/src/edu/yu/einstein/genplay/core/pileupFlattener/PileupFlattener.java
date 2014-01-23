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
package edu.yu.einstein.genplay.core.pileupFlattener;

import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ElementAddedNotSortedException;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;

/**
 * Flattens a pileup of {@link ScoredChromosomeWindow} objects.
 * The score of each "flattened window" is computed from the scores of the
 * windows overlapping the "flattened window".  The {@link ScoreOperation} for
 * the calculation of the resulting score must be specified during the instantiation
 * of this class.
 * The windows involved in the pileup needs to be added in start position order using
 * the {@link #addWindow(int, int, float)} method.
 * This interface extends the {@link Cloneable} interface so {@link PileupFlattener}
 * can be created from an prototype object.
 * @author Julien Lajugie
 */
public interface PileupFlattener extends Cloneable {

	/**
	 * Adds a new window to the {@link PileupFlattener} object.
	 * Windows must be added in start position order.
	 * @param windowStart
	 * @param windowStop
	 * @param windowScore
	 * @throws ElementAddedNotSortedException  If  the elements are not added in sorted order
	 * @throws ObjectAlreadyBuiltException If an element is added after the {@link #getListView()} had been called
	 */
	public void addWindow(int windowStart, int windowStop, float windowScore)throws ElementAddedNotSortedException, ObjectAlreadyBuiltException;


	/**
	 * Adds a new window to the {@link PileupFlattener} object.
	 * Windows must be added in start position order.
	 * @param windowToAdd
	 * @throws ElementAddedNotSortedException  If  the elements are not added in sorted order
	 * @throws ObjectAlreadyBuiltException If an element is added after the {@link #getListView()} had been called
	 */
	public void addWindow(ScoredChromosomeWindow windowToAdd)throws ElementAddedNotSortedException, ObjectAlreadyBuiltException;


	/**
	 * @return a copy of this {@link PileupFlattener} object with no element added.
	 */
	public PileupFlattener clone();


	/**
	 * Build and retrieves the {@link ListView} resulting from the flattening
	 * @return the result of the flattening for the position after
	 */
	public ListView<ScoredChromosomeWindow> getListView();
}
