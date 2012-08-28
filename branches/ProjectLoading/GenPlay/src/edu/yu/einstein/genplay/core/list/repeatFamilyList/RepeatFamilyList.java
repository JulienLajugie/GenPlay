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
package edu.yu.einstein.genplay.core.list.repeatFamilyList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.RepeatFamily;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.DisplayableListOfLists;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;


/**
 * An organized list of repeat families that provides tools to fit the list to the screen.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class RepeatFamilyList extends DisplayableListOfLists<RepeatFamily, List<RepeatFamily>> implements Serializable {

	private static final long serialVersionUID = -7553643226353657650L; // generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	
	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
	}
	

	/**
	 * Creates an instance of {@link RepeatFamilyList}
	 * Generates an organize list of repeats from the data in parameter
	 * @param startList list of start position of the repeats organized by chromosome
	 * @param stopList	list of stop position of the repeats organized by chromosome
	 * @param familyNameList list of name of the repeats organized by chromosome
	 * @throws InvalidChromosomeException
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public RepeatFamilyList(final ChromosomeListOfLists<Integer> startList, 
			final ChromosomeListOfLists<Integer> stopList, 
			final ChromosomeListOfLists<String> familyNameList) throws InvalidChromosomeException, InterruptedException, ExecutionException {
		super();
		// retrieve the instance of the OperationPool
		final OperationPool op = OperationPool.getInstance();
		// list for the threads
		final Collection<Callable<List<RepeatFamily>>> threadList = new ArrayList<Callable<List<RepeatFamily>>>();		
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		for(final Chromosome currentChromosome : projectChromosome) {			

			Callable<List<RepeatFamily>> currentThread = new Callable<List<RepeatFamily>>() {	
				@Override
				public List<RepeatFamily> call() throws Exception {
					List<RepeatFamily> resultList = new ArrayList<RepeatFamily>();
					// Hashtable indexed by repeat family name
					Hashtable<String, Integer> indexTable = new Hashtable<String, Integer>();;
					for(int j = 0; j < startList.size(currentChromosome); j++) {
						SimpleChromosomeWindow currentRepeat = new SimpleChromosomeWindow(startList.get(currentChromosome, j), stopList.get(currentChromosome, j));
						String familyName = familyNameList.get(currentChromosome, j);
						// case when a chromosome doesn't have any data yet
						if (resultList.size() == 0) {
							resultList.add(new RepeatFamily(familyName));
							resultList.get(0).addRepeat(currentRepeat);
							indexTable.put(familyName, 0);
						} else {
							// search if the family already exist on the current chromosome
							Integer familyIndex = indexTable.get(familyName);
							// case we found the family
							if (familyIndex != null) {
								resultList.get(familyIndex).addRepeat(currentRepeat);
							} else { // we didn't find the family
								resultList.add(new RepeatFamily(familyName));
								int index = resultList.size() - 1;
								resultList.get(index).addRepeat(currentRepeat);
								indexTable.put(familyName, index);
							}
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};

			threadList.add(currentThread);
		}
		List<List<RepeatFamily>> result = null;
		// starts the pool
		result = op.startPool(threadList);
		// add the chromosome results
		if (result != null) {
			for (List<RepeatFamily> currentList: result) {
				add(currentList);
			}
		}
		// sort the RepeatFamilyList
		for (List<RepeatFamily> currentRepeatFamilyList : this) {
			Collections.sort(currentRepeatFamilyList);
			for (RepeatFamily currentRepeatFamily : currentRepeatFamilyList) {
				Collections.sort(currentRepeatFamily.getRepeatList());
			}
		}
	}


	/**
	 * Adapts the list of the {@link Chromosome} in parameter to the screen depending on the xfactor
	 */
	protected void fitToScreen() {
		List<RepeatFamily> currentChromosomeList;
		try {
			currentChromosomeList = get(fittedChromosome);
		} catch (InvalidChromosomeException e) {
			e.printStackTrace();
			fittedDataList = null;
			return;
		}

		if (fittedXRatio > 1) {
			fittedDataList = currentChromosomeList;
		} else {
			fittedDataList = new ArrayList<RepeatFamily>();
			for (RepeatFamily currentFamily : currentChromosomeList) {
				if (currentFamily.repeatCount() > 1) {
					RepeatFamily fittedFamily = new RepeatFamily(currentFamily.getName());
					fittedFamily.addRepeat(new SimpleChromosomeWindow(currentFamily.getRepeat(0)));
					int i = 1;
					int j = 0;
					while (i < currentFamily.repeatCount()) {
						double distance = (currentFamily.getRepeat(i).getStart() - fittedFamily.getRepeat(j).getStop()) * fittedXRatio;
						while ((distance < 1) && (i + 1 < currentFamily.repeatCount())) {
							int newStop = Math.max(fittedFamily.getRepeat(j).getStop(), currentFamily.getRepeat(i).getStop());
							fittedFamily.getRepeat(j).setStop(newStop);
							i++;
							distance = (currentFamily.getRepeat(i).getStart() - fittedFamily.getRepeat(j).getStop()) * fittedXRatio;
						}
						fittedFamily.addRepeat(new SimpleChromosomeWindow(currentFamily.getRepeat(i)));
						i++;
						j++;						
					}
					fittedDataList.add(fittedFamily);
				}
			}
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.  
	 * @param list List in which the search is performed.
	 * @param value Searched value.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a Repeat with a position start equals to value. 
	 * Index of the first Repeat with a start position superior to value if nothing found.
	 */
	private int findStart(ArrayList<SimpleChromosomeWindow> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getStart()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getStart()) {
			return findStart(list, value, indexStart + middle + 1, indexStop);
		} else {
			return findStart(list, value, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.  
	 * @param list List in which the search is performed.
	 * @param value Searched value.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a Repeat with a position stop equals to value. 
	 * Index of the first Repeat with a stop position superior to value if nothing found.
	 */
	private int findStop(ArrayList<SimpleChromosomeWindow> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getStop()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getStop()) {
			return findStop(list, value, indexStart + middle + 1, indexStop);
		} else {
			return findStop(list, value, indexStart, indexStart + middle);
		}
	}


	@Override
	protected List<RepeatFamily> getFittedData(int start, int stop) {
		if ((fittedDataList == null) || (fittedDataList.size() == 0)) {
			return null;
		}

		ArrayList<RepeatFamily> resultList = new ArrayList<RepeatFamily>();

		for (RepeatFamily currentFamily : fittedDataList) {
			int indexStart = findStart(currentFamily.getRepeatList(), start, 0, currentFamily.getRepeatList().size());
			int indexStop = findStop(currentFamily.getRepeatList(), stop, 0, currentFamily.getRepeatList().size());
			if ((indexStart > 0) && (currentFamily.getRepeatList().get(indexStart - 1).getStop() > start)) {
				indexStart--;
			}
			resultList.add(new RepeatFamily(currentFamily.getName()));
			for (int i = indexStart; i <= indexStop; i++) {
				if (i < currentFamily.repeatCount()) {
					resultList.get(resultList.size() - 1).addRepeat(currentFamily.getRepeat(i));
				}
			}			
		}
		return resultList;
	}
}
