/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList.operation;

import java.util.Arrays;
import java.util.List;

import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.operation.Operation;


/**
 * Removes a percentage of the greatest and smallest values
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLOFilterPercentage implements Operation<ScoredChromosomeWindowList> {

	private final ScoredChromosomeWindowList 		inputList;				// list to filter
	private final double 							lowPercentage;			// percentage of low values to filter
	private final double 							highPercentage;			// percentage of high values to filter
	private final boolean							isSaturation;			// true if we saturate, false if we remove the filtered values 
	private boolean									stopped = false;		// true if the operation must be stopped
	private Operation<ScoredChromosomeWindowList> 	scwloFilterThreshold;	// threshold filter that does the real fitering operation
	

	/**
	 * Creates an instance of {@link SCWLOFilterPercentage}
	 * @param binList {@link ScoredChromosomeWindowList} to filter
	 * @param lowPercentage percentage of low values to filter
	 * @param highPercentage percentage of high values to filter
	 * @param isSaturation true to saturate, false to remove the filtered values
	 */
	public SCWLOFilterPercentage(ScoredChromosomeWindowList inputList, double lowPercentage, double highPercentage, boolean isSaturation) {
		this.inputList = inputList;
		this.lowPercentage = lowPercentage;
		this.highPercentage = highPercentage;
		this.isSaturation = isSaturation;
	}
	
	
	@Override
	public ScoredChromosomeWindowList compute() throws Exception {
		if ((highPercentage < 0) || (highPercentage > 1) || (lowPercentage < 0) ||(lowPercentage > 1)) {
			throw new IllegalArgumentException("The percentage value must be between 0 and 1");
		}
		if (lowPercentage + highPercentage > 1) {
			throw new IllegalArgumentException("The sum of the low and high percentages value must be between 0 and 1");
		}
		// compute the total number of windows
		int totalLenght = 0;
		for (List<ScoredChromosomeWindow> currentList: inputList) {
			if (currentList != null) {
				totalLenght += currentList.size();
			}
		}
		// create an array containing all the scores of the input list
		double[] allScores = new double[totalLenght];
		int i = 0;
		for (List<ScoredChromosomeWindow> currentList: inputList) {
			if (currentList != null) {
				for (int j = 0; j < currentList.size() && !stopped; j++) {
					Double currentScore = currentList.get(j).getScore();
					allScores[i] = currentScore;
					i++;
				}
			}
		}
		int lowValuesCount = (int)(lowPercentage * allScores.length);
		int highValuesCount = (int)(highPercentage * allScores.length);
		// sort the array and search the value of the min and of the max corresponding to the thresholds
		Arrays.sort(allScores);
		double minValue = lowValuesCount == 0 ? Double.NEGATIVE_INFINITY : allScores[lowValuesCount - 1];
		double maxValue = highValuesCount == 0 ? Double.POSITIVE_INFINITY : allScores[allScores.length - highValuesCount];
		// start a SCWLOFilterThreshold with the min and max value that we just found
		scwloFilterThreshold = new SCWLOFilterThreshold(inputList, minValue, maxValue, isSaturation); 
		return scwloFilterThreshold.compute();
	}

	
	@Override
	public String getDescription() {
		String optionStr;
		if (isSaturation) {
			optionStr = ", option = saturation";
		} else {
			optionStr = ", option = remove";
		}
		return "Operation: Filter, " + (lowPercentage * 100) + "% smallest values, " + (highPercentage * 100) + "% greatest values" + optionStr;
	}
	

	@Override
	public String getProcessingDescription() {
		return "Filtering";
	}

	
	@Override
	public int getStepCount() {
		return 1 + ScoredChromosomeWindowList.getCreationStepCount();
	}

	
	@Override
	public void stop() {
		this.stopped = true;
		if (scwloFilterThreshold != null) {
			scwloFilterThreshold.stop();
		}
	}
}
