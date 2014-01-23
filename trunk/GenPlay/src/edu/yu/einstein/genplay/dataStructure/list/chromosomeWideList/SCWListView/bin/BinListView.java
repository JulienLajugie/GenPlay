/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.bin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.list.listView.AbstractListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.AbstractScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * A memory efficient {@link ListView} of bins with a fix size
 * {@link BinListView} objects are immutable.
 * @author Julien Lajugie
 */
public final class BinListView extends AbstractListView<ScoredChromosomeWindow> implements ListView<ScoredChromosomeWindow> {

	/**
	 * Implementation of the {@link ScoredChromosomeWindow} interface for windows retrieved from a {@link ListView}
	 * @author Julien Lajugie
	 */
	private class SCWFromListView extends AbstractScoredChromosomeWindow implements ScoredChromosomeWindow {

		/** Generated serial ID */
		private static final long serialVersionUID = -4636772386393755263L;

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
			return binScores.get(windowIndex);
		}


		@Override
		public int getStart() {
			return (windowIndex * binSize) + 1;
		}

		@Override
		public int getStop() {
			return ((windowIndex + 1) * binSize) + 1;
		}
	}


	/** Generated serial ID */
	private static final long serialVersionUID = 304172516803341537L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Size of the bins of the list */
	private final int binSize;

	/** List of the score values of the bins */
	private final List<Float> binScores;


	/**
	 * Creates an instance of {@link BinListView}
	 * @param binSize size of the bins of the list
	 * @param windowScore list of the score values of the bins
	 */
	BinListView(int binSize, List<Float> binScores) {
		super();
		this.binSize = binSize;
		this.binScores = binScores;
	}


	@Override
	public ScoredChromosomeWindow get(int elementIndex) {
		return new SCWFromListView(elementIndex);
	}


	/**
	 * @return the size of the bins of the {@link ListView}
	 */
	public int getBinSize() {
		return binSize;
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
		return binScores.size();
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
