/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;

import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.binList.BinList;


/**
 * Creates a new BinList with a new bin size
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOChangeBinSize implements BinListOperation<BinList> {

	private final BinList 					binList;	// input BinList
	private final int 						binSize;	// new bin size 
	private final ScoreCalculationMethod 	method;		// method for the calculation of the new binlist


	/**
	 * Creates a new BinList with a new bin size
	 * @param binList input BinList
	 * @param binSize new bin size
	 * @param method {@link ScoreCalculationMethod} for the calculation of the new BinList
	 */
	public BLOChangeBinSize(BinList binList, int binSize, ScoreCalculationMethod method) {
		this.binList = binList;
		this.binSize = binSize;
		this.method = method;
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		BinList resultList = new BinList(binSize, binList.getPrecision(), method, binList, true);
		return resultList;
	}
	

	@Override
	public String getDescription() {
		return "Bin Size Changes to " + binSize + "bp, Method of Calculation = " + method;
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binSize);
	}
}
