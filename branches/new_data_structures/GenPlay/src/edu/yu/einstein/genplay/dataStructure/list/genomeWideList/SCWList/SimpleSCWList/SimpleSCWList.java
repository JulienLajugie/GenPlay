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
package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.comparator.ChromosomeWindowStartComparator;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOComputeStats;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOCountNonNullLength;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.dense.DenseSCWListView;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListView;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.mask.MaskListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.SimpleGeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.AbstractListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.ListView.ListViews;


/**
 * Class implementing the {@link SCWList} interface using an {@link ArrayList} based data structure
 * @author Julien Lajugie
 */
public final class SimpleSCWList extends AbstractListView<ListView<ScoredChromosomeWindow>> implements SCWList {

	/** Generated serial ID */
	private static final long serialVersionUID = 9159412940141151387L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/**
	 * @param scwListType a {@link SCWListType}
	 * @return the number of steps needed to create a list of the specified type
	 */
	public static int getCreationStepCount(SCWListType scwListType) {
		switch (scwListType) {
		case GENERIC:
			return 2;
		case MASK:
			return 1;
		case BIN:
			return BinList.getCreationStepCount(SCWListType.BIN);
		default:
			return 0;
		}
	}

	/** {@link GenomicDataArrayList} containing the ScoredChromosomeWindows */
	private final List<ListView<ScoredChromosomeWindow>> data;

	/** Type of the list */
	private final SCWListType scwListType;

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
	 * Creates an instance of {@link SimpleGeneList}
	 * @param data {@link ScoredChromosomeWindow} list organized by chromosome
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws InvalidParameterException if the {@link ListView} objects are not valid
	 */
	public SimpleSCWList(List<ListView<ScoredChromosomeWindow>> data) throws InterruptedException, ExecutionException, InvalidParameterException {
		super();
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		this.data = new ArrayList<ListView<ScoredChromosomeWindow>>(projectChromosomes.size());
		for (int i = 0; i < data.size(); i++){
			this.data.add(data.get(i));
		}
		// check if the listviews are valid and retrieve the type of the list
		scwListType = retrieveListType(data);
		// computes some statistic values for this list
		if (scwListType == SCWListType.MASK) {
			maximum = 1f;
			minimum = 1f;
			average = 1f;
			standardDeviation = 0f;
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
	public ListView<ScoredChromosomeWindow> get(Chromosome chromosome) throws InvalidChromosomeException {
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		int chromosomeIndex = projectChromosomes.getIndex(chromosome);
		return get(chromosomeIndex);
	}


	@Override
	public ScoredChromosomeWindow get(Chromosome chromosome, int index) throws InvalidChromosomeException {
		return get(chromosome).get(index);
	}


	@Override
	public ListView<ScoredChromosomeWindow> get(int elementIndex) {
		return 	data.get(elementIndex);
	}


	@Override
	public ScoredChromosomeWindow get(int chromosomeIndex, int elementIndex) {
		return get(chromosomeIndex).get(elementIndex);
	}


	@Override
	public double getAverage() {
		return average;
	}


	@Override
	public int getCreationStepCount() {
		return getCreationStepCount(scwListType);
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
		ListView<ScoredChromosomeWindow> currentList = get(chromosome);
		int indexWindow = ListViews.binarySearch(currentList, new SimpleChromosomeWindow(position, position), new ChromosomeWindowStartComparator());
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
	public double getScoreSum() {
		return scoreSum;
	}


	@Override
	public SCWListType getSCWListType() {
		return scwListType;
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
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		in.defaultReadObject();
	}


	/**
	 * @param data data
	 * @return the type of the {@link SimpleSCWList}
	 * @throws InvalidParameterException if the {@link ListView} objects are not valid
	 */
	private SCWListType retrieveListType(List<ListView<ScoredChromosomeWindow>> data) throws InvalidParameterException {
		if (data == null) {
			return null;
		}
		SCWListType listType = null;
		for (ListView<ScoredChromosomeWindow> currentLV: data) {
			SCWListType currentType;
			if (currentLV instanceof DenseSCWListView) {
				currentType = SCWListType.DENSE;
			} else if (currentLV instanceof GenericSCWListView) {
				currentType = SCWListType.GENERIC;
			} else if (currentLV instanceof MaskListView) {
				currentType = SCWListType.MASK;
			} else {
				throw new InvalidParameterException("Incompatible ListView objects");
			}
			if (listType == null) {
				listType = currentType;
			} else if (currentType != listType) {
				// listview elements need to implement the same class
				throw new InvalidParameterException("Non-consistent ListView objects");
			}
		}
		return listType;
	}


	@Override
	public int size() {
		return data.size();
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
