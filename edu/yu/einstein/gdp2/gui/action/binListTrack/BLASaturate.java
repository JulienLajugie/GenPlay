/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.SaturationType;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOSaturateCount;
import yu.einstein.gdp2.core.list.binList.operation.BLOSaturatePercentage;
import yu.einstein.gdp2.core.list.binList.operation.BLOSaturateThreshold;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.util.Utils;


/**
 * Saturates the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLASaturate extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 2935767531786922267L; 	// generated ID
	private static final String 	ACTION_NAME = "Saturate";			// action name
	private static final String 	DESCRIPTION = 
		"Saturate the selected track";									// tooltip
	private BinListTrack			selectedTrack ;						// selected track

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLASaturate";


	/**
	 * Creates an instance of {@link BLASaturate}
	 */
	public BLASaturate() {
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
				JOptionPane.showMessageDialog(getRootPane(), "Error, no saturation available for 1-Bit tracks", "Error", JOptionPane.ERROR_MESSAGE);
			}
			SaturationType saturationType = Utils.chooseSaturationType(getRootPane());
			switch (saturationType) {
			case COUNT:
				return SaturateCount();
			case PERCENTAGE:
				return saturatePercentage();
			case THRESHOLD:
				return saturateThreshold();
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
	 * Saturates a fixed number of values
	 */
	private Operation<BinList> SaturateCount() {
		BinList binList = selectedTrack.getData();
		Number countLow = NumberOptionPane.getValue(getRootPane(), "Low Values", "Select the number of low values to saturate", new DecimalFormat("0"), Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
		if(countLow != null) {
			Number countHigh = NumberOptionPane.getValue(getRootPane(), "High Values", "Select the number of high values to saturate", new DecimalFormat("0"), Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
			if(countHigh != null) {
				Operation<BinList> operation = new BLOSaturateCount(binList, countLow.intValue(), countHigh.intValue());
				return operation;
			}
		}
		return null;
	}


	/**
	 * Saturates a percentage of extreme values
	 */
	private Operation<BinList> saturatePercentage() {
		BinList binList = selectedTrack.getData();
		Number percentageLow = NumberOptionPane.getValue(getRootPane(), "Low Percentage", "Select the percentage of low values to saturate", new DecimalFormat("0%"), 0, 1, 0.01);
		if(percentageLow != null) {
			Number percentageHigh = NumberOptionPane.getValue(getRootPane(), "High Percentage", "Select the percentage of high values to saturate", new DecimalFormat("0%"), 0, 1, 0.01);
			if(percentageHigh != null) {
				if (percentageHigh.doubleValue() + percentageLow.doubleValue() > 1) {
					JOptionPane.showMessageDialog(getRootPane(), "The sum of the two percentages must be smaller than 1", "Error", JOptionPane.ERROR_MESSAGE, null);
				} else {	
					Operation<BinList> operation = new BLOSaturatePercentage(binList, percentageLow.doubleValue(), percentageHigh.doubleValue());
					return operation;
				}
			}
		}
		return null;
	}


	/**
	 * Saturates values above or under a specified threshold
	 */
	private Operation<BinList> saturateThreshold() {
		BinList binList = selectedTrack.getData();
		Number thresholdLow = NumberOptionPane.getValue(getRootPane(), "Low Threshold", "Saturate values smaller than:", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
		if(thresholdLow != null) {
			Number thresholdHigh = NumberOptionPane.getValue(getRootPane(), "High Threshold", "Saturate values greater than:", new DecimalFormat("0.0"), thresholdLow.doubleValue(), Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
			if(thresholdHigh != null) {
				if (thresholdHigh.doubleValue() <= thresholdLow.doubleValue()) {
					JOptionPane.showMessageDialog(getRootPane(), "The high threshold must be greater than the low one", "Error", JOptionPane.ERROR_MESSAGE, null);
				} else {
					Operation<BinList> operation = new BLOSaturateThreshold(binList, thresholdLow.doubleValue(), thresholdHigh.doubleValue());
					return operation;
				}
			}
		}
		return null;
	}
}
