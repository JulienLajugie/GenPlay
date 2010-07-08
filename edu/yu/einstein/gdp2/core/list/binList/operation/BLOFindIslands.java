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
	private IslandResultType[] 	list;
	private IslandFinder 		island;

	public BLOFindIslands (BinList binList) throws InterruptedException, ExecutionException {
		this.inputBinList = binList;
		this.island = new IslandFinder(binList);
	}
	
	@Override
	public BinList[] compute () throws InterruptedException, ExecutionException {
		this.outputBinList = new BinList[this.list.length];
		for (int i=0; i < this.list.length; i++) {
			if (this.list[i] != null) {
				this.island.setResultType(this.list[i]);	// at this point, the resultType setting is the last to set
				this.outputBinList[i] = this.island.findIsland();	// we store the calculated bin list on the output binlist array of bloIsland object
			}
		}
		return this.outputBinList;
	}

	
	@Override
	public String getDescription () {
		return "Operation: Island Finder.";
	}
	
	
	@Override
	public int getStepCount() {
		return (BinList.getCreationStepCount(inputBinList.getBinSize()) + 1) * this.numResult();
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Searching Islands";
	}

	
	/**
	 * Count the number of valid result type.
	 * The array size will be always 2 (filtered & island score) but some fields can be null and do not been counted.
	 * @return	number of valid result type
	 */
	private int numResult() {
		int cpt = 0;
		for (int i=0; i < this.list.length; i++) {
			if (this.list[i] != null) {
				cpt++;
			}
		}
		return cpt;
	}
	
	//Getters & Setters
	public IslandFinder getIsland() {
		return island;
	}
	
	public void setList(IslandResultType[] list) {
		this.list = list;
	}

}