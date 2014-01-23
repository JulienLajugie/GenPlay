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
package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.binList.BLOComputeAverageList;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.bin.BinListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListStats.SCWListStats;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.listView.AbstractListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;


/**
 * The BinList class provides a representation of a list of genome positions grouped by bins.
 * A score is associated to every bin.
 * @author Julien Lajugie
 */
public final class BinList extends AbstractListView<ListView<ScoredChromosomeWindow>> implements Serializable, SCWList {

	/** Generated serial ID */
	private static final long serialVersionUID = 4578520957543654075L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Value of the BinSize factors averaged BinList */
	public static final int[] AVERAGE_BIN_SIZE_FACTORS = {100, 10000};

	/**
	 * @param scwListType a {@link SCWListType}
	 * @return the number of steps needed to create a list of the specified type
	 */
	public static int getCreationStepCount(SCWListType scwListType) {
		if (scwListType == SCWListType.BIN) {
			return AVERAGE_BIN_SIZE_FACTORS.length + 2;
		} else {
			return SimpleSCWList.getCreationStepCount(scwListType);
		}
	}

	/** List of list of bins with bin sizes equal to {@link #binSize} * {@link #AVERAGE_BIN_SIZE_FACTORS} */
	private final List<List<ListView<ScoredChromosomeWindow>>> averagedList;

	/** Size of the bins in bp */
	private final int binSize;

	/** {@link GenomicDataArrayList} containing the Genes */
	private final BinListView[] data;

	/** Statistics of the list */
	private final SCWListStats listStats;


	/**
	 * Creates an instance of {@link BinList}
	 * @param data list of {@link BinListView} organized by chromosome
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws CloneNotSupportedException
	 * @throws InvalidParameterException
	 */
	public BinList(List<ListView<ScoredChromosomeWindow>> data) throws InterruptedException, ExecutionException, CloneNotSupportedException, InvalidParameterException {
		super();
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		this.data = new BinListView[projectChromosomes.size()];
		for (int i = 0; i < projectChromosomes.size(); i++){
			if (i < data.size()) {
				this.data[i] = (BinListView) data.get(i);
			}
		}
		binSize = retrieveBinSize();
		// computes some statistic values for this list
		listStats = new SCWListStats(this);
		averagedList = new ArrayList<List<ListView<ScoredChromosomeWindow>>>();
		for (int currentFactor: AVERAGE_BIN_SIZE_FACTORS) {
			averagedList.add(new BLOComputeAverageList(this, currentFactor).compute());
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
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		int chromosomeIndex = projectChromosomes.getIndex(chromosome);
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


	/**
	 * @param index
	 * @return The averaged list of bins with a bin size equals to ({@link BinList#getBinSize()} * {@link #AVERAGE_BIN_SIZE_FACTORS}[index])
	 * where index is the specified parameter
	 */
	public List<ListView<ScoredChromosomeWindow>> getAveragedList(int index) {
		return averagedList.get(index);
	}


	/**
	 * @return the size of the bins
	 */
	public int getBinSize() {
		return binSize;
	}


	@Override
	public int getCreationStepCount() {
		return getCreationStepCount(SCWListType.BIN);
	}


	@Override
	public float getScore(Chromosome chromosome, int position) {
		int binIndex = (position - 1) / binSize;
		if (binIndex < size(chromosome)) {
			return get(chromosome).get(binIndex).getScore();
		} else {
			return 0f;
		}
	}


	@Override
	public SCWListType getSCWListType() {
		return SCWListType.BIN;
	}


	@Override
	public SCWListStats getStatistics() {
		return listStats;
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
	 * @return the size of the bins of the data
	 * @throws InvalidParameterException If the {@link BinListView} have different window sizes
	 */
	private int retrieveBinSize() throws InvalidParameterException {
		if (data == null) {
			return -1;
		}
		Integer binSize = null;
		for (BinListView currentBLV: data) {
			int currentBinSize = currentBLV.getBinSize();
			if (binSize == null) {
				binSize = currentBinSize;
			} else if (binSize != currentBinSize) {
				// listview elements need to have the same bin size
				throw new InvalidParameterException("Non-consistent ListView bin sizes");
			}
		}
		return binSize;
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
