/**
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.swing.JComponent;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;


/**
 * Creates bins of score with a size of <i>scoreBinsSize</i>, 
 * and computes how many bins of the BinList there is in each bin of score.
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
public class BLORepartition extends JComponent implements Operation<double [][][]> {

	private static final long serialVersionUID = 7957598559746052918L;	// generated ID
	private final BinList[] binListArray;	// input binListArray
	private final double 	scoreBinSize;	// size of the bins of score
	private boolean			stopped = false;// true if the operation must be stopped


	/**
	 * Creates an instance of {@link BLORepartition}
	 * @param binListArray input BinLists
	 * @param scoreBinSize size of the bins of score
	 */
	public BLORepartition(BinList[] binListArray, double scoreBinSize) {
		this.binListArray = binListArray;
		this.scoreBinSize = scoreBinSize;
	}
	
	@Override
	public double[][][] compute() throws IllegalArgumentException, IOException, InterruptedException, ExecutionException {
		if(scoreBinSize <= 0) {
			throw new IllegalArgumentException("the size of the score bins must be strictly positive");
		}
		double[][][] finalResult = new double[binListArray.length][][];	
		for (int i = 0; i < binListArray.length; i++) {
			finalResult[i] = singleBinListResult(binListArray[i]);
		}
		return finalResult;
	}

	
	/**
	 * Compute the result for one binList 
	 * @param binList input BinList
	 * @return the repartition of the input BinList
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public double[][] singleBinListResult (final BinList binList) throws InterruptedException, ExecutionException {
		// search the greatest and smallest score
		double max = Math.max(0, binList.getMax());
		double min = Math.min(0, binList.getMin());
		// search the score of the first bin
		final double startPoint = Math.floor(min / scoreBinSize) * scoreBinSize;
		// distance from the max to the first score
		final double distanceMinMax = max - startPoint;
		// the +2 is because of the rounding (+1) and also because we want one more value
		// because the data are arrange this way:
		// count(res[i][1] to res[i+1][1]) = res[i][1]
		// meaning that we need to have the value for i + 1
		double result[][] = new double[(int)(distanceMinMax / scoreBinSize) + 2][2];
		int i = 0;
		// we add max + scoreBinSize to have a value for i + 1 (cf previous comment) 
		while ((startPoint + i * scoreBinSize) <= (max + scoreBinSize) && !stopped) {
			result[i][0] = startPoint + i * scoreBinSize;
			i++;
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<double[]>> threadList = new ArrayList<Callable<double[]>>();

		for (final List<Double> currentList: binList) { 
			Callable<double[]> currentThread = new Callable<double[]>() {	
				@Override
				public double[] call() throws Exception {				
 					if (currentList == null) {
						return null;
					}
 					// create an array for the counts
 					double[] chromoResult = new double[(int)(distanceMinMax / scoreBinSize) + 2];
					// count the bins
					for(int j = 0; j < currentList.size() && !stopped; j++) {
						chromoResult[(int)((currentList.get(j) - startPoint) / scoreBinSize)]++;
					}
					op.notifyDone();
					return chromoResult;
				}
			};
			threadList.add(currentThread);
		}

		List<double[]> threadResult = op.startPool(threadList);
		if (threadResult == null) {
			return null;		
		}

		for (double [] currentResult: threadResult) {
			if (currentResult != null) {
				for (i = 0; i < currentResult.length; i++) {
					result[i][1] += currentResult[i];
				}
			}
		}
		
		return result;
	}

	
	@Override
	public String getDescription() {
		return "Operation: Show Repartition";
	}	

	
	@Override
	public int getStepCount() {
		return binListArray.length;
	}

	
	@Override
	public String getProcessingDescription() {
		return "Plotting Repartition";
	}


	@Override
	public void stop() {
		this.stopped = true;
	}
}
