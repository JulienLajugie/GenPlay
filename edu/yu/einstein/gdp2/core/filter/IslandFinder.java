/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.IslandResultType;
import yu.einstein.gdp2.core.list.arrayList.ListFactory;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOCountNonNullBins;
import yu.einstein.gdp2.core.list.binList.operation.BLOSumScore;
import yu.einstein.gdp2.core.operationPool.OperationPool;
import yu.einstein.gdp2.core.stat.MathFunctions;
import yu.einstein.gdp2.core.stat.Poisson;
import yu.einstein.gdp2.exception.InvalidFactorialParameterException;
import yu.einstein.gdp2.exception.InvalidLambdaPoissonParameterException;

/**
 * IslandFinder
 * This class implements the island approach. 
 * It contains algorithm to separate data on island and some statistics methods corresponding.
 * @author Nicolas Fourel 
 */
public class IslandFinder {

	private final BinList 				binList;			// input binlist
	private int							gap;				// minimum windows number needed to separate 2 islands
	private int							islandMinLength;
	private double 						windowMinValue;		// limit window value to get an eligible windows
	private double						islandMinScore;		// island score limit to select island
	private double						lambda;				// average number of reads in a window
	private IslandResultType 			resultType;			// type of the result (constant, score, average)
	private HashMap <Double, Double>	readScoreStorage;	// store the score for a read, the read is use as index and the score as value
	
	/**
	 * IslandFinder constructor
	 * 
	 * @param binList			the related binList
	 * @param windowMinValue	limit reads number to get an eligible windows
	 * @param gap				minimum windows number needed to separate 2 islands
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public IslandFinder (BinList binList) throws InterruptedException, ExecutionException {
		this.binList = binList;
		this.lambda = lambdaCalcul();
		this.readScoreStorage = new HashMap <Double, Double>();
	}
	
	/**
	 * IslandFinder constructor
	 * 
	 * @param binList			the related binList
	 * @param readCountLimit	limit reads number to get an eligible windows
	 * @param gap				minimum windows number needed to separate 2 islands
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public IslandFinder (BinList binList, int gap, int minIslandLength, double windowLimitValue, double islandLimitScore, IslandResultType resultType) throws InterruptedException, ExecutionException {
		this.binList = binList;
		this.gap = gap;
		this.islandMinLength = minIslandLength;
		this.windowMinValue = windowLimitValue;
		this.islandMinScore = islandLimitScore;
		this.lambda = lambdaCalcul();
		this.resultType = resultType;
		this.readScoreStorage = new HashMap <Double, Double>();
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
	 */
	public BinList findIsland () throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		final DataPrecision precision = binList.getPrecision();
		for (short i = 0; i < binList.size(); i++) {
			final List<Double> currentList = binList.get(i);
			
			Callable<List<Double>> currentThread = new Callable<List<Double>>() {
				@Override
				public List<Double> call() throws Exception {
					List<List<Integer>> islandsPositions;
					List<Double> scoreIsland;
					List<Double> resultList;
					// Search all islands position (0: start; 1: stop)
					islandsPositions = searchIslandPosition(currentList);
					// Calculate all islands score
					scoreIsland = islandScore(currentList, islandsPositions.get(0), islandsPositions.get(1));
					// Create the result list
					resultList = getListIsland(precision, currentList, scoreIsland, islandsPositions.get(0), islandsPositions.get(1));
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};
			threadList.add(currentThread);
		}
		List<List<Double>> result = op.startPool(threadList);
		if (result != null) {
			BinList resultList = new BinList(binList.getBinSize(), precision, result);
			return resultList;
		} else {
			return null;
		}
	}
	
	/////////////////////////////////////////////////////////	Main methods
	
