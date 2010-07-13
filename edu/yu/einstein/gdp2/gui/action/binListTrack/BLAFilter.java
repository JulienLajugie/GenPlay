/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.FilterType;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOFilterBandStop;
import yu.einstein.gdp2.core.list.binList.operation.BLOFilterCount;
import yu.einstein.gdp2.core.list.binList.operation.BLOFilterDensity;
import yu.einstein.gdp2.core.list.binList.operation.BLOFilterPercentage;
import yu.einstein.gdp2.core.list.binList.operation.BLOFilterThreshold;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.dialog.TwoNumbersOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.util.Utils;


/**
 * Filters the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLAFilter extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 2935767531786922267L; 	// generated ID
	private static final String 	ACTION_NAME = "Filter";				// action name
	private static final String 	DESCRIPTION = 
		"Filter the selected track";									// tooltip
	private BinListTrack			selectedTrack ;						// selected track


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAFilter";


	/**
	 * Creates an instance of {@link BLAFilter}
	 */
	public BLAFilter() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}

	@Override
	public Operation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			if (selectedTrack.getData().getPrecision() == DataPrecision.PRECISION_1BIT) {
				JOptionPane.showMessageDialog(getRootPane(), "Error, not filter available for 1-Bit tracks", "Error", JOptionPane.ERROR_MESSAGE);
			}
			final FilterType filterType = Utils.chooseFilterType(getRootPane());
			switch (filterType) {
			case COUNT:
				return filterCount();
			case PERCENTAGE:
				return filterPercentage();
			case THRESHOLD:
				return filterThreshold();
			case DENSITY:
				return filterDensity();
			case BANDSTOP:
				return filterBandStop();
			default:
				throw new IllegalArgumentException("Invalid Saturation Type");
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			selectedTrack.setData(actionResult, operation.getDescription());
		}
	}


	/**
	 * Filters a fixed number of values
	 */
	private Operation<BinList> filterCount() {
		BinList binList = selectedTrack.getData();
		Number[] counts = TwoNumbersOptionPane.getValue(getRootPane(), "Enter Counts", "Count of low values to remove:", "Count of high values to remove", new DecimalFormat("0"), Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
		if(counts != null) {
			int countLow = counts[0].intValue();
			int countHigh = counts[1].intValue();
			return new BLOFilterCount(binList, countLow, countHigh);
		}
		return null;
	}


	/**
	 * Filters values where the density of not null values on a region is too low
	 */
	private Operation<BinList> filterDensity() {
		BinList binList = selectedTrack.getData();
		Number[] thresholds = TwoNumbersOptionPane.getValue(getRootPane(), "Enter Threshold", "Remove values smaller than:", "Remove values greater than:", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		if(thresholds != null) {
			double thresholdLow = thresholds[0].doubleValue();
			double thresholdHigh = thresholds[1].doubleValue();			
			if (thresholdHigh <= thresholdLow) {
				JOptionPane.showMessageDialog(getRootPane(), "The high threshold must be greater than the low one", "Error", JOptionPane.ERROR_MESSAGE, null);
			} else {
				Number regionSize = NumberOptionPane.getValue(getRootPane(), "Size", "<html>Select the size of the region filtered<br/><center>(in number of bins)</center></html>", new DecimalFormat("0"), 1, 1000, 1); 
				if(regionSize != null) {
					Number density = NumberOptionPane.getValue(getRootPane(), "Density", "Enter the percentage of value above the filter", new DecimalFormat("###.###%"), 0, 1, 1);
					if (density != null) {
						return new BLOFilterDensity(binList, thresholdLow, thresholdHigh, density.doubleValue(), regionSize.intValue());
					}
				}
			}
		}
		return null;
	}


	/**
	 * Filters a percentage of extreme values
	 */
	private Operation<BinList> filterPercentage() {
		BinList binList = selectedTrack.getData();
		Number[] percentages = TwoNumbersOptionPane.getValue(getRootPane(), "Enter Percentages", "Select the percentage of low values to filter:", "Select the percentage of high values to filter:", new DecimalFormat("0%"), 0, 1, 0.01, 0.01);
		if(percentages != null) {
			double percentageLow = percentages[0].doubleValue();
			double percentageHigh = percentages[1].doubleValue();
			if (percentageHigh + percentageLow > 1) {
				JOptionPane.showMessageDialog(getRootPane(), "The sum of the two percentages must be smaller than 1", "Error", JOptionPane.ERROR_MESSAGE, null);
			} else {
				return new BLOFilterPercentage(binList, percentageLow, percentageHigh);
			}
		}
		return null;
	}


	/**
	 * Filters values above or under a specified threshold
	 */
	private Operation<BinList> filterThreshold() {
		BinList binList = selectedTrack.getData();
		Number[] thresholds = TwoNumbersOptionPane.getValue(getRootPane(), "Enter Thresholds", "Remove values smaller than:", "Or greater than:", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
		if(thresholds != null) {
			double thresholdLow = thresholds[0].doubleValue();
			double thresholdHigh = thresholds[1].doubleValue();
			if (thresholdHigh <= thresholdLow) {
				JOptionPane.showMessageDialog(getRootPane(), "The high threshold must be greater than the low one", "Error", JOptionPane.ERROR_MESSAGE, null);
			} else {
				Number successiveValues = NumberOptionPane.getValue(getRootPane(), "Bin Count", "Select a minimum number of successive valid bins", new DecimalFormat("0"), 1, 1000, 1);
				if(successiveValues != null) {
					return new BLOFilterThreshold(binList, thresholdLow, thresholdHigh, successiveValues.intValue());
				}
			}
		}
		return null;
	}


	/**
	 * Filters values between two specified thresholds
	 */
	private Operation<BinList> filterBandStop() {
		BinList binList = selectedTrack.getData();		
		Number[] thresholds = TwoNumbersOptionPane.getValue(getRootPane(), "Enter Thresholds", "Remove Values Between", "And", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 100);
		if(thresholds != null) {
			double thresholdLow = thresholds[0].doubleValue();
			double thresholdHigh = thresholds[1].doubleValue();
			if (thresholdHigh <= thresholdLow) {
				JOptionPane.showMessageDialog(getRootPane(), "The high threshold must be greater than the low one", "Error", JOptionPane.ERROR_MESSAGE, null);
			} else {
				return new BLOFilterBandStop(binList, thresholdLow, thresholdHigh);
			}
		}
		return null;
	}
}
