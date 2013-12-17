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
package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList;

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListStats.SCWListStats;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * A {@link GenomicListView} of {@link ScoredChromosomeWindow}
 * @author Julien Lajugie
 */
public interface SCWList extends GenomicListView<ScoredChromosomeWindow> {


	/**
	 * @return the number of steps needed to create a {@link SCWList} simimlar to this one
	 */
	public int getCreationStepCount();


	/**
	 * @param chromosome a {@link Chromosome}
	 * @param position a position on the chromosome
	 * @return the score at the specified position
	 */
	public float getScore(Chromosome chromosome, int position);


	/**
	 * @return the type of the {@link SCWList}
	 */
	public SCWListType getSCWListType();



	/**
	 * @return the statistics of the {@link SCWList}
	 */
	public SCWListStats getStatistics();
}
