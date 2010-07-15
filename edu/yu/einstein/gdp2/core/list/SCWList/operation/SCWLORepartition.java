/**
 * @author Chirag Gorasia
 * @version 0.1
 */

package yu.einstein.gdp2.core.list.SCWList.operation;

import java.io.IOException;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.operation.Operation;

/**
 * @author Chirag Gorasia
 * @version 0.1
 */
public class SCWLORepartition implements Operation<double [][][]>{

	private final ScoredChromosomeWindowList[] scwListArray;
	private final double scoreWindowSize;
	private final int graphType;

	public SCWLORepartition(ScoredChromosomeWindowList[] scwListArray, double scoreWindowSize, int graphType) {
		this.scwListArray = scwListArray;
		this.scoreWindowSize = scoreWindowSize;
		this.graphType = graphType;
	}

	@Override
	public double[][][] compute() throws IllegalArgumentException, IOException {
		if(scoreWindowSize <= 0) {
			throw new IllegalArgumentException("the size of the score bins must be strictly positive");
		}
		// search the greatest and smallest score
		double[] max = new double[scwListArray.length];
		double[] min = new double[scwListArray.length];
		double[] distanceMinMax = new double[scwListArray.length];
		double result[][][] = new double[scwListArray.length][][];


		for (int i = 0; i < scwListArray.length; i++) {
			max[i] = scwListArray[i].getMax();
			min[i] = scwListArray[i].getMin();
			distanceMinMax[i] = max[i] - min[i];	
			result[i] = new double[(int)(distanceMinMax[i] / scoreWindowSize) + 2][2];	
		}
		int z = 0;
		int k = 0;
		while (k < scwListArray.length) {
			int minNegative = 0;
			double startPoint;
			if (min[k] < 0)
				minNegative = 1;
			int i = 0;
			while ((scoreWindowSize*i) < Math.abs(min[k])) {
				i++;
			}
			if (minNegative == 1) {
				startPoint = scoreWindowSize*(i)*(-1);
			}else {
				startPoint = scoreWindowSize*(i-1);
			}
			if (Math.ceil(startPoint + z*scoreWindowSize) >= max[k]) {
				z = 0;
				k++;
				if (k == scwListArray.length)
					break;
			}
			result[k][z][0] = (startPoint + z*scoreWindowSize);
			z++;
		}		

 		for (k = 0; k < scwListArray.length; k++) {
			for (short i = 0; i < scwListArray[k].size(); i++) {
				if (scwListArray[k].get(i) != null) {
					for(int j = 0; j < scwListArray[k].size(i); j++) {
						if (scwListArray[k].get(i,j) != null) {
							if (graphType == 1) {
								result[k][(int)((scwListArray[k].get(i,j).getScore() - min[k]) / scoreWindowSize)][1]++;
							} else {
								result[k][(int)((scwListArray[k].get(i,j).getScore() - min[k]) / scoreWindowSize)][1] += scwListArray[k].get(i,j).getStop() - scwListArray[k].get(i,j).getStart();
							}
						}
					}
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
	public String getProcessingDescription() {
		return null;
	}

	@Override
	public int getStepCount() {
		return 1;
	}
}
