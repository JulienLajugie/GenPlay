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
package edu.yu.einstein.genplay.util;

import java.util.Collections;
import java.util.List;

import edu.yu.einstein.genplay.core.comparator.ChromosomeWindowStartComparator;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.ListOfIntArraysAsIntegerList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicDataArrayList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.ImmutableGenomicDataList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.SimpleListView.SimpleListViewBuilder;


/**
 * Provides operation on {@link List} of {@link ChromosomeWindow}
 * @author Julien Lajugie
 */
public class ChromosomeWindowLists {

	/**
	 * @param chromosomeWindowList a {@link GenomicListView} of Object that extends {@link ChromosomeWindow}
	 * @return a {@link ImmutableGenomicDataList} containing the start positions of the specified list. This list is organized the same way as the input list
	 */
	public static GenomicListView<Integer> getStartList(ImmutableGenomicDataList<? extends ChromosomeWindow> chromosomeWindowList) {
		GenomicListView<Integer> list = new GenomicDataArrayList<Integer>();
		for (int i = 0; i < chromosomeWindowList.size(); i++) {
			list.add(new ListOfIntArraysAsIntegerList());
		}

		for (int i = 0; i < chromosomeWindowList.size(); i++) {
			ListView<? extends ChromosomeWindow> currentList = chromosomeWindowList.getView(i);
			for (ChromosomeWindow currentChromosomeWindow: currentList) {
				list.get(i).add(currentChromosomeWindow.getStop());
			}
		}
		return list;
	}


	/**
	 * @param chromosomeWindowList a {@link GenomicListView} of Object that extends {@link ChromosomeWindow}
	 * @return a {@link ImmutableGenomicDataList} containing the stop positions of the specified list. This list is organized the same way as the input list
	 */
	public static GenomicListView<Integer> getStopList(ImmutableGenomicDataList<? extends ChromosomeWindow> chromosomeWindowList) {
		GenomicListView<Integer> list = new GenomicDataArrayList<Integer>();
		for (int i = 0; i < chromosomeWindowList.size(); i++) {
			list.add(new ListOfIntArraysAsIntegerList());
		}

		for (int i = 0; i < chromosomeWindowList.size(); i++) {
			ListView<? extends ChromosomeWindow> currentList = chromosomeWindowList.getView(i);
			for (ChromosomeWindow currentChromosomeWindow: currentList) {
				list.get(i).add(currentChromosomeWindow.getStop());
			}
		}
		return list;
	}


	/**
	 * @param list a {@link GenomicListView} of objects that extends {@link ChromosomeWindow}
	 * @param genomeWindow a {@link GenomeWindow}
	 * @return the chromosome windows of the input list that are located in the specified genome window (including elements that are not fully in the window)
	 */
	public static final <T extends ChromosomeWindow> ListView<T> sublist(GenomicListView<T> list, GenomeWindow genomeWindow) {
		return sublist(list.get(genomeWindow.getChromosome()), genomeWindow.getStart(), genomeWindow.getStop());
	}


	/**
	 * @param list a {@link ListView} of objects that extends {@link ChromosomeWindow}
	 * @param start a start position
	 * @param stop a stop position
	 * @return a {@link ListView} that contains all the elements of the input list that are located between the start and stop positions.
	 * The elements that are not fully between the specified positions are still included.
	 */
	public static final <T extends ChromosomeWindow> ListView<T> sublist(ListView<T> list, int start, int stop) {
		ListViewBuilder<T> resultLVBuilder = new SimpleListViewBuilder<T>();
		ChromosomeWindow startChromosomeWindow = new SimpleChromosomeWindow(start, start);
		ChromosomeWindow stopChromosomeWindow = new SimpleChromosomeWindow(stop, stop);

		// search the start
		int indexStart = Collections.binarySearch(list, startChromosomeWindow, new ChromosomeWindowStartComparator());
		if (indexStart < 0) {
			indexStart = -indexStart - 1;
		}
		// and the previous element if its stop is after the specified start position
		if ((indexStart > 0) && (list.get(indexStart - 1).getStop() > start)) {
			indexStart--;
		}

		// search the stop
		int indexStop = Collections.binarySearch(list, stopChromosomeWindow, new ChromosomeWindowStartComparator());
		if (indexStop < 0) {
			indexStop = -indexStop - 1;
		}
		indexStop = Math.min(list.size(), indexStop);

		// add the elements between the index start and the index stop
		for (int i = indexStart; i < indexStop; i++) {
			resultLVBuilder.addElementToBuild(list.get(i));
		}
		return resultLVBuilder.getListView();
	}
}
