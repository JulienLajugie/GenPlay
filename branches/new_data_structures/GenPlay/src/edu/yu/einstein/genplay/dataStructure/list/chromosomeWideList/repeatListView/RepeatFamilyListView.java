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

import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewIterator;
import edu.yu.einstein.genplay.dataStructure.list.listView.subListView.SubListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Representation of a family of repeats. A repeat family has a name and
 * a list of repeats. Repeats are {@link ChromosomeWindow} objects having a
 * start and a stop position.
 * {@link RepeatFamilyListView} objects are immutable.
 * @author Julien Lajugie
 */
public final class RepeatFamilyListView implements Serializable, ListView<ScoredChromosomeWindow> {

	/** generated ID */
	private static final long serialVersionUID = -7691967168795920365L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** List of the repeats */
	private final ListView<ScoredChromosomeWindow> repeatListView;

	/** Name of the family of repeat */
	private final String name;



	/**
	 * Creates an instance of {@link RepeatFamilyListView}
	 * @param name name of the family
	 * @param repeatListView ListView of the repeats
	 */
	public RepeatFamilyListView(String name, ListView<ScoredChromosomeWindow> repeatListView) {
		this.name = name;
		this.repeatListView = repeatListView;
	}


	@Override
	public ScoredChromosomeWindow get(int repeatIndex) {
		return repeatListView.get(repeatIndex);
	}


	/**
	 * @return the name of the family
	 */
	public String getName() {
		return name;
	}


	@Override
	public boolean isEmpty() {
		return size() == 0;
	}


	@Override
	public Iterator<ScoredChromosomeWindow> iterator() {
		return new ListViewIterator<ScoredChromosomeWindow>(this);
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
	public int size() {
		return repeatListView.size();
	}


	@Override
	public ListView<ScoredChromosomeWindow> subList(int fromIndex, int toIndex) {
		return new SubListView<ScoredChromosomeWindow>(this, fromIndex, toIndex);
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
	}
}
