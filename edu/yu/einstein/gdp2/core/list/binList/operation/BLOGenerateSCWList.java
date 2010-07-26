/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;


/**
 * Creates a SCWList from the data of the input {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class BLOGenerateSCWList implements Operation<ScoredChromosomeWindowList> {

	private final BinList binList; // input list
	
	
	/**
	 * Creates a SCWList from the data of the input BinList
	 * @param binList the BinList
	 */
	public BLOGenerateSCWList(BinList binList) {
		this.binList = binList;
	}
	
	
	@Override
	public ScoredChromosomeWindowList compute() throws Exception {
		return new ScoredChromosomeWindowList(this.binList);
	}
	
	
	@Override
	public String getDescription() {
		return "Operation: Generate Variable Window Track";
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Generating Variable Window Track";
	}
	
	
	@Override
	public int getStepCount() {
		return 1 + ScoredChromosomeWindowList.getCreationStepCount();
	}
}
