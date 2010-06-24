/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;


import java.io.IOException;

import javax.swing.JComponent;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;


/**
 * Creates bins of score with a size of <i>scoreBinsSize</i>, 
 * and computes how many bins of the BinList there is in each bin of score.
 * Writes the result in a file. 
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLORepartition extends JComponent implements Operation<double [][][]> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final BinList[] binListArray;	// input binListArray
	private final double 	scoreBinSize;	// size of the bins of score
		

//	/**
//	 * Creates bins of score with a size of <i>scoreBinsSize</i>, 
//	 * and computes how many bins of the BinList there is in each bin of score.
//	 * Writes the result in a file. 
//	 * @param binList input BinList
//	 * @param scoreBinSize size of the bins of score
//	 * @param file File where to write the result
//	 */
//	public BLORepartition(BinList binList, double scoreBinSize, File file) {
//		this.binList = binList;
//		this.scoreBinSize = scoreBinSize;
//		this.file = file;
//	}
	
//	/**
//	 * Creates bins of score with a size of <i>scoreBinsSize</i>, 
//	 * and computes how many bins of the BinList there is in each bin of score.
//	 * Writes the result in a file. 
//	 * @param binList input BinList
//	 * @param scoreBinSize size of the bins of score
//	 * @param file File where to write the result
//	 */
//	public BLORepartition(BinList[] binListArray, double scoreBinSize, File file) {
//		this.binListArray = binListArray;
//		this.scoreBinSize = scoreBinSize;
//		this.file = file;
//	}

	public BLORepartition(BinList[] binListArray, double scoreBinSize) {
		this.binListArray = binListArray;
		this.scoreBinSize = scoreBinSize;
	}

	@Override
	public double[][][] compute() throws IllegalArgumentException, IOException {
		if(scoreBinSize <= 0) {
			throw new IllegalArgumentException("the size of the score bins must be strictly positive");
		}
		// search the greatest and smallest score
		double[] max = new double[binListArray.length];
		double[] min = new double[binListArray.length];
		double[] distanceMinMax = new double[binListArray.length];
		double result[][][] = new double[binListArray.length][][];
		
	
		for (int i = 0; i < binListArray.length; i++) {
			max[i] = binListArray[i].getMax();
			min[i] = binListArray[i].getMin();
			distanceMinMax[i] = max[i] - min[i];	
			result[i] = new double[(int)(distanceMinMax[i] / scoreBinSize) + 2][2];	
		}
		
		int z = 0;
		int k = 0;
		while (k < binListArray.length) {
			int minNegative = 0;
			double startPoint;
			if (min[k] < 0)
				minNegative = 1;
			min[k] = Math.abs(min[k]);
			int i = 0;
			while (Math.abs(min[k] - scoreBinSize*i) > min[k]) {
				i++;
			}
			if (minNegative == 1) {
				startPoint = scoreBinSize*(i-1)*(-1);
			}else {
				startPoint = scoreBinSize*(i-1);
			}
			if (startPoint + z*scoreBinSize >= max[k]) {
				z = 0;
				k++;
				if (k == binListArray.length)
					break;
			}
			result[k][z++][0] = startPoint + z*scoreBinSize;			
		}		
		
		for (k = 0; k < binListArray.length; k++) {
			for (short i = 1; i < binListArray[k].size(); i++) {
				if (binListArray[k].get(i) != null) {
					for(int j = 0; j < binListArray[k].size(i); j++) { 
						result[k][(int)((binListArray[k].get(i, j) - min[k]) / scoreBinSize)][1]++;
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
	public int getStepCount() {
		return 1;
	}

	@Override
	public String getProcessingDescription() {
		return null;
	}
}
