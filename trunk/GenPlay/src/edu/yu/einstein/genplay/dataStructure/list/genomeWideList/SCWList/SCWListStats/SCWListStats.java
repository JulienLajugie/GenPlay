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
package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListStats;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOComputeStats;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;


/**
 * Statistics of a {@link SCWList}.
 * @author Julien Lajugie
 */
public class SCWListStats implements Serializable, Stoppable {

	/** Generated serial ID */
	private static final long serialVersionUID = -3203945684171788408L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Operation computing the statistics */
	private transient SCWLOComputeStats statOperation;

	/** Smallest value of the list */
	private final float minimum;

	/** Smallest values of each chromosome */
	private final float[] minimums;

	/** Greatest value of the list */
	private final float maximum;

	/** Greatest value of each chromosome */
	private final float[] maximums;

	/** Average of the list */
	private final double average;

	/** Averages of each chromosome */
	private final double[] averages;

	/** Standard deviation of the list */
	private final double standardDeviation;

	/** Standard deviation of each chromosome */
	private final double[] standardDeviations;

	/** Number of windows with a score different from 0 */
	private final long windowCount;

	/** Number of windows with a score different from 0 for each chromosome*/
	private final long[] windowCounts;

	/** Sum of the length of the windows with a score different from 0 */
	private final long windowLength;

	/** Sum of the length of the windows with a score different from 0 for each chromosome */
	private final long[] windowLengths;

	/** Sum of Float scores of all windows */
	private final double scoreSum;

	/** Sum of Float scores of all windows for each chromosome */
	private final double[] scoreSums;


	/**
	 * Creates an instance of {@link SCWListStats}.
	 * @param scwList a {@link SCWList}
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public SCWListStats(SCWList scwList) throws InterruptedException, ExecutionException {
		statOperation = new SCWLOComputeStats(scwList);
		statOperation.compute();
		minimum = statOperation.getMinimum();
		minimums = statOperation.getMinimums();
		maximum = statOperation.getMaximum();
		maximums = statOperation.getMaximums();
		average = statOperation.getAverage();
		averages = statOperation.getAverages();
		standardDeviation = statOperation.getStandardDeviation();
		standardDeviations = statOperation.getStandardDeviations();
		windowCount = statOperation.getWindowCount();
		windowCounts = statOperation.getWindowCounts();
		windowLength = statOperation.getWindowLength();
		windowLengths = statOperation.getWindowLengths();
		scoreSum = statOperation.getScoreSum();
		scoreSums = statOperation.getScoreSums();
		statOperation = null;
	}


	/**
	 * @return the average
	 */
	public double getAverage() {
		return average;
	}


	/**
	 * @return the averages
	 */
	public double[] getAverages() {
		return averages;
	}


	/**
	 * @return the maximum
	 */
	public float getMaximum() {
		return maximum;
	}


	/**
	 * @return the maximums
	 */
	public float[] getMaximums() {
		return maximums;
	}


	/**
	 * @return the minimum
	 */
	public float getMinimum() {
		return minimum;
	}


	/**
	 * @return the minimums
	 */
	public float[] getMinimums() {
		return minimums;
	}


	/**
	 * @return the scoreSum
	 */
	public double getScoreSum() {
		return scoreSum;
	}


	/**
	 * @return the scoreSums
	 */
	public double[] getScoreSums() {
		return scoreSums;
	}


	/**
	 * @return the standardDeviation
	 */
	public double getStandardDeviation() {
		return standardDeviation;
	}


	/**
	 * @return the standardDeviations
	 */
	public double[] getStandardDeviations() {
		return standardDeviations;
	}


	/**
	 * @return the statOperation
	 */
	public SCWLOComputeStats getStatOperation() {
		return statOperation;
	}


	/**
	 * @return the windowCount
	 */
	public long getWindowCount() {
		return windowCount;
	}


	/**
	 * @return the windowCounts
	 */
	public long[] getWindowCounts() {
		return windowCounts;
	}


	/**
	 * @return the windowLength
	 */
	public long getWindowLength() {
		return windowLength;
	}


	/**
	 * @return the windowLengths
	 */
	public long[] getWindowLengths() {
		return windowLengths;
	}


	/**
	 * Method used for unserialization. Computes the statistics of the list after unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		in.defaultReadObject();
	}


	@Override
	public void stop() {
		if (statOperation != null) {
			statOperation.stop();
		}
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(CLASS_VERSION_NUMBER);
		out.defaultWriteObject();
	}
}
