/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import yu.einstein.gdp2.core.list.binList.BinList;


/**
 * Creates bins of score with a size of <i>scoreBinsSize</i>, 
 * and computes how many bins of the BinList there is in each bin of score.
 * Writes the result in a file. 
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLORepartition implements BinListOperation<Void> {

	private final BinList 	binList;		// input binlist
	private final double 	scoreBinSize;	// size of the bins of score
	private final File		file;			// file where to write the result


	/**
	 * Creates bins of score with a size of <i>scoreBinsSize</i>, 
	 * and computes how many bins of the BinList there is in each bin of score.
	 * Writes the result in a file. 
	 * @param binList input BinList
	 * @param scoreBinSize size of the bins of score
	 * @param file File where to write the result
	 */
	public BLORepartition(BinList binList, double scoreBinSize, File file) {
		this.binList = binList;
		this.scoreBinSize = scoreBinSize;
		this.file = file;
	}


	@Override
	public Void compute() throws IllegalArgumentException, IOException {
		if(scoreBinSize <= 0) {
			throw new IllegalArgumentException("the size of the score bins must be strictly positive");
		}
		// search the greatest and smallest score
		double max = binList.getMax();
		max = Math.max(max, 0);
		double min = binList.getMin();
		min = Math.min(min, 0);
		double distanceMinMax = max - min;

		int result[] = new int[(int)(distanceMinMax / scoreBinSize) + 1];
		for (short i = 0; i < binList.size(); i++) {
			if (binList.get(i) != null) {
				for(int j = 0; j < binList.size(i); j++) 
					result[(int)((binList.get(i, j) - min) / scoreBinSize)]++;
			}
		}	
		BufferedWriter writer = null;
		// try to create a output file
		try {
			writer = new BufferedWriter(new FileWriter(file));
			for(int i = 0; i < result.length; i++) {
				double position = i * scoreBinSize + min; 
				writer.write(Double.toString(position) + ", " + Double.toString(position + scoreBinSize) + ", " + Integer.toString(result[i]));
				writer.newLine();		
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		return null;
	}


	@Override
	public String getDescription() {
		return "Operation: Show Repartition";
	}
	
	
	@Override
	public int getStepCount() {
		return 1;
	}
}
