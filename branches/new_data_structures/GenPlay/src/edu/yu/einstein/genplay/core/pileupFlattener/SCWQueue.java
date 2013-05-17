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
package edu.yu.einstein.genplay.core.pileupFlattener;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.AbstractScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;

/**
 * Queue of {@link ScoredChromosomeWindow} objects
 * @author Julien Lajugie
 */
class SCWQueue extends AbstractList<ScoredChromosomeWindow> implements List<ScoredChromosomeWindow> {

	private class QueuedSCW extends AbstractScoredChromosomeWindow implements ScoredChromosomeWindow {

		/** Generated serial ID */
		private static final long serialVersionUID = 4435941282086283975L;

		/** Index of the window in the queue */
		private final int index;

		/**
		 * Creates an instance of {@link QueuedSCW}
		 * @param index index of the {@link ScoredChromosomeWindow} in the queue
		 */
		private QueuedSCW(int index) {
			this.index = index;
		}

		@Override
		public float getScore() {
			return windowScores.get(index);
		}

		@Override
		public int getStart() {
			return windowStarts.get(index);
		}

		@Override
		public int getStop() {
			return windowStops.get(index);
		}
	}

	/** List of the start positions of the SCWs */
	private final List<Integer> windowStarts;

	/** List of the stop positions of the SCWs */
	private final List<Integer> windowStops;

	/** List of the score values of the SCWs */
	private final List<Float> windowScores;


	/**
	 * Creates an instance of {@link SCWQueue}
	 */
	SCWQueue() {
		windowStarts = new ArrayList<Integer>();
		windowStops = new ArrayList<Integer>();
		windowScores = new ArrayList<Float>();
	}


	/**
	 * Adds a scored window element to the queue with the specified start, stop and score values
	 * @param start
	 * @param stop
	 * @param score
	 */
	void add(int start, int stop, float score) {
		windowStarts.add(start);
		windowStops.add(stop);
		windowScores.add(score);
	}


	/**
	 * {@inheritDoc}
	 * @return The {@link ScoredChromosomeWindow} at the specified index
	 */
	@Override
	public ScoredChromosomeWindow get(int index) {
		return new QueuedSCW(index);
	}


	/**
	 * {@inheritDoc}
	 * @return This method returns null for speed reason
	 */
	@Override
	public ScoredChromosomeWindow remove(int index) {
		windowStarts.remove(index);
		windowStops.remove(index);
		windowScores.remove(index);
		return null;
	}


	/**
	 * {@inheritDoc}
	 * @return The size of this queue
	 */
	@Override
	public int size() {
		return windowStarts.size();
	}
}
