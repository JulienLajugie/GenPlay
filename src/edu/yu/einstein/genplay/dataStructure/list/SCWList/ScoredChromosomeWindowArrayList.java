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
package edu.yu.einstein.genplay.dataStructure.list.SCWList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOComputeStats;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOCountNonNullLength;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.list.GenomicDataArrayList;
import edu.yu.einstein.genplay.dataStructure.list.geneList.GeneArrayList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.ExceptionManager;


/**
 * Class implementing the {@link ScoredChromosomeWindowList} interface using an {@link ArrayList} based data structure
 * @author Julien Lajugie
 * @version 0.1
 */
public class ScoredChromosomeWindowArrayList extends GenomicDataArrayList<ScoredChromosomeWindow> implements ScoredChromosomeWindowList {

	/** Generated serial ID */
	private static final long serialVersionUID = 62775243765033535L;

	/** Saved format version */
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;

	/** Type of the list */
	private SCWListType scwListType;

	/*
	 * The following values are statistic values of the list
	 * They are transient because they depend on the chromosome manager that is also transient
	 * They are calculated at the creation of the list to avoid being recalculated
	 */

	/** Smallest value of the list */
	transient private Double 	minimum = null;

	/** Greatest value of the list */
	transient private Double 	maximum = null;

	/** Average of the list */
	transient private Double 	average = null;

	/** Standard deviation of the list */
	transient private Double 	standardDeviation = null;

	/** Sum of the scores of all windows */
	transient private Double	scoreSum = null;

	/** Count of none-null bins in the BinList */
	transient private Long 		nonNullLength = null;


	/**
	 * Creates an instance of {@link GeneArrayList}
	 * @param scwListType type of the list (as a {@link SCWListType} element)
	 */
	protected ScoredChromosomeWindowArrayList(SCWListType scwListType) {
		super();
		this.scwListType = scwListType;
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		for (int i = 0; i < projectChromosome.size(); i++){
			add(new ArrayList<ScoredChromosomeWindow>());
		}
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
			scoreSum = (double) nonNullLength;
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


	/**
	 * Performs a deep clone of the current {@link SimpleScoredChromosomeWindowList}
	 * @return a new ScoredChromosomeWindowList
	 */
	@Override
	public ScoredChromosomeWindowArrayList deepClone() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ((ScoredChromosomeWindowArrayList)ois.readObject());
		} catch (Exception e) {
			ExceptionManager.getInstance().caughtException(e);
			return null;
		}
	}


	@Override
	public Double getAverage() {
		return average;
	}


	@Override
	public Double getMaximum() {
		return maximum;
	}


	@Override
	public Double getMinimum() {
		return minimum;
	}


	@Override
	public Long getNonNullLength() {
		return nonNullLength;
	}


	@Override
	public SCWListType getScoredChromosomeWindowListType() {
		return scwListType;
	}


	@Override
	public Double getScoreSum() {
		return scoreSum;
	}


	@Override
	public Double getStandardDeviation() {
		return standardDeviation;
	}


	/**
	 * Method used for unserialization. Computes the statistics of the list after unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		scwListType = (SCWListType) in.readObject();
		try {
			computeStatistics();
		} catch (Exception e) {
			ExceptionManager.getInstance().caughtException(e);
		}
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(scwListType);
	}


	@Override
	public double getScore(Chromosome chromosome, int position) {
		/*List<ScoredChromosomeWindow> currentList = get(chromosome);
		int indexWindow = Collections.binarySearch(currentList, position, new ChromosomeWindowStartComparator());
		if (scwListType == SCWListType.MASK) {
			//ret
		}*/
		return 0;
	}
}
