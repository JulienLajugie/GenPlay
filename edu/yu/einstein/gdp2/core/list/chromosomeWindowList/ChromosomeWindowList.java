/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.chromosomeWindowList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.ChromosomeWindow;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.DisplayableListOfLists;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;
import yu.einstein.gdp2.util.ChromosomeManager;


/**
 * A list of {@link ChromosomeWindow} with tool to rescale it
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ChromosomeWindowList extends DisplayableListOfLists<ChromosomeWindow, List<ChromosomeWindow>> implements Serializable {

	private static final long serialVersionUID = -2180037918131928807L; // generated ID

	
	/**
	 * Creates an instance of {@link ChromosomeWindowList}
	 * @param chromosomeManager a {@link ChromosomeManager}
	 * @param startList list of start positions
	 * @param stopList list of stop positions
	 * @throws ManagerDataNotLoadedException
	 * @throws InvalidChromosomeException
	 */
	public ChromosomeWindowList(ChromosomeManager chromosomeManager, ChromosomeListOfLists<Integer> startList, 
			ChromosomeListOfLists<Integer> stopList) throws ManagerDataNotLoadedException, InvalidChromosomeException {
		super(chromosomeManager);
		for(short i = 0; i < startList.size(); i++) {
			add(new ArrayList<ChromosomeWindow>());
			Chromosome chromo = chromosomeManager.getChromosome(i);
			for(int j = 0; j < startList.size(i); j++) {
				int start = startList.get(i).get(j);
				int stop = stopList.get(i).get(j);
				add(chromo, new ChromosomeWindow(start, stop));
			}
		}
		for (List<ChromosomeWindow> currentChrWindowList : this) {
			Collections.sort(currentChrWindowList);
		}
	}

	
	/**
	 * Merges two windows together if the gap between this two windows is not visible 
	 */
	@Override
	protected void fitToScreen() {
		List<ChromosomeWindow> currentChromosomeList;
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
			fittedDataList = new ArrayList<ChromosomeWindow>();			
			if (currentChromosomeList.size() > 1) {
				fittedDataList.add(new ChromosomeWindow(currentChromosomeList.get(0)));
				int i = 1;
				int j = 0;
				while (i < currentChromosomeList.size()) {
					double distance = (currentChromosomeList.get(i).getStart() - fittedDataList.get(j).getStop()) * fittedXRatio;
					// we merge two intervals together if there is a gap smaller than 1 pixel
					while ((distance < 1) && (i + 1 < currentChromosomeList.size())) {
						// the new stop position is the max of the current stop and the stop of the new merged interval
						int newStop = Math.max(fittedDataList.get(j).getStop(), currentChromosomeList.get(i).getStop());
						fittedDataList.get(j).setStop(newStop);
						i++;
						distance = (currentChromosomeList.get(i).getStart() - fittedDataList.get(j).getStop()) * fittedXRatio;
					}
					fittedDataList.add(new ChromosomeWindow(currentChromosomeList.get(i)));
					i++;
					j++;						
				}
			}
		}
	}
	
	
	@Override
	protected List<ChromosomeWindow> getFittedData(int start, int stop) {
		if ((fittedDataList == null) || (fittedDataList.size() == 0)) {
			return null;
		}
		
		ArrayList<ChromosomeWindow> resultList = new ArrayList<ChromosomeWindow>();

		int indexStart = findStart(fittedDataList, start, 0, fittedDataList.size() - 1);
		int indexStop = findStop(fittedDataList, stop, 0, fittedDataList.size() - 1);
		if (indexStart > 0) {
			if (fittedDataList.get(indexStart - 1).getStop() >= start) {
				ChromosomeWindow currentWindow = fittedDataList.get(indexStart - 1); 
				ChromosomeWindow newLastWindow = new ChromosomeWindow(start, currentWindow.getStop());
				resultList.add(newLastWindow);
			}
		}
		for (int i = indexStart; i <= indexStop; i++) {
			resultList.add(fittedDataList.get(i));
		}
		if (indexStop + 1 < fittedDataList.size()) {
			if (fittedDataList.get(indexStop + 1).getStart() <= stop) {
				ChromosomeWindow currentWindow = fittedDataList.get(indexStop + 1); 
				ChromosomeWindow newLastWindow = new ChromosomeWindow(currentWindow.getStart(), stop);
				resultList.add(newLastWindow);
			}
		}
		return resultList;
	}


	/**
	 * Recursive function. Returns the index where the start value of the window is found
	 * or the index right after if the exact value is not find.
	 * @param list
	 * @param value
	 * @param indexStart
	 * @param indexStop
	 * @return
	 */
	private int findStart(List<ChromosomeWindow> list, int value, int indexStart, int indexStop) {
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
	 * Recursive function. Returns the index where the stop value of the window is found
	 * or the index right before if the exact value is not find.
	 * @param list
	 * @param value
	 * @param indexStart
	 * @param indexStop
	 * @return
	 */
	private int findStop(List<ChromosomeWindow> list, int value, int indexStart, int indexStop) {
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
}
