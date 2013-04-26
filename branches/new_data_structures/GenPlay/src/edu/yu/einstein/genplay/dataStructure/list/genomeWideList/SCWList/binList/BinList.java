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
package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOComputeStats;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.bin.BinListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;


/**
 * The BinList class provides a representation of a list of genome positions grouped by bins.
 * A score is associated to every bin.
 * @author Julien Lajugie
 */
public final class BinList implements Serializable, SCWList, Iterator<ListView<ScoredChromosomeWindow>> {

	/** Generated serial ID */
	private static final long serialVersionUID = 4578520957543654075L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Value of the BinSize factors averaged BinList */
	public static final int[] AVERAGE_BIN_SIZE_FACTORS = {100, 1000};

	/** Size of the bins in bp */
	private final int binSize;

	/** {@link GenomicDataArrayList} containing the Genes */
	private final BinListView[] data;

	/** Current index of the iterator */
	private transient int iteratorIndex = 0;

	/** Smallest value of the list */
	private final float minimum;

	/** Greatest value of the list */
	private final float maximum;

	/** Average of the list */
	private final double average;

	/** Standard deviation of the list */
	private final double standardDeviation;

	/** Sum of the scores of all windows */
	private final double scoreSum;

	/** Count of none-null bins in the BinList */
	private final long nonNullLength;


	/**
	 * Creates an instance of {@link BinList}
	 * @param data list of {@link BinListView} organized by chromosome
	 * @param binSize size of the bins
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public BinList(List<BinListView> data, int binSize) throws InterruptedException, ExecutionException {
		super();
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		this.binSize = binSize;
		this.data = new BinListView[projectChromosome.size()];
		for (int i = 0; i < projectChromosome.size(); i++){
			if (i < data.size()) {
				this.data[i] = data.get(i);
			}
		}
		// computes some statistic values for this list
		SCWLOComputeStats operation = new SCWLOComputeStats(this);
		operation.compute();
		maximum = operation.getMaximum();
		minimum = operation.getMinimum();
		average = operation.getAverage();
		standardDeviation = operation.getStandardDeviation();
		nonNullLength = operation.getNonNullLength();
		scoreSum = operation.getScoreSum();
	}


	@Override
	public ListView<ScoredChromosomeWindow> get(Chromosome chromosome) throws InvalidChromosomeException {
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		int chromosomeIndex = projectChromosome.getIndex(chromosome);
		return get(chromosomeIndex);
	}


	@Override
	public ScoredChromosomeWindow get(Chromosome chromosome, int index) throws InvalidChromosomeException {
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		int chromosomeIndex = projectChromosome.getIndex(chromosome);
		return get(chromosomeIndex, index);
	}


	@Override
	public ListView<ScoredChromosomeWindow> get(int chromosomeIndex) {
		return data[chromosomeIndex];
	}


	@Override
	public ScoredChromosomeWindow get(int chromosomeIndex, int elementIndex) {
		return get(chromosomeIndex).get(elementIndex);
	}


	@Override
	public double getAverage() {
		return average;
	}


	/**
	 * @return the size of the bins
	 */
	public int getBinSize() {
		return binSize;
	}


	@Override
	public float getMaximum() {
		return maximum;
	}


	@Override
	public float getMinimum() {
		return minimum;
	}


	@Override
	public long getNonNullLength() {
		return nonNullLength;
	}


	@Override
	public float getScore(Chromosome chromosome, int position) {
		int binIndex = position / binSize;
		return get(chromosome).get(binIndex).getScore();
	}


	@Override
	public double getScoreSum() {
		return scoreSum;
	}


	@Override
	public SCWListType getSCWListType() {
		return SCWListType.BIN;
	}


	@Override
	public double getStandardDeviation() {
		return standardDeviation;
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
	public Iterator<ListView<ScoredChromosomeWindow>> iterator() {
		return this;
	}


	@Override
	public ListView<ScoredChromosomeWindow> next() {
		int currentIndex = iteratorIndex;
		iteratorIndex++;
		return get(currentIndex);
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
	public void remove() {
		throw new UnsupportedOperationException();
	}


	@Override
	public int size() {
		return data.length;
	}


	@Override
	public int size(Chromosome chromosome) throws InvalidChromosomeException {
		return get(chromosome).size();
	}


	@Override
	public int size(int chromosomeIndex) {
		return get(chromosomeIndex).size();
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
