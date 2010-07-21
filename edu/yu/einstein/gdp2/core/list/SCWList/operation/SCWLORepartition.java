/**
 * @author Chirag Gorasia
 * @version 0.1
 */

package yu.einstein.gdp2.core.list.SCWList.operation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;

/**
 * @author Chirag Gorasia
 * @version 0.1
 */
public class SCWLORepartition implements Operation<double [][][]>{

	private final ScoredChromosomeWindowList[] scwListArray;
	private final double scoreWindowSize;
	private final int graphType;
	private static final int COUNTGRAPH = 1; 

	/**
	 * Creates an instance of {@link SCWLORepartition}
	 * @param scwListArray
	 * @param scoreWindowSize
	 * @param graphType
	 */
	public SCWLORepartition(ScoredChromosomeWindowList[] scwListArray, double scoreWindowSize, int graphType) {
		this.scwListArray = scwListArray;
		this.scoreWindowSize = scoreWindowSize;
		this.graphType = graphType;
	}

	@Override
	public double[][][] compute() throws IllegalArgumentException, IOException, InterruptedException, ExecutionException {
		if(scoreWindowSize <= 0) {
			throw new IllegalArgumentException("the size of the score bins must be strictly positive");
		}
		double[][][] finalResult = new double[scwListArray.length][][];	
		for (int i = 0; i < scwListArray.length; i++) {
			finalResult[i] = singleSCWListResult(scwListArray[i]);
		}
		return finalResult;
	}
	
	public double[][] singleSCWListResult (final ScoredChromosomeWindowList scwList) throws InterruptedException, ExecutionException {
		// search the greatest and smallest score
		double max = scwList.getMax();
		final double min = scwList.getMin();
		final double distanceMinMax = max - min;
		final double startPoint;
		int minNegative = 0;
		if (min < 0) {
			minNegative = 1;
		}
		int i = 0;
		while ((scoreWindowSize*i) < Math.abs(min)) {
			i++;
		}
		if (minNegative == 1) {
			startPoint = scoreWindowSize*(i)*(-1);
		}else {
			startPoint = scoreWindowSize*(i-1);
		}
		double result[][] = new double[(int)(distanceMinMax / scoreWindowSize) + 2][2];
		int z = 0;
		while (Math.ceil(startPoint + z*scoreWindowSize) <= max) {
			result[z][0] = (startPoint + z*scoreWindowSize);
			z++;
		}

		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<double[]>> threadList = new ArrayList<Callable<double[]>>();

		for (final List<ScoredChromosomeWindow> currentList: scwList) { 
			Callable<double[]> currentThread = new Callable<double[]>() {	
				@Override
				public double[] call() throws Exception {				
					double[] chromoResult = new double[(int)(distanceMinMax / scoreWindowSize) + 2];					                                
					if (currentList == null) {
						return null;
					}

					for(int j = 0; j < currentList.size(); j++) {
						if (currentList.get(j).getScore() != 0) {
							if (graphType == COUNTGRAPH) {
								chromoResult[(int)((currentList.get(j).getScore() - min) / scoreWindowSize)]++;
							} else {							
								chromoResult[(int)((currentList.get(j).getScore() - min) / scoreWindowSize)] += currentList.get(j).getStop() - currentList.get(j).getStart();
							}
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
			for (i = 0; i < currentResult.length; i++) {
				result[i][1] += currentResult[i];
			}
		}
		return result;
	}

	@Override
	public String getDescription() {
		return "Operation: Show Repartition";
	}

	@Override
	public String getProcessingDescription() {
		return "Plotting Repartition";
	}

	@Override
	public int getStepCount() {
		return scwListArray.length;
	}
}
