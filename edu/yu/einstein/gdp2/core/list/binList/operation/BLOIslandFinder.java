/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;

import java.util.concurrent.ExecutionException;
import yu.einstein.gdp2.core.filter.IslandFinder;
import yu.einstein.gdp2.core.list.binList.BinList;


/**
 * Use the Island approach to separate datas on islands
 * @author Nicolas Fourel
 * @version 0.1
 */
public class BLOIslandFinder implements BinListOperation<BinList> {

	private final BinList 	binList;	// input binlist
	private final double 	read_count_limit;	// number limit of reads to get an eligible windows
	private final int		gap;	//minimum number of windows needed to separate 2 islands
	
	
	/**
	 * Adds a specified constant to the scores of each bin of a {@link BinList}
	 * @param binList input {@link BinList}
	 * @param constant constant to add
	 */
	public BLOIslandFinder(BinList binList, double read_count_limit, int gap) {
		this.binList = binList;
		this.read_count_limit = read_count_limit;
		this.gap = gap;
	}
	
	
	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		IslandFinder island = new IslandFinder(binList, read_count_limit, gap);
		return island.find();
	}

	
	@Override
	public String getDescription() {
		return "Operation: Island Finder, Read count limit = " + read_count_limit + ", Gap = " + gap;
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}
}
