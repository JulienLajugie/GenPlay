/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;

import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.enums.IslandResultType;
import yu.einstein.gdp2.core.filter.IslandFinder;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;


/**
 * Use the Island approach to separate data on islands
 * @author Nicolas Fourel
 * @version 0.1
 */
public class BLOFindIslands implements Operation<BinList[]> {

	private final BinList 		inputBinList;	// input binlist
	private BinList[]			outputBinList;
	private IslandFinder 		island;

	public BLOFindIslands (BinList binList) {
		this.inputBinList = binList;
		this.island = new IslandFinder(binList);
	}
	
	
	@Override
	public BinList[] compute () throws InterruptedException, ExecutionException {
		return outputBinList;
	}

	
	@Override
	public String getDescription () {
		return "Operation: Island Finder.";
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(inputBinList.getBinSize()) + 1;
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Searching Islands";
	}

	
	public IslandFinder getIsland() {
		return island;
	}

	
	public void setOutputBinList(BinList outputBinList, int index) {
		this.outputBinList[index] = outputBinList;
	}
	
	public void initOutputBinList(int size) {
		this.outputBinList = new BinList[size];
	}
	
}
