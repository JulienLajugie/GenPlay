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
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.ListFactory;
import yu.einstein.gdp2.core.list.binList.operation.BLOCountNonNullBins;
import yu.einstein.gdp2.core.list.binList.operation.BLOSumScore;
import yu.einstein.gdp2.core.list.binList.operation.OperationPool;
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

	private final BinList 	binList;	// input binlist
	private final double 	readCountLimit;	// limit reads number to get an eligible windows
	private final int		gap;	// minimum windows number needed to separate 2 islands
	private final double	lambda;	// average number of reads in a window
	
	/**
	 * IslandFinder constructor
	 * 
	 * @param binList			the related binList
	 * @param readCountLimit	limit reads number to get an eligible windows
	 * @param gap				minimum windows number needed to separate 2 islands
	 */
	public IslandFinder (BinList binList, double readCountLimit, int gap) {
		this.binList = binList;
		this.readCountLimit = readCountLimit;
		this.gap = gap;
		this.lambda = lambdaCalcul();
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
					//resultList = getListIslandWithConstantValue(precision, currentList, islands_start, islands_stop);
					resultList = getListIslandWithScoreWindowValue(precision, currentList, islands_start, islands_stop);
					//System.out.println("Factorial: " + MyMathClass.getFactorialStorage());
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
	 * This value is the window score. All values out of islands are to 0.
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
		int current_pos = 0;
		double value = 0.0;
		for (int i = 0; i < currentList.size(); i++) {
			if (current_pos < islands_start.size()){
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
	
	private ArrayList<Double> calculateScoreIsland (List<Double> currentList,
													ArrayList<Integer> islands_start,
													ArrayList<Integer> islands_stop) {
		ArrayList<Double> scoreIsland = new ArrayList<Double> ();
		int pos = 0;
		int eligibleWindow;
		double sumScore;
		
		
		while (pos < islands_start.size()) {
			sumScore = 0.0;
			eligibleWindow = 0;
			for (int i = islands_start.get(pos); i <= islands_stop.get(pos); i++) {
				if (currentList.get(i) >= this.readCountLimit) {
				sumScore += scoreOfWindow(currentList.get(i));
				eligibleWindow++;
				}
			}
			scoreIsland.add(sumScore / eligibleWindow);
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
	
}
