/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.generator;

import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.binList.BinList;


/**
 * The interface BinListGenerator could be implemented by the class able to create a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface BinListGenerator extends Generator {

	/**
	 * Creates and returns a {@link BinList}
	 * @param binSize size of the {@link BinList}
	 * @param precision precision of the data (eg: 1/8/16/32/64-BIT)
	 * @param method method to generate the BinList (eg: AVERAGE, SUM or MAXIMUM)
	 * @return a {@link BinList}
	 * @throws IllegalArgumentException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public BinList toBinList(int binSize, DataPrecision precision, ScoreCalculationMethod method) throws IllegalArgumentException, InterruptedException, ExecutionException;
	
	
	/**
	 * @return true if the yu.einstein.gdp2.core.generator needs information regarding the {@link ScoreCalculationMethod} criterion
	 */
	public boolean isCriterionNeeded();
	
	
	/**
	 * @return true if the yu.einstein.gdp2.core.generator needs information regarding the size of the bins
	 */
	public boolean isBinSizeNeeded();
	
	
	/**
	 * @return true if the yu.einstein.gdp2.core.generator needs information regarding the precision of the data
	 */
	public boolean isPrecisionNeeded();
}
