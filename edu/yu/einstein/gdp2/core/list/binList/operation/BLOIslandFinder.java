/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;

import java.util.concurrent.ExecutionException;
import yu.einstein.gdp2.core.filter.IslandFinder;
import yu.einstein.gdp2.core.list.binList.BinList;


/**
 * Use the Island approach to separate data on islands
 * @author Nicolas Fourel
 * @version 0.1
 */
public class BLOIslandFinder implements BinListOperation<BinList> {

	private final BinList 	binList;	// input binlist
	private final double 	readCountLimit;	// limit reads number to get an eligible windows
	private final int		gap;	// minimum windows number needed to separate 2 islands
	
	

	public BLOIslandFinder(BinList binList, double read_count_limit, int gap) {
		this.binList = binList;
		this.readCountLimit = read_count_limit;
		this.gap = gap;
	}
	
	
	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		IslandFinder island = new IslandFinder(binList, readCountLimit, gap);
		return island.findIsland();
	}

	
	@Override
	public String getDescription() {
		return "Operation: Island Finder, Read count limit = " + readCountLimit + ", Gap = " + gap;
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}
}
