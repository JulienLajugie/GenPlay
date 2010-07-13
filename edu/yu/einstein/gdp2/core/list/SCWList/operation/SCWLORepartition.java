/**
 * @author Chirag Gorasia
 * @version 0.1
 */

package yu.einstein.gdp2.core.list.SCWList.operation;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.operation.Operation;

/**
 * @author Chirag Gorasia
 * @version 0.1
 */
public class SCWLORepartition implements Operation<double [][][]>{

	private final ScoredChromosomeWindowList[] scwListArray;
	private final double scoreWindowSize;

	public SCWLORepartition(ScoredChromosomeWindowList[] scwListArray, double scoreWindowSize) {
		this.scwListArray = scwListArray;
		this.scoreWindowSize = scoreWindowSize;
	}

	@Override
	public double[][][] compute() throws Exception {
		return null;
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
