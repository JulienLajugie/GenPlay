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
package edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.repeatFamily;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;


/**
 * Representation of a family of repeats. A repeat family has a name and
 * a list of repeats. Repeats are {@link ChromosomeWindow} objects having a
 * start and a stop position.
 * {@link RepeatFamily} objects are immutable.
 * @author Julien Lajugie
 */
public final class RepeatFamily implements Serializable, ListView<ChromosomeWindow>, Iterator<ChromosomeWindow> {

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
	 * Creates an instance of {@link RepeatFamily}
	 * @param name name of the family
	 * @param repeatList {@link ListView} of {@link ChromosomeWindow} with the start and stop position of the repeats
	 */
	RepeatFamily(String name, List<Integer> repeatStarts, List<Integer> repeatStops) {
		this.name = name;
		this.repeatStarts = repeatStarts;
		this.repeatStops = repeatStops;
	}


	/**
	 * @return the name of the family
	 */
	public String getName() {
		return name;
	}


	/**
	 * Prints the name and the repeats of the {@link RepeatFamily}
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


	@Override
	public Iterator<ChromosomeWindow> iterator() {
		return this;
	}


	@Override
	public int size() {
		return repeatStarts.size();
	}


	@Override
	public ChromosomeWindow get(int repeatIndex) {
		return new SimpleChromosomeWindow(repeatStarts.get(repeatIndex), repeatStops.get(repeatIndex));
	}


	@Override
	public boolean hasNext() {
		return (iteratorIndex + 1) < size();
	}


	@Override
	public ChromosomeWindow next() {
		iteratorIndex++;
		return get(iteratorIndex);
	}


	@Override
	public void remove() {}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		// read the final fields
		in.defaultReadObject();
		// read the version number of the object
		in.readInt();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		// write the final fields
		out.defaultWriteObject();
		// write the format version number of the object
		out.writeInt(CLASS_VERSION_NUMBER);
		// reinitialize the index of the iterator
		iteratorIndex = 0;
	}
}
