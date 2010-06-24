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
	private double 						readCountLimit;		// limit reads number to get an eligible windows
	private double						lambda;				// average number of reads in a window
	private double						cutOff;				// island score limit to select island
	private IslandResultType 			resultType;			// type of the result (constant, score, average)
	private HashMap <Double, Double>	readScoreStorage;	// store the score for a read, the read is use as index and the score as value
	
	
	/**
	 * IslandFinder constructor
	 * 
	 * @param binList			the related binList
	 * @param readCountLimit	limit reads number to get an eligible windows
	 * @param gap				minimum windows number needed to separate 2 islands
	 */
	public IslandFinder (BinList binList) {
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
	 */
	public IslandFinder (BinList binList, double readCountLimit, int gap, IslandResultType resultType) {
		this.binList = binList;
		this.readCountLimit = readCountLimit;
		this.gap = gap;
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
					List<Double> resultList;	// often commented because it's useless here but it's kept because it may be interesting
					ArrayList<Integer> islands_start = new ArrayList<Integer>();	// stores all start islands position
					ArrayList<Integer> islands_stop = new ArrayList<Integer>();		// stores all stop islands position
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = ListFactory.createList(precision, currentList.size());
						int j = 0;
						while (j < currentList.size()) {	// while we are below the current list size,
							if (currentList.get(j) >= readCountLimit) {	// the current window score must be higher than readCountLimit
								islands_start.add(j);	// it's the start of an island
								//resultList.set(j, currentList.get(j));
								int gap_found = 0;	// there are no gap found
								int j_tmp = j + 1;	// we prepared the research on the next window
								while ((gap_found <= gap) && (j_tmp < currentList.size())) {	// while we are below the gap number authorized and below the list size
									if (currentList.get(j_tmp) >= readCountLimit) {	// if the next window score is higher than the readCountLimit
										//resultList.set(j_tmp, currentList.get(j_tmp));
										gap_found = 0;	// gap number found must be 0
									} else {	// if the next window score is smaller than the readCountLimit
										//resultList.set(j_tmp, 0.0);
										gap_found++;	// one gap is found
									}
									j_tmp++;	// we search on the next window
								}
								// we are here only if the number gap found is higher than the number gap authorized or if it's the end list
								// so, it's necessary the end of the island
								islands_stop.add(j_tmp - gap_found - 1);
								j = j_tmp;	// we can continue to search after this end island (not necessary if we are out of the list size)
							} else {
								//resultList.set(j, 0.0);
								j++;
							}
						}
					}
					// the resultList is ready to be set
					resultList = getListIsland(precision, currentList, islands_start, islands_stop);
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
	
	
	/////////////////////////////////////////////////////////	getList method
	
	/**
	 * getListIsland method
	 * This method makes a list of double to determine the value of each windows of the BinList.
	 * This value is the island score if IFSCORE is required else is the original window value (read window number).
	 * All values out of islands or below the cut off are to 0.
	 * 
	 * @param precision			bits precision
	 * @param currentList		current list of windows value
	 * @param islands_start		start positions of all islands
	 * @param islands_stop		stop positions of all islands
	 * @return					list of windows values
	 */
	private List<Double> getListIsland (DataPrecision precision,
															List<Double> currentList,
															ArrayList<Integer> islands_start,
															ArrayList<Integer> islands_stop) {
		List<Double> resultList = ListFactory.createList(precision, currentList.size());
		ArrayList<Double> scoreIsland = islandScore(currentList, islands_start, islands_stop);
		int current_pos = 0;	// position on the island start and stop arrays
		double value = 0.0;
		for (int i = 0; i < currentList.size(); i++) {	// for all window positions
			if (current_pos < islands_start.size()){	// we must be below the island array size (start and stop are the same size)
				if (i >= islands_start.get(current_pos) && i <= islands_stop.get(current_pos)) {	// if the actual window is on an island
					if (scoreIsland.get(current_pos) >= this.cutOff) {	// the island score must be higher than the cut-off
						switch (this.resultType) {	// if the result type is
						case FILTERED:
							value = currentList.get(i);	// we keep the original value
							break;
						case IFSCORE:
							value = scoreIsland.get(current_pos);	// we keep the island score value
						}
					} else {
						value = 0.0;
					}
					if (i == islands_stop.get(current_pos)) {	// when we are on the end of the island
						current_pos++;	// position is increased
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
	
	
	/////////////////////////////////////////////////////////	Score methods
	
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
	 * calculateScoreIsland method
	 * This method calculate the score for all islands.
	 * The score of an island is the sum of all eligible windows score contained in it.
	 * 
	 * @param currentList		current list of windows value
	 * @param islands_start		start positions of all islands
	 * @param islands_stop		stop positions of all islands
	 * @return					list of score islands
	 */
	private ArrayList<Double> islandScore (List<Double> currentList,
													ArrayList<Integer> islands_start,
													ArrayList<Integer> islands_stop) {
		ArrayList<Double> scoreIsland = new ArrayList<Double> ();
		int current_pos = 0;
		double sumScore;
		while (current_pos < islands_start.size()) {
			sumScore = 0.0;
			for (int i = islands_start.get(current_pos); i <= islands_stop.get(current_pos); i++) {	// Loop for the sum
				if (currentList.get(i) >= this.readCountLimit) {	// the window reads must be highter than the readCountLimit
					sumScore += windowScore(currentList.get(i));
				}
			}
			scoreIsland.add(sumScore);
			current_pos++;
		}
		return scoreIsland;
	}
	
	
	/////////////////////////////////////////////////////////	statistics methods
	
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
	 * findReadCountLimit method
	 * This method calculate the read count limit with a pvalue.
	 * 
	 * @param pvalue	probability that the result appear by chance
	 * @return			the read count limit
	 */
	public double findReadCountLimit (double pvalue) {
		double value = 1.0;
		int index = 0;
		if (pvalue > 0.0 && pvalue < 1.0) {
			try {
				value -= Poisson.poisson(this.lambda, index);
			} catch (InvalidLambdaPoissonParameterException e) {
				e.printStackTrace();
			} catch (InvalidFactorialParameterException e) {
				e.printStackTrace();
			}
			while (value > pvalue) {
				index++;
				try {
					value -= Poisson.poisson(this.lambda, index);
				} catch (InvalidLambdaPoissonParameterException e) {
					e.printStackTrace();
				} catch (InvalidFactorialParameterException e) {
					e.printStackTrace();
				}
			}
			return (index + 1.0);
		} else if (pvalue == 1.0) {
			return 0.0;
		} else {
			return -1.0;
		}
	}
	
	/**
	 * findPValue method
	 * This method calculate the p-value with a read count limit.
	 * 
	 * @param read	read count limit
	 * @return		the p-value
	 */
	public double findPValue (double read) {
		double value = 0.0;
		double pvalue;
		if (read > 1.0) {
			for (int i=0; i < (read-1.0); i++) {
				try {
					value += Poisson.poisson(this.lambda, i);
				} catch (InvalidLambdaPoissonParameterException e) {
					e.printStackTrace();
				} catch (InvalidFactorialParameterException e) {
					e.printStackTrace();
				}
			}
		} else if (read == 1.0) {
			try {
				value = Poisson.poisson(this.lambda, 0);
			} catch (InvalidLambdaPoissonParameterException e) {
				e.printStackTrace();
			} catch (InvalidFactorialParameterException e) {
				e.printStackTrace();
			}
		}
		pvalue = Math.floor((1 - value) * Math.pow(10.0, 15)) / Math.pow(10.0, 15);
		return pvalue;
	}
	
	
	// Setters
	public void setGap(int gap) {
		this.gap = gap;
	}

	public void setReadCountLimit(double readCountLimit) {
		this.readCountLimit = readCountLimit;
	}

	public void setCutOff(double cutOff) {
		this.cutOff = cutOff;
	}

	public void setResultType(IslandResultType resultType) {
		this.resultType = resultType;
	}

	// Getters
	public int getGap() {
		return gap;
	}

	public double getReadCountLimit() {
		return readCountLimit;
	}

	public double getCutOff() {
		return cutOff;
	}

}
