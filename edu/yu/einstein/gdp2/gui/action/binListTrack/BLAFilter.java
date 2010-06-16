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
import yu.einstein.gdp2.core.list.binList.operation.BLOFilterCount;
import yu.einstein.gdp2.core.list.binList.operation.BLOFilterDensity;
import yu.einstein.gdp2.core.list.binList.operation.BLOFilterPercentage;
import yu.einstein.gdp2.core.list.binList.operation.BLOFilterThreshold;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
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
			if (selectedTrack.getBinList().getPrecision() == DataPrecision.PRECISION_1BIT) {
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
			default:
				throw new IllegalArgumentException("Invalid Saturation Type");
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			selectedTrack.setBinList(actionResult, operation.getDescription());
		}
	}


	/**
	 * Filters a fixed number of values
	 */
	private Operation<BinList> filterCount() {
		BinList binList = selectedTrack.getBinList();
		Number countLow = NumberOptionPane.getValue(getRootPane(), "Low Values", "Select the number of low values to filter", new DecimalFormat("0"), Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
		if(countLow != null) {
			Number countHigh = NumberOptionPane.getValue(getRootPane(), "High Values", "Select the number of high values to filter", new DecimalFormat("0"), Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
			if(countHigh != null) {
				return new BLOFilterCount(binList, countLow.intValue(), countHigh.intValue());
			}
		}
		return null;
	}


	/**
	 * Filters values where the density of not null values on a region is too low
	 */
	private Operation<BinList> filterDensity() {
		BinList binList = selectedTrack.getBinList();
		Number thresholdLow = NumberOptionPane.getValue(getRootPane(), "Low Threshold", "Remove values smaller than:", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
		if(thresholdLow != null) {
			Number thresholdHigh = NumberOptionPane.getValue(getRootPane(), "High Threshold", "Remove values greater than:", new DecimalFormat("0.0"), thresholdLow.doubleValue(), Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
			if(thresholdHigh != null) {
				if (thresholdHigh.doubleValue() <= thresholdLow.doubleValue()) {
					JOptionPane.showMessageDialog(getRootPane(), "The high threshold must be greater than the low one", "Error", JOptionPane.ERROR_MESSAGE, null);
				} else {
					Number regionSize = NumberOptionPane.getValue(getRootPane(), "Size", "<html>Select the size of the region filtered<br/><center>(in number of bins)</center></html>", new DecimalFormat("0"), 1, 1000, 1); 
					if(regionSize != null) {
						Number density = NumberOptionPane.getValue(getRootPane(), "Density", "Enter the percentage of value above the filter", new DecimalFormat("###.###%"), 0, 1, 1);
						if (density != null) {
							return new BLOFilterDensity(binList, thresholdLow.doubleValue(), thresholdHigh.doubleValue(), density.doubleValue(), regionSize.intValue());
						}
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
		BinList binList = selectedTrack.getBinList();
		Number percentageLow = NumberOptionPane.getValue(getRootPane(), "Low Percentage", "Select the percentage of low values to filter", new DecimalFormat("0%"), 0, 1, 0.01);
		if(percentageLow != null) {
			Number percentageHigh = NumberOptionPane.getValue(getRootPane(), "High Percentage", "Select the percentage of high values to filter", new DecimalFormat("0%"), 0, 1, 0.01);
			if(percentageHigh != null) {
				if (percentageHigh.doubleValue() + percentageLow.doubleValue() > 1) {
					JOptionPane.showMessageDialog(getRootPane(), "The sum of the two percentages must be smaller than 1", "Error", JOptionPane.ERROR_MESSAGE, null);
				} else {
					return new BLOFilterPercentage(binList, percentageLow.doubleValue(), percentageHigh.doubleValue());
				}
			}
		}
		return null;
	}


	/**
	 * Filters values above or under a specified threshold
	 */
	private Operation<BinList> filterThreshold() {
		BinList binList = selectedTrack.getBinList();
		Number thresholdLow = NumberOptionPane.getValue(getRootPane(), "Low Threshold", "Remove values smaller than:", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
		if(thresholdLow != null) {
			Number thresholdHigh = NumberOptionPane.getValue(getRootPane(), "High Threshold", "Remove values greater than:", new DecimalFormat("0.0"), thresholdLow.doubleValue(), Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
			if(thresholdHigh != null) {
				if (thresholdHigh.doubleValue() <= thresholdLow.doubleValue()) {
					JOptionPane.showMessageDialog(getRootPane(), "The high threshold must be greater than the low one", "Error", JOptionPane.ERROR_MESSAGE, null);
				} else {
					Number successiveValues = NumberOptionPane.getValue(getRootPane(), "Bin Count", "Select a minimum number of successive valid bins", new DecimalFormat("0"), 1, 1000, 1);
					if(successiveValues != null) {
						return new BLOFilterThreshold(binList, thresholdLow.doubleValue(), thresholdHigh.doubleValue(), successiveValues.intValue());
					}
				}
			}
		}
		return null;
	}
}
