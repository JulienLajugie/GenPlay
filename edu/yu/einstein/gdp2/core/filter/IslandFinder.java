/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.filter;

import java.util.ArrayList;
import java.util.Collection;
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
import yu.einstein.gdp2.core.stat.Poisson;
import yu.einstein.gdp2.exception.InvalidFactorialParameterException;
import yu.einstein.gdp2.exception.InvalidLambdaPoissonParameterException;

/**
 * IslandFinder
 * This class implements the island approach. 
 * It contains algorithm to separate datas on island and some statistics methods corresponding.
 * @author Nicolas Fourel 
 */
public class IslandFinder {

	private final BinList 			binList;	// input binlist
	private final double 			readCountLimit;	// limit reads number to get an eligible windows
	private final int				gap;	// minimum windows number needed to separate 2 islands
	private final double			lambda;	// average number of reads in a window
	private final IslandResultType 	resultType;	// type of the result (constant, score, average)
	
	/**
	 * IslandFinder constructor
	 * 
	 * @param binList			the related binList
	 * @param readCountLimit	limit reads number to get an eligible windows
	 * @param gap				minimum windows number needed to separate 2 islands
	 */
	public IslandFinder (BinList binList, double readCountLimit, int gap, IslandResultType resultType) {
		this.binList = binList;
		this.readCountLimit = readCountLimit;
		this.gap = gap;
		this.lambda = lambdaCalcul();
		this.resultType = resultType;
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
	 * The relation can be write like this:	N/C
	 * 
	 * @return	value of lambda
	 */
	private double lambdaCalcul () {
		double result = 0.1;
		BLOSumScore totalScore = new BLOSumScore(this.binList, null);
		BLOCountNonNullBins windowsNumber = new BLOCountNonNullBins(this.binList, null);
		try {
			result = totalScore.compute() / windowsNumber.compute();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * findIsland method
	 * This method define island from data.
	 * It uses a specific bin list, the read count threshold and the gap.
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
					List<Double> resultList = null;
					ArrayList<Integer> islands_start = new ArrayList<Integer>();
					ArrayList<Integer> islands_stop = new ArrayList<Integer>();
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = ListFactory.createList(precision, currentList.size());
						int j = 0;
						while (j < currentList.size()) {
							if (currentList.get(j) >= readCountLimit) {
								islands_start.add(j);
								resultList.set(j, currentList.get(j));
								int gap_found = 0;
								int j_tmp = j + 1;
								while ((gap_found <= gap) && (j_tmp < currentList.size())) {
									if (currentList.get(j_tmp) >= readCountLimit) {
										resultList.set(j_tmp, currentList.get(j_tmp));
										gap_found = 0;
									} else {
										resultList.set(j_tmp, 0.0);
										gap_found++;
									}
									j_tmp++;
								}
								islands_stop.add(j_tmp - gap_found - 1);
								j = j_tmp;
							} else {
								resultList.set(j, 0.0);
								j++;
							}
						}
					}
					switch (resultType) {
					case CONSTANT:
						resultList = getListIslandWithConstantValue(precision, currentList, islands_start, islands_stop);
						break;
					case WINDOWSCORE:
						resultList = getListIslandWithScoreWindowValue(precision, currentList, islands_start, islands_stop);
						break;
					case ISLANDSCORE:
						resultList = getListIslandWithScoreIslandValue(precision, currentList, islands_start, islands_stop);
						break;
					case ISLANDSCOREAVERAGE:
						resultList = getListIslandWithScoreIslandAverageValue(precision, currentList, islands_start, islands_stop);
					}
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
	
	/**
	 * getListIslandWithConstantValue method
	 * This method makes a list of double to determine the value of each windows of the BinList.
	 * This value is constant and is 10. All values out of islands are to 0.
	 * 
	 * @param precision			bits precision
	 * @param currentList		current list of windows value (just the size is used here, i.e. the number of windows in the BinList)
	 * @param islands_start		start positions of all islands
	 * @param islands_stop		stop positions of all islands
	 * @return					list of windows values
	 */
	private List<Double> getListIslandWithConstantValue (DataPrecision precision,
														List<Double> currentList,
														ArrayList<Integer> islands_start,
														ArrayList<Integer> islands_stop) {
		List<Double> resultList = ListFactory.createList(precision, currentList.size());
		int current_pos = 0;
		double value = 0.0;
		for (int i = 0; i < currentList.size(); i++){
			if (current_pos < islands_start.size()) {
				if (i >= islands_start.get(current_pos) && i <= islands_stop.get(current_pos)) {
					value = 10.0;
					if (i == islands_stop.get(current_pos)) {
						current_pos++;
					}
				} else {
					value = 0.0;
				}
			} else {
				value = 0.0;
			}
			resultList.set(i, value);
		}
		return resultList;
	}
	
	/**
	 * getListIslandWithScoreWindowValue method
	 * This method makes a list of double to determine the value of each windows of the BinList.
	 * This value is the window score. All values out of islands are to 0.
	 * 
	 * @param precision			bits precision
	 * @param currentList		current list of windows value
	 * @param islands_start		start positions of all islands
	 * @param islands_stop		stop positions of all islands
	 * @return					list of windows values
	 */
	private List<Double> getListIslandWithScoreWindowValue (DataPrecision precision,
															List<Double> currentList,
															ArrayList<Integer> islands_start,
															ArrayList<Integer> islands_stop) {
		List<Double> resultList = ListFactory.createList(precision, currentList.size());
		int current_pos = 0;
		double value = 0.0;
		for (int i = 0; i < currentList.size(); i++) {
			if (current_pos < islands_start.size()) {
				if (i >= islands_start.get(current_pos) && i <= islands_stop.get(current_pos)) {
					value = scoreOfWindow(currentList.get(i));
					if (i == islands_stop.get(current_pos)) {
						current_pos++;
					}
				} else {
					value = 0.0;
				}
			} else {
				value = 0.0;
			}
			resultList.set(i, value);
		}
		return resultList;
	}
	
	/**
	 * getListIslandWithScoreWindowValue method
	 * This method makes a list of double to determine the value of each windows of the BinList.
	 * This value is the island score. All values out of islands are to 0.
	 * 
	 * @param precision			bits precision
	 * @param currentList		current list of windows value
	 * @param islands_start		start positions of all islands
	 * @param islands_stop		stop positions of all islands
	 * @return					list of windows values
	 */
	private List<Double> getListIslandWithScoreIslandValue (DataPrecision precision,
															List<Double> currentList,
															ArrayList<Integer> islands_start,
															ArrayList<Integer> islands_stop) {
		List<Double> resultList = ListFactory.createList(precision, currentList.size());
		ArrayList<Double> scoreIsland = calculateScoreIsland(currentList, islands_start, islands_stop);
		int current_pos = 0;
		double value = 0.0;
		for (int i = 0; i < currentList.size(); i++) {
			if (current_pos < islands_start.size()){
				if (i >= islands_start.get(current_pos) && i <= islands_stop.get(current_pos)) {
					value = scoreIsland.get(current_pos);
					if (i == islands_stop.get(current_pos)) {
						current_pos++;
					}
				} else {
					value = 0.0;
				}
			} else {
				value = 0.0;
			}
			resultList.set(i, value);
		}
		//showValue(currentList, islands_start, islands_stop, scoreIsland);
		return resultList;
	}
	
	/**
	 * getListIslandWithScoreWindowValue method
	 * This method makes a list of double to determine the value of each windows of the BinList.
	 * This value is the island average score. All values out of islands are to 0.
	 * 
	 * @param precision			bits precision
	 * @param currentList		current list of windows value
	 * @param islands_start		start positions of all islands
	 * @param islands_stop		stop positions of all islands
	 * @return					list of windows values
	 */
	private List<Double> getListIslandWithScoreIslandAverageValue (DataPrecision precision,
															List<Double> currentList,
															ArrayList<Integer> islands_start,
															ArrayList<Integer> islands_stop) {
		List<Double> resultList = ListFactory.createList(precision, currentList.size());
		ArrayList<Double> scoreIsland = calculateScoreIslandAverage(currentList, islands_start, islands_stop);
		int current_pos = 0;
		double value = 0.0;
		for (int i = 0; i < currentList.size(); i++) {
			if (current_pos < islands_start.size()){
				if (i >= islands_start.get(current_pos) && i <= islands_stop.get(current_pos)) {
					value = scoreIsland.get(current_pos);
					if (i == islands_stop.get(current_pos)) {
						current_pos++;
					}
				} else {
					value = 0.0;
				}
			} else {
				value = 0.0;
			}
			resultList.set(i, value);
		}
		//showValue(currentList, islands_start, islands_stop, scoreIsland);
		return resultList;
	}
	
	
	/**
	 * calculateScoreIslandAverage method
	 * This method calculate the average score for all islands.
	 * 
	 * @param currentList		current list of windows value
	 * @param islands_start		start positions of all islands
	 * @param islands_stop		stop positions of all islands
	 * @return					list of average score islands
	 */
	private ArrayList<Double> calculateScoreIslandAverage (List<Double> currentList,
													ArrayList<Integer> islands_start,
													ArrayList<Integer> islands_stop) {
		ArrayList<Double> scoreIsland = new ArrayList<Double> ();
		int current_pos = 0;
		int eligibleWindow;
		double sumScore;
		while (current_pos < islands_start.size()) {
			sumScore = 0.0;
			eligibleWindow = 0;
			for (int i = islands_start.get(current_pos); i <= islands_stop.get(current_pos); i++) {
				if (currentList.get(i) >= this.readCountLimit) {
					sumScore += scoreOfWindow(currentList.get(i));
					eligibleWindow++;
				}
			}
			scoreIsland.add(sumScore / eligibleWindow);
			current_pos++;
		}
		return scoreIsland;
	}
	
	/**
	 * calculateScoreIsland method
	 * This method calculate the score for all islands.
	 * 
	 * @param currentList		current list of windows value
	 * @param islands_start		start positions of all islands
	 * @param islands_stop		stop positions of all islands
	 * @return					list of score islands
	 */
	private ArrayList<Double> calculateScoreIsland (List<Double> currentList,
													ArrayList<Integer> islands_start,
													ArrayList<Integer> islands_stop) {
		ArrayList<Double> scoreIsland = new ArrayList<Double> ();
		int current_pos = 0;
		double sumScore;
		while (current_pos < islands_start.size()) {
			sumScore = 0.0;
			for (int i = islands_start.get(current_pos); i <= islands_stop.get(current_pos); i++) {
				if (currentList.get(i) >= this.readCountLimit) {
					sumScore += scoreOfWindow(currentList.get(i));
				}
			}
			scoreIsland.add(sumScore);
			current_pos++;
		}
		return scoreIsland;
	}
	
	/**
	 * scoreOfWindow method
	 * This method calculate the window score with the number of reads of this window.
	 * 
	 * @param 	value	number of reads of the window
	 * @return			the window score
	 * @throws InvalidLambdaPoissonParameterException
	 * @throws InvalidFactorialParameterException
	 */
	private double scoreOfWindow (double value) {
		try {
			return -1*Poisson.logPoisson(lambda, (int)value);
		} catch (InvalidLambdaPoissonParameterException e) {
			e.printStackTrace();
		} catch (InvalidFactorialParameterException e) {
			e.printStackTrace();
		}
		return -1.0;
	}
	
	/**
	 * showValue method
	 * This method print different information about islands (start, end, score, average...)
	 * 
	 * @param currentList
	 * @param islands_start
	 * @param islands_stop
	 * @param scoreIsland
	 */
	private void showValue (List<Double> currentList,
							ArrayList<Integer> islands_start,
							ArrayList<Integer> islands_stop,
							ArrayList<Double> scoreIsland) {
		int current_pos = 0;
		while (current_pos < islands_start.size()) {
			System.out.println("___________________ Island " + current_pos + 1);
			System.out.println("Start: " + islands_start.get(current_pos));
			System.out.println("End: " + islands_stop.get(current_pos));
			for (int i = islands_start.get(current_pos); i <= islands_stop.get(current_pos); i++) {
				if (currentList.get(i) >= this.readCountLimit) {
					System.out.println("Score (" + i + "): " + scoreOfWindow(currentList.get(i)));
				} else {
					System.out.println("Score (" + i + "): below the readCountLimit threshold");
				}
			}
			System.out.println("Island average: " + scoreIsland.get(current_pos));
			current_pos++;
		}
		
	}
}
