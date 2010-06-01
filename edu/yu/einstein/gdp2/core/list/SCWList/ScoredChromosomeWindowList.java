/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.DisplayableListOfLists;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.util.DoubleLists;


/**
 * A list of {@link ScoredChromosomeWindow}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ScoredChromosomeWindowList extends DisplayableListOfLists<ScoredChromosomeWindow, List<ScoredChromosomeWindow>> implements Serializable {

	private static final long serialVersionUID = 6268393967488568174L; // generated ID


	/**
	 * Creates an instance of {@link ScoredChromosomeWindowList}
	 * @param startList list of start positions
	 * @param stopList list of stop position
	 * @param scoreList list of score
	 * @throws InvalidChromosomeException
	 */
	public ScoredChromosomeWindowList(ChromosomeListOfLists<Integer> startList, 
			ChromosomeListOfLists<Integer> stopList, ChromosomeListOfLists<Double> scoreList) throws InvalidChromosomeException {
		super();
		for(short i = 0; i < startList.size(); i++) {
			add(new ArrayList<ScoredChromosomeWindow>());
			Chromosome chromo = chromosomeManager.get(i);
			for(int j = 0; j < startList.size(i); j++) {
				double score = scoreList.get(i, j);
				if (score != 0) {
					int start = startList.get(i, j);
					int stop = stopList.get(i, j);
					add(chromo, new ScoredChromosomeWindow(start, stop, score));
				}
			}
		}
		for (List<ScoredChromosomeWindow> currentChrWindowList : this) {
			Collections.sort(currentChrWindowList);
		}
	}

	
	/**
	 * Creates an instance of {@link ScoredChromosomeWindow} 
	 */
	public ScoredChromosomeWindowList() {
		super();
		for (int i = 0; i < chromosomeManager.size(); i++) {
			add(new ArrayList<ScoredChromosomeWindow>());
		}
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
	private int findStart(List<ScoredChromosomeWindow> list, int value, int indexStart, int indexStop) {
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
	private int findStop(List<ScoredChromosomeWindow> list, int value, int indexStart, int indexStop) {
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


	/**
	 * @param position a position on the fitted chromosome
	 * @return the score of the window on the fitted chromosome containing the specified position
	 */
	public double getScore(int position) {
		// if the fitted chromosome as no windows we return 0
		if ((get(fittedChromosome) == null) || (get(fittedChromosome).size() == 0)) {
			return 0;
		}
		// we search a window containing the position in parameter
		int indexStart = findStop(get(fittedChromosome), position, 0, size(fittedChromosome) - 1);
		if ((position >= get(fittedChromosome, indexStart).getStart()) && (position <= get(fittedChromosome, indexStart).getStop())) {
			return get(fittedChromosome, indexStart).getScore();
		} else {
			// if no window containing the position in parameter has been found we return 0
			return 0;
		}
		
	}	
	
	
	/**
	 * Merges two windows together if the gap between this two windows is not visible 
	 */
	@Override
	protected void fitToScreen() {
		List<ScoredChromosomeWindow> currentChromosomeList;
		try {
			currentChromosomeList = get(fittedChromosome);
		} catch (InvalidChromosomeException e) {
			e.printStackTrace();
			fittedChromosome = null;
			return;
		}
		if (fittedXRatio > 1) {
			fittedDataList = currentChromosomeList;
		} else {
			fittedDataList = new ArrayList<ScoredChromosomeWindow>();			
			if (currentChromosomeList.size() > 1) {
				ArrayList<Double> scoreList = new ArrayList<Double>();
				fittedDataList.add(new ScoredChromosomeWindow(currentChromosomeList.get(0)));
				scoreList.add(currentChromosomeList.get(0).getScore());
				int i = 1;
				int j = 0;
				while (i < currentChromosomeList.size()) {
					double gapDistance = (currentChromosomeList.get(i).getStart() - fittedDataList.get(j).getStop()) * fittedXRatio;
					double windowWidth = (currentChromosomeList.get(i).getStop() - fittedDataList.get(j).getStart()) * fittedXRatio;
					double currentScore = fittedDataList.get(j).getScore();
					double nextScore = currentChromosomeList.get(i).getScore();
					// we merge two intervals together if there is a gap smaller than 1 pixel and have the same score 
					// or if the width of a window is smaller than 1
					while ( (i + 1 < currentChromosomeList.size()) && 
							( ((gapDistance < 1) && (currentScore == nextScore)) || 
									((windowWidth < 1) && (nextScore != 0)))) {
						// the new stop position is the max of the current stop and the stop of the new merged interval
						int newStop = Math.max(fittedDataList.get(j).getStop(), currentChromosomeList.get(i).getStop());
						fittedDataList.get(j).setStop(newStop);
						scoreList.add(currentChromosomeList.get(i).getScore());
						i++;
						gapDistance = (currentChromosomeList.get(i).getStart() - fittedDataList.get(j).getStop()) * fittedXRatio;
						windowWidth = (currentChromosomeList.get(i).getStop() - fittedDataList.get(j).getStart()) * fittedXRatio;
						nextScore = currentChromosomeList.get(i).getScore(); 
					}
					fittedDataList.get(j).setScore(DoubleLists.average(scoreList));
					fittedDataList.add(new ScoredChromosomeWindow(currentChromosomeList.get(i)));
					scoreList = new ArrayList<Double>();
					scoreList.add(currentChromosomeList.get(i).getScore());
					j++;	
					i++;
				}
			}
		}		
	}


	/**
	 * Creates a BinList from the data of the current list
	 * @param binSize size of the bins
	 * @param precision precision of the data (eg: 1/8/16/32/64-BIT)
	 * @param method method to generate the BinList (eg: AVERAGE, SUM or MAXIMUM)
	 * @return a {@link BinList}
	 * @throws IllegalArgumentException
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public BinList generateBinList(int binSize, DataPrecision precision, ScoreCalculationMethod method) throws IllegalArgumentException, InterruptedException, ExecutionException {
		return new BinList(binSize, precision, method, this);
	}


	@Override
	protected List<ScoredChromosomeWindow> getFittedData(int start, int stop) {
		if ((fittedDataList == null) || (fittedDataList.size() == 0)) {
			return null;
		}

		ArrayList<ScoredChromosomeWindow> resultList = new ArrayList<ScoredChromosomeWindow>();

		int indexStart = findStart(fittedDataList, start, 0, fittedDataList.size() - 1);
		int indexStop = findStop(fittedDataList, stop, 0, fittedDataList.size() - 1);
		if (indexStart > 0) {
			// any window starting before the screen start position 
			// and ending after this position need to be printed  
			for (int i = 0; i < indexStart; i++) {
				if (fittedDataList.get(i).getStop() >= start) {
					ScoredChromosomeWindow currentWindow = fittedDataList.get(i); 
					ScoredChromosomeWindow newLastWindow = new ScoredChromosomeWindow(start, currentWindow.getStop(), currentWindow.getScore());
					resultList.add(newLastWindow);
				}
			}
		}
		for (int i = indexStart; i <= indexStop; i++) {
			resultList.add(fittedDataList.get(i));
		}
		if (indexStop + 1 < fittedDataList.size()) {
			if (fittedDataList.get(indexStop + 1).getStart() <= stop) {
				ScoredChromosomeWindow currentWindow = fittedDataList.get(indexStop + 1); 
				ScoredChromosomeWindow newLastWindow = new ScoredChromosomeWindow(currentWindow.getStart(), stop, currentWindow.getScore());
				resultList.add(newLastWindow);
			}
		}
		return resultList;
	}
}