	/**
	 * searchIslandPosition method
	 * This method determines the positions of all islands.
	 * Two list are create, the first for the start position and the second for the stop position. 
	 * 
	 * @param currentList	current list of the bin list 
	 * @return				array list with start position on index 0 and stop position on index 1
	 */
	private List<List<Integer>> searchIslandPosition (List<Double> currentList) {
		List<List<Integer>> islandsPositions = new ArrayList<List<Integer>>();
		List<Integer> islandsStart = new ArrayList<Integer>();	// stores all start islands position
		List<Integer> islandsStop = new ArrayList<Integer>();		// stores all stop islands position
		if ((currentList != null) && (currentList.size() != 0)) {
			int j = 0;
			int islandStartPos;
			int islandStopPos;
			while (j < currentList.size()) {	// while we are below the current list size,
				if (currentList.get(j) >= windowMinValue) {	// the current window score must be higher than readCountLimit
					islandStartPos = j;
					int gapFound = 0;	// there are no gap found
					int jTmp = j + 1;	// we prepared the research on the next window
					while ((gapFound <= gap) && (jTmp < currentList.size())) {	// while we are below the gap number authorized and below the list size
						if (currentList.get(jTmp) >= windowMinValue) {	// if the next window score is higher than the readCountLimit
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
					if ((islandStopPos - islandStartPos) >= this.islandMinLength ) {
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
	 * calculateScoreIsland method
	 * This method calculate the score for all islands.
	 * The score of an island is the sum of all eligible windows score contained in it.
	 * 
	 * @param currentList		current list of windows value
	 * @param islandsStart		start positions of all islands
	 * @param islandsStop		stop positions of all islands
	 * @return					list of score islands
	 */
	private List<Double> islandScore (List<Double> currentList,
										List<Integer> islandsStart,
										List<Integer> islandsStop) {
		List<Double> scoreIsland = new ArrayList<Double> ();
		int currentPos = 0;
		double sumScore;
		while (currentPos < islandsStart.size()) {
			sumScore = 0.0;
			for (int i = islandsStart.get(currentPos); i <= islandsStop.get(currentPos); i++) {	// Loop for the sum
				if (currentList.get(i) >= this.windowMinValue) {	// the window reads must be highter than the readCountLimit
					sumScore += windowScore(currentList.get(i));
				}
			}
			scoreIsland.add(sumScore);
			currentPos++;
		}
		return scoreIsland;
	}
	
	/**
	 * getListIsland method
	 * This method makes a list of double to determine the value of each windows of the BinList.
	 * This value is the island score if IFSCORE is required else is the original window value (read window number).
	 * All values out of islands or below the cut off are to 0.
	 * 
	 * @param precision			bits precision
	 * @param currentList		current list of windows value
	 * @param islandsStart		start positions of all islands
	 * @param islandsStop		stop positions of all islands
	 * @return					list of windows values
	 */
	private List<Double> getListIsland (DataPrecision precision,
											List<Double> currentList,
											List<Double> scoreIsland,
											List<Integer> islandsStart,
											List<Integer> islandsStop) {
		List<Double> resultList = ListFactory.createList(precision, currentList.size());
		int currentPos = 0;	// position on the island start and stop arrays
		double value = 0.0;
		for (int i = 0; i < currentList.size(); i++) {	// for all window positions
			if (currentPos < islandsStart.size()){	// we must be below the island array size (start and stop are the same size)
				if (i >= islandsStart.get(currentPos) && i <= islandsStop.get(currentPos)) {	// if the actual window is on an island
					if (scoreIsland.get(currentPos) >= this.islandMinScore) {	// the island score must be higher than the cut-off
						switch (this.resultType) {	// if the result type is
						case FILTERED:
							value = currentList.get(i);	// we keep the original value
							break;
						case IFSCORE:
							value = scoreIsland.get(currentPos);	// we keep the island score value
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
			resultList.set(i, value);	// the result list is set with the right value
		}
		return resultList;
	}
	
	
	/////////////////////////////////////////////////////////	statistics methods
	
	/**
	 * scoreOfWindow method
	 * This method calculate the window score with the number of reads of this window.
	 * 
	 * @param 	value	number of reads of the window
	 * @return			the window score
	 * @throws InvalidLambdaPoissonParameterException
	 * @throws InvalidFactorialParameterException
	 */
	private double windowScore (double value) {
		double result = -1.0;
		if (this.readScoreStorage.containsKey(value)){	// if the score is stored
			try {
				result = this.readScoreStorage.get(value);	// we get it
			} catch (Exception e) {
				System.out.println("value: " + value);
				e.printStackTrace();
			}
		} else {	// else we have to calculated it
			try {
				result = -1*Poisson.logPoisson(lambda, (int)value);
				this.readScoreStorage.put(value, result);
			} catch (InvalidLambdaPoissonParameterException e) {
				e.printStackTrace();
			} catch (InvalidFactorialParameterException e) {
				e.printStackTrace();
			}
		}
		return result;
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
		BLOSumScore totalScore = new BLOSumScore(this.binList, null);
		BLOCountNonNullBins windowsNumber = new BLOCountNonNullBins(this.binList, null);
		result = totalScore.compute() / windowsNumber.compute();
		return result;
	}
	
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
				value += Poisson.poisson(this.lambda, i);
			}
			if (!MathFunctions.isInteger(read)) {
				value = MathFunctions.linearInterpolation(	(int)Math.round(read - 0.5d),
															value,
															(int)Math.round(read + 0.5d),
															value + Poisson.poisson(this.lambda, (int)Math.round(read + 0.5d)),
															read);
			}
		} else if (read == 1.0) {
			value = Poisson.poisson(this.lambda, 0);
		}
		return (1 - value);
	}
	
	
	// Setters
	public void setGap(int gap) {
		this.gap = gap;
	}
	
	public void setIslandMinLength(int minIslandLength) {
		this.islandMinLength = minIslandLength;
	}

	public void setWindowMinValue(double readCountLimit) {
		this.windowMinValue = readCountLimit;
	}

	public void setIslandMinScore(double cutOff) {
		this.islandMinScore = cutOff;
	}

	public void setResultType(IslandResultType resultType) {
		this.resultType = resultType;
	}

	// Getters
	public BinList getBinList() {
		return binList;
	}
	
	public int getGap() {
		return gap;
	}
	
	public int getMinIslandLength() {
		return islandMinLength;
	}

	public double getWindowLimitValue() {
		return windowMinValue;
	}

	public double getIslandLimitScore() {
		return islandMinScore;
	}

	public double getLambda() {
		return lambda;
	}
	
}
