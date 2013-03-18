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
package edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.sparse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;


/**
 * A {@link ListView} of {@link ScoredChromosomeWindow} optimized to minimize the memory usage
 * when most of the windows are not consecutive (ie: when they are separated by windows with a score of 0).
 * In this representation, the windows with a score of 0 are not stored.
 * {@link SparseSCWListView} objects are immutable.
 * @author Julien Lajugie
 */
public final class SparseSCWListView implements ListView<ScoredChromosomeWindow>, Iterator<ScoredChromosomeWindow> {

	/** Generated serial ID */
	private static final long serialVersionUID = -1875439465830656207L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Current index of the iterator */
	private transient int iteratorIndex = 0;

	/** List of the start positions of the SCWs */
	private final List<Integer> windowStarts;

	/** List of the stop positions of the SCWs */
	private final List<Integer> windowStops;

	/** List of the score values of the SCWs */
	private final List<Float> windowScores;


	/**
	 * Creates an instance of {@link SparseSCWListView}
	 * @param windowStarts list of the start positions of the SCWs
	 * @param windowStops list of the stop positions of the SCWs
	 * @param windowScore list of the score values of the SCWs
	 */
	SparseSCWListView(List<Integer> windowStarts, List<Integer> windowStops, List<Float> windowScores) {
		super();
		this.windowStarts = windowStarts;
		this.windowStops = windowStops;
		this.windowScores = windowScores;
	}


	@Override
	public ScoredChromosomeWindow get(int elementIndex) {
		int start = windowStarts.get(elementIndex);
		int stop = windowStops.get(elementIndex);
		float score = windowScores.get(elementIndex);
		return new SimpleScoredChromosomeWindow(start, stop, score);
	}


	@Override
	public boolean hasNext() {
		return iteratorIndex < size();
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


	@Override
	public void remove() {}


	@Override
	public int size() {
		return windowStops.size();
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
