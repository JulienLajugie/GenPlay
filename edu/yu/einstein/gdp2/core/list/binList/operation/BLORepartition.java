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

	private static final long serialVersionUID = 1L;
	private final BinList[] binListArray;	// input binListArray
	private final double 	scoreBinSize;	// size of the bins of score

/**
 * Creates an instance of {@link BLORepartition}
 * @param binListArray
 * @param scoreBinSize
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


	public double[][] singleBinListResult (final BinList binList) throws InterruptedException, ExecutionException {
		// search the greatest and smallest score
		double max = binList.getMax();
		final double min = binList.getMin();
		final double distanceMinMax = max - min;
		final double startPoint;
		int minNegative = 0;
		if (min < 0) {
			minNegative = 1;
		}
		int i = 0;
		while ((scoreBinSize*i) < Math.abs(min)) {
			i++;
		}
		if (minNegative == 1) {
			startPoint = scoreBinSize*(i)*(-1);
		}else {
			startPoint = scoreBinSize*(i-1);
		}
		double result[][] = new double[(int)(distanceMinMax / scoreBinSize) + 2][2];
		int z = 0;
		while (Math.ceil(startPoint + z*scoreBinSize) <= max) {
			//System.out.println("Z = " + z);
			result[z][0] = (startPoint + z*scoreBinSize);
			z++;
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<double[]>> threadList = new ArrayList<Callable<double[]>>();

		for (final List<Double> currentList: binList) { 
			Callable<double[]> currentThread = new Callable<double[]>() {	
				@Override
				public double[] call() throws Exception {				
					double[] chromoResult = new double[(int)(distanceMinMax / scoreBinSize) + 2];					                                
					if (currentList == null) {
						return null;
					}


					for(int j = 0; j < currentList.size(); j++) {
						if (currentList.get(j) != 0) {
							chromoResult[(int)((currentList.get(j) - min) / scoreBinSize)]++;
						}
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
}
