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
package edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.repeatListView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;


/**
 * Representation of a family of repeats. A repeat family has a name and
 * a list of repeats. Repeats are {@link ChromosomeWindow} objects having a
 * start and a stop position.
 * {@link RepeatFamilyListView} objects are immutable.
 * @author Julien Lajugie
 */
public final class RepeatFamilyListView implements Serializable, ListView<ScoredChromosomeWindow>, Iterator<ScoredChromosomeWindow> {

	/** generated ID */
	private static final long serialVersionUID = -7691967168795920365L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Current index of the iterator */
	private transient int iteratorIndex = 0;

	/** List of the start positions of the repeats */
	private final List<Integer> repeatStarts;

	/** List of the stop positions of the repeats */
	private final List<Integer> repeatStops;

	/** Name of the family of repeat */
	private final String name;


	/**
	 * Creates an instance of {@link RepeatFamilyListView}
	 * @param name name of the family
	 * @param repeatList {@link ListView} of {@link ChromosomeWindow} with the start and stop position of the repeats
	 */
	RepeatFamilyListView(String name, List<Integer> repeatStarts, List<Integer> repeatStops) {
		this.name = name;
		this.repeatStarts = repeatStarts;
		this.repeatStops = repeatStops;
	}


	@Override
	public ScoredChromosomeWindow get(int repeatIndex) {
		return new SimpleScoredChromosomeWindow(repeatStarts.get(repeatIndex), repeatStops.get(repeatIndex), 1f);
	}


	/**
	 * @return the name of the family
	 */
	public String getName() {
		return name;
	}


	@Override
	public boolean hasNext() {
		return iteratorIndex < size();
	}


	@Override
	public boolean isEmpty() {
		return size() == 0;
	}


	@Override
	public Iterator<ScoredChromosomeWindow> iterator() {
		return this;
	}


	@Override
	public ScoredChromosomeWindow next() {
		int currentIndex = iteratorIndex;
		iteratorIndex++;
		return get(currentIndex);
	}


	/**
	 * Prints the name and the repeats of the {@link RepeatFamilyListView}
	 */
	public void print() {
		String info = "";
		info += "Family name: " + name + "\n";
		info += "Number of repeats: " + size() + "\n";
		for (ChromosomeWindow repeat: this) {
			info += "(" + repeat.getStart() + ", " + repeat.getStop() + ") ";
		}
		System.out.println(info);
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		// read the version number of the object
		in.readInt();
		// read the final fields
		in.defaultReadObject();
	}


	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}


	@Override
	public int size() {
		return repeatStarts.size();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		// write the format version number of the object
		out.writeInt(CLASS_VERSION_NUMBER);
		// write the final fields
		out.defaultWriteObject();
		// reinitialize the index of the iterator
		iteratorIndex = 0;
	}
}
