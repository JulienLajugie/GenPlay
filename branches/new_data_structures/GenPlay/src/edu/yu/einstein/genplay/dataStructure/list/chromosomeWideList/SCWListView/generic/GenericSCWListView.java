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
package edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.list.listView.AbstractListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.AbstractScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * A generic {@link ListView} of {@link ScoredChromosomeWindow} optimized to minimize the memory usage
 * when most of the windows are not consecutive (ie: when they are separated by windows with a score of 0).
 * In this representation, the windows with a score of 0 are not stored.
 * {@link GenericSCWListView} objects are immutable.
 * @author Julien Lajugie
 */
public final class GenericSCWListView extends AbstractListView<ScoredChromosomeWindow> implements ListView<ScoredChromosomeWindow> {

	/**
	 * Implementation of the {@link ScoredChromosomeWindow} interface for windows retrieved from a {@link ListView}
	 * @author Julien Lajugie
	 */
	private class SCWFromListView extends AbstractScoredChromosomeWindow implements ScoredChromosomeWindow {

		/** Generated serial ID */
		private static final long serialVersionUID = -4689411462110094080L;

		/**  Index of the window in the parent {@link ListView} */
		private final int windowIndex;


		/**
		 * Creates an instance of {@link SCWFromListView}
		 * @param windowIndex index of the window in the parent {@link ListView}
		 */
		private SCWFromListView(int windowIndex) {
			this.windowIndex = windowIndex;
		}


		@Override
		public float getScore() {
			return windowScores.get(windowIndex);
		}


		@Override
		public int getStart() {
			return windowStarts.get(windowIndex);
		}

		@Override
		public int getStop() {
			return windowStops.get(windowIndex);
		}
	}


	/** Generated serial ID */
	private static final long serialVersionUID = -1875439465830656207L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** List of the start positions of the SCWs */
	private final List<Integer> windowStarts;

	/** List of the stop positions of the SCWs */
	private final List<Integer> windowStops;

	/** List of the score values of the SCWs */
	private final List<Float> windowScores;


	/**
	 * Creates an instance of {@link GenericSCWListView}
	 * @param windowStarts list of the start positions of the SCWs
	 * @param windowStops list of the stop positions of the SCWs
	 * @param windowScore list of the score values of the SCWs
	 */
	GenericSCWListView(List<Integer> windowStarts, List<Integer> windowStops, List<Float> windowScores) {
		super();
		this.windowStarts = windowStarts;
		this.windowStops = windowStops;
		this.windowScores = windowScores;
	}


	@Override
	public ScoredChromosomeWindow get(int elementIndex) {
		return new SCWFromListView(elementIndex);
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
		return windowStops.size();
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
