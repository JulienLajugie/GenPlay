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
package edu.yu.einstein.genplay.dataStructure.list.arrayList;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Variant of the {@link IntArrayAsIntegerList} class, implementing the same interfaces,
 * a bit slower but creating a smaller memory peak
 * @author Julien Lajugie
 */
public class SuperIntArrayAsIntegerList extends AbstractList<Integer> implements Serializable, List<Integer> {

	private static final long serialVersionUID = 8605012555225930866L;	// generated ID
	private static final int ARRAY_SIZE = 10000;		// size of the sub arrays
	private final List<int[]> 	data;					// data of the list
	private int 				currentIndex = 0;		// index in the current sub-array of the list


	/**
	 * Creates an instance of {@link SuperIntArrayAsIntegerList}
	 */
	public SuperIntArrayAsIntegerList() {
		data = new ArrayList<int[]>();
		data.add(new int[ARRAY_SIZE]);
	}


	/**
	 * Creates an instance of {@link SuperIntArrayAsIntegerList}
	 * @param size size of the list
	 */
	public SuperIntArrayAsIntegerList(int size) {
		int listCount = (size / ARRAY_SIZE) + 1;
		data = new ArrayList<int[]>(listCount);
	}


	@Override
	public boolean add(Integer e) {
		int[] currentArray = data.get(data.size() - 1);
		if (currentIndex < currentArray.length) {
			currentArray[currentIndex] = e;
			currentIndex++;
			return true;
		} else {
			data.add(new int[ARRAY_SIZE]);
			currentIndex = 0;
			return this.add(e);
		}
	}


	@Override
	public Integer get(int index) {
		int[] currentArray = data.get(index / ARRAY_SIZE);
		int currentIndex = index % ARRAY_SIZE;
		return currentArray[currentIndex];
	}


	@Override
	public Integer set(int index, Integer element) {
		int[] currentArray = data.get(index / ARRAY_SIZE);
		int currentIndex = index % ARRAY_SIZE;
		currentArray[currentIndex] = element;
		return null;
	}


	@Override
	public int size() {
		int size = ((data.size() - 1) * ARRAY_SIZE) + currentIndex;
		return size;
	}
}
