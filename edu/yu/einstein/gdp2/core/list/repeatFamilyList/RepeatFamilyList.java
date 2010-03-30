/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.repeatFamilyList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.ChromosomeWindow;
import yu.einstein.gdp2.core.RepeatFamily;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.DisplayableListOfLists;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;
import yu.einstein.gdp2.util.ChromosomeManager;


/**
 * An organized list of repeat families that provides tools to fit the list to the screen.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class RepeatFamilyList extends DisplayableListOfLists<RepeatFamily, List<RepeatFamily>> implements Serializable {

	private static final long serialVersionUID = -7553643226353657650L; // generated ID


	/**
	 * Creates an instance of {@link RepeatFamilyList}
	 * Generates an organize list of repeats from the data in parameter
	 * @param chromosomeManager a {@link ChromosomeManager}
	 * @param startList list of start position of the repeats organized by chromosome
	 * @param stopList	list of stop position of the repeats organized by chromosome
	 * @param familyNameList list of name of the repeats organized by chromosome
	 * @throws ManagerDataNotLoadedException
	 * @throws InvalidChromosomeException
	 */
	public RepeatFamilyList(ChromosomeManager chromosomeManager, ChromosomeListOfLists<Integer> startList, 
			ChromosomeListOfLists<Integer> stopList, ChromosomeListOfLists<String> familyNameList) throws ManagerDataNotLoadedException, InvalidChromosomeException {
		super(chromosomeManager);
		for(short i = 0; i < startList.size(); i++) {
			add(new ArrayList<RepeatFamily>());
			Chromosome currentChromosome = chromosomeManager.getChromosome(i);
			// Hashtable indexed by repeat family name
			Hashtable<String, Integer> indexTable = new Hashtable<String, Integer>();;
			for(int j = 0; j < startList.size(i); j++) {
				ChromosomeWindow currentRepeat = new ChromosomeWindow(startList.get(i, j), stopList.get(i, j));
				String familyName = familyNameList.get(i, j);

				// case when a chromosome doesn't have any data yet
				if (size(currentChromosome) == 0) {
					add(currentChromosome, new RepeatFamily(familyName));
					get(currentChromosome, 0).addRepeat(currentRepeat);
					indexTable.put(familyName, 0);
				} else {
					// search if the family already exist on the current chromosome
					Integer familyIndex = indexTable.get(familyName);
					// case we found the family
					if (familyIndex != null) {
						get(currentChromosome, familyIndex).addRepeat(currentRepeat);
					} else { // we didn't find the family
						add(currentChromosome, new RepeatFamily(familyName));
						int index = size(currentChromosome) - 1;
						get(currentChromosome, index).addRepeat(currentRepeat);
						indexTable.put(familyName, index);
					}
				}
			}			
		}
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
		} catch (ManagerDataNotLoadedException e) {
			e.printStackTrace();
			fittedDataList = null;
			return;
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
					fittedFamily.addRepeat(new ChromosomeWindow(currentFamily.getRepeat(0)));
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
						fittedFamily.addRepeat(new ChromosomeWindow(currentFamily.getRepeat(i)));
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
	private int findStart(ArrayList<ChromosomeWindow> list, int value, int indexStart, int indexStop) {
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
	private int findStop(ArrayList<ChromosomeWindow> list, int value, int indexStart, int indexStop) {
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
