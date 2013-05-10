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
package edu.yu.einstein.genplay.core.pileupFlattener;

import java.util.List;

import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ElementAddedNotSortedException;

/**
 * Flattens a pileup of {@link ScoredChromosomeWindow} objects.
 * The score of each "flattened window" is computed from the scores of the
 * windows overlapping the "flattened window".  The {@link ScoreOperation} for
 * the calculation of the resulting score must be specified during the instantiation
 * of this class.
 * The windows involved in the pileup needs to be added in start position order using
 * the {@link #addWindow(ScoredChromosomeWindow)} method.
 * Once all the windows have been added the {@link #flush()} method needs to be used
 * in order to receive the last "flattened windows".
 * @author Julien Lajugie
 */
public interface PileupFlattener {

	/**
	 * Adds a new window to the {@link PileupFlattener} object.
	 * Windows must be added in start position order.
	 * @param window a {@link ScoredChromosomeWindow} to add
	 * @return a list of {@link ScoredChromosomeWindow} resulting from the flattening
	 * between the start position of the window added in the previous call of this
	 * function and the start position of the window specified in the current call.
	 * @throws ElementAddedNotSortedException  If  the elements are not added in sorted order
	 */
	public List<ScoredChromosomeWindow> addWindow(ScoredChromosomeWindow window)throws ElementAddedNotSortedException;


	/**
	 * Flushes this {@link PileupFlattener} object and returns the result for the positions located after start position
	 * of the window specified at the last call of the {@link #addWindow(ScoredChromosomeWindow)} method
	 * @return the result of the flattening for the position after
	 */
	public List<ScoredChromosomeWindow> flush();
}
