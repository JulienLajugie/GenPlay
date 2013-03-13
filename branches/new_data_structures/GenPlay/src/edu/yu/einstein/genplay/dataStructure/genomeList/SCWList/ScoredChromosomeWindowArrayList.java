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
package edu.yu.einstein.genplay.dataStructure.genomeList.SCWList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.comparator.ChromosomeWindowStartComparator;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOComputeStats;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOCountNonNullLength;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.genomeList.GenomicDataArrayList;
import edu.yu.einstein.genplay.dataStructure.genomeList.GenomicDataList;
import edu.yu.einstein.genplay.dataStructure.genomeList.geneList.SimpleGeneList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;


/**
 * Class implementing the {@link ScoredChromosomeWindowList} interface using an {@link ArrayList} based data structure
 * @author Julien Lajugie
 * @version 0.1
 */
public class ScoredChromosomeWindowArrayList implements ScoredChromosomeWindowList {

	/** Generated serial ID */
	private static final long serialVersionUID = 62775243765033535L;

	/** Saved format version */
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;


	/**
	 * @param scwListType a {@link SCWListType}
	 * @return the number of steps needed to create the list.
	 */
	public static int getCreationStepCount(SCWListType scwListType) {
		switch (scwListType) {
		case GENERIC:
			return 5;
		case MASK:
			return 2;
		default:
			return 0;
		}
	}

	/** {@link GenomicDataArrayList} containing the ScoredChromosomeWindows */
	private GenomicDataList<ScoredChromosomeWindow> data;

	/** Type of the list */
	private SCWListType scwListType;

	/** Smallest value of the list */
	private double minimum;

	/** Greatest value of the list */
	private double maximum;

	/** Average of the list */
	private double average;

	/** Standard deviation of the list */
	private double standardDeviation;

	/** Sum of the scores of all windows */
	private double scoreSum;

	/** Count of none-null bins in the BinList */
	private long nonNullLength;


	/**
	 * Creates an instance of {@link SimpleGeneList}
	 * @param data {@link ScoredChromosomeWindow} list organized by chromosome
	 * @param scwListType type of the list (as a {@link SCWListType} element)
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public ScoredChromosomeWindowArrayList(List<List<Gene>> data, SCWListType scwListType) throws InterruptedException, ExecutionException {
		super();
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		for (int i = 0; i < projectChromosome.size(); i++){
			if (i < data.size()) {
				data.add(data.get(i));
			} else {
				// add an empty list
				data.add(new ArrayList<Gene>());
			}
		}
		this.scwListType = scwListType;
		sort();
		computeStatistics();
	}


	/**
	 * Computes some statistic values for this list
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	private void computeStatistics() throws InterruptedException, ExecutionException {
		if (scwListType == SCWListType.MASK) {
			maximum = 1d;
			minimum = 1d;
			average = 1d;
			standardDeviation = 0d;
			nonNullLength = new SCWLOCountNonNullLength(this, null).compute();
			scoreSum = nonNullLength;
		} else {
			SCWLOComputeStats operation = new SCWLOComputeStats(this);
			operation.compute();
			maximum = operation.getMaximum();
			minimum = operation.getMinimum();
			average = operation.getAverage();
			standardDeviation = operation.getStandardDeviation();
			nonNullLength = operation.getNonNullLength();
			scoreSum = operation.getScoreSum();
		}
	}


	@Override
	public double getAverage() {
		return average;
	}


	@Override
	public double getMaximum() {
		return maximum;
	}


	@Override
	public double getMinimum() {
		return minimum;
	}


	@Override
	public long getNonNullLength() {
		return nonNullLength;
	}


	@Override
	public double getScore(Chromosome chromosome, int position) {
		List<ScoredChromosomeWindow> currentList = getView(chromosome);
		int indexWindow = Collections.binarySearch(currentList, new SimpleChromosomeWindow(position, position), new ChromosomeWindowStartComparator());
		if (indexWindow < 0) {
			// retrieve the window right before the insert point
			indexWindow = -indexWindow - 2;
			if (indexWindow < 0) {
				return 0;
			}
		}
		// check if the window contains the stop position
		if (currentList.get(indexWindow).getStop() >= position) {
			return currentList.get(indexWindow).getScore();
		}
		return 0;
	}


	@Override
	public SCWListType getSCWListType() {
		return scwListType;
	}


	@Override
	public double getScoreSum() {
		return scoreSum;
	}


	@Override
	public double getStandardDeviation() {
		return standardDeviation;
	}


	/**
	 * Method used for unserialization. Computes the statistics of the list after unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		data = (GenomicDataList<ScoredChromosomeWindow>) in.readObject();
		scwListType = (SCWListType) in.readObject();
		maximum = in.readDouble();
		minimum = in.readDouble();
		average = in.readDouble();
		standardDeviation = in.readDouble();
		nonNullLength = in.readLong();
		scoreSum = in.readDouble();
	}


	/**
	 * Sorts the elements of the {@link ScoredChromosomeWindowList} by position
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void sort() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		for (final List<ScoredChromosomeWindow> currentList: this) {
			Callable<Void> currentThread = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					if (currentList != null) {
						Collections.sort(currentList);
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return null;
				}
			};
			threadList.add(currentThread);
		}
		op.startPool(threadList);
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(data);
		out.writeObject(scwListType);
		out.writeDouble(maximum);
		out.writeDouble(minimum);
		out.writeDouble(average);
		out.writeDouble(standardDeviation);
		out.writeLong(nonNullLength);
		out.writeDouble(scoreSum);
	}


	@Override
	public ScoredChromosomeWindow get(Chromosome chromosome, int index) throws InvalidChromosomeException {
		return data.get(chromosome, index);
	}


	@Override
	public ScoredChromosomeWindow get(int chromosomeIndex, int elementIndex) {
		return data.get(chromosomeIndex, elementIndex);
	}


	@Override
	public List<ScoredChromosomeWindow> getView(Chromosome chromosome) throws InvalidChromosomeException {
		return data.getView(chromosome);
	}


	@Override
	public List<ScoredChromosomeWindow> getView(int chromosomeIndex) {
		return data.getView(chromosomeIndex);
	}


	@Override
	public int size() {
		return data.size();
	}


	@Override
	public int size(Chromosome chromosome) throws InvalidChromosomeException {
		return data.size(chromosome);
	}


	@Override
	public int size(int chromosomeIndex) {
		return data.size(chromosomeIndex);
	}


	@Override
	public Iterator<List<ScoredChromosomeWindow>> iterator() {
		return data.iterator();
	}
}
