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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.SNPList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.SNP;
import edu.yu.einstein.genplay.core.enums.Nucleotide;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.DisplayableListOfLists;
import edu.yu.einstein.genplay.core.manager.ChromosomeManager;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;



/**
 * A list of SNPs
 * @author Julien Lajugie
 * @version 0.1
 */
public class SNPList extends DisplayableListOfLists<SNP, List<SNP>> implements Serializable {

	private static final long serialVersionUID = 5581991460611158113L;	// generated ID


	/**
	 * Creates an instance of {@link SNPList} containing the specified data.
	 * @param data data of the list 
	 */
	public SNPList(Collection<? extends List<SNP>> data) {
		addAll(data);
		ChromosomeManager chromosomeManager = ChromosomeManager.getInstance();
		// add the eventual missing chromosomes
		if (size() < chromosomeManager.size()) {
			for (int i = size(); i < chromosomeManager.size(); i++){
				add(null);
			}
		}
		// sort the data
		sort();
	}


	/**
	 * Creates an instance of {@link SNPList}
	 * @param nameList list of name; null if the SNPs are not named
	 * @param positionList list of position
	 * @param firstBaseList list of first base
	 * @param firstBaseCountList list of first base count
	 * @param secondBaseList list of second base
	 * @param secondBaseCountList list of second base count
	 * @param isSecondBaseSignificantList list of boolean
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public SNPList(final ChromosomeListOfLists<String> nameList,
			final ChromosomeListOfLists<Integer> positionList,
			final ChromosomeListOfLists<Nucleotide> firstBaseList,
			final ChromosomeListOfLists<Integer> firstBaseCountList,
			final ChromosomeListOfLists<Nucleotide> secondBaseList,
			final ChromosomeListOfLists<Integer> secondBaseCountList,
			final ChromosomeListOfLists<Boolean> isSecondBaseSignificantList) 
	throws InterruptedException, ExecutionException {
		super();
		// retrieve the instance of the OperationPool
		final OperationPool op = OperationPool.getInstance();
		// list for the threads
		final Collection<Callable<List<SNP>>> threadList = new ArrayList<Callable<List<SNP>>>();		
		ChromosomeManager chromosomeManager = ChromosomeManager.getInstance();
		for(final Chromosome currentChromosome : chromosomeManager) {			
			Callable<List<SNP>> currentThread = new Callable<List<SNP>>() {	
				@Override
				public List<SNP> call() throws Exception {
					List<SNP> resultList = new ArrayList<SNP>();
					for(int j = 0; j < positionList.size(currentChromosome); j++) {
						int position = positionList.get(currentChromosome, j);
						Nucleotide firstBase = firstBaseList.get(currentChromosome, j);
						int firstBaseCount = firstBaseCountList.get(currentChromosome, j);
						Nucleotide secondBase = secondBaseList.get(currentChromosome, j);
						int secondBaseCount = secondBaseCountList.get(currentChromosome, j);
						boolean isSecondBaseSignificant = isSecondBaseSignificantList.get(currentChromosome, j);						
						String name = null;
						if (nameList != null) {
							name = nameList.get(currentChromosome, j);;
						}
						resultList.add(new SNP(name, position, firstBase, firstBaseCount, secondBase, secondBaseCount, isSecondBaseSignificant));
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};

			threadList.add(currentThread);
		}		
		List<List<SNP>> result = null;
		// starts the pool
		result = op.startPool(threadList);
		// add the chromosome results
		if (result != null) {
			for (List<SNP> currentList: result) {
				add(currentList);
			}
		}			
		// sort the SNP list	
		sort();
	}


	/**
	 * Does nothing
	 */
	@Override
	protected void fitToScreen() {}


	/**
	 * @param genomeWindow a {@link GenomeWindow}
	 * @return a list with all the SNPs in the specified GenomeWindow
	 */
	public List<SNP> get(GenomeWindow genomeWindow) {
		List<SNP> result = new ArrayList<SNP>();
		List<SNP> currentList;
		try {
			currentList = get(genomeWindow.getChromosome());
		} catch (InvalidChromosomeException e) {
			return null;
		}
		int indexStart = findSNP(currentList, genomeWindow.getStart(), 0, currentList.size() - 1);
		int indexStop = findSNP(currentList, genomeWindow.getStop(), 0, currentList.size() - 1) - 1;
		for (int i = indexStart; i <= indexStop; i++) {
			result.add(currentList.get(i));
		}		
		return result;
	}



	@Override
	protected List<SNP> getFittedData(int start, int stop) {
		List<SNP> result = new ArrayList<SNP>();
		List<SNP> currentList;
		try {
			currentList = get(fittedChromosome);
		} catch (InvalidChromosomeException e) {
			e.printStackTrace();
			fittedDataList = null;
			return null;
		}
		if ((currentList != null) && (!currentList.isEmpty())) {
			int indexStart = findSNP(currentList, start, 0, currentList.size() - 1);
			int indexStop = findSNP(currentList, stop, 0, currentList.size() - 1) - 1;
			for (int i = indexStart; i <= indexStop; i++) {
				result.add(currentList.get(i));
			}		
			return result;
		} else {
			return null;
		}
	}


	/**
	 * Sorts the SNPList
	 */
	public void sort() {
		for (List<SNP> currentList: this) {
			if (currentList != null) {
				Collections.sort(currentList);
			}
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.  
	 * @param list List in which the search is performed.
	 * @param value Searched value
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position start equals to value. 
	 * Index of the first gene with a start position superior to value if nothing found.
	 */
	public static int findSNP(List<SNP> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getPosition()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getPosition()) {
			return findSNP(list, value, indexStart + middle + 1, indexStop);
		} else {
			return findSNP(list, value, indexStart, indexStart + middle);
		}
	}


	/**
	 * Performs a deep clone of the current SNPList
	 * @return a new SNPList
	 */
	public SNPList deepClone() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ((SNPList) ois.readObject());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
