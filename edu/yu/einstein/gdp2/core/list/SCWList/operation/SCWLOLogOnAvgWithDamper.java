/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.enums.LogBase;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;


/**
 * Applies the function f(x)=log((x + damper) / (avg + damper)) to each score x of the {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLOLogOnAvgWithDamper implements Operation<ScoredChromosomeWindowList> {

	private final ScoredChromosomeWindowList 	scwList;	// input binlist
	private final LogBase						logBase;	// base of the log
	private final double						damper;		// damper

	
	/**
	 * Applies the function f(x)=log((x + damper) / (avg + damper)) to each score x of the {@link ScoredChromosomeWindowList}
	 * @param binList input {@link ScoredChromosomeWindowList}
	 * @param logBase base of the logarithm
	 * @param damper a double value
	 */
	public SCWLOLogOnAvgWithDamper(ScoredChromosomeWindowList scwList, LogBase logBase, double damper) {
		this.scwList = scwList;
		this.logBase = logBase;
		this.damper = damper;
	}


	@Override
	public ScoredChromosomeWindowList compute() throws InterruptedException, ExecutionException, ArithmeticException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();

		// compute log(average)
		final double mean = scwList.getAverage();
		final double logMean;
		// log is defined on R+*
		if (mean + damper > 0) {
			if (logBase == LogBase.BASE_E) {
				// the Math.log function return the natural log (no needs to change the base)
				logMean = Math.log(mean + damper);
			} else {
				// change of base: logb(x) = logk(x) / logk(b)
				logMean = Math.log(mean + damper) / Math.log(logBase.getValue());
			}
		} else {
			throw new ArithmeticException("Logarithm of a negative value not allowed");
		}		

		for (short i = 0; i < scwList.size(); i++) {
			final List<ScoredChromosomeWindow> currentList = scwList.get(i);

			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {	
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = new ArrayList<ScoredChromosomeWindow>();
						// We log each element
						for (ScoredChromosomeWindow currentWindow: currentList) {
							ScoredChromosomeWindow resultWindow = new ScoredChromosomeWindow(currentWindow);
							// log is define on R+*
							if (currentWindow.getScore() > 0) {
								double resultValue;
								if (logBase == LogBase.BASE_E) {
									// the Math.log function return the natural log (no needs to change the base)
									resultValue = Math.log(currentWindow.getScore() + damper) - logMean;	
								} else {
									// change of base: logb(x) = logk(x) / logk(b)
									resultValue = Math.log(currentWindow.getScore() + damper) / Math.log(logBase.getValue()) - logMean;									
								}
								resultWindow.setScore(resultValue);
							} else if (currentWindow.getScore() == 0) {
								resultWindow.setScore(0d);
							} else {
								// can't apply a log function on a negative or null numbers
								throw new ArithmeticException("Logarithm of a negative value not allowed");
							}
							resultList.add(resultWindow);
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};

			threadList.add(currentThread);
		}
		List<List<ScoredChromosomeWindow>> result = op.startPool(threadList);
		if (result != null) {
			ScoredChromosomeWindowList resultList = new ScoredChromosomeWindowList(result);
			return resultList;
		} else {
			return null;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: " +  logBase + " on Average, with damper - f(x) = log((x + " + damper + ") / (average + " + damper + "))";
	}


	@Override
	public int getStepCount() {
		return 1 + ScoredChromosomeWindowList.getCreationStepCount();
	}


	@Override
	public String getProcessingDescription() {
		return "Logging";
	}
}
