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
package edu.yu.einstein.genplay.core.operation.binList.peakFinder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOCountNonNullLength;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOSumScore;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.core.stat.MathFunctions;
import edu.yu.einstein.genplay.core.stat.Poisson;
import edu.yu.einstein.genplay.dataStructure.enums.IslandResultType;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.bin.BinListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidFactorialParameterException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidLambdaPoissonParameterException;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;

/**
 * IslandFinder
 * This class implements the island approach.
 * It contains algorithm to separate data on island and some statistics methods corresponding.
 * @author Nicolas Fourel
 */
public class IslandFinder implements Serializable, Stoppable {

	private static final long serialVersionUID = -4661852717981921332L;
	private final BinList 					binList;			// input binlist
	private int								gap;				// minimum windows number needed to separate 2 islands
	private int								islandMinLength;
	private double 							windowMinValue;		// limit window value to get an eligible windows
	private double							islandMinScore;		// island score limit to select island
	private final double					lambda;				// average number of reads in a window
	private IslandResultType 				resultType;			// type of the result (constant, score, average)
	private final HashMap <Double, Double>	readScoreStorage;	// store the score for a read, the read is use as index and the score as value
	private boolean 						stopped = false;	// true when the action must be stopped


	/**
	 * IslandFinder constructor
	 * 
	 * @param binList			the related binList
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public IslandFinder (BinList binList) throws InterruptedException, ExecutionException {
		this.binList = binList;
		lambda = lambdaCalcul();
		readScoreStorage = new HashMap <Double, Double>();
	}

	/**
	 * IslandFinder constructor
	 * 
	 * @param binList			the related binList
	 * @param gap				minimum windows number needed to separate 2 islands
	 * @param minIslandLength	minimum size of the island in windows for an island to be eligible
	 * @param windowLimitValue	limit reads number to get an eligible windows
	 * @param islandLimitScore  minimum score for an island to eligible
	 * @param resultType 		{@link IslandResultType} of the result of the IslandFinder operation
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public IslandFinder (BinList binList, int gap, int minIslandLength, double windowLimitValue, double islandLimitScore, IslandResultType resultType) throws InterruptedException, ExecutionException {
		this.binList = binList;
		this.gap = gap;
		islandMinLength = minIslandLength;
		windowMinValue = windowLimitValue;
		islandMinScore = islandLimitScore;
		lambda = lambdaCalcul();
		this.resultType = resultType;
		readScoreStorage = new HashMap <Double, Double>();
	}


	/////////////////////////////////////////////////////////	main method

	/**
	 * findIsland method
	 * This method define island from data.
	 * It uses a specific bin list, the read count threshold and the gap.
	 * It run the type of score required.
	 * 
	 * @return	a bin list with a specific value to show islands on a track
	 * @throws 	InterruptedException
	 * @throws 	ExecutionException
	 * @throws CloneNotSupportedException
	 */
	public BinList findIsland () throws InterruptedException, ExecutionException, CloneNotSupportedException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<ListView<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<ListView<ScoredChromosomeWindow>>>();
		for (short i = 0; i < binList.size(); i++) {
			final ListView<ScoredChromosomeWindow> currentList = binList.get(i);
			Callable<ListView<ScoredChromosomeWindow>> currentThread = new Callable<ListView<ScoredChromosomeWindow>>() {
				@Override
				public ListView<ScoredChromosomeWindow> call() throws Exception {
					ListView<ScoredChromosomeWindow> resultList;
					if (currentList != null) {
						List<List<Integer>> islandsPositions;
						List<Double> scoreIsland;
						List<Double> islandSummits;

						// Search all islands position (0: start; 1: stop)
						islandsPositions = searchIslandPosition(currentList);
						// Calculate all islands score
						scoreIsland = islandScore(currentList, islandsPositions.get(0), islandsPositions.get(1));
						// Calculate all islands summit
						islandSummits = islandSummits(currentList, islandsPositions.get(0), islandsPositions.get(1));
						// Create the result list
						resultList = getListIsland(currentList, scoreIsland, islandsPositions.get(0), islandsPositions.get(1), islandSummits);
					} else {
						resultList = null;
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};
			threadList.add(currentThread);
		}
		List<ListView<ScoredChromosomeWindow>> result = op.startPool(threadList);
		if (result != null) {
			BinList resultList = new BinList(result, binList.getBinSize());
			return resultList;
		} else {
			return null;
		}
	}

	/////////////////////////////////////////////////////////	Main methods

	/**
	 * findPValue method
	 * This method calculate the p-value with a read count limit.
	 * 
	 * @param read	read count limit
	 * @return		the p-value
	 * @throws InvalidFactorialParameterException
	 * @throws InvalidLambdaPoissonParameterException
	 */
	public double findPValue (double read) throws InvalidLambdaPoissonParameterException, InvalidFactorialParameterException {
		double value = 0.0;
		if (read > 1.0) {
			for (int i=0; i <= (int)Math.round(read - 0.5d); i++) {
				value += Poisson.poisson(lambda, i);
			}
			if (!MathFunctions.isInteger(read)) {
				value = MathFunctions.linearInterpolation(	(int)Math.round(read - 0.5d),
						value,
						(int)Math.round(read + 0.5d),
						value + Poisson.poisson(lambda, (int)Math.round(read + 0.5d)),
						read);
			}
		} else if (read == 1.0) {
			value = Poisson.poisson(lambda, 0);
		}
		return (1 - value);
	}

	/**
	 * @return the input BinList
	 */
	public BinList getBinList() {
		return binList;
	}


	/**
	 * @return the size of the gap in bp
	 */
	public int getGap() {
		return gap;
	}


	/**
	 * @return the island score limit to select island
	 */
	public double getIslandLimitScore() {
		return islandMinScore;
	}


	/////////////////////////////////////////////////////////	statistics methods

	/**
	 * @return the average number of reads in a window
	 */
	public double getLambda() {
		return lambda;
	}

	/**
	 * getListIsland method
	 * This method makes a list of double to determine the value of each windows of the BinList.
	 * This value is the island score if IFSCORE is required else is the original window value (read window number).
	 * All values out of islands or below the cut off are to 0.
	 * 
	 * @param currentList		current list of windows value
	 * @param islandsStart		start positions of all islands
	 * @param islandsStop		stop positions of all islands
	 * @param islandSummits		summit values of the islands
	 * @return					list of windows values
	 */
	private ListView<ScoredChromosomeWindow> getListIsland (
			ListView<ScoredChromosomeWindow> currentList,
			List<Double> scoreIsland,
			List<Integer> islandsStart,
			List<Integer> islandsStop,
			List<Double> islandSummits) {
		BinListViewBuilder resultLVBuilder = new BinListViewBuilder(binList.getBinSize());
		int currentPos = 0;	// position on the island start and stop arrays
		double value = 0.0;
		for (int i = 0; (i < currentList.size()) && !stopped; i++) {	// for all window positions
			if (currentPos < islandsStart.size()){	// we must be below the island array size (start and stop are the same size)
				if ((i >= islandsStart.get(currentPos)) && (i <= islandsStop.get(currentPos))) {	// if the actual window is on an island
					if (scoreIsland.get(currentPos) >= islandMinScore) {	// the island score must be higher than the cut-off
						switch (resultType) {	// if the result type is
						case FILTERED:
							value = currentList.get(i).getScore();	// we keep the original value
							break;
						case IFSCORE:
							value = scoreIsland.get(currentPos);	// we keep the island score value
							break;
						case SUMMIT:
							if (currentList.get(i).getScore() < islandSummits.get(currentPos)) {
								value = 0d;
							} else {
								value = islandSummits.get(currentPos);
							}
						}
					} else {
						value = 0.0;
					}
					if (i == islandsStop.get(currentPos)) {	// when we are on the end of the island
						currentPos++;	// position is increased
					}
				} else {
					value = 0.0;
				}
			} else {
				value = 0.0;
			}
			resultLVBuilder.addElementToBuild((float) value);
		}
		return resultLVBuilder.getListView();
	}

	/**
	 * @return the minimum size of an island
	 */
	public int getMinIslandLength() {
		return islandMinLength;
	}


	/**
	 * @return the type of the result
	 */
	public IslandResultType getResultType() {
		return resultType;
	}


	/**
	 * @return the limit window value to get an eligible window
	 */
	public double getWindowLimitValue() {
		return windowMinValue;
	}


	/**
	 * calculateScoreIsland method
	 * This method calculate the score for all islands.
	 * The score of an island is the sum of all eligible windows score contained in it.
	 * 
	 * @param currentList		current list of windows value
	 * @param islandsStart		start positions of all islands
	 * @param islandsStop		stop positions of all islands
	 * @return					list of score islands
	 */
	private List<Double> islandScore (ListView<ScoredChromosomeWindow> currentList,
			List<Integer> islandsStart,
			List<Integer> islandsStop) {
		List<Double> scoreIsland = new ArrayList<Double> ();
		int currentPos = 0;
		double sumScore;
		while ((currentPos < islandsStart.size()) && !stopped) {
			sumScore = 0.0;
			int i = islandsStart.get(currentPos);
			while ((i <= islandsStop.get(currentPos)) && !stopped) {	// Loop for the sum
				if (currentList.get(i).getScore() >= windowMinValue) {	// the window reads must be highter than the readCountLimit
					sumScore += windowScore(currentList.get(i).getScore());
				}
				i++;
			}
			scoreIsland.add(sumScore);
			currentPos++;
		}
		return scoreIsland;
	}

	/**
	 * islandSummits method
	 * This method calculate the summit value for each island.
	 * The summit of an island is the greatest window score contained in it.
	 * 
	 * @param currentList		current list of windows value
	 * @param islandsStart		start positions of all islands
	 * @param islandsStop		stop positions of all islands
	 * @return					list of score islands
	 */
	private List<Double> islandSummits (ListView<ScoredChromosomeWindow> currentList,
			List<Integer> islandsStart,
			List<Integer> islandsStop) {
		List<Double> scoreIsland = new ArrayList<Double> ();
		int currentPos = 0;
		double summitScore;
		while ((currentPos < islandsStart.size()) && !stopped) {
			summitScore = Double.NEGATIVE_INFINITY; // the summit is the smallest double value
			int i = islandsStart.get(currentPos);
			while ((i <= islandsStop.get(currentPos)) && !stopped) {	// Loop for the sum
				summitScore = Math.max(summitScore, currentList.get(i).getScore());
				i++;
			}
			scoreIsland.add(summitScore);
			currentPos++;
		}
		return scoreIsland;
	}

	/**
	 * lambdaCalcul method
	 * This method calculate the lambda value.
	 * Lambda value is:	w.(N/L)
	 * with:	w: windows fixed size
	 * 			N: total number of reads
	 * 			L: genome length
	 * The genome length can be calculate with:	w.C
	 * with:	w: windows fixed size
	 * 			C: count of non null bins
	 * Then, the relation can be wrote like this:	N/C
	 * 
	 * @return	value of lambda
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	private double lambdaCalcul () throws InterruptedException, ExecutionException {
		double result = 0.1;
		SCWLOSumScore totalScore = new SCWLOSumScore(binList, null);
		SCWLOCountNonNullLength windowsLength = new SCWLOCountNonNullLength(binList, null);
		result = (totalScore.compute() * binList.getBinSize()) / windowsLength.compute();
		return result;
	}

	/**
	 * searchIslandPosition method
	 * This method determines the positions of all islands.
	 * Two list are create, the first for the start position and the second for the stop position.
	 * 
	 * @param currentList	current list of the bin list
	 * @return				array list with start position on index 0 and stop position on index 1
	 */
	private List<List<Integer>> searchIslandPosition (ListView<ScoredChromosomeWindow> currentList) {
		List<List<Integer>> islandsPositions = new ArrayList<List<Integer>>();
		List<Integer> islandsStart = new ArrayList<Integer>();	// stores all start islands position
		List<Integer> islandsStop = new ArrayList<Integer>();		// stores all stop islands position
		if ((currentList != null) && (currentList.size() != 0)) {
			int j = 0;
			int islandStartPos;
			int islandStopPos;
			while ((j < currentList.size()) && !stopped) {	// while we are below the current list size,
				if (currentList.get(j).getScore() >= windowMinValue) {	// the current window score must be higher than readCountLimit
					islandStartPos = j;
					int gapFound = 0;	// there are no gap found
					int jTmp = j + 1;	// we prepared the research on the next window
					while ((gapFound <= gap) && (jTmp < currentList.size()) && !stopped) {	// while we are below the gap number authorized and below the list size
						if (currentList.get(jTmp).getScore() >= windowMinValue) {	// if the next window score is higher than the readCountLimit
							gapFound = 0;	// gap number found must be 0
						} else {	// if the next window score is smaller than the readCountLimit
							gapFound++;	// one gap is found
						}
						jTmp++;	// we search on the next window
					}
					// we are here only if the number gap found is higher than the number gap authorized or if it's the end list
					// so, it's necessary the end of the island
					islandStopPos = jTmp - gapFound - 1;
					// the island must have a valid number of windows
					if (((islandStopPos - islandStartPos) + 1) >= islandMinLength ) {
						islandsStart.add(islandStartPos);
						islandsStop.add(islandStopPos);
					}
					j = jTmp;	// we can continue to search after this end island (not necessary if we are out of the list size)
				} else {
					j++;
				}
			}
		}
		islandsPositions.add(islandsStart);
		islandsPositions.add(islandsStop);
		return islandsPositions;
	}

	/**
	 * @param gap size of the gap in bp to set
	 */
	public void setGap(int gap) {
		this.gap = gap;
	}

	/**
	 * @param minIslandLength minimum size of an island
	 */
	public void setIslandMinLength(int minIslandLength) {
		islandMinLength = minIslandLength;
	}

	/**
	 * @param cutOff island score limit to select island to set
	 */
	public void setIslandMinScore(double cutOff) {
		islandMinScore = cutOff;
	}

	/**
	 * @param resultType type of the result to set
	 */
	public void setResultType(IslandResultType resultType) {
		this.resultType = resultType;
	}

	/**
	 * @param readCountLimit limit window value to get an eligible window to set
	 */
	public void setWindowMinValue(double readCountLimit) {
		windowMinValue = readCountLimit;
	}

	@Override
	public void stop() {
		stopped = true;
	}

	/**
	 * scoreOfWindow method
	 * This method calculate the window score with the number of reads of this window.
	 * 
	 * @param 	value	number of reads of the window
	 * @return			the window score
	 */
	private double windowScore (double value) {
		double result = -1.0;
		synchronized (readScoreStorage) {

			if (readScoreStorage.containsKey(value)){	// if the score is stored
				try {
					result = readScoreStorage.get(value);	// we get it
				} catch (Exception e) {
					ExceptionManager.getInstance().caughtException(e);
				}
			} else {	// else we have to calculated it
				try {
					result = -1*Poisson.logPoisson(lambda, (int)value);
					readScoreStorage.put(value, result);
				} catch (InvalidLambdaPoissonParameterException e) {
					ExceptionManager.getInstance().caughtException(e);
				} catch (InvalidFactorialParameterException e) {
					ExceptionManager.getInstance().caughtException(e);
				}
			}
		}
		return result;
	}

}
