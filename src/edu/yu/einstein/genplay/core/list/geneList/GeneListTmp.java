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
package edu.yu.einstein.genplay.core.list.geneList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.yu.einstein.genplay.core.chromosomeWindow.ChromosomeWindowStartComparator;
import edu.yu.einstein.genplay.core.chromosomeWindow.ChromosomeWindowStopComparator;
import edu.yu.einstein.genplay.core.gene.Gene;
import edu.yu.einstein.genplay.core.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.core.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.DisplayableDataList;


/**
 * A list of {@link Gene} with tool to rescale it
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GeneListTmp extends ChromosomeArrayListOfLists<Gene> implements Serializable, ChromosomeListOfLists<Gene>, DisplayableDataList<List<List<Gene>>> {
	List<List<Gene>> fittedDataList;
	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -1567605708127718216L;


	/**
	 * The name of the genes are printed if the horizontal ratio is above this value
	 */
	public  static final double	MIN_X_RATIO_PRINT_NAME = 0.0005d;


	@Override
	public List<List<Gene>> getFittedData(GenomeWindow genomeWindow, double xRatio) {
		if (fittedDataList == null) {
			return null;
		}
		List<List<Gene>> resultList = new ArrayList<List<Gene>>();
		// search genes for each line
		for (List<Gene> currentLine : fittedDataList) {
			// search the start
			int indexStart = Collections.binarySearch(currentLine, genomeWindow, new ChromosomeWindowStartComparator());
			// search if the there is a previous stop (stop of the gene or stop of the name of the string) stopping after the start
			if (indexStart > 0) {
				indexStart = indexStart - 1;
			}
			// search the stop
			int indexStop = Collections.binarySearch(currentLine, genomeWindow, new ChromosomeWindowStopComparator());
			if (currentLine.get(indexStart) != null) {
				// add all the genes found for the current line between index start and index stop to the result list
				resultList.add(new ArrayList<Gene>());
				for (int i = indexStart; i <= indexStop; i++) {
					resultList.get(resultList.size() - 1).add(currentLine.get(i));
				}
			}
		}
		return resultList;
	}

}
