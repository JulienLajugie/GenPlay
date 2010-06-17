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

	private final BinList 				binList;		// input binlist
	private final double 				readCountLimit;	// limit reads number to get an eligible windows
	private final int					gap;			// minimum windows number needed to separate 2 islands
	private final double				lambda;			// average number of reads in a window
	private final IslandResultType 		resultType;		// type of the result (constant, score, average)
	private	double						tValue; 		// probability of a window being ineligible
	private double						GFactor;		// G factor
	private double						scoreZero;
	private double 						boundaryContribution;
	private double 						genomeLength;
	private HashMap <Double, Double>	readScoreStorage;	// store the score for a read, the read is use as index and the score as value
	private HashMap <Double, Double>	windowProbabilityStorage;	// store the probability to find a  window score, the score is use as index and the probability as value
	private ArrayList <Double>	islandIntermediateProbStorage;	// store the intermediate probability to find an island score, the score is use as index and the intermediate probability as value
	
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
		this.tValue = -1.0;
		this.GFactor = -1.0;
		this.readScoreStorage = new HashMap <Double, Double>();
		this.windowProbabilityStorage = new HashMap <Double, Double>();
		this.islandIntermediateProbStorage = new ArrayList <Double>();
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
						resultList = getListIslandWithWindowScoreValue(precision, currentList, islands_start, islands_stop);
						break;
					case ISLANDSCORE:
						resultList = getListIslandWithIslandScoreValue(precision, currentList, islands_start, islands_stop);
						break;
					case ISLANDSCOREAVERAGE:
						resultList = getListIslandWithIslandScoreAverageValue(precision, currentList, islands_start, islands_stop);
						break;
					case WINDOWPROBABILITY:
						resultList = getListIslandWithWindowProbabilityValue(precision, currentList, islands_start, islands_stop);
						break;
					case ISLANDEXPECTATION:
						probabilityInitialization ();
						resultList = getListIslandWithIslandExpectationValue(precision, currentList, islands_start, islands_stop);
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
	private List<Double> getListIslandWithWindowScoreValue (DataPrecision precision,
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
	private List<Double> getListIslandWithIslandScoreValue (DataPrecision precision,
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
	private List<Double> getListIslandWithIslandScoreAverageValue (DataPrecision precision,
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
		double result = -1.0;
		if (this.readScoreStorage.containsKey(value)){
			try {
				result = this.readScoreStorage.get(value);
			} catch (Exception e) {
				System.out.println("value: " + value);
				e.printStackTrace();
			}
		} else {
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
	 * getListIslandWithWindowProbabilityValue method
	 * This method makes a list of double to determine the probability of significant of each windows of the BinList.
	 * This value is the window score. All values out of islands are to 0.
	 * 
	 * @param precision			bits precision
	 * @param currentList		current list of windows value
	 * @param islands_start		start positions of all islands
	 * @param islands_stop		stop positions of all islands
	 * @return					list of windows values
	 */
	private List<Double> getListIslandWithWindowProbabilityValue (DataPrecision precision,
																	List<Double> currentList,
																	ArrayList<Integer> islands_start,
																	ArrayList<Integer> islands_stop) {
		List<Double> resultList = ListFactory.createList(precision, currentList.size());
		int current_pos = 0;
		double value = 0.0;
		for (int i = 0; i < currentList.size(); i++) {
			if (current_pos < islands_start.size()) {
				if (i >= islands_start.get(current_pos) && i <= islands_stop.get(current_pos)) {
					value = windowProbability(currentList, scoreOfWindow(currentList.get(i)));
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
	 * getListIslandWithIslandExpectationValue method
	 * This method makes a list of double to determine the probability of significant of each island of the BinList.
	 * This value is the window score. All values out of islands are to 0.
	 * 
	 * @param precision			bits precision
	 * @param currentList		current list of windows value
	 * @param islands_start		start positions of all islands
	 * @param islands_stop		stop positions of all islands
	 * @return					list of windows values
	 */
	private List<Double> getListIslandWithIslandExpectationValue (DataPrecision precision,
																List<Double> currentList,
																ArrayList<Integer> islands_start,
																ArrayList<Integer> islands_stop) {
		List<Double> resultList = ListFactory.createList(precision, currentList.size());
		ArrayList<Double> scoreIsland = calculateScoreIsland(currentList, islands_start, islands_stop);
		ArrayList<Double> probabilityIsland = calculateIslandExpectation (scoreIsland);
		int current_pos = 0;
		double value = 0.0;
		for (int i = 0; i < currentList.size(); i++) {
			if (current_pos < islands_start.size()){
				if (i >= islands_start.get(current_pos) && i <= islands_stop.get(current_pos)) {
					value = probabilityIsland.get(current_pos);
					if (value < 0.01) {
						value = 0.01;
					}
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
	 * probabilityInitialization method
	 * Some operations and attributes must be initialized:
	 * 	- G factor,
	 * 	- the t value,
	 * 	- the boundary contribution
	 * 	- the genome length
	 * 	- the boundary contribution
	 * 	- the array containing intermediate island probability (kernel M)
	 */
	private void probabilityInitialization () {
		double prob = 0.0;
		this.genomeLength = 0.0;
		BLOCountNonNullBins windowsNumber = new BLOCountNonNullBins(this.binList, null);
		calculateTValue ();
		calculateGFactor ();
		this.boundaryContribution = Math.pow(this.tValue, this.gap+1);
		this.boundaryContribution *= Math.pow(this.tValue, this.gap+1);
		try {
			prob = Poisson.poisson(this.lambda, (int)this.readCountLimit);
		} catch (InvalidLambdaPoissonParameterException e1) {
			e1.printStackTrace();
		} catch (InvalidFactorialParameterException e1) {
			e1.printStackTrace();
		}
		try {
			this.scoreZero = -1 * Math.log(Poisson.poisson(this.lambda, (int)this.readCountLimit));
		} catch (InvalidLambdaPoissonParameterException e) {
			e.printStackTrace();
		} catch (InvalidFactorialParameterException e) {
			e.printStackTrace();
		}
		try {
			this.genomeLength = windowsNumber.compute() + this.binList.getBinSize();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		this.islandIntermediateProbStorage.add(1/this.GFactor);
		for (int i=1; i < (int)this.scoreZero; i++) {
			this.islandIntermediateProbStorage.add(islandKernelProbability(i));
		}
		this.islandIntermediateProbStorage.add(prob);
		/*
		System.out.println("__________________________________________________");
		System.out.println("Lambda: " + this.lambda);
		System.out.println("scoreZero: " + this.scoreZero);
		System.out.println("tValue: " + this.tValue);
		System.out.println("GFactor: " + this.GFactor);
		System.out.println("boundaryContribution: " + this.boundaryContribution);
		System.out.println("genomeLength: " + this.genomeLength);
		*/
		
	}
	
	/**
	 * calculateTValue method
	 * The t value is the sum of poisson value from k=0 to k=readCountLimit
	 */
	private void calculateTValue () {
		double result = 0.0;
		for (int i=0; i < this.readCountLimit; i++) {
			try {
				result += Poisson.poisson(this.lambda, i);
			} catch (InvalidLambdaPoissonParameterException e) {
				e.printStackTrace();
			} catch (InvalidFactorialParameterException e) {
				e.printStackTrace();
			}
		}
		this.tValue = result;
	}
	
	/**
	 * calculateGFactor method
	 * This method determine the G factor value.
	 * It's the sum of t^k where k=0 to k=gap
	 */
	private void calculateGFactor () {
		double result = 0.0;
		if (this.tValue == -1.0) {
			calculateTValue();
		}
		for (int i=0; i <= this.gap; i++) {
			result += Math.pow(this.tValue, i);
		}
		this.GFactor = result;
	}
	
	/**
	 * diracFunction method
	 * The Dirac delta function is theoritically define like this:
	 * 	- f(x=0) = 1
	 * 	- f(x!=0) = 0
	 * 
	 * @param value	x coordinate
	 * @return		the y coordinate
	 */
	private int diracFunction (double value) {
		int result;
		if (value == 0.0) {
			result = 1;
		} else {
			result = 0;
		}
		return result;
	}
	
	/**
	 * windowProbability method
	 * This method determines the probability of significant of one window with its score.
	 * 
	 * @param currentList	list of every scores
	 * @param score			score for the probability seeking
	 * @return				the window probability
	 */
	private double windowProbability (List<Double> currentList, double score) {
		double result = 0.0;
		if (this.windowProbabilityStorage.containsKey(score)) {
			result = this.windowProbabilityStorage.get(score);
		} else {
			double result_tmp;
			for (int i=0; i < currentList.size(); i++){
				if (currentList.get(i) >= this.readCountLimit) {
					try {
						result_tmp = diracFunction(score - scoreOfWindow(currentList.get(i)));
						if (result_tmp != 0) {
							result_tmp *= Poisson.poisson(this.lambda, currentList.get(i).intValue());
							result += result_tmp;
						}
					} catch (InvalidLambdaPoissonParameterException e) {
						e.printStackTrace();
					} catch (InvalidFactorialParameterException e) {
						e.printStackTrace();
					}
				}
			}
			this.windowProbabilityStorage.put(score, result);
		}
		return result;
	}
	
	/**
	 * calculateIslandExpectation method
	 * This method determines all of the island expectation on the genome.
	 * 
	 * @param scoreIsland	scores of every islands
	 * @return				the list off all islands expectation values
	 */
	private ArrayList<Double> calculateIslandExpectation (List<Double> scoreIsland) {
		double result = 0.0;
		ArrayList<Double> listResult = new ArrayList<Double>();
		for (int i=0; i<scoreIsland.size(); i++) {
			result = islandKernelProbability(scoreIsland.get(i));
			result *= this.boundaryContribution;
			result *= this.genomeLength;
			listResult.add(result);
		}
		return listResult;
	}
	
	/**
	 * islandKernelProbability method
	 * The island kernel probability is an intermediate value needed to determine the island probability.
	 * The probability is function of a score. Each score have its own probability.
	 * 
	 * @param score	required to determine the island kernel probability
	 * @return		the intermediate probability of the score
	 */
	private double islandKernelProbability (double score) {
		int currentScore = this.islandIntermediateProbStorage.size() - 1;
		if (score > currentScore) {
			for (int i = (currentScore + 1); i <= (score + 1); i++) {
				double temp = 0.0;
				double reads = this.readCountLimit;
				while ((int)Math.round(i - scoreOfWindow((double)reads)) >= 0) {
					int sub = (int)Math.round(i - scoreOfWindow((double)reads));
					try {
						temp += Poisson.poisson(this.lambda, (int)reads);
						if ((this.islandIntermediateProbStorage.size()-1) >= sub) {
							temp *= this.islandIntermediateProbStorage.get(sub);
						} else {
							temp *= islandKernelProbability (sub);
						}
					} catch (InvalidLambdaPoissonParameterException e) {
						e.printStackTrace();
					} catch (InvalidFactorialParameterException e) {
						e.printStackTrace();
					}
					reads++;
				}
				temp *= this.GFactor;
				this.islandIntermediateProbStorage.add(temp);
			}
		}
		return this.islandIntermediateProbStorage.get((int)Math.round(score));
	}

}
