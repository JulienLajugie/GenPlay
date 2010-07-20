/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.DisplayableListOfLists;
import yu.einstein.gdp2.core.list.SCWList.overLap.OverLapManagement;
import yu.einstein.gdp2.core.list.arrayList.IntArrayAsIntegerList;
import yu.einstein.gdp2.core.operationPool.OperationPool;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.util.DoubleLists;
import yu.einstein.gdp2.util.Utils;


/**
 * A list of {@link ScoredChromosomeWindow}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ScoredChromosomeWindowList extends DisplayableListOfLists<ScoredChromosomeWindow, List<ScoredChromosomeWindow>> implements Serializable {

	private static final long serialVersionUID = 6268393967488568174L; // generated ID
	/**
	 * @return the number of steps needed to create a {@link ScoredChromosomeWindowList}
	 */
	public static int getCreationStepCount() {
		return 2;
	}
	/*
	 * The following values are statistic values of the list
	 * They are transient because they depend on the chromosome manager that is also transient
	 * They are calculated at the creation of the list to avoid being recalculated
	 */
	transient private Double 	min = null;			// smallest value of the BinList
	transient private Double 	max = null;			// greatest value of the BinList 
	transient private Double 	average = null;		// average of the BinList
	transient private Double 	stDev = null;		// standard deviation of the BinList
	transient private Long 		nonNullLength = null;// count of none-null bins in the BinList
	
	private OverLapManagement overLapManagement;


	/**
	 * Creates an instance of {@link ScoredChromosomeWindowList}
	 * @param startList list of start positions
	 * @param stopList list of stop position
	 * @param scoreList list of score
	 * @throws InvalidChromosomeException
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public ScoredChromosomeWindowList(final ChromosomeListOfLists<Integer> startList, 
			final ChromosomeListOfLists<Integer> stopList,
			final ChromosomeListOfLists<Double> scoreList) throws InvalidChromosomeException, InterruptedException, ExecutionException {
		super();
		// retrieve the instance of the OperationPool
		final OperationPool op = OperationPool.getInstance();
		// list for the threads
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();		
		
		this.overLapManagement = new OverLapManagement(startList, stopList, scoreList);
		final boolean runOverLapEngine;
		if (this.overLapManagement.overLappingExist()) {
			runOverLapEngine = true;
			this.overLapManagement.setScoreCalculationMethod(Utils.chooseScoreCalculation(null));
		} else {
			runOverLapEngine = false;
		}
		for(final Chromosome currentChromosome : chromosomeManager) {
			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {	
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					if (startList.get(currentChromosome) == null) {
						return null;
					}
					List<ScoredChromosomeWindow> resultList = new ArrayList<ScoredChromosomeWindow>();	
					if (runOverLapEngine) {
						overLapManagement.run(currentChromosome);
						IntArrayAsIntegerList newStartList = overLapManagement.getNewStartList(currentChromosome);
						IntArrayAsIntegerList newStopList = overLapManagement.getNewStopList(currentChromosome);
						List<Double> newScoreList = overLapManagement.getNewScoreList(currentChromosome);
						for(int j = 0; j < newStartList.size(); j++) {
							double score = newScoreList.get(j);
							if (score != 0) {
								int start = newStartList.get(j);
								int stop = newStopList.get(j);
								resultList.add(new ScoredChromosomeWindow(start, stop, score));
							}
						}
					} else {
						for(int j = 0; j < startList.get(currentChromosome).size(); j++) {
							double score = scoreList.get(currentChromosome, j);
							if (score != 0) {
								int start = startList.get(currentChromosome, j);
								int stop = stopList.get(currentChromosome, j);
								resultList.add(new ScoredChromosomeWindow(start, stop, score));
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
		List<List<ScoredChromosomeWindow>> result = null;
		// starts the pool
		result = op.startPool(threadList);
		// add the chromosome results
		if (result != null) {
			for (List<ScoredChromosomeWindow> currentList: result) {
				add(currentList);
			}
		}		
		// sort the list
		for (List<ScoredChromosomeWindow> currentChrWindowList : this) {
			Collections.sort(currentChrWindowList);
		}
		// generate the statistics
		generateStatistics();
	}
	

	/**
	 * Creates an instance of {@link ScoredChromosomeWindow} 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public ScoredChromosomeWindowList(Collection<? extends List<ScoredChromosomeWindow>> data) throws InterruptedException, ExecutionException {
		super();
		addAll(data);
		// add the eventual missing chromosomes
		if (size() < chromosomeManager.size()) {
			for (int i = size(); i < chromosomeManager.size(); i++){
				add(null);
			}
		}
		// sort the list
		for (List<ScoredChromosomeWindow> currentChrWindowList : this) {
			if (currentChrWindowList != null) {
				Collections.sort(currentChrWindowList);
			}
		}
		generateStatistics();
	}


	/**
	 * Performs a deep clone of the current {@link ScoredChromosomeWindowList}
	 * @return a new ScoredChromosomeWindowList
	 */
	public ScoredChromosomeWindowList deepClone() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ((ScoredChromosomeWindowList)ois.readObject());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
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
	 * Computes some statistic values for this list
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	private void generateStatistics() throws InterruptedException, ExecutionException {
		// retrieve the instance of the OperationPool singleton
		final OperationPool op = OperationPool.getInstance();
		// list for the threads
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();

		// set the default value
		min = Double.POSITIVE_INFINITY;
		max = Double.NEGATIVE_INFINITY;
		average = 0d;
		stDev = 0d;
		nonNullLength = 0l;

		// create arrays so each statics variable can be calculated for each chromosome
		final double[] mins = new double[chromosomeManager.size()];
		final double[] maxs = new double[chromosomeManager.size()];
		final double[] stDevs = new double[chromosomeManager.size()];
		final double[] sumScoreByLengths = new double[chromosomeManager.size()];
		final long[] nonNullLengths = new long[chromosomeManager.size()];

		// computes min / max / total score / non null bin count for each chromosome
		for(short i = 0; i < size(); i++)  {
			final List<ScoredChromosomeWindow> currentList = get(i);
			final short currentIndex = i;

			Callable<Void> currentThread = new Callable<Void>() {	
				@Override
				public Void call() throws Exception {
					mins[currentIndex] = Double.POSITIVE_INFINITY;
					maxs[currentIndex] = Double.NEGATIVE_INFINITY;
					if (currentList != null) {				
						for (ScoredChromosomeWindow currentWindow: currentList) {
							if (currentWindow.getScore() != 0) {
								mins[currentIndex] = Math.min(mins[currentIndex], currentWindow.getScore());
								maxs[currentIndex] = Math.max(maxs[currentIndex], currentWindow.getScore());
								sumScoreByLengths[currentIndex] += currentWindow.getScore() * currentWindow.getSize();
								nonNullLengths[currentIndex] += currentWindow.getSize();
							}
						}
					}
					// notify that the current chromosome is done
					op.notifyDone();
					return null;
				}
			};

			threadList.add(currentThread);			
		}
		// start the pool of thread 
		op.startPool(threadList);

		double sumScoreByLength = 0;
		// compute the genome wide result from the chromosomes results
		for (int i = 0; i < chromosomeManager.size(); i++) {
			min = Math.min(min, mins[i]);
			max = Math.max(max, maxs[i]);
			sumScoreByLength += sumScoreByLengths[i];
			nonNullLength += nonNullLengths[i];
		}

		if (nonNullLength != 0) {
			// compute the average
			average = sumScoreByLength / (double) nonNullLength;
			threadList.clear();

			// compute the standard deviation for each chromosome
			for(short i = 0; i < size(); i++)  {
				final List<ScoredChromosomeWindow> currentList = get(i);
				final short currentIndex = i;

				Callable<Void> currentThread = new Callable<Void>() {	
					@Override
					public Void call() throws Exception {
						if (currentList != null) {				
							for (ScoredChromosomeWindow currentWindow: currentList) {
								if (currentWindow.getScore() != 0) {
									stDevs[currentIndex] += Math.pow(currentWindow.getScore() - average, 2) * currentWindow.getSize();
								}
							}
						}
						// notify that the current chromosome is done
						op.notifyDone();
						return null;
					}
				};

				threadList.add(currentThread);			
			}
			// start the pool of thread
			op.startPool(threadList);

			// compute the genome wide standard deviation
			for (int i = 0; i < chromosomeManager.size(); i++) {
				stDev += stDevs[i];
			}
			stDev = Math.sqrt(stDev / (double) nonNullLength);
		}
	}
	
		
	/**
	 * @return the average of the BinList
	 */
	public Double getAverage() {
		return average;
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


	/**
	 * @return the greatest value of the BinList
	 */
	public Double getMax() {
		return max;
	}


	/**
	 * @return the smallest value of the BinList
	 */
	public Double getMin() {
		return min;
	}


	/**
	 * @return the count of none-null bins in the BinList
	 */
	public Long getNonNullLength() {
		return nonNullLength;
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
	 * @return the standard deviation of the BinList
	 */
	public Double getStDev() {
		return stDev;
	}
	
	
	/**
	 * Computes the statistics of the list after unserialization
	 * @param in {@link ObjectInputStream}
	 * @throws IOExceptionm
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		try {
			generateStatistics();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
